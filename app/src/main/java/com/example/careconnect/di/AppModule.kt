package com.example.careconnect.di

import com.example.careconnect.domain.repository.AppointmentRepository
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.domain.repository.MedicineRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.OrganizationRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.domain.repository.PrescriptionRepository
import com.example.careconnect.domain.repository.SettingsRepository
import com.example.careconnect.domain.repository.StorageRepository
import com.example.careconnect.domain.repository.UserRepository
import com.example.careconnect.local.SettingsRepositoryImpl
import com.example.careconnect.local.dataStore
import com.example.careconnect.platform.AndroidGeocoder
import com.example.careconnect.platform.AndroidLocationTracker
import com.example.careconnect.platform.AndroidNotifier
import com.example.careconnect.platform.LocationTracker
import com.example.careconnect.platform.Notifier
import com.example.careconnect.platform.PlatformGeocoder
import com.example.careconnect.remote.api.GeoapifyApi
import com.example.careconnect.remote.api.OsrmApi
import com.example.careconnect.remote.repository.AppointmentRepositoryImpl
import com.example.careconnect.remote.repository.AuthRepositoryImpl
import com.example.careconnect.remote.repository.EmployeeRepositoryImpl
import com.example.careconnect.remote.repository.GeoRepositoryImpl
import com.example.careconnect.remote.repository.LocationRepositoryImpl
import com.example.careconnect.remote.repository.MedicineRepositoryImpl
import com.example.careconnect.remote.repository.OrderRepositoryImpl
import com.example.careconnect.remote.repository.OrganizationRepositoryImpl
import com.example.careconnect.remote.repository.PatientRepositoryImpl
import com.example.careconnect.remote.repository.PrescriptionRepositoryImpl
import com.example.careconnect.remote.repository.StorageRepositoryImpl
import com.example.careconnect.remote.repository.UserRepositoryImpl
import com.example.careconnect.service.NotificationWatcher
import com.example.careconnect.service.TrackingManager
import com.example.careconnect.session.OrgSession
import com.example.careconnect.ui.auth.AuthViewModel
import com.example.careconnect.ui.auth.LoginViewModel
import com.example.careconnect.ui.auth.RegisterViewModel
import com.example.careconnect.ui.employee.employeeProfile.EmployeeProfileViewModel
import com.example.careconnect.ui.employee.home.HomeViewModel
import com.example.careconnect.ui.employee.orderDetail.OrderDetailViewModel
import com.example.careconnect.ui.employee.orders.NewOrderViewModel
import com.example.careconnect.ui.employee.orders.OrdersViewModel
import com.example.careconnect.ui.employee.organization.ManageOrganizationViewModel
import com.example.careconnect.ui.employee.patientProfile.PatientProfileViewModel
import com.example.careconnect.ui.employee.patients.PatientsViewModel
import com.example.careconnect.ui.employee.prescriptions.PrescriptionsViewModel
import com.example.careconnect.ui.employee.search.SearchViewModel
import com.example.careconnect.ui.employee.settings.EditEmployeeProfileViewModel
import com.example.careconnect.ui.organization.OrganizationViewModel
import com.example.careconnect.ui.patient.appointments.AppointmentsViewModel
import com.example.careconnect.ui.patient.home.PatientHomeViewModel
import com.example.careconnect.ui.patient.medicine.MedicineViewModel
import com.example.careconnect.ui.patient.orders.PatientOrderDetailViewModel
import com.example.careconnect.ui.patient.orders.PatientOrdersViewModel
import com.example.careconnect.ui.patient.settings.EditPatientProfileViewModel
import com.example.careconnect.ui.patient.settings.SharingViewModel
import com.example.careconnect.ui.settings.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    // Retrofit für Geoapify und OSRM
    single { GeoapifyApi.retrofitService }
    single { OsrmApi.retrofitService }

    // DataStore und Geräte-Funktionen
    single { androidContext().dataStore }
    single<LocationTracker> { AndroidLocationTracker(androidContext()) }
    single<PlatformGeocoder> { AndroidGeocoder(androidContext()) }
    single<Notifier> { AndroidNotifier(androidContext()) }

    // Aktuelle Organisation
    single { OrgSession() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<OrganizationRepository> { OrganizationRepositoryImpl(get()) }
    single<StorageRepository> { StorageRepositoryImpl(get()) }
    single<EmployeeRepository> { EmployeeRepositoryImpl(get(), get(), get()) }
    single<PatientRepository> { PatientRepositoryImpl(get(), get()) }
    single<OrderRepository> { OrderRepositoryImpl(get(), get()) }
    single<MedicineRepository> { MedicineRepositoryImpl(get(), get()) }
    single<AppointmentRepository> { AppointmentRepositoryImpl(get(), get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single<PrescriptionRepository> { PrescriptionRepositoryImpl(get(), get()) }
    single<GeoRepository> { GeoRepositoryImpl(get(), get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single { TrackingManager(get(), get()) }
    single { NotificationWatcher(get(), get(), get(), get(), get()) }

    // ViewModels
    viewModelOf(::AuthViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::OrganizationViewModel)

    // Mitarbeiter
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::PatientsViewModel)
    viewModelOf(::PatientProfileViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::NewOrderViewModel)
    viewModelOf(::OrderDetailViewModel)
    viewModelOf(::EmployeeProfileViewModel)
    viewModelOf(::EditEmployeeProfileViewModel)
    viewModelOf(::ManageOrganizationViewModel)
    viewModelOf(::PrescriptionsViewModel)

    // Patienten
    viewModelOf(::PatientHomeViewModel)
    viewModelOf(::PatientOrdersViewModel)
    viewModelOf(::PatientOrderDetailViewModel)
    viewModelOf(::MedicineViewModel)
    viewModelOf(::AppointmentsViewModel)
    viewModelOf(::EditPatientProfileViewModel)
    viewModelOf(::SharingViewModel)
}

