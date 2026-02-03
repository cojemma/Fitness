package com.fitness.sample.ui.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.WorkoutTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for the template list screen.
 */
class TemplateListViewModel : ViewModel() {

    private val templateManager by lazy { FitnessSDK.getTemplateManager() }

    private val _templates = MutableStateFlow<List<WorkoutTemplate>>(emptyList())
    val templates: StateFlow<List<WorkoutTemplate>> = _templates.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        try {
            observeTemplates()
        } catch (e: Exception) {
            _error.value = "Failed to initialize: ${e.message}"
            _isLoading.value = false
        }
    }

    private fun observeTemplates() {
        viewModelScope.launch {
            templateManager.observeTemplates()
                .catch { e ->
                    _error.value = e.message ?: "Failed to load templates"
                    _isLoading.value = false
                }
                .collect { templateList ->
                    _templates.value = templateList
                    _isLoading.value = false
                }
        }
    }

    fun deleteTemplate(templateId: Long) {
        viewModelScope.launch {
            templateManager.deleteTemplate(templateId)
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to delete template"
                }
        }
    }

    fun duplicateTemplate(templateId: Long) {
        viewModelScope.launch {
            templateManager.duplicateTemplate(templateId)
                .onSuccess {
                    // Template will appear in the list via observeTemplates
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to duplicate template"
                }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
