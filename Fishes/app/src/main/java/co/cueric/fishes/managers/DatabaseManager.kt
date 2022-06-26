package co.cueric.fishes.managers

import co.cueric.fishes.models.Product
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DatabaseManager {
    private lateinit var database: FirebaseDatabase

    @JvmStatic val FIREBASE_REF_LOGIN = "login"
    @JvmStatic val FIREBASE_REF_PRODUCT = "product"

    init {
        setupDB()
    }

    fun setupDB(dbUrl: String = "https://fishes-c5ddf-default-rtdb.asia-southeast1.firebasedatabase.app/"): FirebaseDatabase {
        database = Firebase.database(dbUrl)
        database.setPersistenceEnabled(true)
        return database
    }

    fun getMessageRef() = database.getReference("message")
    fun getLoginRef() = database.getReference(FIREBASE_REF_LOGIN)
    fun getCartRef() = database.getReference("cart")
    fun getProductRef() = database.getReference(FIREBASE_REF_PRODUCT)

    fun addProduct(type: String, product: Product){
        val newProduct = getProductRef().child(type).push()
        product.id = newProduct.key
        newProduct.setValue(product)
    }
}