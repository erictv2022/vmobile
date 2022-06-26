package co.cueric.fishes.core.errors

/**
 * ErrorCode enum
 *
 */
enum class ERRORCODE {
    FAIL,
    OK
}

/**
 * Base class for error
 *
 * @property errorCode
 * @property message Message to show for user
 */
open class BaseError(open val errorCode: Int, open val message: String?)
