package com.adapsense.escooter.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.adapsense.escooter.R
import com.adapsense.escooter.messages.views.MessageViewHolder

class MessagesRecyclerViewAdapter(private val messagesPresenter: MessagesContract.Presenter) : Adapter<MessageViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return  MessageViewHolder(messagesPresenter, LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        messagesPresenter.onBindMessageViewAtPosition(position, holder)
    }

    override fun getItemCount(): Int {
        return messagesPresenter.getMessagesCount()
    }

}
