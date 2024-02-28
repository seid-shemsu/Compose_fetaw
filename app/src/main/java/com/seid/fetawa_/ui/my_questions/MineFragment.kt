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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.models.User
import com.seid.fetawa_.ui.components.*
import com.seid.fetawa_.utils.AnimatedShimmer
import com.seid.fetawa_.utils.Constants
import com.seid.fetawa_.utils.Resource
import kotlinx.coroutines.runBlocking

class MineFragment : Fragment() {
    private lateinit var viewModel: MineViewModel
    private lateinit var user: User
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = MineViewModel()
        runBlocking {
            Thread {
                user = DB(requireContext()).dbDao().getUser() ?: User()
            }.start()
        }
        return ComposeView(requireContext()).apply {

            setContent {
                var selectedCat by remember { mutableStateOf("All") }
                var loading by remember { mutableStateOf(true) }
                var questions by remember { mutableStateOf(listOf<Question>()) }
                Scaffold() {
                    LaunchedEffect(true) {
                        viewModel.getQuestions(context, user.uuid)
                    }

                    viewModel.questionsResponse.collectAsState().value.let {
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                LaunchedEffect(it.data) {
                                    Log.e("TAG", "----------${it}")
                                    questions=(it.data ?: listOf())
                                    loading = false
                                }
                            }
                            Resource.Status.ERROR -> {
                                LaunchedEffect(true) {

                                    loading = false
                                }
                            }
                            Resource.Status.LOADING -> {
                                LaunchedEffect(true) {
                                    loading = true

                                }
                            }
                            Resource.Status.INITIAL -> {
                            }
                        }
                    }
                    Column {
                        Greeting(context = context)
                        TitleComponent(title = "My Questions")
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)) {
                            Box(modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (selectedCat.equals("All"))
                                        Constants.blue
                                    else
                                        Color.LightGray
                                )
                                .clickable {
                                    selectedCat = "All"
                                    viewModel.getList(1)
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
                                    if (selectedCat.equals("All"))
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium,
                                    color =
                                    if (selectedCat.equals("All"))
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
                                    if (selectedCat == "Pending")
                                        Constants.blue
                                    else
                                        Color.LightGray
                                )
                                .clickable {
                                    selectedCat = "Pending"
                                    viewModel.getList(1)
                                }
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 7.dp
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Pending",
                                    fontWeight =
                                    if (selectedCat.equals("Pending"))
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium,
                                    color =
                                    if (selectedCat.equals("Pending"))
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
                                    if (selectedCat == "Answered")
                                        Constants.blue
                                    else
                                        Color.LightGray
                                )
                                .clickable {
                                    selectedCat = "Answered"
                                    viewModel.getList(2)
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
                                    if (selectedCat.equals("Answered"))
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium,
                                    color =
                                    if (selectedCat.equals("Answered"))
                                        Color.White
                                    else
                                        Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        AnimatedVisibility(loading) {
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
                        AnimatedVisibility(!loading) {
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