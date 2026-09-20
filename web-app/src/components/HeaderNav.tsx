import React from 'react';
import { ArrowLeft, Eye, Volume2, Settings } from 'lucide-react';

interface HeaderNavProps {
  title: string;
  subtitle?: string;
  showBack?: boolean;
  onBack?: () => void;
  onSettingsClick?: () => void;
  speakAnnouncement?: (msg: string) => void;
}

export const HeaderNav: React.FC<HeaderNavProps> = ({
  title,
  subtitle,
  showBack = false,
  onBack,
  onSettingsClick,
  speakAnnouncement
}) => {
  return (
    <header
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '1.25rem 0.5rem',
        marginBottom: '1rem',
        position: 'relative',
        zIndex: 10
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.875rem' }}>
        {showBack && onBack ? (
          <button
            onClick={() => {
              if (speakAnnouncement) speakAnnouncement('Navigating back');
              onBack();
            }}
            aria-label="Go Back"
            style={{
              background: 'rgba(255, 255, 255, 0.08)',
              border: '1px solid var(--border-glass)',
              borderRadius: '0.875rem',
              width: '42px',
              height: '42px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--text-primary)',
              cursor: 'pointer'
            }}
          >
            <ArrowLeft size={20} />
          </button>
        ) : (
          <div
            style={{
              width: '42px',
              height: '42px',
              borderRadius: '0.875rem',
              background: 'linear-gradient(135deg, rgba(0,242,254,0.2), rgba(139,92,246,0.2))',
              border: '1px solid var(--border-glass-glow)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <Eye size={22} style={{ color: 'var(--secondary-accent)' }} />
          </div>
        )}

        <div>
          <h1
            style={{
              fontSize: '1.35rem',
              fontWeight: 800,
              letterSpacing: '-0.02em',
              background: 'linear-gradient(135deg, #ffffff 0%, var(--secondary-accent) 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent'
            }}
          >
            {title}
          </h1>
          {subtitle && (
            <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '0.1rem' }}>
              {subtitle}
            </p>
          )}
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        {speakAnnouncement && (
          <button
            onClick={() => speakAnnouncement(`Screen title: ${title}. ${subtitle || ''}`)}
            aria-label="Vocalize Screen Summary"
            style={{
              background: 'rgba(255, 255, 255, 0.08)',
              border: '1px solid var(--border-glass)',
              borderRadius: '0.875rem',
              width: '42px',
              height: '42px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--secondary-accent)',
              cursor: 'pointer'
            }}
          >
            <Volume2 size={20} />
          </button>
        )}

        {onSettingsClick && (
          <button
            onClick={onSettingsClick}
            aria-label="Open Settings"
            style={{
              background: 'rgba(255, 255, 255, 0.08)',
              border: '1px solid var(--border-glass)',
              borderRadius: '0.875rem',
              width: '42px',
              height: '42px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--text-primary)',
              cursor: 'pointer'
            }}
          >
            <Settings size={20} />
          </button>
        )}
      </div>
    </header>
  );
};
