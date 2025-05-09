package com.mediapicker.gallery.presentation.viewmodels.factory

import android.app.Application
import android.database.Cursor
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.mediapicker.gallery.presentation.viewmodels.StateData
import java.util.concurrent.Executors

abstract class BaseLoadMediaViewModel(application: Application) : AndroidViewModel(application),
    LoaderManager.LoaderCallbacks<Cursor> {

    private val loadingStateLiveData = MutableLiveData<StateData>()

    fun <T> loadMedia(t: T) where T : androidx.lifecycle.LifecycleOwner, T : androidx.lifecycle.ViewModelStoreOwner {
        loadingStateLiveData.postValue(StateData.LOADING)
        LoaderManager.getInstance(t).restartLoader(getUniqueLoaderId(), null, this)
    }

    fun getLoadingState() = loadingStateLiveData

    abstract fun getCursorLoader(): Loader<Cursor>

    abstract fun getUniqueLoaderId(): Int

    abstract fun prepareDataForAdapterAndPost(cursor: Cursor)


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return getCursorLoader()
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        Executors.newSingleThreadExecutor().submit {
            data?.let {
                prepareDataForAdapterAndPost(it)
                loadingStateLiveData.postValue(StateData.SUCCESS)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}