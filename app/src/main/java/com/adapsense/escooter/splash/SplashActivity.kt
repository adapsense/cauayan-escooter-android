package com.adapsense.escooter.splash

import android.Manifest
import android.animation.Animator
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.transition.TransitionManager
import com.adapsense.escooter.R
import com.adapsense.escooter.auth.AuthActivity
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.home.HomeActivity
import com.adapsense.escooter.receivers.NetworkChangedBroadcastReceiver
import com.adapsense.escooter.util.CacheUtil
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity : BaseActivity(), SplashContract.View {

    private lateinit var splashPresenter: SplashContract.Presenter

    private var codeScanner: CodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splashPresenter = SplashPresenter(this)
        splashPresenter.start()
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
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun setPresenter(presenter: SplashContract.Presenter?) {
        splashPresenter = presenter!!
    }

    override fun setupViews() {

        codeScanner = CodeScanner(this, codeScannerView)

        codeScanner!!.camera = CodeScanner.CAMERA_BACK
        codeScanner!!.formats = listOf(BarcodeFormat.QR_CODE)
        codeScanner!!.autoFocusMode = AutoFocusMode.SAFE
        codeScanner!!.scanMode = ScanMode.SINGLE
        codeScanner!!.isAutoFocusEnabled = true
        codeScanner!!.isFlashEnabled = false

        codeScanner!!.decodeCallback = DecodeCallback { result ->
            this@SplashActivity.runOnUiThread {
                showProgressDialog()
                splashPresenter.searchVehicle(result.text.toLowerCase(Locale.getDefault()))
            }
        }

        progress.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                image.visibility = View.VISIBLE
                val animFadeIn = AnimationUtils.loadAnimation(
                    this@SplashActivity,
                    R.anim.fade_in
                )
                animFadeIn.fillBefore = true
                animFadeIn.fillAfter = true
                animFadeIn.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {

                        if(CacheUtil.instance!!.user != null) {

                            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                            finish()

                        } else {

                            if (!isNetworkConnected()) {
                                showAlertDialog(
                                    "No Internet Connection",
                                    "Connect to the internet to continue."
                                )
                                val receiver = NetworkChangedBroadcastReceiver()
                                receiver.callback = object :
                                    NetworkChangedBroadcastReceiver.OnNetworkChangedCallback {
                                    override fun onNetworkChanged() {
                                        if (isNetworkConnected()) {
                                            unregisterReceiver(receiver)
                                            initializeScan()
                                        }
                                    }
                                }
                                registerReceiver(
                                    receiver,
                                    IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
                                )
                            } else {
                                initializeScan()
                            }
                        }
                    }

                    override fun onAnimationStart(animation: Animation?) {

                    }

                })
                image.startAnimation(animFadeIn)

            }

        })

        scanStart.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                codeScannerView.visibility = View.VISIBLE
                val animFadeIn = AnimationUtils.loadAnimation(
                    this@SplashActivity,
                    R.anim.fade_in
                )
                animFadeIn.fillBefore = true
                animFadeIn.fillAfter = true
                codeScannerView.startAnimation(animFadeIn)
                codeScanner!!.startPreview()
                val containerConstraintSet = ConstraintSet()
                containerConstraintSet.clone(container)
                val codeConstraintSet = ConstraintSet()
                codeConstraintSet.clone(this@SplashActivity, R.layout.activity_splash_camera)
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

        loginAsAdmin.setOnClickListener {
            val intent = Intent(this@SplashActivity, AuthActivity::class.java)
            intent.putExtra("login", true)
            intent.putExtra("rider", false)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

    }

    override fun initializeScan() {
        dismissDialogs()
        Handler().postDelayed(Runnable {
            if(CacheUtil.instance!!.user != null) {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                finish()
            } else {
                checkCameraPermission()
            }
        }, 2000)
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
        startConstraintSet.clone(this, R.layout.activity_splash_start)
        TransitionManager.beginDelayedTransition(container)
        startConstraintSet.applyTo(container)
        scanStart.playAnimation()
    }

    override fun showScanError() {
        val containerConstraintSet = ConstraintSet()
        containerConstraintSet.clone(container)
        val errorConstraintSet = ConstraintSet()
        errorConstraintSet.clone(this, R.layout.activity_splash_error)
        TransitionManager.beginDelayedTransition(container)
        errorConstraintSet.applyTo(container)
    }

    override fun showAuth(login: Boolean) {
        dismissDialogs()
        val intent = Intent(this@SplashActivity, AuthActivity::class.java)
        intent.putExtra("login", login)
        intent.putExtra("rider", true)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    override fun showError(title: String, message: String) {
        if(codeScanner != null) {
            codeScanner!!.startPreview()
        }
        showAlertDialog(title, message)
    }

}