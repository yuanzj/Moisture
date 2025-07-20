package com.drt.moisture.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.InputStream;

public class CustomTextUtil {
    
    private static final String CUSTOM_TEXT_DIR = "HKYQ_Moisture";
    private static final String FACTORY_NAME_FILENAME = "factory_name.txt";
    private static final String FACTORY_NAME_SHORT_FILENAME = "factory_name_short.txt";
    
    // 自定义图片文件名
    private static final String CUSTOM_LOGO_FILENAME = "custom_logo.png";
    private static final String CUSTOM_BACKGROUND_FILENAME = "custom_background.png";
    
    /**
     * 获取自定义工厂名称文件的完整路径
     * 优先使用Download目录，这个目录在Android 15中更容易访问
     * 路径: /storage/emulated/0/Download/HKYQ_Moisture/factory_name.txt
     */
    public static String getFactoryNameFilePath() {
        return getFactoryNameFilePath(false);
    }
    
    /**
     * 获取自定义工厂名称文件的完整路径
     * 统一使用Download目录，方便运营人员通过文件管理器编辑
     * @param isShort true=获取短名称文件路径，false=获取完整名称文件路径
     */
    public static String getFactoryNameFilePath(boolean isShort) {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File textDir = new File(downloadDir, CUSTOM_TEXT_DIR);
        String filename = isShort ? FACTORY_NAME_SHORT_FILENAME : FACTORY_NAME_FILENAME;
        File textFile = new File(textDir, filename);
        
        MyLog.d("CustomTextUtil", "Factory name file path (" + (isShort ? "short" : "full") + "): " + textFile.getAbsolutePath());
        return textFile.getAbsolutePath();
    }
    
    /**
     * 检查自定义工厂名称文件是否存在
     */
    public static boolean isFactoryNameFileExists() {
        return isFactoryNameFileExists(false);
    }
    
    /**
     * 检查自定义工厂名称文件是否存在
     * @param isShort true=检查短名称文件，false=检查完整名称文件
     */
    public static boolean isFactoryNameFileExists(boolean isShort) {
        File textFile = new File(getFactoryNameFilePath(isShort));
        boolean exists = textFile.exists();
        boolean isFile = textFile.isFile();
        long size = textFile.length();
        
        String fileType = isShort ? "short" : "full";
        MyLog.d("CustomTextUtil", "Factory name file (" + fileType + ") exists: " + exists + ", isFile: " + isFile + ", size: " + size);
        
        if (exists && isFile && size > 0) {
            MyLog.d("CustomTextUtil", "Custom factory name file (" + fileType + ") found");
            return true;
        } else {
            MyLog.d("CustomTextUtil", "Custom factory name file (" + fileType + ") not found, using default");
            return false;
        }
    }
    
    /**
     * 从txt文件加载工厂名称，如果不存在则使用默认值
     * @param context 上下文
     * @param defaultFactoryName 默认工厂名称
     * @return 工厂名称文本
     */
    public static String loadFactoryName(Context context, String defaultFactoryName) {
        return loadFactoryName(context, defaultFactoryName, false);
    }
    
