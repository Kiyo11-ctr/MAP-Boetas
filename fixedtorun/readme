# Valentine's Garage – Truck Check-In & Service Management App
**MAP711S – Mobile Application Development | NUST**

An Android application built with Kotlin and Jetpack Compose that allows Valentine's Garage to manage truck check-ins, collaborative mechanic service boards, and management reporting.

---

## Features

| Feature | Description |
|---|---|
| **Truck Check-In** | Records registration, make/model, odometer at arrival, vehicle condition, and condition notes — permanently — to prevent misuse and establish a baseline |
| **Collaborative Service Board** | Mechanics tick off tasks and write notes per vehicle. Every completion is attributed to a named mechanic. Prevents tasks from going undone |
| **Management Reports** | Valentine sees per-employee task completion stats and a full vehicle condition log |
| **Employee Roster** | Lists all mechanics and their roles |

---

## Architecture

This app follows the **three-layer Android architecture** as taught in MAP711S:

```
UI Layer          →  Screens (Composables) + ViewModels + StateFlow
Domain Layer      →  EmployeeReport, validation helpers
Data Layer        →  Repositories → DAOs → Room Database
```

**Key patterns used:**
- **Unidirectional Data Flow (UDF)** — state flows down from ViewModel, events flow up from UI
- **Single Source of Truth** — Room database; all queries return `Flow<T>` so UI reacts automatically
- **Repository pattern** — UI never touches DAOs directly
- **Dependency Injection** — Hilt provides all dependencies
- **Single Activity** — Jetpack Navigation Compose manages all screen transitions

---

## Tech Stack

| Component | Library |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Database | Room (SQLite) |
| State management | StateFlow / ViewModel |
| DI | Hilt (Dagger) |
| Async | Kotlin Coroutines |
| Testing | JUnit 4, Espresso |

---

## Project Structure

```
app/src/main/java/com/valentinesgarage/
├── GarageApplication.kt          # Hilt app entry point
├── MainActivity.kt               # Single activity
├── data/
│   ├── model/                    # Entity data classes + enums
│   │   ├── Vehicle.kt
│   │   ├── ServiceTask.kt
│   │   ├── Employee.kt
│   │   ├── VehicleWithTasks.kt
│   │   └── EmployeeReport.kt
│   ├── local/                    # Room database + DAOs
│   │   ├── GarageDatabase.kt
│   │   ├── VehicleDao.kt
│   │   ├── ServiceTaskDao.kt
│   │   └── EmployeeDao.kt
│   └── repository/
│       ├── VehicleRepository.kt
│       └── EmployeeRepository.kt
├── di/
│   └── DatabaseModule.kt         # Hilt DI module
└── ui/
    ├── theme/Theme.kt
    ├── navigation/NavHost.kt
    ├── components/SharedComponents.kt
    ├── dashboard/               (DashboardScreen + ViewModel)
    ├── checkin/                 (CheckInScreen + ViewModel)
    ├── service/                 (ServiceBoardScreen + ViewModel)
    ├── reports/                 (ReportsScreen + ViewModel)
    └── employees/               (EmployeesScreen + ViewModel)
```

---

## Setup

1. Clone this repo
2. Open in **Android Studio Hedgehog** (or newer)
3. Let Gradle sync
4. Run on emulator (API 26+) or physical device

> The database seeds default employees (Valentine, Johannes, Petrus, Maria, Festus, Absalom) on first launch.

---

## Unit Tests

```bash
./gradlew test
```

Tests cover:
- Odometer input validation (boundary values: 0, negative, non-numeric)
- Registration field validation
- Condition notes requirement for critical vehicles
- `EmployeeReport.completionPercent` — including division-by-zero guard

---

## Assessment Rubric Mapping

| Criterion | Implementation |
|---|---|
| **App Architecture** | 3-layer architecture, Hilt DI, modularised packages, Room DB |
| **Code** | KDoc comments, single-responsibility functions, unit tests |
| **Functionality** | Check-in, collaborative service board, reports, employee roster |
| **UI & Navigation** | Material 3, bottom nav, dialogs, snackbars, progress indicators |
| **Presentation** | Clear separation: each team member owns a feature module |
