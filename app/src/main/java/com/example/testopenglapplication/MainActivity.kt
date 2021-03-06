package com.example.testopenglapplication

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.example.testopenglapplication.databinding.ActivityMainBinding
import com.example.testopenglapplication.util.BitmapUtil

import com.example.testopenglapplication.util.PermissionUtil
import com.example.testopenglapplication.util.ShaderUtil
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val REQUEST_CODE_ALBUM=11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        checkPermission()
        initListener()
    }

    private fun initListener() {
        binding.seek.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.myglview.setThreshold((progress+10f)/100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        binding.btnHeibai.setOnClickListener {
            binding.myglview.setXiaoGuoType(0)
        }
        binding.btnPic.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent,REQUEST_CODE_ALBUM)
            binding.myglview.setTextureByALBUM(null)
        }
        binding.btnFp.setOnClickListener {
            binding.myglview.setXiaoGuoType(1)
        }
        binding.btnHj.setOnClickListener {
            binding.myglview.setXiaoGuoType(2)
        }
        binding.btnLd.setOnClickListener {
            binding.myglview.setXiaoGuoType(3)
        }
        binding.btnSource.setOnClickListener {
            binding.myglview.setXiaoGuoType(99)
        }

        binding.seekTime.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                binding.myglview.setTimeProgress((progress)/100f)
                binding.myglview.rotatePic(progress*1.8f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })


    ////
        binding.seekVideo.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.myglvideo.setThreshold((progress+10f)/100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })


        binding.btnCamerax.setOnClickListener {
            startActivity(Intent(this@MainActivity,CameraxActivity::class.java))
        }

        binding.btnSave.setOnClickListener {

           var width= binding.myglview.width
            var height= binding.myglview.height
            var path="${externalCacheDir!!.toString()}/saveBitmap.png"
            binding.myglview.saveBitmap(width,height,path)

//            ShaderUtil.Logi("lenth:${bitmap.byteCount}")

        }
    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            PermissionUtil.checkPermission(
                this, permissions, 99
            ) {

            }
        } else {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            99->{
                for (index in grantResults.indices){
                    if(grantResults[index]<0){
                        gotoSetting(permissions[index])
                        break
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun gotoSetting(permissions:String){
        var converPermission=""
        converPermission = when(permissions){
            Manifest.permission.CAMERA->{
                "????????????"
            }
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE->{
                "????????????"
            }
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION->{
                "??????????????????"
            }else->{
                permissions
            }
        }
        Snackbar.make(binding.constraintAll,"???????????????:$converPermission", Snackbar.LENGTH_LONG)
            .setAction("????????????") {
                val intent = Intent()
                try {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    if (Build.VERSION.SDK_INT >= 26) {
                        //8.0??????????????????????????????extra.  >=API 26
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
                    } else if (Build.VERSION.SDK_INT <= 25) {
                        //5.0-7.1 ???????????????extra.  <= API 25, >=API 21
                        intent.putExtra("app_package", packageName)
                        intent.putExtra("app_uid", applicationInfo.uid)
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //?????????????????????????????????????????????????????????APP????????????
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.putExtra("package", packageName)
                    startActivity(intent)
                }
            }.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var uri: Uri? = null
        when(requestCode) {
            REQUEST_CODE_ALBUM -> {
                if (data == null) {
                    return
                }
                uri = data.data
                binding.myglview.setTextureByALBUM(uri)

            }
        }
    }
}