package org.unizd.rma.kristic.mojaputovanja.model

import androidx.room.TypeConverter

private const val DELIM = "§§"

class Converters {

    @TypeConverter
    fun fromList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        // escapamo delimiter ako se slučajno pojavi
        return list.joinToString(DELIM) { it.replace(DELIM, "\\$DELIM") }
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        if (data.isBlank()) return emptyList()
        // split s obzirom na naš delimiter vraćamo original zamjenom escape-a
        return data.split(DELIM).map { it.replace("\\$DELIM", DELIM) }
    }
}
