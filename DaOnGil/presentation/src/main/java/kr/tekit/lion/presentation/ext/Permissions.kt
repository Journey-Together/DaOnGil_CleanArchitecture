package kr.tekit.lion.presentation.ext

import android.Manifest
import android.os.Build

object Permissions {

    val REQUEST_LOCATION_PERMISSIONS by lazy {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    val LOCATION_PERMISSION_REQUEST_CODE by lazy { 100 }
}