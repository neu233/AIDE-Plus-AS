

package com.android.aaptcompiler

import com.android.utils.StdLogger
import com.android.utils.StdLogger.Level.VERBOSE

object IDELogger : StdLogger(VERBOSE) {

  var enabled = false

  override fun error(t: Throwable?, errorFormat: String?, vararg args: Any?) {
    if (!enabled) {
      return
    }
    super.error(t, errorFormat, *args)
  }

  override fun warning(warningFormat: String?, vararg args: Any?) {
    if (!enabled) {
      return
    }

    super.warning(warningFormat, *args)
  }

  override fun quiet(msgFormat: String?, vararg args: Any?) {
    if (!enabled) {
      return
    }

    super.quiet(msgFormat, *args)
  }

  override fun lifecycle(msgFormat: String?, vararg args: Any?) {
    if (!enabled) {
      return
    }

    super.lifecycle(msgFormat, *args)
  }

  override fun info(msgFormat: String?, vararg args: Any?) {
    if (!enabled) {
      return
    }

    super.info(msgFormat, *args)
  }

  override fun verbose(msgFormat: String?, vararg args: Any?) {
    if (!enabled) {
      return
    }

    super.verbose(msgFormat, *args)
  }
}
