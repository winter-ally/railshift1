# Railshift Passenger Android App

Railshift is a modern, passenger-facing Android application for train ticket booking built with Jetpack Compose and Material Design 3. It supports Reserved ticket booking, Paperless General (Unreserved) tickets, Platform entry tickets, Paper ticket upgrades to higher classes, real-time train tracking, PNR status lookup, and station QR scanning.

---

## Architecture & Tech Stack

- **Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** Single-Activity MVVM (Model-View-ViewModel) with Kotlin Coroutines and `StateFlow`
- **Navigation:** Navigation Compose (`NavHost` with type-safe route management and bottom bar visibility rules)
- **QR & Camera:** CameraX (`camera2`, `lifecycle`, `view`) + ML Kit Barcode Scanning (`com.google.mlkit:barcode-scanning`)
- **Typography:** Plus Jakarta Sans font family (`res/font/plus_jakarta_sans.ttf`)
- **Theming:** Dual Light & Dark themes following system settings with persistent user override
  - **Light Theme:** Background `#EDF1F7`, Surface `#FFFFFF`, Surface2 `#F2F5FA`, Border `#E4E9F1`, Text `#0E1726`, Accent `#1B6EF3`, Success `#12793A`, Danger `#D92D3A`
  - **Dark Theme:** Background `#07090D`, Surface `#12151B`, Surface2 `#1B1F27`, Border `#262B35`, Text `#F1F4F9`, Accent `#5CA3FF`, Success `#4FD28A`, Danger `#FF6B73`
  - **Hero & Wallet Gradients:** Fixed 135° linear gradient `#0A2A6B` to `#1B6EF3`
  - **Shapes:** Tiles 16dp, Cards/Inputs/Buttons 12dp (height 46dp), Chips 16dp
- **Data Persistence:** Jetpack DataStore (`datastore-preferences`) for theme and language preferences
- **Localization:** 3 languages with resource strings:
  - English (`res/values/strings.xml`)
  - Hindi (`res/values-hi/strings.xml`)
  - Telugu (`res/values-te/strings.xml`)

---

## Directory Structure

```
passengerapp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── com/example/
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   └── com/railshift/passenger/
│   │   │   │       ├── data/
│   │   │   │       │   ├── models/           # Data models (Train, Trip, Station, UserProfile, etc.)
│   │   │   │       │   ├── preferences/      # DataStore preferences repository
│   │   │   │       │   └── repository/       # RailshiftRepository interface & Fake implementation
│   │   │   │       ├── navigation/           # Routes and navigation graph logic
│   │   │   │       ├── ui/
│   │   │   │       │   ├── components/       # Reusable UI widgets (StationCard, TripCard, etc.)
│   │   │   │       │   ├── screens/          # Feature screens (Home, Reserved, Unreserved, etc.)
│   │   │   │       │   ├── theme/            # Color, Type, Shape, Theme tokens
│   │   │   │       │   └── RailshiftApp.kt   # App root Scaffold and NavHost
│   │   │   ├── res/
│   │   │   │   ├── font/                     # plus_jakarta_sans.ttf
│   │   │   │   ├── values/                   # strings.xml, colors.xml, themes.xml
│   │   │   │   ├── values-hi/                # Hindi strings.xml
│   │   │   │   └── values-te/                # Telugu strings.xml
│   │   ├── test/                             # Unit tests
│   └── build.gradle.kts
├── docs/
│   └── screenshots/                          # Screen references
├── gradle/
│   └── libs.versions.toml                    # Version catalog
└── README.md
```

---

## Features & Screens

1. **Home Screen**
   - Top bar with language switcher, Railshift brand mark, and notification indicator
   - Personalized greeting ("Good morning, Ravi")
   - 2x2 main services grid: Reserved (primary highlighted), Unreserved, Platform, Upgrade ticket
   - "More services" quick grid: Search trains, PNR status, Coach position, Track your train
   - Upcoming trip card with status badge and departure/arrival times

2. **Reserved Ticket Screen**
   - From/To station selection with interactive station swap button
   - Journey date picker with Today, Tomorrow, and custom date chips
   - Class selector (SL, 3A, 2A, 1A, CC)
   - Filters: "Show only trains with available berths" and "Flexible with dates"
   - Search results list with train timings, duration, class fares, availability chips, and booking

