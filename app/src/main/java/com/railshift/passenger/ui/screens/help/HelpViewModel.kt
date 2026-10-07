package com.railshift.passenger.ui.screens.help

import androidx.lifecycle.ViewModel
import com.example.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HelpViewModel : ViewModel() {

    private val initialFaqs = listOf(
        FaqItem("faq-1", R.string.faq_q1, R.string.faq_a1, isExpanded = true),
        FaqItem("faq-2", R.string.faq_q2, R.string.faq_a2, isExpanded = false),
        FaqItem("faq-3", R.string.faq_q3, R.string.faq_a3, isExpanded = false)
    )

    private val _uiState = MutableStateFlow(HelpUiState(faqItems = initialFaqs))
    val uiState: StateFlow<HelpUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFaq(id: String) {
        _uiState.update { state ->
            state.copy(
                faqItems = state.faqItems.map { item ->
                    if (item.id == id) item.copy(isExpanded = !item.isExpanded) else item
                }
            )
        }
    }
}
