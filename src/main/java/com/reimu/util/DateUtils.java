package com.reimu.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }


    /**
     * 得到当前日期
     */
    public static Date getNow() {
        return new Date();
    }


    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static Date getDateAfterAdd(int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, days);
        return c.getTime();
    }


    /**
     * data 转化为Timestamp
     *
     * @param date
     * @return
     */
    public static Timestamp parseDate2Timestamp(Date date) {
        return new Timestamp(toCalendar(date).getTimeInMillis());
    }


    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    ;


    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }


    /**
     *
     * @param mss 要转换的毫秒数
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + "  天 " + hours + " 小时 " + minutes + " 分钟 "
                + seconds + " 秒 ";
    }
    /**
     *
     * @param begin 时间段的开始
     * @param end	时间段的结束
     * @return	输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     * @author fy.zhang
     */
    public static String formatDuring(Date begin, Date end) {
        return formatDuring(end.getTime() - begin.getTime());
    }


    /**
     * 以字符的形式获取当前的时间
     *
     * @return 时间字符串
     */
    public static String GetCurrentTime() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        return df.format(now);
    }

    public static Date stringToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        if (date != null && !"".equals(date)) {
            try {
                d = sdf.parse(date);
            } catch (ParseException e) {

            }

            return d;

        }
        return null;
    }

    /**
     * 以日期格式形式获取当前的时间
     *
     * @return
     */
    public static String currentTimeToDateFormat(String dateFormatStr) {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat(dateFormatStr);// 设置日期格式
        return df.format(now);
    }

    /**
     * date按时间格式转时间字符串
     *
     * @return
     */
    public static String dateToDateStringFormat(Date date, String dateFormatStr) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormatStr);// 设置日期格式
        return df.format(date);
    }


    public static Timestamp getCurrentTimestamp() {
        return  new Timestamp(System.currentTimeMillis());
    }


    /**
     * 以Date类型获取当前时间
     *
     * @return
     */
    public static Date currentTimeToDate() {
        Date currentTime;
        String nowTime;
        // 获取当前时间
        nowTime = DateUtils.currentTimeToDateFormat(GetCurrentTime());
        currentTime = Timestamp.valueOf(nowTime);
        return currentTime;
    }

    /**
     * 获取当天的开始时间
     *
     * @return
     */
    public static Date getTodayStartTime() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String startTime = df.format(now) + " 00:00:00";
        return stringToDate(startTime);
    }

    /**
     * 获取当天的结束时间
     *
     * @return
     */
    public static Date getTodayEndTime() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String endTime = df.format(now) + " 23:59:99";
        return stringToDate(endTime);
    }


    public static String getTodayEndTimeString() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String endTime = df.format(now) + " 23:59:59";
        return endTime;
    }
    /**
     * 获取当天某个小时整点的时间(01或23)
     *
     * @return
     */
    public static Date getTodayOneHourTime(int hour) {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String endTime = df.format(now) + " " + integerToTime(hour) + ":00:00";
        return stringToDate(endTime);
    }

    /**
     * 改变当前时间当中的小时时间
     *
     * @param hour
     * @return
     */
    public static Date changCurrentTimeForHour(int hour) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String time = year + "-" + integerToTime(month) + "-"
                + integerToTime(date) + " " + integerToTime(hour) + ":"
                + integerToTime(minute) + ":" + integerToTime(second);

        return Timestamp.valueOf(time);
    }

    /**
     * 整数转成时间格式中的某个数值
     *
     * @param n
     * @return
     */
    private static String integerToTime(int n) {
        if (n < 10) {
            return "0" + n;
        } else {
            return "" + n;
        }
    }

    /**
     * 获取某天的开始时间
     *
     * @return
     */
    public static Date getOneodayStartTime(String time) {
        Date date = stringToDate(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String startTime = df.format(date) + " 00:00:00";
        return stringToDate(startTime);
    }

    /**
     * 获取某天的结束时间
     *
     * @return
     */
    public static Date getOneodayEndTime(String time) {
        Date date = stringToDate(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String endTime = df.format(date) + " 23:59:99";
        return stringToDate(endTime);
    }

    /**
     * 毫秒值转Date
     *
     * @param millis
     * @return
     */
    public static Date longToDate(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return (Timestamp.valueOf(formatter.format(calendar.getTime())));
    }

    /**
     * Date转毫秒值
     *
     * @param time
     * @return
     */
    public static long dateToLong(Date time) {
        String ss = "" + time.getTime();
        return Long.parseLong(ss);
    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1
     *            date1
     * @param date2
     *            date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    public static Long dateDiff(String startTime, String endTime, String what) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // 获得两个时间的毫秒时间差异
            return dateDiff(sd.parse(endTime), sd.parse(startTime),what);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Long dateDiff(Date startTime, Date endTime, String what) {
             // 获得两个时间的毫秒时间差异
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
            long  diff = endTime.getTime() - startTime.getTime();
            if("day".equals(what)){
                long day = diff / nd;// 计算差多少天
                return day;
            }else if("hour".equals(what)){
                long hour = diff % nd / nh;// 计算差多少小时
                return hour;
            }else if("min".equals(what)){
                long min = diff % nd % nh / nm;// 计算差多少分钟
                return min;
            }else if("sec".equals(what)){
                long sec = diff % nd % nh % nm / ns;// 计算差多少秒
                return sec;
            }else {
                return null;
            }
    }


    /**
     * @author LuoB.
     * @param oldTime 较小的时间
     * @param newTime 较大的时间 (如果为空   默认当前时间 ,表示和当前时间相比)
     * @return -1 ：同一天.    0：昨天 .   1 ：至少是前天.
     * @throws ParseException 转换异常
     */
    public static int isYeaterday(Date oldTime, Date newTime) throws ParseException {
        if(newTime==null){
            newTime=new Date();
        }
        //将下面的 理解成  yyyy-MM-dd 00：00：00 更好理解点
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(newTime);
        Date today = format.parse(todayStr);
        //昨天 86400000=24*60*60*1000 一天
        if((today.getTime()-oldTime.getTime())>0 && (today.getTime()-oldTime.getTime())<=86400000) {
            return 0;
        }
        else if((today.getTime()-oldTime.getTime())<=0){ //至少是今天
            return -1;
        }
        else{ //至少是前天
            return 1;
        }

    }

}
