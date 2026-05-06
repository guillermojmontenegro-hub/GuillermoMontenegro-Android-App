package com.example.cvguillermomontenegro.ui.users

object PhoneNumberFormatter {
    private val groups = listOf(2, 1, 2, 4, 4)

    fun normalize(input: String): String = input.filter(Char::isDigit).take(groups.sum())

    fun format(digits: String): String {
        val normalized = normalize(digits)
        if (normalized.isEmpty()) return ""

        var index = 0
        val parts = mutableListOf<String>()

        for (size in groups) {
            if (index >= normalized.length) break
            val end = (index + size).coerceAtMost(normalized.length)
            parts += normalized.substring(index, end)
            index = end
        }

        return buildString {
            append("+")
            append(parts.joinToString("-"))
        }
    }

    fun transformedToOriginal(offset: Int, digits: String): Int {
        val formatted = format(digits)
        val clampedOffset = offset.coerceIn(0, formatted.length)
        return formatted.take(clampedOffset).count(Char::isDigit)
    }

    fun originalToTransformed(offset: Int, digits: String): Int {
        val normalized = normalize(digits)
        val clampedOffset = offset.coerceIn(0, normalized.length)
        if (clampedOffset == 0) return 1.coerceAtMost(format(normalized).length)

        val formatted = format(normalized)
        var digitsSeen = 0
        formatted.forEachIndexed { index, char ->
            if (char.isDigit()) {
                digitsSeen++
                if (digitsSeen == clampedOffset) {
                    return index + 1
                }
            }
        }
        return formatted.length
    }
}
