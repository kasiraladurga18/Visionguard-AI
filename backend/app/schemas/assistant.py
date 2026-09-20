from pydantic import BaseModel, Field
from typing import Optional

class AssistantChatRequest(BaseModel):
    query: str = Field(..., description="User voice or typed question")
    context: Optional[str] = Field(None, description="Previous visual or spatial context")
    location_summary: Optional[str] = Field(None, description="e.g. 'Near Main Library'")

class AssistantChatResponse(BaseModel):
    success: bool = True
    answer: str
    spoken_answer: str
    action_intent: Optional[str] = None # e.g. "open_camera", "read_text", "call_emergency"
