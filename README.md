# RailOne (Indian Railways Super App) - Android Clone

A production-grade Android clone of India's official railway super app, **RailOne**, built with **Kotlin**, **Jetpack Compose**, and **Material 3** for high-impact live college project demonstrations.

---

## 🌟 Key Highlights & Demo Features

1. **Pixel-Perfect Visual Identity & Branding**:
   - Signature **Indian Railways Deep Blue (`#0B63E5` / `#1A4E8A`)** and **Vibrant Saffron/Orange (`#F26522`)**.
   - Custom **adaptive launcher icon** with aerodynamic bullet train / Vande Bharat nose profile.
   - Personalized greeting: `"Hi, Kedar Mitesh Vyas!"` matching real Indian Railways user account profiles.
   - Clean Material 3 surfaces, subtle card elevations, and authentic ticket cutouts with side semi-circle notches.

2. **Core Demo Flow (Zero-Failure 100% Offline)**:
   - **Home Screen** → **Unreserved Ticket Booking** → **Instant Payment Bypass** → **Digital Ticket View** → **My Bookings**.
   - Realistic 1-second `"Generating Ticket..."` simulation connecting to local UTS database.
   - **Persistent Local Storage**: Booked tickets are persisted locally via `TicketRepository` (backed by Android SharedPreferences + JSON serialization) so tickets remain saved even if the app is closed and reopened.

3. **Authentic Digital Ticket View**:
   - Header with **"Booking Details"** & `"Thank You, Kedar Mitesh Vyas, Happy Journey!"`.
   - **Black animated dynamic preview card** with Indian Railways branding and live digital countdown timer (`"Dynamic preview will close in 03:46"` counting down every second).
   - High-density **scannable QR Code** rendered dynamically on Compose Canvas using ZXing encoding.
   - Authentic route grid (`BHARUCH JN. (BH)` → `VADODARA JN. (BRC)`), 71 km distance, Class SECOND (II), Train Type SUPERFAST, ₹60.00 fare.
   - Statutory Indian Railways disclaimers (Railways Act 1989 Section 143, 57% cost recovery notice, non-refundable local storage warning).

4. **Authentic My Bookings Screen**:
   - Dynamic tabbed view: **Upcoming**, **Completed**, **Cancelled**, **All**.
   - Ticket cards with **physical ticket cutout notches** and dashed perforations.
   - Newly booked unreserved tickets immediately appear at the top with green `● ACTIVE` badge.
   - Pre-populated realistic historical reserved tickets:
     - **20906 EKNR MAHAMANA**: Jabalpur (JBP) → Bharuch Jn (BH)
     - **19309 SHANTI EXPRESS**: Godhra Jn (GDA) → Indore Jn (INDB)
     - **12188 JBP GARIB RATH**: Khandwa (KNW) → Jabalpur (JBP)
   - Tapping any ticket card re-opens the full Digital Ticket view with its live QR code.

5. **Complete Super App Offering Suite**:
   - **Journey Planner**: Reserved, Unreserved, Platform ticket booking.
   - **PNR Enquiry**: Realistic lookup returning confirmation status (`CNF / B3 / 32 / MB`) and chart preparation state.
   - **Live Train Status**: Real-time delay and station halt progression.
   - **Coach Position**: Rake composition (Engine to Guard with coach B3 highlighted).
   - **Food on Track (IRCTC)**: Seat delivery food menu.
   - **Rail Madad**: Grievance redressal token generation.
   - **My Profile & Rail Wallet**: User profile, wallet balance (₹450.00), biometric security toggle.

---

## 🏗 Tech Stack & Architecture

- **Language**: Kotlin 2.0.21
- **UI Toolkit**: Jetpack Compose + Material 3 (BOM 2024.10.00)
- **Architecture**: Clean MVVM (Model-View-ViewModel) + StateFlow
- **Navigation**: Navigation Compose 2.8.3
- **Data Persistence**: Local Storage via `TicketRepository` with Gson JSON serialization
- **QR Code Generation**: ZXing Core 3.5.3 + Custom Compose Canvas renderer
- **Target SDK**: Android 15 (compileSdk 35, minSdk 26)

---

## 📁 Project Structure

