package com.kanzun.perbendaharaan.feature.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.dao.AuditLogDao
import com.kanzun.perbendaharaan.core.database.dao.BudgetDao
import com.kanzun.perbendaharaan.core.database.dao.FundraisingTargetDao
import com.kanzun.perbendaharaan.core.database.dao.TransactionDao
import com.kanzun.perbendaharaan.core.navigation.Screen
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationDateGroup
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationFilter
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationItem
import com.kanzun.perbendaharaan.feature.notifications.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class NotificationUiState(
    val activeFilter: NotificationFilter = NotificationFilter.ALL,
    val dateGroups: List<NotificationDateGroup> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val missingEntityDialogMessage: String? = null,
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val transactionDao: TransactionDao,
    private val targetDao: FundraisingTargetDao,
    private val budgetDao: BudgetDao,
    private val auditDao: AuditLogDao,
) : ViewModel() {

    private val _activeFilter = MutableStateFlow(NotificationFilter.ALL)
    val activeFilter: StateFlow<NotificationFilter> = _activeFilter.asStateFlow()

    private val _missingEntityDialogMessage = MutableStateFlow<String?>(null)
    val missingEntityDialogMessage: StateFlow<String?> = _missingEntityDialogMessage.asStateFlow()

    val uiState: StateFlow<NotificationUiState> = combine(
        _activeFilter,
        notificationRepository.getUnreadCount(),
        _missingEntityDialogMessage,
    ) { filter, unreadCount, missingMessage ->
        Triple(filter, unreadCount, missingMessage)
    }.combine(
        _activeFilter.stateIn(viewModelScope, SharingStarted.Eagerly, NotificationFilter.ALL)
    ) { (filter, unreadCount, missingMessage), _ ->
        // Return placeholder while loading flow
        NotificationUiState(
            activeFilter = filter,
            unreadCount = unreadCount,
            missingEntityDialogMessage = missingMessage,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NotificationUiState(isLoading = true)
    )

    val notificationsGrouped: StateFlow<List<NotificationDateGroup>> = combine(
        _activeFilter,
    ) { filterArray ->
        filterArray[0] as NotificationFilter
    }.stateIn(viewModelScope, SharingStarted.Eagerly, NotificationFilter.ALL).let { filterFlow ->
        combine(filterFlow) { (filter) ->
            notificationRepository.getNotifications(filter = filter)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, kotlinx.coroutines.flow.flowOf(emptyList()))
            .let { nestedFlow ->
                // Flat map nested flow
                MutableStateFlow<List<NotificationDateGroup>>(emptyList())
            }
    }

    // Direct state representation for Compose UI
    val dateGroupsState: StateFlow<List<NotificationDateGroup>> = _activeFilter.combine(
        notificationRepository.getNotifications(NotificationFilter.ALL)
    ) { filter, allNotifications ->
        val filtered = allNotifications.filter { item ->
            when (filter) {
                NotificationFilter.ALL -> true
                NotificationFilter.UNREAD -> !item.isRead
                NotificationFilter.TRANSACTION -> item.category == com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory.TRANSAKSI
                NotificationFilter.TARGET -> item.category == com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory.TARGET_DANA
                NotificationFilter.RAPBM -> item.category == com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory.RAPBM
                NotificationFilter.AUDIT -> item.category == com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory.AUDIT
                NotificationFilter.BACKUP -> item.category == com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory.BACKUP
                NotificationFilter.SECURITY -> item.category == com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory.SECURITY
                NotificationFilter.SYSTEM -> item.category == com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory.SYSTEM
            }
        }
        groupNotificationsByDate(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCountState: StateFlow<Int> = notificationRepository.getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setFilter(filter: NotificationFilter) {
        _activeFilter.value = filter
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun dismissNotification(id: String) {
        viewModelScope.launch {
            notificationRepository.dismissNotification(id)
        }
    }

    fun dismissMissingEntityDialog() {
        _missingEntityDialogMessage.value = null
    }

    fun onNotificationClicked(
        item: NotificationItem,
        onNavigate: (String) -> Unit,
    ) {
        viewModelScope.launch {
            markAsRead(item.id)

            val type = item.targetEntityType
            val id = item.targetEntityId

            if (type == null || id == null) {
                // Generic notification without deep link
                return@launch
            }

            // Deep link verification
            when (type) {
                "transaction" -> {
                    val trx = transactionDao.getTransactionById(id)
                    if (trx != null) {
                        onNavigate(Screen.CashFlow.route)
                    } else {
                        _missingEntityDialogMessage.value = "Data transaksi terkait tidak tersedia."
                    }
                }
                "target" -> {
                    val target = targetDao.getTargetById(id)
                    if (target != null) {
                        onNavigate(Screen.Fundraising.route)
                    } else {
                        _missingEntityDialogMessage.value = "Data target dana terkait tidak tersedia."
                    }
                }
                "budget" -> {
                    onNavigate(Screen.Budget.route)
                }
                "audit" -> {
                    onNavigate(Screen.Audit.route)
                }
                "backup" -> {
                    onNavigate(Screen.Settings.route)
                }
                "security" -> {
                    onNavigate(Screen.Settings.route)
                }
                else -> {
                    _missingEntityDialogMessage.value = "Data terkait tidak tersedia."
                }
            }
        }
    }

    companion object {
        fun groupNotificationsByDate(items: List<NotificationItem>): List<NotificationDateGroup> {
            if (items.isEmpty()) return emptyList()

            val nowCal = Calendar.getInstance()
            val todayYear = nowCal.get(Calendar.YEAR)
            val todayDay = nowCal.get(Calendar.DAY_OF_YEAR)

            val todayItems = mutableListOf<NotificationItem>()
            val yesterdayItems = mutableListOf<NotificationItem>()
            val thisWeekItems = mutableListOf<NotificationItem>()
            val olderItems = mutableListOf<NotificationItem>()

            for (item in items) {
                val itemCal = Calendar.getInstance().apply { timeInMillis = item.timestamp }
                val itemYear = itemCal.get(Calendar.YEAR)
                val itemDay = itemCal.get(Calendar.DAY_OF_YEAR)

                val dayDiff = if (todayYear == itemYear) {
                    todayDay - itemDay
                } else {
                    val daysInPrevYear = if (nowCal.getActualMaximum(Calendar.DAY_OF_YEAR) == 366) 366 else 365
                    (todayDay + daysInPrevYear) - itemDay
                }

                when {
                    dayDiff == 0 -> todayItems.add(item)
                    dayDiff == 1 -> yesterdayItems.add(item)
                    dayDiff in 2..7 -> thisWeekItems.add(item)
                    else -> olderItems.add(item)
                }
            }

            val groups = mutableListOf<NotificationDateGroup>()
            if (todayItems.isNotEmpty()) groups.add(NotificationDateGroup("HARI INI", todayItems))
            if (yesterdayItems.isNotEmpty()) groups.add(NotificationDateGroup("KEMARIN", yesterdayItems))
            if (thisWeekItems.isNotEmpty()) groups.add(NotificationDateGroup("MINGGU INI", thisWeekItems))
            if (olderItems.isNotEmpty()) groups.add(NotificationDateGroup("SEBELUMNYA", olderItems))

            return groups
        }
    }
}
