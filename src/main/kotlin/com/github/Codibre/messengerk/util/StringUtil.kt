package com.github.Codibre.messengerk.util

/**
 * Utility class for string operations.
 */
class StringUtil {
    companion object {
        /**
         * Converts a string to kebab case.
         *
         * @param input The input string.
         * @return The string converted to kebab case.
         */
        @JvmStatic
        fun toKebabCase(input: String): String {
            return input.replace(Regex("([a-z])([A-Z]+)"), "$1-$2")
                .replace(Regex("\\s+"), "-")
                .replace(Regex("[^A-Za-z0-9-]"), "")
                .lowercase()
        }

        /**
         * Converts a string to dot case.
         *
         * @param input The input string.
         * @return The string converted to dot case.
         */
        @JvmStatic
        fun toDotCase(input: String): String {
            return input.replace(Regex("([a-z])([A-Z]+)"), "$1.$2")
                .replace(Regex("\\s+"), "")
                .replace(Regex("[^A-Za-z0-9-]"), ".")
                .lowercase()
        }
    }
}
