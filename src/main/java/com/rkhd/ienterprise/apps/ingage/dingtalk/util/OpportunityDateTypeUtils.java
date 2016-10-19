package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DateDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.OpportunityCloseDateTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 商机日期工具类
 */
public class OpportunityDateTypeUtils {
    public static final Logger LOG = LoggerFactory.getLogger(OpportunityDateTypeUtils.class);

    public static final String Y_M_D_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_BEGIN_FORMAT = "yyyy-MM-dd 00:00:00";
    public static final String DATE_END_FORMAT = "yyyy-MM-dd 23:59:59";

    public static final String MONTH_BEGIN_FORMAT = "yyyy-MM-01 00:00:00";
    public static final String MONTH_END_FORMAT = "yyyy-MM-dd 23:59:59";

    public static final String FIRST_JI_BEGIN_FORMAT = "yyyy-01-01 00:00:00";
    public static final String FIRST_JI_END_FORMAT = "yyyy-03-31 23:59:59";

    public static final String SECOND_JI_BEGIN_FORMAT = "yyyy-04-01 00:00:00";
    public static final String SECOND_JI_END_FORMAT = "yyyy-06-30 23:59:59";

    public static final String THIRD_JI_BEGIN_FORMAT = "yyyy-07-01 00:00:00";
    public static final String THIRD_JI_END_FORMAT = "yyyy-09-30 23:59:59";

    public static final String FOURTH_JI_BEGIN_FORMAT = "yyyy-10-01 00:00:00";
    public static final String FOURTH_JI_END_FORMAT = "yyyy-12-31 23:59:59";

