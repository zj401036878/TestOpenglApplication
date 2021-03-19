package com.example.testopenglapplication

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.testopenglapplication.databinding.ActivityMainBinding
import com.example.testopenglapplication.util.PermissionUtil
import com.example.testopenglapplication.util.ShaderUtil
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
                binding.myglview.setTimeProgress((progress)/100f)
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
                "相机访问"
            }
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE->{
                "存储访问"
            }
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION->{
                "获取位置信息"
            }else->{
                permissions
            }
        }
        Snackbar.make(binding.constraintAll,"权限被拒绝:$converPermission", Snackbar.LENGTH_LONG)
            .setAction("跳转设置") {
                val intent = Intent()
                try {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    if (Build.VERSION.SDK_INT >= 26) {
                        //8.0及以后版本使用这两个extra.  >=API 26
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
                    } else if (Build.VERSION.SDK_INT <= 25) {
                        //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
                        intent.putExtra("app_package", packageName)
                        intent.putExtra("app_uid", applicationInfo.uid)
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //其他低版本或者异常情况，走该节点。进入APP设置界面
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.putExtra("package", packageName)
                    startActivity(intent)
                }
            }.show()

    }
}