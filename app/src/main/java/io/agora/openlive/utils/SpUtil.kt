package io.agora.openlive.utils

import android.content.Context
import android.content.SharedPreferences
import io.agora.openlive.MyApplication

/**
 * Desc SharedPreferences存储工具类
 * Author Zy
 * Date 2020/6/8 17:17
 */
class SpUtil {

    companion object {

        private val instance = SpUtil()

        fun get(fileName: String? = null): ZySharedPreferences {
            val key = fileName ?: instance.fileName
            var zySharedPreferences = instance.zyShareMap[key]
            if (zySharedPreferences == null) {
                zySharedPreferences = ZySharedPreferences(key)
                instance.zyShareMap[key] = zySharedPreferences
            }
            return zySharedPreferences
        }
    }

    /**
     * 文件名
     */
    private var fileName = "sharedPreferences_info"


    val zyShareMap = HashMap<String, ZySharedPreferences>()

    class ZySharedPreferences(private val fileName: String) {
        private val sharedPreferences: SharedPreferences by lazy {
            MyApplication.getInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE)
        }

        /**
         * 保存String信息
         */
        fun setString(key: String, content: String) =
                sharedPreferences.edit().putString(key, content).apply()

        fun getString(key: String, content: String): String =
                sharedPreferences.getString(key, null) ?: content

        fun getString(key: String): String = getString(key, "")


        /**
         * 保存Boolean类型的信息
         */
        fun setBoolean(key: String, flag: Boolean) =
                sharedPreferences.edit().putBoolean(key, flag).apply()

        fun getBoolean(key: String, flag: Boolean): Boolean =
                sharedPreferences.getBoolean(key, flag)

        fun getBoolean(key: String): Boolean = getBoolean(key, false)


        /**
         * 保存Integer信息
         */
        fun setInteger(key: String, content: Int = 0) =
                sharedPreferences.edit().putInt(key, content).apply()

        fun getInteger(key: String): Int = getInteger(key, 0)

        fun getInteger(key: String, defValue: Int): Int = sharedPreferences.getInt(key, defValue)

        /**
         * 删除元素
         */
        fun remove(key: String) {
            sharedPreferences.edit().remove(key).apply()
        }


        /**
         * 清空share文件
         */
        fun clear() {
            sharedPreferences.edit().clear().apply()
        }
    }

    fun onDestroy() {
        zyShareMap.clear()
    }
}