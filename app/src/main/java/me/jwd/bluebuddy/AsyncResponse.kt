package me.jwd.bluebuddy

interface AsyncResponse {
    fun response(output:String, exit: Boolean = false)
}