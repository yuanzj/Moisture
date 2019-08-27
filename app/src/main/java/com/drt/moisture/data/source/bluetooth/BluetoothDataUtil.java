package com.drt.moisture.data.source.bluetooth;

import com.drt.moisture.data.source.bluetooth.resquest.RecordDataRequest;
import com.rokyinfo.convert.RkFieldConverter;
import com.rokyinfo.convert.exception.FieldConvertException;
import com.rokyinfo.convert.exception.RkFieldException;
import com.rokyinfo.convert.util.ByteConvert;
import com.zhjian.bluetooth.spp.CRC16;
import com.zhjian.bluetooth.spp.HexString;

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
        byte[] crcValue = ByteConvert.ushortToBytes((int) CRC16.caluCRC(crcData));
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

        if ((int) CRC16.caluCRC(data) == ByteConvert.bytesToUshort(new byte[]{dataValue[dataValue.length - 2], dataValue[dataValue.length - 1]})) {
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

    public static void main(String... args) {
        RecordDataRequest recordDataRequest = new RecordDataRequest();
        recordDataRequest.setCmdGroup((byte) 0xA3);
        recordDataRequest.setCmd((byte) 0x05);
        recordDataRequest.setResponse((byte) 0x01);
        recordDataRequest.setReserved(0);
        System.out.println(String.valueOf(System.currentTimeMillis() / 1000));

        recordDataRequest.setTime(ByteConvert.uintToBytes(System.currentTimeMillis() / 1000));
        try {
            System.out.println(HexString.bytesToHex(BluetoothDataUtil.encode(recordDataRequest)));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RkFieldException e) {
            e.printStackTrace();
        } catch (FieldConvertException e) {
            e.printStackTrace();
        }
        System.out.println(ByteConvert.bytesToUint(HexString.hexToBytes("5D64A9E5")));

    }

}
