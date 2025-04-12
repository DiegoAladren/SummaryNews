package com.example.summarynews.ui.noticias

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NewsCategoryAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    internal val categories = listOf("Todo", "Politica", "Deportes", "Tecnología", "Salud", "Ciencia")

    override fun getItemCount() = categories.size

    override fun createFragment(position: Int): Fragment {
        val category = categories[position]
        return NewsListFragment.newInstance(category)
    }
}
