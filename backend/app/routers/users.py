from fastapi import APIRouter, Depends
from app.dependencies import get_current_user, AuthUser
from app.schemas.user import (
    UserProfileResponse,
    UserProfileUpdate,
    UserPreferencesResponse,
    UserPreferencesUpdate
)

router = APIRouter(prefix="/api/users", tags=["Users & Preferences"])

_USER_PROFILE_STORE = {
    "00000000-0000-0000-0000-000000000001": {
        "id": "00000000-0000-0000-0000-000000000001",
        "email": "user@visionguard.ai",
        "full_name": "VisionGuard Explorer",
        "speech_rate": 1.0,
        "response_length": "concise",
        "high_contrast": True,
        "face_opt_in": False
    }
}

_USER_PREF_STORE = {
    "00000000-0000-0000-0000-000000000001": {
        "user_id": "00000000-0000-0000-0000-000000000001",
        "auto_speak": True,
        "tts_pitch": 1.0,
        "preferred_language": "en-US",
        "obstacle_alerts": True,
        "vibration_feedback": True,
        "sos_auto_send": False
    }
}

@router.get("/profile", response_model=UserProfileResponse)
async def get_profile(current_user: AuthUser = Depends(get_current_user)):
    profile = _USER_PROFILE_STORE.get(current_user.id, {
        "id": current_user.id,
        "email": current_user.email,
        "full_name": "VisionGuard User",
        "speech_rate": 1.0,
        "response_length": "concise",
        "high_contrast": True,
        "face_opt_in": False
    })
    return UserProfileResponse(**profile)

@router.put("/profile", response_model=UserProfileResponse)
async def update_profile(
    update: UserProfileUpdate,
    current_user: AuthUser = Depends(get_current_user)
):
    profile = _USER_PROFILE_STORE.setdefault(current_user.id, {
        "id": current_user.id,
        "email": current_user.email,
        "full_name": "VisionGuard User",
        "speech_rate": 1.0,
        "response_length": "concise",
        "high_contrast": True,
        "face_opt_in": False
    })
    for k, v in update.dict(exclude_unset=True).items():
        profile[k] = v
    return UserProfileResponse(**profile)

@router.get("/preferences", response_model=UserPreferencesResponse)
async def get_preferences(current_user: AuthUser = Depends(get_current_user)):
    pref = _USER_PREF_STORE.get(current_user.id, {
        "user_id": current_user.id,
        "auto_speak": True,
        "tts_pitch": 1.0,
        "preferred_language": "en-US",
        "obstacle_alerts": True,
        "vibration_feedback": True,
        "sos_auto_send": False
    })
    return UserPreferencesResponse(**pref)

@router.put("/preferences", response_model=UserPreferencesResponse)
async def update_preferences(
    update: UserPreferencesUpdate,
    current_user: AuthUser = Depends(get_current_user)
):
    pref = _USER_PREF_STORE.setdefault(current_user.id, {
        "user_id": current_user.id,
        "auto_speak": True,
        "tts_pitch": 1.0,
        "preferred_language": "en-US",
        "obstacle_alerts": True,
        "vibration_feedback": True,
        "sos_auto_send": False
    })
    for k, v in update.dict(exclude_unset=True).items():
        pref[k] = v
    return UserPreferencesResponse(**pref)
