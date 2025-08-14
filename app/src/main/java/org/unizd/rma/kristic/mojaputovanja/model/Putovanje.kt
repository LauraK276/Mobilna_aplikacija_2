package org.unizd.rma.kristic.mojaputovanja.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "putovanja")
data class Putovanje(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destinacija: String,
    val prijevoz: String,
    val tipPutovanja: String,
    val datum: String,
    val opis: String? = null,
    val slikeUri: List<String> = emptyList()
)
