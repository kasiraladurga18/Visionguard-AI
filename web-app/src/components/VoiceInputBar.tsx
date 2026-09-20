import React, { useState } from 'react';
import { Mic, Send, Square } from 'lucide-react';

interface VoiceInputBarProps {
  isListening: boolean;
  onMicToggle: () => void;
  onSubmitText: (query: string) => void;
  placeholder?: string;
  liveText?: string;
}

export const VoiceInputBar: React.FC<VoiceInputBarProps> = ({
  isListening,
  onMicToggle,
  onSubmitText,
  placeholder = 'Ask Vision Guard AI anything...',
  liveText = ''
}) => {
  const [inputText, setInputText] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const query = inputText.trim() || liveText.trim();
    if (query) {
      onSubmitText(query);
      setInputText('');
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      style={{
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        gap: '0.75rem',
        marginTop: '1rem'
      }}
    >
      <div style={{ position: 'relative', flex: 1 }}>
        <input
          type="text"
          className="glass-input"
          value={isListening ? liveText : inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder={isListening ? 'Listening to speech...' : placeholder}
          aria-label="Ask Vision Guard AI voice or text prompt"
        />
      </div>

      <button
        type="button"
        onClick={onMicToggle}
        aria-label={isListening ? 'Stop Listening' : 'Start Voice Input'}
        style={{
          width: '52px',
          height: '52px',
          borderRadius: '1rem',
          background: isListening
            ? 'linear-gradient(135deg, #ef4444, #dc2626)'
            : 'linear-gradient(135deg, var(--primary-accent), var(--secondary-accent))',
          border: 'none',
          color: '#ffffff',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: 'pointer',
          flexShrink: 0,
          boxShadow: isListening ? '0 0 20px rgba(239, 68, 68, 0.6)' : '0 4px 15px rgba(0, 242, 254, 0.3)'
        }}
      >
        {isListening ? <Square size={22} /> : <Mic size={22} />}
      </button>

      <button
        type="submit"
        aria-label="Send Query"
        style={{
          width: '52px',
          height: '52px',
          borderRadius: '1rem',
          background: 'rgba(255, 255, 255, 0.08)',
          border: '1px solid var(--border-glass)',
          color: 'var(--text-primary)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: 'pointer',
          flexShrink: 0
        }}
      >
        <Send size={20} />
      </button>
    </form>
  );
};
