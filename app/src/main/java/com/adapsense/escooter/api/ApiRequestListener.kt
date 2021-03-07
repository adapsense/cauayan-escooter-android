package com.adapsense.escooter.api

interface ApiRequestListener {
	fun onSuccess(obj: Any)
	fun onError(obj: Any)
}
