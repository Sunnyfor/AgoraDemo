package io.agora.openlive.stats

import java.util.*

/**
 * Desc 远端数据统计
 * Author ZY
 * Mail zuoyu98@foxmail.com
 * Date 2021年7月9日 17:34:45
 */
class RemoteStatsData : StatsData() {
    var videoDelay = 0
    var audioNetDelay = 0
    var audioNetJitter = 0
    var audioLoss = 0
    var audioQuality: String? = null

    override fun toString(): String {
        return String.format(Locale.getDefault(), fORMAT,
                uid,
                width, height, framerate,
                sendQuality, recvQuality,
                videoDelay,
                audioNetDelay, audioNetJitter,
                audioLoss, audioQuality)
    }

    companion object {
        const val fORMAT = "Remote(%d)\n\n" +
                "%dx%d %dfps\n" +
                "Quality tx/rx: %s/%s\n" +
                "Video delay: %d ms\n" +
                "Audio net delay/jitter: %dms/%dms\n" +
                "Audio loss/quality: %d%%/%s"
    }
}