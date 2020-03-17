package com.lihang.mycamera.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leo on 2017/9/19.
 */

public class TimeUtils {
    /**
     * 时间戳转为时间(年月日，时分秒)
     */

    public static String getDateToString(String milSecond) {
        long lcc_time = Long.valueOf(milSecond);
        Date date = new Date(lcc_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getDateToStringReoprt(String milSecond) {
        long lcc_time = Long.valueOf(milSecond);
        Date date = new Date(lcc_time);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
        return format.format(date);
    }


    public static String getDateToStringLeo(String milSecond) {
        long lcc_time = Long.valueOf(milSecond);
        Date date = new Date(lcc_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }


    public static String getDateToGreen(String milSecond) {
        long lcc_time = Long.valueOf(milSecond);
        Date date = new Date(lcc_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd  HH:mm");
        return format.format(date);
    }


    /**
     * 时间转换为时间戳
     *
     * @param timeStr 时间 例如: 2016-03-09
     * @param format  时间对应格式  例如: yyyy-MM-dd
     * @return
     */
    public static long getTimeStamp(String timeStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 将时间戳转换为 天时分
     */
    public static String getTimeStr(long time) {
        int day = (int) (time / 1000 / 60 / 60 / 24);
        int hours = (int) (time / 1000 / 60 / 60 % 24);
        int minute = (int) (time / 1000 / 60 % 60);
        int s = (int) (time / 1000 % 60);

        if (day == 0) {
            if (hours == 0) {
                if (minute == 0) {
                    return s + " 秒";
                } else {
                    return minute + " 分 " + s + " 秒";
                }
            } else {
                return hours + " 时 " + minute + " 分 " + s + " 秒";
            }
        } else {
            return day + " 天 " + hours + " 时 " + minute + " 分";
        }
    }


    /**
     * 将时间戳转换为 天时分
     */
    public static String getScaleTime(long time) {
        int minute = (int) (time / 60);
        int s = (int) (time % 60);
        String mm = "";
        if (s <= 9) {
            mm = "0" + s;
        } else {
            mm = "" + s;
        }

        String min = "";
        if (minute <= 9) {
            min = "0" + minute;
        } else {
            min = "" + minute;
        }
        return min + " : " + mm;
    }


    /*
    获取时间差
     */
    public static String getTimesToNow(String milSecond) {
        if (TextUtils.isEmpty(milSecond)) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = format.format(new Date());
        String returnText = null;
        long from = Long.parseLong(milSecond);
        try {
            long to = format.parse(now).getTime();
            int days = (int) ((to - from) / (1000 * 60 * 60 * 24));
            if (days == 0) {//一天以内，以分钟或者小时显示
                int hours = (int) ((to - from) / (1000 * 60 * 60));
                if (hours == 0) {
                    int minutes = (int) ((to - from) / (1000 * 60));
                    if (minutes == 0) {
                        returnText = "刚刚";
                    } else {
                        //解决Bug - 1分钟
                        if (minutes < 0) {
                            returnText = "刚刚";
                        } else {
                            returnText = minutes + "分钟前";
                        }
                    }
                } else if (hours < 0) {
                    returnText = "刚刚";
                } else {
                    returnText = hours + "小时前";
                }
            } else if (days == 1) {
                Date date = new Date(from);
                SimpleDateFormat format_zuo = new SimpleDateFormat("HH:mm");
                returnText = "昨天" + format_zuo.format(date);
            } else if (days >= 365) {
                Date date = new Date(from);
                SimpleDateFormat format_zuo = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                returnText = format_zuo.format(date);
            } else {
                Date date = new Date(from);
                SimpleDateFormat format_zuo = new SimpleDateFormat("MM-dd HH:mm");
                returnText = format_zuo.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnText;
    }

    /*
    获取时间差
     */
    public static String getTimesToChat(String milSecond) {
        if (TextUtils.isEmpty(milSecond)) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = format.format(new Date());
        String returnText = null;
        long from = Long.parseLong(milSecond);
        try {
            long to = format.parse(now).getTime();
            int days = (int) ((to - from) / (1000 * 60 * 60 * 24));
            if (days == 0) {//一天以内，以分钟或者小时显示
                Date date = new Date(from);
                SimpleDateFormat format_zuo = new SimpleDateFormat("HH:mm");
                returnText = format_zuo.format(date);
            } else if (days == 1) {
                returnText = "昨天";
            } else if (days >= 365) {
                Date date = new Date(from);
                SimpleDateFormat format_zuo = new SimpleDateFormat("yyyy-MM-dd");
                returnText = format_zuo.format(date);
            } else {
                Date date = new Date(from);
                SimpleDateFormat format_zuo = new SimpleDateFormat("MM-dd");
                returnText = format_zuo.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnText;
    }


    /*
        获取月 时 分
     */
    public static String getTimesToNowMouth(String milSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = format.format(new Date());
        String returnText = null;
        long from = Long.parseLong(milSecond);
        Date date = new Date(from);
        SimpleDateFormat format_zuo = new SimpleDateFormat("MM-dd HH:mm");
        returnText = format_zuo.format(date);
        return returnText;
    }


}
