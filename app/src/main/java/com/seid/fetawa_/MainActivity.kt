package com.seid.fetawa_

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import com.seid.fetawa_.databinding.ActivityMainBinding
import com.seid.fetawa_.utils.Constants
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var phone: EditText
    private lateinit var code: EditText
    private lateinit var send_card: CardView
    private lateinit var cancel_card: CardView
    private lateinit var send_text: TextView
    private lateinit var cancel_text: TextView
    private var verificationId: String = ""
    private var mAuth: FirebaseAuth? = null
    private var phone_number = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            if (getSharedPreferences("user", MODE_PRIVATE).getBoolean("auth", false)) {
                /**
                 * QUESTION DIALOG
                 */
            } else {

                /**
                 * LOGIN DIALOG
                 */
                mAuth = FirebaseAuth.getInstance();
                val dialog = Dialog(this)
                dialog.setCancelable(false)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.login_dialog)
                dialog.show()
                phone = dialog.findViewById(R.id.phone)
                code = dialog.findViewById(R.id.code)
                send_card = dialog.findViewById(R.id.send_code_card)
                send_text = dialog.findViewById(R.id.send_code_text)
                cancel_card = dialog.findViewById(R.id.cancel_card)
                cancel_text = dialog.findViewById(R.id.cancel_text)
                cancel_text.setOnClickListener { dialog.dismiss() }
                cancel_card.setOnClickListener { dialog.dismiss() }
                send_card.setOnClickListener { sendCode() }
                send_text.setOnClickListener { sendCode() }
            }
        }

    }

    private fun sendCode() {
        var num = phone.text.toString()
        if (checkPhone(num)) {
            if (num.startsWith("0"))
                num = "+251" + num.substring(1, num.length);
            else if (num.startsWith("251"))
                num = "+" + num;
            phone_number = num.substring(1, num.length);
            sendVerificationCode(num)
            send_text.text = "Sending"
        } else {
            phone.error = "Check Your Input"
        }
    }

    private fun checkPhone(number: String): Boolean {
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
        send_text.text = "Verifying"
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithCredential(credential)
        } catch (e: Exception) {
            e.printStackTrace()
            this.code.setError("Invalid code...")
            send_text.text = "Verify"
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("Users").child(phone_number)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val sp = getSharedPreferences(Constants.SP_USER, MODE_PRIVATE)
                                if (!snapshot.hasChild("phone")) {
                                    FirebaseDatabase.getInstance().getReference("Users")
                                        .child(phone_number)
                                        .child("phone").setValue(phone_number)
                                } else {
                                    sp.edit().putBoolean("auth", true)
                                        .putString("phone", phone_number)
                                        .apply()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                } else {
                    if (task.exception!!.message!!.contains("invalid")) {
                        code.setError("Invalid code...")
                        send_text.text = "Verify"
                    }
                }
            }
    }

    private fun sendVerificationCode(number: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,
            60,
            TimeUnit.SECONDS,
            this,
            mCallBack
        )
    }

    private val mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                code.requestFocus()
                verificationId = s
                code.isEnabled = true
                send_text.text = "Verify"
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val smsCode = phoneAuthCredential.smsCode
                if (smsCode != null) {
                    code.setText(smsCode)
                    verifyCode(smsCode)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
}

