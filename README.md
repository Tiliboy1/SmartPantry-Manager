# Smart Pantry Manager

A Java Android application that helps users reduce food waste by tracking pantry ingredients and suggesting recipes that can be made **strictly** from what is already available — no shopping required.

## Features

- **Pantry Management** – Add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- **Strict Recipe Matching** – Only shows recipes where *every* required ingredient is present in sufficient quantity
- **Recipe Collection** – 18 pre-loaded recipes with ingredients and preparation steps
- **Colour-coded Expiry** – Green (fresh), orange (expiring within 3 days), red (expired)
- **Settings** – Toggle for expiring-soon alerts
- **Modern UI** – Material Design with floating bottom navigation, dark mode support, and adaptive launcher icon

## Database Choice

**SQLite via Room Persistence Library**

I chose Room because:
- It works fully offline / on-device (no internet required)
- Provides compile-time verification of SQL queries
- Data persists after the app is closed or the device is restarted
- Matches the persistent data approach covered in the module

## Screens

1. **Pantry List** – Grid of current ingredients
2. **Add / Edit Ingredient** – Form with validation
3. **Suggested Recipes** – Only recipes the user can make right now
4. **Recipe Detail** – Full ingredient list + numbered method
5. **Settings** – Expiry alert preference

## Strict-Matching Rule

A recipe is only suggested if **every single ingredient** it requires is present in the pantry in at least the required quantity. Partial matches are excluded. The matcher also handles simple plural forms (tomato / tomatoes) and basic unit conversion (g/kg, ml/l, etc.).

## How to Run

1. Clone this repository
2. Open the project in **Android Studio**
3. Let Gradle sync and download dependencies
4. Run on an emulator or physical device (API 24+)

```bash
git clone <your-repo-url>
```

## Tech Stack

- Language: **Java**
- Min SDK: 24
- Database: Room (SQLite)
- UI: Material Components, RecyclerView, Bottom Navigation

## Author

**Natilio Moerat**
Mobile App Development 700 – Practical Assignment
