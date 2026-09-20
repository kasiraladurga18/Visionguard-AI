# VisionGuard AI — System Architecture

## 1. System Overview

VisionGuard AI is a comprehensive, production-quality assistive vision application designed for blind and visually impaired individuals. It bridges on-device sensors (CameraX, SpeechRecognizer, FusedLocationProviderClient, TextToSpeech, Vibrator) with a dedicated Python FastAPI backend and Google Gemini Vision intelligence.

```
+-------------------------------------------------------------+
|                  Android Native Client                      |
|  +-------------------+  +----------------+  +------------+  |
|  |   CameraX Engine  |  | Speech & TTS   |  | Location   |  |
|  +-------------------+  +----------------+  +------------+  |
|  +-------------------------------------------------------+  |
|  |     Jetpack Compose UI (High-Contrast M3 Layouts)     |  |
|  +-------------------------------------------------------+  |
|  +-------------------------------------------------------+  |
|  |     VisionGuardViewModel & Room Database Cache        |  |
|  +-------------------------------------------------------+  |
|  +-------------------------------------------------------+  |
|  |             Retrofit & OkHttp API Layer               |  |
|  +-------------------------------------------------------+  |
+------------------------------|------------------------------+
                               | HTTPS / JSON
                               v
+-------------------------------------------------------------+
|               VisionGuard Python FastAPI Backend            |
|  +------------------+  +-----------------+  +------------+  |
|  | Vision & OCR Svc |  | Assistant Svc   |  | SOS Engine |  |
|  +------------------+  +-----------------+  +------------+  |
|  +-------------------------------------------------------+  |
|  |      Gemini Multimodal Prompt Engineering Pipeline    |  |
|  +-------------------------------------------------------+  |
+--------------|-------------------------------|--------------+
               |                               |
               v                               v
+-----------------------------+ +-----------------------------+
|      Google Gemini API      | |     Supabase PostgreSQL     |
| (Image Understanding, OCR)  | |  (Profiles, Contacts, RLS)  |
+-----------------------------+ +-----------------------------+
```

## 2. Key Modules

### A. Android Client
1. **Camera Assist Module**: Captures and processes image frames via CameraX, optimizes compression, sends to vision API, and vocalizes spatially oriented descriptions.
2. **Read Text (OCR) Module**: Specialized OCR text extractor with "Read All" and "Read Summary" options.
3. **Voice-First Navigation**: Android SpeechRecognizer listens to triggers like *"what is in front of me"*, *"read this"*, *"where am I"*, *"emergency"*, and reads back responses using Android TextToSpeech.
4. **Emergency / SOS Module**: High-priority accessible card with tactile vibration, immediate contact calling, and GPS coordinate dispatch.
5. **Location Module**: Fused location with geocoder reverse-lookup for landmark awareness.
6. **Local Persistence**: Room database caches vision history, emergency contacts, and accessibility preferences for offline resilience.

### B. FastAPI Backend
1. **Vision Service**: Validates images, structures prompts to force concise spatial descriptions and obstacle lists.
2. **OCR Service**: Organizes extracted text into summary and sections.
3. **Assistant Service**: Context-aware natural conversational assistant for blind users.
4. **Emergency Service**: Handles SOS alerts and persists dispatch logs.

### C. Supabase & Database
1. PostgreSQL with Row Level Security (RLS) guaranteeing data privacy.
2. User profiles, preferences, contacts, vision history, and SOS event logs.
