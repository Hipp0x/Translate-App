package com.example.translateapp.fragments.mainActivity.jeu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.translateapp.database.DicoBD
import com.example.translateapp.database.entity.Mot
import kotlin.concurrent.thread

class JeuModel(application: Application) : AndroidViewModel(application) {

    private val dao = DicoBD.getDatabase(application).MyDao()

    val listMots = MutableLiveData<List<Mot>>()
    fun loadAllMotsNeedToBeLearnWithSpecificLanguages(boolean: Boolean, l1: String, l2: String) {
        thread {
            listMots.postValue(
                dao.loadAllMotsNeedToBeLearnWithSpecificLanguages(
                    boolean,
                    l1,
                    l2
                )
            )
        }
    }
}