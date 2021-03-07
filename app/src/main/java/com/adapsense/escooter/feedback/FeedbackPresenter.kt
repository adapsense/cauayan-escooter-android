package com.adapsense.escooter.feedback

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.FeedbacksApiCall
import com.adapsense.escooter.api.models.objects.Feedback
import com.adapsense.escooter.api.models.responses.ErrorResponse
import com.adapsense.escooter.api.models.responses.FeedbackResponse
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.google.gson.Gson
import java.io.File

class FeedbackPresenter(private val feedbackView: FeedbackContract.View) : FeedbackContract.Presenter {

    init {
        feedbackView.setPresenter(this)
    }

    override fun start() {
        feedbackView.setupViews()
    }


    override fun submit(content: String, image: File?) {
        var valid = true

        if(content.isEmpty()) {
            valid = false
            feedbackView.showError("Please enter your comments or suggestions.")
        }

        if(valid) {
            val feedback = Feedback()
            feedback.user = CacheUtil.instance!!.user
            feedback.content = content
            if(image == null) {
                FeedbacksApiCall.sendWithoutImage(feedback, object : ApiRequestListener {
                    override fun onSuccess(obj: Any) {
                        val feedbackResponse = Gson().fromJson(obj.toString(), FeedbackResponse::class.java)
                        when {
                            feedbackResponse.success -> {
                                feedbackView.showMessage("Feedback successfully submitted.")
                            }
                            feedbackResponse.error != null -> {
                                feedbackView.showError(feedbackResponse.error!!)
                            }
                            else -> {
                                feedbackView.showError("An unexpected error occurred.")
                            }
                        }
                    }

                    override fun onError(obj: Any) {
                        AppLogger.printRetrofitError(obj)
                        val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                        if(errorResponse.error != null) {
                            feedbackView.showError(errorResponse.error!!)
                        } else {
                            feedbackView.showError("An unexpected error occurred.")
                        }
                    }

                })
            } else {
                FeedbacksApiCall.sendWithImage(feedback, image, object : ApiRequestListener {
                    override fun onSuccess(obj: Any) {
                        val feedbackResponse = Gson().fromJson(obj.toString(), FeedbackResponse::class.java)
                        when {
                            feedbackResponse.success -> {
                                feedbackView.showMessage("Feedback successfully submitted.")
                            }
                            feedbackResponse.error != null -> {
                                feedbackView.showError(feedbackResponse.error!!)
                            }
                            else -> {
                                feedbackView.showError("An unexpected error occurred.")
                            }
                        }
                    }

                    override fun onError(obj: Any) {
                        AppLogger.printRetrofitError(obj)
                        val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                        if(errorResponse.error != null) {
                            feedbackView.showError(errorResponse.error!!)
                        } else {
                            feedbackView.showError("An unexpected error occurred.")
                        }
                    }

                })
            }
        }

    }

}
