package co.cueric.fishes.features.authentication.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.config.fastforexAPIKEY
import co.cueric.fishes.core.BaseViewModel
import co.cueric.fishes.core.errors.BaseError
import co.cueric.fishes.core.parseToFloat
import co.cueric.fishes.core.parseToInt
import co.cueric.fishes.managers.AuthManager
import co.cueric.fishes.managers.DatabaseManager
import co.cueric.fishes.models.Fish
import co.cueric.fishes.models.Product
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) :BaseViewModel(application) {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _promptBiometric = Channel<Unit>(Channel.BUFFERED)
    val promptBiometric = _promptBiometric.receiveAsFlow()
    val username = MutableStateFlow("")

    val products = MutableStateFlow<List<Product>>(
        emptyList<Product>()
    )

    val exchageRate = MutableStateFlow<Float?>(null)

    init {
        viewModelScope.launch {
            AuthManager.auth.currentUser?.run {
                _promptBiometric.trySend(Unit)
            }
        }
    }

    fun biometricSuccess() {
        viewModelScope.launch {
            loadProducts()
        }
    }

    fun loadProducts() {
        DatabaseManager.getProductRef().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()
                try {
                    snapshot.children.forEach {
                        val childMap = it.value as HashMap<String, Any>
                        childMap.entries.forEach {
                            val fieldsMap = it.value as HashMap<String, Any>
                            try {
                                products.add(Fish(
                                    id = it.key,
                                    fishType = fieldsMap["fishType"] as String,
                                    sku = fieldsMap["sku"] as String,
                                    name = fieldsMap["name"] as String,
                                    productDescription = fieldsMap["productDescription"] as String,
                                    price = parseToFloat(fieldsMap["price"]) ?: 0f,
                                    weightInG = parseToFloat(fieldsMap["weightInG"]),
                                    volumeInMl = parseToFloat(fieldsMap["volumeInMl"]),
                                    inStock =  parseToInt(fieldsMap["inStock"]) ?: 0,
                                    productUrls = fieldsMap["productUrls"]?.let { it as? List<String> }
                                ))
                            } catch (e: Exception) {
                                Log.d(TAG, e.localizedMessage)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, e.localizedMessage)
                }

                this@HomeViewModel.products.update { products }
            }

            override fun onCancelled(error: DatabaseError) {
                showError(BaseError(errorCode = error.code, message = error.message))
            }
        })
    }

    fun fetchExchangeRate(from: String = "HKD", to:String = "GBP", context: Context) {
        val url = "https://api.fastforex.io/convert?from=${from}&to=${to}&amount=100&api_key=${fastforexAPIKEY}"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val exchangeRate = parseToFloat(response.getJSONObject("result")["rate"])
                this.exchageRate.update { exchangeRate }
                "Response: %s".format(response.toString())
            },
            Response.ErrorListener { error ->
                showError(
                    BaseError(
                        errorCode = error.networkResponse.statusCode,
                        message = error.localizedMessage
                    )
                )
            }
        )

        // Add the request to the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        queue.add(jsonObjectRequest)
    }
}
