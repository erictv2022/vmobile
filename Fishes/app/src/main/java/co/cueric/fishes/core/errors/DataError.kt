package co.cueric.fishes.core.errors

/**
 * Data related error
 *
 * @property errorCode
 * @property message
 */
data class DataError(override val errorCode: Int, override val message: String?) :
    BaseError(errorCode, message)