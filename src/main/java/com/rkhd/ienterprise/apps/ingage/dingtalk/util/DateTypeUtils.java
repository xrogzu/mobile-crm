package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dell on 2016/1/12.
 */
public class DateTypeUtils {
    private  static Logger LOG = LoggerFactory.getLogger(DateTypeUtils.class);


    //    0:全部 ，1：本日，2：昨日，3：本周，4：本月，5：本季，6本年,7:
    public static long getCreateTime(int createTimeType) throws ParseException {
        if(createTimeType == 0){
            return 0L;
        }else {
            Calendar   cal   =   Calendar.getInstance();
            if(createTimeType == 1){

            }else if(createTimeType == 2){//2：昨日
                cal.add(Calendar.DATE,   -1);
            }else if(createTimeType == 3){

                cal.set(Calendar.DAY_OF_WEEK,1);//星期日为0，星期一为1

            }else if(createTimeType == 4){//本月
                cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天

            }else if(createTimeType == 5){//本季度
                int w = cal.get(Calendar.MONTH) ;//0-11
                if(w >= 0 && w <=2){
                    cal.set(Calendar.MONTH,0);

                }else if(w >= 3 && w <=5){
                    cal.set(Calendar.MONTH,3);
                }else if(w >= 6 && w <=8){
                    cal.set(Calendar.MONTH,6);
                }else if(w >= 9 && w <=11){
                    cal.set(Calendar.MONTH,9);
                }
                cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天

            }else if(createTimeType == 6){//本年
                cal.set(Calendar.MONTH,0);
                cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天

            }else if(createTimeType == 7){//下年
                cal.add(Calendar.YEAR,1);
                cal.set(Calendar.MONTH,0);
                cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
            }
            cal.set(Calendar.HOUR,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            LOG.info("starttime="+sdf.format(cal.getTime()));

            return cal.getTime().getTime();
        }
    }
    public static long getYestarDayEndTime()   {
        Calendar   cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   -1);
        cal.set(Calendar.HOUR,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        return cal.getTime().getTime();
    }
    public static void  main(String[] args) throws ParseException {



//        Long endtime  = getYestarDayEndTime();
        System.out.println("当天="+getCreateTime(1));//1465920000000
        System.out.println("昨日="+getCreateTime(2));//1465920000000
        System.out.println("本周="+getCreateTime(3));//1465920000000
        System.out.println("本月="+getCreateTime(4));//1465920000000
        System.out.println("本季="+getCreateTime(5));//1465920000000
        System.out.println("本年="+getCreateTime(6));//1465920000000

//        System.out.println("endtime="+endtime);
    }
}
