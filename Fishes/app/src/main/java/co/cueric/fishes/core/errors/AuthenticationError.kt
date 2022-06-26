package co.cueric.fishes.core.errors

data class AuthenticationError(override val errorCode: Int, override val message: String?) :
    BaseError(errorCode, message)