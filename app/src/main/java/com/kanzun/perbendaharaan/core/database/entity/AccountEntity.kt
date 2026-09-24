package com.kanzun.perbendaharaan.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val accountNumber: String,
    val bankName: String,
    val isBank: Boolean,
    val openingBalanceInCents: Long,
    val currentBalanceInCents: Long,
    val currency: String = "IDR",
    val isActive: Boolean = true,
)
