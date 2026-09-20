# VisionGuard AI — Setup & Installation Guide

## 1. Prerequisites
- Android Studio Ladybug or newer
- Android SDK 34+ (compileSdk 36)
- Python 3.11+
- Supabase Account & PostgreSQL Instance
- Google Gemini API Key

## 2. Backend Setup
1. Navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Create and activate a virtual environment:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Configure environment:
   Copy `.env.example` to `.env` and fill in:
   ```env
   GEMINI_API_KEY=your_gemini_api_key
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=your_anon_key
   ```
5. Start FastAPI server:
   ```bash
   uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
   ```

## 3. Supabase Database Migration
1. Go to your Supabase Project Dashboard -> SQL Editor.
2. Run the migration file:
   `supabase/migrations/20260918000001_visionguard_schema.sql`
3. This creates all tables, indexes, triggers, and Row Level Security policies.

## 4. Android Application Setup
1. Open the project in Android Studio.
2. Ensure `.env` is configured (Secrets plugin will read it at compile time).
3. Connect an Android device or launch an emulator.
4. Run:
   ```bash
   gradle assembleDebug
   ```
5. Note: When running on an Android emulator connecting to `localhost`, use `http://10.0.2.2:8000`. You can configure the Backend Server URL in VisionGuard's Settings screen directly!
