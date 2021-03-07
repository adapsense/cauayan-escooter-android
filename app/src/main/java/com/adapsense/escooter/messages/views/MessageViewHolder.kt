package com.adapsense.escooter.messages.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.Message
import com.adapsense.escooter.messages.MessagesContract
import kotlinx.android.synthetic.main.item_message.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessageViewHolder(private var messagesPresenter: MessagesContract.Presenter, itemView: View?) : ViewHolder(itemView!!), MessagesContract.View {

	init {
        setupViews()
    }

	override fun setPresenter(presenter: MessagesContract.Presenter?) {
		messagesPresenter = presenter!!
	}

	override fun setupViews() {

	}

	override fun initializeMqtt() {

	}

	override fun showMessages(messages: LinkedList<Message>) {

	}

	override fun setMessage(isSender: Boolean, message: Message) {
		if(isSender) {
			itemView.senderContent.text = message.content
			var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
			var sentSeen: Date? = null
			if(message.seen == null) {
				sentSeen = sdf.parse(message.createdAt!!)
				itemView.sentSeenIcon.setBackgroundResource(R.drawable.message_sent)
			} else {
				sentSeen = sdf.parse(message.seen!!)
				itemView.sentSeenIcon.setBackgroundResource(R.drawable.message_seen)
			}
			sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
			itemView.sentSeen.text = "${if(message.seen == null) "Sent" else "Seen"} ${sdf.format(sentSeen!!)}"
			itemView.sentSeenContainer.visibility = View.VISIBLE
			itemView.senderContainer.visibility = View.VISIBLE
			itemView.receiverContent.visibility = View.GONE
		} else {
			itemView.sentSeenContainer.visibility = View.GONE
			itemView.receiverContent.text = message.content
			itemView.senderContainer.visibility = View.GONE
			itemView.receiverContent.visibility = View.VISIBLE
			if(message.seen == null) {
				messagesPresenter.seenMessage(message)
			}
		}
	}

	override fun showMessage(message: String) {

	}

}
