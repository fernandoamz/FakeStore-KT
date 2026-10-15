# Android Continuous Delivery (CD) Documentation

## 1. Pipeline Architecture
The Continuous Delivery (CD) pipeline is orchestrated using **GitHub Actions**. It automates building the Android application, verifying output artifacts, uploading them as GitHub Actions build artifacts, and distributing test builds to QA testers via **Firebase App Distribution**.

---

## 2. Workflow Triggers
The workflow (`.github/workflows/android-cd.yml`) is triggered under the following conditions:
- **Push to `main` branch**: Builds and uploads artifacts to GitHub Actions.
- **Push to Version Tags (`v*`)**: Builds, uploads artifacts, and distributes the build to Firebase App Distribution.
- **Manual Workflow Dispatch (`workflow_dispatch`)**: Allows developers to trigger a build manually and optionally toggle Firebase distribution (`distribute_firebase: true`).

---

## 3. Required Repository Secrets & Variables
To enable Firebase App Distribution, configure the following secrets in your GitHub Repository settings (`Settings > Secrets and variables > Actions`):

1. **`FIREBASE_APP_ID`**: The Firebase App ID for Android (found in Firebase Console > Project Settings > General > Your Apps > App ID, e.g., `1:1234567890:android:abcdef123456`).
2. **`FIREBASE_SERVICE_ACCOUNT`**: The full JSON content of a least-privilege Google Cloud Service Account service key with the **Firebase App Distribution Admin** role.

---

## 4. Firebase Setup Instructions
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Register your Android application with package name `com.example.fakestore`.
3. Enable **App Distribution** in the Firebase console.
4. Create a tester group named `testers` and add your QA team members.
5. Generate a Service Account key in Google Cloud IAM with permissions for Firebase App Distribution and store it as `FIREBASE_SERVICE_ACCOUNT`.

---

## 5. How to Create and Push a Release Tag
To trigger a tagged release and distribute to testers:
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## 6. How to Run a Workflow Manually
1. Go to your GitHub repository > **Actions**.
2. Select **Android CI/CD & Firebase Distribution**.
3. Click **Run workflow**, choose the branch (`main`), toggle `Distribute build to Firebase App Distribution` if desired, and click **Run workflow**.

---

## 7. Artifacts & Tester Distribution
- **GitHub Actions Artifacts**: Generated APKs are stored under the Actions run summary as `app-debug-apk` and retained for 90 days.
- **Firebase App Distribution**: Testers in the `testers` group receive an email notification and can download/install the test build directly on their devices.
