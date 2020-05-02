# Introduction

A simple library for the selection of photos and videos from device and present them in gallery app like UI.
By default it scan all the photos and videos in the device also you can provide specific folder to scan and it will only show media from that folder.

# Download

[![](https://jitpack.io/v/olxgroup-oss/media-picker-android.svg)](https://jitpack.io/#olxgroup-oss/media-picker-android)


Add it in your root build.gradle at the end of repositories
```
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}

```  

Add the dependency
```
dependencies {
	implementation 'com.github.olxgroup-oss:media-picker-android:-SNAPSHOT'
}
```

# Usage

**Initializd gallery with desired configuration**
```       
val galleryConfig = GalleryConfig.GalleryConfigBuilder(application, BuildConfig.APPLICATION_ID + ".provider", MyClientGalleryCommunicator())
            .useMyPhotoCamera(true)
            .useMyVideoCamera(false)
            .mediaScanningCriteria(GalleryConfig.MediaScanningCriteria("",""))
            .typeOfMediaSupported(GalleryConfig.MediaType.PhotoWithVideo)
            .validation(getValidation())
            .build()
        Gallery.init(galleryConfig)
        
```
```
    private fun getValidation(): Validation {
        return Validation.ValidationBuilder()
            .setMinPhotoSelection(Rule.MinPhotoSelection(1, "Minimum 0 photos can be selected "))
            .setMinVideoSelection(Rule.MinVideoSelection(1, "Minimum 1 video can be selected "))
            .setMaxPhotoSelection(Rule.MaxPhotoSelection(2, "Maximum 2 photos can be selected "))
            .setMaxVideoSelection(Rule.MaxVideoSelection(2, "Maximum 2 videos can be selected")).build()
    }
```
**Add this to your manifest**
```
<activity android:name="com.mediapicker.gallery.presentation.activity.FolderViewActivity"
            android:screenOrientation="portrait"/>
<provider
       android:name="androidx.core.content.FileProvider"
       android:authorities="${applicationId}.provider"
       android:exported="false"
       android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths" />
</provider>
```	

**UI Rendering**

```
try {
        val transaction = supportFragmentManager.beginTransaction()
        fragment = HomeFragment.getInstance(<previous selected photos>,
                <previous selected videos>)
  	    transaction.replace(container.id, fragment!!, fragment!!::class.java.simpleName)
        transaction.addToBackStack(fragment!!.javaClass.name)
        transaction.commitAllowingStateLoss()
} catch (ex: Exception) {
        ex.printStackTrace()
}
```

**Screenshots**

<img src="https://user-images.githubusercontent.com/44491561/80382551-08259300-88c0-11ea-9f49-777cab94f0f5.png" width="320" height="480" />

<img src="https://user-images.githubusercontent.com/44491561/80382632-1f648080-88c0-11ea-84a7-904a959c0733.png" width="320" height="480" />
