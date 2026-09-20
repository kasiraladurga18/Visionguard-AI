type ListeningCallback = (listening: boolean) => void;
type LiveTextCallback = (text: string) => void;

class SpeechManager {
  private recognition: any = null;
  private isListening: boolean = false;
  private listeningSubscribers: Set<ListeningCallback> = new Set();
  private liveTextSubscribers: Set<LiveTextCallback> = new Set();

  public onResultCallback: ((text: string) => void) | null = null;
  public onErrorCallback: ((error: string) => void) | null = null;

  constructor() {
    if (typeof window !== 'undefined') {
      const SpeechRecognition =
        (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

      if (SpeechRecognition) {
        this.recognition = new SpeechRecognition();
        this.recognition.continuous = false;
        this.recognition.interimResults = true;
        this.recognition.lang = 'en-US';

        this.recognition.onstart = () => {
          this.isListening = true;
          this.notifyListening(true);
        };

        this.recognition.onresult = (event: any) => {
          let interimTranscript = '';
          let finalTranscript = '';

          for (let i = event.resultIndex; i < event.results.length; ++i) {
            if (event.results[i].isFinal) {
              finalTranscript += event.results[i][0].transcript;
            } else {
              interimTranscript += event.results[i][0].transcript;
            }
          }

          const currentText = finalTranscript || interimTranscript;
          this.notifyLiveText(currentText);

          if (finalTranscript && this.onResultCallback) {
            this.onResultCallback(finalTranscript);
          }
        };

        this.recognition.onerror = (event: any) => {
          this.isListening = false;
          this.notifyListening(false);
          if (this.onErrorCallback) {
            this.onErrorCallback(event.error || 'Speech recognition error');
          }
        };

        this.recognition.onend = () => {
          this.isListening = false;
          this.notifyListening(false);
        };
      }
    }
  }

  public subscribeListeningChange(callback: ListeningCallback): () => void {
    this.listeningSubscribers.add(callback);
    return () => {
      this.listeningSubscribers.delete(callback);
    };
  }

  public subscribeLiveText(callback: LiveTextCallback): () => void {
    this.liveTextSubscribers.add(callback);
    return () => {
      this.liveTextSubscribers.delete(callback);
    };
  }

  private notifyListening(listening: boolean) {
    this.listeningSubscribers.forEach((cb) => cb(listening));
  }

  private notifyLiveText(text: string) {
    this.liveTextSubscribers.forEach((cb) => cb(text));
  }

  public startListening() {
    if (this.recognition && !this.isListening) {
      try {
        this.recognition.start();
      } catch (err) {
        console.warn('Speech recognition start failed:', err);
      }
    }
  }

  public stopListening() {
    if (this.recognition && this.isListening) {
      try {
        this.recognition.stop();
      } catch (err) {
        console.warn('Speech recognition stop failed:', err);
      }
    }
  }

  public getIsListening(): boolean {
    return this.isListening;
  }
}

export const speechManager = new SpeechManager();
