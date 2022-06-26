package co.cueric.fishes.core.errors

enum class ERRORCODE {
    FAIL,
    OK
}

open class BaseError(open val errorCode: Int, open val message: String?)
