package com.zahra.yummyrecipes.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zahra.yummyrecipes.adapter.AdvancedSearchAdapter
import com.zahra.yummyrecipes.databinding.FragmentSearchBinding
import com.zahra.yummyrecipes.models.search.IngredientsModel
import com.zahra.yummyrecipes.ui.recipe.RecipeFragmentDirections
import com.zahra.yummyrecipes.utils.setupRecyclerview
import com.zahra.yummyrecipes.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {
    //Binding
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var advancedSearchAdapter: AdvancedSearchAdapter

    //Others
    private val viewModel: SearchViewModel by viewModels()
    private val searchIngredientsList: MutableList<IngredientsModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //InitViews
        binding.apply {
            //load data
            viewModel.loadLimitIngredientsList()
            viewModel.limitIngredientsList.observe(viewLifecycleOwner) {
                if (searchIngredientsList.size == 0) {
                    searchIngredientsList.clear()
                    searchIngredientsList.addAll(it)
                    advancedSearchAdapter.setData(searchIngredientsList)
                }
                //RecyclerView setup
                ingredientsList.setupRecyclerview(
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    ),
                    advancedSearchAdapter
                )
                viewAllSearchByIngredients.setOnClickListener {
                    val direction = SearchFragmentDirections.actionToSearchAllIngredients()
                    findNavController().navigate(direction)
                }

                //Click on items
                advancedSearchAdapter.setonItemClickListener {ingredientName ->
                    val action = RecipeFragmentDirections.actionToSearchAllIngredients(ingredientName)
                    findNavController().navigate(action)
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}