package Service;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ActivityRecordDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ActivityRecordPositionDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.ActivityRecordService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by dell on 2016/1/13.
 */
public class ActivityRecordServiceTest extends BaseSpringTest{
    private  static Logger LOG = LoggerFactory.getLogger(ActivityRecordServiceTest.class);
    @Autowired
    @Qualifier("mwebActivityRecordService")
    private ActivityRecordService activityRecordService;
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

    @Test
    public void getRecordListTest(){
        long d = 0L;// new Date().getTime()-100000000;
        //objectId":"264313","belongId":"1"
        EntityReturnData ret =  activityRecordService.getPage(BaseTest.token,0,20,1,276536,null,null,0,null);

        LOG.info(JSON.toJSONString(ret));
    }
    @Test
    public void getActivityRecordDescTest(){
        EntityReturnData ret =  activityRecordService.getDesc(BaseTest.token);

        LOG.info(JSON.toJSONString(ret));
    }

    @Test
    public void createActivityRecordDescTest(){
        ActivityRecordDto activityRecordDto = new ActivityRecordDto();
        activityRecordDto.setActivityTypeId(120833);//从打desc里查值
        activityRecordDto.setBelongId(1);
        activityRecordDto.setContent("测试活动记录");
        activityRecordDto.setSource(1);//来源业务对象1：客户，2：联系人，3：销售机会，6：市场活动，11：销售线索
        activityRecordDto.setObjectId(276536);//{"id":264033,"accountName":"测试2016"}

        ActivityRecordPositionDto positionDto = new ActivityRecordPositionDto();
        positionDto.setLocationDetail("位置详情");
        positionDto.setLocation("北京朝阳");
        positionDto.setLongitude(101.42);
        positionDto.setLatitude(122.65);

        activityRecordDto.setPosition(positionDto);

        EntityReturnData ret =  activityRecordService.addActivityRecord(BaseTest.token,activityRecordDto);

        LOG.info(JSON.toJSONString(ret));
    }


}
