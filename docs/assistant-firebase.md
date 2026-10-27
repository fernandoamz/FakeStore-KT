# FakeStore Assistant (Firebase Functions + OpenAI)

## What this does
The Android **IA** tab calls a Firebase HTTPS function (`assistantChat`).
The function keeps the OpenAI API key in Secret Manager and returns a Spanish shopping reply.

## 1. Firebase project setup
1. Create/select a Firebase project in [Firebase Console](https://console.firebase.google.com/).
2. Install CLI: `npm install -g firebase-tools`
3. Login: `firebase login`
4. Edit `.firebaserc` and set `default` to your project id.
5. From repo root:
   ```bash
   cd functions
   npm install
   cd ..
   firebase functions:secrets:set OPENAI_API_KEY
   firebase deploy --only functions
   ```
6. Copy the **Cloud Run** URL from deploy output, e.g.
   `https://assistantchat-xxxxx-uc.a.run.app`
7. Gen2 functions need public invoker (`invoker: "public"` in `functions/index.js`).
   Without that, unauthenticated calls return **403**.

## 2. Android configuration
In `local.properties` (not committed):
```properties
ASSISTANT_BASE_URL=https://assistantchat-xxxxx-uc.a.run.app/
```
Note the trailing slash. The app posts to the function root (`.` / `/`).

Rebuild/reinstall the app after changing `local.properties`.

## 3. Store API keys locally (`.env` / `.secret.local`)

Firebase Functions v2 keeps production secrets in **Secret Manager** (`defineSecret`).
For local/emulator keys, use files under `functions/` (already gitignored).

### Option A — recommended for this project (emulator + `defineSecret`)
1. Copy the example:
   ```bash
   cd functions
   copy .secret.local.example .secret.local
   ```
2. Edit `functions/.secret.local`:
   ```env
   OPENAI_API_KEY=sk-your-real-key-here
   ```
3. Start emulator:
   ```bash
   firebase emulators:start --only functions
   ```

### Option B — regular `.env` (non-secret config)
1. Copy:
   ```bash
   cd functions
   copy .env.example .env
   ```
2. Put non-secret values there if needed later.
3. **Do not put production OpenAI keys only in `.env` and deploy** — use Secret Manager for deploy:
   ```bash
   firebase functions:secrets:set OPENAI_API_KEY
   ```

### Android side
The OpenAI key never goes in the Android app.
Only the function URL goes in `local.properties`:
```properties
ASSISTANT_BASE_URL=https://assistantchat-xxxxx-uc.a.run.app/
```

## 4. Local emulator URL for Android
```bash
firebase emulators:start --only functions
```
Use in `local.properties`:
```properties
ASSISTANT_BASE_URL=http://10.0.2.2:5001/<PROJECT_ID>/us-central1/assistantChat/
```
(Android emulator → host machine; trailing slash so Retrofit `POST .` hits the function)

## 5. Request / response contract
`POST {ASSISTANT_BASE_URL}` (function root)
```json
{
  "message": "Busco auriculares baratos",
  "history": [
    { "role": "user", "content": "Hola" },
    { "role": "assistant", "content": "¿Qué buscas hoy?" }
  ],
  "catalogContext": "id=1 title=iPhone price=999; ..."
}
```
Response:
```json
{ "reply": "Te recomiendo..." }
```
