package Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.ScheduleService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dell on 2016/7/25.
 */
public class ScheduleServiceTest extends  BaseSpringTest {

    private  static Logger LOG = LoggerFactory.getLogger(ScheduleServiceTest.class);

    @Autowired
    private ScheduleService scheduleService;



    /**
     * 在每个测试方法之前执行
     */
    @Before
    public  void setUp(){

        super.setUp();

    }

    /**
     * 在每个测试方法之前执行
     */
    @After
    public  void tearDown(){
        super.tearDown();

    }

    /**
     * 所有方法测试完之后执行
     */
    @AfterClass
    public static void tearDownAll(){

    }
    @Test
    public void descTest(){

        EntityReturnData ret =  scheduleService.desc(BaseTest.token);
        LOG.info( JSON.toJSONString(ret));
    }

    @Test
    public void doCreateTest(){
        JSONObject record = new JSONObject();
        record.put("name","重复永不停止");
        record.put("description","描述1");
        record.put("startDate","2016-08-02 08:00");
        record.put("endDate","2016-08-04 10:00");

        record.put("isPrivate","true");//私有的
        record.put("isAllDay","true");
        record.put("isRecur","true");
        record.put("frequency","1");
        record.put("recurStopCondition","1");
        record.put("belongId","1");
        record.put("objectId","373501");
//        record.put("objectId","1");
        record.put("reminder","86400");
        record.put("type","1958179");
        JSONArray members = new JSONArray();
        members.add("93706");
        members.add("93707");
        record.put("members",members);
       EntityReturnData ret =  scheduleService.docreate(BaseTest.token,record);
        LOG.info( JSON.toJSONString(ret));
    }

    @Test
    public void doisRecurTest(){
        JSONObject record = new JSONObject();
        record.put("name","按月重复-开始0823，跨度3天，重复结束于1029");
        record.put("description","描述8");
        record.put("startDate","2016-08-23 08:00");
        record.put("endDate","2016-08-25 10:00");
        record.put("isAllDay",true);
        record.put("isPrivate",true);//私有的
        record.put("isRecur",true);//是重复
        record.put("frequency","3");//1：每日重复；2：每周重复3：每月重复
        record.put("recurStopCondition","2");//1：永不结束；2：结束日期
         record.put("recurStopValue","2016-10-29");//终止日期
        record.put("belongId",1);
        record.put("objectId",373501);
        record.put("reminder",86400);//定时提醒,提前1天
        record.put("type",1958179);//会议
        JSONArray members = new JSONArray();
        members.add("93741");
        members.add("93742");
        record.put("members",members);
        EntityReturnData ret =  scheduleService.docreate(BaseTest.token,record);
        LOG.info( JSON.toJSONString(ret));
    }

    @Test
    public void doUpdateTest(){

        JSONObject record = new JSONObject();
        record.put("id",119537);
        record.put("name","拜访老刘-119524");
        record.put("description","描述1");
        record.put("startDate","2016-08-02 08:00");
        record.put("endDate","2016-08-04 10:00");
        record.put("isAllDay","true");
        record.put("isRecur","false");
        record.put("isPrivate","true");//私有的
        record.put("frequency","1");
        record.put("recurStopCondition","1");
        record.put("belongId","1");
        record.put("objectId","373501");
//        record.put("objectId","1");
        record.put("reminder","86400");
        record.put("type","1958179");
        JSONArray members = new JSONArray();
        members.add("93706");
        members.add("93707");
        record.put("members",members);
        EntityReturnData ret =  scheduleService.doUpdate(BaseTest.token,record);
        LOG.info( JSON.toJSONString(ret));
    }

    @Test
    public void getTest(){
        long id = 120743;
        EntityReturnData ret =  scheduleService.get(BaseTest.token,id);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void acceptTest(){
        long id = 119524;
        EntityReturnData ret =  scheduleService.accept(BaseTest.token,id);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void quitTest(){
        long id = 119524;
        EntityReturnData ret =  scheduleService.quit(BaseTest.token,id);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void rejectTest(){
        long id = 119524;
        EntityReturnData ret =  scheduleService.reject(BaseTest.token,id);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void deleteTest(){
        long id = 119524;
        EntityReturnData ret =  scheduleService.delete(BaseTest.token,id);
        LOG.info( JSON.toJSONString(ret));
    }

    @Test
    public void listTest(){



        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH,6);//从0开始
        cal.set(Calendar.DAY_OF_MONTH,1);//从1开始
        cal.set(Calendar.MINUTE, 0);//从0开始
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();


        cal.set(Calendar.MONTH,8);//从0开始
        cal.set(Calendar.DAY_OF_MONTH,30);//从1开始
        cal.set(Calendar.HOUR_OF_DAY, 23);//从0开始
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long endTime = cal.getTimeInMillis();

//        cal.add(Calendar.DATE,-2);


        EntityReturnData ret =  scheduleService.list(BaseTest.token,startTime,endTime,0L,"",0L,0L);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void dateFormatTest(){
        SimpleDateFormat startdf = new SimpleDateFormat("yyyy-MM-dd");
        String dString = "2016-0-1";
        try {
            LOG.info(""+startdf.parse(dString).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }




}
