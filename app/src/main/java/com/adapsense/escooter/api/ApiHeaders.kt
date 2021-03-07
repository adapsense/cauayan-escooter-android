package com.adapsense.escooter.api

import com.adapsense.escooter.util.CacheUtil
import java.util.*

class ApiHeaders {

    fun getHeaders(): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        //headers["Content-Type"] = "application/json"

        if(CacheUtil.instance!!.token != null) {
            headers["Authorization"] = "Bearer ${CacheUtil.instance!!.token}"
        }

        return headers
    }

    /*fun getUploadHeaders(): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "multipart/form-data"
        if(CacheUtil.instance!!.token != null) {
            headers["Authorization"] = "Bearer ${CacheUtil.instance!!.token}"
        }

        return headers
    }*/

}