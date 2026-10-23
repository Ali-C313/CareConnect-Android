package com.example.careconnect.mock

import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.model.CompletionReport
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.domain.model.Invite
import com.example.careconnect.domain.model.JoinRequest
import com.example.careconnect.domain.model.LinkedOrg
import com.example.careconnect.domain.model.Medication
import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.domain.model.Membership
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.model.Prescription
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.domain.model.TimeOfDay
import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.util.DateHelper.currentTimeMillis

// Beispieldaten für Previews und Mock-Repositories
object MockData {
    private val now = currentTimeMillis()
    private const val HOUR = 60 * 60 * 1000L
    private const val DAY = 24 * HOUR

    val organization = Organization(
        id = "org1",
        name = "Pflegedienst Sonnenschein",
        type = OrganizationType.PFLEGEDIENST,
        address = "Hauptstraße 1, 10115 Berlin",
        ownerId = "e1"
    )

    val practice = Organization(
        id = "org2",
        name = "Praxis Dr. Weber",
        type = OrganizationType.ARZTPRAXIS,
        address = "Friedrichstraße 20, 10117 Berlin"
    )

    val employee = Employee(
        id = "e1",
        firstName = "Ali",
        lastName = "Chehab",
        role = "Pflegekraft",
        status = StaffStatus.AVAILABLE,
        email = "ali@careconnect.de",
        birthDate = now - 30 * 365 * DAY
    )

    val employees = listOf(
        employee,
        Employee(id = "e2", firstName = "Sarah", lastName = "Klein", role = "Leitung", status = StaffStatus.BUSY, email = "sarah@careconnect.de"),
        Employee(id = "e3", firstName = "Tom", lastName = "Becker", role = "Pflegekraft", status = StaffStatus.PAUSE, email = "tom@careconnect.de")
    )

    val documents = listOf(
        EmployeeDocument(id = "d1", fileName = "Arbeitsvertrag.pdf", downloadUrl = "", uploadedAt = now - 30 * DAY),
        EmployeeDocument(id = "d2", fileName = "Erste-Hilfe-Kurs.pdf", downloadUrl = "", uploadedAt = now - 5 * DAY)
    )

    val patient = Patient(
        id = "p1",
        firstName = "Berta",
        lastName = "Landmann",
        address = "Torstraße 12",
        city = "10119 Berlin",
        phone = "030 1234567",
        email = "berta@example.com",
        allergies = "Penicillin",
        medicalHistory = "Diabetes Typ 2, Bluthochdruck",
        birthDate = now - 82 * 365 * DAY,
        notes = "Schlüssel liegt beim Nachbarn",
        medications = listOf(Medication("m1", "Metformin"), Medication("m2", "Ramipril")),
        history = listOf("Hüft-OP 2021"),
        emergencyContact = EmergencyContact(
            name = "Klaus Landmann",
            relation = "Sohn",
            phone = "0170 1234567",
            email = "klaus@example.com",
            address = "Kastanienallee 3, 10435 Berlin",
            age = 54
        ),
        linkedOrgs = listOf(LinkedOrg("org1", "Pflegedienst Sonnenschein"))
    )

    val patients = listOf(
        patient,
        Patient(id = "p2", firstName = "Heinz", lastName = "Müller", address = "Schönhauser Allee 80", city = "10439 Berlin", phone = "030 7654321")
    )

    val report = CompletionReport(
        note = "Verband gewechselt, Wunde heilt gut.",
        signature = "",
        signedBy = "Berta Landmann",
        completedAt = now - 2 * DAY,
        completedBy = "e1",
        completedByName = "Ali Chehab"
    )

    val orders = listOf(
        Order(id = "o1", state = OrderState.AKTUELL, description = "Notfallauftrag", patientId = "p1", patientName = "Berta Landmann", employeeId = "e1", dueDate = now, createdAt = now - HOUR),
        Order(id = "o2", state = OrderState.OFFEN, description = "Blutdruck messen", patientId = "p1", patientName = "Berta Landmann", dueDate = now + 2 * HOUR, createdAt = now - DAY),
        Order(id = "o3", state = OrderState.OFFEN, description = "Medikamente stellen", patientId = "p2", patientName = "Heinz Müller", dueDate = now + DAY, createdAt = now - DAY),
        Order(id = "o4", state = OrderState.ERLEDIGT, description = "Verbandswechsel", patientId = "p1", patientName = "Berta Landmann", employeeId = "e1", dueDate = now - 2 * DAY, createdAt = now - 3 * DAY, report = report)
    )

    val medicines = listOf(
        MedicineEntry(id = "md1", name = "Metformin", dosage = "500 mg", timeOfDay = TimeOfDay.MORNING),
        MedicineEntry(id = "md2", name = "Ramipril", dosage = "5 mg", timeOfDay = TimeOfDay.EVENING)
    )

    val appointments = listOf(
        Appointment(id = "a1", what = "Hausarzt", address = "Friedrichstraße 20, 10117 Berlin", date = now + 3 * DAY, bringItems = listOf("Krankenkassenkarte"), notes = "Blutwerte besprechen")
    )

    val prescriptions = listOf(
        Prescription(
            id = "rx1",
            fromOrgId = "org2",
            fromOrgName = "Praxis Dr. Weber",
            toOrgId = "org1",
            toOrgName = "Pflegedienst Sonnenschein",
            patientId = "p1",
            patientName = "Berta Landmann",
            title = "Verbandswechsel",
            details = "Wunde am linken Unterschenkel",
            frequency = "3x pro Woche",
            startDate = now + DAY,
            createdByName = "Dr. Weber",
            createdAt = now - HOUR
        )
    )

    val profile = UserProfile(
        uid = "e1",
        firstName = "Ali",
        lastName = "Chehab",
        email = "ali@careconnect.de",
        activeOrgId = "org1"
    )

    val membership = Membership(
        orgId = "org1",
        orgName = "Pflegedienst Sonnenschein",
        orgType = OrganizationType.PFLEGEDIENST,
        role = MemberRole.ADMIN
    )

    val members = listOf(
        Member("e1", "Ali", "Chehab", MemberRole.ADMIN),
        Member("e2", "Sarah", "Klein", MemberRole.STAFF),
        Member("p1", "Berta", "Landmann", MemberRole.PATIENT)
    )

    val invites = listOf(
        Invite("ABC123", "org1", "Pflegedienst Sonnenschein", OrganizationType.PFLEGEDIENST, MemberRole.STAFF),
        Invite("XYZ789", "org1", "Pflegedienst Sonnenschein", OrganizationType.PFLEGEDIENST, MemberRole.PATIENT)
    )

    val joinRequests = listOf(
        JoinRequest(
            orgId = "org1",
            orgName = "Pflegedienst Sonnenschein",
            orgType = OrganizationType.PFLEGEDIENST,
            uid = "p3",
            firstName = "Erika",
            lastName = "Schulz",
            email = "erika@example.com",
            city = "Berlin",
            createdAt = now - HOUR
        )
    )

    // Berlin Mitte
    val employeeLocation = Coordinate(52.5200, 13.4050)
    val patientLocation = Coordinate(52.5290, 13.4010)
}
