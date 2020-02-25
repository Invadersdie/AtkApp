package com.atk.app.screens.qrcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.util.valueIterator
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.atk.app.R
import com.atk.app.core.base.BaseDataBindingFragment
import com.atk.app.core.base.toolbar.ToolbarConfiguration
import com.atk.app.core.base.toolbar.configuration
import com.atk.app.core.models.ExecuteOnce
import com.atk.app.databinding.QrCodeFragmentBinding
import com.atk.app.screens.SharedViewModel
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class QrCodeFragment : BaseDataBindingFragment<QrCodeFragmentBinding>(R.layout.qr_code_fragment) {

    override val viewModel: QrCodeViewModel by viewModels()
    private val sharedVM: SharedViewModel by activityViewModels()

    private val QrCodeFragmentBinding.toolbar: Toolbar
        get() = toolbarHolder.toolbar

    override fun QrCodeFragmentBinding.onViewCreated(view: View, bundle: Bundle?) {
        vm = viewModel
        initSurfaceView()
        toolbar.configuration =
            ToolbarConfiguration(
                title = "QR code capture",
                homeClick = { activity?.onBackPressed() })
        viewModel.navigateWithQrCode.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is EventOrError.Event -> {
                        sharedVM.capturedQrCode.postValue(ExecuteOnce(it.eventValue))
                        findNavController().popBackStack()
                    }
                    is EventOrError.Error -> showAlert(it.text)
                }
            }
        }
    }

    private fun QrCodeFragmentBinding.initSurfaceView() {
        val barcodeDetector = BarcodeDetector.Builder(surfaceView.context)
            .setBarcodeFormats(Barcode.ALL_FORMATS) // QR_CODE
            .build()
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    val value = barcodes.valueIterator().next().displayValue
                    viewModel.onBarCodeDetected(value)
                }
            }
        })
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}

            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }

                    val width = surfaceView.width
                    val height = width / 4 * 3
                    CameraSource.Builder(requireContext(), barcodeDetector)
                        .setRequestedPreviewSize(width, height)
                        .setAutoFocusEnabled(true)
                        .build()
                        .start(surfaceView.holder)
                } catch (ie: IOException) {
                    Log.e("CAMERA SOURCE", ie.message!!)
                }
            }
        })
    }
}