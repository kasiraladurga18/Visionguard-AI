from fastapi import APIRouter, Depends, HTTPException, status
from app.dependencies import get_current_user, AuthUser
from app.schemas.ocr import OcrRequest, OcrResponse
from app.services.ocr_service import ocr_service

router = APIRouter(prefix="/api/ocr", tags=["OCR / Reading"])

@router.post("/read", response_model=OcrResponse)
async def read_text(
    request: OcrRequest,
    current_user: AuthUser = Depends(get_current_user)
):
    """
    Dedicated OCR / Text Reading endpoint: extracts legible printed or handwritten text
    optimized for speech output.
    """
    try:
        return await ocr_service.extract_text(
            image_base64=request.image_base64,
            mode=request.mode or "full",
            focus_topic=request.focus_topic
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"OCR reading failed: {str(e)}"
        )
