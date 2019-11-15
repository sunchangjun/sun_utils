package com.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {

    /**
     * 获取几天前/后的当前日期
     */
    public static Date getDayCurrTime(Date date, int days) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + days);
        return now.getTime();
    }

    /**
     * 根据当前时间，获取前/后几天的时间
     *
     * @param date
     * @return
     */
    public static Date getDate(Date date, int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return calendar.getTime();
    }
    /**
     * 获取几天前/后的凌晨时间
     */
    public static long getDateSmallHours(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime().getTime();
    }

    public static  List<String> getStringBetweenTime(String begintime, String endtime) {
        LinkedList<String> list = new LinkedList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
            Date beginDate = sdf.parse(begintime);
            while (beginDate.getTime() <= sdf.parse(endtime).getTime()) {
                list.add(sdf.format(beginDate));
                beginDate = DateUtils.getDate(beginDate, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public void getCurr() {
        //获取当前星期几
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar);
        System.out.println("当前月份：" + Calendar.DAY_OF_MONTH);
        String week;
        week = calendar.get(Calendar.DAY_OF_WEEK) - 1 + "";
        if ("0".equals(week)) {
            week = "7";
        }
        System.out.println("当前星期：" + week);
    }


    public static String getDayOfWeekByDate(String date) {
        String dayOfweek = "-1";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E");
            String str = formatter.format(myDate);
            dayOfweek = str;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dayOfweek;
    }

	public static String getYmd(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	public static String getYmd(long ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(ts));
	}
    
    /**
     * 日期转字符串
     *
     * @param date      日期
     * @param formatStr 字符串格式
     * @return
     */
    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        return simpleDateFormat.format(date);
    }

	public static Date getDate(String yymmdd) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(yymmdd);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static java.sql.Date getSqlDate(String yymmdd) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(yymmdd);
		return new java.sql.Date(date.getTime());
	}
	
	public static List<Date> validateDayRange(String from, String to) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date fdate = null;
		Date tdate = null;
		try{
			fdate = sdf.parse(from);
			tdate = sdf.parse(to);
		}catch(Exception e){
			throw new Exception("无效日期格式:"+from+",举例日期格式'2018-04-06'");
		}
		if(fdate.getTime()>tdate.getTime()){
			throw new Exception("起点时间应该小于终止时间");
		}
		List<Date> ds = new ArrayList<Date>();
		ds.add(fdate);
		ds.add(tdate);
		return ds;
	}

    /**
     * 根据日期获取年份
     *
     * @param time
     * @return java.lang.String
     **/
    public static String getYearFromTimeStr(String time){
        String year = "";

        if(StringUtils.isNotBlank(time)){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = simpleDateFormat.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                year = String.valueOf(calendar.get(Calendar.YEAR));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return year;
    }

	/**
     * 秒转HH:MM:SS格式
     * @param seconds
     * @return
     */
    public static String secondsToTimeStr(int seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            return "00:00";
        }
        else {
            minute = seconds / 60;
            if (minute < 60) {
                second = seconds % 60;
                timeStr = "00"+unitFormat(minute) + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = seconds - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + unitFormat(minute) + unitFormat(second);
            }
        }
        return timeStr;
    }

	private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static void main(String[] args)  {
        System.out.println(getDayOfWeekByDate("2019-06-01"));
    }


}
