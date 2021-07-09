package io.agora.openlive.stats

/**
 * Desc 直播数据统计
 * Author ZY
 * Mail zuoyu98@foxmail.com
 * Date 2021年7月9日 16:35:42
 */
open class StatsData {
    var uid: Long = 0
    var width = 0
    var height = 0
    var framerate = 0
    var recvQuality: String? = null
    var sendQuality: String? = null
}