package com.example.careconnect.ui.patient.medicine

import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.TimeOfDay
import com.example.careconnect.domain.repository.MedicineRepository
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.MedicineRepositoryMock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MedicineViewModelTest {

    // Repository, bei dem jedes Speichern fehlschlägt
    private class FailingMedicineRepository : MedicineRepository {
        override fun getMedicines(patientId: String): Flow<List<MedicineEntry>> = flowOf(emptyList())
        override suspend fun addMedicine(patientId: String, medicine: MedicineEntry): Result<Unit> =
            Result.failure(Exception("Keine Verbindung"))
        override suspend fun deleteMedicine(patientId: String, medicineId: String): Result<Unit> =
            Result.failure(Exception("Keine Verbindung"))
    }

    private val auth = AuthRepositoryMock(MockData.patient.id)

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addMedicine_appearsInList() = runTest {
        val viewModel = MedicineViewModel(MedicineRepositoryMock(), auth)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.medicines.collect {} }

        viewModel.addMedicine("  Ibuprofen ", "400 mg", TimeOfDay.NOON)

        assertTrue(viewModel.medicines.value.any { it.name == "Ibuprofen" })
    }

    @Test
    fun deleteMedicine_removesFromList() = runTest {
        val viewModel = MedicineViewModel(MedicineRepositoryMock(), auth)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.medicines.collect {} }
        val first = viewModel.medicines.value.first()

        viewModel.deleteMedicine(first)

        assertEquals(MockData.medicines.size - 1, viewModel.medicines.value.size)
    }

    @Test
    fun addMedicine_whenSavingFails_showsError() {
        val viewModel = MedicineViewModel(FailingMedicineRepository(), auth)

        viewModel.addMedicine("Ibuprofen", "400 mg", TimeOfDay.NOON)

        assertNotNull(viewModel.errorMessage.value)
    }

    @Test
    fun dismissError_clearsError() {
        val viewModel = MedicineViewModel(FailingMedicineRepository(), auth)
        viewModel.addMedicine("Ibuprofen", "400 mg", TimeOfDay.NOON)

        viewModel.dismissError()

        assertNull(viewModel.errorMessage.value)
    }
}
