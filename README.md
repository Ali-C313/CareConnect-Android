<div align="center">

<img src="docs/banner.svg" alt="CareConnect – Pflege, die verbindet" width="100%"/>

<br/>

![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20·%20Firestore%20·%20Storage-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Android](https://img.shields.io/badge/Android-8.0%2B%20(API%2026)-3DDC84?style=for-the-badge&logo=android&logoColor=white)

![Architektur](https://img.shields.io/badge/Architektur-MVVM%20%2B%20Repository-1EAE98?style=flat-square)
![DI](https://img.shields.io/badge/DI-Koin-30B0C7?style=flat-square)
![Netzwerk](https://img.shields.io/badge/Netzwerk-Retrofit%20%2B%20Moshi-1EAE98?style=flat-square)
![Sprachen](https://img.shields.io/badge/Sprachen-DE%20·%20EN%20·%20TR%20·%20AR%20·%20ES-30B0C7?style=flat-square)
![Dark Mode](https://img.shields.io/badge/Dark%20Mode-✓-1C1C1E?style=flat-square)

**Die All-in-One-App für die ambulante Pflege.**<br/>
Pflegedienste, Arztpraxen, Kliniken und Patienten arbeiten in einer App zusammen –<br/>
von der Verordnung über den Einsatz bis zur Live-Ankunft an der Haustür.

[Features](#-features) · [Vorschau](#-vorschau) · [Technik](#-technik) · [Architektur](#-architektur) · [Projekt starten](#-projekt-starten)

</div>

---

## 💡 Die Idee

In der Pflege laufen viele Absprachen noch über Telefon, Zettel und Faxgeräte. Die Arztpraxis schickt eine Verordnung, der Pflegedienst plant Einsätze per Excel, und der Patient weiß nicht, wann jemand kommt.

**CareConnect bringt alle Beteiligten in eine App.** Jede Organisation hat ihren eigenen Bereich, jede Rolle sieht genau das, was sie braucht. Der Patient sieht wie bei einem Lieferdienst, wo seine Pflegekraft gerade ist und wann sie ankommt.

<div align="center">
<img src="docs/roles.svg" alt="Ablauf: Arztpraxis, Pflegedienst-Leitung, Pflegekraft, Patient" width="100%"/>
</div>

---

## 📱 Vorschau

<div align="center">
<img src="docs/screens.svg" alt="Vorschau der App: Startseite, Aufträge, Live-Standort, Patienten-Startseite" width="100%"/>
<br/>
<sub><i>Illustrierte Vorschau der Oberfläche · Namen und Daten sind Beispiele</i></sub>
</div>

---

## ✨ Features

<table>
<tr>
<td width="50%" valign="top">

### 🏢 Organisationen & Rollen
- Pflegedienste, Arztpraxen und Kliniken gründen ihre **eigene Organisation**
- Beitritt per **Einladungscode** oder **Suche mit Beitrittsanfrage**
- Rollen: **Leitung**, **Mitarbeiter**, **Patient**
- Leitung verwaltet Anfragen, Codes und Mitglieder
- Wechsel zwischen mehreren Organisationen

</td>
<td width="50%" valign="top">

### 📝 Aufträge & Leistungsnachweis
- Aufträge nach **Offen · Aktuell · Erledigt**
- Annehmen mit **Transaktion**, damit niemand doppelt annimmt
- Abschluss mit **Notiz und Unterschrift mit dem Finger**
- Patienten sehen ihre Aufträge mit **Status-Verlauf**

</td>
</tr>
<tr>
<td width="50%" valign="top">

### 🚗 Live-Standort
- Patient sieht die Pflegekraft als **Auto auf der Karte**
- **Strecke als Linie** und **Ankunftszeit**
- Geteilt wird nur während eines laufenden Auftrags
- Nur mit **Zustimmung von beiden Seiten**
- OpenStreetMap-Karte, Routing über OSRM – **kein API-Key nötig**

</td>
<td width="50%" valign="top">

### 📄 Verordnungen
- Praxen und Kliniken schicken Verordnungen an den **freigegebenen Pflegedienst**
- Übernahme als **einzelner** oder **wiederkehrender Einsatz**<br/>(z. B. Mo / Mi / Fr für 4 Wochen)

</td>
</tr>
<tr>
<td width="50%" valign="top">

### 🏠 Startseiten pro Rolle
- **Mitarbeiter:** eigener Status (Verfügbar, Beschäftigt, Pause …), Team-Übersicht und „Heute für dich“ mit Tour in Google Maps
- **Patient:** großer **Notruf-Button** – 112, Notfallkontakt oder Pflegedienst

</td>
<td width="50%" valign="top">

### 🧑‍🤝‍🧑 Patienten, 💊 Medikamente, 📅 Termine
- Patientenprofil mit Kontakt, Notfallkontakt, Allergien und Vorgeschichte
- Medikamente nach **Morgens · Mittags · Abends**
- Termine mit Adresse und Dingen zum Mitbringen

</td>
</tr>
<tr>
<td width="50%" valign="top">

### 🔔 Benachrichtigungen
- Mitarbeiter: neue Aufträge und Verordnungen – **nur während der Arbeitszeit**
- Patienten: Auftrag angenommen, erledigt, geändert oder abgesagt

</td>
<td width="50%" valign="top">

### 🌍 Sprachen & Darstellung
- **Deutsch, Englisch, Türkisch, Arabisch** (von rechts nach links) und **Spanisch**
- Sprache direkt in der App umschaltbar
- **Hell- und Dunkelmodus**

</td>
</tr>
</table>

---

## 🛠 Technik

| Bereich | Umsetzung |
|---|---|
| **Sprache & UI** | Kotlin, Jetpack Compose, Material 3 |
| **Architektur** | MVVM mit Repository Pattern |
| **Dependency Injection** | Koin |
| **Backend** | Firebase Authentication, Cloud Firestore, Cloud Storage |
| **Navigation** | Navigation Compose mit typsicheren Routen |
| **Lokale Daten** | DataStore Preferences |
| **Texte & Sprachen** | `strings.xml` in 5 Sprachen, App-Sprache über `AppCompatDelegate` |
| **Netzwerk** | Retrofit + Moshi + OkHttp-Logging (Routing über OSRM, optional Geoapify) |
| **Bilder & Dateien** | Coil, Photo Picker, Activity Result API |
| **Karte** | osmdroid (OpenStreetMap-Daten) |
| **Standort** | FusedLocationProvider |

---

## 🏛 Architektur

```mermaid
flowchart LR
    subgraph UI["🎨 UI Layer"]
        S["Screens &<br/>Components"] --> VM["ViewModels<br/>(StateFlow)"]
    end
    subgraph DOMAIN["📦 Domain"]
        RI["Repository-<br/>Interfaces"]
        M["Datenmodelle"]
    end
    subgraph DATA["💾 Data Layer"]
        FR["Firestore-<br/>Repositories"]
        API["Retrofit<br/>OSRM · Geoapify"]
        DS["DataStore"]
    end
    subgraph DEVICE["📱 Gerät"]
        LOC["Standort"]
        NOT["Benachrichtigungen"]
        GEO["Geocoder"]
    end

    VM --> RI
    RI -.implementiert.-> FR
    RI -.implementiert.-> API
    RI -.implementiert.-> DS
    FR --> FB[("☁️ Firebase")]
    VM --> DEVICE

    KOIN{{"Koin"}} -. stellt bereit .-> VM
    KOIN -. stellt bereit .-> FR
```

- **Jedes Composable hat eine eigene Datei mit Preview.** Die Previews laufen über die Mocks in `mock/repository` – dafür braucht man weder Firebase noch Koin.
- **Live-Daten überall:** Firestore-`SnapshotListener` werden per `callbackFlow` zum `Flow` und landen über `StateFlow` direkt in der UI.
- **Navigation mit Argumenten:** Detail-Screens holen ihre ID über `SavedStateHandle.toRoute()` im ViewModel.
- **Fehler werden abgefangen** (`Result`, `runCatching`) und in der Oberfläche verständlich angezeigt.

<details>
<summary><b>🗂 Datenmodell in Firestore</b></summary>

<br/>

Alle Daten liegen pro Organisation getrennt. So sieht jede Organisation nur ihre eigenen Patienten, Aufträge und Verordnungen.

```mermaid
flowchart TD
    ORG["organizations/{orgId}"] --> MEM["members"]
    ORG --> EMP["employees"]
    EMP --> DOC["documents"]
    ORG --> PAT["patients"]
    PAT --> MED["medicines"]
    PAT --> APP["appointments"]
    ORG --> ORD["orders"]
    ORG --> PRE["prescriptions"]
    ORG --> LOCS["employeeLocations"]
    ORG --> JR["joinRequests"]

    USR["users/{uid}"] --> MS["memberships"]
    INV["invites/{code}"]
    DIR["directory/{orgId}"]
```

Die Zugriffsregeln stehen in [`firestore.rules`](firestore.rules) und [`storage.rules`](storage.rules).

</details>

<details>
<summary><b>📁 Projektstruktur</b></summary>

<br/>

```
app/src/main/java/com/example/careconnect
├── di/                 App-Klasse (startet Koin) und Koin-Modul
├── domain/
│   ├── model/          Datenmodelle (je Klasse/Enum eine Datei)
│   └── repository/     Repository-Interfaces
├── remote/
│   ├── api/            Retrofit (Geoapify, OSRM)
│   ├── FirestoreListener.kt  SnapshotListener als Flow
│   ├── model/          DTOs mit Mappern
│   └── repository/     Firestore-Implementierungen
├── local/              DataStore (Einstellungen)
├── mock/               Beispieldaten und Repository-Mocks für die Previews
├── service/            Standort-Tracking und Benachrichtigungen
├── session/            Aktuelle Organisation
├── platform/           Karte, Geocoder, Standort, Benachrichtigungen
├── i18n/               Auswählbare App-Sprachen
├── ui/                 Screens, Komponenten und ViewModels pro Feature
├── util/               Helferklassen (Datum, Texte, Dateien, Links)
└── MainActivity.kt     Einstieg, Berechtigungen

app/src/main/res/values*/strings.xml   Texte auf Deutsch, Englisch, Türkisch, Arabisch, Spanisch
```

</details>

---

## 🚀 Projekt starten

**Voraussetzungen**
- Android Studio (aktuelle Version)
- Ein Firebase-Projekt mit Authentication (E-Mail/Passwort), Firestore und Storage

**Firebase einrichten**
1. `google-services.json` nach `app/` legen
2. Inhalt von `firestore.rules` und `storage.rules` in der Firebase-Konsole veröffentlichen

**Starten**
```bash
./gradlew :app:assembleDebug
```
oder in Android Studio die Run-Konfiguration `app` starten.

<details>
<summary><b>⚙️ Optional</b></summary>

<br/>

- Geoapify-Key in `remote/Secrets.kt` eintragen – dann gibt es Adressvorschläge beim Tippen
- Im Emulator unter **⋯ → Location** einen Standort oder eine Route setzen, um den Live-Standort zu testen

</details>

---

## 🔭 Ausblick

- [ ] Push-Benachrichtigungen mit Firebase Cloud Messaging, auch wenn die App geschlossen ist
- [ ] Unit- und UI-Tests für ViewModels und wichtige Screens
- [ ] Export des Leistungsnachweises als PDF
- [ ] Veröffentlichung im Google Play Store

---

<div align="center">

### 👤 Autor

**Ali Chehab**<br/>
Abschlussprojekt im Android-Modul am Syntax Institut

[![GitHub](https://img.shields.io/badge/GitHub-Ali--C313-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Ali-C313)

</div>
