package com.drt.moisture.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.drt.moisture.R;

import java.io.File;

public class CustomLogoUtil {
    
    private static final String CUSTOM_LOGO_DIR = "QUAKE_Moisture";
    private static final String CUSTOM_LOGO_FILENAME = "custom_logo.png";
    
    /**
     * 获取自定义logo的完整路径
     * 使用Download目录，这个目录在Android 15中更容易访问
     * 路径: /storage/emulated/0/Download/QUAKE_Moisture/custom_logo.png
     */
    public static String getCustomLogoPath() {
        // 使用Download目录，这在Android所有版本中都比较容易访问
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File logoDir = new File(downloadDir, CUSTOM_LOGO_DIR);
        File logoFile = new File(logoDir, CUSTOM_LOGO_FILENAME);
        
        MyLog.d("CustomLogoUtil", "Android version: " + Build.VERSION.SDK_INT + ", Custom logo path: " + logoFile.getAbsolutePath());
        return logoFile.getAbsolutePath();
    }
    
    /**
     * 检查自定义logo是否存在
     */
    public static boolean isCustomLogoExists() {
        File logoFile = new File(getCustomLogoPath());
        boolean exists = logoFile.exists();
        boolean isFile = logoFile.isFile();
        long size = logoFile.length();
        
        MyLog.d("CustomLogoUtil", "Logo file exists: " + exists + ", isFile: " + isFile + ", size: " + size);
        
        if (exists && isFile && size > 0) {
            MyLog.d("CustomLogoUtil", "Custom logo found, using custom logo");
            return true;
        } else {
            MyLog.d("CustomLogoUtil", "Custom logo not found, using default logo");
            return false;
        }
    }
    
    /**
     * 加载logo drawable，优先使用自定义logo，如果不存在则使用默认logo
     * @param context 上下文
     * @return logo drawable
     */
    public static Drawable loadLogoDrawable(Context context) {
        MyLog.d("CustomLogoUtil", "Loading logo drawable...");
        
        if (isCustomLogoExists()) {
            try {
                String logoPath = getCustomLogoPath();
                MyLog.d("CustomLogoUtil", "Attempting to load custom logo from: " + logoPath);
                
                Bitmap bitmap = BitmapFactory.decodeFile(logoPath);
                if (bitmap != null) {
                    MyLog.d("CustomLogoUtil", "Successfully loaded custom logo, size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                    return new BitmapDrawable(context.getResources(), bitmap);
                } else {
                    MyLog.e("CustomLogoUtil", "Failed to decode bitmap from file");
                }
            } catch (Exception e) {
                MyLog.e("CustomLogoUtil", "Failed to load custom logo: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        MyLog.d("CustomLogoUtil", "Using default logo");
        return ContextCompat.getDrawable(context, R.mipmap.logo_title);
    }
    
    /**
     * 为ImageView设置logo，优先使用自定义logo，如果不存在则使用默认logo
     * @param context 上下文
     * @param imageView 要设置的ImageView
     */
    public static void setLogoToImageView(Context context, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        
        Drawable logoDrawable = loadLogoDrawable(context);
        imageView.setImageDrawable(logoDrawable);
    }
    
    /**
     * 创建自定义logo目录
     */
    public static boolean createCustomLogoDir() {
        try {
            // 使用Download目录
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File logoDir = new File(downloadDir, CUSTOM_LOGO_DIR);
            
            if (!logoDir.exists()) {
                boolean created = logoDir.mkdirs();
                MyLog.d("CustomLogoUtil", "Logo directory created: " + created + ", path: " + logoDir.getAbsolutePath());
                return created;
            }
            return true;
        } catch (Exception e) {
            MyLog.e("CustomLogoUtil", "Failed to create custom logo directory: " + e.getMessage());
            return false;
        }
    }
}