package com.adapsense.escooter.threads

import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import com.adapsense.escooter.api.models.objects.Thread
import com.adapsense.escooter.threads.views.ThreadViewHolder
import java.util.*

interface ThreadsContract {

    interface View : BaseView<Presenter?> {

        fun showThreads(threads: LinkedList<Thread>)

        fun setThread(thread: Thread)

        fun showThread()

    }

    interface Presenter : BasePresenter {

        fun getThreads()

        fun onBindThreadViewAtPosition(position: Int, holder: ThreadViewHolder)

        fun getThreadsCount(): Int

        fun selectThread(thread: Thread)

    }

}
