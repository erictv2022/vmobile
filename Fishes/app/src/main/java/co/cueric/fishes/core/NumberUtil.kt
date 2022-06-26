package co.cueric.fishes.core

import java.text.DecimalFormat

fun parseToFloat(number: Any?) = number?.toString()?.toBigDecimalOrNull()?.toFloat()

fun parseToInt(number: Any?) = number?.toString()?.toBigDecimalOrNull()?.toInt()

fun currencyText(symbol: String, value: Any) = "${symbol}${
    DecimalFormat("#,###.##").format(value)
}"