package com.vitalong.bluetest2.Utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ByteTransformUtil {
    /**
     * 字节转十六进制
     *
     * @param b 需要进行转换的byte字节
     * @return 转换后的Hex字符串
     */
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static String ByteArraytoHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String bs = String.format("%02X ", b);
            sb.append(bs);
        }
        return sb.toString();
    }

    /**
     * 字节数组转float
     * 采用IEEE 754标准
     *
     * @param
     * @return
     */
    public static Float byte2float(byte[] b) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        float f = dis.readFloat();
        return f;
    }

    public static double byteToDouble(byte[] b) {
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            tmp.append(b[i]);
        }
        double to = Double.valueOf(tmp.toString());
        return to;
    }

    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    public static float getFloat(byte[] arr, int index) {
        return Float.intBitsToFloat(getInt(arr, index));
    }

    public static int getInt(byte[] arr, int index) {
        return (0xff000000 & (arr[index + 0] << 24)) |
                (0x00ff0000 & (arr[index + 1] << 16)) |
                (0x0000ff00 & (arr[index + 2] << 8)) |
                (0x000000ff & arr[index + 3]);
    }

    public static float bytes2Float(byte[] bytes) {
        //获取 字节数组转化成的2进制字符串
        String BinaryStr = bytes2BinaryStr(bytes);
        //符号位S
        Long s = Long.parseLong(BinaryStr.substring(0, 1));
        //指数位E
        Long e = Long.parseLong(BinaryStr.substring(1, 9), 2);
        //位数M
        String M = BinaryStr.substring(9);
        float m = 0, a, b;
        for (int i = 0; i < M.length(); i++) {
            a = Integer.valueOf(M.charAt(i) + "");
            b = (float) Math.pow(2, i + 1);
            m = m + (a / b);
        }
        Float f = (float) ((Math.pow(-1, s)) * (1 + m) * (Math.pow(2, (e - 127))));
        return f;
    }

    /**
     * 将字节数组转换成2进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytes2BinaryStr(byte[] bytes) {
        StringBuffer binaryStr = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String str = Integer.toBinaryString((bytes[i] & 0xFF) + 0x100).substring(1);
            binaryStr.append(str);
        }
        return binaryStr.toString();
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }
}
