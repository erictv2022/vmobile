package co.cueric.fishes.core

import java.text.DecimalFormat

/**
 * Try to parse value to float without exception
 *
 * @param number
 * @return
 */
fun parseToFloat(number: Any?) = number?.toString()?.toBigDecimalOrNull()?.toFloat()

/**
 * Try to parse value to Int without exception
 *
 * @param number
 * @return
 */
fun parseToInt(number: Any?) = number?.toString()?.toBigDecimalOrNull()?.toInt()

/**
 * Price formatter
 *
 * @param symbol
 * @param value
 * @return
 */
fun currencyText(symbol: String, value: Any) = "${symbol}${
    DecimalFormat("#,###.##").format(value)
}"