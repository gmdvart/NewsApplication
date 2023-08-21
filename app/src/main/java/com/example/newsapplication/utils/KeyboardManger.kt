package com.example.newsapplication.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardManger(private val context: Context) {
    fun hideKeyboard(view: View?) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}