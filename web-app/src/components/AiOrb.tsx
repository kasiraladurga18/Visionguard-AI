import React from 'react';
import { Mic, Volume2, Sparkles, AlertCircle } from 'lucide-react';

export type AiOrbState = 'Idle' | 'Listening' | 'Thinking' | 'Speaking' | 'Error';

interface AiOrbProps {
  state: AiOrbState;
  onClick?: () => void;
  size?: number;
}

export const AiOrb: React.FC<AiOrbProps> = ({ state, onClick, size = 160 }) => {
  const getOrbStyle = (): React.CSSProperties => {
    switch (state) {
      case 'Listening':
        return {
          background: 'radial-gradient(circle at 30% 30%, #60a5fa, #1d4ed8)',
          boxShadow: '0 0 50px rgba(59, 130, 246, 0.8)',
          animation: 'orbPulseListening 2s ease-in-out infinite'
        };
      case 'Thinking':
        return {
          background: 'radial-gradient(circle at 30% 30%, #c084fc, #7e22ce)',
          boxShadow: '0 0 60px rgba(139, 92, 246, 0.9)',
          animation: 'orbPulseThinking 2.5s linear infinite'
        };
      case 'Speaking':
        return {
          background: 'radial-gradient(circle at 30% 30%, #34d399, #047857)',
          boxShadow: '0 0 50px rgba(16, 185, 129, 0.8)',
          animation: 'orbPulseSpeaking 1.8s ease-in-out infinite'
        };
      case 'Error':
        return {
          background: 'radial-gradient(circle at 30% 30%, #f87171, #b91c1c)',
          boxShadow: '0 0 50px rgba(239, 68, 68, 0.8)'
        };
      case 'Idle':
      default:
        return {
          background: 'radial-gradient(circle at 30% 30%, #38bdf8, #0369a1)',
          boxShadow: '0 0 40px rgba(0, 242, 254, 0.5)',
          animation: 'orbPulseIdle 4s ease-in-out infinite'
        };
    }
  };

  const renderIcon = () => {
    switch (state) {
      case 'Listening':
        return <Mic size={48} style={{ color: '#ffffff' }} />;
      case 'Thinking':
        return <Sparkles size={48} style={{ color: '#ffffff' }} />;
      case 'Speaking':
        return <Volume2 size={48} style={{ color: '#ffffff' }} />;
      case 'Error':
        return <AlertCircle size={48} style={{ color: '#ffffff' }} />;
      case 'Idle':
      default:
        return <Mic size={48} style={{ color: '#ffffff' }} />;
    }
  };

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        margin: '1.5rem 0'
      }}
    >
      <div
        onClick={onClick}
        role="button"
        tabIndex={0}
        aria-label={`Vision Guard AI Assistant status: ${state}. Click to start voice prompt.`}
        style={{
          width: `${size}px`,
          height: `${size}px`,
          borderRadius: '50%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: 'pointer',
          position: 'relative',
          transition: 'all 0.4s ease',
          userSelect: 'none',
          ...getOrbStyle()
        }}
      >
        {/* Inner Glass Highlight Sphere */}
        <div
          style={{
            position: 'absolute',
            top: '10%',
            left: '15%',
            width: '40%',
            height: '25%',
            borderRadius: '50%',
            background: 'rgba(255, 255, 255, 0.35)',
            filter: 'blur(3px)',
            pointerEvents: 'none'
          }}
        />

        {renderIcon()}
      </div>

      <span
        style={{
          marginTop: '1rem',
          fontSize: '0.875rem',
          fontWeight: 700,
          letterSpacing: '0.08em',
          textTransform: 'uppercase',
          color:
            state === 'Listening'
              ? 'var(--primary-accent)'
              : state === 'Thinking'
              ? 'var(--purple-highlight)'
              : state === 'Speaking'
              ? 'var(--status-success)'
              : state === 'Error'
              ? 'var(--status-error)'
              : 'var(--text-secondary)'
        }}
      >
        {state === 'Idle' ? 'Tap to Speak' : state}
      </span>
    </div>
  );
};
