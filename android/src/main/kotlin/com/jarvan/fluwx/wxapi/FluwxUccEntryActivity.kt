package com.jarvan.fluwx.wxapi

import cn.wo.account.BaseWXEntryActivity
import com.jarvan.fluwx.handlers.FluwxRequestHandler
import com.jarvan.fluwx.handlers.FluwxResponseHandler
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp

/**
 * @author xiong
 * @since  2022/9/10
 * @description 整合用户中心UccAccount微信登录
 **/
class FluwxUccEntryActivity : BaseWXEntryActivity() {

    override fun onWxResp(baseResp: BaseResp) {
        FluwxResponseHandler.handleResponse(baseResp)
        finish()
    }

    override fun onWxReq(baseReq: BaseReq) {
        // FIXME: 可能是官方的Bug，从微信拉起APP的Intent类型不对，无法跳转回Flutter Activity
        // 稳定复现场景：微信版本为7.0.5，小程序SDK为2.7.7
        FluwxRequestHandler.onReq(baseReq,this)
    }

    override fun finishInChild(): Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            finishAndRemoveTask()
//        }
//        return true
        return false
    }
}