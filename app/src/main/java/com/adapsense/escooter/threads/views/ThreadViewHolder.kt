package com.adapsense.escooter.threads.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.adapsense.escooter.api.models.objects.Message
import com.adapsense.escooter.api.models.objects.Thread
import com.adapsense.escooter.messages.MessagesContract
import com.adapsense.escooter.threads.ThreadsContract
import kotlinx.android.synthetic.main.item_thread.view.*
import java.text.SimpleDateFormat
import java.util.*

class ThreadViewHolder(private var threadsPresenter: ThreadsContract.Presenter, itemView: View?) : ViewHolder(itemView!!), ThreadsContract.View {

	init {
        setupViews()
    }

	override fun setPresenter(presenter: ThreadsContract.Presenter?) {
		threadsPresenter = presenter!!
	}

	override fun setupViews() {

	}

	override fun showThreads(threads: LinkedList<Thread>) {

	}

	override fun setThread(thread: Thread) {

		itemView.container.setOnClickListener {
			threadsPresenter.selectThread(thread)
		}

		itemView.fullName.text = thread.rider!!.fullName
		itemView.email.text = thread.rider!!.email
		var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
		val updatedAt = sdf.parse(thread.updatedAt!!)
		sdf = SimpleDateFormat("MMM d", Locale.ENGLISH)
		itemView.updatedAt.text = sdf.format(updatedAt!!)
	}

	override fun showThread() {

	}

}
