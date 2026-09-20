from pydantic import BaseModel
from typing import Optional

class UserProfileResponse(BaseModel):
    id: str
    email: str
    full_name: Optional[str] = None
    speech_rate: float = 1.0
    response_length: str = "concise"
    high_contrast: bool = True
    face_opt_in: bool = False

class UserProfileUpdate(BaseModel):
    full_name: Optional[str] = None
    speech_rate: Optional[float] = None
    response_length: Optional[str] = None
    high_contrast: Optional[bool] = None
    face_opt_in: Optional[bool] = None

class UserPreferencesResponse(BaseModel):
    user_id: str
    auto_speak: bool = True
    tts_pitch: float = 1.0
    preferred_language: str = "en-US"
    obstacle_alerts: bool = True
    vibration_feedback: bool = True
    sos_auto_send: bool = False

class UserPreferencesUpdate(BaseModel):
    auto_speak: Optional[bool] = None
    tts_pitch: Optional[float] = None
    preferred_language: Optional[str] = None
    obstacle_alerts: Optional[bool] = None
    vibration_feedback: Optional[bool] = None
    sos_auto_send: Optional[bool] = None
