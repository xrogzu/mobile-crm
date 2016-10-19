package Service;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.CharDataService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.exception.ServiceException;
import net.sf.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dell on 2016/2/29.
 */
public class CharDataServiceTest extends BaseSpringTest{
    private  static Logger LOG = LoggerFactory.getLogger(CharDataServiceTest.class);

    @Autowired
    private CharDataService charDataService;

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
    public void getCharDataTest(){

        TenantParam tenantParam = new TenantParam(AuthServiceTest.tenantId);

        User currentUser = new User();
        currentUser.setId(AuthServiceTest.userId);
        long startTime = 0l;
        long endTime = System.currentTimeMillis();

        try {
            EntityReturnData retData =  charDataService.getSalesFunnelCharData( tenantParam,currentUser, startTime,endTime);
            LOG.info("retData={}", JSON.toJSONString(retData));

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void getSalesRankDataTest(){

        TenantParam tenantParam = new TenantParam(AuthServiceTest.tenantId);

        User currentUser = new User();
        currentUser.setId(AuthServiceTest.userId);
        currentUser.setDepartId(176327L);
        long startTime =1464710400000l;
        long endTime =1467302399000L;
        try {
            JSONObject retData =  charDataService.getSalesRankData( tenantParam,currentUser, startTime,endTime,300);
            LOG.info("retData={}", JSON.toJSONString(retData));

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
