package com.bogdandovgopol.weather.Extensions

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class RecyclerViewVerticalSpacingDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.right = spaceSize
        outRect.left = spaceSize
    }
}