package com.seid.fetawa_.ui.components

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.R
import com.seid.fetawa_.activities.DetailActivity
import com.seid.fetawa_.utils.Constants
import com.seid.fetawa_.utils.Constants.blue
import com.seid.fetawa_.utils.DateFormatter
import java.io.Serializable

@Composable
fun MyQuestion(context: Context, question: Question) {
    Spacer(modifier = Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(blue)
            .padding(top = 15.dp)
            .clickable {
                var intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("object", question as Serializable)
                context.startActivity(intent)
            }
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            if (question.category.isNotEmpty()) {

                Row(
                    modifier = Modifier.padding(start = 10.dp,end = 10.dp, bottom = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn, "", tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        question.category, color = Color.White, fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                question.question,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                modifier = Modifier.padding(horizontal = 15.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.history),
                    "",
                    modifier = Modifier.size(17.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Posted ${
                        DateFormatter.getMoment(
                            question.askedDate
                        )
                    }", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Constants.white
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}