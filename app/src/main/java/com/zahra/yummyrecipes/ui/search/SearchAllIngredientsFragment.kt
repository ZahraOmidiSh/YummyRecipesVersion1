package com.zahra.yummyrecipes.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zahra.yummyrecipes.adapter.AdvancedAllSearchAdapter
import com.zahra.yummyrecipes.databinding.FragmentSearchAllIngredientsBinding
import com.zahra.yummyrecipes.models.search.IngredientsModel
import com.zahra.yummyrecipes.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchAllIngredientsFragment : BottomSheetDialogFragment() {
    //Binding
    private var _binding: FragmentSearchAllIngredientsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var advancedAllSearchAdapter: AdvancedAllSearchAdapter

    //Others
    private lateinit var viewModel: SearchViewModel
    private val args: SearchAllIngredientsFragmentArgs by navArgs()
    private lateinit var selectedIngredientsList: List<IngredientsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchAllIngredientsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Args
            val selectedPosition = arguments?.getInt("selectedPosition", RecyclerView.NO_POSITION)
            if (selectedPosition != null) {
                advancedAllSearchAdapter.updateSelectedItemPosition(selectedPosition)
            }

        //InitViews
        binding.apply {
            //close button
            closeImg.setOnClickListener { findNavController().navigateUp() }
            // Set up RecyclerView
            expandedList.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 4)
                adapter = advancedAllSearchAdapter
                setHasFixedSize(true)
            }
            // Observe and update data
            viewModel.expandedIngredientsList.observe(
                viewLifecycleOwner,
                Observer { expandedIngredients ->
                    advancedAllSearchAdapter.setData(expandedIngredients)
                })
            // Set item click listener
            advancedAllSearchAdapter.setonItemClickListener { ingredientModel ->
                ingredientModel.isSelected = !ingredientModel.isSelected
                viewModel.updateExpandedIngredientByName(
                    ingredientModel.ingredientsName,
                    ingredientModel.isSelected
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}