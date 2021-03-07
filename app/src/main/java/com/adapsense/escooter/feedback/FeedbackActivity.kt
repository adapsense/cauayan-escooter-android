package com.adapsense.escooter.feedback

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.adapsense.escooter.R
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.util.AppLogger
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import gun0912.tedbottompicker.TedBottomPicker
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.navigation.*
import java.io.File
import java.io.IOException

class FeedbackActivity : BaseActivity(), FeedbackContract.View {

    private lateinit var feedbackPresenter: FeedbackContract.Presenter

    private var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        feedbackPresenter = FeedbackPresenter(this)
        feedbackPresenter.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right)
    }

    override fun setPresenter(presenter: FeedbackContract.Presenter?) {
        feedbackPresenter = presenter!!
    }

    override fun setupViews() {

        back.visibility = View.VISIBLE

        back.setOnClickListener { onBackPressed() }

        titleText.text = "Send Feedback"

        content.requestFocus()

        uploadChange.setOnClickListener {
            TedPermission.with(this)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        TedBottomPicker.with(this@FeedbackActivity)
                            .show {
                                if(it != null) {
                                    val compressor = Compressor(this@FeedbackActivity)
                                        .setQuality(50)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    try {
                                        imageFile = compressor.compressToFile(File(it.path!!))
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        AppLogger.error(e.message)
                                    }
                                }
                                Glide.with(this@FeedbackActivity).load(it)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .apply(RequestOptions.centerInsideTransform())
                                    .listener(object : RequestListener<Drawable?> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            progress.visibility = View.GONE
                                            uploadChange.text = "Upload Image"
                                            uploadChange.visibility = View.VISIBLE
                                            delete.visibility = View.GONE
                                            image.visibility = View.GONE
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            progress.visibility = View.GONE
                                            uploadChange.text = "Change Image"
                                            uploadChange.visibility = View.VISIBLE
                                            delete.visibility = View.VISIBLE
                                            image.visibility = View.VISIBLE
                                            return false
                                        }

                                    }).into(image)
                            }
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                    }
                })
                .setDeniedMessage("If you reject permission, you cannot upload an image\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check()
        }

        delete.setOnClickListener {
            imageFile = null
            progress.visibility = View.GONE
            uploadChange.text = "Upload Image"
            uploadChange.visibility = View.VISIBLE
            delete.visibility = View.GONE
            image.visibility = View.GONE
        }

        send.setOnClickListener {
            showProgressDialog()
            feedbackPresenter.submit(content.text.toString().trim(), imageFile)
        }

    }

    override fun showMessage(message: String) {
        showAlertDialog("", message, "Ok") { dialog, _ ->
            dialog.dismiss()
            onBackPressed()
        }
    }

    override fun showError(error: String) {
        showAlertDialog("", error)
    }

}