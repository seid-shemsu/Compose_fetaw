package com.seid.fetawa_

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
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
        progress = dialog.findViewById(R.id.progress)
        cancel_card = dialog.findViewById(R.id.cancel_card)
        cancel_text = dialog.findViewById(R.id.cancel_text)
        cancel_text.setOnClickListener {
            current_action = "send"
            dialog.dismiss()
        }
        cancel_card.setOnClickListener {
            current_action = "send"
            dialog.dismiss()
        }

        send_card = dialog.findViewById(R.id.send_card)
        send_text = dialog.findViewById(R.id.send_text)
        question_text_input = dialog.findViewById(R.id.question)
        send_card.setOnClickListener {
            if (question_text_input.text.toString().length > 15) {
                sendQuestion(question_text_input.text.toString())
            } else {
                question_text_input.error = "Use more words."
            }
        }
        send_text.setOnClickListener {
            if (question_text_input.text.toString().length > 15) {
                sendQuestion(question_text_input.text.toString())
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
        progress = dialog.findViewById(R.id.progress)
        name_input = dialog.findViewById(R.id.name_input)
        name = dialog.findViewById(R.id.name)
        send_card = dialog.findViewById(R.id.send_code_card)
        send_text = dialog.findViewById(R.id.send_code_text)
        cancel_card = dialog.findViewById(R.id.cancel_card)
        cancel_text = dialog.findViewById(R.id.cancel_text)
        cancel_card.setOnClickListener {
            dialog.dismiss()
        }
        send_card.setOnClickListener {
            save()
        }
    }

    private fun sendQuestion(q: String) {
        progressing(true)
        val id =  FirebaseDatabase.getInstance().reference.push().key ?: UUID.randomUUID().toString()
        val question = Question()
        question.uuid = id
        question.askedBy = user
        question.askedDate = System.currentTimeMillis()
        question.question = q

        FirebaseDatabase.getInstance().getReference("questions").child(id).setValue(question)
        FirebaseDatabase.getInstance().getReference("users_questions").child(user.uuid).child(id)
            .setValue(question).addOnSuccessListener {
                progressing(false)
                Toast.makeText(this, "Question asked Successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
    }

    private fun save() {
        if (!name.text.isNullOrEmpty()) {
            progressing(true)
            val uuid = UUID.randomUUID().toString()
            user = User(name = name.text.toString(), uuid = uuid)
            Thread {
                DB(this).dbDao().insertUser(user)

            }.start()
            FirebaseDatabase.getInstance().getReference("Users").child(uuid).setValue(user)
                .addOnSuccessListener {
                    progressing(false)
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    ask()
                }
        } else {
            Log.e("Name", name.text.toString())
            Toast.makeText(this, "Check your input", Toast.LENGTH_SHORT).show()
        }

    }

    /*private fun sendCode() {
        var num = phone.text.toString()
        if (checkPhone(num)) {
            progressing(true)
            if (num.startsWith("0"))
                num = "+251" + num.substring(1, num.length);
            else if (num.startsWith("251"))
                num = "+" + num;
            phone.isEnabled = false
            phone_number = num.substring(1, num.length);
            sendVerificationCode(num)
        } else {
            phone.error = "Check Your Input"
        }
    }*/

    private fun progressing(value: Boolean) {
        if (value) {
            progress.visibility = View.VISIBLE
            send_text.visibility = View.GONE
            cancel_card.isEnabled = false
        } else {
            progress.visibility = View.GONE
            send_text.visibility = View.VISIBLE
        }
        cancel_card.isEnabled = !value
        cancel_text.isEnabled = !value
    }

    private fun checkPhone(number: String): Boolean {
        if (number.length < 10) return false
        if (number.startsWith("0")) {
            if (number.length != 10) return false
        }
        if (number.startsWith("+251")) {
            if (number.length != 13) return false
        }
        if (number.startsWith("251")) if (number.length != 12) return false
        return true
    }

    private fun verifyCode(code: String) {
        try {
            progressing(true)
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithCredential(credential)
        } catch (e: Exception) {
            e.printStackTrace()
            //this.code.setError("Invalid code...")
            progressing(false)
            send_text.text = "Verify"
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult?> ->
            if (task.isSuccessful) {
                FirebaseDatabase.getInstance().getReference("Users").child(phone_number)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val sp = getSharedPreferences(Constants.SP_USER, MODE_PRIVATE)
                            if (!snapshot.hasChild("phone")) {
                                progressing(false)
                                send_text.text = "Save"
                                //phone_input.visibility = View.GONE
                                // code_input.visibility = View.GONE
                                name_input.visibility = View.VISIBLE
                                //email_input.visibility = View.VISIBLE
                                current_action = "save"
                            } else {
                                sp.edit().putBoolean("auth", true).putString(
                                    "phone", snapshot.child("phone").value as String?
                                ).putString(
                                    "email", snapshot.child("email").value as String?
                                ).putString("name", snapshot.child("name").value as String?).apply()
                                dialog.dismiss()
                                ask()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            } else {
                if (task.exception!!.message!!.contains("invalid")) {
                    //code.setError("Invalid code...")
                    send_text.text = "Verify"
                    progressing(false)
                }
            }
        }
    }

    private fun sendVerificationCode(number: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number, 60, TimeUnit.SECONDS, this, mCallBack
        )
    }

    private val mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                //code.requestFocus()
                verificationId = s
                //code_input.isEnabled = true
                send_text.text = "Verify"
                current_action = "verify"
                progressing(false)
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val smsCode = phoneAuthCredential.smsCode
                if (smsCode != null) {
                    //code.setText(smsCode)
                    verifyCode(smsCode)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                send_text.text = "Send Code"
                //code_input.isEnabled = false
                progressing(false)
            }
        }
}

