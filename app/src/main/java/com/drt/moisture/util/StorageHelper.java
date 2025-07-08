package com.drt.moisture.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;

import java.io.File;

/**
 * 存储访问帮助类，处理不同Android版本的存储访问兼容性
 */
public class StorageHelper {
    
    /**
     * 检查是否可以访问外部存储
     */
    public static boolean canAccessExternalStorage(Context context) {
        if (Build.VERSION.SDK_INT >= 34) {
            // Android 14+ 需要更细粒度的权限检查
            return AppPermission.isGrantExternalRW((Activity) context) || 
                   AppPermission.hasFullStorageAccess();
        } else {
            return AppPermission.isGrantExternalRW((Activity) context);
        }
    }
    
    /**
     * 获取推荐的存储目录
     */
    public static File getRecommendedStorageDirectory(Context context) {
        if (Build.VERSION.SDK_INT >= 34) {
            // Android 14+ 优先使用应用专用目录
            File appExternalDir = new File(Environment.getExternalStorageDirectory(), 
                                         "Android/data/" + context.getPackageName() + "/files/Documents");
            if (!appExternalDir.exists()) {
                appExternalDir.mkdirs();
            }
            return appExternalDir;
        } else {
            // 传统方式使用Download目录
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }
    }
    
    /**
     * 检查指定文件是否存在且可读
     */
    public static boolean isFileReadable(String filePath) {
        try {
            File file = new File(filePath);
            return file.exists() && file.canRead() && file.isFile();
        } catch (Exception e) {
            MyLog.e("StorageHelper", "Error checking file readability: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 显示存储权限设置对话框（Android 14+）
     */
    public static void showStoragePermissionDialog(Activity activity) {
        if (Build.VERSION.SDK_INT >= 34) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("存储权限设置");
            builder.setMessage("为了读取自定义工厂名称文件，应用需要访问存储权限。\n\n" +
                             "请在设置中允许应用访问「所有文件」或确保将自定义文件放在应用专用目录中。");
            
            builder.setPositiveButton("打开设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppPermission.requestManageExternalStorage(activity);
                }
            });
            
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            
            builder.setNeutralButton("了解更多", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showStorageInstructionDialog(activity);
                }
            });
            
            builder.show();
        }
    }
    
    /**
     * 显示存储使用说明对话框
     */
    private static void showStorageInstructionDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("自定义工厂名称使用说明");
        builder.setMessage("在Android 14+系统中，您可以通过以下方式使用自定义工厂名称：\n\n" +
                         "方式1：将文件放在应用专用目录\n" +
                         "• 路径：Android/data/com.drt.moisture/files/Documents/QUAKE_Moisture/\n" +
                         "• 文件：factory_name.txt（完整名称）\n" +
                         "• 文件：factory_name_short.txt（短名称）\n\n" +
                         "方式2：授予完整存储权限\n" +
                         "• 在设置中允许应用访问「所有文件」\n" +
                         "• 然后将文件放在：Download/QUAKE_Moisture/ 目录中");
        
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.show();
    }
    
    /**
     * 尝试从多个位置读取文件
     */
    public static String readFileFromMultipleLocations(Context context, String fileName) {
        // 位置1：应用专用目录（Android 14+优先）
        if (Build.VERSION.SDK_INT >= 34) {
            File appDir = new File(getRecommendedStorageDirectory(context), "QUAKE_Moisture");
            File appFile = new File(appDir, fileName);
            if (isFileReadable(appFile.getAbsolutePath())) {
                MyLog.d("StorageHelper", "Reading from app-specific directory: " + appFile.getAbsolutePath());
                return appFile.getAbsolutePath();
            }
        }
        
        // 位置2：Download目录（传统位置）
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File downloadFile = new File(new File(downloadDir, "QUAKE_Moisture"), fileName);
        if (isFileReadable(downloadFile.getAbsolutePath())) {
            MyLog.d("StorageHelper", "Reading from Download directory: " + downloadFile.getAbsolutePath());
            return downloadFile.getAbsolutePath();
        }
        
        MyLog.w("StorageHelper", "File not found in any location: " + fileName);
        return null;
    }
    
    /**
     * 检查并提示用户如何解决存储访问问题
     */
    public static void checkAndPromptStorageAccess(Activity activity) {
        if (Build.VERSION.SDK_INT >= 34) {
            if (!canAccessExternalStorage(activity)) {
                MyLog.w("StorageHelper", "Storage access limited on Android 14+");
                
                // 检查是否有自定义文件在传统位置
                boolean hasFileInDownload = isFileReadable(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + 
                    "/QUAKE_Moisture/factory_name.txt") ||
                    isFileReadable(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + 
                    "/QUAKE_Moisture/factory_name_short.txt");
                
                if (hasFileInDownload) {
                    // 如果在Download目录有文件但无法访问，提示用户
                    showStoragePermissionDialog(activity);
                }
            }
        }
    }
}