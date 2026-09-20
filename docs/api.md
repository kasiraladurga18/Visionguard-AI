# VisionGuard AI — API Specification

Base URL: `http://localhost:8000` (or Android emulator `http://10.0.2.2:8000`)

## Endpoints

### 1. Vision Analysis
- **`POST /api/vision/analyze`**
  - **Body**:
    ```json
    {
      "image_base64": "<base64_encoded_image>",
      "prompt": "What is in front of me?",
      "task": "describe"
    }
    ```
  - **Response (200 OK)**:
    ```json
    {
      "success": true,
      "message": "Scene analyzed successfully",
      "description": "Indoors environment. A comfortable chair is situated about 2 meters ahead.",
      "concise_speech": "A chair is approximately two meters ahead to your right.",
      "obstacles": [
        {
          "name": "Chair",
          "estimated_distance": "approx. 2 meters",
          "direction": "ahead right",
          "hazard_level": "low"
        }
      ],
      "detected_objects": ["Chair", "Doorway"],
      "face_detected": null
    }
    ```

### 2. OCR / Text Reading
- **`POST /api/ocr/read`**
  - **Body**:
    ```json
    {
      "image_base64": "<base64_encoded_image>",
      "mode": "full",
      "focus_topic": null
    }
    ```
  - **Response (200 OK)**:
    ```json
    {
      "success": true,
      "message": "Text extraction completed",
      "full_text": "VisionGuard Prescription Label...",
      "concise_speech": "Prescription for Amoxicillin 500mg, take twice daily with meals.",
      "summary": "Prescription bottle details and dosage.",
      "key_sections": ["Dosage: 500mg", "Instructions: Twice daily"]
    }
    ```

### 3. Assistant Chat
- **`POST /api/assistant/chat`**
  - **Body**:
    ```json
    {
      "query": "Is there a doorway nearby?",
      "context": "Hallway scene"
    }
    ```
  - **Response (200 OK)**:
    ```json
    {
      "success": true,
      "answer": "Yes, an open doorway is visible about 3 meters straight ahead.",
      "spoken_answer": "Yes, an open doorway is visible 3 meters straight ahead.",
      "action_intent": "none"
    }
    ```

### 4. Emergency Contacts & SOS
- **`GET /api/emergency/contacts`** — Returns user's emergency contact list.
- **`POST /api/emergency/contacts`** — Adds new contact.
- **`DELETE /api/emergency/contacts/{id}`** — Removes contact.
- **`POST /api/emergency/sos`** — Dispatches emergency alert with GPS coordinates.

### 5. Profile & Preferences
- **`GET /api/users/profile`** & **`PUT /api/users/profile`**
- **`GET /api/users/preferences`** & **`PUT /api/users/preferences`**

### 6. History
- **`GET /api/history`** — Returns vision queries.
- **`DELETE /api/history/{id}`** — Removes single record.
- **`DELETE /api/history/clear`** — Clears all vision history.
