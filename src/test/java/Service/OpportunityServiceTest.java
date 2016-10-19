package Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.OpportunityDto;
import com.rkhd.ienterprise.apps.ingage.services.DimensionService;
import com.rkhd.ienterprise.apps.ingage.services.OpportunityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/1/11.
 */
public class OpportunityServiceTest extends  BaseSpringTest {

    private  static Logger LOG = LoggerFactory.getLogger(OpportunityServiceTest.class);

    @Autowired
    @Qualifier("mwebOpportunityService")
    private OpportunityService opportunityService;

    private DimensionService dimensionService;

    /**
     * 在每个测试方法之前执行
     */
    @Before
    public  void setUp(){

        super.setUp();
        dimensionService = new DimensionService();
    }

    /**
     * 在每个测试方法之前执行
     */
    @After
    public  void tearDown(){
        super.tearDown();
    }

    /**
     * 查询商机描述
     */
    @Test
    public void getOpportunityDescTest(){
        EntityReturnData ret =  opportunityService.getOpportunityDesc(BaseTest.token);//.getDepartmentDesc(BaseTest.token);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void getOpportunityByIdTest(){
        EntityReturnData ret =  opportunityService.getOpportunityById(BaseTest.token,211517);//.getDepartmentDesc(BaseTest.token);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void createOpportunityTest(){
        OpportunityDto dto = new OpportunityDto();
        dto.setComment("商机内容");
        dto.setOpportunityName("赢单商机2");
        dto.setOpportunityType(1);
//        dto.setSaleStageId("89744");
        dto.setAccountId("278327");
        dto.setCloseDate("2016-02-27");
        dto.setSaleStageId("97509");// 97509:签约成功；97512：输单
        dto.setMoney(10002);
        /**
         *1:客户
         * 2：联系人
         *3:销售机会
         */
        EntityReturnData data  =  dimensionService.dimensionDepartments(BaseTest.token,3);
        JSONObject entity = (JSONObject) data.getEntity();
        JSONArray records = entity.getJSONArray("records");
        JSONObject dim = records.getJSONObject(0);
        long did = dim.getLong("id");
        dto.setDimDepart(did);

        EntityReturnData ret =  opportunityService.createOpportunity(BaseTest.token,JSON.parseObject(JSON.toJSONString(dto)));
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void updateOpportunityTest(){
        OpportunityDto dto = new OpportunityDto();
        dto.setId(165904);
        dto.setComment("商机内容[修改后]");
        dto.setOpportunityName("商机名称[修改后]");
        dto.setOpportunityType(1);
        dto.setSaleStageId("89744");
//        dto.setAccountId("240208"); 修改商机不允许传这个参数
        dto.setCloseDate("2015-12-12");
        /**
         *1:客户
         * 2：联系人
         *3:销售机会
         */
        EntityReturnData data  =  dimensionService.dimensionDepartments(BaseTest.token,3);
        JSONObject entity = (JSONObject) data.getEntity();
        JSONArray records = entity.getJSONArray("records");
        JSONObject dim = records.getJSONObject(0);
        long did = dim.getLong("id");
        dto.setDimDepart(did);

        EntityReturnData ret =  opportunityService.updateOpportunity(BaseTest.token,JSON.parseObject(JSON.toJSONString(dto)));
        LOG.info( JSON.toJSONString(ret));
    }

    /**
     * 商机列表查询
     */
    @Test
    public void getOpportunityListTest(){
//        {"entity":[{"id":165904,"ownerId":0,"opportunityName":"商机名称"}],"success":true}
        EntityReturnData ret =  opportunityService.getOpportunityList(BaseTest.token,"",0,0,30,0,0,0,0,0,"money",true,0,0);
        LOG.info( JSON.toJSONString(ret));
    }

    @Test
    public void delOpportunityListTest(){
        EntityReturnData ret =  opportunityService.deleteOpportunity(BaseTest.token,165906);
        LOG.info( JSON.toJSONString(ret));
    }

    /**
     * 查询商机关联的联系人
     */
    @Test
    public void getContactsTest(){
        EntityReturnData ret =  opportunityService.getContacts(BaseTest.token,211517);
        LOG.info( JSON.toJSONString(ret));
    }
    /**
     * 增加商机关联的联系人
     */
    @Test
    public void addContactssTest(){
        List<Long> ids = new ArrayList<Long>();
        ids.add(new Long("125330"));//联系人"徐-测试"
        EntityReturnData ret =  opportunityService.addContacts(BaseTest.token,166405,ids);
        LOG.info( JSON.toJSONString(ret));
    }
    /**
     * 删除商机关联的联系人
     */
    @Test
    public void removecontactsTest(){
        List<Long> ids = new ArrayList<Long>();
        ids.add(new Long("85804"));//联系人"徐-测试"
        EntityReturnData ret =  opportunityService.deleteContacts(BaseTest.token,166405,ids);
        LOG.info( JSON.toJSONString(ret));
    }

    /**
     * 转移商机测试
     */
    @Test
    public void transOpportunityTest(){
        long  opportunityId = 170217L;
        long targetUserId = 64413L;
        EntityReturnData ret =  opportunityService.transOpportunity(BaseTest.token,opportunityId,targetUserId);
        LOG.info( JSON.toJSONString(ret));
    }
}
