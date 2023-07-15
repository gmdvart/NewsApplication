package com.example.newsapplication.ui.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NewsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.offset(8, 8)
//        super.getItemOffsets(outRect, view, parent, state)
    }
}