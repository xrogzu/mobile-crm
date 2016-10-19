package Service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class IsvReceiveServiceTest extends  BaseSpringTest {
    private  static Logger LOG = LoggerFactory.getLogger(IsvReceiveServiceTest.class);
//    @Autowired
//    private IsvReceiveService isvReceiveService;

    @Test
    public void __doCreateNewPassPortTest(){
        String loginName = "xu_test2@dingtal.com";
        String corpId = "01fc3bd44bd382e6";
        String permanent_code = "-JmeTRhMSefCw85PSvj8xC8ydKFAqvPjZtqHp2PzqvQLh8XttEHzgs5_Yf-Tu6ND";
        String companyName = "销售易-钉钉";
        String title = "管理员";
        String contact = "15001279241";
//        try {
//            isvReceiveService.__doCreateNewPassPort( loginName, corpId, permanent_code, companyName, title, contact );
//        } catch (ServiceException e) {
//            e.printStackTrace();
//        } catch (PaasException e) {
//            e.printStackTrace();
//        }
//        System.out.println(" test end");
    }
}
