package io.agora.openlive

import io.agora.rtc.Constants
import io.agora.rtc.video.BeautyOptions
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions

object AgoraConstant {
    private const val BEAUTY_EFFECT_DEFAULT_CONTRAST = BeautyOptions.LIGHTENING_CONTRAST_NORMAL
    private const val BEAUTY_EFFECT_DEFAULT_LIGHTNESS = 0.7f
    private const val BEAUTY_EFFECT_DEFAULT_SMOOTHNESS = 0.5f
    private const val BEAUTY_EFFECT_DEFAULT_REDNESS = 0.1f

    val DEFAULT_BEAUTY_OPTIONS = BeautyOptions(
            BEAUTY_EFFECT_DEFAULT_CONTRAST,
            BEAUTY_EFFECT_DEFAULT_LIGHTNESS,
            BEAUTY_EFFECT_DEFAULT_SMOOTHNESS,
            BEAUTY_EFFECT_DEFAULT_REDNESS)

    var VIDEO_DIMENSIONS = arrayOf(
            VideoEncoderConfiguration.VD_320x240,
            VideoEncoderConfiguration.VD_480x360,
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.VD_640x480,
            VideoDimensions(960, 540),
            VideoEncoderConfiguration.VD_1280x720
    )

    var VIDEO_MIRROR_MODES = intArrayOf(
            Constants.VIDEO_MIRROR_MODE_AUTO,
            Constants.VIDEO_MIRROR_MODE_ENABLED,
            Constants.VIDEO_MIRROR_MODE_DISABLED)

    fun fileAgora(): String {
        return "sp_agora"
    }

    const val DEFAULT_PROFILE_IDX = 2
    const val resolution_idx = "resolution_idx"
    const val enable_stats = "enable_stats"
    const val mirror_local = "mirror_local"
    const val mirror_remote = "mirror_remote"
    const val mirror_encode = "mirror_encode"

}