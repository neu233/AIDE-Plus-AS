/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.termux.shared.window

import android.app.Activity
import android.graphics.Rect
import android.util.Log

/**
 * A class for translating Extension data classes
 */
internal class ExtensionAdapter(private val windowMetricsCalculator: WindowMetricsCalculator) {
    /**
     * Perform the translation from [ExtensionWindowLayoutInfo] to [WindowLayoutInfo].
     * Translates a valid [ExtensionDisplayFeature] into a valid [DisplayFeature]. If
     * a feature is not valid it is removed
     *
     * @param activity   An [android.app.Activity].
     * @param layoutInfo The source [ExtensionWindowLayoutInfo] to be converted
     * @return [WindowLayoutInfo] containing the valid [DisplayFeature]
     */
    fun translate(
        activity: Activity,
        layoutInfo: ExtensionWindowLayoutInfo
    ): WindowLayoutInfo {
        val featureList = layoutInfo.displayFeatures.mapNotNull { translate(activity, it) }
        return WindowLayoutInfo(featureList)
    }

    fun translate(activity: Activity, displayFeature: ExtensionDisplayFeature): DisplayFeature? {
        if (displayFeature !is ExtensionFoldingFeature) {
            return null
        }
        val windowMetrics = windowMetricsCalculator.computeCurrentWindowMetrics(activity)
        return translateFoldFeature(windowMetrics.bounds, displayFeature)
    }

    companion object {
        private const val TAG = "ExtensionAdapter"
        internal fun translateFoldFeature(
            windowBounds: Rect,
            feature: ExtensionFoldingFeature
        ): DisplayFeature? {
            if (!isValid(windowBounds, feature)) {
                return null
            }
            val type = when (feature.type) {
                ExtensionFoldingFeature.TYPE_FOLD -> FoldingFeature.TYPE_FOLD
                ExtensionFoldingFeature.TYPE_HINGE -> FoldingFeature.TYPE_HINGE
                else -> {
                    if (ExtensionCompat.DEBUG) {
                        Log.d(TAG, "Unknown feature type: ${feature.type}, skipping feature.")
                    }
                    return null
                }
            }
            val state = when (feature.state) {
                ExtensionFoldingFeature.STATE_FLAT -> FoldingFeature.STATE_FLAT
                ExtensionFoldingFeature.STATE_HALF_OPENED -> FoldingFeature.STATE_HALF_OPENED
                else -> {
                    if (ExtensionCompat.DEBUG) {
                        Log.d(TAG, "Unknown feature state: ${feature.state}, skipping feature.")
                    }
                    return null
                }
            }
            return FoldingFeature(feature.bounds, type, state)
        }

        private fun isValid(windowBounds: Rect, feature: ExtensionFoldingFeature): Boolean {
            if (feature.bounds.width() == 0 && feature.bounds.height() == 0) {
                return false
            }
            if (feature.type == ExtensionFoldingFeature.TYPE_FOLD && !feature.bounds.isEmpty) {
                return false
            }
            return if (feature.type != ExtensionFoldingFeature.TYPE_FOLD &&
                feature.type != ExtensionFoldingFeature.TYPE_HINGE
            ) {
                false
            } else hasMatchingDimension(
                feature.bounds,
                windowBounds
            )
        }

        private fun hasMatchingDimension(lhs: Rect, rhs: Rect): Boolean {
            val matchesWidth = lhs.left == rhs.left && lhs.right == rhs.right
            val matchesHeight = lhs.top == rhs.top && lhs.bottom == rhs.bottom
            return matchesWidth || matchesHeight
        }
    }
}