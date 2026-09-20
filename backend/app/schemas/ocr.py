from pydantic import BaseModel, Field
from typing import Optional, List

class OcrRequest(BaseModel):
    image_base64: str = Field(..., description="Base64 encoded JPEG/PNG image")
    mode: Optional[str] = Field("full", description="'full', 'summary', or 'selective'")
    focus_topic: Optional[str] = Field(None, description="Optional filter topic, e.g. 'price', 'ingredients'")

class OcrResponse(BaseModel):
    success: bool = True
    message: str = "Text extracted successfully"
    full_text: str
    concise_speech: str
    summary: Optional[str] = None
    key_sections: List[str] = []
