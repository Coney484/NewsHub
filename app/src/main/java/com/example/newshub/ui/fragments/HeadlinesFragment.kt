package com.example.newshub.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.newshub.MainActivity

import com.example.newshub.R
import com.example.newshub.adapters.NewsAdapter
import com.example.newshub.databinding.FragmentHeadlinesBinding
import com.example.newshub.ui.NewsViewModel
import com.example.newshub.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newshub.utils.Resource

class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentHeadlinesBinding
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        Log.e("TEST", "FINEOK")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_headlines, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initializeViewModel()
        setRecyclerView()
        observeDataNews()
        onlickHandlersOnArticle()

    }


    private fun initializeViewModel() {
        viewModel = (activity as MainActivity).viewModel
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlinesFragment.handlePagination())
        }
    }


    private fun observeDataNews() {
        viewModel.headlines.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsheadlines ->
                        newsAdapter.differ.submitList(newsheadlines.articles.toList())
                        val totalPagesCount = newsheadlines.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.headlinesPage == totalPagesCount
                        if (isLastPage) {
                            binding.rvHeadlines.setPadding(0, 0, 0, 0)
                        }
                    }

                }

                is Resource.Failure -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(
                            activity,
                            "Some Error Occurred :$message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


    private fun onlickHandlersOnArticle() {
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_headlinesFragment_to_articlesFragment,
                bundle
            )
        }
    }


    private fun handlePagination(): RecyclerView.OnScrollListener {
        val scrollListner = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount

                val isNotAtFirst = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

                val toPaginate =
                    isNotLoadingAndNotLastPage && isAtLastItem && isNotAtFirst && isTotalMoreThanVisible && isScrolling
                if (toPaginate) {
                    viewModel.getHeadlines("in")
                    isScrolling = false
                }


            }
        }
        return scrollListner
    }


}


