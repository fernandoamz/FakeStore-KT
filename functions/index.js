const { onRequest } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const { setGlobalOptions } = require("firebase-functions/v2");

setGlobalOptions({ region: "us-central1" });

const openaiApiKey = defineSecret("OPENAI_API_KEY");

const SYSTEM_PROMPT = `Eres el asistente de compras de FakeStore.
Responde siempre en español, de forma clara y concisa.
Ayudas a encontrar productos, comparar opciones y decidir qué comprar.
Si el usuario envía contexto de catálogo, úsalo y no inventes precios ni stock.
Si no hay coincidencias, dilo y sugiere otra búsqueda.
Si te preguntan qué puedes hacer, explica: buscar productos, recomendar por categoría/presupuesto y comparar opciones.
No menciones proveedores externos ni que eres ChatGPT, salvo que te lo pidan.

Formato de respuesta (markdown simple, sin bloques de código):
- Párrafos cortos separados por una línea en blanco.
- Usa **negrita** para nombres de producto y precios.
- Cuando listes opciones, usa viñetas con "- " (una por línea).
- Si comparas 2-3 productos, estructura: intro breve, lista, y una recomendación final en una línea.
- Evita tablas y encabezados largos; máximo un "## " si hace falta un título.`;

/**
 * POST /assistantChat
 * body: {
 *   message: string,
 *   history?: Array<{ role: "user"|"assistant", content: string }>,
 *   catalogContext?: string
 * }
 * response: { reply: string }
 */
exports.assistantChat = onRequest(
  {
    cors: true,
    // Allow the Android app to call without Firebase Auth.
    invoker: "public",
    secrets: [openaiApiKey],
    timeoutSeconds: 60,
    memory: "256MiB",
  },
  async (req, res) => {
    if (req.method === "OPTIONS") {
      res.status(204).send("");
      return;
    }

    if (req.method !== "POST") {
      res.status(405).json({ error: "Method not allowed" });
      return;
    }

    try {
      const message = (req.body?.message || "").toString().trim();
      if (!message) {
        res.status(400).json({ error: "message is required" });
        return;
      }

      const history = Array.isArray(req.body?.history) ? req.body.history : [];
      const catalogContext = (req.body?.catalogContext || "").toString().trim();

      // Lazy-load to keep deploy analysis fast.
      const OpenAI = require("openai");
      const openai = new OpenAI({ apiKey: openaiApiKey.value() });

      const messages = [
        { role: "system", content: SYSTEM_PROMPT },
      ];

      if (catalogContext) {
        messages.push({
          role: "system",
          content: `Contexto de catálogo FakeStore:\n${catalogContext}`,
        });
      }

      for (const item of history.slice(-8)) {
        const role = item?.role === "assistant" ? "assistant" : "user";
        const content = (item?.content || "").toString().trim();
        if (content) {
          messages.push({ role, content });
        }
      }

      messages.push({ role: "user", content: message });

      const completion = await openai.chat.completions.create({
        model: "gpt-4o-mini",
        temperature: 0.4,
        max_tokens: 500,
        messages,
      });

      const reply =
        completion.choices?.[0]?.message?.content?.trim() ||
        "No pude generar una respuesta. Intenta de nuevo.";

      res.status(200).json({ reply });
    } catch (error) {
      console.error("assistantChat error", error);
      res.status(500).json({
        error: "assistant_failed",
        message: error?.message || "Unexpected error",
      });
    }
  }
);
