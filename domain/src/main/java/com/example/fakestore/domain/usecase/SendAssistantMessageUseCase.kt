package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.model.ChatMessage
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.AssistantRepository
import com.example.fakestore.domain.repository.ProductRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class SendAssistantMessageUseCase @Inject constructor(
    private val assistantRepository: AssistantRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        message: String,
        history: List<ChatMessage>
    ): Result<String> {
        val catalogContext = buildCatalogContext(message)
        return assistantRepository.sendMessage(
            message = message,
            history = history,
            catalogContext = catalogContext
        )
    }

    private suspend fun buildCatalogContext(message: String): String {
        return try {
            val products = productRepository.getProducts().first()
            val queryTerms = message
                .lowercase()
                .split(" ", ",", ".", "?", "!", "¿", "¡")
                .map { it.trim() }
                .filter { it.length >= 3 }

            val matches = products
                .asSequence()
                .filter { product ->
                    queryTerms.any { term ->
                        product.title.contains(term, ignoreCase = true) ||
                            product.category.contains(term, ignoreCase = true) ||
                            product.description.contains(term, ignoreCase = true) ||
                            product.brand.contains(term, ignoreCase = true)
                    }
                }
                .take(5)
                .toList()
                .ifEmpty { products.take(5) }

            matches.joinToString(separator = "\n") { product ->
                formatProduct(product)
            }
        } catch (_: Exception) {
            ""
        }
    }

    private fun formatProduct(product: Product): String {
        return "id=${product.id} | ${product.title} | categoría=${product.category} | " +
            "precio=$${String.format("%.2f", product.price)} | stock=${product.stock}"
    }
}