    /**
     * 从txt文件加载工厂名称，如果不存在则使用默认值
     * @param context 上下文
     * @param defaultFactoryName 默认工厂名称
     * @param isShort true=加载短名称，false=加载完整名称
     * @return 工厂名称文本
     */
    public static String loadFactoryName(Context context, String defaultFactoryName, boolean isShort) {
        String fileType = isShort ? "short" : "full";
        MyLog.d("CustomTextUtil", "Loading factory name (" + fileType + ")...");
        
        // 检查是否启用自定义品牌
        CustomContentManager customContentManager = CustomContentManager.getInstance(context);
        if (!customContentManager.isCustomContentEnabled()) {
            MyLog.d("CustomTextUtil", "Custom content disabled, using default factory name (" + fileType + "): " + defaultFactoryName);
            return defaultFactoryName;
        }
        
        // 首先尝试创建目录
        createCustomTextDir();
        
        // 直接从Download目录读取文件
        String filePath = getFactoryNameFilePath(isShort);
        if (StorageHelper.isFileReadable(filePath)) {
            try {
                MyLog.d("CustomTextUtil", "Attempting to load factory name (" + fileType + ") from: " + filePath);
                
                FileInputStream fis = new FileInputStream(filePath);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    if (content.length() > 0) {
                        content.append("\n");
                    }
                    content.append(line);
                }
                
                br.close();
                isr.close();
                fis.close();
                
                String factoryName = content.toString().trim();
                if (!factoryName.isEmpty()) {
                    MyLog.d("CustomTextUtil", "Successfully loaded custom factory name (" + fileType + "): " + factoryName);
                    return factoryName;
                } else {
                    MyLog.w("CustomTextUtil", "Factory name file (" + fileType + ") is empty");
                }
                
            } catch (Exception e) {
                MyLog.e("CustomTextUtil", "Failed to load custom factory name (" + fileType + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        MyLog.d("CustomTextUtil", "Using default factory name (" + fileType + "): " + defaultFactoryName);
        return defaultFactoryName;
    }
    
    /**
     * 从txt文件加载工厂名称，如果不存在则从strings.xml获取默认值
     * @param context 上下文
     * @param stringResId 字符串资源ID (如 R.string.factory_name_full)
     * @return 工厂名称文本
     */
    public static String loadFactoryName(Context context, int stringResId) {
        String defaultFactoryName = context.getString(stringResId);
        return loadFactoryName(context, defaultFactoryName, false);
    }
    
    /**
     * 从txt文件加载工厂名称，如果不存在则从strings.xml获取默认值
     * @param context 上下文
     * @param stringResId 字符串资源ID (如 R.string.factory_name 或 R.string.factory_name_full)
     * @param isShort true=加载短名称，false=加载完整名称
     * @return 工厂名称文本
     */
    public static String loadFactoryName(Context context, int stringResId, boolean isShort) {
        String defaultFactoryName = context.getString(stringResId);
        return loadFactoryName(context, defaultFactoryName, isShort);
    }
    
    /**
     * 创建自定义文本目录
     * 统一使用Download目录
     */
    public static boolean createCustomTextDir() {
        try {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File textDir = new File(downloadDir, CUSTOM_TEXT_DIR);
            
            if (!textDir.exists()) {
                boolean created = textDir.mkdirs();
                MyLog.d("CustomTextUtil", "Download text directory created: " + created + ", path: " + textDir.getAbsolutePath());
                return created;
            }
            MyLog.d("CustomTextUtil", "Download text directory already exists: " + textDir.getAbsolutePath());
            return true;
        } catch (Exception e) {
            MyLog.e("CustomTextUtil", "Failed to create custom text directory: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * 加载自定义logo图片
     * @param context 上下文
     * @return Bitmap对象，如果未启用自定义品牌或文件不存在则返回null
     */
    public static Bitmap loadCustomLogo(Context context) {
        return loadCustomImage(context, CUSTOM_LOGO_FILENAME);
    }
    
    /**
     * 加载自定义背景图片
     * @param context 上下文
     * @return Bitmap对象，如果未启用自定义品牌或文件不存在则返回null
     */
    public static Bitmap loadCustomBackground(Context context) {
        return loadCustomImage(context, CUSTOM_BACKGROUND_FILENAME);
    }
    
    /**
     * 加载自定义图片的通用方法
     * @param context 上下文
     * @param filename 图片文件名
     * @return Bitmap对象，如果未启用自定义品牌或文件不存在则返回null
     */
    private static Bitmap loadCustomImage(Context context, String filename) {
        MyLog.d("CustomTextUtil", "Loading custom image: " + filename);
        
        // 检查是否启用自定义品牌
        CustomContentManager customContentManager = CustomContentManager.getInstance(context);
        if (!customContentManager.isCustomContentEnabled()) {
            MyLog.d("CustomTextUtil", "Custom content disabled, skipping image load: " + filename);
            return null;
        }
        
        // 构建图片文件路径
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File imageDir = new File(downloadDir, CUSTOM_TEXT_DIR);
        File imageFile = new File(imageDir, filename);
        
        if (imageFile.exists() && imageFile.canRead()) {
            try {
                MyLog.d("CustomTextUtil", "Loading custom image from: " + imageFile.getAbsolutePath());
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    MyLog.d("CustomTextUtil", "Successfully loaded custom image: " + filename + " (" + bitmap.getWidth() + "x" + bitmap.getHeight() + ")");
                    return bitmap;
                } else {
                    MyLog.w("CustomTextUtil", "Failed to decode image file: " + filename);
                }
            } catch (Exception e) {
                MyLog.e("CustomTextUtil", "Error loading custom image: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            MyLog.d("CustomTextUtil", "Custom image file not found or not readable: " + imageFile.getAbsolutePath());
        }
        
        return null;
    }
    
    /**
     * 检查自定义图片是否存在
     * @param context 上下文
     * @param filename 图片文件名
     * @return true=文件存在且可读，false=文件不存在或不可读
     */
    public static boolean isCustomImageAvailable(Context context, String filename) {
        // 检查是否启用自定义品牌
        CustomContentManager customContentManager = CustomContentManager.getInstance(context);
        if (!customContentManager.isCustomContentEnabled()) {
            return false;
        }
        
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File imageDir = new File(downloadDir, CUSTOM_TEXT_DIR);
        File imageFile = new File(imageDir, filename);
        
        return imageFile.exists() && imageFile.canRead() && imageFile.length() > 0;
    }
    
    /**
     * 复制文件
     */
    private static void copyFile(File source, File destination) throws Exception {
        FileInputStream fis = new FileInputStream(source);
        java.io.FileOutputStream fos = new java.io.FileOutputStream(destination);
        
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        
        fis.close();
        fos.close();
    }
}