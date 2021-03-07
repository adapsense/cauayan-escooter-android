package com.adapsense.escooter.vehicles.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.adapsense.escooter.R
import com.adapsense.escooter.vehicles.VehiclesContract
import com.adapsense.escooter.vehicles.view.VehicleViewHolder
import com.chauthai.swipereveallayout.ViewBinderHelper
import kotlinx.android.synthetic.main.item_vehicle.view.*

class VehiclesRecyclerViewAdapter(private val vehiclesPresenter: VehiclesContract.Presenter) : Adapter<VehicleViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    private var selected = -1

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        return VehicleViewHolder(
            vehiclesPresenter, LayoutInflater.from(parent.context).inflate(
                R.layout.item_vehicle,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        viewBinderHelper.bind(holder.itemView.swipeRevealLayout, vehiclesPresenter.getVehicle(position).id);
        vehiclesPresenter.onBindVehicleViewAtPosition(position, holder, selected == position)
    }

    override fun getItemCount(): Int {
        return vehiclesPresenter.getVehiclesCount()
    }

    fun setSelected(selected: Int) {
        this.selected = selected
        notifyDataSetChanged()
    }

    fun getSelected(): Int {
        return selected
    }

}
