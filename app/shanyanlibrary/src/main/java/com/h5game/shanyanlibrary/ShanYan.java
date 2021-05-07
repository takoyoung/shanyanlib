package com.h5game.shanyanlibrary;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.h5game.thirdpartycallback.ThirdPartyCallback;

public class ShanYan extends ThirdPartyCallback {
    public ShanYan(Activity activity, String appId, String className){
        super(className);
        mActivity = activity;

        OneKeyLoginManager.getInstance().init(mActivity, appId, (code, result) -> getPhoneInfo());
    }

    public void getPhoneInfo(){
        OneKeyLoginManager.getInstance().getPhoneInfo((_code, _result) -> {

        });
    }

    public void setAuthThemeConfig(){
        OneKeyLoginManager.getInstance().setAuthThemeConfig(null);
    }

    public void login(int callbackId){
        if(!checkCallbackId(callbackId)){
            return;
        }

        if(!isHasSimCard(mActivity)){
            callErr(-1, 3, "未检测到SIM卡");
            return;
        }

        OneKeyLoginManager.getInstance().setAuthThemeConfig(createShanyanUI());

        OneKeyLoginManager.getInstance().openLoginAuth(true, (i, s) -> {
            if (i != 1000){
                JSONObject json = JSON.parseObject(s);
                callErr(-1, 1, json.getString("message"));
            }

        }, (code, result) -> {
            if (code == 1000) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                callSuccess(jsonObject);
            } else {
                JSONObject json = JSON.parseObject(result);
                callErr(-1, 2, json.getString("message"));
            }
        });
    }

    public ShanYanUIConfig createShanyanUI(){
        Drawable logoImgPath = mActivity.getResources().getDrawable(mActivity.getResources().getIdentifier("shanyan_logo", "drawable", mActivity.getPackageName()));
        ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
                .setNavText("")  //设置导航栏标题文字
                .setNavTextColor(0xff080808) //设置标题栏文字颜色

                //授权页logo
                .setLogoImgPath(logoImgPath)  //设置logo图片
                .setLogoWidth(108)   //设置logo宽度
                .setLogoHeight(45)   //设置logo高度
                .setLogoOffsetY(25)  //设置logo相对于标题栏下边缘y偏移
                .setLogoHidden(false)   //是否隐藏logo


                //授权页隐私栏：
                .setAppPrivacyOne("闪验用户协议", "https://api.253.com/api_doc/yin-si-zheng-ce/wei-hu-wang-luo-an-quan-sheng-ming.html")  //设置隐私条款1名称和URL(名称，url)
                .setAppPrivacyTwo("闪验隐私政策", "https://api.253.com/api_doc/yin-si-zheng-ce/ge-ren-xin-xi-bao-hu-sheng-ming.html")  //设置隐私条款2名称和URL(名称，url)
                .setAppPrivacyColor(0xff666666, 0xff0085d0)   //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                .setPrivacyOffsetBottomY(20)//设置隐私条款相对于屏幕下边缘y偏
                .setPrivacyState(true)//设置隐私条款

                .build();

        return uiConfig;
    }

    public boolean isHasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
                return false;
            default:
                return true;
        }
    }
}
