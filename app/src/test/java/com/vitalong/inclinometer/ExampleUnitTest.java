package com.vitalong.inclinometer;

import org.junit.Test;

import com.vitalong.inclinometer.Utils.ByteTransformUtil;

import static org.junit.Assert.*;

import java.io.IOException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }

    @Test
    public void testFloat() throws IOException {

//        System.out.printf("123");

        float a = ByteTransformUtil.byte2float(hexStringToByteArray("41f03500"));
        System.out.println("a->" + a);

        float b = Integer.parseUnsignedInt("41f03500", 16);
        System.out.println("b->" + b);
    }


    public static byte[] hexStringToByteArray(String hexString) {
        // 去掉字符串中的空格
        hexString = hexString.replace(" ", "");

        // 字节数组的长度是字符串长度的一半
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];

        // 每两个字符转换为一个 byte
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return byteArray;
    }

}