package Service;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.AccountDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.AccountService;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by dell on 2015/12/17.
 */

public class AccountServiceTest extends BaseSpringTest{
    private  static Logger LOG = LoggerFactory.getLogger(AccountServiceTest.class);
    @Autowired
    @Qualifier("mwebAccountService")
    private AccountService accountService;


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
    public void getCompanyListTest(){
        long a = System.currentTimeMillis();
        EntityReturnData ret = accountService.getAccountList(BaseTest.token,"",0,0,10,0,0);
        long b = System.currentTimeMillis();
        LOG.info("查询耗时"+(b-a)+"毫秒,结果为："+JSON.toJSONString(ret));
    }
    @Test
    public void getCompanyDescTest(){
        long a = System.currentTimeMillis();
        EntityReturnData ret = accountService.getAccountDesc(BaseTest.token);
        long b = System.currentTimeMillis();
        LOG.info("查询客户描述耗时"+(b-a)+"毫秒,结果为："+JSON.toJSONString(ret));
    }
    @Test
    public void getAccountByIdTest(){
        long a = System.currentTimeMillis();
        EntityReturnData ret = accountService.getAccountById(BaseTest.token,360014);
        long b = System.currentTimeMillis();
        LOG.info("查询客户耗时"+(b-a)+"毫秒,结果为："+JSON.toJSONString(ret));
    }
    @Test
    public void createAccountTest(){
//        for(int i=100;i<200;i++){
            AccountDto company = new AccountDto();
            company.setAccountName("高信"+(1));
            company.setComment("备注1");
            company.setOwnerId("74823");
            company.setDimDepart(92105);//联系人多维度
            company.setLevel(1);

            EntityReturnData ret =  accountService.createAccount(BaseTest.token,JSON.parseObject(JSON.toJSONString(company)));
            LOG.info("创建客户返回结果为："+JSON.toJSONString(ret));
//        }
    }
    @Test
    public void updateAccountByIdTest(){
        //{"entity":{"region":"","recentActivityCreatedBy":"","phone":"","ownerId":64413,"state":"","parentAccountId":"","updatedBy":64413,"city":"","id":240208,"level":"","recentActivityRecordTime":"","createdAt":"2015-12-22 17:05","approvalStatus":"","zipCode":"","longitude":"","members":[{"id":64413,"status":0,"ownerFlag":0}],"fax":"","applicantId":"","url":"","updatedAt":"2015-12-22 17:05","accountName":"高信1","annualRevenue":"","createdBy":64413,"address":"","attributes":{"currencyUnit":"元"},"latitude":"","comment":"备注1","industryId":"","employeeNumber":"","owners":[],"dimDepart":85205,"weibo":""},"success":true}
        long a = System.currentTimeMillis();
        AccountDto company = new AccountDto();
        company.setAccountName("高信"+(200+2));
        company.setComment("备注1");
        company.setOwnerId("64413");
        company.setDimDepart(84508);//联系人多维度
        company.setLevel(1);
        company.setId(240208);

        EntityReturnData ret = accountService.updateAccount(BaseTest.token,JSON.parseObject(JSON.toJSONString(company)));
        long b = System.currentTimeMillis();
        LOG.info("修改客户耗时"+(b-a)+"毫秒,结果为："+JSON.toJSONString(ret));
    }

}
