from fastapi import APIRouter, Depends, HTTPException, status
from app.dependencies import get_current_user, AuthUser
from app.schemas.vision import VisionAnalyzeRequest, VisionAnalyzeResponse
from app.services.vision_service import vision_service

router = APIRouter(prefix="/api/vision", tags=["Vision"])

@router.post("/analyze", response_model=VisionAnalyzeResponse)
async def analyze_camera_frame(
    request: VisionAnalyzeRequest,
    current_user: AuthUser = Depends(get_current_user)
):
    """
    Main Camera Assist endpoint: Analyzes captured camera frame and returns
    high-contrast, concise spoken description and obstacle awareness.
    """
    try:
        return await vision_service.analyze_scene(
            image_base64=request.image_base64,
            prompt=request.prompt or "What is in front of me?",
            task=request.task or "describe"
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Vision analysis failed: {str(e)}"
        )

@router.post("/describe", response_model=VisionAnalyzeResponse)
async def describe_scene(
    request: VisionAnalyzeRequest,
    current_user: AuthUser = Depends(get_current_user)
):
    """
    Quick scene description endpoint for voice queries like 'Describe the scene'.
    """
    return await analyze_camera_frame(request, current_user)