3. **Unreserved Ticket Screen**
   - Paperless general ticket booking between major stations
   - Steppers for Adults (12+ years) and Children (5–11 years) with touch-friendly 48dp controls
   - Real-time live fare breakdown and total fare summary
   - One-tap booking with UTS ticket confirmation dialog
   - Recent tickets quick-rebook list

4. **Upgrade Ticket Screen**
   - Viewfinder with corner brackets and animated scanning laser to scan paper ticket QR
   - Gallery import and manual "Type no." options
   - Scanned status banner and general ticket details card
   - Train picker (e.g. 12711 Pinakini Express)
   - Class options (Sleeper, AC 3 Tier, AC 2 Tier) with berth availability and per-person fare difference
   - Real-time amount to pay summary and confirmation

5. **Platform Ticket Screen**
   - High-contrast gradient hero banner with platform and train artwork
   - Journey date field and 2x2 recent stations grid (BZA, MAS, HYB, SC)
   - One-tap search & booking with instant pass generation
   - Information notice on platform ticket validity

6. **My Bookings Screen**
   - Active and Past filter tabs
   - Trip cards with status badges (Confirmed in green, Completed in gray), coach and berth details
   - Departure and arrival timings with dashed train path
   - Past 6 months booking history stack

7. **Help & Support Screen**
   - Help topics search bar
   - Direct communication channels: Call (139 rail helpline), Chat, Email
   - Browse topics: Booking & tickets, Cancellation & refunds, PNR status, rWallet, QR scanning
   - Expandable frequently asked questions (FAQs)

8. **Profile & Account Screen**
   - User initials avatar (`RK`) with gradient styling
   - User identity (Ravi Kumar, ravi.kumar@example.com)
   - Menu navigation: Edit profile, Payment methods, Booking history, Preferences (toggle theme), Log out

9. **Edit Profile Screen**
   - Editable Name and Email address
   - Masked password with edit dialog
   - rWallet gradient card with real-time balance and "Add money" dialog
   - Log out action

10. **QR Scanner Screen**
    - Full-screen camera viewfinder using CameraX
    - Real-time QR and barcode detection via Google ML Kit Barcode Scanning
    - Graceful runtime permission rationale and denied state handling
    - Quick-scan test triggers for emulator validation

---

## Setup & Build Instructions

1. **Prerequisites:**
   - Android SDK 35/36
   - JDK 11 or higher
   - Gradle with Kotlin DSL

2. **Compilation:**
   ```bash
   gradle assembleDebug
   ```

3. **Running Unit Tests:**
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## Backend Integration Requirements (What is Currently Faked)

The app currently uses a robust repository layer (`RailshiftRepository` & `FakeRailshiftRepository`) with in-memory state. To connect to the production backend (`railshift/backend`), implement the following REST or gRPC integrations:

1. **Authentication & User Management:**
   - Real login/signup endpoint to replace the hardcoded `Ravi Kumar` profile
   - JWT or OAuth2 session token management stored securely in encrypted DataStore
   - Password reset and profile picture upload APIs

2. **Train Search & Berth Availability:**
   - Real-time IRCTC / Indian Railways search API for train schedules, route halts, and seat quotas
   - Live berth availability inventory (SL, 3A, 2A, 1A, CC)
   - Quota management (General, Tatkal, Senior Citizen, Ladies)

3. **Ticket Booking & Payment Gateway:**
   - Payment gateway integration (Razorpay / Cashfree / UPI Intent) for ticket payments
   - rWallet backend integration for wallet balances, recharge, and deductions
   - Real PNR allocation and IRCTC PRS transaction processing

4. **Paper Ticket Digitization / Upgrade Backend:**
   - Server-side verification of scanned paper ticket QR barcodes against UTS database
   - Automatic calculation of fare difference between unreserved fare and target train class
   - Generation of upgraded digital PNR replacing the paper ticket

5. **Live Train Tracking & GPS:**
   - Real-time GPS train location feed and platform rake layout (coach position) APIs
   - Platform ticket generation system integrated with station gate entry turnstiles

6. **Push Notifications:**
   - Firebase Cloud Messaging (FCM) integration for PNR status chart preparation, platform changes, and delay alerts
