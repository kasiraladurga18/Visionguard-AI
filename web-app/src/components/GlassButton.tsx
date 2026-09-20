import React from 'react';
import { LucideIcon } from 'lucide-react';

interface GlassButtonProps {
  text: string;
  onClick: () => void;
  icon?: LucideIcon;
  fullWidth?: boolean;
  accentColor?: string;
  disabled?: boolean;
  ariaLabel?: string;
  type?: 'button' | 'submit' | 'reset';
}

export const GlassButton: React.FC<GlassButtonProps> = ({
  text,
  onClick,
  icon: Icon,
  fullWidth = false,
  accentColor,
  disabled = false,
  ariaLabel,
  type = 'button'
}) => {
  return (
    <button
      type={type}
      className="glass-button"
      onClick={onClick}
      disabled={disabled}
      aria-label={ariaLabel || text}
      style={{
        width: fullWidth ? '100%' : 'auto',
        borderColor: accentColor || undefined,
        opacity: disabled ? 0.6 : 1,
        cursor: disabled ? 'not-allowed' : 'pointer'
      }}
    >
      {Icon && <Icon size={20} style={{ color: accentColor || 'var(--secondary-accent)' }} />}
      <span>{text}</span>
    </button>
  );
};
