package io.agora.openlive.activity

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import io.agora.openlive.AgoraConstant
import io.agora.openlive.MyApplication
import io.agora.openlive.R
import io.agora.openlive.rtc.EventHandler
import io.agora.openlive.stats.LocalStatsData
import io.agora.openlive.stats.RemoteStatsData
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler.*
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions
import kotlinx.android.synthetic.main.agora_act_live_room.*

class LiveActivity : BaseAgoraActivity(), EventHandler, View.OnClickListener {

    companion object {
        fun intent(context: Context, roleId: Int) {
            val intent = Intent(context, LiveActivity::class.java)
            intent.putExtra("roleId", roleId)
            context.startActivity(intent)
        }
    }

    private val roleId by lazy { intent.getIntExtra("roleId", Constants.CLIENT_ROLE_AUDIENCE) }

    private val application by lazy { MyApplication.getInstance() }

    private var mVideoDimension: VideoDimensions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agora_act_live_room)
        initUI()
        initData()

        iv_btn_switch_camera.setOnClickListener(this)
    }

    private fun initUI() {

        tv_live_room_name.text = application.engineConfig().channelName
        tv_live_room_name.isSelected = true

        initUserIcon()


        val isBroadcaster = roleId == Constants.CLIENT_ROLE_BROADCASTER
        iv_btn_mute_video?.isActivated = isBroadcaster
        iv_btn_mute_audio.isActivated = isBroadcaster
        iv_btn_beautification.isActivated = true

        application.rtcEngine()?.setBeautyEffectOptions(iv_btn_beautification.isActivated, AgoraConstant.DEFAULT_BEAUTY_OPTIONS)

        videoGridContainer.setStatsManager(application.statsManager())
        application.rtcEngine()?.setClientRole(roleId)
        if (isBroadcaster) startBroadcast()
    }

    private fun initUserIcon() {
        val origin = BitmapFactory.decodeResource(resources, R.drawable.fake_user_icon)
        val drawable = RoundedBitmapDrawableFactory.create(resources, origin)
        drawable.isCircular = true
        iv_name_board.setImageDrawable(drawable)
    }

    private fun initData() {
        mVideoDimension = AgoraConstant.VIDEO_DIMENSIONS[application.engineConfig().videoDimenIndex]
    }


    private fun startBroadcast() {
        application.rtcEngine()?.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        val surface = prepareRtcVideo(0, true)
        videoGridContainer.addUserVideoSurface(0, surface, true)
        iv_btn_mute_audio!!.isActivated = true
    }

    private fun stopBroadcast() {
        application.rtcEngine()?.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
        removeRtcVideo(0, true)
        videoGridContainer.removeUserVideo(0, true)
        iv_btn_mute_audio!!.isActivated = false
    }

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        // Do nothing at the moment
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        // Do nothing at the moment
    }

    override fun onLastMileQuality(quality: Int) {

    }

    override fun onLastMileProbeResult(result: LastmileProbeResult) {

    }

    override fun onUserOffline(uid: Int, reason: Int) {
        runOnUiThread { removeRemoteUser(uid) }
    }

    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        runOnUiThread { renderRemoteUser(uid) }
    }

    override fun onLeaveChannel(stats: RtcStats) {

    }

    private fun renderRemoteUser(uid: Int) {
        val surface = prepareRtcVideo(uid, false)
        videoGridContainer.addUserVideoSurface(uid, surface, false)
    }

    private fun removeRemoteUser(uid: Int) {
        removeRtcVideo(uid, false)
        videoGridContainer.removeUserVideo(uid, false)
    }

    override fun onLocalVideoStats(stats: LocalVideoStats) {
        if (!application.statsManager().isEnabled) return
        val data = application.statsManager().getStatsData(0) as LocalStatsData
        data.width = mVideoDimension!!.width
        data.height = mVideoDimension!!.height
        data.framerate = stats.sentFrameRate
    }

    override fun onRtcStats(stats: RtcStats) {
        if (!application.statsManager().isEnabled) return
        val data = application.statsManager().getStatsData(0) as LocalStatsData
        data.lastMileDelay = stats.lastmileDelay
        data.videoSendBitrate = stats.txVideoKBitRate
        data.videoRecvBitrate = stats.rxVideoKBitRate
        data.audioSendBitrate = stats.txAudioKBitRate
        data.audioRecvBitrate = stats.rxAudioKBitRate
        data.cpuApp = stats.cpuAppUsage
        data.cpuTotal = stats.cpuAppUsage
        data.sendLoss = stats.txPacketLossRate
        data.recvLoss = stats.rxPacketLossRate
    }

    override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
        if (!application.statsManager().isEnabled) return
        val data = application.statsManager().getStatsData(uid) ?: return
        data.sendQuality = application.statsManager().qualityToString(txQuality)
        data.recvQuality = application.statsManager().qualityToString(rxQuality)
    }

    override fun onRemoteVideoStats(stats: RemoteVideoStats) {
        if (!application.statsManager().isEnabled) return
        val data = application.statsManager().getStatsData(stats.uid) as RemoteStatsData
        data.width = stats.width
        data.height = stats.height
        data.framerate = stats.rendererOutputFrameRate
        data.videoDelay = stats.delay
    }

    override fun onRemoteAudioStats(stats: RemoteAudioStats) {
        if (!application.statsManager().isEnabled) return
        val data = application.statsManager().getStatsData(stats.uid) as RemoteStatsData
        data.audioNetDelay = stats.networkTransportDelay
        data.audioNetJitter = stats.jitterBufferDelay
        data.audioLoss = stats.audioLossRate
        data.audioQuality = application.statsManager().qualityToString(stats.quality)
    }

    override fun finish() {
        super.finish()
        application.statsManager().clearAllData()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            iv_btn_switch_camera.id ->application.rtcEngine()?.switchCamera()
        }
    }

    fun onBeautyClicked(view: View) {
        view.isActivated = !view.isActivated
        application.rtcEngine()?.setBeautyEffectOptions(view.isActivated, AgoraConstant.DEFAULT_BEAUTY_OPTIONS)
    }

    fun onMuteAudioClicked(view: View) {
        if (!iv_btn_mute_video!!.isActivated) return
        application.rtcEngine()?.muteLocalAudioStream(view.isActivated)
        view.isActivated = !view.isActivated
    }

    fun onMuteVideoClicked(view: View) {
        if (view.isActivated) {
            stopBroadcast()
        } else {
            startBroadcast()
        }
        view.isActivated = !view.isActivated
    }


}