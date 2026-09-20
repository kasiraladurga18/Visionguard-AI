import logging
from typing import Dict, Any, List
from app.services.gemini_service import gemini_service
from app.schemas.vision import VisionAnalyzeResponse, ObstacleItem

logger = logging.getLogger("visionguard.vision_service")

class VisionService:
    async def analyze_scene(self, image_base64: str, prompt: str = "What is in front of me?", task: str = "describe") -> VisionAnalyzeResponse:
        logger.info(f"Processing vision request: task={task}")
        raw_result = await gemini_service.analyze_image(image_base64=image_base64, prompt=prompt, task=task)
        
        obstacles: List[ObstacleItem] = []
        raw_obstacles = raw_result.get("obstacles", [])
        if isinstance(raw_obstacles, list):
            for item in raw_obstacles:
                if isinstance(item, dict):
                    obstacles.append(
                        ObstacleItem(
                            name=item.get("name", "Object"),
                            estimated_distance=item.get("estimated_distance", "Unknown"),
                            direction=item.get("direction", "Ahead"),
                            hazard_level=item.get("hazard_level", "low")
                        )
                    )

        concise_speech = raw_result.get("concise_speech") or raw_result.get("detailed_description", "Scene analyzed.")
        description = raw_result.get("detailed_description") or concise_speech
        detected_objects = raw_result.get("detected_objects", [])

        return VisionAnalyzeResponse(
            success=True,
            message="Scene analyzed successfully",
            description=description,
            concise_speech=concise_speech,
            obstacles=obstacles,
            detected_objects=detected_objects,
            face_detected=raw_result.get("face_description")
        )

vision_service = VisionService()
