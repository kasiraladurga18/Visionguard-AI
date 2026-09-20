import React, { useEffect, useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { AiOrbState } from './components/AiOrb';
import { EmergencyModal } from './components/EmergencyModal';
import { ttsManager } from './services/ttsManager';
import { speechManager } from './services/speechManager';
import { queryGeminiSpatialEngine } from './services/geminiNavEngine';

import { SplashPage } from './pages/SplashPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage';
import { ProfilePage } from './pages/ProfilePage';
import { HomePage } from './pages/HomePage';
import { ObstacleScannerPage } from './pages/ObstacleScannerPage';
import { RouteGuidancePage } from './pages/RouteGuidancePage';
import { SavedLocationsPage } from './pages/SavedLocationsPage';
import { SettingsPage } from './pages/SettingsPage';

export type ScreenType =
  | 'splash'
  | 'login'
  | 'register'
  | 'forgot_password'
  | 'profile'
  | 'home'
  | 'vision_scanner'
  | 'route_guidance'
  | 'saved_locations'
  | 'settings';

const AppContent: React.FC = () => {
  const { session } = useAuth();
  const [currentScreen, setCurrentScreen] = useState<ScreenType>('splash');

  const [aiOrbState, setAiOrbState] = useState<AiOrbState>('Idle');
  const [isListening, setIsListening] = useState<boolean>(false);
  const [liveSpeechText, setLiveSpeechText] = useState<string>('');
  const [showSosModal, setShowSosModal] = useState<boolean>(false);

  useEffect(() => {
    const unsubListening = speechManager.subscribeListeningChange((listening) => {
      setIsListening(listening);
      if (listening) {
        setAiOrbState('Listening');
      } else if (aiOrbState === 'Listening') {
        setAiOrbState('Idle');
      }
    });

    const unsubLiveText = speechManager.subscribeLiveText((text) => {
      setLiveSpeechText(text);
    });

    const unsubSpeaking = ttsManager.subscribeSpeakingChange((speaking) => {
      if (speaking && aiOrbState !== 'Thinking') {
        setAiOrbState('Speaking');
      } else if (!speaking && aiOrbState === 'Speaking') {
        setAiOrbState('Idle');
      }
    });

    speechManager.onResultCallback = (recognizedText) => {
      if (recognizedText.trim()) {
        setAiOrbState('Thinking');
        queryGeminiSpatialEngine(recognizedText.trim()).then((reply) => {
          setAiOrbState('Speaking');
          ttsManager.speak(reply);
        });
      }
    };

    speechManager.onErrorCallback = (errorMsg) => {
      console.warn('Voice recognition error:', errorMsg);
      setAiOrbState('Idle');
    };

    return () => {
      unsubListening();
      unsubLiveText();
      unsubSpeaking();
      speechManager.onResultCallback = null;
      speechManager.onErrorCallback = null;
    };
  }, [aiOrbState]);

  const toggleVoiceInput = () => {
    if (isListening) {
      speechManager.stopListening();
      setAiOrbState('Idle');
      ttsManager.speak('Listening stopped.');
    } else {
      speechManager.startListening();
      setAiOrbState('Listening');
      ttsManager.speak('Listening active. Ask Vision Guard AI anything.');
    }
  };

  const handleManualQuery = (query: string) => {
    speechManager.stopListening();
    setAiOrbState('Thinking');
    queryGeminiSpatialEngine(query).then((reply) => {
      setAiOrbState('Speaking');
      ttsManager.speak(reply);
    });
  };

  const speakAnnouncement = (message: string) => {
    ttsManager.speak(message);
  };

  return (
    <main style={{ minHeight: '100vh', position: 'relative' }}>
      {/* Ambient glass background canvas */}
      <div className="bg-ambient-canvas" />

      {/* Navigation Router */}
      <div style={{ position: 'relative', zIndex: 1 }}>
        {currentScreen === 'splash' && (
          <SplashPage
            onSplashFinished={(hasSession) => {
              setCurrentScreen(hasSession ? 'home' : 'login');
            }}
          />
        )}

        {currentScreen === 'login' && (
          <LoginPage
            onNavigateToRegister={() => setCurrentScreen('register')}
            onNavigateToForgotPassword={() => setCurrentScreen('forgot_password')}
            onLoginSuccess={() => setCurrentScreen('home')}
          />
        )}

        {currentScreen === 'register' && (
          <RegisterPage
            onNavigateBackToLogin={() => setCurrentScreen('login')}
            onRegisterSuccess={() => setCurrentScreen('home')}
          />
        )}

        {currentScreen === 'forgot_password' && (
          <ForgotPasswordPage
            onNavigateBackToLogin={() => setCurrentScreen('login')}
          />
        )}

        {currentScreen === 'profile' && (
          <ProfilePage
            onNavigateBack={() => setCurrentScreen('home')}
            onLoggedOut={() => setCurrentScreen('login')}
          />
        )}

        {currentScreen === 'home' && (
          <HomePage
            aiState={aiOrbState}
            isListening={isListening}
            liveSpeechText={liveSpeechText}
            onOrbClick={toggleVoiceInput}
            onMicToggle={toggleVoiceInput}
            onSubmitQuery={handleManualQuery}
            onNavigateToVisionScanner={() => setCurrentScreen('vision_scanner')}
            onNavigateToRouteGuidance={() => setCurrentScreen('route_guidance')}
            onNavigateToSavedLocations={() => setCurrentScreen('saved_locations')}
            onNavigateToSettings={() => setCurrentScreen('settings')}
            onNavigateToProfile={() => setCurrentScreen('profile')}
            onTriggerSos={() => {
              setShowSosModal(true);
              setAiOrbState('Error');
            }}
            speakAnnouncement={speakAnnouncement}
          />
        )}

        {currentScreen === 'vision_scanner' && (
          <ObstacleScannerPage
            onBack={() => setCurrentScreen('home')}
            speakAnnouncement={speakAnnouncement}
          />
        )}

        {currentScreen === 'route_guidance' && (
          <RouteGuidancePage
            onBack={() => setCurrentScreen('home')}
            speakAnnouncement={speakAnnouncement}
          />
        )}

        {currentScreen === 'saved_locations' && (
          <SavedLocationsPage
            onBack={() => setCurrentScreen('home')}
            onStartNavigation={(placeName) => {
              setCurrentScreen('route_guidance');
              ttsManager.speak(`Starting turn by turn guidance to ${placeName}`);
            }}
            speakAnnouncement={speakAnnouncement}
          />
        )}

        {currentScreen === 'settings' && (
          <SettingsPage
            onBack={() => setCurrentScreen('home')}
            speakAnnouncement={speakAnnouncement}
          />
        )}
      </div>

      {/* Emergency SOS Modal */}
      <EmergencyModal
        isOpen={showSosModal}
        onDismiss={() => {
          setShowSosModal(false);
          setAiOrbState('Idle');
        }}
        speakAnnouncement={speakAnnouncement}
      />
    </main>
  );
};

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

export default App;
