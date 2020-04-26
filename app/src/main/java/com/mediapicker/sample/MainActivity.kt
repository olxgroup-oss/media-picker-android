package com.mediapicker.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.domain.contract.IGalleryCommunicator
import com.mediapicker.gallery.domain.entity.PostingDraftPhoto
import com.mediapicker.gallery.domain.entity.Rule
import com.mediapicker.gallery.domain.entity.Validation
import com.mediapicker.gallery.presentation.fragments.DefaultPage
import com.mediapicker.gallery.presentation.fragments.HomeFragment
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val REQUEST_VIDEO_CAPTURE: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       /* val galleryConfig = GalleryConfig.GalleryConfigBuilder(
            application, BuildConfig.APPLICATION_ID + ".provider",
            MyClientGalleryCommunicator()
        )
            .useMyPhotoCamera(true)
            .useMyVideoCamera(true)
            .typeOfMediaSupported(GalleryConfig.MediaType.PhotoWithVideo)
            .validation(getValidation())
            .build()

        PanameraGallery.init(galleryConfig)*/
        showStepFragment()
    }

    private fun getValidation(): Validation {
        val i = Rule.MaxVideoSelection(2,"")
        return Validation.ValidationBuilder()
            .setMaxPhotoSelection(Rule.MaxPhotoSelection(getRandomNumber(), "Max Photo Limit Reached "))
            .setMaxVideoSelection(Rule.MaxVideoSelection(getRandomNumber(), "Max Video Limit Reached")).build()
    }


    private fun getRandomNumber() = Random(1).nextInt(10)

    private fun getValidationV2(): Validation {
        return Validation.ValidationBuilder()
            .setMinPhotoSelection(Rule.MinPhotoSelection(1, "Minimum 0 photos can be selected "))
            .setMinVideoSelection(Rule.MinVideoSelection(1, "0 photos can be selected "))
            .setMaxPhotoSelection(Rule.MaxPhotoSelection(2, "Maximum 2 photos can be selected "))
            .setMaxVideoSelection(Rule.MaxVideoSelection(2, "Maximum 2 videos can be selected")).build()
    }


    private var fragment: HomeFragment? = null

    private fun attachGalleryFragment() {

        val galleryConfig = GalleryConfig.GalleryConfigBuilder(
            application, BuildConfig.APPLICATION_ID + ".provider",
            MyClientGalleryCommunicator()
        )
            .useMyPhotoCamera(true)
            .useMyVideoCamera(false)
            .mediaScanningCriteria(GalleryConfig.MediaScanningCriteria("",""))
            .typeOfMediaSupported(GalleryConfig.MediaType.PhotoWithVideo)
            .validation(getValidationV2())
            .build()

        Gallery.init(galleryConfig)
        try {
            val transaction = supportFragmentManager.beginTransaction()
            fragment = HomeFragment.getInstance(SelectedItemHolder.listOfSelectedPhotos,
                SelectedItemHolder.listOfSelectedVideos,
                defaultPageType = DefaultPage.PhotoPage
            )
            transaction.replace(container.id, fragment!!, fragment!!::class.java.simpleName)
            transaction.addToBackStack(fragment!!.javaClass.name)
            transaction.commitAllowingStateLoss()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun jumpToGallery() {
        attachGalleryFragment()
    }

    private fun showStepFragment() {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = StepFragment()
            transaction.replace(container.id, fragment, fragment::class.java.simpleName)
            transaction.commitAllowingStateLoss()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    inner class MyClientGalleryCommunicator : IGalleryCommunicator {
        override fun onCloseMainScreen() {
            Toast.makeText(baseContext, "Close on main screen", Toast.LENGTH_LONG).show()

        }

        override fun actionButtonClick(listOfSelectedPhotos: List<PostingDraftPhoto>, listofSelectedVideos: List<VideoFile>) {
            SelectedItemHolder.listOfSelectedPhotos = listOfSelectedPhotos
            SelectedItemHolder.listOfSelectedVideos = listofSelectedVideos
            showStepFragment()
        }


        /*override fun photosListFromGallery(selectedPhotosList: List<PostingDraftPhoto>) {
            showMessage("photosListFromGallery")
        }

        override fun getSelectedPhotosList(): List<PostingDraftPhoto> {
            showMessage("getSelectedPhotosList")
            return ArrayList()
        }*/

        override fun captureImage() {
            showMessage("captureImage")
        }

        override fun onImageCaptured(capturedImage: File) {
            showMessage("onImageCaptured")
        }

        override fun recordVideo() {
            dispatchTakeVideoIntent()
        }

        override fun onVideoRecorded(file: File) {
            showMessage("onVideoRecorded")
        }

        private fun showMessage(msg: String) {
            //Snackbar.make(this@MainActivity.coordinator_layout, msg, Snackbar.LENGTH_LONG).show()
        }
    }


    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = intent.data
            Toast.makeText(baseContext, "Recorded ", Toast.LENGTH_LONG).show()
            fragment?.reloadMedia()
        }
    }

}


object SelectedItemHolder {
    var listOfSelectedPhotos = emptyList<PostingDraftPhoto>()
    var listOfSelectedVideos = emptyList<VideoFile>()
}