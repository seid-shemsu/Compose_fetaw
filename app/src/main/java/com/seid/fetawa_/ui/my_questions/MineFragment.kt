package com.seid.fetawa_.ui.my_questions

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.compose_test.models.Question
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.ui.components.*
import com.seid.fetawa_.utils.AnimatedShimmer
import com.seid.fetawa_.utils.Constants
import com.seid.fetawa_.utils.Resource

class MineFragment : Fragment() {
    private lateinit var viewModel: MineViewModel

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = MineViewModel()
        return ComposeView(requireContext()).apply {

            setContent {
                var selectedCat = remember { mutableStateOf("All") }
                var loading = remember { mutableStateOf(true) }
                var questions: List<Question> = remember { listOf() }
                Scaffold() {
                    LaunchedEffect(true) {
                        viewModel.getQuestions(context)
                    }

                    viewModel.questionsResponse.collectAsState().value.let {
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                LaunchedEffect(true) {

                                    Log.e("List", "${it.data?.size}")
                                    if (it.data != null)
                                        questions = it.data
                                    loading.value = false
                                }
                            }
                            Resource.Status.ERROR -> {
                                LaunchedEffect(true) {

                                    loading.value = false
                                }
                            }
                            Resource.Status.LOADING -> {
                                LaunchedEffect(true) {
                                    loading.value = true

                                }
                            }
                            Resource.Status.INITIAL -> {
                            }
                        }
                    }
                    Column {
                        Greeting(context = context)
                        TitleComponent(title = "My Questions")
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                            Box(modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(
                                        if (selectedCat.value.equals("All"))
                                            Constants.blue
                                        else
                                            Color.LightGray
                                    )
                                    .clickable {
                                        selectedCat.value = "All"
                                        //homeViewModel.getQuestions(categories[index])
                                    }
                                    .padding(
                                        horizontal = 20.dp,
                                        vertical = 7.dp
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "All",
                                    fontWeight =
                                    if (selectedCat.value.equals("All"))
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium,
                                    color =
                                    if (selectedCat.value.equals("All"))
                                        Color.White
                                    else
                                        Color.Black
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(5.dp)
                                    .background(
                                        Color.LightGray
                                    )
                            )
                            Box(modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (selectedCat.value.equals("Saved"))
                                        Constants.blue
                                    else
                                        Color.LightGray
                                )
                                .clickable {
                                    selectedCat.value = "Saved"
                                    //homeViewModel.getQuestions(categories[index])
                                }
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 7.dp
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Saved",
                                    fontWeight =
                                    if (selectedCat.value.equals("Saved"))
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium,
                                    color =
                                    if (selectedCat.value.equals("Saved"))
                                        Color.White
                                    else
                                        Color.Black
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(5.dp)
                                    .background(
                                        Color.LightGray
                                    )
                            )
                            Box(modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (selectedCat.value.equals("Answered"))
                                        Constants.blue
                                    else
                                        Color.LightGray
                                )
                                .clickable {
                                    selectedCat.value = "Answered"
                                    //homeViewModel.getQuestions(categories[index])
                                }
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 7.dp
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Answered",
                                    fontWeight =
                                    if (selectedCat.value.equals("Answered"))
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium,
                                    color =
                                    if (selectedCat.value.equals("Answered"))
                                        Color.White
                                    else
                                        Color.Black
                                )
                            }
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
                                        MyQuestion(
                                            question = questions[index]
                                        )
                                    }
                                }, modifier = Modifier.padding(horizontal = 20.dp))
                            }

                        }
                    }
                }
            }
        }
    }
}