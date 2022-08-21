package com.dacv.apppasswords.utils

import java.math.BigInteger
import java.security.MessageDigest

class File {
    companion object {
        fun function(input:String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }
    }
}