```
RailOne/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/indianrailways/railone/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Station.kt
│   │   │   │   │   │   └── TicketModels.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── StationRepository.kt (40+ major Indian stations)
│   │   │   │   │       ├── FareCalculator.kt (Authentic distance & slab logic)
│   │   │   │   │       ├── TicketRepository.kt (Persistent store & Flow)
│   │   │   │   │       └── MockRailwayService.kt (PNR & Live Train Status)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── RailOneApp.kt (Root Scaffold & NavHost)
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── BottomNavBar.kt (Signature blue bottom nav)
│   │   │   │   │   │   ├── DashedDivider.kt (Ticket divider lines)
│   │   │   │   │   │   ├── HomeVisuals.kt (Custom train illustrations)
│   │   │   │   │   │   ├── QrCodeView.kt (ZXing Compose canvas renderer)
│   │   │   │   │   │   └── TicketShape.kt (Custom notched ticket card shape)
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   └── Screen.kt
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── UnreservedBookingScreen.kt
│   │   │   │   │   │   ├── DigitalTicketScreen.kt
│   │   │   │   │   │   ├── MyBookingsScreen.kt
│   │   │   │   │   │   ├── PlatformBookingScreen.kt
│   │   │   │   │   │   ├── PnrStatusScreen.kt
│   │   │   │   │   │   ├── LiveTrainStatusScreen.kt
│   │   │   │   │   │   └── SecondaryScreens.kt (Coach, Food, Madad, Profile)
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Type.kt
│   │   │   │   │       └── Theme.kt
│   │   │   └── res/
│   │   │       ├── drawable/ (Adaptive icons & vectors)
│   │   │       ├── mipmap-anydpi-v26/ (ic_launcher.xml)
│   │   │       └── values/ (colors.xml, strings.xml)
│   │   └── test/
│   │       └── java/com/indianrailways/railone/
│   │           └── RailOneUnitTest.kt
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   ├── libs.versions.toml (Version catalog)
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew.bat
```

---

## 🚀 How to Run for Demo Presentation

1. **Open in Android Studio**:
   - Open **Android Studio** (Koala / Ladybug / Iguana or newer).
   - Select **File → Open...** and navigate to `k:\Personal\RailOne`.
   - Android Studio will automatically sync the Gradle project and index dependencies.

2. **Select Device / Emulator**:
   - Choose any physical Android phone or Android Emulator (API 26 to API 35).
   - Press **Run** (`Shift + F10` or the green play icon).

3. **Step-by-step Demo Walkthrough for Evaluators**:
   1. **Home Screen**:
      - Point out the official RailOne branding, language chip (`A/अ`), notification count badge (`2`), and user greeting (`"Hi, Kedar Mitesh Vyas!"`).
      - Show the **Journey Planner** cards and the **More Offerings** 4x2 grid.
      - Show the **Upcoming Journey** purple gradient preview card.
   2. **Tap "Unreserved"**:
      - Notice the **Paperless / Outside Station** toggle.
      - Tap the station picker: search for any station (e.g. `Ahmedabad (ADI)`, `Surat (ST)`, `Vadodara (BRC)`).
      - Tap the **Swap** circular button (`⇅`) to reverse the route.
      - Adjust passengers (Adults/Children) or change train type (Ordinary vs Mail/Express vs Superfast) — show that the **fare dynamically updates**.
   3. **Tap "Proceed To Book" (Payment Bypass)**:
      - The app displays a 1-second `"Generating Ticket..."` dialog connecting to the UTS server.
      - It bypasses payment gateways completely for smooth, instantaneous demo presentation.
   4. **Digital Ticket Screen**:
      - Show the **black Indian Railways card** with the **dynamic countdown clock** (`03:46`) live-decrementing every second!
      - Show the **real scannable QR code** (can even be scanned with a smartphone camera).
      - Highlight the route, distance, UTS alphanumeric ID (`X509EIA0BF`), and statutory railway warnings.
   5. **Tap "Go to My Bookings"**:
      - The newly booked unreserved ticket appears **instantly at the top** of the list with a green `● ACTIVE` badge.
      - Point out the authentic **side ticket cutout notches** and dashed perforations.
      - Tap the ticket to demonstrate reopening the digital ticket at any time.
   6. **Explore Secondary Features**:
      - Test **PNR Enquiry** (enter 10-digit PNR to see coach `B3` berth `32`).
      - Test **Platform Ticket** (instant issuance with 2-hour validity).
      - Test **Track Your Train** (live station timetable).
      - Switch to **You** profile tab to show the Rail Wallet and offline storage toggles.
