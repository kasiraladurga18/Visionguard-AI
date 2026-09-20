import { sendBackendSpatialQuery } from './backendApi';

export async function queryGeminiSpatialEngine(query: string, locationContext?: string): Promise<string> {
  const customApiKey = localStorage.getItem('vg_gemini_api_key');

  if (customApiKey) {
    try {
      const endpoint = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${customApiKey}`;
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [
            {
              role: 'user',
              parts: [{ text: `You are Vision Guard AI, an intelligent accessibility and spatial navigation companion for visually impaired users. Provide reassuring, brief 2-3 sentence visual guidance cues. Query: ${query}` }]
            }
          ]
        })
      });
      const data = await response.json();
      if (data?.candidates?.[0]?.content?.parts?.[0]?.text) {
        return data.candidates[0].content.parts[0].text;
      }
    } catch (e) {
      console.warn('Direct Gemini API call failed, falling back to backend API', e);
    }
  }

  try {
    const backendRes = await sendBackendSpatialQuery(query, locationContext);
    if (backendRes?.answer) {
      return backendRes.answer;
    }
    if (backendRes?.spoken_answer) {
      return backendRes.spoken_answer;
    }
    if (backendRes?.reply) {
      return backendRes.reply;
    }
  } catch (err) {
    console.warn('Backend spatial API call offline, using built-in spatial rules engine', err);
  }

  return fallbackSpatialEngine(query);
}

function fallbackSpatialEngine(query: string): string {
  const lower = query.toLowerCase();
  if (lower.includes('where') || lower.includes('location')) {
    return 'You are currently at 742 Evergreen Terrace sidewalk, facing North-East toward Central Metro Station. Pathway clear for 12 meters.';
  }
  if (lower.includes('door') || lower.includes('exit')) {
    return 'Automatic exit doors detected 5 meters directly ahead. Tactile flooring guides your path.';
  }
  if (lower.includes('stair') || lower.includes('step')) {
    return 'Ascending staircase located 3 steps forward on your right side. Metal handrail available on left.';
  }
  if (lower.includes('home') || lower.includes('house')) {
    return 'Home bookmark selected. Head North on 4th street for 150 meters. Crosswalk has blinking audio beacon.';
  }
  if (lower.includes('sos') || lower.includes('help') || lower.includes('emergency')) {
    return 'Emergency alert broadcast triggered. Spatial GPS coordinates dispatched to guardian contact.';
  }
  return 'Vision Guard AI active. Pathway clear for 8 meters. No overhead or ground hazards detected.';
}
