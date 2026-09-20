import logging
from typing import Dict, Any
from app.services.gemini_service import gemini_service
from app.schemas.ocr import OcrResponse

logger = logging.getLogger("visionguard.ocr_service")

class OcrService:
    async def extract_text(self, image_base64: str, mode: str = "full", focus_topic: str = None) -> OcrResponse:
        prompt = "Extract and structure all visible text in this image for reading aloud."
        if mode == "summary":
            prompt += " Provide a concise executive summary of this document."
        elif focus_topic:
            prompt += f" Focus particularly on information related to: {focus_topic}."

        result = await gemini_service.analyze_image(image_base64=image_base64, prompt=prompt, task="ocr")
        
        full_text = result.get("full_text") or result.get("detailed_description", "No text detected in image.")
        concise_speech = result.get("concise_speech", "Text extracted.")
        summary = result.get("summary")
        key_sections = result.get("key_sections", [])

        return OcrResponse(
            success=True,
            message="Text extraction completed",
            full_text=full_text,
            concise_speech=concise_speech,
            summary=summary,
            key_sections=key_sections
        )

ocr_service = OcrService()
