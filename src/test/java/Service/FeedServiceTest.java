package Service;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.FeedService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dell on 2016/3/8.
 */
public class FeedServiceTest extends BaseSpringTest{
    private  static Logger LOG = LoggerFactory.getLogger(FeedServiceTest.class);
    @Autowired
    private FeedService feedService;

    @Before
    public  void setUp(){

    }
    @After
    public  void tearDown(){
        super.tearDown();
    }

    @Test
    public void findFeedListTest(){
        String authorization = "883823daf49304c16b77b8005872aa60f9ecdac201959698ad698480b09dc552";//BaseTest.token;
        long belongId = 1;
        long objectId = 292568;
        int pageNo = 1;
        int pageSize = 20 ;
        EntityReturnData data =  feedService.findFeedList(authorization,belongId,objectId,pageNo,pageSize);
        LOG.info(JSON.toJSONString(data));

    }
}
