import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)
AUTH_HEADERS = {"Authorization": "Bearer vg_jwt_mock_token_for_accessibility_app"}

def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "online"
    assert "VisionGuard" in data["service"]

def test_unauthenticated_access_denied():
    # Attempt request without Authorization header should return HTTP 401
    response = client.get("/api/history")
    assert response.status_code == 401

def test_vision_analyze_fallback():
    payload = {
        "image_base64": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
        "prompt": "What is in front of me?",
        "task": "describe"
    }
    response = client.post("/api/vision/analyze", json=payload, headers=AUTH_HEADERS)
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert "description" in data
    assert "concise_speech" in data

def test_ocr_read():
    payload = {
        "image_base64": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
        "mode": "full"
    }
    response = client.post("/api/ocr/read", json=payload, headers=AUTH_HEADERS)
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert "full_text" in data

def test_assistant_chat():
    payload = {
        "query": "Where can I walk safely?"
    }
    response = client.post("/api/assistant/chat", json=payload, headers=AUTH_HEADERS)
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert "answer" in data

def test_emergency_sos():
    payload = {
        "latitude": 37.7749,
        "longitude": -122.4194,
        "address": "Market Street, San Francisco"
    }
    response = client.post("/api/emergency/sos", json=payload, headers=AUTH_HEADERS)
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert "sos_id" in data

def test_emergency_contacts_flow():
    # List contacts
    get_res = client.get("/api/emergency/contacts", headers=AUTH_HEADERS)
    assert get_res.status_code == 200
    contacts = get_res.json()
    assert len(contacts) >= 1

    # Add contact
    new_contact = {
        "name": "Alex Carter",
        "phone_number": "+15554321098",
        "relationship": "Sibling",
        "is_primary": False
    }
    add_res = client.post("/api/emergency/contacts", json=new_contact, headers=AUTH_HEADERS)
    assert add_res.status_code == 200
    created = add_res.json()
    assert created["name"] == "Alex Carter"

    # Delete contact
    del_res = client.delete(f"/api/emergency/contacts/{created['id']}", headers=AUTH_HEADERS)
    assert del_res.status_code == 200
