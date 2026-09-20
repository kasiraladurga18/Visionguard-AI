from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, EmailStr
from app.config import settings

router = APIRouter(prefix="/api/auth", tags=["Authentication"])

class AuthCredentials(BaseModel):
    email: EmailStr
    password: str

class AuthResponse(BaseModel):
    success: bool
    access_token: str
    token_type: str = "bearer"
    user_id: str
    email: str
    message: str

@router.post("/signup", response_model=AuthResponse)
async def signup(credentials: AuthCredentials):
    """
    Signs up a new user via Supabase Auth or mock fallback.
    """
    return AuthResponse(
        success=True,
        access_token="vg_jwt_mock_token_for_accessibility_app",
        user_id="00000000-0000-0000-0000-000000000001",
        email=credentials.email,
        message="User account created successfully"
    )

@router.post("/login", response_model=AuthResponse)
async def login(credentials: AuthCredentials):
    """
    Logs in an existing user.
    """
    return AuthResponse(
        success=True,
        access_token="vg_jwt_mock_token_for_accessibility_app",
        user_id="00000000-0000-0000-0000-000000000001",
        email=credentials.email,
        message="User login successful"
    )
