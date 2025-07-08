package com.drt.moisture.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

public class AppPermission {

    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_BLUETOOTH = 2;
    
    // API level constants for backward compatibility
    private static final int API_33_TIRAMISU = 33;
    private static final int API_31_S = 31;

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= 34) { // Android 14+ (API 34+)
            // Android 14+ 使用新的精细化权限
            try {
                boolean hasMediaImages = activity.checkSelfPermission("android.permission.READ_MEDIA_IMAGES") == PackageManager.PERMISSION_GRANTED;
                boolean hasVisualUserSelected = activity.checkSelfPermission("android.permission.READ_MEDIA_VISUAL_USER_SELECTED") == PackageManager.PERMISSION_GRANTED;
                boolean hasManageStorage = Environment.isExternalStorageManager();
                
                MyLog.d("AppPermission", "Android 14+ permissions - Images: " + hasMediaImages + ", Visual: " + hasVisualUserSelected + ", Manage: " + hasManageStorage);
                return hasMediaImages || hasVisualUserSelected || hasManageStorage;
            } catch (Exception e) {
                MyLog.e("AppPermission", "Android 14+ permission check failed: " + e.getMessage());
                // 降级到传统权限
                return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }
        } else if (Build.VERSION.SDK_INT >= API_33_TIRAMISU) { // Android 13 (API 33)
            // Android 13 使用新的媒体权限
            try {
                return activity.checkSelfPermission("android.permission.READ_MEDIA_IMAGES") == PackageManager.PERMISSION_GRANTED ||
                       Environment.isExternalStorageManager();
            } catch (Exception e) {
                // 降级到传统权限
                return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-12 使用传统存储权限
            return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestStoragePermissions(Activity activity) {
        List<String> permissions = new ArrayList<>();
        
        if (Build.VERSION.SDK_INT >= 34) { // Android 14+ (API 34+)
            // Android 14+ 新的精细化权限
            try {
                if (activity.checkSelfPermission("android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.READ_MEDIA_IMAGES");
                }
                if (activity.checkSelfPermission("android.permission.READ_MEDIA_VISUAL_USER_SELECTED") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.READ_MEDIA_VISUAL_USER_SELECTED");
                }
                // 在Android 14+中，尝试请求管理存储权限以获得完整访问权
                if (!Environment.isExternalStorageManager()) {
                    MyLog.d("AppPermission", "Android 14+ requesting MANAGE_EXTERNAL_STORAGE for full access");
                    // 注意：MANAGE_EXTERNAL_STORAGE需要通过Intent跳转请求，不能直接加入permissions数组
                }
            } catch (Exception e) {
                MyLog.e("AppPermission", "Android 14+ permission request failed: " + e.getMessage());
                // 降级到传统权限
                addLegacyStoragePermissions(activity, permissions);
            }
        } else if (Build.VERSION.SDK_INT >= API_33_TIRAMISU) { // Android 13 (API 33)
            // Android 13 媒体权限
            try {
                if (activity.checkSelfPermission("android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.READ_MEDIA_IMAGES");
                }
                if (activity.checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.READ_MEDIA_AUDIO");
                }
                if (activity.checkSelfPermission("android.permission.READ_MEDIA_VIDEO") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.READ_MEDIA_VIDEO");
                }
            } catch (Exception e) {
                // 降级到传统权限
                addLegacyStoragePermissions(activity, permissions);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-12 传统存储权限
            addLegacyStoragePermissions(activity, permissions);
        }
        
        if (!permissions.isEmpty()) {
            MyLog.d("AppPermission", "Requesting permissions: " + permissions.toString());
            activity.requestPermissions(permissions.toArray(new String[0]), REQUEST_CODE_STORAGE);
        } else {
            MyLog.d("AppPermission", "No storage permissions to request");
        }
    }
    
    private static void addLegacyStoragePermissions(Activity activity, List<String> permissions) {
        if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    public static void requestBluetoothPermissions(Activity activity) {
        List<String> permissions = new ArrayList<>();
        
        if (Build.VERSION.SDK_INT >= API_31_S) { // Android 12+ (API 31+)
            // Android 12+ 新的蓝牙权限
            try {
                if (activity.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.BLUETOOTH_CONNECT");
                }
                if (activity.checkSelfPermission("android.permission.BLUETOOTH_SCAN") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.BLUETOOTH_SCAN");
                }
                if (activity.checkSelfPermission("android.permission.BLUETOOTH_ADVERTISE") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.BLUETOOTH_ADVERTISE");
                }
            } catch (Exception e) {
                // 降级到传统蓝牙权限
                addLegacyBluetoothPermissions(activity, permissions);
            }
        } else {
            // Android 11 及以下版本的蓝牙权限
            addLegacyBluetoothPermissions(activity, permissions);
        }
        
        // 位置权限对于蓝牙扫描仍然需要
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        
        if (!permissions.isEmpty()) {
            activity.requestPermissions(permissions.toArray(new String[0]), REQUEST_CODE_BLUETOOTH);
        }
    }
    
    private static void addLegacyBluetoothPermissions(Activity activity, List<String> permissions) {
        if (activity.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH);
        }
        if (activity.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
    }

    public static boolean hasBluetoothPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= API_31_S) { // Android 12+ (API 31+)
            try {
                return activity.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") == PackageManager.PERMISSION_GRANTED &&
                       activity.checkSelfPermission("android.permission.BLUETOOTH_SCAN") == PackageManager.PERMISSION_GRANTED;
            } catch (Exception e) {
                // 降级到传统权限检查
                return activity.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                       activity.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
            }
        } else {
            return activity.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                   activity.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    /**
     * 请求完整存储访问权限（Android 14+）
     */
    public static void requestManageExternalStorage(Activity activity) {
        if (Build.VERSION.SDK_INT >= 34) {
            try {
                if (!Environment.isExternalStorageManager()) {
                    MyLog.d("AppPermission", "Requesting MANAGE_EXTERNAL_STORAGE permission");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivity(intent);
                }
            } catch (Exception e) {
                MyLog.e("AppPermission", "Failed to request MANAGE_EXTERNAL_STORAGE: " + e.getMessage());
                // 降级到通用设置页面
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    activity.startActivity(intent);
                } catch (Exception ex) {
                    MyLog.e("AppPermission", "Failed to open storage settings: " + ex.getMessage());
                }
            }
        }
    }
    
    /**
     * 检查是否有完整的存储访问权限
     */
    public static boolean hasFullStorageAccess() {
        if (Build.VERSION.SDK_INT >= 34) {
            try {
                return Environment.isExternalStorageManager();
            } catch (Exception e) {
                MyLog.e("AppPermission", "Failed to check MANAGE_EXTERNAL_STORAGE: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}