package com.app.helpoome

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_request.*

class RequestActivity : AppCompatActivity() {
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val multiplePermissionsCode = 100
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onPermission()

        photoButton.setOnClickListener {
            selectGallery()
        }

        apply.setOnClickListener {
            if (titleInput.text.isEmpty()) {
                Toast.makeText(applicationContext, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (textInput.text.isEmpty()) {
                Toast.makeText(applicationContext, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (image == null) {
                Toast.makeText(applicationContext, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPermission() {
        var rejectedPermissionList = ArrayList<String>()
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED
            ) {
                rejectedPermissionList.add(permission)
            }
        }

        if (rejectedPermissionList.isNotEmpty()) {
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this,
                rejectedPermissionList.toArray(array),
                multiplePermissionsCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            multiplePermissionsCode -> {
                if (grantResults.isNotEmpty()) {
                    for ((i, permission) in permissions.withIndex()) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun selectGallery() {
        var intent = Intent(Intent.ACTION_PICK)

        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                var currentImageUrl = data?.data

                try {
                    image = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
                    apply.setBackgroundColor(Color.parseColor("#FF676767"))
                    photoButton.setImageBitmap(image)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}