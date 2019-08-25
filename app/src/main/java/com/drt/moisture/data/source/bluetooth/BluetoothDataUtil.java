package com.drt.moisture.data.source.bluetooth;

import com.drt.moisture.util.Crc16;
import com.rokyinfo.convert.RkFieldConverter;
import com.rokyinfo.convert.exception.FieldConvertException;
import com.rokyinfo.convert.exception.RkFieldException;
import com.rokyinfo.convert.util.ByteConvert;

public class BluetoothDataUtil {

    /**
     * 编码
     *
     * @return
     * @throws RkFieldException
     * @throws FieldConvertException
     */
    public static <T> byte[] encode(T data) throws IllegalAccessException, RkFieldException, FieldConvertException {

        // 数据域
        byte[] paramsContent = RkFieldConverter.entity2bytes(data);
        // 帧头
        byte[] bleParams = new byte[paramsContent.length + 20];
        for (int i = 0; i < 6; i++) {
            bleParams[i] = (byte) 0xA5;
        }
        // ---------------包头数据-----------------
        // 目的地址
        byte[] address = ByteConvert.shortToBytes((short) 0x0001);
        bleParams[6] = address[0];
        bleParams[7] = address[1];
        // 源地址
        byte[] cource = ByteConvert.shortToBytes((short) 0x8000);
        bleParams[8] = cource[0];
        bleParams[9] = cource[1];
        // 保留
        for (int i = 0; i < 3; i++) {
            bleParams[10 + i] = (byte) 0x00;
        }

        // 选项
        bleParams[13] = (byte) 0x00;
        // 协议版本
        bleParams[14] = (byte) 0x01;
        // 数据与长度
        byte[] dataLength = ByteConvert.shortToBytes((short) paramsContent.length);
        bleParams[15] = dataLength[0];
        bleParams[16] = dataLength[1];
        // ---------------包头数据-----------------

        // 数据域
        for (int i = 0; i < paramsContent.length; i++) {
            bleParams[17 + i] = paramsContent[i];
        }

        // CRC校验
        byte[] crcData = new byte[paramsContent.length + 11];
        System.arraycopy(bleParams, 6, crcData, 0, crcData.length);
        byte[] crcValue = ByteConvert.intToBytes(Crc16.crc16CcittCalc(crcData, crcData.length));
        bleParams[17 + paramsContent.length] = crcValue[0];
        bleParams[17 + paramsContent.length + 1] = crcValue[1];
        // 包尾
        bleParams[17 + paramsContent.length + 2] = 0x5A;

        return bleParams;
    }

    public static <T> T decode(Class<T> tClass, byte[] dataValue) throws InstantiationException, IllegalAccessException, FieldConvertException, RkFieldException {

        byte[] data = new byte[dataValue.length - 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = dataValue[i];
        }

        if (Crc16.crc16CcittCalc(data, data.length) == ByteConvert.bytesToInt(new byte[]{dataValue[dataValue.length - 2], dataValue[dataValue.length - 1], 0, 0})) {
            // 不包含包头和包尾巴
            byte[] objValue = new byte[dataValue.length - 2 - 11];
            for (int i = 0; i < objValue.length; i++) {
                objValue[i] = dataValue[11 + i];
            }

            return RkFieldConverter.bytes2entity(tClass.newInstance(), objValue);
        } else {
            throw new FieldConvertException("CRC 校验失败");
        }
    }

}
