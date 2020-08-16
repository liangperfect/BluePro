package com.vitalong.bluetest2.Utils;

import java.math.BigInteger;

public class cmdAnalyze {
    /**
     * 获取sn地址
     *
     * @param data
     * @return
     */
    public static String getSn(byte[] data) {
        if (data.length > 4) {
            String sn = getStringData(data);
            return sn;
        }
        return "";
    }

    /**
     * 角度范围
     *
     * @param data
     * @return
     */
    public static String getAngleFW(byte[] data) {
        if (data.length > 4) {
            String Angle = getIntData(data).toString();
            return Angle;
        }
        return "";
    }

    public static String getSensorType(byte[] data) {
        if (data.length > 4) {
            String Angle = getIntData(data).toString();
            return Angle;
        }
        return "";
    }

    static String getStringData(byte[] data) {
        if (data.length > 4) {
            int length = data[2];
            byte[] dt = new byte[length];
            System.arraycopy(data, 3, dt, 0, length);
            return Integer.toString(Integer.parseInt(ByteTransformUtil.bytesToHex(dt), 16));
        }
        return "";
    }

    static Integer getIntData(byte[] data) {
        if (data.length > 4) {
            int length = data[2];
            byte[] dt = new byte[length];
            System.arraycopy(data, 3, dt, 0, length);
            return bytesToInt(dt);
        }
        return -1;
    }

    public static int bytesToInt(byte[] bytes) {
        byte[] bytes1 = new byte[bytes.length];
        for (int i = 0; i < bytes1.length; i++) {
            bytes1[i] = bytes[i];

        }
        return new BigInteger(bytes1).intValue();
    }

    /**
     * 获取角度值
     *
     * @param data
     * @return
     */
    public static Double getAngle1(byte[] data, double A, double B, double C, double D) {
        if (data.length == 4) {
            try {
                byte[] vale = new byte[]{data[2], data[3], data[0], data[1]};
                Double Angle = Double.valueOf(ByteTransformUtil.getFloat(vale, 0));
                Angle = Angle * 7.2;
                //Angle = 0 * Angle * Angle * Angle + 0 * Angle * Angle + 1 * Angle + 0;
                Angle = A * Angle * Angle * Angle + B * Angle * Angle + C * Angle + D;
                //Angle=1.09314E-03*Angle*Angle*Angle+-8.91574E-11*Angle*Angle+8.19623E-14*Angle+2.41167E-04;
                return Angle;
            } catch (Exception err) {

            }
            /*
            }*/
        }
        return 100000.0;
    }

    public static Double getAngle2(byte[] data, double A, double B, double C, double D) {
        if (data.length == 4) {
            try {
                byte[] vale = new byte[]{data[2], data[3], data[0], data[1]};
                Double Angle = Double.valueOf(ByteTransformUtil.getFloat(vale, 0));
                Angle = Angle * 7.2;
                //Angle = 0 * Angle * Angle * Angle + 0 * Angle * Angle + 1 * Angle + 0;
                Angle = A * Angle * Angle * Angle + B * Angle * Angle + C * Angle + D;
                //Angle=1.09314E-03*Angle*Angle*Angle+-8.91574E-11*Angle*Angle+8.19623E-14*Angle+2.41167E-04;
                return Angle;
            } catch (Exception err) {

            }
        }
        return 100000.0;
    }

    /**
     * 获取弧度值
     *
     * @param deg
     * @return
     */
    public static double getRaw(double deg) {
        try {
            double raw = Math.sin(deg * Math.PI / 180) * 25000;
            return raw;
        } catch (Exception err) {
        }
        return 100000.0;
    }

    /**
     * 获取倾斜值
     *
     * @param raw1
     * @param raw2
     * @return
     */
    public static double getIncline1(double raw1, double raw2, double ftRaw1, double ftRaw2) {
        try {
            double raw = Math.asin((((raw1 - raw2) + (ftRaw1 - ftRaw2)) / 50000.0)) * 180 / Math.PI * 3600;//Math.asin((raw1 -raw2)/25000) * 180/Math.PI;
            return raw;
        } catch (Exception err) {
        }
        return 100000.0;
    }

    public static double getIncline2(double raw1, double raw2, double ftRaw1, double ftRaw2) {
        try {
            double raw = Math.asin((((raw1 - raw2) - (ftRaw1 - ftRaw2)) / 50000.0)) * 180 / Math.PI * 3600;
            return raw;
        } catch (Exception err) {
        }
        return 100000.0;
    }

    /**
     * 参数转换成命令
     *
     * @param param
     * @return
     */
    public static String getParamToCmd(String param) {
        try {
            param = param.toUpperCase();
            if (param != null && !param.equals("")) {
                String value = "00";
                String bit = param.substring(0, 1);
                if (bit.equals("-")) value = "01";
                if (bit.equals("-") || bit.equals("+")) param = param.substring(1);
                int index = param.indexOf(".");//查找小数点位置
                if (index > -1) {
                    bit = param.substring(0, index);//去整数位
                    if (bit.length() < 2) value += "0" + bit;//判断整数长度
                    else value += bit;
                    param = param.substring(index + 1);//排除小数点及签名的数
                    index = param.indexOf("E");
                    if (index > -1) {
                        bit = param.substring(0, index);
                        value += bit;
                        if (bit.length() < 6) {
                            String fill = "000000";
                            fill = fill.substring(bit.length());
                            value += fill;
                        }
                        param = param.substring(index);//E后面的字符
                        if (param.indexOf("E-") > -1) value += "01";
                        else value += "00";
                        param = param.replace("E", "");
                        param = param.replace("+", "");
                        param = param.replace("-", "");
                        if (param.length() < 2) value += "0" + param;
                        else value += param;
                    } else {
                        if (param.length() > 5) value += param.substring(0, 6);
                        else value += param;
                    }
                } else {
                    if (param.length() < 2)
                        value += "0" + param;
                    else value += param;
                }
                if (value.length() < 14) {
                    String fill = "00000000000000";
                    fill = fill.substring(value.length());
                    value += fill;
                }
                return value;
            }
        } catch (Exception er) {

        }
        return "";
    }

    /**
     * 参数转换
     *
     * @param Angle
     * @return
     */
    public static String getParam(String Angle) {
        try {
            if (Angle.length() == 14) {
                String bit = Angle.substring(0, 2);//1
                String value = "";
                if (bit.equals("01")) value = "-";
                bit = Angle.substring(2, 4);//2
                value += Integer.parseInt(bit) + ".";
                bit = Angle.substring(4, 10);//3
                value += bit;
                /*bit = Angle.substring(6, 8);//4
                if (!bit.equals("00"))
                    value += bit;
                bit = Angle.substring(8, 10);//5
                if (!bit.equals("00"))
                    value += bit;*/
                value = Double.valueOf(value).toString();
                bit = Angle.substring(10, 12);
                if (bit.equals("00")) value += "E";
                else value += "E-";
                value += Integer.parseInt(Angle.substring(12));
                return value;
            }
        } catch (Exception er) {

        }
        return "0";
    }
}
