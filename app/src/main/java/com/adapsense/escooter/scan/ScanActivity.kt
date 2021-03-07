package com.adapsense.escooter.scan

import android.Manifest
import android.animation.Animator
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.transition.TransitionManager
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.vehicles.view.VehicleDialogFragment
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_scan.*
import kotlinx.android.synthetic.main.navigation.*
import java.util.*

class ScanActivity : BaseActivity(), ScanContract.View {

    private lateinit var scanPresenter: ScanContract.Presenter

    private var codeScanner: CodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        scanPresenter = ScanPresenter(this)
        scanPresenter.start()
        checkCameraPermission()
    }

    override fun onResume() {
        super.onResume()
        if(codeScanner != null && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            codeScanner!!.startPreview()
        }
    }

    override fun onPause() {
        if(codeScanner != null) {
            codeScanner!!.releaseResources()
        }
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }

    override fun setPresenter(presenter: ScanContract.Presenter?) {
        scanPresenter = presenter!!
    }

    override fun setupViews() {

        back.visibility = View.VISIBLE
        back.setOnClickListener { onBackPressed() }

        titleText.text = "Scan Scooter QR"

        codeScanner = CodeScanner(this, codeScannerView)

        codeScanner!!.camera = CodeScanner.CAMERA_BACK
        codeScanner!!.formats = listOf(BarcodeFormat.QR_CODE)
        codeScanner!!.autoFocusMode = AutoFocusMode.SAFE
        codeScanner!!.scanMode = ScanMode.SINGLE
        codeScanner!!.isAutoFocusEnabled = true
        codeScanner!!.isFlashEnabled = false

        codeScanner!!.decodeCallback = DecodeCallback { result ->
            this@ScanActivity.runOnUiThread {
                showProgressDialog()
                scanPresenter.searchVehicle(result.text.toLowerCase(Locale.getDefault()))
            }
        }

        scanStart.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                codeScannerView.visibility = View.VISIBLE
                val animFadeIn = AnimationUtils.loadAnimation(
                    this@ScanActivity,
                    R.anim.fade_in
                )
                animFadeIn.fillBefore = true
                animFadeIn.fillAfter = true
                codeScannerView.startAnimation(animFadeIn)
                codeScanner!!.startPreview()
                val containerConstraintSet = ConstraintSet()
                containerConstraintSet.clone(container)
                val codeConstraintSet = ConstraintSet()
                codeConstraintSet.clone(this@ScanActivity, R.layout.activity_scan_camera)
                TransitionManager.beginDelayedTransition(container)
                codeConstraintSet.applyTo(container)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })

        allowCamera.setOnClickListener {
            checkCameraPermission()
        }

    }

    override fun checkCameraPermission() {
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    showScanStart()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    showScanError()
                }
            })
            .setDeniedMessage("If you reject permission, you cannot scan scooter QR code\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    override fun showScanStart() {
        val containerConstraintSet = ConstraintSet()
        containerConstraintSet.clone(container)
        val startConstraintSet = ConstraintSet()
        startConstraintSet.clone(this, R.layout.activity_scan_start)
        TransitionManager.beginDelayedTransition(container)
        startConstraintSet.applyTo(container)
        scanStart.playAnimation()
    }

    override fun showScanError() {
        val containerConstraintSet = ConstraintSet()
        containerConstraintSet.clone(container)
        val errorConstraintSet = ConstraintSet()
        errorConstraintSet.clone(this, R.layout.activity_scan_error)
        TransitionManager.beginDelayedTransition(container)
        errorConstraintSet.applyTo(container)
    }

    override fun showVehicle(vehicles: LinkedList<Vehicle>, vehicle: Vehicle) {
        if(vehicle.label.isEmpty()) {
            val vehicleDialogFragment = VehicleDialogFragment(settings = false, add = false)
            vehicleDialogFragment.setVehicleTypes(LinkedList())
            vehicleDialogFragment.setVehicleStatuses(LinkedList())
            vehicleDialogFragment.setVehicles(vehicles)
            vehicleDialogFragment.setVehicle(vehicle, false)
            vehicleDialogFragment.isCancelable = false
            vehicleDialogFragment.show(supportFragmentManager, VehicleDialogFragment::class.java.simpleName)
        } else {
            onBackPressed()
        }
    }

    override fun showError(title: String, message: String) {
        if(codeScanner != null) {
            codeScanner!!.startPreview()
        }
        showAlertDialog(title, message)
    }
}