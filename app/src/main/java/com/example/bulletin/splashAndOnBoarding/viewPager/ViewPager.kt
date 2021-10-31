package com.example.bulletin.splashAndOnBoarding.viewPager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.bulletin.R
import com.example.bulletin.splashAndOnBoarding.screens.Screen1
import com.example.bulletin.splashAndOnBoarding.screens.Screen2
import com.example.bulletin.splashAndOnBoarding.screens.Screen3

class ViewPager : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            Screen1(),
            Screen2(),
            Screen3()
        )

        val adapter = ViewPagerAdapter(fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.findViewById<ViewPager2>(R.id.ViewPager).adapter = adapter

        return view
    }

}