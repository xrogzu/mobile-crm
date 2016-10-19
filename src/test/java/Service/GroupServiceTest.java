package Service;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.GroupService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/2/2.
 */
public class GroupServiceTest extends BaseSpringTest {
    private  static Logger LOG = LoggerFactory.getLogger(GroupServiceTest.class);

    @Autowired
    @Qualifier("mwebGroupService")
    private GroupService groupService;

    /**
     * 查询相关成员测试
     */
    @Test
    public void relateownerListTest(){
        EntityReturnData ret = null;
         long  businessId = 264033;//实体对象的主键id
        long belongs  = 1;//取值范围 1:客户,2：联系人,3:销售机会
        try{
            ret = groupService.queryMember(BaseTest.token,businessId, belongs,0);
        }catch (Exception e){
            e.printStackTrace();
            ret =  new EntityReturnData();
        }
        LOG.info("查询结果为：{}", JSON.toJSONString(ret));
    }

    /**
     * 设置相关成员测试
     */
    @Test
    public void setrelateownerTest(){
        EntityReturnData ret = null;
        long  businessId = 264033;//实体对象的主键id
        long belongs  = 1;//取值范围 1:客户,2：联系人,3:销售机会
        try{
            List<Long> userId = new ArrayList<Long>();
            userId.add(64413L);
            userId.add(67420L);
            ret = groupService.joinRelated(BaseTest.token,belongs ,businessId,userId);
        }catch (Exception e){
            e.printStackTrace();
            ret =  new EntityReturnData();
        }
        LOG.info("增加团队成员结果为：{}", JSON.toJSONString(ret));
    }
    @Test
    public void delrelateownerTest(){
        EntityReturnData ret = null;
        long  businessId = 264033;//实体对象的主键id
        long belongs  = 1;//取值范围 1:客户,2：联系人,3:销售机会
        try{
            List<Long> userId = new ArrayList<Long>();
            userId.add(64413L);
            userId.add(67421L);
            ret = groupService.quitMember(BaseTest.token,belongs,businessId,userId);
        }catch (Exception e){
            e.printStackTrace();
            ret =  new EntityReturnData();
        }
        LOG.info("删除团队成员结果为：{}", JSON.toJSONString(ret));
    }
}
