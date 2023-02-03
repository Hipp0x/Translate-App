package com.example.translateapp.database
import android.app.Application

class DicoApplication : Application() {

    val database by lazy {
        DicoBD.getDatabase(this)
    }

}