package com.example.kotlin2

//import android.util.Log
//import android.webkit.MimeTypeMap
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kotlin2.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private val PICK_IMAGE_REQUEST:Int = 2022

    private var filePath: Uri? = null
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getAction = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback{result: Uri? ->
                binding.userImage.setImageURI(result)
        })

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        mDbRef = FirebaseDatabase.getInstance().getReference("user").child(firebaseUser.uid)
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        mDbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.txtName.text = user!!.name

                if (user.userImage == null){
                    binding.userImage.setImageResource(R.drawable.profile_image_default)
                }else{
                    Glide.with(this@ProfileActivity).load(user.userImage).into(binding.userImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message, Toast.LENGTH_SHORT).show()
            }

        })


        binding.imageProfile.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.userImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getAction.launch("image/*")
            } else {
                //  Request Permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 2022
                )
            }
        }

        binding.btnSave?.setOnClickListener {
            Log.d("test", "buttonerhn jj")
            //   binding.progressBar?.visibility = View.VISIBLE
            if (filePath != null) {
                binding.progressBar?.visibility = View.VISIBLE
                // val imageExtension = MimeTypeMap.getSingleton()
                //   .getExtensionFromMimeType(contentResolver.getType(filePath))
                //val stRef: StorageReference = FirebaseStorage.getInstance().reference.child("images" + System.currentTimeMillis()+ "."+imageExtension)
                val ref = storageRef.child("images/$firebaseUser")
                ref.putFile(filePath!!)
                    .addOnSuccessListener {
                        binding.progressBar?.visibility = View.GONE
                        Toast.makeText(applicationContext, "Successfully uploaded", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener {
                        // binding.progressBar?.visibility = View.GONE
                        Toast.makeText(applicationContext,
                            "Failed"+it.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }

            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val binding = ActivityProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null){
//            filePath = data!!.data
//            try {
//                var bitmap:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
//                binding.userImage.setImageBitmap(bitmap)
//            }catch (e:IOException){
//                e.printStackTrace()
//            }
//        }
//    }

}