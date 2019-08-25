package com.drt.moisture.util;

public class Crc16 {

    private static final int CRC_16_POLYNOMIAL = 0x1021;
    private static final int CRC_16_L_POLYNOMIAL = 0x8000;
    private static final int CRC_16_L_SEED = 0x80;
    private static final int CRC_16_L_OK = 0x00;

    public static int crc16CcittCalc(byte[] buf_ptr, int len) {
        int i;
        int crc = 0;

        int index = 0;
        while (len-- != 0) {
            for (i = CRC_16_L_SEED; i != 0; i = i >> 1) {
                if ((crc & CRC_16_L_POLYNOMIAL) != 0) {
                    crc = crc << 1;
                    crc = crc ^ CRC_16_POLYNOMIAL;
                } else {
                    crc = crc << 1;
                }

                if ((buf_ptr[index] & i) != 0) {
                    crc = crc ^ CRC_16_POLYNOMIAL;
                }
            }
            index++;
        }

        return (crc);
    }

}
