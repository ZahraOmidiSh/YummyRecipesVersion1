package com.zahra.yummyrecipes.ui.recipe

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.todkars.shimmer.ShimmerRecyclerView
import com.zahra.yummyrecipes.R
import com.zahra.yummyrecipes.adapter.SuggestedAdapter
import com.zahra.yummyrecipes.databinding.FragmentRecipeBinding
import com.zahra.yummyrecipes.utils.NetworkRequest
import com.zahra.yummyrecipes.utils.setupRecyclerview
import com.zahra.yummyrecipes.utils.showSnackBar
import com.zahra.yummyrecipes.viewmodel.RecipeViewModel
import com.zahra.yummyrecipes.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class RecipeFragment : Fragment() {
    //Binding
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var suggestedAdapter: SuggestedAdapter

    //other
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //InitViews

        //Show Greeting
        lifecycleScope.launch { showGreeting() }
        //Meal Type Suggestion
        lifecycleScope.launch {showMealType() }
        //Call api
        recipeViewModel.callSuggestedApi(recipeViewModel.suggestedQueries())
        //Load data
        loadSuggestedData()


    }

    private fun loadSuggestedData() {
        recipeViewModel.suggestedData.observe(viewLifecycleOwner) { response ->
            binding.apply {
                when (response) {
                    is NetworkRequest.Loading -> {
                        setupLoading(true, suggestedList)
                    }

                    is NetworkRequest.Success -> {
                        setupLoading(false, suggestedList)
                        response.data?.let { data ->
                            if (data.results!!.isNotEmpty()) {
                                suggestedAdapter.setData(data.results)
                                initSuggestedRecycler()
                            }
                        }
                    }
                    is NetworkRequest.Error -> {
                        setupLoading(false, suggestedList)
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    private fun initSuggestedRecycler() {
        binding.suggestedList.setupRecyclerview(
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false),
            suggestedAdapter
        )
        //Click
        suggestedAdapter.setonItemClickListener {
            //Go to detail page
        }

    }

    private fun setupLoading(isShownLoading: Boolean, shimmer: ShimmerRecyclerView) {
        shimmer.apply {
            if (isShownLoading) showShimmer() else hideShimmer()
        }
    }

    @SuppressLint("SetTextI18n")
    fun showMealType() {
        binding.SuggestedTxt.text = getMealType()
    }
    @SuppressLint("SetTextI18n")
    fun showGreeting() {
        binding.usernameTxt.text = "${getGreeting()} ${getEmojiByUnicode()}"
        recipeViewModel.getSlogan()
        recipeViewModel.slogan.observe(viewLifecycleOwner) {
            binding.sloganTxt.text = it
        }
    }

    private fun getMealType(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 7..11 -> getString(R.string.breakfast_time)
            in 11..15 -> getString(R.string.lunch_time)
            in 15..19 -> getString(R.string.snack_time)
            else -> getString(R.string.dinner_time)
        }
    }

    private fun getGreeting(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 7..12 -> getString(R.string.goodMorning)
            in 12..17 -> getString(R.string.goodAfternoon)
            in 17..20 -> getString(R.string.goodEvening)
            else -> getString(R.string.goodNight)
        }
    }

    private fun getEmojiByUnicode(): String {
        return String(Character.toChars(0x1f44b))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}