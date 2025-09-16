# Vehicle Companion

## 🔧 Continuous Integration (CI)

This project uses **GitHub Actions** to automatically build and test the app on every push and pull request to `master`.

**Workflow:** `.github/workflows/android_ci.yml`

**Steps:**

1. Checkout the code.
2. Setup JDK 17.
3. Run unit tests for all modules:

   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Upload test reports as `unit-test-results.zip`.

### Status & Reports

* CI status badge shown at the top of this README.
* Detailed test reports are available in the workflow run summary under the **Actions** tab.

### Test Scope

* **Unit Tests:** Fully executed on CI.
* **UI Tests:** Hilt UI tests are scaffolded but currently disabled on CI to reduce build time.

---

## 🏃 Running the App

Clone the repository:

```bash
git clone https://github.com/<username>/vehicle-companion.git
cd vehicle-companion
```

1. Open the project in **Android Studio**.
2. Add your Mapbox API key in `local.properties`:

```properties
MAPS_API_KEY=YOUR_API_KEY_HERE
```

3. Build and run the app on a device or emulator.

---

## 🏗 Architecture

* **Pattern:** MVVM + Repository Pattern with Hilt for DI
* **Modularization:** `feature_garage`, `feature_places`, `core_data`, `core_domain`
* **Persistence:** Room for local data (vehicles & POIs)
* **Networking:** Retrofit + OkHttp
* **UI:** Jetpack Compose with reusable Composables

---

## 🧪 Tests & CI

* **Unit Tests:** `VehicleDataClassTest`, `PlaceServiceTest`
* Run locally:

```bash
./gradlew testDebugUnitTest
```

* **CI:** GitHub Actions builds the app and runs unit tests on every push/PR to `master`. Test reports are generated as `unit-test-results.zip`.
* **UI Tests:** Scaffolded with Hilt but currently disabled on CI to reduce build time.

---

## ⚠ Error / Empty States

* **Garage:** Shows “Your garage is empty” with a CTA button.
* **Places:** Shows “No places loaded” with a refresh button.
* Validation prevents invalid vehicle data submission.
* Favorites are persisted in Room.

---

## 🚀 Next Steps
* Introduce `BaseView` and `BaseViewModel`:
  - `BaseView` to standardize UI behavior across screens
  - `BaseViewModel` to centralize state, events, and error handling
* Map-based POI exploration with interactive pins.
* Add CI UI test execution.
* POI filtering by category & rating.
* Image caching for POIs.
