package com.global.vtg.utils

import timber.log.Timber

class TimberUtils {

    companion object {

        @JvmStatic
        fun verbose(message: String, vararg args: Any) {
            Timber.v(message, *args)
        }

        @JvmStatic
        fun info(message: String, vararg args: Any) {
            Timber.i(message, *args)
        }

        @JvmStatic
        fun warn(message: String, vararg args: Any) {
            Timber.w(message, *args)
        }


        @JvmStatic
        fun logFunc() {
            if (!SystemUtils.isInstalledFromMarketPlace()) {
                // On Android there is an extra stack frame that gets the stack trace.
                val stackFrameToPrint = if (SystemUtils.isRunningOnAndroid()) 3 else 2

                val stack = Thread.currentThread().stackTrace
                if (stack != null && stack.size > stackFrameToPrint + 1) {
                    // stack[0] == getStackTrace
                    // stack[1] == logFunc
                    val callingMethod = stack[stackFrameToPrint]
                    verbose("%s", callingMethod.toString())
                }
            }
        }

        @JvmStatic
        fun logFuncWithMessage(message: String, vararg args: Any) {
            var traceLocationStr = ""
            if (!SystemUtils.isInstalledFromMarketPlace()) {
                // On Android there is an extra stack frame that gets the stack trace.
                val stackFrameToPrint = if (SystemUtils.isRunningOnAndroid()) 3 else 2
                val stack = Thread.currentThread().stackTrace

                if (stack != null && stack.size > stackFrameToPrint + 1) {
                    // stack[0] == getStackTrace
                    // stack[1] == logFuncWithMessage
                    val callingMethod = stack[stackFrameToPrint]
                    traceLocationStr = String.format("%s", callingMethod.toString())
                }
            }
//            Log.e("TAG",traceLocationStr)
            verbose("$traceLocationStr $message", *args)
        }

        @JvmStatic
        fun verifyNoInstances() {
            throw AssertionError("No instances.")
        }

    }
}