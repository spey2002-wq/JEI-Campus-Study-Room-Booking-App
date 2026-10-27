package com.jei.campusbooking.util

import java.security.MessageDigest

object PasswordUtil {
    /** SHA-256 hex digest. (Course project: a real app should also use a salt.) */
    fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}
