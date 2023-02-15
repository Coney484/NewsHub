package com.example.newshub.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.newshub.MainActivity

import com.example.newshub.R
import com.example.newshub.ui.NewsViewModel

class ArticleScreenFragment : Fragment(R.layout.fragment_article_screen) {

    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewModel()

    }

    private fun initializeViewModel() {
        viewModel = (activity as MainActivity).viewModel
    }

}