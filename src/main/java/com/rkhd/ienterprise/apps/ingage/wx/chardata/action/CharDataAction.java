package com.rkhd.ienterprise.apps.ingage.wx.chardata.action;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DateDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.TimeEnum;
import com.rkhd.ienterprise.apps.ingage.services.CharDataService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.StaticsDateUtil;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


@Namespace("/wx/char")
public class CharDataAction extends WxBaseAction {

    private static final Logger LOG = LoggerFactory.getLogger(CharDataAction.class);

    @Autowired
    private CharDataService charDataService;

    @Autowired
    private UserService userService;

    /**
     * 查询日期类型
     */
    private long searchDateType = TimeEnum.THIS_WEEK.getValue();

    private int maxCount = 300;

    /**
     * 查询销售漏斗的数据
     * @return
     */
    @Action(value = "salesfunnel", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String SalesFunnel() {
        EntityReturnData returnData = null;
        try {
           TimeEnum[] timeEnum = TimeEnum.values();
            TimeEnum temp = null;
            long searchDateType = this.getSearchDateType();
            for(TimeEnum type :timeEnum){
                if(type.getValue() == searchDateType){
                    temp = type;
                    break;
                }
            }
            DateDto dto =  StaticsDateUtil.getTimeInfo(temp);
            SessionUser  sessionUser = getSessionUser();
            TenantParam tenantParam =  sessionUser.getXsy_TenantParam();
            XsyUserDto xsyUserDto = sessionUser.getXsy_user();
            User currentUser = userService.get(xsyUserDto.getId(),tenantParam);

            long startTime = dto.getDownDate();
            long endTime = dto.getUpDate();
            returnData = charDataService.getSalesFunnelCharData(tenantParam,currentUser,startTime,endTime);
            if(!returnData.isSuccess()){
                LOG.error("charDataService.getSalesFunnelCharData error,tenantParam = "+JSON.toJSONString(tenantParam)
                        +" ;currentUser"+ JSON.toJSONString(currentUser)
                        +" ;startTime"+ startTime
                        +" ;endTime"+ endTime

                );
            }
        } catch ( Exception e) {
            e.printStackTrace();
            returnData = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     * 查询销售排名的数据
     * @return
     */
    @Action(value = "salesRank", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getSalesRankData() {
        EntityReturnData returnData = new EntityReturnData();
        try {
            TimeEnum[] timeEnum = TimeEnum.values();
            TimeEnum temp = null;
            long searchDateType = this.getSearchDateType();
            for(TimeEnum type :timeEnum){
                if(type.getValue() == searchDateType){
                    temp = type;
                    break;
                }
            }
            DateDto dto =  StaticsDateUtil.getTimeInfo(temp);
            SessionUser  sessionUser = getSessionUser();
            TenantParam tenantParam =  sessionUser.getXsy_TenantParam();
            XsyUserDto xsyUserDto = sessionUser.getXsy_user();
            User currentUser = userService.get(xsyUserDto.getId(),tenantParam);
            
            long startTime = dto.getDownDate();
            long endTime = dto.getUpDate();

            JSONObject selDataList = charDataService.getSalesRankData(tenantParam,currentUser,startTime,endTime,getMaxCount());

            returnData.setSuccess(true);
            returnData.setEntity(selDataList);
        } catch ( Exception e) {
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    public long getSearchDateType() {
        return searchDateType;
    }

    public void setSearchDateType(long searchDateType) {
        this.searchDateType = searchDateType;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
