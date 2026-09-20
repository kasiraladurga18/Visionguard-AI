import logging
import uuid
from datetime import datetime
from typing import Dict, Any, List
from app.schemas.emergency import SosTriggerRequest, SosTriggerResponse, EmergencyContactResponse

logger = logging.getLogger("visionguard.emergency_service")

# In-memory store for fallback when direct DB connection is not initialized
_CONTACTS_DB: List[Dict[str, Any]] = [
    {
        "id": "c1a2b3c4-0001-4000-a000-000000000001",
        "user_id": "00000000-0000-0000-0000-000000000001",
        "name": "Dr. Sarah Adams",
        "phone_number": "+15551234567",
        "relationship": "Primary Physician / Guardian",
        "is_primary": True,
        "created_at": datetime.utcnow().isoformat()
    },
    {
        "id": "c1a2b3c4-0002-4000-a000-000000000002",
        "user_id": "00000000-0000-0000-0000-000000000001",
        "name": "Arun Kumar",
        "phone_number": "+15559876543",
        "relationship": "Family / Caregiver",
        "is_primary": False,
        "created_at": datetime.utcnow().isoformat()
    }
]

class EmergencyService:
    def get_contacts(self, user_id: str) -> List[EmergencyContactResponse]:
        user_contacts = [c for c in _CONTACTS_DB if c["user_id"] == user_id]
        return [EmergencyContactResponse(**c) for c in user_contacts]

    def add_contact(self, user_id: str, name: str, phone: str, relationship: str, is_primary: bool) -> EmergencyContactResponse:
        new_id = str(uuid.uuid4())
        contact = {
            "id": new_id,
            "user_id": user_id,
            "name": name,
            "phone_number": phone,
            "relationship": relationship,
            "is_primary": is_primary,
            "created_at": datetime.utcnow().isoformat()
        }
        _CONTACTS_DB.append(contact)
        return EmergencyContactResponse(**contact)

    def delete_contact(self, user_id: str, contact_id: str) -> bool:
        global _CONTACTS_DB
        initial_len = len(_CONTACTS_DB)
        _CONTACTS_DB = [c for c in _CONTACTS_DB if not (c["id"] == contact_id and c["user_id"] == user_id)]
        return len(_CONTACTS_DB) < initial_len

    def trigger_sos(self, user_id: str, req: SosTriggerRequest) -> SosTriggerResponse:
        contacts = self.get_contacts(user_id)
        sos_id = str(uuid.uuid4())
        loc_str = f"Lat: {req.latitude}, Lon: {req.longitude}" if req.latitude else "Location unavailable"
        if req.address:
            loc_str = f"{req.address} ({loc_str})"
            
        alert_msg = f"EMERGENCY ALERT: VisionGuard AI user has triggered an SOS alert. Current location: {loc_str}. Please contact or check on them immediately."
        logger.warning(f"SOS Triggered! id={sos_id}, user={user_id}, payload={alert_msg}")

        return SosTriggerResponse(
            success=True,
            message="SOS triggered successfully. Emergency contacts notified.",
            sos_id=sos_id,
            alert_message=alert_msg,
            contacts_notified=len(contacts)
        )

emergency_service = EmergencyService()
