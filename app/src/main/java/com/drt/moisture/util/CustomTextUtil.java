package com.drt.moisture.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.database.Cursor;
import android.content.ContentResolver;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.InputStream;

public class CustomTextUtil {
    
    private static final String CUSTOM_TEXT_DIR = "QUAKE_Moisture";
    private static final String FACTORY_NAME_FILENAME = "factory_name.txt";
    private static final String FACTORY_NAME_SHORT_FILENAME = "factory_name_short.txt";
    
    /**
     * 获取自定义工厂名称文件的完整路径
     * 优先使用Download目录，这个目录在Android 15中更容易访问
     * 路径: /storage/emulated/0/Download/QUAKE_Moisture/factory_name.txt
     */
    public static String getFactoryNameFilePath() {
        return getFactoryNameFilePath(false);
    }
    
    /**
     * 获取自定义工厂名称文件的完整路径
     * @param isShort true=获取短名称文件路径，false=获取完整名称文件路径
     */
    public static String getFactoryNameFilePath(boolean isShort) {
        File textFile;
        
        // 针对Android 14+优化存储访问
        if (Build.VERSION.SDK_INT >= 34) { // Android 14+ (API 34+)
            // 优先使用应用专用存储目录
            File appExternalDir = new File(Environment.getExternalStorageDirectory(), "Android/data/com.drt.moisture/files/Documents");
            if (!appExternalDir.exists()) {
                appExternalDir.mkdirs();
            }
            File textDir = new File(appExternalDir, CUSTOM_TEXT_DIR);
            String filename = isShort ? FACTORY_NAME_SHORT_FILENAME : FACTORY_NAME_FILENAME;
            textFile = new File(textDir, filename);
            
            // 如果应用专用目录文件不存在，尝试从Download目录复制
            if (!textFile.exists()) {
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File downloadTextDir = new File(downloadDir, CUSTOM_TEXT_DIR);
                File downloadTextFile = new File(downloadTextDir, filename);
                if (downloadTextFile.exists()) {
                    try {
                        textDir.mkdirs();
                        copyFile(downloadTextFile, textFile);
                        MyLog.d("CustomTextUtil", "Copied file from Download to app storage: " + textFile.getAbsolutePath());
                    } catch (Exception e) {
                        MyLog.e("CustomTextUtil", "Failed to copy file: " + e.getMessage());
                    }
                }
            }
        } else {
            // Android 13及以下版本使用传统方式
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File textDir = new File(downloadDir, CUSTOM_TEXT_DIR);
            String filename = isShort ? FACTORY_NAME_SHORT_FILENAME : FACTORY_NAME_FILENAME;
            textFile = new File(textDir, filename);
        }
        
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
        String filename = isShort ? FACTORY_NAME_SHORT_FILENAME : FACTORY_NAME_FILENAME;
        MyLog.d("CustomTextUtil", "Loading factory name (" + fileType + ")...");
        
        // 首先尝试创建目录
        createCustomTextDir();
        
        // 使用StorageHelper获取文件路径
        String filePath = StorageHelper.readFileFromMultipleLocations(context, filename);
        if (filePath != null && StorageHelper.isFileReadable(filePath)) {
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
        
        // Android 14+ 使用媒体存储API作为备用方案
        if (Build.VERSION.SDK_INT >= 34) {
            String content = loadFactoryNameFromMediaStore(context, isShort);
            if (content != null && !content.isEmpty()) {
                MyLog.d("CustomTextUtil", "Successfully loaded custom factory name (" + fileType + ") from MediaStore: " + content);
                return content;
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
     */
    public static boolean createCustomTextDir() {
        try {
            // Android 14+ 优先使用应用专用存储
            if (Build.VERSION.SDK_INT >= 34) {
                File appExternalDir = new File(Environment.getExternalStorageDirectory(), "Android/data/com.drt.moisture/files/Documents");
                File textDir = new File(appExternalDir, CUSTOM_TEXT_DIR);
                
                if (!textDir.exists()) {
                    boolean created = textDir.mkdirs();
                    MyLog.d("CustomTextUtil", "App-specific text directory created: " + created + ", path: " + textDir.getAbsolutePath());
                    return created;
                }
                return true;
            } else {
                // 传统方式：使用Download目录
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File textDir = new File(downloadDir, CUSTOM_TEXT_DIR);
                
                if (!textDir.exists()) {
                    boolean created = textDir.mkdirs();
                    MyLog.d("CustomTextUtil", "Text directory created: " + created + ", path: " + textDir.getAbsolutePath());
                    return created;
                }
                return true;
            }
        } catch (Exception e) {
            MyLog.e("CustomTextUtil", "Failed to create custom text directory: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 通过MediaStore API加载工厂名称文件（Android 14+兼容）
     */
    private static String loadFactoryNameFromMediaStore(Context context, boolean isShort) {
        try {
            String filename = isShort ? FACTORY_NAME_SHORT_FILENAME : FACTORY_NAME_FILENAME;
            String relativePath = "Download/" + CUSTOM_TEXT_DIR + "/" + filename;
            
            // 使用MediaStore查询文件
            ContentResolver resolver = context.getContentResolver();
            Uri uri = MediaStore.Files.getContentUri("external");
            
            String[] projection = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME};
            String selection = MediaStore.Files.FileColumns.RELATIVE_PATH + "=? AND " + 
                             MediaStore.Files.FileColumns.DISPLAY_NAME + "=?";
            String[] selectionArgs = {"Download/" + CUSTOM_TEXT_DIR + "/", filename};
            
            Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                long id = cursor.getLong(idColumn);
                Uri fileUri = Uri.withAppendedPath(uri, String.valueOf(id));
                
                InputStream inputStream = resolver.openInputStream(fileUri);
                if (inputStream != null) {
                    InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
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
                    inputStream.close();
                    cursor.close();
                    
                    String result = content.toString().trim();
                    MyLog.d("CustomTextUtil", "Loaded factory name from MediaStore: " + result);
                    return result;
                }
            }
            
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            MyLog.e("CustomTextUtil", "Failed to load factory name from MediaStore: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
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