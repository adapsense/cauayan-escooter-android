package com.adapsense.escooter.vehicles.view

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.adapsense.escooter.BuildConfig
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.*
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.dashboard.DashboardFragment
import com.adapsense.escooter.home.HomeActivity
import com.adapsense.escooter.scan.ScanActivity
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.adapsense.escooter.util.ViewUtil
import com.adapsense.escooter.vehicles.VehiclesContract
import com.adapsense.escooter.vehicles.VehiclesPresenter
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
import kotlinx.android.synthetic.main.fragment_dialog_vehicle.*
import java.io.File
import java.io.IOException
import java.util.*

class VehicleDialogFragment(private val settings: Boolean, private val add: Boolean) : DialogFragment(), VehiclesContract.View {

    private lateinit var vehiclesPresenter: VehiclesContract.Presenter

    private var imageFile: File? = null

    companion object {

        @JvmStatic
        fun newInstance(settings: Boolean, add: Boolean) = VehicleDialogFragment(settings, add)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_vehicle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setupViews()
        if(!add) {
            showVehicle(vehiclesPresenter.getVehicle())
        }
    }

    override fun setPresenter(presenter: VehiclesContract.Presenter?) {
        vehiclesPresenter = presenter!!
    }

    override fun setupViews() {

        if(!settings && !add) {
            title.text = "Name your Scooter"
            labelTextInputLayout.hint = null
            imageContainer.visibility = View.GONE
        } else {
            if(!add) {
                title.text = "Edit Scooter"
                progress.visibility = View.VISIBLE
                uploadChangeDeleteContainer.visibility = View.GONE
                saveAdd.text = "Save"
            } else {
                title.text = "Add Scooter"
                labelTextInputEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
                labelTextInputEditText.nextFocusForwardId = R.id.serialNumberTextInputEditText
                labelTextInputEditText.requestFocus()
                addContainer.visibility = View.VISIBLE
                progress.visibility = View.GONE
                uploadChangeDeleteContainer.visibility = View.VISIBLE
                val layoutParams = image.layoutParams
                layoutParams.width = ViewUtil.dpToPx(activity!!, 100)
                layoutParams.height = ViewUtil.dpToPx(activity!!, 100)
                image.layoutParams = layoutParams
                saveAdd.text = "Add"
            }
            labelTextInputLayout.hint = "Name"
            imageContainer.visibility = View.VISIBLE
            image.visibility = View.GONE
        }

        labelTextInputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveAdd.performClick()
                val inputMethodManager =
                    activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(labelTextInputEditText.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        modelTextInputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveAdd.performClick()
                val inputMethodManager =
                    activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(modelTextInputEditText.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        uploadChange.setOnClickListener {
            TedPermission.with(activity)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        TedBottomPicker.with(activity)
                            .show {
                                if (it != null) {
                                    val compressor = Compressor(activity)
                                        .setQuality(50)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    try {
                                        imageFile = compressor.compressToFile(File(it.path!!))
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        AppLogger.error(e.message)
                                    }
                                }
                                Glide.with(activity!!).load(it)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .apply(RequestOptions.centerInsideTransform())
                                    .listener(object : RequestListener<Drawable?> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            uploadChangeDeleteContainer.visibility = View.VISIBLE
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
                                            uploadChangeDeleteContainer.visibility = View.VISIBLE
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
            uploadChangeDeleteContainer.visibility = View.VISIBLE
            progress.visibility = View.GONE
            uploadChange.text = "Upload Image"
            uploadChange.visibility = View.VISIBLE
            delete.visibility = View.GONE
            image.visibility = View.GONE
        }

        cancel.visibility = if(settings || add) View.VISIBLE else View.GONE
        cancel.setOnClickListener {
            dismiss()
        }

        saveAdd.setOnClickListener {
            (activity as BaseActivity).showProgressDialog()
            if(!add) {
                uploadChangeDeleteContainer.visibility = View.GONE
                progress.visibility = View.VISIBLE
                image.visibility = View.GONE
                vehiclesPresenter.updateVehicle(
                    settings,
                    labelTextInputEditText.text.toString().trim(),
                    delete.visibility == View.GONE,
                    imageFile
                )
            } else {
                vehiclesPresenter.addVehicle(
                    labelTextInputEditText.text.toString().trim(),
                    serialNumberTextInputEditText.text.toString().trim(),
                    brandTextInputEditText.text.toString().trim(),
                    modelTextInputEditText.text.toString().trim(),
                    imageFile
                )
            }
        }

    }

    override fun setVehicleTypes(vehicleTypes: LinkedList<VehicleType>) {
        vehiclesPresenter = VehiclesPresenter(this)
        vehiclesPresenter.setVehicleTypes(vehicleTypes)
    }

    override fun setVehicleStatuses(vehicleStatuses: LinkedList<VehicleStatus>) {
        vehiclesPresenter.setVehicleStatuses(vehicleStatuses)
    }

    override fun setVehicles(vehicles: LinkedList<Vehicle>) {
        vehiclesPresenter.setVehicles(vehicles)
    }

    override fun showVehicles(vehicles: LinkedList<Vehicle>) {
        dismiss()
        when(parentFragment) {
            is VehiclesFragment -> {
                (parentFragment as VehiclesFragment).setVehicles(vehicles)
                (parentFragment as VehiclesFragment).showVehicles(vehicles)
            }
        }
        (activity as BaseActivity).dismissDialogs()
    }

    override fun showVehicleLogs(vehicleLogs: HashMap<String, VehicleLog>) {

    }

    override fun setVehicle(vehicle: Vehicle, selected: Boolean) {
        vehiclesPresenter.setVehicle(vehicle)
    }

    override fun showVehicle(vehicle: Vehicle?) {
        if(vehicle != null) {
            var label: String
            if (!CacheUtil.instance!!.user!!.userType.isAdmin) {
                if (vehicle.label.isEmpty()) {
                    label = "My Scooter"
                    if (vehiclesPresenter.getVehiclesCount() > 1) {
                        label += " ${vehiclesPresenter.getVehiclesCount()}"
                    }
                } else {
                    label = vehicle.label
                }
            } else {
                label = vehicle.name
            }
            labelTextInputEditText.setText("")
            labelTextInputEditText.append(label)
            labelTextInputEditText.requestFocus()

            if (CacheUtil.instance!!.user!!.userType.isAdmin) {
                val statusAdapter: ArrayAdapter<VehicleStatus> = ArrayAdapter<VehicleStatus>(
                    activity!!,
                    R.layout.spinner,
                    vehiclesPresenter.getVehicleStatuses()
                )
                statusAdapter.setDropDownViewResource(R.layout.item_spinner)
                status.adapter = statusAdapter
                status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        vehiclesPresenter.setVehicleStatus(position)
                        showRider(vehiclesPresenter.getVehicle())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
                status.setSelection(vehiclesPresenter.getVehicleStatus(vehicle))
                statusContainer.visibility = View.VISIBLE

                showRider(vehicle)

            }

            when {
                imageFile != null -> {
                    uploadChangeDeleteContainer.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    uploadChange.text = "Change Image"
                    uploadChange.visibility = View.VISIBLE
                    delete.visibility = View.VISIBLE
                    image.visibility = View.VISIBLE
                    image.setImageBitmap(BitmapFactory.decodeFile(imageFile!!.absolutePath))
                }
                vehicle.image.isNotEmpty() -> {
                    uploadChange.text = "Change Image"
                    delete.visibility = View.VISIBLE
                    Glide.with(activity!!)
                        .load(BuildConfig.UPLOADS_URL + vehiclesPresenter.getVehicle()!!.image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions.centerInsideTransform())
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                uploadChangeDeleteContainer.visibility = View.VISIBLE
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
                                uploadChangeDeleteContainer.visibility = View.VISIBLE
                                progress.visibility = View.GONE
                                uploadChange.text = "Change Image"
                                uploadChange.visibility = View.VISIBLE
                                delete.visibility = View.VISIBLE
                                image.visibility = View.VISIBLE
                                return false
                            }

                        }).into(image)
                }
                else -> {
                    uploadChangeDeleteContainer.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    uploadChange.text = "Upload Image"
                    uploadChange.visibility = View.VISIBLE
                    delete.visibility = View.GONE
                    image.visibility = View.GONE
                }
            }
        }
    }

    override fun selectVehicle(position: Int, vehicle: Vehicle) {

    }

    override fun deleteVehicle(vehicle: Vehicle) {

    }

    override fun showVehicleLog(fromMqtt: Boolean, vehicleLog: VehicleLog) {

    }

    override fun showLockVehicle(locked: Boolean) {

    }

    override fun showEmergencyAlarmVehicle(on: Boolean) {

    }

    override fun initializeMqtt() {

    }

    override fun showErrorLabel(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        labelTextInputEditText.error = error
    }

    override fun showErrorSerialNumber(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        serialNumberTextInputEditText.error = error
    }

    override fun showErrorBrand(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        brandTextInputEditText.error = error
    }

    override fun showErrorModel(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        modelTextInputEditText.error = error
    }

    override fun showMessage(message: String) {
        dismiss()
        (activity as BaseActivity).dismissDialogs()
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun updateVehicle(vehicle: Vehicle) {
        dismiss()
        (activity as BaseActivity).dismissDialogs()
        when(activity) {
            is HomeActivity -> (activity as HomeActivity).updateVehicle(false, vehicle)
            is ScanActivity -> (activity as ScanActivity).onBackPressed()
        }
    }

    override fun showSettingsVehicle() {

    }

    override fun showRider(vehicle: Vehicle?) {
        if(vehicle != null) {
            if (vehicle.vehicleStatus.isInUse) {
                val adapter: ArrayAdapter<User> = ArrayAdapter<User>(
                    activity!!,
                    R.layout.spinner,
                    vehiclesPresenter.getRiders()
                )
                adapter.setDropDownViewResource(R.layout.item_spinner)
                rider.adapter = adapter
                rider.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        vehiclesPresenter.setRider(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
                rider.setSelection(vehiclesPresenter.getRider(vehicle))

                riderContainer.visibility = View.VISIBLE

            } else {
                riderContainer.visibility = View.GONE
            }
        }
    }

    override fun updateSettingsVehicle(vehicle: Vehicle) {
        dismiss()
        showVehicle(vehicle)
        when(parentFragment) {
            is DashboardFragment -> (parentFragment as DashboardFragment).updateVehicle(
                false,
                vehicle
            )
        }
        (activity as BaseActivity).dismissDialogs()
    }

    override fun setRiders(riders: LinkedList<User>) {
        vehiclesPresenter.setRiders(riders)
    }

    override fun showQr(vehicle: Vehicle?) {

    }

}