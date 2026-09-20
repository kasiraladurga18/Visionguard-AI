import React, { useState } from 'react';
import { User, Phone, ShieldAlert, LogOut, Save, Mail } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { HeaderNav } from '../components/HeaderNav';
import { GlassCard } from '../components/GlassCard';
import { GlassButton } from '../components/GlassButton';

interface ProfilePageProps {
  onNavigateBack: () => void;
  onLoggedOut: () => void;
}

export const ProfilePage: React.FC<ProfilePageProps> = ({ onNavigateBack, onLoggedOut }) => {
  const { user, profile, updateProfile, signOut } = useAuth();

  const [fullName, setFullName] = useState(profile?.fullName || 'VisionGuard Explorer');
  const [phone, setPhone] = useState(profile?.phone || '+1 (555) 019-2831');
  const [emergencyContact, setEmergencyContact] = useState(profile?.emergencyContact || 'Guardian (+1 555-0199)');
  const [saveStatus, setSaveStatus] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaveStatus(null);
    const res = await updateProfile(fullName, phone, emergencyContact);
    if (res.success) {
      setSaveStatus('Profile updated successfully');
      setTimeout(() => setSaveStatus(null), 3000);
    }
  };

  const handleLogout = async () => {
    await signOut();
    onLoggedOut();
  };

  return (
    <div style={{ maxWidth: '640px', margin: '0 auto', padding: '0 1rem 3rem 1rem' }}>
      <HeaderNav
        title="User Profile"
        subtitle="Account & Guardian Details"
        showBack
        onBack={onNavigateBack}
      />

      <GlassCard style={{ marginBottom: '1.25rem' }}>
        <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {saveStatus && (
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
              {saveStatus}
            </div>
          )}

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
              Full Name
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="glass-input"
                style={{ paddingLeft: '2.75rem' }}
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
              />
              <User size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
              Registered Email
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="email"
                className="glass-input"
                style={{ paddingLeft: '2.75rem', opacity: 0.7 }}
                value={user?.email || 'user@visionguard.ai'}
                disabled
              />
              <Mail size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
              Primary Phone Number
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="glass-input"
                style={{ paddingLeft: '2.75rem' }}
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
              <Phone size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
              Primary Emergency Guardian Contact
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="glass-input"
                style={{ paddingLeft: '2.75rem' }}
                value={emergencyContact}
                onChange={(e) => setEmergencyContact(e.target.value)}
              />
              <ShieldAlert size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--status-error)' }} />
            </div>
          </div>

          <GlassButton
            type="submit"
            text="Save Profile Updates"
            onClick={() => {}}
            icon={Save}
            accentColor="var(--secondary-accent)"
            fullWidth
          />
        </form>
      </GlassCard>

      <GlassButton
        text="Sign Out of Account"
        onClick={handleLogout}
        icon={LogOut}
        accentColor="var(--status-error)"
        fullWidth
      />
    </div>
  );
};
