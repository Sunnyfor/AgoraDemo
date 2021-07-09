package io.agora.openlive.activity

import android.Manifest
import android.animation.Animator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.openlive.MyApplication
import io.agora.openlive.R
import kotlinx.android.synthetic.main.agora_act_live_create.*

/**
 * Desc 创建直播
 * Author ZY
 * Mail zuoyu98@foxmail.com
 * Date 2021年7月7日 17:13:49
 */
class CreateLiveActivity : AppCompatActivity(), View.OnClickListener {

    private val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val mVisibleRect = Rect()
    private var mLastVisibleHeight = 0
    private var mBodyDefaultMarginTop = 0

    var displayMetrics = DisplayMetrics()

    private val mLayoutObserverListener = OnGlobalLayoutListener {
        checkInputMethodWindowState()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agora_act_live_create)

        windowManager.defaultDisplay.getMetrics(displayMetrics)

        et_topic.addTextChangedListener(mTextWatcher)

        if (TextUtils.isEmpty(et_topic.text)) {
            btn_start_live.isEnabled = false
        }

        btn_start_live.setOnClickListener(this)
        iv_btn_setting.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            btn_start_live.id -> checkPermission()
            iv_btn_setting.id -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun checkInputMethodWindowState() {
        window.decorView.rootView.getWindowVisibleDisplayFrame(mVisibleRect)
        val visibleHeight = mVisibleRect.bottom - mVisibleRect.top
        if (visibleHeight == mLastVisibleHeight) return
        val inputShown = displayMetrics.heightPixels - visibleHeight > MIN_INPUT_METHOD_HEIGHT
        mLastVisibleHeight = visibleHeight

        if (inputShown) {
            if (iv_logo.visibility == View.VISIBLE) {
                rl_top.animate()
                        .translationYBy(-iv_logo.measuredHeight.toFloat())
                        .setDuration(ANIM_DURATION.toLong())
                        .setListener(null)
                        .start()
                iv_logo.visibility = View.INVISIBLE
            }
        } else if (iv_logo.visibility != View.VISIBLE) {
            rl_top.animate()
                    .translationYBy(iv_logo.measuredHeight.toFloat())
                    .setDuration(ANIM_DURATION.toLong())
                    .setListener(mLogoAnimListener)
                    .start()
        }
    }


    private val mLogoAnimListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) {
            // Do nothing
        }

        override fun onAnimationEnd(animator: Animator) {
            iv_logo.visibility = View.VISIBLE
        }

        override fun onAnimationCancel(animator: Animator) {
            iv_logo.visibility = View.VISIBLE
        }

        override fun onAnimationRepeat(animator: Animator) {
            // Do nothing
        }
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            // Do nothing
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            // Do nothing
        }

        override fun afterTextChanged(editable: Editable) {
            btn_start_live!!.isEnabled = !TextUtils.isEmpty(editable)
        }
    }

    private fun checkPermission() {
        var granted = true
        for (per in permissions) {
            if (!permissionGranted(per)) {
                granted = false
                break
            }
        }
        if (granted) {
            resetLayoutAndForward()
        } else {
            requestPermissions()
        }
    }

    private fun permissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQ_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQ_CODE) {
            var granted = true
            for (result in grantResults) {
                granted = result == PackageManager.PERMISSION_GRANTED
                if (!granted) break
            }
            if (granted) {
                resetLayoutAndForward()
            } else {
                toastNeedPermissions()
            }
        }
    }

    private fun resetLayoutAndForward() {
        closeImeDialogIfNeeded()
        gotoRoleActivity()
    }

    private fun closeImeDialogIfNeeded() {
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(et_topic!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun gotoRoleActivity() {
        val intent = Intent(this@CreateLiveActivity, RoleActivity::class.java)
        val room = et_topic!!.text.toString()
        MyApplication.getInstance()?.engineConfig()?.channelName = room
        startActivity(intent)
    }

    private fun toastNeedPermissions() {
        Toast.makeText(this, R.string.need_necessary_permissions, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        resetUI()
        registerLayoutObserverForSoftKeyboard()
    }

    private fun resetUI() {
        resetLogo()
        closeImeDialogIfNeeded()
    }

    private fun resetLogo() {
        iv_logo!!.visibility = View.VISIBLE
        rl_top.y = mBodyDefaultMarginTop.toFloat()
    }

    private fun registerLayoutObserverForSoftKeyboard() {
        val view = window.decorView.rootView
        val observer = view.viewTreeObserver
        observer.addOnGlobalLayoutListener(mLayoutObserverListener)
    }

    override fun onPause() {
        super.onPause()
        removeLayoutObserverForSoftKeyboard()
    }

    private fun removeLayoutObserverForSoftKeyboard() {
        val view = window.decorView.rootView
        view.viewTreeObserver.removeOnGlobalLayoutListener(mLayoutObserverListener)
    }

    companion object {
        private const val MIN_INPUT_METHOD_HEIGHT = 200
        private const val ANIM_DURATION = 200

        // Permission request code of any integer value
        private const val PERMISSION_REQ_CODE = 1 shl 4
    }

}