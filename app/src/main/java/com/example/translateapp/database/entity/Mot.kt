package com.example.translateapp.database.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "mot",
    foreignKeys = [ForeignKey (
        entity = Dictionnaire::class,
        parentColumns = ["url"],
        childColumns = ["dictionnary"],
        deferred = true,
        onDelete = ForeignKey.CASCADE)])

data class Mot(
    var word: String,
    var translation: String,
    var urlTransl: String,
    var dictionnary: String,
    var toLearn: Boolean,
    var initLanguage: String,
    var tradLanguage: String,
    //var learningEnding:Date,
    var knowledge: Int,
    @PrimaryKey(autoGenerate = true) val idMot: Long = 0
)