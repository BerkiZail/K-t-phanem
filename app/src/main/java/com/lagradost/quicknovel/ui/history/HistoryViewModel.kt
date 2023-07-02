package com.lagradost.quicknovel.ui.history

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lagradost.quicknovel.BaseApplication.Companion.getKey
import com.lagradost.quicknovel.BaseApplication.Companion.getKeys
import com.lagradost.quicknovel.BaseApplication.Companion.removeKey
import com.lagradost.quicknovel.BaseApplication.Companion.removeKeys
import com.lagradost.quicknovel.BookDownloader2
import com.lagradost.quicknovel.CommonActivity.activity
import com.lagradost.quicknovel.HISTORY_FOLDER
import com.lagradost.quicknovel.MainActivity
import com.lagradost.quicknovel.MainActivity.Companion.loadResult
import com.lagradost.quicknovel.util.Coroutines.ioSafe
import com.lagradost.quicknovel.util.ResultCached

class HistoryViewModel : ViewModel() {

    private fun updateHistory() {
        ioSafe {
            val list = ArrayList<ResultCached>()
            val keys = getKeys(HISTORY_FOLDER) ?: return@ioSafe
            for (k in keys) {
                val res =
                    getKey<ResultCached>(k) ?: continue
                list.add(res)
            }
            list.sortBy { -it.cachedTime }
            cards.postValue(list)
        }
    }

    init {
        updateHistory()
    }

    val cards: MutableLiveData<ArrayList<ResultCached>> by lazy {
        MutableLiveData<ArrayList<ResultCached>>()
    }

    fun deleteAllAlert() {
        val act = activity ?: return

        act.runOnUiThread {
            val dialogClickListener =
                DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            removeKeys(HISTORY_FOLDER)
                            updateHistory()
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }
            // TODO MAKE STRINGS

            val builder: AlertDialog.Builder =
                AlertDialog.Builder(act)
            builder.setMessage("Are you sure?\nAll history will be lost.")
                .setTitle("Remove History")
                .setPositiveButton("Remove", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show()
        }
    }

    fun open(card: ResultCached) {
        loadResult(card.source, card.apiName)
    }

    fun showMetadata(card : ResultCached) {
        MainActivity.loadPreviewPage(card)
    }

    fun deleteAlert(card: ResultCached) {
        val act = activity ?: return
        act.runOnUiThread {
            val dialogClickListener =
                DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            delete(card)
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }
            // TODO MAKE STRINGS
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(act)
            builder.setMessage("This remove ${card.name} from your history.\nAre you sure?")
                .setTitle("Remove")
                .setPositiveButton("Remove", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show()
        }
    }

    fun delete(card: ResultCached) {
        removeKey(HISTORY_FOLDER, card.id.toString())
        updateHistory()
    }

    fun stream(card: ResultCached) {
        BookDownloader2.stream(card)
    }

}