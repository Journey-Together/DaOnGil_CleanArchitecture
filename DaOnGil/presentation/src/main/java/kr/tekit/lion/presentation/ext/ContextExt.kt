package kr.tekit.lion.presentation.ext

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
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
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.myreview.PhotoDialog

fun Context.showSoftInput(view: View) {
    val inputMethodManger = this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManger.showSoftInput(view, 0)
}

fun Context.hideSoftInput(view: View) {
    val inputMethodManager = this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showPermissionSnackBar(view: View) {
    Snackbar.make(
        view, "권한이 거부 되었습니다. 설정(앱 정보)에서 권한을 확인해 주세요.",
        Snackbar.LENGTH_SHORT
    ).setAction("확인") {
        //설정 화면으로 이동
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
    // 새로운 접근성 이벤트 객체 생성
    val event = AccessibilityEvent.obtain()

    // 접근성 서비스가 텍스트를 읽어주도록 하는 이벤트 유형
    event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT

    // 이벤트의 클래스 이름을 TextView로 설정
    // 이는 이벤트가 TextView에서 발생한 것처럼 보이도록 하기 위함
    event.className = TextView::class.java.name
    event.packageName = packageName

    //이벤트의 텍스트 목록에 text를 추가
    event.text.add(text)

    // 접근성 서비스에 이벤트를 전달
    accessibilityManager.sendAccessibilityEvent(event)
}

/**
 * TalkBack이 활성화되어 있는지 확인합니다.
 *
 * @return TalkBack이 활성화되어 있으면 true, 그렇지 않으면 false
 */
fun Context.isTallBackEnabled(): Boolean {
    // 접근성 서비스 관리자
    val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    // 터치 탐색 기능 활성화 여부 반환 (TalkBack 활성화 시 터치 탐색 기능도 활성화됨)
    return accessibilityManager.isTouchExplorationEnabled
}

fun Context.showSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view, message, duration)
        .setBackgroundTint(ContextCompat.getColor(this, R.color.text_secondary))
        .show()
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