package Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Contact;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.ContactService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dell on 2015/12/18.
 */
public class ContactServiceTest extends BaseSpringTest{
    private  static Logger LOG = LoggerFactory.getLogger(ContactServiceTest.class);
    private ContactService contactService;

    /**
     * 在每个测试方法之前执行
     */
    @Before
    public  void setUp(){

        super.setUp();
        contactService = new ContactService();
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
    public void getContactListTest(){
        long d = 0L;// new Date().getTime()-100000000;
        int  endTime  = 0;//0:全部 ，1：本日，2：昨日，3：本周，4：本月，5：本季，6本年
        long startDate = 30000;//起始日期，毫秒计算
        EntityReturnData ret =  contactService.getContactList(BaseTest.token,null,0L,0,30,startDate,endTime,"createdAt",true);
        JSONObject entity = (JSONObject) ret.getEntity();
//        JSONArray records = entity.getJSONArray("records");
//        Map<String ,String> map = new HashMap<String,String>();
//        for(int i=0;i<records.size();i++){
//            JSONObject item = records.getJSONObject(i);
//            String contactName = item.getString("contactName");
//            if(map.containsKey(contactName)){
//                EntityReturnData data = contactService.deleteContact(BaseTest.token,item.getLong("id"));
//                LOG.info("删除对象{}，返回结果：{}",JSON.toJSONString(item),JSON.toJSONString(data));
//            }else{
//                map.put(contactName,"111");
//            }
//
//        }

        LOG.info(JSON.toJSONString(ret));
    }
    @Test
    public void getContactDescTest(){
        EntityReturnData ret =  contactService.getContactDesc(BaseTest.token);
        LOG.info(JSON.toJSONString(ret));
    }

    /**
     * 分页查看联系人列表
     */
    @Test
    public void getContactPageTest(){
        EntityReturnData ret =  contactService.getContactPage(BaseTest.token,"ceshi",0 ,30);
        JSONArray res = (JSONArray)ret.getEntity();
        LOG.info("总数为："+res.size());
        LOG.info(JSON.toJSONString(ret));
    }
    @Test
    public void getContactDetail(){
        EntityReturnData ret =  contactService.getContactById(BaseTest.token,125330);
        LOG.info("联系人信息：{}",JSON.toJSONString(ret));
    }

    @Test
    public void createContactTest(){
        //for(int i=0;i<100;i++){
//        {
//            "record": {
//            "birthday": "2016-06-06",
//                    "phone": "",
//                    "post": "",
//                    "accountId": "332712",
//                    "address": "",
//                    "email": "",
//                    "depart": "",
//                    "contactName": "性别测试2",
//                    "ownerId": "78539",
//                    "zipCode": "",
//                    "state": "",
//                    "comment": "",
//                    "dimDepart": "108707",
//                    "weibo": "",
//                    "mobile": ""
//        }
//        }
            //contact.setGender(1);
        JSONObject  contact = new JSONObject();
//        contact.put("accountId","332712");
//        contact.put("contactName","性别测试3");
//        contact.put("ownerId","78539");
//        contact.put("dimDepart","108707");
//        contact.put("birthday","2016-06-06");
//        contact.put("phone","");
//        contact.put("post","");
//        contact.put("address","");
//        contact.put("email","");
//        contact.put("depart","");
//        contact.put("zipCode","");
//        contact.put("state","");
//        contact.put("comment","");
//        contact.put("weibo","");
//        contact.put("mobile","");

 String contactString = "{\"birthday\":\"2016-06-06\",\"phone\":\"\",\"post\":\"\",\"accountId\":\"292551\",\"address\":\"\",\"email\":\"\",\"depart\":\"\",\"contactName\":\"genderTest1\",\"ownerId\":\"78539\",\"zipCode\":\"\",\"state\":\"\",\"comment\":\"\",\"dimDepart\":\"108708\",\"weibo\":\"\",\"mobile\":\"\"}";
        contact = JSON.parseObject(contactString);


            EntityReturnData data = contactService.createContact(BaseTest.token,contact);
            System.out.println("添加联系人返回结果为："+JSON.toJSONString(data));
        //}

    }
    @Test
    public void delTest(){
        long[] ids = new long[]{127853,127854,127863,127864,127865};
        for(int i=0;i<ids.length;i++){
            long id = ids[i];
            EntityReturnData data = contactService.deleteContact(BaseTest.token,id);
            LOG.info("{}",JSON.toJSONString(data));

        }

    }
    @Test
    public void delTransTest(){
        long id = 0L;
        long targetOwnerId = 0L;
        EntityReturnData entityReturnData = contactService.doTrans(BaseTest.token,id,targetOwnerId);

    }


}
