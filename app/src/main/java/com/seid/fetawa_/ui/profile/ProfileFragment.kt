package com.seid.fetawa_.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.seid.fetawa_.R
import com.seid.fetawa_.utils.SPUtils

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Scaffold {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color(0xFFEAEAEA))
                                .padding(15.dp)
                        ) {
                            Text(
                                "Profile",
                                color = Color.Black,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.TopStart)
                            )
                            Icon(
                                Icons.Filled.MoreVert,
                                "",
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.male),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(130.dp)
                                    .align(Alignment.Center)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter),
                                verticalAlignment = CenterVertically
                            ) {
                                Text(
                                    SPUtils.getName(context).replaceFirstChar { it.uppercase() },
                                    fontSize = 30.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Image(
                                    painterResource(id = com.seid.fetawa_.R.drawable.edit),
                                    "",
                                    modifier = Modifier
                                        .size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        RowElement(
                            icon = R.drawable.ic_baseline_phone_24,
                            title = "Phone Number",
                            value = SPUtils.getPhone(context)
                        ) {

                        }

                        RowElement(
                            icon = R.drawable.ic_baseline_mail_24,
                            title = "Email",
                            value = SPUtils.getEmail(context)
                        ) {

                        }

                        RowElement(
                            icon = R.drawable.ic_baseline_info_24,
                            title = "About Us",
                            value = null
                        ) {

                        }

                        RowElement(
                            icon = R.drawable.ic_baseline_delete_24,
                            title = "Delete Account",
                            value = null
                        ) {

                        }

                        RowElement(
                            icon = R.drawable.ic_baseline_exit_to_app_24,
                            title = "Log out",
                            value = null,
                        ) {

                            FirebaseAuth.getInstance().signOut()
                            SPUtils.signOut(context)
                            activity?.finish()
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun RowElement(icon: Int, title: String, value: String?, function: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color.White)
            .padding(horizontal = 25.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { function() },
        verticalAlignment = CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            "",
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                title,
                fontSize = 19.sp,
                color = if (title.equals("log out", true)) Color.Red else Color.Black,
                fontWeight = FontWeight.Medium
            )
            value?.let {
                Text(
                    value,
                    fontSize = 17.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}