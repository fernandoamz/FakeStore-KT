package com.example.fakestore.feature.products.llm

data class AssistantSuggestion(
    val id: String,
    val emoji: String,
    val label: String,
    val prompt: String
)

private data class CategorySuggestionTemplate(
    val match: Regex,
    val emoji: String,
    val label: String,
    val prompt: String
)

private val categoryTemplates = listOf(
    CategorySuggestionTemplate(
        match = Regex("smartphones?|mobile|celulares?", RegexOption.IGNORE_CASE),
        emoji = "📱",
        label = "Smartphone bueno y económico",
        prompt = "Recomiéndame un smartphone con buena relación calidad-precio y dime por qué."
    ),
    CategorySuggestionTemplate(
        match = Regex("laptops?|tablets?", RegexOption.IGNORE_CASE),
        emoji = "💻",
        label = "Laptop para estudiar o trabajar",
        prompt = "Compara laptops o tablets del catálogo para estudiar o trabajar y dame tu recomendación."
    ),
    CategorySuggestionTemplate(
        match = Regex("fragrances?|beauty|skin-?care|skincare", RegexOption.IGNORE_CASE),
        emoji = "✨",
        label = "Regalo de belleza o fragancia",
        prompt = "Busco un regalo de belleza o fragancia: dame 3 opciones del catálogo con precio."
    ),
    CategorySuggestionTemplate(
        match = Regex("furniture|home-?decoration|kitchen", RegexOption.IGNORE_CASE),
        emoji = "🏠",
        label = "Ideas para el hogar",
        prompt = "Muéstrame opciones de hogar o muebles con buen precio y stock disponible."
    ),
    CategorySuggestionTemplate(
        match = Regex("groceries|food", RegexOption.IGNORE_CASE),
        emoji = "🛒",
        label = "Despensa con buen precio",
        prompt = "¿Qué productos de despensa me conviene comprar ahora según precio y stock?"
    ),
    CategorySuggestionTemplate(
        match = Regex(
            "womens?-?(dresses|bags|shoes|watches|jewellery)|tops|mens?-?(shirts|shoes|watches)",
            RegexOption.IGNORE_CASE
        ),
        emoji = "👗",
        label = "Moda para estrenar",
        prompt = "Recomiéndame 3 prendas o accesorios de moda del catálogo y explica para quién sirven."
    ),
    CategorySuggestionTemplate(
        match = Regex("sports?|sunglasses|motorcycle|vehicle", RegexOption.IGNORE_CASE),
        emoji = "🕶️",
        label = "Accesorios y lifestyle",
        prompt = "Sugiere accesorios del catálogo (deporte, lentes u otros) con buen rating."
    )
)

private val fallbackSuggestions = listOf(
    AssistantSuggestion(
        id = "budget",
        emoji = "💸",
        label = "Algo útil por menos de \$50",
        prompt = "Recomiéndame productos útiles por menos de 50 dólares y ordena del mejor al peor valor."
    ),
    AssistantSuggestion(
        id = "compare",
        emoji = "⚖️",
        label = "Compara 2 productos parecidos",
        prompt = "Elige 2 productos parecidos del catálogo, compáralos en precio, rating y stock, y dime cuál comprar."
    ),
    AssistantSuggestion(
        id = "best-rated",
        emoji = "⭐",
        label = "Lo mejor valorado ahora",
        prompt = "¿Cuáles son los productos mejor valorados del catálogo? Dame un top 3 con precio."
    )
)

private val helpSuggestion = AssistantSuggestion(
    id = "help",
    emoji = "🤝",
    label = "¿Qué puedes hacer por mí?",
    prompt = "Explícame qué puedes hacer para ayudarme a comprar en FakeStore."
)

/**
 * Up to 4 starter prompts: prefers catalog categories, fills with shopping intents, ends with help.
 */
fun buildAssistantSuggestions(
    categories: List<String>,
    sampleTitles: List<String> = emptyList()
): List<AssistantSuggestion> {
    val picked = LinkedHashMap<String, AssistantSuggestion>()

    categories.forEach { category ->
        if (picked.size >= 2) return@forEach
        val template = categoryTemplates.firstOrNull { it.match.containsMatchIn(category) } ?: return@forEach
        picked.putIfAbsent(
            template.label,
            AssistantSuggestion(
                id = "cat-${category.lowercase()}",
                emoji = template.emoji,
                label = template.label,
                prompt = template.prompt
            )
        )
    }

    sampleTitles.firstOrNull()?.takeIf { it.isNotBlank() && picked.size < 3 }?.let { title ->
        val short = if (title.length > 40) title.take(39) + "…" else title
        picked.putIfAbsent(
            "product-like",
            AssistantSuggestion(
                id = "product-like",
                emoji = "🔍",
                label = "Algo como “$short”",
                prompt = "Busco algo parecido a \"$title\". Dame alternativas del catálogo con precio y por qué encajan."
            )
        )
    }

    fallbackSuggestions.forEach { suggestion ->
        if (picked.size >= 3) return@forEach
        picked.putIfAbsent(suggestion.id, suggestion)
    }

    picked[helpSuggestion.id] = helpSuggestion
    return picked.values.take(4)
}
