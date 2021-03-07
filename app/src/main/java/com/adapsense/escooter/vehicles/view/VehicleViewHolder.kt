package com.adapsense.escooter.vehicles.view

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.*
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.vehicles.VehiclesContract
import kotlinx.android.synthetic.main.item_vehicle.view.*
import java.util.*

class VehicleViewHolder(private var vehiclesPresenter: VehiclesContract.Presenter, itemView: View?) : ViewHolder(itemView!!), VehiclesContract.View {

    init {
        setupViews()
    }

	override fun setPresenter(presenter: VehiclesContract.Presenter?) {
		vehiclesPresenter = presenter!!
	}

	override fun setupViews() {

	}

	override fun setVehicleTypes(vehicleTypes: LinkedList<VehicleType>) {

	}

	override fun setVehicleStatuses(vehicleStatuses: LinkedList<VehicleStatus>) {

	}

	override fun setVehicles(vehicles: LinkedList<Vehicle>) {

	}

	override fun showVehicles(vehicles: LinkedList<Vehicle>) {

	}

	override fun showVehicleLogs(vehicleLogs: HashMap<String, VehicleLog>) {

	}

	override fun setVehicle(vehicle: Vehicle, selected: Boolean) {

		if(selected) {
			itemView.card.strokeColor = ContextCompat.getColor(itemView.context, R.color.primary)
		} else {
			itemView.card.strokeColor = ContextCompat.getColor(itemView.context, android.R.color.transparent)
		}

		itemView.container.setOnClickListener {
			if(!selected) {
				vehiclesPresenter.selectVehicle(adapterPosition)
			} else {
				vehiclesPresenter.selectVehicle(-1)
			}
		}

		itemView.edit.setOnClickListener {
			itemView.swipeRevealLayout.close(true)
			vehiclesPresenter.editVehicle(vehicle)
		}

		itemView.deleteContainer.visibility = if(vehicle.vehicleStatus.isDeleted) View.GONE else View.VISIBLE

		itemView.delete.setOnClickListener {
			itemView.swipeRevealLayout.close(true)
			(itemView.context as BaseActivity).showAlertDialog("Delete Vehicle", "Are you sure you want to delete ${vehicle.name}?", "Delete",
				{ dialog, _ ->
					dialog!!.dismiss()
					(itemView.context as BaseActivity).showProgressDialog()
					vehiclesPresenter.deleteVehicle(vehicle)
				}, "Cancel"
			) { dialog, _ -> dialog!!.dismiss() }
		}

		itemView.name.text = vehicle.name
		itemView.status.text = vehicle.vehicleStatus.name

		if(vehicle.vehicleStatus.isInUse && vehicle.rider != null) {
			itemView.rider.text = "Rider: ${vehicle.rider!!.fullName}"
			itemView.rider.visibility = View.VISIBLE
		} else {
			itemView.rider.visibility = View.GONE
		}

		itemView.qr.setOnClickListener {
			vehiclesPresenter.showQr(vehicle)
		}

	}

	override fun showVehicle(vehicle: Vehicle?) {

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

	}

	override fun showErrorSerialNumber(error: String?) {

	}

	override fun showErrorBrand(error: String?) {

	}

	override fun showErrorModel(error: String?) {

	}

	override fun showMessage(message: String) {

	}

	override fun updateVehicle(vehicle: Vehicle) {

	}

	override fun showSettingsVehicle() {

	}

	override fun showRider(vehicle: Vehicle?) {

	}

	override fun updateSettingsVehicle(vehicle: Vehicle) {

	}

	override fun setRiders(riders: LinkedList<User>) {

	}

	override fun showQr(vehicle: Vehicle?) {

	}

}
