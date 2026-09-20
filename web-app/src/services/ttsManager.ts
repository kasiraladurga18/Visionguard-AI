type SpeakingCallback = (speaking: boolean) => void;

class TTSManager {
  private synth: SpeechSynthesis | null = null;
  private isSpeaking: boolean = false;
  private speechRate: number = 1.0;
  private speechPitch: number = 1.0;
  private speakingSubscribers: Set<SpeakingCallback> = new Set();

  constructor() {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      this.synth = window.speechSynthesis;
    }
  }

  public setSpeechRate(rate: number) {
    this.speechRate = Math.max(0.5, Math.min(2.0, rate));
  }

  public setSpeechPitch(pitch: number) {
    this.speechPitch = Math.max(0.5, Math.min(1.5, pitch));
  }

  public subscribeSpeakingChange(callback: SpeakingCallback): () => void {
    this.speakingSubscribers.add(callback);
    return () => {
      this.speakingSubscribers.delete(callback);
    };
  }

  private notifySpeaking(speaking: boolean) {
    this.isSpeaking = speaking;
    this.speakingSubscribers.forEach((cb) => cb(speaking));
  }

  public speak(text: string, onEnd?: () => void) {
    if (!this.synth) {
      console.warn('SpeechSynthesis API not supported in this environment.');
      if (onEnd) onEnd();
      return;
    }

    this.stop();

    const utterance = new SpeechSynthesisUtterance(text);
    utterance.rate = this.speechRate;
    utterance.pitch = this.speechPitch;

    utterance.onstart = () => {
      this.notifySpeaking(true);
    };

    utterance.onend = () => {
      this.notifySpeaking(false);
      if (onEnd) onEnd();
    };

    utterance.onerror = (e) => {
      console.warn('SpeechSynthesis utterance error:', e);
      this.notifySpeaking(false);
      if (onEnd) onEnd();
    };

    this.synth.speak(utterance);
  }

  public stop() {
    if (this.synth) {
      this.synth.cancel();
      this.notifySpeaking(false);
    }
  }

  public getIsSpeaking(): boolean {
    return this.isSpeaking;
  }
}

export const ttsManager = new TTSManager();
