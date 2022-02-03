package com.example.kotlin2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.example.kotlin2.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {

            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etName.text.toString().trim(){it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please Enter Your Name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(binding.etRegisterEmail.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please Enter Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.etRegisterPassword.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val name: String = binding.etName.text.toString().trim{ it<= ' '}
                    val email: String = binding.etRegisterEmail.text.toString().trim{it <= ' '}
                    val password: String = binding.etRegisterPassword.text.toString().trim { it <= ' ' }

                    //create an instance & a register a user with email and password.
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            //if register is successful
                            if (task.isSuccessful) {
                                addUserToDatabase(name, email, FirebaseAuth.getInstance().currentUser?.uid!!)
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(
                                    this@RegisterActivity,
                                    "You are registered as successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                finish()
                                startActivity(intent)

                            } else {
                                //if not successful then show error
                                Toast.makeText(
                                    this@RegisterActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        FirebaseDatabase.getInstance().getReference().child("user").child(uid).setValue(User(name, email, uid))
    }

}