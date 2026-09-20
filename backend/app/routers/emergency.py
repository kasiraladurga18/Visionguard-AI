from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from app.dependencies import get_current_user, AuthUser
from app.schemas.emergency import (
    EmergencyContactCreate,
    EmergencyContactResponse,
    SosTriggerRequest,
    SosTriggerResponse
)
from app.services.emergency_service import emergency_service

router = APIRouter(prefix="/api/emergency", tags=["Emergency & SOS"])

@router.get("/contacts", response_model=List[EmergencyContactResponse])
async def list_emergency_contacts(current_user: AuthUser = Depends(get_current_user)):
    """
    List all configured emergency contacts for current user.
    """
    return emergency_service.get_contacts(current_user.id)

@router.post("/contacts", response_model=EmergencyContactResponse)
async def create_emergency_contact(
    contact: EmergencyContactCreate,
    current_user: AuthUser = Depends(get_current_user)
):
    """
    Add a new emergency contact.
    """
    return emergency_service.add_contact(
        user_id=current_user.id,
        name=contact.name,
        phone=contact.phone_number,
        relationship=contact.relationship,
        is_primary=contact.is_primary
    )

@router.delete("/contacts/{contact_id}")
async def delete_emergency_contact(
    contact_id: str,
    current_user: AuthUser = Depends(get_current_user)
):
    """
    Delete an emergency contact.
    """
    deleted = emergency_service.delete_contact(current_user.id, contact_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Contact not found or access denied"
        )
    return {"success": True, "message": "Contact removed"}

@router.post("/sos", response_model=SosTriggerResponse)
async def trigger_emergency_sos(
    req: SosTriggerRequest,
    current_user: AuthUser = Depends(get_current_user)
):
    """
    Dispatches immediate SOS alert with device coordinates and address.
    """
    return emergency_service.trigger_sos(current_user.id, req)
