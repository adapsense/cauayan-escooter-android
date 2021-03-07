package com.adapsense.escooter.messages.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapsense.escooter.BuildConfig
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.Message
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.messages.MessagesContract
import com.adapsense.escooter.messages.MessagesPresenter
import com.adapsense.escooter.messages.MessagesRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.navigation.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import java.util.*

class MessagesActivity : BaseActivity(), MessagesContract.View {

    private lateinit var messagesPresenter: MessagesContract.Presenter

    private lateinit var adapter: MessagesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        messagesPresenter = MessagesPresenter(this)
    }

    override fun onResume() {
        super.onResume()
        showProgressDialog()
        messagesPresenter.start()
        initializeMqtt()
    }

    override fun onPause() {
        super.onPause()
        messagesPresenter.disconnectMqtt()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right)
    }

    override fun setPresenter(presenter: MessagesContract.Presenter?) {
        messagesPresenter = presenter!!
    }

    override fun setupViews() {
        back.visibility = View.VISIBLE

        back.setOnClickListener { onBackPressed() }

        titleText.text = "Support"

        swipeRefreshLayout.setOnRefreshListener {
            messagesPresenter.getThread()
        }

        adapter = MessagesRecyclerViewAdapter(messagesPresenter)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MessagesActivity)
            adapter = this@MessagesActivity.adapter
        }

        content.requestFocus()

        send.setOnClickListener {
            messagesPresenter.sendMessage(content.text.toString())
            content.setText("")
        }

    }

    override fun initializeMqtt() {
        val connectOptions = MqttConnectOptions()
        connectOptions.userName = BuildConfig.MQTT_USERNAME
        connectOptions.password = BuildConfig.MQTT_PASSWORD.toCharArray()
        messagesPresenter.initializeMqtt(
            MqttAndroidClient(
                this,
                BuildConfig.MQTT_BROKER,
                MqttClient.generateClientId()
            ), connectOptions
        )
    }

    override fun showMessages(messages: LinkedList<Message>) {
        runOnUiThread {
            dismissDialogs()
            swipeRefreshLayout.isRefreshing = false
            if(messages.size > 0) {
                empty.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                empty.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }

    override fun setMessage(isSender: Boolean, message: Message) {

    }

    override fun showMessage(message: String) {
        dismissDialogs()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}