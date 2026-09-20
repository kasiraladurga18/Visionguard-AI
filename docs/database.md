# VisionGuard AI — Database Schema & Entity Relationships

The database is built on PostgreSQL with Supabase, leveraging Row Level Security (RLS) to enforce strict privacy for visually impaired users.

## Tables

### 1. `profiles`
- `id` (UUID, Primary Key, references `auth.users`)
- `email` (TEXT, Unique, Not Null)
- `full_name` (TEXT)
- `speech_rate` (REAL, default 1.0)
- `response_length` (TEXT: 'concise', 'detailed', 'essential')
- `high_contrast` (BOOLEAN, default TRUE)
- `face_opt_in` (BOOLEAN, default FALSE)
- `created_at` / `updated_at` (TIMESTAMP WITH TIME ZONE)

### 2. `user_preferences`
- `user_id` (UUID, Primary Key, references `profiles.id`)
- `auto_speak` (BOOLEAN, default TRUE)
- `tts_pitch` (REAL, default 1.0)
- `preferred_language` (TEXT, default 'en-US')
- `obstacle_alerts` (BOOLEAN, default TRUE)
- `vibration_feedback` (BOOLEAN, default TRUE)
- `sos_auto_send` (BOOLEAN, default FALSE)

### 3. `emergency_contacts`
- `id` (UUID, Primary Key)
- `user_id` (UUID, references `profiles.id`)
- `name` (TEXT)
- `phone_number` (TEXT)
- `relationship` (TEXT)
- `is_primary` (BOOLEAN)
- `created_at` (TIMESTAMP WITH TIME ZONE)

### 4. `vision_history`
- `id` (UUID, Primary Key)
- `user_id` (UUID, references `profiles.id`)
- `task_type` (TEXT: 'scene_describe', 'ocr_read', 'ask_ai', 'face_match', 'obstacle_scan')
- `prompt` (TEXT)
- `result_text` (TEXT)
- `object_tags` (TEXT[])
- `distance_estimate` (TEXT)
- `confidence` (REAL)
- `created_at` (TIMESTAMP WITH TIME ZONE)

### 5. `known_people`
- `id` (UUID, Primary Key)
- `user_id` (UUID, references `profiles.id`)
- `name` (TEXT)
- `relationship` (TEXT)
- `notes` (TEXT)
- `feature_summary` (TEXT)
- `created_at` (TIMESTAMP WITH TIME ZONE)

### 6. `sos_events`
- `id` (UUID, Primary Key)
- `user_id` (UUID, references `profiles.id`)
- `latitude` (DOUBLE PRECISION)
- `longitude` (DOUBLE PRECISION)
- `address` (TEXT)
- `status` (TEXT: 'triggered', 'acknowledged', 'resolved')
- `triggered_at` (TIMESTAMP WITH TIME ZONE)
