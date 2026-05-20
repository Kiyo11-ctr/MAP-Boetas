# Valentine's Garage – MAP711S Android App

Android app built with **Kotlin + Jetpack Compose** for truck check-in, collaborative service boards, and management reporting.

## Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 (bundled with Android Studio)
- Android SDK 34

## Setup & Run
1. Extract the zip / clone the repo
2. Open Android Studio → **Open** → select the `ValentinesGarage` folder
3. Wait for Gradle sync (first time downloads ~500 MB of dependencies)
4. Create an emulator: **Tools → Device Manager → Create Device** (Pixel 6, API 34)
5. Press the green ▶ **Run** button

## Features
| Screen | What it does |
|--------|-------------|
| Dashboard | Summary of active vehicles and progress |
| Truck Check-In | Capture reg, odometer, condition — permanently |
| Service Board | Collaborative task checklist per vehicle |
| Reports | Per-employee activity + vehicle condition log for Valentine |
| Employees | Mechanic roster |

## Architecture
Three-layer: UI (Compose + ViewModel) → Domain (Repositories) → Data (Room DB + DAOs)
