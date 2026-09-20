# VisionGuard AI — Testing Strategy

## 1. Backend Testing
Run pytest for the FastAPI service:
```bash
cd backend
pytest -v tests/test_api.py
```
Coverage includes:
- Health check validation
- Vision analyze with base64 payload & fallback
- OCR text reading & parsing
- Assistant conversational Q&A
- Emergency contacts CRUD and SOS trigger dispatch

## 2. Android Local Testing
Run JVM Unit & Robolectric tests:
```bash
gradle :app:testDebugUnitTest
```
Tests cover:
- Room Database DAOs and entities
- ViewModel state flows (Camera assist, speech rate, reading mode)
- API client request formatting and fallback resilience
- Accessibility content description presence and high-contrast styling

## 3. Manual Accessibility Checklist
- [ ] **TalkBack Compatibility**: Verify every button has an informative `contentDescription`.
- [ ] **Large Touch Targets**: Ensure all buttons and action cards have a minimum height of 56-64dp (exceeding the standard 48dp minimum).
- [ ] **High Contrast**: Verify text against dark backgrounds achieves contrast ratios >= 7:1.
- [ ] **Tactile Feedback**: Confirm haptic vibration fires on tap and during SOS countdown.
- [ ] **Voice Command Verification**: Speak "What is in front of me?", "Read this", "Open emergency", "Where am I".
- [ ] **Network Graceful Degradation**: Switch device to Airplane mode; verify app announces friendly spoken feedback without crashing.
