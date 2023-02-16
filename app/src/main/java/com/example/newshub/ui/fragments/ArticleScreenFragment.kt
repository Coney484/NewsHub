package com.example.newshub.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newshub.MainActivity

import com.example.newshub.R
import com.example.newshub.databinding.FragmentArticleScreenBinding
import com.example.newshub.databinding.FragmentHeadlinesBinding
import com.example.newshub.ui.NewsViewModel

class ArticleScreenFragment : Fragment(R.layout.fragment_article_screen) {

    lateinit var viewModel: NewsViewModel
    val args: ArticleScreenFragmentArgs by navArgs()
    lateinit var binding: FragmentArticleScreenBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        Log.e("TEST", "FINEOK")
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_article_screen, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewModel()
        onClickToOpenWebView()

    }

    private fun initializeViewModel() {
        viewModel = (activity as MainActivity).viewModel
    }


    private fun onClickToOpenWebView() {
        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
    }

}