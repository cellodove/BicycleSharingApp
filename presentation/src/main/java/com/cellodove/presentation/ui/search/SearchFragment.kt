package com.cellodove.presentation.ui.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.cellodove.domain.data.DomainAddresses
import com.cellodove.presentation.base.BaseFragment
import com.cellodove.presentation.databinding.FragmentAddressSearchBinding
import com.cellodove.presentation.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentAddressSearchBinding>(FragmentAddressSearchBinding::inflate){

    private val viewModel : SearchViewModel by activityViewModels()
    private val searchAdapter = SearchAdapter()
    private var searchJob: Job? = null
    private var oldQuery = ""
    private var newQuery = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi(){
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.searchRecycler.adapter = searchAdapter
        binding.searchRecycler.addItemDecoration(decoration)

        binding.etQuery.setOnEditorActionListener { _, actionId, _ ->
            newQuery = binding.etQuery.text.toString()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                when {
                    binding.etQuery.text.toString().isEmpty() -> {
                        binding.inputLayout.error = "텍스트를 입력해 주세요."
                    }
                    newQuery == oldQuery -> Unit
                    else -> {
                        oldQuery = newQuery
                        hideKeyboard()
                        binding.inputLayout.error = null
                        searchAddress(binding.etQuery.text.toString())
                        binding.searchRecycler.scrollToPosition(0)
                    }
                }
                true
            } else {
                false
            }
        }

        searchAdapter.setItemClickListener(object : SearchAdapter.OnItemClickListener{
            override fun onClick(domainAddresses: DomainAddresses) {

            }
        })

        binding.retryButton.setOnClickListener {
            searchAdapter.retry()
        }
    }

    private fun searchAddress(query :String){
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchAddress(query).collectLatest {
                binding.searchNothing.isVisible = false
                binding.errorLayout.isVisible = false
                binding.searchRecycler.visibility = View.VISIBLE
                searchAdapter.submitData(it)
            }
        }
    }

    override fun observeViewModel() {

    }

    private fun hideKeyboard(){
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.etQuery.windowToken, 0)
    }
}