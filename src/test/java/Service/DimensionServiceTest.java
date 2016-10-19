package Service;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.DimensionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dell on 2015/12/22.
 */
public class DimensionServiceTest extends BaseSpringTest{

    private static final Logger LOG = LoggerFactory.getLogger(DimensionServiceTest.class);

    private DimensionService dimensionService;

    /**
     * 在每个测试方法之前执行
     */
    @Before
    public  void setUp(){
        dimensionService = new DimensionService();
    }
    @After
    public  void tearDown(){
        super.tearDown();
    }
    @Test
    public void dimensionBelongTest(){
        EntityReturnData data  =  dimensionService.dimensionBelong(BaseTest.token);
        System.out.println("data = "+ JSON.toJSONString(data));

    }
    @Test
    public void dimensionDepartmentsTest(){
        /**
         *1:客户
         * 2：联系人
         *3:销售机会
         */
        EntityReturnData data  =  dimensionService.dimensionDepartments(BaseTest.token,1);
        System.out.println("data = "+ JSON.toJSONString(data));

    }
    @Test
    public  void dataPermissionTest(){
        long belongId = 3;
        long id = 175609L;
        EntityReturnData entityReturnData =  dimensionService.dataPermission("92c0829dcf9058bfdf7e5cf738570c92931054c39d1f179cc9a6756e97783e95",belongId,id);
        LOG.info("{}",entityReturnData);
    }
}
