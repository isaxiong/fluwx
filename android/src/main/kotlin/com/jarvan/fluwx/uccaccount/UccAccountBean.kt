package com.jarvan.fluwx.uccaccount

//用户中心bean
data class UserCenterBean(
        val code: Int,
        val data: UserData,
        val msg: String
)

data class UserData(
        val accessTokenInfo: AccessTokenInfo,
        val userInfo: UserCenterInfo
)

data class AccessTokenInfo(
        val access_token: String,
        val expires_in: Int,
        val login_way: String,
        val refresh_token: String,
        val scope: String,
        val token_type: String,
        val uid: String
)

data class UserCenterInfo(
        val headIco: String,
        val mobile: String,
        val nickName: String,
        val registerWay: Int,
        val thirdInfos: List<ThirdInfo>,
        val uid: String
)

data class ThirdInfo(
        val openId: String,
        val source: Int,
        val unionId: String
)
