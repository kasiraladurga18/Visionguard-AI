import React, { useState } from 'react';
import { Sun, Moon } from 'lucide-react';
import { HeaderNav } from '../components/HeaderNav';
import { GlassCard } from '../components/GlassCard';
import { GlassButton } from '../components/GlassButton';
import { ttsManager } from '../services/ttsManager';

interface SettingsPageProps {
  onBack: () => void;
  speakAnnouncement: (message: string) => void;
}

export const SettingsPage: React.FC<SettingsPageProps> = ({ onBack, speakAnnouncement }) => {
  const [speechRate, setSpeechRate] = useState<number>(1.0);
  const [speechPitch, setSpeechPitch] = useState<number>(1.0);
  const [highContrast, setHighContrast] = useState<boolean>(
    document.body.getAttribute('data-theme') === 'high-contrast'
  );

  const handleRateChange = (rate: number) => {
    setSpeechRate(rate);
    ttsManager.setSpeechRate(rate);
    speakAnnouncement(`Speech rate set to ${rate}x`);
  };

  const handlePitchChange = (pitch: number) => {
    setSpeechPitch(pitch);
    ttsManager.setSpeechPitch(pitch);
    speakAnnouncement(`Speech pitch set to ${pitch}`);
  };

  const toggleHighContrast = () => {
    const nextState = !highContrast;
    setHighContrast(nextState);
    if (nextState) {
      document.body.setAttribute('data-theme', 'high-contrast');
      speakAnnouncement('High Contrast accessibility theme enabled.');
    } else {
      document.body.removeAttribute('data-theme');
      speakAnnouncement('Default glassmorphic dark theme enabled.');
    }
  };

  return (
    <div style={{ maxWidth: '640px', margin: '0 auto', padding: '0 1rem 3rem 1rem' }}>
      <HeaderNav
        title="Settings & Accessibility"
        subtitle="Voice Rate, Pitch & Contrast"
        showBack
        onBack={onBack}
        speakAnnouncement={speakAnnouncement}
      />

      <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-secondary)', marginBottom: '0.75rem' }}>
        Voice & Speech Preferences
      </h3>

      <GlassCard style={{ marginBottom: '1.25rem' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                Speech Vocalization Rate
              </label>
              <span style={{ fontSize: '0.85rem', color: 'var(--secondary-accent)', fontWeight: 700 }}>
                {speechRate}x
              </span>
            </div>
            <input
              type="range"
              min="0.5"
              max="2.0"
              step="0.1"
              value={speechRate}
              onChange={(e) => handleRateChange(parseFloat(e.target.value))}
              style={{ width: '100%', accentColor: 'var(--secondary-accent)', cursor: 'pointer' }}
            />
          </div>

          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                Speech Pitch Tone
              </label>
              <span style={{ fontSize: '0.85rem', color: 'var(--purple-highlight)', fontWeight: 700 }}>
                {speechPitch}
              </span>
            </div>
            <input
              type="range"
              min="0.5"
              max="1.5"
              step="0.1"
              value={speechPitch}
              onChange={(e) => handlePitchChange(parseFloat(e.target.value))}
              style={{ width: '100%', accentColor: 'var(--purple-highlight)', cursor: 'pointer' }}
            />
          </div>
        </div>
      </GlassCard>

      <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-secondary)', marginBottom: '0.75rem' }}>
        Visual Accessibility Mode
      </h3>

      <GlassCard style={{ marginBottom: '1.25rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            {highContrast ? <Sun size={24} style={{ color: '#ffffff' }} /> : <Moon size={24} style={{ color: 'var(--secondary-accent)' }} />}
            <div>
              <h4 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                High Contrast Display
              </h4>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '0.1rem' }}>
                Pure black and high-contrast borders for low-vision clarity
              </p>
            </div>
          </div>
          <GlassButton
            text={highContrast ? 'Enabled' : 'Enable'}
            onClick={toggleHighContrast}
            accentColor={highContrast ? 'var(--status-success)' : 'var(--secondary-accent)'}
          />
        </div>
      </GlassCard>
    </div>
  );
};
