package com.util;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RESTUtil {

   public static Gson gson = new Gson();

   public static String parJson(Object o){
       return gson.toJson(o);
   }

    public static String timeEvent(long start, long time) {
        SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
        SimpleDateFormat fmFull = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(start);
        String text = fm.format(date) + " - ";
        date.setTime(start + time);
        text += fm.format(date) + "  " + fmFull.format(date);
        return text;
    }

    public static long parLongTime(String content) {

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            Date date = fm.parse(content);
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }



    }

    public static boolean checkText(String text, String content) {
        return (content.split(text).length > 1);
    }

    public static boolean startsWith(String text, String eve_name) {
        return (text.toLowerCase().startsWith(eve_name.toLowerCase()) || eve_name.toLowerCase().startsWith(text.toLowerCase()) || text.toLowerCase().endsWith(eve_name.toLowerCase()) || eve_name.toLowerCase().endsWith(text.toLowerCase()));
    }

    public static boolean checkTime(long time, String strCase) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fm2 = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String start = "", end = "";
        long startTime = 0, endTime = 0;
        if (strCase.equals("today")) {
            String strDate = fm.format(date);
            start = "00:00 " + strDate;
            end = "23:59 " + strDate;
            try {
                startTime = fm2.parse(start).getTime();
                endTime = fm2.parse(end).getTime();
            } catch (ParseException e) {
                return false;
            }
        }
        if (strCase.equals("tomorrow")) {
            date.setTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
            String strDate = fm.format(date);
            try {
                startTime = fm2.parse(start).getTime();
                endTime = fm2.parse(end).getTime();
            } catch (ParseException e) {
                return false;
            }
        }
        if (strCase.equals("this_week")) {
            Date dateStart = new Date(date.getTime() - ((date.getDay() - 1) * 24 * 60 * 60 * 1000));
            Date dateEnd = new Date(date.getTime() + ((7 - date.getDay()) * 24 * 60 * 60 * 1000));

            startTime = dateStart.getTime();
            endTime = dateEnd.getTime();
        }
        if (strCase.equals("this_month")) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat fmMonth = new SimpleDateFormat("/MM/yyyy");
            String strDate = fmMonth.format(date);
            start = "00:00 " + cal.getActualMinimum(Calendar.DATE) + strDate;
            end = "23:59 " + cal.getActualMaximum(Calendar.DATE) + strDate;

            System.out.println(start);
            System.out.println(end);
            try {
                startTime = fm2.parse(start).getTime();
                endTime = fm2.parse(end).getTime();
            } catch (ParseException e) {
                return false;
            }
        }
        return (time > startTime && time < endTime);
    }


}