    public static final String YEAR_BEGIN_FORMAT = "yyyy-01-01 00:00:00";
    public static final String YEAR_END_FORMAT = "yyyy-12-31 23:59:59";
    //
    public static DateDto getTimeInfo(OpportunityCloseDateTypes opportunityCloseDateType) throws ParseException {
        if(opportunityCloseDateType.equals(OpportunityCloseDateTypes.NONE)){
            return null;
        }else {
            SimpleDateFormat beginDateFormat = null;
            SimpleDateFormat endDateFormat = null;
            SimpleDateFormat timeForMAt = new SimpleDateFormat( Y_M_D_FORMAT);
            Calendar   cal   =   Calendar.getInstance();
            DateDto dto = new DateDto();

            if(OpportunityCloseDateTypes.THIS_WEEK.equals(opportunityCloseDateType)){
                /**
                 * 本周
                 */
                beginDateFormat =   new SimpleDateFormat( DATE_BEGIN_FORMAT);
                int cha_time = cal.get(Calendar.DAY_OF_WEEK) -1;//星期日为0，星期一为1
                if(cha_time > 0){
                    cal.add(Calendar.DATE,   cha_time*-1);
                }
                String  dayString = beginDateFormat.format(cal.getTime());
                long  down_day = timeForMAt.parse(dayString).getTime();
                LOG.info("down_day={}",dayString);
                endDateFormat = new SimpleDateFormat( DATE_END_FORMAT);
                Calendar   now_cal   =   Calendar.getInstance();
                cha_time = now_cal.get(Calendar.DAY_OF_WEEK) -1;//星期日为0，星期一为1
                now_cal.add(Calendar.DATE,   6-cha_time);

                dayString = endDateFormat.format(now_cal.getTime());
                LOG.info("up_day={}",dayString);
                long  up_day = timeForMAt.parse(dayString).getTime();

                dto.setDownDate(down_day);
                dto.setUpDate(up_day);

            }else if(OpportunityCloseDateTypes.NEXT_WEEK .equals(opportunityCloseDateType)){
                /**
                 * 下一周
                 */
                cal.add(Calendar.DATE,7);
                beginDateFormat =   new SimpleDateFormat( DATE_BEGIN_FORMAT);
                int cha_time = cal.get(Calendar.DAY_OF_WEEK) -1;//星期日为0，星期一为1
                if(cha_time > 0){
                    cal.add(Calendar.DATE,   cha_time*-1);
                }
                String  dayString = beginDateFormat.format(cal.getTime());
                long  down_day = timeForMAt.parse(dayString).getTime();

                endDateFormat = new SimpleDateFormat( DATE_END_FORMAT);
                Calendar   now_cal   =   Calendar.getInstance();
                now_cal.add(Calendar.DATE,7);
                cha_time = now_cal.get(Calendar.DAY_OF_WEEK) -1;//星期日为0，星期一为1
                now_cal.add(Calendar.DATE,   6-cha_time);

                dayString = endDateFormat.format(now_cal.getTime());
                long  up_day = timeForMAt.parse(dayString).getTime();

                dto.setDownDate(down_day);
                dto.setUpDate(up_day);

            }else if(OpportunityCloseDateTypes.THIS_MONTH .equals(opportunityCloseDateType)){

                /**
                 * 本月
                 * 本月第一天
                 */
                beginDateFormat = new SimpleDateFormat( MONTH_BEGIN_FORMAT);
                String  dayString = beginDateFormat.format(cal.getTime());
                long  down_day = timeForMAt.parse(dayString).getTime();


                /**
                 * 本月最后一天，先设置下一个月第一天 再减少一天
                 */
                cal.add(Calendar.MONTH, 1);    //加一个月
                cal.set(Calendar.DATE, 1);        //设置为该月第一天
                cal.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天

                endDateFormat = new SimpleDateFormat( MONTH_END_FORMAT);
                dayString = endDateFormat.format(cal.getTime());
                long  up_day = timeForMAt.parse(dayString).getTime();

                dto.setDownDate(down_day);
                dto.setUpDate(up_day);
            }else if(OpportunityCloseDateTypes.NEXT_MONTH .equals(opportunityCloseDateType)){
                /**
                 * 下月
                 * 下月第一天
                 */
                cal.add(Calendar.MONTH, 1);    //加一个月
                cal.set(Calendar.DATE, 1);        //设置为该月第一天
                beginDateFormat = new SimpleDateFormat( MONTH_BEGIN_FORMAT);
                String  dayString = beginDateFormat.format(cal.getTime());
                long  down_day = timeForMAt.parse(dayString).getTime();


                /**
                 * 本月最后一天，先设置下一个月第一天 再减少一天
                 */
                cal.add(Calendar.MONTH, 1);    //加一个月
                cal.set(Calendar.DATE, 1);        //设置为该月第一天
                cal.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天

                endDateFormat = new SimpleDateFormat( MONTH_END_FORMAT);
                dayString = endDateFormat.format(cal.getTime());
                long  up_day = timeForMAt.parse(dayString).getTime();

                dto.setDownDate(down_day);
                dto.setUpDate(up_day);
            }else if(OpportunityCloseDateTypes.THIS_QUARTER .equals(opportunityCloseDateType)){
                /**
                 * 本季度
                 */
                int w = cal.get(Calendar.MONTH) ;//0-11
                if(w >= 0 && w <=2){
                    beginDateFormat = new SimpleDateFormat( FIRST_JI_BEGIN_FORMAT);
                    endDateFormat = new SimpleDateFormat( FIRST_JI_END_FORMAT);
                }else if(w >= 3 && w <=5){
                    beginDateFormat = new SimpleDateFormat( SECOND_JI_BEGIN_FORMAT);
                    endDateFormat = new SimpleDateFormat( SECOND_JI_END_FORMAT);
                }else if(w >= 6 && w <=8){
                    beginDateFormat = new SimpleDateFormat( THIRD_JI_BEGIN_FORMAT);
                    endDateFormat = new SimpleDateFormat( THIRD_JI_END_FORMAT);
                }else if(w >= 9 && w <=11){
                    beginDateFormat = new SimpleDateFormat( FOURTH_JI_BEGIN_FORMAT);
                    endDateFormat = new SimpleDateFormat( FOURTH_JI_END_FORMAT);
                }
                String  dayString = beginDateFormat.format(cal.getTime());
                long  down_day = timeForMAt.parse(dayString).getTime();

                dayString = endDateFormat.format(cal.getTime());
                long  up_day = timeForMAt.parse(dayString).getTime();

                dto.setDownDate(down_day);
                dto.setUpDate(up_day);

            }else if(OpportunityCloseDateTypes.NEXT_QUARTER .equals(opportunityCloseDateType)){
                /**
                 * 下一季度
                 */
                cal.add(Calendar.MONTH, 3);    //加3个月

                int w = cal.get(Calendar.MONTH) ;//0-11
                if(w >= 0 && w <=2){
                    beginDateFormat = new SimpleDateFormat( FIRST_JI_BEGIN_FORMAT);
                    endDateFormat = new SimpleDateFormat( FIRST_JI_END_FORMAT);
                }else if(w >= 3 && w <=5){
                    beginDateFormat = new SimpleDateFormat( SECOND_JI_BEGIN_FORMAT);
                    endDateFormat = new SimpleDateFormat( SECOND_JI_END_FORMAT);
                }else if(w >= 6 && w <=8){
                    beginDateFormat = new SimpleDateFormat( THIRD_JI_BEGIN_FORMAT);
                    endDateFormat = new SimpleDateFormat( THIRD_JI_END_FORMAT);
                }else if(w >= 9 && w <=11){
                    beginDateFormat = new SimpleDateFormat( FOURTH_JI_BEGIN_FORMAT);
                    endDateFormat = new SimpleDateFormat( FOURTH_JI_END_FORMAT);
                }
                String  dayString = beginDateFormat.format(cal.getTime());
                long  down_day = timeForMAt.parse(dayString).getTime();

                dayString = endDateFormat.format(cal.getTime());
                long  up_day = timeForMAt.parse(dayString).getTime();

                dto.setDownDate(down_day);
                dto.setUpDate(up_day);
            }else if(OpportunityCloseDateTypes.THIS_YEAR .equals(opportunityCloseDateType)){
                /**
                 * 本年
                 */
                beginDateFormat = new SimpleDateFormat( YEAR_BEGIN_FORMAT);
                endDateFormat = new SimpleDateFormat( YEAR_END_FORMAT);

                String  dayString = beginDateFormat.format(cal.getTime());
                long  down_day = timeForMAt.parse(dayString).getTime();

                String  dayString2  = endDateFormat.format(cal.getTime());
                Date endDate = timeForMAt.parse(dayString2);

                long  up_day = endDate.getTime();

                dto.setDownDate(down_day);
                dto.setUpDate(up_day);

            }else if(OpportunityCloseDateTypes.NEXT_YEAR .equals(opportunityCloseDateType)){
                /**
                 * 下一年
                 */
                cal.add(Calendar.YEAR, 1);    //加1年

                beginDateFormat = new SimpleDateFormat( YEAR_BEGIN_FORMAT);
                endDateFormat = new SimpleDateFormat( YEAR_END_FORMAT);
                String  dayString = beginDateFormat.format(cal.getTime());
                long  down_day = timeForMAt.parse(dayString).getTime();

                dayString = endDateFormat.format(cal.getTime());
                long  up_day = timeForMAt.parse(dayString).getTime();

                dto.setDownDate(down_day);
                dto.setUpDate(up_day);
            }
            return dto;
        }

    }

    public static void main(String[] args) {
        try {
            SimpleDateFormat timeForMAt = new SimpleDateFormat( Y_M_D_FORMAT);

            DateDto dto = OpportunityDateTypeUtils.getTimeInfo(OpportunityCloseDateTypes.THIS_WEEK);
            Date startTime  = new Date(dto.getDownDate());
            Date endTime  = new Date(dto.getUpDate());
            LOG.info("{}", JSON.toJSONString(dto));
            LOG.info("downtime = {}", timeForMAt.format(startTime));
            LOG.info("uptime = {}", timeForMAt.format(endTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
