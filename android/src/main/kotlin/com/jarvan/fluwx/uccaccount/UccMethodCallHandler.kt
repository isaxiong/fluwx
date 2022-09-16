package com.jarvan.fluwx.uccaccount

import android.app.Activity
import android.util.Log
import cn.wo.account.UnicomAccount
import com.example.punch.clock.WoAnalytics
import com.google.gson.Gson
import com.jarvan.fluwx.BuildConfig
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 * @author xiong
 * @since  2022/9/10
 * @description 用户中心
 **/
class UccMethodCallHandler(private val channel: MethodChannel) : MethodChannel.MethodCallHandler {

    private var mActivity: Activity? = null

    fun setActivity(activity: Activity?) {
        mActivity = activity
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "setupSdk" -> { //初始化SDK
                UnicomAccount.getInstance().init(
                    mActivity?.applicationContext,
                    call.argument<String>("appId"),
                    call.argument<String>("appKey"),
                    call.argument<String>("wxAppId"),
                    "", "", "", call.argument<String>("universalLink"), 1
                )
//                UnicomAccount.getInstance().setLogEnabled(true)
                UnicomAccount.getInstance().setOnekeyDrawableColor(
                    call.argument<String>("onekeyDrawableColor") ?: "unicom_bg_womusicstudy"
                )
                UnicomAccount.getInstance()
                    .setOnekeyLoginLogo(call.argument<String>("onekeyLoginLogo") ?: "womusicstudy")
            }
            "setupLogSdk" -> {//初始化打点SDK
                WoAnalytics.getInstance().init(
                    mActivity?.applicationContext,
                    call.argument<String>("appId"),
                    call.argument<Boolean>("isUrl") ?: true,
                    this.mActivity?.application, null
                )
                WoAnalytics.getInstance()
                    .setChannelName(if (BuildConfig.DEBUG) "debug" else call.argument<String>("channelName"))
                WoAnalytics.getInstance().setUId(call.argument<String>("userId"))
                WoAnalytics.getInstance()
                    .setLogEnabled(call.argument<Boolean>("logEnabled") ?: false)
            }
            "unInitLogSdk" -> {  //打点SDK去初始化
//                WoAnalytics.getInstance().unInit(mActivity?.applicationContext, null)
            }
            "startShowPageLog" -> {
                WoAnalytics.getInstance().startShowPage(
                    mActivity,
                    call.argument<String>("currentPageName") ?: this.javaClass.simpleName,
                    null
                )
            }
            "endShowPageLog" -> {
                WoAnalytics.getInstance().endShowPage(
                    mActivity,
                    call.argument<String>("currentPageName") ?: this.javaClass.simpleName,
                    null
                )
            }
            "openGroupLoginPage" -> {
                UnicomAccount.getInstance().launchGroupLoginPage(
                    mActivity
                ) { code, msg ->
                    Log.d(
                        "UccAccountPlugin",
                        "openGroupLoginPage UnicomAccount:code: $code, msg: $msg"
                    )
                    if (code == 200) {
                        //200 登陆成功
//                        val bean: UserCenterBean = Gson().fromJson(msg, object : TypeToken<UserCenterBean?>() {}.type)
                        val userInfo: Map<String, Any> = HashMap()
                        val mUserInfo = Gson().fromJson(msg, userInfo.javaClass)
                        channel.invokeMethod(
                            call.method, buildResult(
                                UccAccountResultType.completion,
                                mapOf(
                                    "granted" to true,
                                    "userInfo" to mUserInfo,
                                    "error" to null
                                )
                            )
                        )

                    } else {
                        channel.invokeMethod(
                            call.method, buildResult(
                                UccAccountResultType.completion,
                                mapOf(
                                    "granted" to false,
                                    "userInfo" to null,
                                    "error" to msg
                                )
                            )
                        )
                    }
                }
            }

            "getAccessToken" -> { //获取token
                channel.invokeMethod(call.method, UnicomAccount.getInstance().accessToken, result)
            }
            "logout" -> { //退出登录
                UnicomAccount.getInstance().logout { code, msg ->
                    Log.d("UccAccountPlugin", "UnicomAccount:logout_code: $code, msg: $msg")
                    result.success(
                        buildResult(
                            UccAccountResultType.logout,
                            mapOf("code" to code, "msg" to msg)
                        )
                    )
                    channel.invokeMethod(
                        call.method, buildResult(
                            UccAccountResultType.logout,
                            mapOf("code" to code, "msg" to msg)
                        )
                    )
                }
            }
            "closeLoginPage" -> {
//                val application = activity?.application as? UccApplication
//                application?.closeActivity()
//                mActivity?.finish()
            }
        }
    }

    fun buildResult(type: String, data: Any?): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["cb"] = type
        data?.run {
            map["data"] = this
        }
        return map
    }
}