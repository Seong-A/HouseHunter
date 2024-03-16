package com.example.househunter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide

class PhotoPagerAdapter(private val photoUrlList: List<String>) : PagerAdapter() {

    override fun getCount(): Int {
        return photoUrlList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.photo_item_layout, container, false)

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        Glide.with(context)
            .load(photoUrlList[position])
            .into(imageView)

        container.addView(view)

        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}