package com.hiwoo.util;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;


public class CommonUtil {

    public static String convertToCronExpression(String dateTimeString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateTime = dateFormat.parse(dateTimeString);

            // 分解日期和时间
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
            SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
            SimpleDateFormat secondFormat = new SimpleDateFormat("ss");

            String year = yearFormat.format(dateTime);
            String month = monthFormat.format(dateTime);
            String day = dayFormat.format(dateTime);
            String hour = hourFormat.format(dateTime);
            String minute = minuteFormat.format(dateTime);
            String second = secondFormat.format(dateTime);

            // 构建cron表达式
            String cronExpression = second + " " + minute + " " + hour + " " + day + " " + month + " ? " + year;

            return cronExpression;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 去除list重复数据
     *
     * @param list
     * @return
     */
    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }


    public static double getdecimalDigit(int num) {
        double resNum = 0;
        switch (num) {
            case 0:
                resNum = 1;
                break;
            case 1:
                resNum = 0.1;
                break;
            case 2:
                resNum = 0.01;
                break;
            case 3:
                resNum = 0.001;
                break;
            case 4:
                resNum = 0.0001;
                break;
        }
        return resNum;
    }

    public static String formatDouble(Double d) {
        if (null == d) {
            d = 0d;
        }
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        double num = bg.doubleValue();
        if (Math.round(num) - num == 0) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }


    public static Integer transDataType(Integer dataType) {

        int res = dataType;
        if (4 == dataType) {
            res = 1;
        } else if (5 == dataType) {
            res = 3;
        }
        return res;
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }


    public static String getTenantIdByUUId() {
        int first = new Random(10).nextInt(8) + 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return first + String.format("%015d", hashCodeV);
    }

    public static String getGroupIdByUUId() {
        int first = new Random(8).nextInt(8) + 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return first + String.format("%010d", hashCodeV);
    }

    /**
     * 根据小数位数转换double类型数据
     *
     * @param d
     * @param decimalDigit
     * @return
     */
    public static Double formatDouble(Double d, Integer decimalDigit) {
        if (null == d) {
            d = 0d;
        }
        BigDecimal bg = new BigDecimal(d).setScale(decimalDigit, RoundingMode.HALF_UP);
        double num = bg.doubleValue();
        if (Math.round(num) - num == 0) {
            return Double.valueOf((long) num);
        }
        return Double.valueOf(num);
    }


}
