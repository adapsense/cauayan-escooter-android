package com.adapsense.escooter.feedback

import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import java.io.File

interface FeedbackContract {

    interface View : BaseView<Presenter?> {

        fun showMessage(message: String)

        fun showError(error: String)

    }

    interface Presenter : BasePresenter {

        fun submit(content: String, image: File?)

    }

}
