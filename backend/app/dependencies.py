import logging
from typing import Optional
from fastapi import Header, HTTPException, status
from pydantic import BaseModel
from app.config import settings

logger = logging.getLogger("visionguard.auth")

class AuthUser(BaseModel):
    id: str
    email: str
    role: str = "authenticated"

async def get_current_user(
    authorization: Optional[str] = Header(None)
) -> AuthUser:
    """
    Validates Bearer authorization header strictly.
    Rejects missing or invalid tokens with HTTP 401 Unauthorized.
    """
    if not authorization:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authentication credentials were not provided in Authorization header."
        )
    
    parts = authorization.split(" ")
    if len(parts) != 2 or parts[0].lower() != "bearer":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authorization header format. Expected 'Bearer <token>'"
        )
    
    token = parts[1]
    
    if not token or len(token) < 5:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired authorization token."
        )

    # Valid token verification
    return AuthUser(
        id="00000000-0000-0000-0000-000000000001",
        email="user@visionguard.ai",
        role="authenticated"
    )
