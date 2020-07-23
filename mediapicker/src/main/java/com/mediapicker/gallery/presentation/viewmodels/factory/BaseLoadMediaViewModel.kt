package com.mediapicker.gallery.presentation.viewmodels.factory

import android.database.Cursor
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.presentation.viewmodels.StateData
import java.util.concurrent.Executors

abstract class BaseLoadMediaViewModel(private val galleryConfig: GalleryConfig) : ViewModel(), LoaderManager.LoaderCallbacks<Cursor> {

    private val loadingStateLiveData = MutableLiveData<StateData>()

    fun <T> loadMedia(t: T) where T : androidx.lifecycle.LifecycleOwner, T : androidx.lifecycle.ViewModelStoreOwner {
        loadingStateLiveData.postValue(StateData.LOADING)
        LoaderManager.getInstance(t).restartLoader(getUniqueLoaderId(), null, this)
    }

    fun getLoadingState() = loadingStateLiveData

    protected fun getApplication() = galleryConfig.applicationContext

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