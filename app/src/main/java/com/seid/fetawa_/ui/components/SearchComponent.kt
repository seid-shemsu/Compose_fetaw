package com.seid.fetawa_.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seid.fetawa_.utils.Constants.blue

@Composable
fun SearchComponent() {
    val text = remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.85f)
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            OutlinedTextField(
                value = text.value,
                onValueChange = { text.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        "",
                        tint = Color.Black
                    )

                },
                placeholder = {
                    Text("Search")
                },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = blue,
                    backgroundColor = Color.White,
                    textColor = Color.Black,
                    cursorColor = blue,
                )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 5.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(blue),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = com.seid.fetawa_.R.drawable.ic_baseline_filter_alt_24),
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}