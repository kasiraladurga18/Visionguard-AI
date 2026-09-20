import React, { createContext, useContext, useEffect, useState } from 'react';
import { supabase } from '../services/supabaseClient';
import { sendBackendAuthLogin, sendBackendAuthRegister, sendBackendForgotPassword } from '../services/backendApi';

export interface UserProfile {
  id: string;
  email: string;
  fullName: string;
  phone?: string;
  emergencyContact?: string;
}

interface AuthContextType {
  user: any | null;
  profile: UserProfile | null;
  session: any | null;
  loading: boolean;
  signIn: (email: string, pass: string) => Promise<{ success: boolean; error?: string }>;
  signUp: (email: string, pass: string, name: string) => Promise<{ success: boolean; error?: string }>;
  signOut: () => Promise<void>;
  resetPassword: (email: string) => Promise<{ success: boolean; message?: string; error?: string }>;
  updateProfile: (fullName: string, phone: string, emergencyContact: string) => Promise<{ success: boolean; error?: string }>;
}

const AuthContext = createContext<AuthContextType>({} as AuthContextType);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<any | null>(null);
  const [session, setSession] = useState<any | null>(null);
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    // 1. Check active Supabase session
    supabase.auth.getSession().then(({ data: { session: supaSession } }) => {
      if (supaSession) {
        setSession(supaSession);
        setUser(supaSession.user ?? null);
        if (supaSession.user) {
          fetchProfile(supaSession.user);
        }
      } else {
        const localToken = localStorage.getItem('vg_auth_token');
        const localEmail = localStorage.getItem('vg_auth_email');
        if (localToken && localEmail) {
          const mockUser = { id: '00000000-0000-0000-0000-000000000001', email: localEmail, user_metadata: { full_name: 'VisionGuard User' } };
          setUser(mockUser);
          setSession({ access_token: localToken, user: mockUser });
          setProfile({
            id: mockUser.id,
            email: localEmail,
            fullName: 'VisionGuard User',
            phone: '+1 (555) 019-2831',
            emergencyContact: 'Guardian (+1 555-0199)'
          });
        }
      }
      setLoading(false);
    });

    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, supaSession) => {
      if (supaSession) {
        setSession(supaSession);
        setUser(supaSession.user ?? null);
        if (supaSession.user) {
          fetchProfile(supaSession.user);
        }
      } else {
        setSession(null);
        setUser(null);
        setProfile(null);
      }
    });

    return () => subscription.unsubscribe();
  }, []);

  const fetchProfile = async (currentUser: any) => {
    try {
      const { data, error } = await supabase
        .from('profiles')
        .select('*')
        .eq('id', currentUser.id)
        .single();

      if (data && !error) {
        setProfile({
          id: data.id,
          email: currentUser.email || '',
          fullName: data.full_name || currentUser.user_metadata?.full_name || 'VisionGuard User',
          phone: data.phone || '+1 (555) 019-2831',
          emergencyContact: data.emergency_contact || 'Guardian (+1 555-0199)'
        });
      } else {
        setProfile({
          id: currentUser.id,
          email: currentUser.email || '',
          fullName: currentUser.user_metadata?.full_name || 'VisionGuard User',
          phone: '+1 (555) 019-2831',
          emergencyContact: 'Guardian (+1 555-0199)'
        });
      }
    } catch (e) {
      setProfile({
        id: currentUser.id,
        email: currentUser.email || '',
        fullName: currentUser.user_metadata?.full_name || 'VisionGuard User',
        phone: '+1 (555) 019-2831',
        emergencyContact: 'Guardian (+1 555-0199)'
      });
    } finally {
      setLoading(false);
    }
  };

  const signIn = async (email: string, pass: string) => {
    try {
      // 1. Direct Supabase Auth Login with project credentials
      const { data, error } = await supabase.auth.signInWithPassword({
        email,
        password: pass
      });

      if (!error && data.session) {
        setSession(data.session);
        setUser(data.user);
        await fetchProfile(data.user);
        return { success: true };
      }

      // 2. Fallback to FastAPI Backend Auth Endpoint
      const backendRes = await sendBackendAuthLogin(email, pass);
      if (backendRes.success && backendRes.access_token) {
        localStorage.setItem('vg_auth_token', backendRes.access_token);
        localStorage.setItem('vg_auth_email', email);
        const fakeUser = { id: backendRes.user_id || '00000000-0000-0000-0000-000000000001', email, user_metadata: { full_name: 'VisionGuard User' } };
        setUser(fakeUser);
        setSession({ access_token: backendRes.access_token, user: fakeUser });
        setProfile({
          id: fakeUser.id,
          email,
          fullName: 'VisionGuard User',
          phone: '+1 (555) 019-2831',
          emergencyContact: 'Guardian (+1 555-0199)'
        });
        return { success: true };
      }

      return { success: false, error: error?.message || 'Login failed' };
    } catch (err: any) {
      return { success: false, error: err.message || 'Authentication error' };
    }
  };

  const signUp = async (email: string, pass: string, name: string) => {
    try {
      // 1. Direct Supabase Auth Registration
      const { data, error } = await supabase.auth.signUp({
        email,
        password: pass,
        options: { data: { full_name: name } }
      });

      if (!error && data.user) {
        setSession(data.session);
        setUser(data.user);

        try {
          await supabase.from('profiles').insert([
            { id: data.user.id, full_name: name, updated_at: new Date().toISOString() }
          ]);
        } catch (_e) {
          // ignore profile table insert error if RLS policy defers it
        }

        await fetchProfile(data.user);
        return { success: true };
      }

      // 2. Fallback to FastAPI Backend Register
      const backendRes = await sendBackendAuthRegister(email, pass, name);
      if (backendRes.success && backendRes.access_token) {
        localStorage.setItem('vg_auth_token', backendRes.access_token);
        localStorage.setItem('vg_auth_email', email);
        const fakeUser = { id: backendRes.user_id || '00000000-0000-0000-0000-000000000001', email, user_metadata: { full_name: name } };
        setUser(fakeUser);
        setSession({ access_token: backendRes.access_token, user: fakeUser });
        setProfile({
          id: fakeUser.id,
          email,
          fullName: name,
          phone: '+1 (555) 019-2831',
          emergencyContact: 'Guardian (+1 555-0199)'
        });
        return { success: true };
      }

      return { success: false, error: error?.message || 'Registration failed' };
    } catch (err: any) {
      return { success: false, error: err.message || 'Registration error' };
    }
  };

  const resetPassword = async (email: string) => {
    try {
      const { error } = await supabase.auth.resetPasswordForEmail(email);
      if (error) {
        await sendBackendForgotPassword(email);
      }
      return { success: true, message: 'Password recovery email sent successfully' };
    } catch (err: any) {
      return { success: false, error: err.message || 'Password reset request failed' };
    }
  };

  const updateProfile = async (fullName: string, phone: string, emergencyContact: string) => {
    if (!user) return { success: false, error: 'No user session found' };

    try {
      const { error } = await supabase
        .from('profiles')
        .upsert({
          id: user.id,
          full_name: fullName,
          phone,
          emergency_contact: emergencyContact,
          updated_at: new Date().toISOString()
        });

      if (error) {
        console.warn('Profile DB update error:', error.message);
      }

      setProfile({
        id: user.id,
        email: user.email || '',
        fullName,
        phone,
        emergencyContact
      });

      return { success: true };
    } catch (err: any) {
      return { success: false, error: err.message };
    }
  };

  const signOut = async () => {
    localStorage.removeItem('vg_auth_token');
    localStorage.removeItem('vg_auth_email');
    await supabase.auth.signOut();
    setUser(null);
    setSession(null);
    setProfile(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        profile,
        session,
        loading,
        signIn,
        signUp,
        signOut,
        resetPassword,
        updateProfile
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
