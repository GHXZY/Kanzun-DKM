package com.kanzun.perbendaharaan.feature.reports.domain.repository

import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportContent
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportFilter
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportType
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun generateReport(reportType: ReportType, filter: ReportFilter): ReportContent
    fun getAuditLogs(filter: ReportFilter): Flow<List<AuditLogEntity>>
}
