package Service;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.rkhd.ienterprise.apps.ingage.services.StaticsServices;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.exception.ServiceException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 统计测试
 */
public class StatisticsTest extends  BaseSpringTest {

    private  static Logger LOG = LoggerFactory.getLogger(StatisticsTest.class);

    @Autowired
    private StaticsServices staticsServices;

//    public static final int SEARCH_TYPE_USER = 1;//按用户查询
//    public static final int SEARCH_TYPE_DEPART = 2;//按部门查询


    /**
     * 统计活动记录
     */

    @Test
    public void doGetActiveRecordStatisticsTest(){
        String xsy_accessToken = BaseTest.token;
        long xsyUserId = AuthServiceTest.userId;
        TenantParam tenantParam = new TenantParam(AuthServiceTest.tenantId);
        long startTime = 0L;
        long endTime = System.currentTimeMillis();
        JSONArray result = null;
        User currentUser = new User();
        currentUser.setId(xsyUserId);
        try {
            result = staticsServices.getActiveRecordCountMap(currentUser,tenantParam,startTime,endTime);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        LOG.info("result={}",JSON.toJSONString(result));
    }




}
