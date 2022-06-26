package co.cueric.fishes.core.errors

/**
 * Authentication Error code
 *
 * @property errorCode
 * @property message
 */
data class AuthenticationError(override val errorCode: Int, override val message: String?) :
    BaseError(errorCode, message)