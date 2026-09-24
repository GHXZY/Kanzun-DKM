package com.kanzun.perbendaharaan.core.util

import java.util.Locale

fun String.formatCategoryName(): String {
    if (this.isBlank()) return ""
    var cleaned = this
    if (cleaned.startsWith("cat_in_", ignoreCase = true)) {
        cleaned = cleaned.substring(7)
    } else if (cleaned.startsWith("cat_ex_", ignoreCase = true)) {
        cleaned = cleaned.substring(7)
    } else if (cleaned.startsWith("cat_", ignoreCase = true)) {
        cleaned = cleaned.substring(4)
    }
    return cleaned
        .replace("_", " ")
        .replace("-", " ")
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
}

fun String.formatAccountName(): String {
    if (this.isBlank()) return ""
    var cleaned = this
    if (cleaned.startsWith("acc_", ignoreCase = true)) {
        cleaned = cleaned.substring(4)
    }
    cleaned = cleaned.replace("_", " ").replace("-", " ").trim()
    if (cleaned.equals("bsi", ignoreCase = true) || cleaned.contains("bsi", ignoreCase = true)) {
        return "Rekening"
    }
    if (cleaned.equals("cash", ignoreCase = true)) {
        return "Kas Tunai"
    }
    return cleaned.split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
}
