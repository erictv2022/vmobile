package co.cueric.fishes.models

interface Product {
    var id: String?
    val sku: String
    val name: String
    val productDescription: String?
    val price: Float
    var weightInG: Float?
    var volumeInMl: Float?
    val inStock: Int
    var productUrls: List<String>?
}