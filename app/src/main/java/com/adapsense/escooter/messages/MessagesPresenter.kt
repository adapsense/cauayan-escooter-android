package com.adapsense.escooter.messages

import com.adapsense.escooter.BuildConfig
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.MessagesApiCall
import com.adapsense.escooter.api.calls.ThreadsApiCall
import com.adapsense.escooter.api.models.objects.Message
import com.adapsense.escooter.api.models.objects.Thread
import com.adapsense.escooter.api.models.objects.VehicleLog
import com.adapsense.escooter.api.models.objects.VehicleMessagePayload
import com.adapsense.escooter.api.models.responses.MessageResponse
import com.adapsense.escooter.api.models.responses.ThreadResponse
import com.adapsense.escooter.api.models.responses.ThreadsResponse
import com.adapsense.escooter.messages.views.MessageViewHolder
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.StandardCharsets
import java.util.*

class MessagesPresenter(private val messagesView: MessagesContract.View) : MessagesContract.Presenter {

    private var mqttAndroidClient: MqttAndroidClient? = null

    private var thread: Thread? = null
    private val messages: LinkedList<Message> = LinkedList()

    init {
        messagesView.setPresenter(this)
    }

    override fun start() {
        messagesView.setupViews()

    }

    override fun initializeMqtt(client: MqttAndroidClient, connectOptions: MqttConnectOptions) {
        disconnectMqtt()
        this.mqttAndroidClient = client
        this.mqttAndroidClient?.apply {
            connect(connectOptions).actionCallback = object :
                IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    AppLogger.info("Connected: ${BuildConfig.MQTT_BROKER}")
                    getThread()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    AppLogger.info("Connect Failure: ${exception!!.message}")
                    exception.printStackTrace()
                    getThread()
                }

            }
        }

    }

    override fun subscribeMqtt() {
        val messagesTopic = "messages/${thread!!.rider!!.id}"
        AppLogger.info("Subscribe: $messagesTopic")
        mqttAndroidClient!!.subscribe(
            messagesTopic, 1
        ) { _, message ->
            AppLogger.info("Subscribe: $messagesTopic")
            AppLogger.info(String(message.payload, StandardCharsets.UTF_8))
            val receivedMessage = Gson().fromJson(String(message.payload, StandardCharsets.UTF_8), Message::class.java)
            messages.push(receivedMessage)
            messagesView.showMessages(messages)
        }
    }

    override fun disconnectMqtt() {
        if(mqttAndroidClient != null && mqttAndroidClient!!.isConnected) {
            mqttAndroidClient!!.apply {
                disconnect().actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        AppLogger.info("Disconnected: ${BuildConfig.MQTT_BROKER}")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        AppLogger.info("Disconnect Failure: ${exception!!.message}")
                        exception.printStackTrace()
                    }
                }
            }
        }
    }

    override fun getThread() {
        val user = CacheUtil.instance!!.user!!
        if(user.userType.isAdmin) {
            thread = CacheUtil.instance!!.thread
            ThreadsApiCall.get(thread!!, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val threadsResponse = Gson().fromJson(obj.toString(), ThreadsResponse::class.java)
                    if(threadsResponse.success) {
                        if(threadsResponse.threads != null) {
                            if(threadsResponse.threads!!.isNotEmpty()) {
                                thread = threadsResponse.threads!![0]
                                subscribeMqtt()
                                messages.clear()
                                messages.addAll(thread!!.messages!!)
                            }
                        }
                    }
                    messagesView.showMessages(messages)
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    messagesView.showMessages(messages)
                }

            })
        } else {
           ThreadsApiCall.get(user, object : ApiRequestListener {
               override fun onSuccess(obj: Any) {
                   val threadsResponse = Gson().fromJson(obj.toString(), ThreadsResponse::class.java)
                   if(threadsResponse.success) {
                       if(threadsResponse.threads != null) {
                           if(threadsResponse.threads!!.isNotEmpty()) {
                               thread = threadsResponse.threads!![0]
                               subscribeMqtt()
                               messages.clear()
                               messages.addAll(thread!!.messages!!)
                           } else {
                               createThread()
                           }
                           messagesView.showMessages(messages)
                       }
                   } else {
                       messagesView.showMessages(messages)
                   }
               }

               override fun onError(obj: Any) {
                   AppLogger.printRetrofitError(obj)
                   messagesView.showMessages(messages)
               }

           })
        }
    }

    override fun createThread() {
        ThreadsApiCall.create(CacheUtil.instance!!.user!!, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val threadResponse = Gson().fromJson(obj.toString(), ThreadResponse::class.java)
                if(threadResponse.success) {
                    thread = threadResponse.thread
                }
                messages.clear()
                messages.addAll(thread!!.messages!!)
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                messages.clear()
                messages.addAll(thread!!.messages!!)
            }

        })
    }

    override fun onBindMessageViewAtPosition(position: Int, holder: MessageViewHolder) {
        val message = messages[position]
        holder.setMessage(CacheUtil.instance!!.user!!.id == message.sender!!.id, message)
    }

    override fun getMessagesCount(): Int {
        return messages.size
    }

    override fun sendMessage(content: String) {
        if(content.isNotEmpty()) {
            val message = Message()
            message.sender = CacheUtil.instance!!.user
            message.content = content
            MessagesApiCall.send(thread!!, message, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val messageResponse = Gson().fromJson(obj.toString(), MessageResponse::class.java)
                    if(messageResponse.success && messageResponse.message != null) {
                        messages.push(messageResponse.message)
                        messagesView.showMessages(messages)
                        val messagesTopic = "messages/${thread!!.rider!!.id}"
                        val publishMessage = messageResponse.message.toString()
                        AppLogger.info("Publish: $messagesTopic")
                        AppLogger.info(publishMessage)
                        mqttAndroidClient!!.publish(messagesTopic, MqttMessage(publishMessage.toByteArray(StandardCharsets.UTF_8)))

                    } else {
                        messagesView.showMessage("Unable to send message")
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj.toString())
                    messagesView.showMessage("Unable to send message")
                }

            })
        }
    }


    override fun seenMessage(message: Message) {
        MessagesApiCall.seen(message, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val messageResponse = Gson().fromJson(obj.toString(), MessageResponse::class.java)
                if(messageResponse.success && messageResponse.message != null) {
                    for(messageItem in messages) {
                        if(messageItem.id == messageResponse.message!!.id) {
                            messages[messages.indexOf(messageItem)] = messageResponse.message!!
                            break
                        }
                    }
                    messagesView.showMessages(messages)
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj.toString())
            }

        })
    }

}
