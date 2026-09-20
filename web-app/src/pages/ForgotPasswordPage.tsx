import React, { useState } from 'react';
import { Eye, Mail, KeyRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { GlassCard } from '../components/GlassCard';
import { GlassButton } from '../components/GlassButton';

interface ForgotPasswordPageProps {
  onNavigateBackToLogin: () => void;
}

export const ForgotPasswordPage: React.FC<ForgotPasswordPageProps> = ({ onNavigateBackToLogin }) => {
  const { resetPassword } = useAuth();
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email) return;

    setIsSubmitting(true);
    const res = await resetPassword(email);
    setIsSubmitting(false);

    if (res.success) {
      setMessage(res.message || 'Recovery instructions sent.');
    }
  };

  return (
    <div
      style={{
        maxWidth: '440px',
        margin: '0 auto',
        padding: '2.5rem 1rem 3rem 1rem',
        position: 'relative',
        zIndex: 10
      }}
    >
      <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
        <div
          style={{
            width: '64px',
            height: '64px',
            borderRadius: '1.25rem',
            background: 'linear-gradient(135deg, rgba(0,242,254,0.2), rgba(139,92,246,0.2))',
            border: '1px solid var(--border-glass-glow)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1rem auto'
          }}
        >
          <Eye size={32} style={{ color: 'var(--secondary-accent)' }} />
        </div>

        <h2 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#ffffff' }}>Reset Password</h2>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.25rem', fontSize: '0.9rem' }}>
          Enter your registered email to receive a recovery link
        </p>
      </div>

      <GlassCard>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {message && (
            <div
              style={{
                padding: '0.75rem 1rem',
                borderRadius: '0.875rem',
                background: 'rgba(16, 185, 129, 0.15)',
                border: '1px solid var(--status-success)',
                color: 'var(--status-success)',
                fontSize: '0.85rem'
              }}
            >
              {message}
            </div>
          )}

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
              Email Address
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="email"
                className="glass-input"
                style={{ paddingLeft: '2.75rem' }}
                placeholder="user@domain.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
              <Mail size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            </div>
          </div>

          <GlassButton
            type="submit"
            text={isSubmitting ? 'Sending Instructions...' : 'Send Recovery Email'}
            onClick={() => {}}
            icon={KeyRound}
            accentColor="var(--secondary-accent)"
            fullWidth
            disabled={isSubmitting}
          />
        </form>
      </GlassCard>

      <div style={{ textAlign: 'center', marginTop: '1.5rem' }}>
        <button
          onClick={onNavigateBackToLogin}
          style={{
            background: 'none',
            border: 'none',
            color: 'var(--secondary-accent)',
            fontWeight: 700,
            cursor: 'pointer'
          }}
        >
          Back to Sign In
        </button>
      </div>
    </div>
  );
};
