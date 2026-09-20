import React from 'react';

interface GlassCardProps {
  children: React.ReactNode;
  onClick?: () => void;
  className?: string;
  style?: React.CSSProperties;
  borderGlow?: string;
  ariaLabel?: string;
}

export const GlassCard: React.FC<GlassCardProps> = ({
  children,
  onClick,
  className = '',
  style = {},
  borderGlow,
  ariaLabel
}) => {
  const dynamicStyle: React.CSSProperties = {
    padding: '1.25rem',
    cursor: onClick ? 'pointer' : 'default',
    borderColor: borderGlow || undefined,
    ...style
  };

  return (
    <div
      className={`glass-panel ${className}`}
      style={dynamicStyle}
      onClick={onClick}
      role={onClick ? 'button' : 'region'}
      tabIndex={onClick ? 0 : undefined}
      aria-label={ariaLabel}
      onKeyDown={(e) => {
        if (onClick && (e.key === 'Enter' || e.key === ' ')) {
          e.preventDefault();
          onClick();
        }
      }}
    >
      {children}
    </div>
  );
};
