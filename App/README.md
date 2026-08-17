# Run locally

This app uses bundled diagnostic data, so you can open and run it in Android Studio without any external setup.

## Run Locally

**Prerequisites:**

1. Open Android Studio.
2. Select **Open** and choose this project directory.
3. Let Android Studio finish syncing the Gradle project.
4. Run the app on an emulator or a physical device.

## What works locally

- Diagnostic centers are seeded into the local Room database on first launch.
- The advisor uses local fallback logic, so no API key is required.
- Internet access is optional and only affects remote sync or location search.
