package co.cueric.fishes.models

data class Fish(
    val fishType: String,
    override var id: String? = null,
    override val sku: String,
    override val name: String,
    override val productDescription: String? = null,
    override val price: Float,
    override var weightInG: Float? = null,
    override var volumeInMl: Float? = null,
    override val inStock: Int,
    override var productUrls: List<String>? = null
) : Product
