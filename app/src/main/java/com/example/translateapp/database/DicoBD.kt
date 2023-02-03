package com.example.translateapp.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.translateapp.database.entity.Dictionnaire
import com.example.translateapp.database.entity.Mot
import kotlin.jvm.Volatile

@Database(entities = [Dictionnaire::class, Mot::class], version = 5)
abstract class DicoBD : RoomDatabase() {

        abstract fun MyDao() : Dao

        companion object {
                @Volatile
                private var instance: DicoBD? = null

                fun getDatabase(context : Context): DicoBD {
                        if(instance != null)
                                return instance!!
                        val db = Room.databaseBuilder(
                                context.applicationContext,
                                DicoBD::class.java,
                                "dictionnaire")
                        .fallbackToDestructiveMigration()
                                .build()
                        instance = db
                        return instance!!
                }
        }
}
