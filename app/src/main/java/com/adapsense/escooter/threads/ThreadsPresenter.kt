package com.adapsense.escooter.threads

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.ThreadsApiCall
import com.adapsense.escooter.api.models.objects.Thread
import com.adapsense.escooter.api.models.responses.ThreadsResponse
import com.adapsense.escooter.threads.views.ThreadViewHolder
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.google.gson.Gson
import java.util.*

class ThreadsPresenter(private val threadsView: ThreadsContract.View) : ThreadsContract.Presenter {

    private val threads: LinkedList<Thread> = LinkedList()

    init {
        threadsView.setPresenter(this)
    }

    override fun start() {
        threadsView.setupViews()
    }

    override fun getThreads() {
        ThreadsApiCall.get(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val threadsResponse = Gson().fromJson(obj.toString(), ThreadsResponse::class.java)
                if(threadsResponse.success && threadsResponse.threads != null) {
                    threads.clear()
                    threads.addAll(threadsResponse.threads!!)
                }
                threadsView.showThreads(threads)
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                threadsView.showThreads(threads)
            }
        })
    }

    override fun onBindThreadViewAtPosition(position: Int, holder: ThreadViewHolder) {
        holder.setThread(threads[position])
    }

    override fun getThreadsCount(): Int {
        return threads.size
    }

    override fun selectThread(thread: Thread) {
        CacheUtil.instance!!.thread = thread
        threadsView.showThread()
    }

}
