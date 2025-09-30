package com.iwip.common.utils;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 时间工具类
 *
 * @author Fei
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算相差天数
     */
    public static int differentDaysByMillisecond(Date date1, Date date2)
    {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算时间差
     *
     * @param endDate 最后时间
     * @param startTime 开始时间
     * @return 时间差（天/小时/分钟）
     */
    public static String timeDistance(Date endDate, Date startTime)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor)
    {
        if (temporalAccessor == null) {
            return null;
        }
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor)
    {
        if (temporalAccessor == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 Date ==> LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date)
    {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        System.out.println("Date = " + date);
        System.out.println("LocalDateTime = " + localDateTime);
        return localDateTime;
    }


    /**
     * 增加 Date ==> LocalDate
     */
    public static LocalDate toLocalDate(Date date)
    {

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = date.toInstant().atZone(zoneId).toLocalDate();
        return localDate;
    }

    public static String timeDiff(LocalDateTime first, LocalDateTime second)
    {

        // 计算时间差（小时）
        Duration duration  = Duration.between(first, second);

        long hours = duration.toHours();          // 总小时数
        long minutes = duration.toMinutes() % 60; // 剩余分钟数
        long seconds = duration.getSeconds() % 60; // 剩余秒数


        String format = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        System.out.println(format);

        // 格式化为 HH:mm:ss 形式
        return format;
    }


    public static String timeDiff( LocalDateTime time)
    {


        // 当前时间
        LocalDateTime now = LocalDateTime.now();

        // 计算时间差
        Duration duration = Duration.between(now, time);

        // 获取时分秒
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        // 计算结果
        return String.format("%s%02d:%02d:%02d", duration.isNegative() ? "" : "-", Math.abs(hours), Math.abs(minutes), Math.abs(seconds));
    }

    /**
     * 校验 startTime2 不能大于startTime1。
     * 返回 true 代表 startTime2 早于 startTime1，返回 true
     * 否则，返回 false
     * @param startTime1
     * @param startTime2
     * @return
     */
    public static boolean validateTime(LocalDateTime startTime1, LocalDateTime startTime2) {
         return startTime1.isAfter(startTime2);
    }

    /**
     * aTime 大于 bTime
     * aTime 小于 cTime
     * 返回 true 代表 aTime 落在 这个时间范围内。
     * 返回 false 代表 aTime 不在 这个时间范围内。
     * @param aTime 需要校验的时间
     * @param bTime 开始时间
     * @param cTime 结束时间
     */
    public static boolean isWithinRange(LocalDateTime aTime, LocalDateTime bTime, LocalDateTime cTime) {
        return aTime.isAfter(bTime) && aTime.isBefore(cTime);
    }



//    public static void main(String[] args) {
//        LocalDateTime aTime = LocalDateTime.of(2025, 2, 15, 11, 30, 0);
//        LocalDateTime bTime = LocalDateTime.of(2025, 2, 15, 10, 0, 0);
//        LocalDateTime cTime = LocalDateTime.of(2025, 2, 15, 11, 0, 0);
//
//        boolean aTimeOutsideBC = isWithinRange(aTime, bTime, cTime);
//        if (aTimeOutsideBC){
//            System.out.println(aTimeOutsideBC+"：在范围里面");
//        }else {
//            System.out.println(aTimeOutsideBC+"：不在范围里面");
//
//        }
//    }

    /**
     * 计算两个时间点之间的小时数
     */
    public static BigDecimal durationToHours(LocalDateTime start, LocalDateTime end) {
        return BigDecimal.valueOf(Duration.between(start, end).toMinutes())
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }



    /**
     * 计算Duration对象的小时数
     */
    public static BigDecimal durationToHours(Duration duration) {
        return BigDecimal.valueOf(duration.toMinutes())
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    /**
     * 判断一个时间是否为整点
     * @param localDateTime
     * @return true 是 false 否
     */
    public static boolean isWholeHour (LocalDateTime localDateTime) {
        return localDateTime.getMinute() == 0 && localDateTime.getSecond() == 0;
    }


}
