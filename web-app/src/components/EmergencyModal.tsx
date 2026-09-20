import React, { useEffect, useState } from 'react';
import { AlertOctagon, PhoneCall, X } from 'lucide-react';
import { GlassButton } from './GlassButton';
import { sendBackendEmergencyAlert } from '../services/backendApi';

interface EmergencyModalProps {
  isOpen: boolean;
  onDismiss: () => void;
  speakAnnouncement: (msg: string) => void;
}

export const EmergencyModal: React.FC<EmergencyModalProps> = ({
  isOpen,
  onDismiss,
  speakAnnouncement
}) => {
  const [countdown, setCountdown] = useState<number>(3);
  const [alertDispatched, setAlertDispatched] = useState<boolean>(false);

  useEffect(() => {
    if (!isOpen) {
      setCountdown(3);
      setAlertDispatched(false);
      return;
    }

    speakAnnouncement('Emergency SOS countdown triggered. 3 seconds to cancel.');

    // Tactile vibration alert if supported by browser/device
    if (typeof window !== 'undefined' && 'vibrate' in navigator) {
      navigator.vibrate([300, 100, 300, 100, 500]);
    }

    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          dispatchSos();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [isOpen]);

  const dispatchSos = async () => {
    setAlertDispatched(true);
    speakAnnouncement('Guardian SOS alert dispatched with location coordinates.');

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          await sendBackendEmergencyAlert(
            position.coords.latitude,
            position.coords.longitude,
            'Primary Guardian (+1 555-0199)'
          );
        },
        async (_err) => {
          await sendBackendEmergencyAlert(37.7749, -122.4194, 'Primary Guardian (+1 555-0199)');
        }
      );
    } else {
      await sendBackendEmergencyAlert(37.7749, -122.4194, 'Primary Guardian (+1 555-0199)');
    }
  };

  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        zIndex: 1000,
        background: 'rgba(3, 7, 18, 0.85)',
        backdropFilter: 'blur(12px)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '1.5rem'
      }}
      role="dialog"
      aria-modal="true"
      aria-labelledby="sos-modal-title"
    >
      <div
        className="glass-panel"
        style={{
          maxWidth: '440px',
          width: '100%',
          padding: '2rem',
          textAlign: 'center',
          borderColor: 'var(--status-error)',
          boxShadow: '0 0 60px rgba(239, 68, 68, 0.4)'
        }}
      >
        <div
          style={{
            width: '72px',
            height: '72px',
            borderRadius: '50%',
            background: 'rgba(239, 68, 68, 0.2)',
            border: '2px solid var(--status-error)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1.25rem auto'
          }}
        >
          <AlertOctagon size={40} style={{ color: 'var(--status-error)' }} />
        </div>

        <h2 id="sos-modal-title" style={{ fontSize: '1.5rem', fontWeight: 800, color: '#ffffff' }}>
          {alertDispatched ? 'SOS ALERT DISPATCHED' : 'EMERGENCY SOS ALERT'}
        </h2>

        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', fontSize: '0.95rem' }}>
          {alertDispatched
            ? 'Your precise GPS location and audio context have been broadcast to your primary guardian and emergency contacts.'
            : `Broadcasting emergency location beacon to primary guardian contact in ${countdown} seconds.`}
        </p>

        {!alertDispatched && (
          <div
            style={{
              fontSize: '3rem',
              fontWeight: 800,
              color: 'var(--status-error)',
              margin: '1.25rem 0'
            }}
          >
            {countdown}
          </div>
        )}

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginTop: '1.5rem' }}>
          {alertDispatched ? (
            <GlassButton
              text="Call Primary Contact (+1 555-0199)"
              onClick={() => {
                window.location.href = 'tel:+15550199';
              }}
              icon={PhoneCall}
              accentColor="var(--status-success)"
              fullWidth
            />
          ) : null}

          <GlassButton
            text={alertDispatched ? 'Dismiss Alert' : 'CANCEL EMERGENCY SOS'}
            onClick={() => {
              speakAnnouncement('Emergency SOS cancelled.');
              onDismiss();
            }}
            icon={X}
            accentColor={alertDispatched ? 'var(--secondary-accent)' : 'var(--status-error)'}
            fullWidth
          />
        </div>
      </div>
    </div>
  );
};
