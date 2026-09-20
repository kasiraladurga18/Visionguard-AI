import { createClient } from '@supabase/supabase-js';

const SUPABASE_URL =
  import.meta.env.VITE_SUPABASE_URL || 'https://hefwtrdgpsyxueatpbfz.supabase.co';
const SUPABASE_KEY =
  import.meta.env.VITE_SUPABASE_PUBLISHABLE_KEY ||
  import.meta.env.VITE_SUPABASE_ANON_KEY ||
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhlZnd0cmRncHN5eHVlYXRwYmZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODg2ODYzNjgsImV4cCI6MjEwNDI2MjM2OH0.sMpKsD4LGBq0yrVAYTc9Hl_44nutyQKc9rL73cAiQqw';

export const supabase = createClient(SUPABASE_URL, SUPABASE_KEY, {
  auth: {
    persistSession: true,
    autoRefreshToken: true,
    detectSessionInUrl: true
  }
});
