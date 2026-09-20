from fastapi import APIRouter, Depends, HTTPException, status
from app.dependencies import get_current_user, AuthUser
from app.schemas.assistant import AssistantChatRequest, AssistantChatResponse
from app.services.gemini_service import gemini_service

router = APIRouter(prefix="/api/assistant", tags=["Assistant"])

@router.post("/chat", response_model=AssistantChatResponse)
async def chat_with_assistant(
    request: AssistantChatRequest,
    current_user: AuthUser = Depends(get_current_user)
):
    """
    Conversational voice assistance endpoint.
    """
    try:
        res = await gemini_service.chat_assistant(
            query=request.query,
            context=request.context
        )
        return AssistantChatResponse(
            success=True,
            answer=res["answer"],
            spoken_answer=res["spoken_answer"],
            action_intent=res.get("action_intent")
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Assistant processing failed: {str(e)}"
        )
