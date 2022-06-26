package co.cueric.fishes.core.errors

data class DataError(override val errorCode: Int, override val message: String?) :
    BaseError(errorCode, message)