package com.example.bulletin.splashAndOnBoarding.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.bulletin.R


class Screen4 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen4, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.ViewPager)

        view.findViewById<TextView>(R.id.NextScn4).setOnClickListener {
            findNavController().navigate(R.id.action_viewPager_to_breakingNewsFragment)
            onBoardingFinished()
        }

        view.findViewById<TextView>(R.id.BackScn4).setOnClickListener {
            viewPager?.currentItem = 2
        }

        return view
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("finished", true)
        editor.apply()
    }

}