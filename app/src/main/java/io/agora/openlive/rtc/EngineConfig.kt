package io.agora.openlive.rtc

import io.agora.openlive.AgoraConstant

/**
 * Desc 配置工具类
 * Author ZY
 * Mail zuoyu98@foxmail.com
 * Date 2021年7月9日 17:20:59
 */
class EngineConfig {

    private var mShowVideoStats = false

    var channelName: String? = null
    var videoDimenIndex = AgoraConstant.DEFAULT_PROFILE_IDX
    var mirrorLocalIndex = 0
    var mirrorRemoteIndex = 0
    var mirrorEncodeIndex = 0

    fun ifShowVideoStats(): Boolean {
        return mShowVideoStats
    }

    fun setIfShowVideoStats(show: Boolean) {
        mShowVideoStats = show
    }
}