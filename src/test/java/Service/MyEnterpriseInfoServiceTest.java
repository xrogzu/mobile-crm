package Service;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.MyEnterpriseInfoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by dell on 2016/8/2.
 */
public class MyEnterpriseInfoServiceTest extends  BaseSpringTest {
    private  static Logger LOG = LoggerFactory.getLogger(MyEnterpriseInfoServiceTest.class);

    @Autowired
    @Qualifier("mwebMyEnterpriseInfoService")
    private MyEnterpriseInfoService myEnterpriseInfoService;

    @Before
    public  void setUp(){

    }
    @After
    public  void tearDown(){
        super.tearDown();
    }

    @Test
    public void findListTest(){
        TenantParam tenantParam = new TenantParam(AuthServiceTest.tenantId);
        EntityReturnData data =   myEnterpriseInfoService.search(BaseTest.token,tenantParam,"联想控股",0);
        LOG.info(JSON.toJSONString(data));

    }
    @Test
    public void findTest(){

        EntityReturnData data =  myEnterpriseInfoService.get("深圳联想海外控股有限公司大连分公司");
        LOG.info(JSON.toJSONString(data));

    }
}
