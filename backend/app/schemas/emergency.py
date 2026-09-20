from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class EmergencyContactCreate(BaseModel):
    name: str
    phone_number: str
    relationship: str
    is_primary: bool = False

class EmergencyContactResponse(BaseModel):
    id: str
    user_id: str
    name: str
    phone_number: str
    relationship: str
    is_primary: bool
    created_at: str

class SosTriggerRequest(BaseModel):
    latitude: Optional[float] = None
    longitude: Optional[float] = None
    address: Optional[str] = None
    reason: Optional[str] = "Immediate user assistance triggered"

class SosTriggerResponse(BaseModel):
    success: bool = True
    message: str
    sos_id: str
    alert_message: str
    contacts_notified: int
