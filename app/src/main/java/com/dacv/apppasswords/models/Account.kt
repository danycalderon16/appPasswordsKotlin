package com.dacv.apppasswords.models

class Account {

    var id = ""
    var account = ""
    var email = ""
    var password = ""
    var image = ""

    override fun toString(): String {
        return "$id\n $account\n $email\n $password\n $image"
    }
}