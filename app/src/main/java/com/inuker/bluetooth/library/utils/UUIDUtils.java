package com.inuker.bluetooth.library.utils;

import java.util.UUID;

/**
 * UUID工具类 - 空实现版本
 */
public class UUIDUtils {
    
    public static UUID makeUuid(String uuid) {
        if (uuid == null || uuid.length() == 0) {
            return null;
        }
        
        try {
            if (uuid.length() == 4) {
                // 16位UUID转换为128位UUID
                return UUID.fromString("0000" + uuid + "-0000-1000-8000-00805F9B34FB");
            } else if (uuid.length() == 8) {
                // 32位UUID转换为128位UUID
                return UUID.fromString(uuid + "-0000-1000-8000-00805F9B34FB");
            } else {
                // 已经是完整的UUID
                return UUID.fromString(uuid);
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String makeUuidString(String uuid) {
        UUID uuidObj = makeUuid(uuid);
        return uuidObj != null ? uuidObj.toString() : null;
    }
    
    public static String makeUUID(int shortUuid) {
        return makeUuidString(String.format("%04X", shortUuid));
    }
    
    public static UUID makeUuidFromInt(int shortUuid) {
        return makeUuid(String.format("%04X", shortUuid));
    }
}