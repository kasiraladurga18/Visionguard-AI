import React, { useState } from 'react';
import { Bookmark, Home, Briefcase, MapPin, Plus, Navigation } from 'lucide-react';
import { HeaderNav } from '../components/HeaderNav';
import { GlassCard } from '../components/GlassCard';
import { GlassButton } from '../components/GlassButton';

interface SavedPlace {
  id: string;
  name: string;
  address: string;
  category: 'Home' | 'Work' | 'Favorite';
}

interface SavedLocationsPageProps {
  onBack: () => void;
  onStartNavigation: (placeName: string) => void;
  speakAnnouncement: (message: string) => void;
}

export const SavedLocationsPage: React.FC<SavedLocationsPageProps> = ({
  onBack,
  onStartNavigation,
  speakAnnouncement
}) => {
  const [places, setPlaces] = useState<SavedPlace[]>([
    { id: '1', name: 'Home Residence', address: '742 Evergreen Terrace, Sector 4', category: 'Home' },
    { id: '2', name: 'Central Tech Park (Office)', address: '100 Innovation Way, Suite 400', category: 'Work' },
    { id: '3', name: 'Community Pharmacy & Health', address: '450 Healthcare Blvd', category: 'Favorite' }
  ]);

  const [newPlaceName, setNewPlaceName] = useState('');
  const [newPlaceAddress, setNewPlaceAddress] = useState('');
  const [showAddForm, setShowAddForm] = useState(false);

  const handleAddPlace = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPlaceName.trim()) return;

    const newPlace: SavedPlace = {
      id: Date.now().toString(),
      name: newPlaceName.trim(),
      address: newPlaceAddress.trim() || 'Current Location Coordinates',
      category: 'Favorite'
    };

    setPlaces([...places, newPlace]);
    speakAnnouncement(`Bookmark ${newPlace.name} saved successfully.`);
    setNewPlaceName('');
    setNewPlaceAddress('');
    setShowAddForm(false);
  };

  return (
    <div style={{ maxWidth: '640px', margin: '0 auto', padding: '0 1rem 3rem 1rem' }}>
      <HeaderNav
        title="Saved Locations"
        subtitle="Bookmarked Destinations"
        showBack
        onBack={onBack}
        speakAnnouncement={speakAnnouncement}
      />

      <GlassButton
        text={showAddForm ? 'Cancel Add Location' : 'Add New Bookmark Location'}
        onClick={() => setShowAddForm(!showAddForm)}
        icon={Plus}
        accentColor="var(--secondary-accent)"
        fullWidth
      />

      {showAddForm && (
        <GlassCard style={{ marginTop: '1rem', borderColor: 'var(--secondary-accent)' }}>
          <form onSubmit={handleAddPlace} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--text-primary)' }}>
              New Location Bookmark
            </h3>
            <input
              type="text"
              className="glass-input"
              placeholder="Location Name (e.g. Grocery Store)"
              value={newPlaceName}
              onChange={(e) => setNewPlaceName(e.target.value)}
              required
            />
            <input
              type="text"
              className="glass-input"
              placeholder="Street Address or Coordinates"
              value={newPlaceAddress}
              onChange={(e) => setNewPlaceAddress(e.target.value)}
            />
            <GlassButton
              type="submit"
              text="Save Bookmark Location"
              onClick={() => {}}
              accentColor="var(--status-success)"
              fullWidth
            />
          </form>
        </GlassCard>
      )}

      <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-secondary)', margin: '1.5rem 0 0.75rem 0' }}>
        Bookmarked Places
      </h3>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.875rem' }}>
        {places.map((place) => {
          const Icon = place.category === 'Home' ? Home : place.category === 'Work' ? Briefcase : MapPin;
          return (
            <GlassCard key={place.id}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.875rem' }}>
                  <div
                    style={{
                      width: '42px',
                      height: '42px',
                      borderRadius: '0.875rem',
                      background: 'rgba(139, 92, 246, 0.15)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}
                  >
                    <Icon size={22} style={{ color: 'var(--purple-highlight)' }} />
                  </div>
                  <div>
                    <h4 style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                      {place.name}
                    </h4>
                    <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '0.125rem' }}>
                      {place.address}
                    </p>
                  </div>
                </div>

                <GlassButton
                  text="Navigate"
                  onClick={() => onStartNavigation(place.name)}
                  icon={Navigation}
                  accentColor="var(--primary-accent)"
                />
              </div>
            </GlassCard>
          );
        })}
      </div>
    </div>
  );
};
