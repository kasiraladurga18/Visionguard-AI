# VISIONGUARD AI — "See More. Live Freely."

A production-quality, fully functional assistive vision full-stack Android application engineered for blind and visually impaired users.

---

## Highlights

- **AI Camera Assistant**: Live camera scene understanding powered by Gemini API, translating scenes into spatial audio descriptions (e.g. *"A chair is approximately two meters ahead"*).
- **OCR / Reading Mode**: Crisp text reading for signs, documents, medicine labels, with "Read All" and "Read Summary".
- **Voice-First Navigation**: Hands-free speech recognition for voice commands (*"What do you see?"*, *"Read this"*, *"Where am I?"*, *"Stop speaking"*).
- **Android Text-to-Speech**: Integrated native TTS with adjustable speech rate, pitch, and repetition.
- **Emergency & SOS**: One-touch accessible emergency calling and location dispatch with a 3-second tactile vibration countdown.
- **Location Assistance**: GPS-powered landmark and address vocalization using Android Geocoder.
- **Local Persistence & Privacy**: Room SQLite database with full offline fallback and zero persistent image hoarding.
- **Production FastAPI Backend**: REST API with Gemini multimodal integration, request validation, and comprehensive error handling.
- **Supabase Cloud Schema**: PostgreSQL database with Row Level Security (RLS) policies.

---

## Tech Stack

- **Frontend**: Kotlin, Jetpack Compose, Material 3, CameraX, Room, Navigation Compose, Coroutines, StateFlow, Retrofit, Android TextToSpeech, SpeechRecognizer.
- **Backend**: Python 3.11+, FastAPI, Pydantic, Uvicorn, HTTPX.
- **AI Intelligence**: Google Gemini API (Multimodal 2.5 Flash / 1.5 Flash).
- **Database & Auth**: PostgreSQL (Supabase) + Android Room DB (Local Cache).

---

## Directory Structure

```
.
├── app/                  # Android Studio Jetpack Compose application
│   ├── src/main/java/com/example/
│   │   ├── accessibility/# TextToSpeech, SpeechRecognizer, Haptics
│   │   ├── camera/       # CameraX integration & frame compression
│   │   ├── data/         # Room Database, Retrofit API, Repositories
│   │   ├── location/     # Location Provider & Geocoder
│   │   ├── ui/           # Compose Screens, Themes, High-Contrast UI
│   │   └── viewmodel/    # StateFlow MVVM Architecture
├── backend/              # Python FastAPI backend service
│   ├── app/              # FastAPI application, Routers, Services
│   └── tests/            # Pytest test suite
├── docs/                 # Architectural, API, Database & Setup guides
├── supabase/             # PostgreSQL database migrations & RLS policies
├── web-app/              # React TypeScript Web Application (Shared Auth & Backend)
└── README.md
```

See [docs/setup.md](docs/setup.md) for detailed installation instructions.
