package Service;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.services.XsyApiUserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dell on 2015/12/17.
 */
public class UserServiceTest extends BaseSpringTest{
    private  static Logger LOG = LoggerFactory.getLogger(UserServiceTest.class);
    private XsyApiUserService userService;

    /**
     * 在每个测试方法之前执行
     */
    @Before
    public  void setUp(){
        userService = new XsyApiUserService();
    }
    @After
    public  void tearDown(){
        super.tearDown();
    }
    @Test
    public void getUserDescTest(){
        EntityReturnData entity = userService.getUserDesc(BaseTest.token);
        LOG.info("用户描述的返回数据为："+ JSON.toJSONString(entity));
    }

    @Test
    public void getUserInfoTest(){
        //{"entity":{"position":"","icon":"","birthday":"","phone":"15001279241","status":"已激活","statusInt":1,"employeeCode":"","id":64413,"email":"","joinAtStr":"","name":"徐保勇","gender":"","departId":85205},"success":true}
        EntityReturnData entity = userService.getUserInfo("67b7b24134579ac3f0405da1ec0dd3cbeb0948973c437c1d01d2919da86a8917",93707);
        LOG.info("查询用户信息的返回数据为："+ JSON.toJSONString(entity));
    }
    @Test
    public void getUserListTest(){
        EntityReturnData entity = userService.getUserList(BaseTest.token,null,0,10);
        LOG.info("查询用户列表的返回数据为："+ JSON.toJSONString(entity));
    }
    @Test
    public void createUserTest(){
        XsyUserDto xsyUser = new XsyUserDto();
        xsyUser.setPhone("15210578828");
        xsyUser.setEmail("04jiangwei04@163.com");
        xsyUser.setGender(1);
        xsyUser.setName("员工2");
        xsyUser.setDepartId(0);
        xsyUser.setUserManagerId(75011);

        EntityReturnData entity = userService.createUser(BaseTest.token,xsyUser);
        LOG.info("创建用户的返回数据为："+ JSON.toJSONString(entity));
    }
    @Test
    public void updateUserTest(){
        XsyUserDto xsyUser = new XsyUserDto();
        xsyUser.setId(65706L);
        xsyUser.setEmail("01jiangwei01@163.com");
        xsyUser.setGender(1);
        xsyUser.setDepartId(84508L);
        xsyUser.setUserManagerId(64413L);
        xsyUser.setName("员工1-修改");

        EntityReturnData entity = userService.updateUser(BaseTest.token,JSON.parseObject(JSON.toJSONString(xsyUser)));
        LOG.info("修改用户的返回数据为："+ JSON.toJSONString(entity));
    }

    @Test
    public void deleteUserTest(){
        long[] ids = new long[]{87305L,87304L};
        for(int i=0;i<ids.length;i++){
            EntityReturnData ret =  userService.deleteUser(BaseTest.token,ids[i]);
            LOG.info("id={},result={}",ids[i],JSON.toJSONString(ret));
        }
    }
}
