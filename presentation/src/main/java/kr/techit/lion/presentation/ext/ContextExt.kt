package kr.techit.lion.presentation.ext

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.DocumentsContract
import java.io.File
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.myreview.PhotoDialog

fun Context.showSoftInput(view: View) {
    val inputMethodManger = this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManger.showSoftInput(view, 0)
}

fun Context.hideSoftInput(view: View) {
    val inputMethodManager = this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showPermissionSnackBar(view: View) {
    Snackbar.make(view, "권한이 거부 되었습니다. 설정(앱 정보)에서 권한을 확인해 주세요.", Snackbar.LENGTH_SHORT)
        .setAction("확인") {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = this.packageName
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri

        this.startActivity(intent)

    }.show()
}

fun Context.toAbsolutePath(uri: Uri): String? {
    if (DocumentsContract.isDocumentUri(this, uri)) {
        when {
            uri.isExternalStorageDocument() -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return "${getExternalFilesDir(null)?.absolutePath}/${split[1]}"
                }
            }

            uri.isDownloadsDocument() -> {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(contentUri, null, null)
            }

            uri.isMediaDocument() -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()

                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(contentUri, selection, selectionArgs)
            }
        }
    }
    // MediaStore
    else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumn(uri, null, null)
    }
    // File
    else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }

    return null
}

/**
 * 포토 피커 등에서 받은 content URI 를 앱 전용 캐시 디렉터리에 복사하고 그 파일 경로를 반환합니다.
 * contentResolver 로 읽으므로 저장소 권한 필요 없음
 */
fun Context.copyUriToCacheFile(uri: Uri): String? {
    return try {
        val extension = contentResolver.getType(uri)?.substringAfterLast('/')?.takeIf { it.isNotBlank() } ?: "jpg"
        val file = File(cacheDir, "upload_${System.nanoTime()}.$extension")
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}

fun Context.getDataColumn(uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = uri?.let { this.contentResolver.query(it, projection, selection, selectionArgs, null) }
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * 주어진 텍스트를 접근성 서비스(예: TalkBack)를 통해 읽어줍니다.
 *
 * @param text 접근성 서비스를 통해 읽어줄 텍스트
 */
fun Context.announceForAccessibility(text: String) {
    val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val event = AccessibilityEvent.obtain()

    event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT

    event.className = TextView::class.java.name
    event.packageName = packageName

    event.text.add(text)

    accessibilityManager.sendAccessibilityEvent(event)
}

/**
 * TalkBack이 활성화되어 있는지 확인합니다.
 *
 * @return TalkBack이 활성화되어 있으면 true, 그렇지 않으면 false
 */
fun Context.isTallBackEnabled(): Boolean {
    val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    return accessibilityManager.isTouchExplorationEnabled
}

fun Context.showSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view, message, duration)
        .setBackgroundTint(ContextCompat.getColor(this, R.color.text_secondary))
        .show()
}

fun Context.showInfinitySnackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).apply {
        setBackgroundTint(ContextCompat.getColor(this@showInfinitySnackBar, R.color.text_secondary))
        setAction("닫기") { this.dismiss() }
        show()
    }
}

fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

fun Context.showPhotoDialog(
    fragmentManager: FragmentManager,
    imageList: List<String>,
    position: Int
) {
    PhotoDialog(
        imageList,
        position
    ).show(fragmentManager, "PhotoDialog")
}