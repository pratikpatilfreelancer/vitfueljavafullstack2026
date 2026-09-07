import { NextResponse } from "next/server";

export async function POST(req: Request) {
  try {
    const { messages, partnerName, partnerGender } = await req.json();

    const apiKey = process.env.SARVAM_API_KEY;
    if (!apiKey) {
      return NextResponse.json({ error: "SARVAM_API_KEY environment variable is not configured" }, { status: 500 });
    }

    const systemPrompt = {
      role: "system",
      content: `You are ${partnerName || "Priya Patel"}, a 25-year-old female Financial Analyst from Mumbai who just matched with Sriyaan on HeartMate. 
You are sweet, charming, playful, and slightly flirtatious. Speak naturally in modern Hinglish (a mix of Hindi and English like in Indian WhatsApp/Bumble chats). 
Use emojis appropriately. Keep your responses short (1-3 sentences maximum). Never act like a robotic AI assistant.`
    };

    const response = await fetch("https://api.sarvam.ai/v1/chat/completions", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "api-subscription-key": apiKey
      },
      body: JSON.stringify({
        model: "sarvam-105b-conversations",
        messages: [systemPrompt, ...messages],
        temperature: 0.7,
        max_tokens: 200
      })
    });

    if (!response.ok) {
      const errText = await response.text();
      return NextResponse.json({ error: "Sarvam AI error", details: errText }, { status: response.status });
    }

    const data = await response.json();
    const reply = data.choices?.[0]?.message?.content || "Aww, network issue lag raha hai! Phir se bolo? 😉";

    return NextResponse.json({ reply });
  } catch (error: any) {
    return NextResponse.json({ error: "Internal server error", details: error.message }, { status: 500 });
  }
}
