package com.example.ocr

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract.Helpers.insert
import android.view.SurfaceView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class ScannerActivity : AppCompatActivity() {
    val extractValue:String?=""
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val  CAMERA_RQ = 102
    private lateinit var inputImageBtn: MaterialButton
    private lateinit var recognizeTextBtn: MaterialButton
    private lateinit var imageIv :ImageView
    private lateinit var recognizedTextEt :EditText
    private var imageUri : Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner2)

        inputImageBtn = findViewById(R.id.inputImageBtn)
        recognizeTextBtn = findViewById(R.id.recognizeTextBtn)
        imageIv=findViewById(R.id.imageiv)
        recognizedTextEt = findViewById(R.id.recognizedTextEt)

        inputImageBtn.setOnClickListener{
//            checkOnPermission(android.Manifest.permission.CAMERA,"Camera",CAMERA_RQ)
            Toast.makeText(this,"test",Toast.LENGTH_SHORT).show()
        }

    }
    private fun cameraLauncher(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Sample Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        cameraActivityResultLauncher.launch(intent)
    }
    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode== Activity.RESULT_OK){
                imageIv.setImageURI(imageUri)
            }else{
                Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
            }
        }
    private fun  checkOnPermission(permission:String,name:String,requestCode:Int){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(applicationContext,permission)== PackageManager.PERMISSION_GRANTED ->{
                    Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()
                    cameraLauncher()

                }
                shouldShowRequestPermissionRationale(permission)-> showDialog(permission,name,requestCode)
                else -> ActivityCompat.requestPermissions(this, arrayOf(permission),requestCode)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name:String){
            if (grantResults.isEmpty()|| grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"$name permission refused",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()
                cameraLauncher()
            }
        }
        when(requestCode){
            CAMERA_RQ -> innerCheck("Camera")
        }
    }
    private fun showDialog(permission: String,name: String,requestCode: Int){
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission Required")
            setPositiveButton("OK"){dialog,which ->
                ActivityCompat.requestPermissions(this@ScannerActivity,arrayOf(permission),requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }


}