package com.seid.fetawa_.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.R
import com.seid.fetawa_.activities.DetailActivity
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.utils.Constants
import com.seid.fetawa_.utils.Constants.HOME_SCREEN
import com.seid.fetawa_.utils.DateFormatter
import java.io.Serializable

@Composable
fun QuestionComponent(
    context: Context,
    question: Question,
    db: DB,
    screen: String,
    function: () -> Unit
) {
    //val favorite = remember { mutableStateOf(db.isFav(question.id)) }
    Spacer(modifier = Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.question_bg),
            contentDescription = "",
        )
        Column(horizontalAlignment = Alignment.Start) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.male),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(50.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .padding(vertical = 5.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        question.askedBy.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "By - ${question.answeredBy.name}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(.4f))
                    .padding(
                        horizontal = 10.dp,
                        vertical = 3.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    "",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    question.category,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                question.question,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                modifier = Modifier.padding(15.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Constants.light_gray)
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.history),
                        "",
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Posted ${
                            DateFormatter.getMoment(
                                question.askedDate
                            )
                        }",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Constants.black
                    )
                }
                Icon(
                    if (screen == HOME_SCREEN) {
                        /*if (favorite.value) Icons.Filled.Favorite else*/ Icons.Filled.FavoriteBorder
                    } else {
                        Icons.Filled.Delete
                    },
                    "",
                    tint = if (screen == HOME_SCREEN) Color.Red else Color.Black,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clickable {
                            /*if (screen == HOME_SCREEN) {
                                if (favorite.value)
                                    db.removeQuestion(question.id)
                                else
                                    db.addQuestion(question)
                                favorite.value = !favorite.value
                            } else {
                                db.removeQuestion(question.id)
                                function()
                            }*/
                        }
                )
            }
        }
        Row(
            modifier = Modifier
                .width(80.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color.Black)
                .align(Alignment.TopEnd)
                .clickable {
                    var intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("object", question as Serializable)
                    context.startActivity(intent)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "view",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_call_made_24),
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 3.dp),
                contentDescription = ""
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun AnimatedListItem(
    item: String,
    onItemClick: (String) -> Unit
) {
    var removing by remember { mutableStateOf(false) }
    val slideAnim by animateFloatAsState(
        targetValue = if (removing) -1000f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(bottom = 8.dp)
            .graphicsLayer(translationX = slideAnim)
            .clickable { onItemClick(item) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item,
            style = MaterialTheme.typography.body1,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { removing = true },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }
}