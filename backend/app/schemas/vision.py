from pydantic import BaseModel, Field
from typing import Optional, List

class VisionAnalyzeRequest(BaseModel):
    image_base64: str = Field(..., description="Base64 encoded JPEG/PNG image")
    prompt: Optional[str] = Field("What is in front of me?", description="Spoken or contextual user query")
    task: Optional[str] = Field("describe", description="Task: 'describe', 'detect_objects', 'distance_estimate', 'face_match'")

class ObstacleItem(BaseModel):
    name: str
    estimated_distance: str
    direction: str
    hazard_level: str = "low" # low, medium, high

class VisionAnalyzeResponse(BaseModel):
    success: bool = True
    message: str = "Analysis completed"
    description: str
    concise_speech: str
    obstacles: List[ObstacleItem] = []
    detected_objects: List[str] = []
    face_detected: Optional[str] = None
