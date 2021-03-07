package com.adapsense.escooter.base

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.adapsense.escooter.R

open class BaseActivity: AppCompatActivity() {

    companion object {
        var alertDialog: AlertDialog? = null
        var progressDialog: AlertDialog? = null
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onPause() {
        super.onPause()
        dismissDialogs()
    }

    @Suppress("DEPRECATION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkConnected(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
    }

    fun dismissDialogs() {
        if(alertDialog != null) {
            alertDialog!!.dismiss()
        }
        if(progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    fun showProgressDialog() {
        dismissDialogs()
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        val customLayout: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        builder.setView(customLayout)

        progressDialog = builder.create()
        progressDialog!!.show()

    }

    open fun showAlertDialog(
        title: String?,
        message: String?
    ) {
        dismissDialogs()
        val builder =
            AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        alertDialog = builder.create()
        alertDialog!!.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Ok"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog!!.setOnShowListener(OnShowListener {
            alertDialog!!.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        })
        alertDialog!!.show()
    }

    open fun showAlertDialog(
        title: String?,
        message: String?,
        neutral: String?,
        neutralOnClickListener: DialogInterface.OnClickListener?
    ) {
        dismissDialogs()
        val builder =
            AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        alertDialog = builder.create()
        alertDialog!!.setButton(
            AlertDialog.BUTTON_POSITIVE,
            neutral,
            neutralOnClickListener
        )
        alertDialog!!.setOnShowListener(OnShowListener {
            alertDialog!!.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        })
        alertDialog!!.show()
    }

    open fun showAlertDialog(
        title: String?,
        message: String?,
        positive: String,
        positiveOnClickListener: DialogInterface.OnClickListener?,
        negative: String,
        negativeOnClickListener: DialogInterface.OnClickListener?
    ) {
        dismissDialogs()
        val builder =
            AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        alertDialog = builder.create()
        if (positive != "") {
            alertDialog!!.setButton(
                AlertDialog.BUTTON_POSITIVE,
                positive,
                positiveOnClickListener
            )
        }
        if (negative != "") {
            alertDialog!!.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                negative,
                negativeOnClickListener
            )
        }
        alertDialog!!.setOnShowListener(OnShowListener {
            if (positive != "") {
                alertDialog!!.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
            }
            if (negative != "") {
                alertDialog!!.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
                alertDialog!!.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this@BaseActivity, R.color.divider))
            }
        })
        alertDialog!!.show()
    }

    open fun showProgressDialog(message: String?) {
        dismissDialogs()
        /*val builder: SpotsDialog.Builder = Builder().setContext(this).setTheme(R.style.SpotsDialog)
        builder.setCancelable(true)
        builder.setMessage(message)
        progressDialog = builder.build()
        progressDialog.show()*/
    }
}
