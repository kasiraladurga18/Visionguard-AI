import React from 'react';
import { Camera, Navigation, Bookmark, Settings, User, AlertOctagon } from 'lucide-react';
import { HeaderNav } from '../components/HeaderNav';
import { GlassCard } from '../components/GlassCard';
import { AiOrb, AiOrbState } from '../components/AiOrb';
import { VoiceInputBar } from '../components/VoiceInputBar';

interface HomePageProps {
  aiState: AiOrbState;
  isListening: boolean;
  liveSpeechText: string;
  onOrbClick: () => void;
  onMicToggle: () => void;
  onSubmitQuery: (query: string) => void;
  onNavigateToVisionScanner: () => void;
  onNavigateToRouteGuidance: () => void;
  onNavigateToSavedLocations: () => void;
  onNavigateToSettings: () => void;
  onNavigateToProfile: () => void;
  onTriggerSos: () => void;
  speakAnnouncement: (message: string) => void;
}

export const HomePage: React.FC<HomePageProps> = ({
  aiState,
  isListening,
  liveSpeechText,
  onOrbClick,
  onMicToggle,
  onSubmitQuery,
  onNavigateToVisionScanner,
  onNavigateToRouteGuidance,
  onNavigateToSavedLocations,
  onNavigateToSettings,
  onNavigateToProfile,
  onTriggerSos,
  speakAnnouncement
}) => {
  return (
    <div style={{ maxWidth: '640px', margin: '0 auto', padding: '0 1rem 3rem 1rem' }}>
      <HeaderNav
        title="VisionGuard AI"
        subtitle="Autonomous Spatial Companion"
        onSettingsClick={onNavigateToSettings}
        speakAnnouncement={speakAnnouncement}
      />

      {/* Center AI Voice Assistant Orb */}
      <AiOrb state={aiState} onClick={onOrbClick} size={150} />

      {/* Voice Prompt Input Bar */}
      <VoiceInputBar
        isListening={isListening}
        onMicToggle={onMicToggle}
        onSubmitText={onSubmitQuery}
        liveText={liveSpeechText}
      />

      {/* High-Accessibility Action Grid */}
      <h2
        style={{
          fontSize: '1rem',
          fontWeight: 700,
          color: 'var(--text-secondary)',
          margin: '2rem 0 1rem 0',
          letterSpacing: '0.05em'
        }}
      >
        Navigation & Scene Tools
      </h2>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
        <GlassCard
          onClick={() => {
            speakAnnouncement('Opening Obstacle Vision Scanner');
            onNavigateToVisionScanner();
          }}
          borderGlow="rgba(0, 242, 254, 0.4)"
          ariaLabel="Obstacle Vision Scanner. AI hazard radar scanning."
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            <div
              style={{
                width: '44px',
                height: '44px',
                borderRadius: '0.875rem',
                background: 'rgba(0, 242, 254, 0.15)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <Camera size={24} style={{ color: 'var(--secondary-accent)' }} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                Obstacle Scanner
              </h3>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '0.2rem' }}>
                Camera hazard radar
              </p>
            </div>
          </div>
        </GlassCard>

        <GlassCard
          onClick={() => {
            speakAnnouncement('Opening Route Guidance');
            onNavigateToRouteGuidance();
          }}
          borderGlow="rgba(59, 130, 246, 0.4)"
          ariaLabel="Route Guidance. Turn by turn audio navigation."
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            <div
              style={{
                width: '44px',
                height: '44px',
                borderRadius: '0.875rem',
                background: 'rgba(59, 130, 246, 0.15)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <Navigation size={24} style={{ color: 'var(--primary-accent)' }} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                Route Guidance
              </h3>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '0.2rem' }}>
                Turn-by-turn audio
              </p>
            </div>
          </div>
        </GlassCard>

        <GlassCard
          onClick={() => {
            speakAnnouncement('Opening Saved Locations');
            onNavigateToSavedLocations();
          }}
          borderGlow="rgba(139, 92, 246, 0.4)"
          ariaLabel="Saved Locations. Bookmarks for Home, Work, and Favorites."
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            <div
              style={{
                width: '44px',
                height: '44px',
                borderRadius: '0.875rem',
                background: 'rgba(139, 92, 246, 0.15)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <Bookmark size={24} style={{ color: 'var(--purple-highlight)' }} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                Bookmarks
              </h3>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '0.2rem' }}>
                Saved home & destinations
              </p>
            </div>
          </div>
        </GlassCard>

        <GlassCard
          onClick={() => {
            speakAnnouncement('Opening User Profile');
            onNavigateToProfile();
          }}
          borderGlow="rgba(236, 72, 153, 0.4)"
          ariaLabel="User Profile. View credentials and contacts."
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            <div
              style={{
                width: '44px',
                height: '44px',
                borderRadius: '0.875rem',
                background: 'rgba(236, 72, 153, 0.15)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <User size={24} style={{ color: 'var(--pink-accent)' }} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                User Profile
              </h3>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '0.2rem' }}>
                Guardian contact & account
              </p>
            </div>
          </div>
        </GlassCard>
      </div>

      {/* Emergency SOS Banner Button */}
      <div style={{ marginTop: '1.5rem' }}>
        <button
          onClick={onTriggerSos}
          aria-label="TRIGGER EMERGENCY GUARDIAN SOS ALERT"
          style={{
            width: '100%',
            padding: '1.25rem',
            borderRadius: '1.5rem',
            background: 'linear-gradient(135deg, rgba(239,68,68,0.25), rgba(185,28,28,0.35))',
            border: '2px solid var(--status-error)',
            color: '#ffffff',
            fontWeight: 800,
            fontSize: '1.1rem',
            letterSpacing: '0.05em',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '0.75rem',
            cursor: 'pointer',
            boxShadow: '0 8px 30px rgba(239, 68, 68, 0.3)'
          }}
        >
          <AlertOctagon size={28} />
          <span>TRIGGER EMERGENCY SOS</span>
        </button>
      </div>
    </div>
  );
};
