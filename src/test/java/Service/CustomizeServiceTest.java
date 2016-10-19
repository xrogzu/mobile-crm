package Service;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.enums.CustomerEntityType;
import com.rkhd.ienterprise.apps.ingage.services.CustomizeService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dell on 2016/5/19.
 */
public class CustomizeServiceTest extends BaseSpringTest{
    private  static Logger LOG = LoggerFactory.getLogger(ContactServiceTest.class);

    @Autowired
    private CustomizeService customizeService;

    @Test
    public void getCustomizeLayout(  ){
        long entityId = 0L;
        String authorization = "";
        CustomerEntityType type = CustomerEntityType.opportunity;
        EntityReturnData result = customizeService.getDefaultCustomizeLayout(BaseTest.token,0,type);
        LOG.info("result is "+ JSON.toJSONString(result));
    }
}
