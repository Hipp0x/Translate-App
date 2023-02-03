package com.example.translateapp.database
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.example.translateapp.database.entity.Dictionnaire
import com.example.translateapp.database.entity.Mot

@Dao
abstract class Dao {

    /*
    **************
    * INSERTIONS *
    **************
     */

    @Insert(entity = Dictionnaire::class, onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertDico(vararg dictionnaire: Dictionnaire): List<Long>

    @Insert(entity = Mot::class, onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertMot(vararg mot: Mot):List<Long>

    /*
    **********
    * UPDATE *
    **********
     */

    @Update
    abstract fun updateMot(mot: Mot): Int


    /*
    **********
    * REMOVE *
    **********
     */

    @Delete
    abstract fun deleteMot(mot: Mot): Int

    /*
    *********
    * LOADS *
    *********
     */

    @Query("SELECT * FROM Dictionnaire")
    abstract fun loadAllDictionnaires(): LiveData<List<Dictionnaire>>

    @Query("SELECT * FROM Mot")
    abstract fun loadAllMots(): LiveData<List<Mot>>

    @Query("SELECT * FROM Mot WHERE toLearn = :learn")
    abstract fun loadAllMotsNeedToBeLearn(learn: Boolean): List<Mot>

    //@Query("SELECT * FROM Dictionnaire WHERE url LIKE 'http_//%' || :url || '.%' AND startLanguage = :startLang AND endLanguage = :endLang ")
    @Query("SELECT * FROM Dictionnaire WHERE url LIKE :url AND startLanguage = :startLang AND endLanguage = :endLang ")
    abstract fun loadDictionnaire(
        url: String,
        startLang: String,
        endLang: String
    ): List<Dictionnaire>

    @Query("SELECT * FROM Dictionnaire WHERE startLanguage = :startLang AND endLanguage = :endLang ")
    abstract fun loadSameLangDictionnaires(
        startLang: String,
        endLang: String
    ): List<Dictionnaire>

    @Query("SELECT * FROM Mot INNER JOIN Dictionnaire WHERE word = :mot AND url = dictionnary AND endLanguage = :endLang")
    abstract fun loadMot(mot: String, endLang: String): List<Mot>

    @Transaction
    open fun insertMotAndDictionnaireOfMot(mot: Mot, dictionnaire: Dictionnaire) {
        insertDico(dictionnaire)
        insertMot(mot)
    }

    @Query("SELECT * FROM Mot WHERE initLanguage = :startLang AND tradLanguage = :endLang AND toLearn = :learn")
    abstract fun loadAllMotsNeedToBeLearnWithSpecificLanguages(
        learn: Boolean,
        startLang: String,
        endLang: String
    ): List<Mot>

}
