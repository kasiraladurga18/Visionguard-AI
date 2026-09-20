import uuid
from datetime import datetime
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from app.dependencies import get_current_user, AuthUser

router = APIRouter(prefix="/api/history", tags=["Vision History"])

class HistoryItem(BaseModel):
    id: str
    user_id: str
    task_type: str
    prompt: Optional[str] = None
    result_text: str
    created_at: str

_HISTORY_STORE: List[HistoryItem] = [
    HistoryItem(
        id="h001",
        user_id="00000000-0000-0000-0000-000000000001",
        task_type="scene_describe",
        prompt="What is in front of me?",
        result_text="A clean hallway with an open doorway 3 meters straight ahead. No floor obstacles.",
        created_at=datetime.utcnow().isoformat()
    ),
    HistoryItem(
        id="h002",
        user_id="00000000-0000-0000-0000-000000000001",
        task_type="ocr_read",
        prompt="Read this sign",
        result_text="Exit Door. Push bar to open.",
        created_at=datetime.utcnow().isoformat()
    )
]

@router.get("", response_model=List[HistoryItem])
async def get_history(current_user: AuthUser = Depends(get_current_user)):
    return [h for h in _HISTORY_STORE if h.user_id == current_user.id]

@router.delete("/{history_id}")
async def delete_history_item(
    history_id: str,
    current_user: AuthUser = Depends(get_current_user)
):
    global _HISTORY_STORE
    orig_len = len(_HISTORY_STORE)
    _HISTORY_STORE = [h for h in _HISTORY_STORE if not (h.id == history_id and h.user_id == current_user.id)]
    if len(_HISTORY_STORE) == orig_len:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="History record not found")
    return {"success": True, "message": "History item deleted"}

@router.delete("/clear")
async def clear_all_history(current_user: AuthUser = Depends(get_current_user)):
    global _HISTORY_STORE
    _HISTORY_STORE = [h for h in _HISTORY_STORE if h.user_id != current_user.id]
    return {"success": True, "message": "All history cleared"}
