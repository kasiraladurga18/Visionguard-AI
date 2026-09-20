import React, { useState } from 'react';
import { Eye, EyeOff, Lock, Mail, LogIn, ShieldCheck, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { GlassCard } from '../components/GlassCard';
import { GlassButton } from '../components/GlassButton';

interface LoginPageProps {
  onNavigateToRegister: () => void;
  onNavigateToForgotPassword: () => void;
  onLoginSuccess: () => void;
}

export const LoginPage: React.FC<LoginPageProps> = ({
  onNavigateToRegister,
  onNavigateToForgotPassword,
  onLoginSuccess
}) => {
  const { signIn } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);

    if (!email.trim() || !password) {
      setError('Please enter your email address and password.');
      return;
    }

    setIsSubmitting(true);
    const res = await signIn(email.trim(), password);
    setIsSubmitting(false);

    if (res.success) {
      setSuccessMsg('Authentication verified. Welcome back!');
      setTimeout(() => {
        onLoginSuccess();
      }, 600);
    } else {
      setError(res.error || 'Invalid email or password. Please check your credentials.');
    }
  };

  return (
    <div
      style={{
        maxWidth: '460px',
        margin: '0 auto',
        padding: '2.5rem 1rem 3rem 1rem',
        position: 'relative',
        zIndex: 10
      }}
    >
      {/* Brand Header */}
      <div style={{ textAlign: 'center', marginBottom: '1.75rem' }}>
        <div
          style={{
            width: '68px',
            height: '68px',
            borderRadius: '1.5rem',
            background: 'linear-gradient(135deg, rgba(0,242,254,0.25), rgba(139,92,246,0.25))',
            border: '1px solid var(--border-glass-glow)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1rem auto',
            boxShadow: '0 0 40px rgba(0, 242, 254, 0.3)'
          }}
        >
          <Eye size={36} style={{ color: 'var(--secondary-accent)' }} />
        </div>

        <h2
          style={{
            fontSize: '2rem',
            fontWeight: 800,
            background: 'linear-gradient(135deg, #ffffff 0%, var(--secondary-accent) 100%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent'
          }}
        >
          Secure Sign In
        </h2>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.35rem', fontSize: '0.9rem' }}>
          Access your VisionGuard assistive spatial navigation profile
        </p>
      </div>

      {/* Main Auth Card */}
      <GlassCard style={{ padding: '1.75rem', borderColor: 'rgba(0, 242, 254, 0.3)' }}>
        {/* Auth Mode Toggle Selector */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            background: 'rgba(255, 255, 255, 0.05)',
            padding: '0.25rem',
            borderRadius: '1rem',
            marginBottom: '1.5rem',
            border: '1px solid var(--border-glass)'
          }}
        >
          <button
            type="button"
            style={{
              padding: '0.625rem',
              borderRadius: '0.75rem',
              border: 'none',
              background: 'var(--primary-accent)',
              color: '#ffffff',
              fontWeight: 700,
              fontSize: '0.875rem',
              cursor: 'pointer',
              boxShadow: '0 2px 10px rgba(59, 130, 246, 0.4)'
            }}
          >
            Sign In
          </button>
          <button
            type="button"
            onClick={onNavigateToRegister}
            style={{
              padding: '0.625rem',
              borderRadius: '0.75rem',
              border: 'none',
              background: 'transparent',
              color: 'var(--text-secondary)',
              fontWeight: 600,
              fontSize: '0.875rem',
              cursor: 'pointer'
            }}
          >
            Register
          </button>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {error && (
            <div
              style={{
                padding: '0.875rem 1rem',
                borderRadius: '1rem',
                background: 'rgba(239, 68, 68, 0.15)',
                border: '1px solid var(--status-error)',
                color: 'var(--status-error)',
                fontSize: '0.85rem',
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem'
              }}
            >
              <ShieldCheck size={18} />
              <span>{error}</span>
            </div>
          )}

          {successMsg && (
            <div
              style={{
                padding: '0.875rem 1rem',
                borderRadius: '1rem',
                background: 'rgba(16, 185, 129, 0.15)',
                border: '1px solid var(--status-success)',
                color: 'var(--status-success)',
                fontSize: '0.85rem',
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem'
              }}
            >
              <CheckCircle2 size={18} />
              <span>{successMsg}</span>
            </div>
          )}

          <div>
            <label
              style={{
                display: 'block',
                fontSize: '0.85rem',
                fontWeight: 600,
                color: 'var(--text-secondary)',
                marginBottom: '0.5rem'
              }}
            >
              Email Address
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="email"
                className="glass-input"
                style={{ paddingLeft: '2.75rem' }}
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoComplete="email"
              />
              <Mail
                size={18}
                style={{
                  position: 'absolute',
                  left: '1rem',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: 'var(--text-muted)'
                }}
              />
            </div>
          </div>

          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <label style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                Password
              </label>
              <button
                type="button"
                onClick={onNavigateToForgotPassword}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'var(--secondary-accent)',
                  fontSize: '0.8rem',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                Forgot Password?
              </button>
            </div>
            <div style={{ position: 'relative' }}>
              <input
                type={showPassword ? 'text' : 'password'}
                className="glass-input"
                style={{ paddingLeft: '2.75rem', paddingRight: '2.75rem' }}
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                autoComplete="current-password"
              />
              <Lock
                size={18}
                style={{
                  position: 'absolute',
                  left: '1rem',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: 'var(--text-muted)'
                }}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
                style={{
                  position: 'absolute',
                  right: '0.875rem',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  background: 'none',
                  border: 'none',
                  color: 'var(--text-muted)',
                  cursor: 'pointer'
                }}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <GlassButton
            type="submit"
            text={isSubmitting ? 'Authenticating...' : 'Sign In'}
            onClick={() => {}}
            icon={LogIn}
            accentColor="var(--secondary-accent)"
            fullWidth
            disabled={isSubmitting}
          />
        </form>

        {/* Security Assurance Badge */}
        <div
          style={{
            marginTop: '1.5rem',
            paddingTop: '1rem',
            borderTop: '1px solid var(--border-glass)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '0.5rem',
            fontSize: '0.75rem',
            color: 'var(--text-muted)'
          }}
        >
          <ShieldCheck size={16} style={{ color: 'var(--status-success)' }} />
          <span>Encrypted Authentication & Row-Level Privacy</span>
        </div>
      </GlassCard>

      <div style={{ textAlign: 'center', marginTop: '1.5rem' }}>
        <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
          Don't have an account?{' '}
          <button
            onClick={onNavigateToRegister}
            style={{
              background: 'none',
              border: 'none',
              color: 'var(--secondary-accent)',
              fontWeight: 700,
              cursor: 'pointer'
            }}
          >
            Create Account
          </button>
        </p>
      </div>
    </div>
  );
};
