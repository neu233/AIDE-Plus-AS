


package com.intellij.openapi.util

import com.intellij.openapi.Disposable
import java.util.concurrent.ExecutorService

class LowMemoryWatcherManager(@Suppress("UNUSED_PARAMETER") service: ExecutorService) : Disposable {

    // no-op
    override fun dispose() {}
}
