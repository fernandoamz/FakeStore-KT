package com.example.fakestore.feature.products.llm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantSuggestionTest {

    @Test
    fun buildSuggestions_prefersCatalogCategories_andEndsWithHelp() {
        val suggestions = buildAssistantSuggestions(
            categories = listOf("smartphones", "laptops", "groceries"),
            sampleTitles = listOf("iPhone 13 Pro")
        )

        assertEquals(4, suggestions.size)
        assertEquals("help", suggestions.last().id)
        assertTrue(suggestions.any { it.label.contains("Smartphone", ignoreCase = true) })
        assertTrue(suggestions.any { it.label.contains("Laptop", ignoreCase = true) })
        assertTrue(suggestions.none { it.prompt.contains("Ghostface") })
    }

    @Test
    fun buildSuggestions_usesFallbacksWhenCategoriesUnknown() {
        val suggestions = buildAssistantSuggestions(
            categories = listOf("unknown-category"),
            sampleTitles = emptyList()
        )

        assertEquals(4, suggestions.size)
        assertEquals("help", suggestions.last().id)
        assertTrue(suggestions.any { it.id == "budget" })
    }
}
