package com.seid.fetawa_

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seid.fetawa_.admob.AdMob
import com.seid.fetawa_.databinding.ActivityMainBinding
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.models.User
import com.seid.fetawa_.utils.Constants
import com.seid.fetawa_.utils.SpUtils
import com.seid.fetawa_.utils.Utils
import java.util.UUID
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var name: EditText
    private lateinit var send_card: CardView
    private lateinit var cancel_card: CardView
    private lateinit var send_text: TextView
    private lateinit var cancel_text: TextView
    private lateinit var name_input: TextInputLayout
    private lateinit var question_text_input: EditText
    private lateinit var progress: ProgressBar
    private lateinit var dialog: Dialog
    private var verificationId: String = ""
    private var mAuth: FirebaseAuth? = null
    private var phone_number = ""
    private var current_action = "send"
    private var user = User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Thread {
            user = DB(this).dbDao().getUser() ?: User()
            Log.e("TAG", "--------------------------------------------")
            Log.e("TAG", "$user")
        }.start()
        MobileAds.initialize(this)
        AdMob.getInstance(this)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_mine,
                R.id.navigation_about
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.fab.setOnClickListener {
            if (user.uuid.isNotEmpty()) {
                ask()
            } else {
                login()
            }
        }
        checkUpdate()
    }

    private fun checkUpdate() {
        FirebaseDatabase.getInstance().getReference("version")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChildren()) return
                    val value = snapshot.child("version").value as String
                    if (Utils.compareVersion(value, Constants.VERSION)) {
                        dialog = Dialog(this@MainActivity)
                        dialog.setCancelable(false)
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.update_dialog)
                        dialog.show()
                        dialog.findViewById<CardView>(R.id.update_card).setOnClickListener {

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun ask() {
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ask_dialog)
        dialog.show()
        val save = dialog.findViewById<TextView>(R.id.save)
        val submit = dialog.findViewById<TextView>(R.id.submit)
        val progress = dialog.findViewById<ProgressBar>(R.id.progress)
        val close = dialog.findViewById<ImageView>(R.id.close)
        val question = dialog.findViewById<EditText>(R.id.question)
        val draft = SpUtils.getDraft(this)
        question.setText(draft)

        close.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener {
            SpUtils.saveDraft(this, question.text.toString())
            dialog.dismiss()
        }
        submit.setOnClickListener {
            if (question.text.toString().length > 15) {
                save.isEnabled = false
                progress.visibility = View.VISIBLE
                submit.visibility = View.GONE
                SpUtils.saveDraft(this, "")
                sendQuestion(question.text.toString())
            } else {
                question_text_input.error = "Use more words."
            }
        }

    }

    private fun login() {
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.login_dialog)
        dialog.show()
        val cancel = dialog.findViewById<TextView>(R.id.cancel)
        val submit = dialog.findViewById<TextView>(R.id.submit)
        val progress = dialog.findViewById<ProgressBar>(R.id.progress)
        val close = dialog.findViewById<ImageView>(R.id.close)
        val name = dialog.findViewById<EditText>(R.id.question)

        close.setOnClickListener { dialog.dismiss() }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        submit.setOnClickListener {
            if (name.text.toString().isNotEmpty()) {
                cancel.isEnabled = false
                progress.visibility = View.VISIBLE
                submit.visibility = View.GONE
                save()
            } else {
                question_text_input.error = ""
            }
        }
    }

    private fun sendQuestion(q: String) {
        val id = FirebaseDatabase.getInstance().reference.push().key ?: UUID.randomUUID().toString()
        val question = Question()
        question.uuid = id
        question.askedBy = user
        question.askedDate = System.currentTimeMillis()
        question.question = q

        FirebaseDatabase.getInstance().getReference("questions").child(id).setValue(question)
        FirebaseDatabase.getInstance().getReference("users_questions").child(user.uuid).child(id)
            .setValue(question).addOnSuccessListener {
                Toast.makeText(this, "Question asked Successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
    }

    private fun save() {
        val uuid = UUID.randomUUID().toString()
        user = User(name = name.text.toString(), uuid = uuid)
        Thread {
            DB(this).dbDao().insertUser(user)

        }.start()
        FirebaseDatabase.getInstance().getReference("Users").child(uuid).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                ask()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
    }
}

