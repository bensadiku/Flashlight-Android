package com.bensadiku.flashlight.utils

import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work when pre-populating database on the first creation
 */
fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}