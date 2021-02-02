package com.fetecom.data

import timber.log.Timber

object Reporter {

    fun reportI(message: String, tag: String = "") {
        if (tag.isNotEmpty())
            Timber.tag(tag).i(message)
        else
            Timber.i(message)
    }

    fun reportE(throwable: Throwable, tag: String = "") {
        if (tag.isNotEmpty())
            Timber.tag(tag).e(throwable)
        else
            Timber.e(throwable)
    }

    fun reportD(message: String, tag: String = "") {
        if (tag.isNotEmpty())
            Timber.tag(tag).d(message)
        else
            Timber.d(message)
    }

    fun reportV(message: String, tag: String = "") {
        if (tag.isNotEmpty())
            Timber.tag(tag).v(message)
        else
            Timber.v(message)
    }
 }