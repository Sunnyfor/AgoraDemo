package io.agora.openlive.activity

import android.app.AlertDialog
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import io.agora.openlive.AgoraConstant
import io.agora.openlive.MyApplication
import io.agora.openlive.R
import io.agora.openlive.ui.ResolutionAdapter
import io.agora.openlive.utils.SpUtil
import kotlinx.android.synthetic.main.agora_act_settings.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var mItemPadding = 0
    private var mResolutionAdapter: ResolutionAdapter? = null

    private val config by lazy { MyApplication.getInstance().engineConfig() }

    private val mItemDecoration: ItemDecoration = object : ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State) {
            outRect.top = mItemPadding
            outRect.bottom = mItemPadding
            outRect.left = mItemPadding
            outRect.right = mItemPadding
            val pos = parent.getChildAdapterPosition(view)
            if (pos < DEFAULT_SPAN) {
                outRect.top = 0
            }
            if (pos % DEFAULT_SPAN == 0) outRect.left = 0 else if (pos % DEFAULT_SPAN == DEFAULT_SPAN - 1) outRect.right = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agora_act_settings)
        initUI()

        role_title_layout.setOnClickListener(this)
        tv_cb_setting_stats.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        var key: String? = null
        var textView: TextView? = null
        when (view.id) {
            role_title_layout.id -> finish()
            tv_cb_setting_stats.id ->{
                view.isActivated = !view.isActivated
                MyApplication.getInstance().statsManager().enableStats(view.isActivated)
            }
            R.id.rl_setting_mirror_local_view -> {
                key = AgoraConstant.mirror_local
                textView = tv_mirror_local
                textView?.let { showDialog(key, it) }
            }
            R.id.setting_mirror_remote_view -> {
                key = AgoraConstant.mirror_remote
                textView = tv_mirror_remote
                textView?.let { showDialog(key, it) }
            }
            R.id.setting_mirror_encode_view -> {
                key = AgoraConstant.mirror_encode
                textView = tv_mirror_encode
                textView?.let { showDialog(key, it) }
            }

        }

    }


    private fun initUI() {
        val resolutionList = findViewById<RecyclerView>(R.id.resolution_list)
        resolutionList.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, DEFAULT_SPAN)
        resolutionList.layoutManager = layoutManager
        mResolutionAdapter = ResolutionAdapter(this, config.videoDimenIndex)
        resolutionList.adapter = mResolutionAdapter
        resolutionList.addItemDecoration(mItemDecoration)
        mItemPadding = resources.getDimensionPixelSize(R.dimen.dp_6)

        tv_cb_setting_stats.isActivated = config.ifShowVideoStats()

        resetText(tv_mirror_local, config.mirrorLocalIndex)
        resetText(tv_mirror_remote, config.mirrorRemoteIndex)
        resetText(tv_mirror_encode, config.mirrorEncodeIndex)
    }

    private fun resetText(view: TextView?, index: Int) {
        if (view == null) {
            return
        }
        val strings = resources.getStringArray(R.array.mirror_modes)
        view.text = strings[index]
    }


    override fun onBackPressed() {
        saveResolution()
        saveShowStats()
        finish()
    }

    private fun saveResolution() {
        val profileIndex = mResolutionAdapter!!.selected
        config.videoDimenIndex = profileIndex
        SpUtil.get(AgoraConstant.fileAgora()).setInteger(AgoraConstant.resolution_idx, profileIndex)
    }

    private fun saveShowStats() {
        val it = tv_cb_setting_stats.isActivated
        config.setIfShowVideoStats(it)
        SpUtil.get(AgoraConstant.fileAgora()).setBoolean(AgoraConstant.enable_stats, it)
    }

    private fun saveVideoMirrorMode(key: String?, value: Int) {
        if (TextUtils.isEmpty(key)) return
        when (key) {
            AgoraConstant.mirror_local -> config.mirrorLocalIndex = value
            AgoraConstant.mirror_remote -> config.mirrorRemoteIndex = value
            AgoraConstant.mirror_encode -> config.mirrorEncodeIndex = value
        }
        SpUtil.get(AgoraConstant.fileAgora()).setInteger(key ?: "", value)
    }



    private fun showDialog(key: String?, view: TextView) {
        val builder = AlertDialog.Builder(this)
        val strings = resources.getStringArray(R.array.mirror_modes)
        val checkedItem = listOf(*strings).indexOf(view.text.toString())
        builder.setSingleChoiceItems(strings, checkedItem) { dialog, which ->
            saveVideoMirrorMode(key, which)
            resetText(view, which)
            dialog.dismiss()
        }
        builder.create().show()
    }

    companion object {
        private const val DEFAULT_SPAN = 3
    }
}