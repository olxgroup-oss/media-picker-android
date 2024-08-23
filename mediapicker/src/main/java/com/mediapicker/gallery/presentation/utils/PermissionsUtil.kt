package com.mediapicker.gallery.presentation.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.mediapicker.gallery.R

object PermissionsUtil {

    private const val REQUEST_CODE_PERMISSION = 1001

    fun requestPermissions(
        activity: FragmentActivity,
        permissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
            Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            else -> arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        permissionLauncher.launch(permissions)
    }

    fun handlePermissionsResult(
        activity: FragmentActivity,
        granted: Map<String, Boolean>,
        onAllPermissionsGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        val allPermissionsGranted = granted.all { it.value }
        // Special case for Android 14 (API level 34) and newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val isReadMediaVisualUserSelectedGranted = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED

            if (isReadMediaVisualUserSelectedGranted) {
                onAllPermissionsGranted()
            } else {
                handleDeniedPermissions(activity, granted, onPermissionDenied)
            }
        } else {
            if (allPermissionsGranted) {
                onAllPermissionsGranted()
            } else {
                handleDeniedPermissions(activity, granted, onPermissionDenied)
            }
        }
    }

    private fun handleDeniedPermissions(
        activity: FragmentActivity,
        granted: Map<String, Boolean>,
        onPermissionDenied: () -> Unit
    ) {
        val deniedPermissions = granted.filter { !it.value }.keys

        deniedPermissions.forEach { permission ->
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showNeverAskAgainPermission(activity)
                return
            }
        }

        onPermissionDenied()
    }

    private fun showNeverAskAgainPermission(activity: FragmentActivity) {
        Toast.makeText(activity, activity.getString(R.string.permissions_denied_never_ask_again), Toast.LENGTH_LONG).show()

        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.permissions_required_title))
            .setMessage(activity.getString(R.string.permissions_required_message))
            .setPositiveButton(activity.getString(R.string.settings)) { _, _ ->
                openAppSettings(activity)
            }
            .setNegativeButton(activity.getString(R.string.cancel), null)
            .show()
    }

    fun showPermissionRationale(activity: AppCompatActivity) {
        Toast.makeText(activity, activity.getString(R.string.permissions_denied_rationale), Toast.LENGTH_LONG).show()
    }

    fun openAppSettings(context: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }
}
