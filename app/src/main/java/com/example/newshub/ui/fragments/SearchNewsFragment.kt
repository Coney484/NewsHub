package com.example.newshub.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newshub.MainActivity
import com.example.newshub.R
import com.example.newshub.adapters.NewsAdapter
import com.example.newshub.databinding.FragmentSearchNewsBinding
import com.example.newshub.ui.NewsViewModel
import com.example.newshub.utils.Constants
import com.example.newshub.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    lateinit var binding: FragmentSearchNewsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        Log.e("TEST", "FINEOK")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_news, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewModel()
        setRecyclerView()
        observeSearchData()
        onlickHandlersOnArticle()

    }

    private fun initializeViewModel() {
        viewModel = (activity as MainActivity).viewModel
    }

    private fun observeSearchData() {
        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsheadlines ->
                        newsAdapter.differ.submitList(newsheadlines.articles)
                    }

                }

                is Resource.Failure -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("Headlines Failed", "An error Occured: $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })

    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun onlickHandlersOnArticle() {
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articlesFragment,
                bundle
            )
        }
    }




}