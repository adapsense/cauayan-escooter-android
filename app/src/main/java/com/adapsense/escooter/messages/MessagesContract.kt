package com.adapsense.escooter.messages

import com.adapsense.escooter.api.models.objects.Message
import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import com.adapsense.escooter.messages.views.MessageViewHolder
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import java.util.*

interface MessagesContract {

    interface View : BaseView<Presenter?> {

        fun initializeMqtt()

        fun showMessages(messages: LinkedList<Message>)

        fun setMessage(isSender: Boolean, message: Message)

        fun showMessage(message: String)

    }

    interface Presenter : BasePresenter {

        fun initializeMqtt(client: MqttAndroidClient, connectOptions: MqttConnectOptions)

        fun subscribeMqtt()

        fun disconnectMqtt()

        fun getThread()

        fun createThread()

        fun onBindMessageViewAtPosition(position: Int, holder: MessageViewHolder)

        fun getMessagesCount(): Int

        fun sendMessage(content: String)

        fun seenMessage(message: Message)

    }

}
