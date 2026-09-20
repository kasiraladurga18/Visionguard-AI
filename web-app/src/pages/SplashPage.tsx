import React, { useEffect } from 'react';
import { Eye, Shield, Sparkles } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface SplashPageProps {
  onSplashFinished: (hasSession: boolean) => void;
}

export const SplashPage: React.FC<SplashPageProps> = ({ onSplashFinished }) => {
  const { session, loading } = useAuth();

  useEffect(() => {
    if (!loading) {
      const timer = setTimeout(() => {
        onSplashFinished(!!session);
      }, 1600);
      return () => clearTimeout(timer);
    }
  }, [loading, session]);

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '2rem',
        textAlign: 'center',
        position: 'relative',
        zIndex: 10
      }}
    >
      <div
        style={{
          width: '96px',
          height: '96px',
          borderRadius: '2rem',
          background: 'linear-gradient(135deg, rgba(0, 242, 254, 0.25), rgba(139, 92, 246, 0.25))',
          border: '1px solid var(--border-glass-glow)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          marginBottom: '1.5rem',
          boxShadow: '0 0 50px rgba(0, 242, 254, 0.4)'
        }}
      >
        <Eye size={48} style={{ color: 'var(--secondary-accent)' }} />
      </div>

      <h1
        style={{
          fontSize: '2.5rem',
          fontWeight: 800,
          letterSpacing: '-0.03em',
          background: 'linear-gradient(135deg, #ffffff 0%, var(--secondary-accent) 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          marginBottom: '0.5rem'
        }}
      >
        VISIONGUARD AI
      </h1>

      <p
        style={{
          fontSize: '1.1rem',
          color: 'var(--text-secondary)',
          letterSpacing: '0.12em',
          textTransform: 'uppercase',
          fontWeight: 600,
          marginBottom: '2rem'
        }}
      >
        See More. Live Freely.
      </p>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', color: 'var(--text-muted)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.85rem' }}>
          <Shield size={16} style={{ color: 'var(--status-success)' }} />
          <span>Secure Auth</span>
        </div>
        <span>•</span>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.85rem' }}>
          <Sparkles size={16} style={{ color: 'var(--purple-highlight)' }} />
          <span>AI Vision Engine</span>
        </div>
      </div>
    </div>
  );
};
