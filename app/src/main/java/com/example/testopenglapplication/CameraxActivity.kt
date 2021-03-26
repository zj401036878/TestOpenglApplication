package com.example.testopenglapplication

import android.os.Bundle
import android.view.Surface
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.example.testopenglapplication.databinding.ActivityCameraxBinding
import com.example.testopenglapplication.util.ShaderUtil
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class CameraxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraxBinding

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture

    lateinit var preview:Preview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_camerax)
        setContentView(binding.root)
        initData()
        initListener()
    }


    private fun initData() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
// Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case to display camera preview.
             preview = binding.glcamera.preview
//            preview= Preview.Builder().build()

            // Set up the capture use case to allow users to take photos.
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Choose the camera by requiring a lens facing
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            // Attach use cases to the camera with the same lifecycle owner
            val camera = cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageCapture)



//            val surfaceTexture = binding.glcamera.surfaceTexture
//            val surface = Surface(surfaceTexture)s
//            val executor = Executors.newSingleThreadExecutor()
//            val previewSurfaceProvider = PreviewSurfaceProvider(surface, executor)
//            preview.setSurfaceProvider(executor,previewSurfaceProvider)


        },ContextCompat.getMainExecutor(this))

    }


    private fun initListener() {
            binding.btnTakePic.setOnClickListener {
                var pathStr=this.externalCacheDir!!.toString()+"/abcd.png"
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(File(pathStr)).build()
                imageCapture.takePicture(outputFileOptions,
                    Executors.newSingleThreadExecutor(),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(error: ImageCaptureException)
                        {
                            // insert your code here.
                            ShaderUtil.Logi("onError:${error.message}")
                        }
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            // insert your code here.
                            ShaderUtil.Logi("save:${outputFileResults.savedUri?.path}")
                        }
                    })
            }

        binding.seekThe.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.glcamera.setTTT((progress+10f)/100)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }


    class PreviewSurfaceProvider (private val surface: Surface, private val executor: Executor): Preview.SurfaceProvider {

        override fun onSurfaceRequested(request: SurfaceRequest) {
            request.provideSurface(surface, executor, Consumer { result: SurfaceRequest.Result ->  {

            }})
        }
    }
}