package io.agora.openlive.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.agora.openlive.R
import io.agora.rtc.Constants
import kotlinx.android.synthetic.main.agora_act_choose_role.*

/**
 * Desc 角色选择页
 * Author ZY
 * Mail zuoyu98@foxmail.com
 * Date 2021年7月8日 17:12:11
 */
class RoleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agora_act_choose_role)


        cl_broadcaster.setOnClickListener {
            LiveActivity.intent(this, Constants.CLIENT_ROLE_BROADCASTER)
        }

        cl_audience.setOnClickListener {
            LiveActivity.intent(this, Constants.CLIENT_ROLE_AUDIENCE)
        }
    }
}