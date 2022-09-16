import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class UccAccountMixinFluwx {
  static const MethodChannel _channel = const MethodChannel('ucc_account_mixin_fluwx');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  ///原生回调
  static Future<dynamic> _myHandler(MethodCall methodCall) async {
    print(methodCall.method);
    final method = methodCall.method,
        args = methodCall.arguments,
        cb = args['cb'],
        data = args['data'];
    switch (method) {
      case 'openLoginPage':
        switch (cb) {
          case 'loading':
            _loadingHander?.call(data);
            break;
          case 'completion':
            try {
              final granted = data['granted'],
                  userInfo = data['userInfo'],
                  error = data['error'];
              _completionHandler?.call(granted, info: userInfo, error: error);
            } catch (e) {
              print('openLoginPage-completion error $e');
            }
            break;
          default:
        }
        break;
      case 'openGroupLoginPage':
        switch (cb) {

          case 'completion':
            try {
              final granted = data['granted'],
                  userInfo = data['userInfo'],
                  error = data['error'];
              _completionHandler?.call(granted, info: userInfo, error: error);
            } catch (e) {
              print('openGroupLoginPage-completion error $e');
            }
            break;
          default:
        }
        break;
    }
  }

  ///初始化SDK
  static Future<void> setupSdk(
      {required String appId,
      required String appKey,
      String? universalLink,
      String? wxAppId,
      String? onekeyDrawableColor,
      String? onekeyLoginLogo,
      bool isTestMode = false}) async {
    _channel.setMethodCallHandler(_myHandler);
    await _channel.invokeMethod('setupSdk', {
      'appId': appId,
      'appKey': appKey,
      'universalLink': universalLink,
      'wxAppId': wxAppId,
      'onekeyDrawableColor': onekeyDrawableColor,
      'onekeyLoginLogo': onekeyLoginLogo,
      'isTestMode': isTestMode,
    });
  }

  ///初始化打点SDK
  static Future<void> setupLogSdk(
      {required String appId,
      required bool isUrl,
      required String channelName,
      required String userId,
      bool? logEnabled}) async{
    await _channel.invokeMethod('setupLogSdk',{
      'appId':appId,
      'isUrl':isUrl,
      'channelName':channelName,
      'userId':userId,
      'logEnabled':logEnabled
    });
  }

  ///打点SDK去初始化
  static Future<void> unInitLogSdk() async{
    await _channel.invokeMethod('unInitLogSdk');
  }

  static Future<void> startShowPageLog({required String currentPageName}) async{
    await _channel.invokeMethod('startShowPageLog',{
      'currentPageName':currentPageName
    });
  }

  static Future<void> endShowPageLog({required String currentPageName}) async{
    await _channel.invokeMethod('endShowPageLog',{
      'currentPageName':currentPageName
    });
  }

  static void Function(bool success, {dynamic? info, dynamic? error})?
      _completionHandler;
  static void Function(bool isloading)? _loadingHander;

  ///打开登录页
  static Future<void> openLoginPage(
      {void Function(bool isloading)? loadingHander,
      void Function(bool success, {dynamic? info, dynamic? error})?
          completionHandler}) async {
    _loadingHander = loadingHander;
    _completionHandler = completionHandler;
    await _channel.invokeMethod('openLoginPage');
  }
  ///打开登录页(“包含一键登录”)
  static Future<void> openGroupLoginPage(
      {void Function(bool isloading)? loadingHander,
      void Function(bool success, {dynamic? info, dynamic? error})?
        completionHandler}) async {
    _loadingHander = loadingHander;
    _completionHandler = completionHandler;
    await _channel.invokeMethod('openGroupLoginPage');
  }


  ///关闭登录页
  static void closeLoginPage() {
    _channel.invokeMethod('closeLoginPage');
  }

  ///退出登录
  static Future<void> logout({required String token}) async {
    await _channel.invokeMethod('logout', token);
  }

  static Future<String?> getAccessToken() async {
    await _channel.invokeMethod('getAccessToken');
  }

  ///接管open url
  static void handleOpenUrl(String url) {
    _channel.invokeMethod('handleOpenUrl', url);
  }
}
