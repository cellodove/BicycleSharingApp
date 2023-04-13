package com.cellodove.presentation.ui.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.cellodove.domain.data.DomainAddresses
import com.cellodove.presentation.R
import com.cellodove.presentation.base.BaseFragment
import com.cellodove.presentation.databinding.FragmentAddressSearchBinding
import com.cellodove.presentation.ui.main.MainActivity.Companion.PATH_STATUS
import com.cellodove.presentation.ui.main.MainActivity.Companion.X_VALUE
import com.cellodove.presentation.ui.main.MainActivity.Companion.Y_VALUE
import com.cellodove.presentation.ui.main.MainViewModel
import com.cellodove.presentation.util.decideOnState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentAddressSearchBinding>(FragmentAddressSearchBinding::inflate){

    private val viewModel : MainViewModel by activityViewModels()
    private val searchAdapter = SearchAdapter()
    private var searchJob: Job? = null
    private var oldQuery = ""
    private var newQuery = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi(){
        val pathStatus = arguments?.getString(PATH_STATUS) ?: ""

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
                val bundle = Bundle()
                bundle.putString(PATH_STATUS , pathStatus)
                bundle.putDouble(X_VALUE , domainAddresses.x)
                bundle.putDouble(Y_VALUE ,domainAddresses.y)
                findNavController().navigate(R.id.fragment_main_map,bundle)
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

    private fun statusList() {
        searchAdapter.addLoadStateListener { loadState ->
            loadState.decideOnState(
                adapter = searchAdapter,
                showLoading = { visible ->
                    binding.progressBar.isVisible = visible
                    binding.searchNothing.isVisible = false
                    binding.errorLayout.isVisible = false
                },
                showEmptyState = { visible ->
                    binding.searchNothing.isVisible = visible
                    binding.errorLayout.isVisible = false
                },
                showError = {
                    binding.searchRecycler.visibility = View.INVISIBLE
                    binding.searchNothing.isVisible = false
                    binding.errorLayout.isVisible = true
                }
            )
        }
    }

    override fun observeViewModel() {
        statusList()
    }

    private fun hideKeyboard(){
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.etQuery.windowToken, 0)
    }
}