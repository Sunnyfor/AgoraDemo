package io.agora.openlive

import android.app.Application
import io.agora.openlive.rtc.AgoraEventHandler
import io.agora.openlive.rtc.EngineConfig
import io.agora.openlive.rtc.EventHandler
import io.agora.openlive.stats.StatsManager
import io.agora.openlive.utils.FileUtil
import io.agora.openlive.utils.SpUtil
import io.agora.rtc.RtcEngine

class MyApplication : Application() {

    private var mRtcEngine: RtcEngine? = null
    private val mGlobalConfig = EngineConfig()
    private val mHandler = AgoraEventHandler()
    private val mStatsManager = StatsManager()

    companion object {
        lateinit var application: MyApplication
        fun getInstance(): MyApplication {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        try {
            mRtcEngine = RtcEngine.create(applicationContext, getString(R.string.private_app_id), mHandler)
            mRtcEngine?.setLogFile(FileUtil.initializeLogFile(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initConfig()
    }

    private fun initConfig() {
        val showStats = SpUtil.get(AgoraConstant.fileAgora()).getBoolean(AgoraConstant.enable_stats, false)
        mGlobalConfig.videoDimenIndex = SpUtil.get(AgoraConstant.fileAgora()).getInteger(AgoraConstant.resolution_idx, AgoraConstant.DEFAULT_PROFILE_IDX)
        mGlobalConfig.setIfShowVideoStats(showStats)
        mStatsManager.enableStats(showStats)
        mGlobalConfig.mirrorLocalIndex = SpUtil.get(AgoraConstant.fileAgora()).getInteger(AgoraConstant.mirror_local, 0)
        mGlobalConfig.mirrorRemoteIndex = SpUtil.get(AgoraConstant.fileAgora()).getInteger(AgoraConstant.mirror_remote, 0)
        mGlobalConfig.mirrorEncodeIndex = SpUtil.get(AgoraConstant.fileAgora()).getInteger(AgoraConstant.mirror_encode, 0)
    }

    fun engineConfig(): EngineConfig {
        return mGlobalConfig
    }

    fun rtcEngine(): RtcEngine? {
        return mRtcEngine
    }

    fun statsManager(): StatsManager {
        return mStatsManager
    }

    fun registerEventHandler(handler: EventHandler?) {
        mHandler.addHandler(handler)
    }

    fun removeEventHandler(handler: EventHandler?) {
        mHandler.removeHandler(handler)
    }

    override fun onTerminate() {
        super.onTerminate()
        RtcEngine.destroy()
    }
}