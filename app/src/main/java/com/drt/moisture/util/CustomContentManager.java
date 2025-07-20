package com.drt.moisture.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 自定义品牌管理类
 * 管理自定义品牌名称和品牌图片的开关状态
 */
public class CustomContentManager {
    
    private static final String PREF_NAME = "custom_content_settings";
    private static final String KEY_CUSTOM_CONTENT_ENABLED = "custom_content_enabled";
    
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
     * 设置是否启用自定义品牌
     * @param enabled true=启用自定义品牌，false=使用默认内容
     */
    public void setCustomContentEnabled(boolean enabled) {
        sharedPreferences.edit()
            .putBoolean(KEY_CUSTOM_CONTENT_ENABLED, enabled)
            .apply();
        MyLog.d("CustomContentManager", "Custom content enabled: " + enabled);
    }
    
    /**
     * 获取是否启用自定义品牌
     * @return true=启用自定义品牌，false=使用默认内容
     */
    public boolean isCustomContentEnabled() {
        boolean enabled = sharedPreferences.getBoolean(KEY_CUSTOM_CONTENT_ENABLED, false);
        MyLog.d("CustomContentManager", "Custom content enabled: " + enabled);
        return enabled;
    }
    
    /**
     * 重置所有设置为默认值
     */
    public void resetToDefaults() {
        sharedPreferences.edit()
            .putBoolean(KEY_CUSTOM_CONTENT_ENABLED, false)
            .apply();
        MyLog.d("CustomContentManager", "Reset custom content settings to defaults");
    }
}