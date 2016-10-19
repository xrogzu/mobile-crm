package Service;


import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/applicationContext-sca.xml" })
public class BaseSpringTest {//extends AbstractTransactionalJUnit4SpringContextTests
	private  static Logger logger = LoggerFactory.getLogger(BaseSpringTest.class);

	private long  begintime = 0;
	private long endtime = 0;
	@BeforeClass
	public  static void beforeClass(){

		//logger.info("执行顺序：{}","@BeforeClass");
		//PropertyConfigurator.configure ("log4j.properties");

		SystemGlobals.loadConfig(BaseTest.sysconfigFilePath);

		String crmapi_host_url_key  = "crmapi.host.url";
		String crmapi_host = SystemGlobals.getPreference("dev."+crmapi_host_url_key);
		SystemGlobals.setPreference(crmapi_host_url_key,crmapi_host);
	}
	@Before
	public  void setUp(){
		begintime = System.currentTimeMillis();
	}

	/**
	 * 在每个测试方法之前执行
	 */
	@After
	public  void tearDown(){
		endtime = System.currentTimeMillis();
		long c_b = endtime  - begintime;
		if(c_b <100){
			logger.info("优秀,耗时"+c_b+"毫秒");
		}else if(c_b <1000){
			logger.info("需优化,耗时"+c_b+"毫秒");
		}else  if(c_b <30000){
			logger.info("报警,耗时"+c_b+"毫秒,折合"+(c_b/1000)+"秒");
		}else  {
			logger.info("严重报警,耗时"+c_b+"毫秒,折合"+(c_b/1000)+"秒，约"+(c_b/(1000*60))+"分钟");
		}

	}
//	@Test
//	public void test() throws SQLException{
//		System.out.println("test");
//	}

}
