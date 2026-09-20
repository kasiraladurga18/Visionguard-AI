import React, { useEffect, useState } from 'react';
import { Navigation, MapPin, Compass, Volume2, ArrowRight } from 'lucide-react';
import { HeaderNav } from '../components/HeaderNav';
import { GlassCard } from '../components/GlassCard';
import { GlassButton } from '../components/GlassButton';

interface RouteStep {
  id: number;
  instruction: string;
  distance: string;
  isCurrent: boolean;
}

interface RouteGuidancePageProps {
  onBack: () => void;
  speakAnnouncement: (message: string) => void;
}

export const RouteGuidancePage: React.FC<RouteGuidancePageProps> = ({
  onBack,
  speakAnnouncement
}) => {
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [destination, setDestination] = useState('Central Metro Station');

  const steps: RouteStep[] = [
    { id: 1, instruction: 'Head North on 4th Avenue sidewalk for 50 meters.', distance: '50m', isCurrent: currentStepIndex === 0 },
    { id: 2, instruction: 'Turn Right at signalized crosswalk. Audio beacon active.', distance: '120m', isCurrent: currentStepIndex === 1 },
    { id: 3, instruction: 'Continue straight past Metro Entrance B. Tactile paving guides your path.', distance: '40m', isCurrent: currentStepIndex === 2 },
    { id: 4, instruction: 'Arrived at Central Metro Station Ticket Barrier.', distance: 'Arrived', isCurrent: currentStepIndex === 3 }
  ];

  useEffect(() => {
    speakAnnouncement(`Route Guidance active for ${destination}. Current step: ${steps[currentStepIndex].instruction}`);
  }, [currentStepIndex]);

  const advanceStep = () => {
    if (currentStepIndex < steps.length - 1) {
      const nextIdx = currentStepIndex + 1;
      setCurrentStepIndex(nextIdx);
    } else {
      speakAnnouncement(`Navigation complete. You have arrived at ${destination}.`);
    }
  };

  return (
    <div style={{ maxWidth: '640px', margin: '0 auto', padding: '0 1rem 3rem 1rem' }}>
      <HeaderNav
        title="Route Audio Guidance"
        subtitle="Turn-by-Turn Spatial Directions"
        showBack
        onBack={onBack}
        speakAnnouncement={speakAnnouncement}
      />

      {/* Primary Current Step Card */}
      <GlassCard
        borderGlow="var(--primary-accent)"
        style={{
          background: 'linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(0, 242, 254, 0.1))',
          marginBottom: '1.25rem'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--secondary-accent)', fontSize: '0.85rem', fontWeight: 700 }}>
            <Compass size={18} />
            <span>CURRENT STEP {currentStepIndex + 1} OF {steps.length}</span>
          </div>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
            Destination: {destination}
          </span>
        </div>

        <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--text-primary)', lineHeight: '1.4' }}>
          {steps[currentStepIndex].instruction}
        </h3>

        <div style={{ marginTop: '1.25rem', display: 'flex', gap: '0.75rem' }}>
          <GlassButton
            text={currentStepIndex === steps.length - 1 ? 'Finish Navigation' : 'Next Direction Step'}
            onClick={advanceStep}
            icon={ArrowRight}
            accentColor="var(--primary-accent)"
            fullWidth
          />
        </div>
      </GlassCard>

      <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-secondary)', margin: '1.5rem 0 0.75rem 0' }}>
        Upcoming Route Milestones
      </h3>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
        {steps.map((step, idx) => (
          <GlassCard
            key={step.id}
            onClick={() => {
              setCurrentStepIndex(idx);
            }}
            borderGlow={idx === currentStepIndex ? 'var(--primary-accent)' : undefined}
          >
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <div
                  style={{
                    width: '32px',
                    height: '32px',
                    borderRadius: '50%',
                    background: idx === currentStepIndex ? 'var(--primary-accent)' : 'rgba(255, 255, 255, 0.08)',
                    color: '#ffffff',
                    fontWeight: 700,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '0.85rem'
                  }}
                >
                  {step.id}
                </div>
                <p style={{ fontSize: '0.95rem', fontWeight: idx === currentStepIndex ? 700 : 400, color: 'var(--text-primary)' }}>
                  {step.instruction}
                </p>
              </div>

              <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-secondary)', marginLeft: '1rem' }}>
                {step.distance}
              </span>
            </div>
          </GlassCard>
        ))}
      </div>
    </div>
  );
};
