package com.dacv.apppasswords.models

import java.io.Serializable

class Account : Serializable{

    var id = ""
    var account = ""
    var email = ""
    var password = ""
    var image = ""

    override fun toString(): String {
        return "$id\n $account\n $email\n $password\n $image"
    }
}