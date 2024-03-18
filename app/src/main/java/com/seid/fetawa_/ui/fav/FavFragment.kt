package com.seid.fetawa_.ui.fav

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.ui.components.QuestionComponent
import com.seid.fetawa_.ui.components.SearchComponent
import com.seid.fetawa_.ui.components.TitleComponent
import com.seid.fetawa_.ui.home.HomeViewModel
import com.seid.fetawa_.utils.Constants.FAV_SCREEN
import com.seid.fetawa_.utils.Resource

class FavFragment : Fragment() {

    companion object {
        fun newInstance() = FavFragment()
    }

    private lateinit var viewModel: FavViewModel

    @OptIn(ExperimentalAnimationApi::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {

                var questions: List<Question> = remember { listOf() }
                homeViewModel.favResponse.collectAsState().value.let {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            if (it.data != null)
                                questions = it.data
                        }

                        else -> {}
                    }
                }
                val db = DB(context)
                LaunchedEffect(true) {
                    homeViewModel.setupDb(requireContext())
                    homeViewModel.getFavorites()
                }
                Scaffold {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TitleComponent(title = "Favorite Questions")
                        Spacer(modifier = Modifier.height(10.dp))
                        // SearchComponent()
                        // Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn(content = {
                            items(questions) { question ->
                                AnimatedContent(targetState = question,
                                    modifier = Modifier // Add sliding animation
                                        .padding(vertical = 8.dp),
                                    transitionSpec = {
                                        slideInHorizontally(
                                            initialOffsetX = { -it }, // Set initial offset for entering content
                                            animationSpec = tween(durationMillis = 300) // Set duration for animation
                                        ) with slideOutHorizontally(
                                            targetOffsetX = { it }, // Set target offset for exiting content
                                            animationSpec = tween(durationMillis = 300) // Set duration for animation
                                        )
                                    }) {
                                    QuestionComponent(
                                        homeViewModel,
                                        context,
                                        question = question,
                                        db = db,
                                        FAV_SCREEN
                                    ) {
                                        questions = questions.filterNot { it == question }
                                            .toMutableList()
                                    }
                                }

                            }
                        }, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
            }
        }
    }
}