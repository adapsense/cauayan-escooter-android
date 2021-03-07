package com.adapsense.escooter.threads

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.adapsense.escooter.R
import com.adapsense.escooter.threads.views.ThreadViewHolder

class ThreadsRecyclerViewAdapter(private val threadsPresenter: ThreadsContract.Presenter) : Adapter<ThreadViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        return ThreadViewHolder(threadsPresenter, LayoutInflater.from(parent.context).inflate(R.layout.item_thread, parent, false))
    }

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        threadsPresenter.onBindThreadViewAtPosition(position, holder)
    }

    override fun getItemCount(): Int {
        return threadsPresenter.getThreadsCount()
    }

}
