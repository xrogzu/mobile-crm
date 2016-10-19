package Service;

import cloud.multi.tenant.TenantParam;
import com.rkhd.ienterprise.base.oauth.model.OauthToken;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.exception.ServiceException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dell on 2016/1/20.
 */
public class AuthServiceTest extends BaseSpringTest{

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceTest.class);

    @Autowired
    private OauthTokenService oauthTokenService;


    public static final  long userId = 93741;

    public static final  long tenantId = 102520;

    @Test
    public void getOauth(){
        try {
            OauthToken oauthToken = oauthTokenService.getBasicTokenByUserIdAndTenantId(userId,new TenantParam(tenantId));
            LOG.info("oauthToken.accessToken={}",oauthToken.getAccessToken());
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
