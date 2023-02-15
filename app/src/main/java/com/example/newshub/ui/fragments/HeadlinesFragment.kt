package com.example.newshub.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newshub.MainActivity

import com.example.newshub.R
import com.example.newshub.adapters.NewsAdapter
import com.example.newshub.databinding.FragmentHeadlinesBinding
import com.example.newshub.ui.NewsViewModel
import com.example.newshub.utils.Resource

class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentHeadlinesBinding




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        Log.e("TEST","FINEOK")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_headlines, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initializeViewModel()
        setRecyclerView()
        observeDataNews()

    }


    private fun initializeViewModel() {
        viewModel = (activity as MainActivity).viewModel
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


    private fun observeDataNews() {
        viewModel.headlines.observe(viewLifecycleOwner, Observer { response ->
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

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }


}


