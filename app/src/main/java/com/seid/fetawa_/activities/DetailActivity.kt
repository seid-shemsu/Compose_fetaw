package com.seid.fetawa_.activities

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.seid.fetawa_.R
import com.seid.fetawa_.activities.ui.theme.Fetawa_Theme
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.utils.Constants
import com.seid.fetawa_.utils.Constants.blue
import com.seid.fetawa_.utils.Constants.white
import com.seid.fetawa_.utils.DateFormatter
import com.seid.fetawa_.utils.Utils
import java.io.File
import java.util.concurrent.TimeUnit


class DetailActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var pdp: MutableState<Int>
    private lateinit var isDownloading: MutableState<Boolean>
    private lateinit var question: Question
    lateinit var start_time: MutableState<String>
    lateinit var end_time: MutableState<String>
    lateinit var final_time: MutableState<Int>
    lateinit var current_time: MutableState<Int>
    lateinit var progress: MutableState<Int>
    lateinit var category: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        question = intent.extras?.getSerializable("object") as Question
        val db = DB(this.applicationContext)
        val root = Environment.getExternalStorageDirectory().toString()
        val dir = File("$root/fetawa")
        val file = File(dir, question.id + ".ogg")
        setContent {
            Fetawa_Theme {
                // A surface container using the 'background' color from the theme
                val favorite = remember { mutableStateOf(db.isFav(question.id)) }
                Log.e(
                    "Init",
                    "--------------------------------------------------------------------"
                )
                progress = remember { mutableStateOf(0) }
                pdp = remember { mutableStateOf(0) }
                start_time = remember { mutableStateOf("") }
                end_time = remember { mutableStateOf("") }
                final_time = remember { mutableStateOf(0) }
                current_time = remember { mutableStateOf(0) }
                isDownloading = remember { mutableStateOf(false) }
                LaunchedEffect(true) {
                    if (file.exists() && file.length() > 0)
                        pdp.value = 1
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(white)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 20.dp)
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            "",
                            modifier = Modifier
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                ) {
                                    onBackPressed()
                                }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Question Detail",
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp, bottom = 25.dp)
                            .clip(RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp))
                            .background(blue)
                    ) {
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
                                        question.user ?: "",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "By - ${question.answered_by}",
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
                                    (question.category
                                        ?: ""),
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
                                    .padding(vertical = 10.dp),
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
                                                question.posted_date?.toLong()
                                            )
                                        }",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Constants.black
                                    )
                                }
                                Icon(
                                    if (favorite.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    "",
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .clickable {
                                            if (favorite.value)
                                                db.removeQuestion(question.id)
                                            else
                                                db.addQuestion(question)
                                            favorite.value = !favorite.value
                                        }
                                )
                            }
                        }
                    }
                    Text(
                        "Question Description",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                    )
                    Text(
                        question.question,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Start,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 25.dp)
                    )
                    Text(
                        "Answer",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                    )
                    if (question.answer != null) {
                        if ((question.answer ?: "").startsWith("http")) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(blue.copy(.1f))
                            ) {

                                LinearProgressIndicator(
                                    progress = (progress.value / 100.0).toFloat(),
                                    color = blue,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        start_time.value,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_baseline_fast_rewind_24),
                                            contentDescription = "rewind",
                                            modifier = Modifier
                                                .size(45.dp)
                                                .border(2.dp, Color.LightGray, CircleShape)
                                                .padding(7.dp)
                                                .clickable {
                                                    if (mediaPlayer != null) {
                                                        progress.value -= 10
                                                        mediaPlayer?.seekTo(mediaPlayer?.currentPosition!! - 10 * 1000)
                                                    }
                                                }
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Box(contentAlignment = Alignment.Center) {
                                            Image(
                                                painter = painterResource(id = if (pdp.value == 1) R.drawable.ic_baseline_play_arrow_24 else if (pdp.value == 2) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_download_24),
                                                contentDescription = "rewind",
                                                modifier = Modifier
                                                    .size(65.dp)
                                                    .border(2.dp, Color.LightGray, CircleShape)
                                                    .padding(10.dp)
                                                    .clickable {
                                                        if (Utils.isPermissionGranted(
                                                                applicationContext
                                                            )
                                                        ) {
                                                            if (mediaPlayer != null) {
                                                                if (mediaPlayer?.isPlaying == true) {
                                                                    mediaPlayer?.pause();
                                                                    pdp.value = 1
                                                                } else {
                                                                    mediaPlayer?.start();
                                                                    pdp.value = 2
                                                                }
                                                            } else {
                                                                if (file.isFile && file.length() > 0) {
                                                                    play();
                                                                } else
                                                                    download();
                                                            }
                                                        } else {
                                                            askPermission();
                                                        }
                                                    }
                                            )
                                            this@Row.AnimatedVisibility(visible = isDownloading.value) {
                                                CircularProgressIndicator(
                                                    color = blue,
                                                    strokeWidth = 2.dp,
                                                    modifier = Modifier.size(65.dp)
                                                )
                                            }
                                            CircularProgressIndicator(
                                                progress = (progress.value / 100.0).toFloat(),
                                                color = blue,
                                                strokeWidth = 3.dp,
                                                modifier = Modifier.size(65.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_baseline_fast_forward_24),
                                            contentDescription = "rewind",
                                            modifier = Modifier
                                                .size(45.dp)
                                                .border(2.dp, Color.LightGray, CircleShape)
                                                .padding(7.dp)
                                                .clickable {
                                                    if (mediaPlayer != null) {
                                                        progress.value += 10
                                                        mediaPlayer?.seekTo(mediaPlayer?.currentPosition!! + 10 * 1000)
                                                    }
                                                }
                                        )

                                    }
                                    Text(
                                        end_time.value,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        } else {
                            Text(
                                question.answer + question.answer,
                                color = Color.Black,
                                textAlign = TextAlign.Start,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(
                                    start = 15.dp,
                                    end = 15.dp,
                                    bottom = 10.dp
                                )
                            )
                        }
                    }

                }
            }
        }
    }

    private fun askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1000
            )
        }
    }

    private fun download() {
        try {
            val root = Environment.getExternalStorageDirectory().toString()
            val dir = File("$root/fetawa")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, question.id + ".ogg")
            isDownloading.value = true
            val storageReference =
                FirebaseStorage.getInstance().getReference("answered")
                    .child("1627797710786")//.child(question.id)
            file.createNewFile()
            try {
                storageReference.getFile(file)
                    .addOnSuccessListener { taskSnapshot: FileDownloadTask.TaskSnapshot? -> play() }
                    .addOnFailureListener { e: Exception? ->
                        isDownloading.value = false
                        Toast.makeText(this@DetailActivity, e?.message, Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_SHORT).show()
                isDownloading.value = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isDownloading.value = false
            Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun play() {
        isDownloading.value = false
        pdp.value = 2
        mediaPlayer = null
        mediaPlayer = MediaPlayer()
        try {
            val root = Environment.getExternalStorageDirectory().toString()
            val dir = File("$root/fetawa")
            val file = File(dir, question.id + ".ogg")
            mediaPlayer?.setDataSource(file.toString())
            mediaPlayer?.prepare()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
        Utils.stop()
        Utils.Player(mediaPlayer)
        val finalTime = mediaPlayer!!.duration
        val startTime = mediaPlayer!!.currentPosition
        end_time.value = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
        )

        start_time.value = (
                String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
                )
                )
        handler.post(updateSeekbar)
    }

    var handler: Handler = Handler()
    private val updateSeekbar: Runnable = object : Runnable {
        override fun run() {
            val startTime = mediaPlayer!!.currentPosition
            start_time.value = (
                    String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
                    )
                    )
            if (mediaPlayer != null) {
                progress.value =
                    (mediaPlayer!!.currentPosition * 100.0 / mediaPlayer!!.duration).toInt()
            }
            //progress.value = startTime

            Log.e("Proogress", "----------------------------------------------- ${progress.value}")
            if (progress.value == 99) {
                Log.e("Player", "----------------------------------------------- ENDED")
                handler.removeCallbacks(this)
                pdp.value = 1
                mediaPlayer = null
            } else
                handler.postDelayed(this, 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                download()
            }
        }
    }
}