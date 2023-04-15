package com.seid.fetawa_.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.ui.components.Greeting
import com.seid.fetawa_.ui.components.QuestionComponent
import com.seid.fetawa_.ui.components.SearchComponent
import com.seid.fetawa_.ui.components.TitleComponent
import com.seid.fetawa_.utils.AnimatedShimmer
import com.seid.fetawa_.utils.Constants.HOME_SCREEN
import com.seid.fetawa_.utils.Constants.blue
import com.seid.fetawa_.utils.Resource

class HomeFragment : Fragment() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                var questions: List<Question> = remember { listOf() }
                var categories: List<String> = remember { listOf() }
                var loading = remember { mutableStateOf(true) }
                var catLoad = remember { mutableStateOf(false) }
                val selectedCat = homeViewModel.category.collectAsState()
                val db = DB(context)

                homeViewModel.questionsResponse.collectAsState().value.let {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            Log.e("List", "${it.data?.size}")
                            if (it.data != null)
                                questions = it.data
                            loading.value = false

                        }
                        Resource.Status.ERROR -> {
                            loading.value = false

                        }
                        Resource.Status.LOADING -> {
                            loading.value = true
                        }
                        Resource.Status.INITIAL -> {
                        }
                    }
                }
                homeViewModel.categoryResponse.collectAsState().value.let {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            Log.e("Cat List", "${it.data}")
                            if (it.data != null)
                                categories = it.data
                            catLoad.value = false
                        }
                        Resource.Status.ERROR -> {
                            catLoad.value = false
                        }
                        Resource.Status.LOADING -> {
                            catLoad.value = true
                        }
                        Resource.Status.INITIAL -> {
                            catLoad.value = false
                        }
                    }
                }
                MaterialTheme {
                    Scaffold {
                        Column(
                            modifier = Modifier
                                .background(Color.White)
                                .fillMaxSize()
                        ) {
                            Greeting(context)
                            TitleComponent(title = "Find answers")
                            AnimatedVisibility(visible = catLoad.value) {
                                val configuration = LocalConfiguration.current
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp)
                                        .height(100.dp),
                                ) {
                                    AnimatedShimmer(
                                        height = 135.dp,
                                        width = (configuration.screenWidthDp * 0.99).dp,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                }
                            }
                            AnimatedVisibility(visible = !catLoad.value) {
                                LazyRow(
                                    content = {
                                        items(categories.size) { index ->
                                            Row(verticalAlignment = CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(100.dp))
                                                        .background(
                                                            if (selectedCat.value.equals(
                                                                    categories[index],
                                                                    ignoreCase = true
                                                                )
                                                            )
                                                                blue
                                                            else
                                                                Color.LightGray
                                                        )
                                                        .clickable {
                                                            homeViewModel.category.value =
                                                                categories[index]
                                                            homeViewModel.getQuestions()
                                                            Log.e("Cats", "${catLoad.value}")
                                                        }
                                                        .padding(
                                                            horizontal = 20.dp,
                                                            vertical = 7.dp
                                                        ),
                                                    contentAlignment = Center
                                                ) {
                                                    Text(
                                                        categories[index],
                                                        fontWeight =
                                                        if (selectedCat.value.equals(
                                                                categories[index],
                                                                ignoreCase = true
                                                            )
                                                        )
                                                            FontWeight.Bold
                                                        else
                                                            FontWeight.Medium,
                                                        color =
                                                        if (selectedCat.value.equals(
                                                                categories[index],
                                                                ignoreCase = true
                                                            )
                                                        )
                                                            Color.White
                                                        else
                                                            Color.Black
                                                    )
                                                }
                                                if (index != categories.size - 1) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(12.dp)
                                                            .height(5.dp)
                                                            .background(
                                                                Color.LightGray
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(start = 20.dp, bottom = 5.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            AnimatedVisibility(loading.value) {
                                val configuration = LocalConfiguration.current
                                Column() {
                                    Row(
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp)
                                            .height(220.dp)
                                            .padding(top = 5.dp),
                                    ) {
                                        AnimatedShimmer(
                                            height = 135.dp,
                                            width = (configuration.screenWidthDp * 0.99).dp,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                    }
                                }

                            }
                            AnimatedVisibility(!loading.value) {
                                Column {
                                    SearchComponent()
                                    Spacer(modifier = Modifier.height(5.dp))
                                    LazyColumn(content = {
                                        items(questions.size) { index ->
                                            QuestionComponent(
                                                context = context,
                                                question = questions[index],
                                                db = db,
                                                HOME_SCREEN
                                            ) {

                                            }
                                        }
                                    }, modifier = Modifier.padding(horizontal = 20.dp))
                                }
                            }
                            Log.e("Category", "${catLoad.value}")
                            Log.e("Question", "${loading.value}")
                        }

                    }
                }
            }
        }
    }
}