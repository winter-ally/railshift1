package com.railshift.passenger.ui.screens.help

data class FaqItem(
    val id: String,
    val questionRes: Int,
    val answerRes: Int,
    val isExpanded: Boolean = false
)

data class HelpUiState(
    val searchQuery: String = "",
    val faqItems: List<FaqItem> = emptyList()
)
