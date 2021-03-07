package com.adapsense.escooter.util

import android.util.Patterns
import java.util.regex.Pattern

object StringUtil {

    fun isValidName(name: String?): Boolean {
        val pattern = Pattern.compile("^[a-zA-Z \\\\s]*$")
        return name != null && !name.isEmpty() && pattern.matcher(name)
            .matches()
    }

    fun isValidEmail(email: String?): Boolean {
        return email != null && email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

}