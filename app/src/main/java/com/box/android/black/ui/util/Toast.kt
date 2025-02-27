package com.box.android.black.ui.util

import android.widget.Toast
import androidx.annotation.StringRes
import com.box.android.black.hmaApp

fun makeToast(@StringRes resId: Int) {
    Toast.makeText(hmaApp, resId, Toast.LENGTH_SHORT).show()
}

fun makeToast(text: CharSequence) {
    Toast.makeText(hmaApp, text, Toast.LENGTH_SHORT).show()
}
