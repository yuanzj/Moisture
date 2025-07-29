package com.drt.moisture.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 自定义品牌管理类
 * 管理自定义品牌名称和品牌图片的开关状态
 */
public class CustomContentManager {
    
    private static final String PREF_NAME = "custom_content_settings";
    private static final String KEY_BRAND_LOGO_VISIBLE = "brand_logo_visible";
    
    private static CustomContentManager instance;
    private SharedPreferences sharedPreferences;
    
    private CustomContentManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 获取单例实例
     */
    public static synchronized CustomContentManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomContentManager(context);
        }
        return instance;
    }
    
    /**
     * 设置品牌标识是否可见
     * @param visible true=显示品牌标识，false=隐藏品牌标识
     */
    public void setBrandLogoVisible(boolean visible) {
        sharedPreferences.edit()
            .putBoolean(KEY_BRAND_LOGO_VISIBLE, visible)
            .apply();
        MyLog.d("CustomContentManager", "Brand logo visible: " + visible);
    }
    
    /**
     * 获取品牌标识是否可见
     * @return true=显示品牌标识，false=隐藏品牌标识
     */
    public boolean isBrandLogoVisible() {
        boolean visible = sharedPreferences.getBoolean(KEY_BRAND_LOGO_VISIBLE, true); // 默认显示
        MyLog.d("CustomContentManager", "Brand logo visible: " + visible);
        return visible;
    }
    
    /**
     * 重置所有设置为默认值
     */
    public void resetToDefaults() {
        sharedPreferences.edit()
            .putBoolean(KEY_BRAND_LOGO_VISIBLE, true) // 默认显示品牌标识
            .apply();
        MyLog.d("CustomContentManager", "Reset brand logo settings to defaults");
    }
    
    /**
     * 为了向后兼容，保留旧方法但标记为过时
     * @deprecated 请使用 setBrandLogoVisible(boolean)
     */
    @Deprecated
    public void setCustomContentEnabled(boolean enabled) {
        setBrandLogoVisible(enabled);
    }
    
    /**
     * 为了向后兼容，保留旧方法但标记为过时
     * @deprecated 请使用 isBrandLogoVisible()
     */
    @Deprecated
    public boolean isCustomContentEnabled() {
        return isBrandLogoVisible();
    }
}