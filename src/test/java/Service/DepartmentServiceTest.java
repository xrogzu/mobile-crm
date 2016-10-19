package Service;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by dell on 2016/1/7.
 */
public class DepartmentServiceTest extends BaseSpringTest{
    private  static Logger LOG = LoggerFactory.getLogger(DepartmentServiceTest.class);

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;

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
     * 查询联系人列表
     */
    @Test
    public void getDepartmentDescTest(){
        EntityReturnData ret =  departmentService.getDepartmentDesc(BaseTest.token);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void createDepartmentTest(){
        Department department = new Department();
        department.setDepartCode("");
        department.setDepartName("dev-3");
        department.setDepartType(2L);
        department.setParentDepartId(92312L);

        EntityReturnData ret =  departmentService.createDepartment(BaseTest.token,department);
        LOG.info( JSON.toJSONString(ret));
    }



    @Test
    public void getDepartmentByIdTest(){
        EntityReturnData ret =  departmentService.getDepartmentById(BaseTest.token,84508L);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void updateDepartmentTest(){
        Department department = new Department();
        department.setId(87105L);
        department.setDepartCode("");
        department.setDepartName("dev-3修改后");
        department.setDepartType(2L);
        department.setParentDepartId(84508L);

        EntityReturnData ret =  departmentService.updateDepartment(BaseTest.token,department);
        LOG.info( JSON.toJSONString(ret));
    }
    /**
     * 查询部门树
     */
    @Test
    public void departmentListTest(){
        EntityReturnData ret =  departmentService.getDepartmentList(BaseTest.token);
        LOG.info( JSON.toJSONString(ret));
    }
    @Test
    public void deleteDepartmentByIdTest(){
        //87104,87103,87102,87001,87105,87106
        long[] ids = new long[]{88612L,88613L,88614L,88615L,88616L};
        for(int i=ids.length-1;i>=0;i--){
            EntityReturnData ret =  departmentService.deleteDeparment(BaseTest.token,ids[i]);
            LOG.info("id={},result={}",ids[i],JSON.toJSONString(ret));
        }
    }

    @Test
    public void getMyDepartmenttreeTest(){
        //87104,87103,87102,87001,87105,87106

        String token = "1cd2b33784747e5a3c2e09532243b33f14c6c2cb0edf2c12498c9df8aa001aa1";//BaseTest.token
        EntityReturnData entityReturnData =  departmentService.getMyDepartmenttree(token);
        LOG.info("getMyDepartmenttreeTest data is {}",JSON.toJSONString(entityReturnData));
    }
}
