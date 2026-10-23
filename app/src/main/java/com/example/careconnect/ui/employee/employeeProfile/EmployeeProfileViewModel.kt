package com.example.careconnect.ui.employee.employeeProfile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.careconnect.R
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.ui.employee.EmployeeRoute
import com.example.careconnect.util.ResourceHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class EmployeeProfileViewModel(
    private val employeeRepo: EmployeeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ID kommt über die Route
    private val employeeId = MutableStateFlow<String?>(savedStateHandle.toRoute<EmployeeRoute.EmployeeProfile>().employeeId)

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val employee: StateFlow<Employee?> = employeeId
        .filterNotNull()
        .flatMapLatest { employeeRepo.getEmployee(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val documents: StateFlow<List<EmployeeDocument>> = employeeId
        .filterNotNull()
        .flatMapLatest { employeeRepo.getDocuments(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * PDF hochladen und in employees/{id}/documents speichern.
     * @param fileName Dateiname aus dem Picker
     */
    fun uploadPdf(fileName: String, bytes: ByteArray) {
        val id = employeeId.value ?: return
        val name = if (fileName.endsWith(".pdf", ignoreCase = true)) fileName else "$fileName.pdf"

        if (documents.value.any { it.fileName == name }) {
            _errorMessage.update { ResourceHelper.getString(R.string.diese_datei_wurde_bereits_hochgeladen) }
            return
        }

        viewModelScope.launch {
            _isUploading.update { true }
            employeeRepo.addDocument(id, name, bytes)
                .onFailure { e -> _errorMessage.update { ResourceHelper.getString(R.string.upload_fehlgeschlagen, e.message.orEmpty()) } }
            _isUploading.update { false }
        }
    }

    fun deleteDocument(document: EmployeeDocument) {
        val id = employeeId.value ?: return
        viewModelScope.launch {
            employeeRepo.deleteDocument(id, document.id)
                .onFailure { e -> _errorMessage.update { ResourceHelper.getString(R.string.loeschen_fehlgeschlagen, e.message.orEmpty()) } }
        }
    }

    fun dismissError() {
        _errorMessage.update { null }
    }
}
