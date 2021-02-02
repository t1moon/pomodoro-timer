package com.fetecom.pomodoro.common

abstract class ListAdapterItem {
    abstract fun getType() : Int
    abstract fun getId() : Int
}