package com.example.util

object GreekTransliterationHelper {
    /**
     * Transliterates Greek text into Latin (Romanized) phonetic representation
     * following standard Greek romanization rules (e.g., "η γάτα" -> "i gáta").
     */
    fun transliterate(greekText: String): String {
        if (greekText.isBlank()) return ""

        var result = greekText

        // Diphthongs & special combinations first
        val diphthongs = listOf(
            "αι" to "ai", "αί" to "aí", "Αι" to "Ai", "Αί" to "Aí",
            "ει" to "ei", "εί" to "eí", "Ει" to "Ei", "Εί" to "Eí",
            "οι" to "oi", "οί" to "oí", "Οι" to "Oi", "Οί" to "Oí",
            "ου" to "ou", "ού" to "oú", "Ου" to "Ou", "Ού" to "Oú",
            "υι" to "yi", "υί" to "yí",
            "αυ" to "av", "αύ" to "áv",
            "ευ" to "ev", "εύ" to "év",
            "ηυ" to "iv", "ηύ" to "ív",
            "μπ" to "b", "Μπ" to "B",
            "ντ" to "d", "Ντ" to "D",
            "γκ" to "g", "Γκ" to "G",
            "γγ" to "ng", "Γγ" to "Ng",
            "τσ" to "ts", "Τσ" to "Ts",
            "τζ" to "tz", "Τζ" to "Tz"
        )

        for ((gr, lat) in diphthongs) {
            result = result.replace(gr, lat)
        }

        // Single letters
        val singleMap = mapOf(
            'α' to "a", 'ά' to "á", 'Α' to "A", 'Ά' to "Á",
            'β' to "v", 'Β' to "V",
            'γ' to "g", 'Γ' to "G",
            'δ' to "d", 'Δ' to "D",
            'ε' to "e", 'έ' to "é", 'Ε' to "E", 'Έ' to "É",
            'ζ' to "z", 'Ζ' to "Z",
            'η' to "i", 'ή' to "í", 'Η' to "I", 'Ή' to "Í",
            'θ' to "th", 'Θ' to "Th",
            'ι' to "i", 'ί' to "í", 'ϊ' to "ï", 'ΐ' to "ḯ", 'Ι' to "I", 'Ί' to "Í",
            'κ' to "k", 'Κ' to "K",
            'λ' to "l", 'Λ' to "L",
            'μ' to "m", 'Μ' to "M",
            'ν' to "n", 'Ν' to "N",
            'ξ' to "x", 'Ξ' to "X",
            'ο' to "o", 'ό' to "ó", 'Ο' to "O", 'Ό' to "Ó",
            'π' to "p", 'Π' to "P",
            'ρ' to "r", 'Ρ' to "R",
            'σ' to "s", 'ς' to "s", 'Σ' to "S",
            'τ' to "t", 'Τ' to "T",
            'υ' to "y", 'ύ' to "ý", 'ϋ' to "ÿ", 'ΰ' to "ÿ́", 'Υ' to "Y", 'Ύ' to "Ý",
            'φ' to "f", 'Φ' to "F",
            'χ' to "ch", 'Χ' to "Ch",
            'ψ' to "ps", 'Ψ' to "Ps",
            'ω' to "o", 'ώ' to "ó", 'Ω' to "O", 'Ώ' to "Ó"
        )

        val sb = StringBuilder()
        for (c in result) {
            sb.append(singleMap[c] ?: c.toString())
        }
        return sb.toString()
    }
}
