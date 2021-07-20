package io.agora.openlive.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import io.agora.openlive.AgoraConstant.VIDEO_DIMENSIONS
import io.agora.openlive.AgoraConstant.VIDEO_MIRROR_MODES
import io.agora.openlive.MyApplication
import io.agora.openlive.R
import io.agora.openlive.rtc.EventHandler
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration

abstract class BaseAgoraActivity : AppCompatActivity(), EventHandler {

    private val application by lazy { MyApplication.getInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application.registerEventHandler(this)
        joinChannel()
    }

    private fun configVideo() {
        val configuration = VideoEncoderConfiguration(
                VIDEO_DIMENSIONS[application.engineConfig().videoDimenIndex],
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        )
        configuration.mirrorMode = VIDEO_MIRROR_MODES[application.engineConfig().mirrorEncodeIndex]
        application.rtcEngine()?.setVideoEncoderConfiguration(configuration)
    }

    private fun joinChannel() {
        // Initialize token, extra info here before joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name and uid that
        // you use to generate this token.
        var token: String? = getString(R.string.agora_live_app_token)
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null
        }

        // Sets the channel profile of the Agora RtcEngine.
        // The Agora RtcEngine differentiates channel profiles and applies different optimization algorithms accordingly. For example, it prioritizes smoothness and low latency for a video call, and prioritizes video quality for a video broadcast.
        application.rtcEngine()?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        application.rtcEngine()?.enableVideo()
        configVideo()
        application.rtcEngine()?.joinChannel(token, application.engineConfig().channelName, "", 0)
    }

    protected fun prepareRtcVideo(uid: Int, local: Boolean): SurfaceView {
        // Render local/remote video on a SurfaceView
        val surface = RtcEngine.CreateRendererView(applicationContext)
        if (local) {
            application.rtcEngine()?.setupLocalVideo(
                    VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            0,
                            VIDEO_MIRROR_MODES[application.engineConfig().mirrorLocalIndex]
                    )
            )
        } else {
            application.rtcEngine()?.setupRemoteVideo(
                    VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            uid,
                            VIDEO_MIRROR_MODES[application.engineConfig().mirrorRemoteIndex]
                    )
            )
        }
        return surface
    }

    protected fun removeRtcVideo(uid: Int, local: Boolean) {
        if (local) {
            application.rtcEngine()?.setupLocalVideo(null)
        } else {
            application. rtcEngine()?.setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        application.removeEventHandler(this)
        application.rtcEngine()?.leaveChannel()
    }
}