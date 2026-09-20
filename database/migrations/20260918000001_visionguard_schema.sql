-- ==========================================================
-- VISIONGUARD AI DATABASE SCHEMA (PostgreSQL / Supabase)
-- Final-Year Engineering Project
-- ==========================================================

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. PROFILES TABLE
-- Extends Supabase auth.users with assistive accessibility preferences
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT,
    speech_rate REAL DEFAULT 1.0 CHECK (speech_rate >= 0.5 AND speech_rate <= 2.5),
    response_length TEXT DEFAULT 'concise' CHECK (response_length IN ('concise', 'detailed', 'essential')),
    high_contrast BOOLEAN DEFAULT TRUE,
    face_opt_in BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 2. USER PREFERENCES TABLE
CREATE TABLE IF NOT EXISTS public.user_preferences (
    user_id UUID PRIMARY KEY REFERENCES public.profiles(id) ON DELETE CASCADE,
    auto_speak BOOLEAN DEFAULT TRUE,
    tts_pitch REAL DEFAULT 1.0,
    preferred_language TEXT DEFAULT 'en-US',
    obstacle_alerts BOOLEAN DEFAULT TRUE,
    vibration_feedback BOOLEAN DEFAULT TRUE,
    sos_auto_send BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 3. EMERGENCY CONTACTS TABLE
CREATE TABLE IF NOT EXISTS public.emergency_contacts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    phone_number TEXT NOT NULL,
    relationship TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 4. VISION HISTORY TABLE
-- Stores past scene queries, OCR transcripts, AI answers (no raw images retained for privacy)
CREATE TABLE IF NOT EXISTS public.vision_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    task_type TEXT NOT NULL CHECK (task_type IN ('scene_describe', 'ocr_read', 'ask_ai', 'face_match', 'obstacle_scan')),
    prompt TEXT,
    result_text TEXT NOT NULL,
    object_tags TEXT[] DEFAULT '{}',
    distance_estimate TEXT,
    confidence REAL DEFAULT 1.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 5. KNOWN PEOPLE TABLE (Privacy-Opt-in Face Identification)
CREATE TABLE IF NOT EXISTS public.known_people (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    relationship TEXT,
    notes TEXT,
    feature_summary TEXT, -- Privacy-safe descriptor, no raw biometric video
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 6. SOS EVENTS TABLE
CREATE TABLE IF NOT EXISTS public.sos_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    address TEXT,
    status TEXT DEFAULT 'triggered' CHECK (status IN ('triggered', 'acknowledged', 'resolved', 'cancelled')),
    triggered_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- INDEXES for fast retrieval
CREATE INDEX IF NOT EXISTS idx_emergency_contacts_user ON public.emergency_contacts(user_id);
CREATE INDEX IF NOT EXISTS idx_vision_history_user ON public.vision_history(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_known_people_user ON public.known_people(user_id);
CREATE INDEX IF NOT EXISTS idx_sos_events_user ON public.sos_events(user_id, triggered_at DESC);

-- ==========================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- Strict Isolation: Each user can only access their own data
-- ==========================================================

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_preferences ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.emergency_contacts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vision_history ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.known_people ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.sos_events ENABLE ROW LEVEL SECURITY;

-- Profiles Policies
CREATE POLICY "Users can view their own profile" 
    ON public.profiles FOR SELECT 
    USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile" 
    ON public.profiles FOR UPDATE 
    USING (auth.uid() = id);

-- User Preferences Policies
CREATE POLICY "Users can manage their own preferences" 
    ON public.user_preferences FOR ALL 
    USING (auth.uid() = user_id);

-- Emergency Contacts Policies
CREATE POLICY "Users can manage their own emergency contacts" 
    ON public.emergency_contacts FOR ALL 
    USING (auth.uid() = user_id);

-- Vision History Policies
CREATE POLICY "Users can manage their own vision history" 
    ON public.vision_history FOR ALL 
    USING (auth.uid() = user_id);

-- Known People Policies
CREATE POLICY "Users can manage their known people" 
    ON public.known_people FOR ALL 
    USING (auth.uid() = user_id);

-- SOS Events Policies
CREATE POLICY "Users can view and create their own SOS events" 
    ON public.sos_events FOR ALL 
    USING (auth.uid() = user_id);

-- ==========================================================
-- AUTOMATIC PROFILE CREATION TRIGGER ON AUTH SIGNUP
-- ==========================================================

CREATE OR REPLACE FUNCTION public.handle_new_user() 
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, email, full_name)
    VALUES (new.id, new.email, COALESCE(new.raw_user_meta_data->>'full_name', 'VisionGuard User'));
    
    INSERT INTO public.user_preferences (user_id)
    VALUES (new.id);
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();
