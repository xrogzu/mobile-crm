package com.rkhd.ienterprise.apps.ingage.services;

import cloud.multi.tenant.TenantParam;
import com.rkhd.ienterprise.apps.isales.assistant.service.AssistantService;
import com.rkhd.ienterprise.apps.isales.department.model.Depart;
import com.rkhd.ienterprise.apps.isales.department.service.DepartService;
import com.rkhd.ienterprise.base.dbcustomize.constant.DBCustomizeConstants;
import com.rkhd.ienterprise.base.dimension.constant.DimensionConstants;
import com.rkhd.ienterprise.base.dimension.service.DimensionPrivilegeService;
import com.rkhd.ienterprise.base.manager.service.UserManagerService;
import com.rkhd.ienterprise.base.relation.model.GroupMember;
import com.rkhd.ienterprise.base.relation.service.GroupMemberService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.dimension.DimensionUtil;
import com.rkhd.ienterprise.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AuthCommonService {

    @Autowired
    private DimensionPrivilegeService dimensionPrivilegeService;

    @Autowired
    private AssistantService assistantService;

    @Autowired
    private DimensionUtil dimensionUtil;

    @Autowired
    private UserManagerService userManagerService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private DepartService departService;

    public static final int SALES_TRENDS = 20002;
    public static final int SALES_FUNNEL = 20003;

    protected static final int SEARCH_TYPE_USER = 1;//按用户查询
    protected static final int SEARCH_TYPE_DEPART = 2;//按部门查询
    protected static final int SEARCH_TYPE_HIGH_SEA = 3;//按线索公海查询
    protected static final int SEARCH_TYPE_CAMPAIGN = 4;//按市场活动查询


    public boolean canViewAllWithAssist(long belongId,Long userId, TenantParam tenantParam) throws ServiceException {
        if(this.canViewAll(belongId, userId,  tenantParam)) {
            return true;
        } else {
            Iterator i$ = this.getAssistantLeaderIds( userId, tenantParam).iterator();
            boolean canOne;
            do {
                if(!i$.hasNext()) {
                    return false;
                }

                Long leader = (Long)i$.next();
                Map leaderPermissions = this.dimensionUtil.getDimPermissions(leader.longValue(), belongId, tenantParam);
                canOne = true;
                Iterator i$1 = leaderPermissions.keySet().iterator();

                while(i$1.hasNext()) {
                    Short dimType = (Short)i$1.next();
                    if(((Short[])leaderPermissions.get(dimType))[0].shortValue() != 40) {
                        canOne = false;
                    }
                }
            } while(!canOne);

            return true;
        }
    }
    public boolean canViewAll(long belongId,Long userId, TenantParam tenantParam) throws ServiceException {
        Iterator i$ = this.getDimPermissions(belongId,userId,tenantParam).keySet().iterator();
        Short type;
        do {
            if(!i$.hasNext()) {
                return true;
            }
            type = (Short)i$.next();
        } while(((Short[])this.getDimPermissions(belongId,userId,tenantParam).get(type))[0].shortValue() == 40);

        return false;
    }
    public Map<Short, Short[]> getDimPermissions(long belongId, Long userId, TenantParam tenantParam) throws ServiceException {
        return this.dimensionPrivilegeService.getUserPerMapByBelongId(userId, belongId, tenantParam);
    }

    public Set<Long> getAssistantLeaderIds(Long userId, TenantParam tenantParam) throws ServiceException {
        return  new HashSet(this.assistantService.leaderListByAssistantId(userId, tenantParam));
    }

    public String getChartTitlePrefix(User currentUser, TenantParam tenantParam, Integer dashBoardType) throws ServiceException {
        Map<Short, Short[]> permMap = getDimPermissions(DBCustomizeConstants.ENTITY_BELONG_OPPORTUNITY,currentUser.getId(),tenantParam) ;
        Short permType = permMap.get(DimensionConstants.Type.DEPARTMENT)[0];
        String title =  "我的";
        if( permType == DimensionConstants.PrivilegeType.DIMENSION_CHILD || permType == DimensionConstants.PrivilegeType.DIMENSION ) {
            title =  "本部门";
        } else if( permType == DimensionConstants.PrivilegeType.ALL ) {
            title =    "全公司";
        } else {
            if( dashBoardType == SALES_TRENDS || dashBoardType == SALES_FUNNEL ) {
                List<Long> userIds =  userManagerService.getAllSubUserIdsByManagerId(currentUser.getId(),tenantParam);
                if(userIds.size() > 1 ) {
                    title =  "我及我下属的";
                }
            }
        }
        return  title;
    }

    public Set<Long> getDimUserIds(TenantParam tenantParam,User currentUser,long belongId) throws ServiceException {
        Set<Long> users = new HashSet<Long>();
        List<Long> allSales = getAllSalesUserIds(tenantParam);
        if (canViewAllWithAssist(belongId,currentUser.getId(),tenantParam)) {
            users.addAll(allSales);
        } else {
            users.addAll(dimensionUtil.getDimUserIds(currentUser.getId(), belongId, tenantParam));
            // 过掉不是销售的孩子
            for (Iterator<Long> it = users.iterator(); it.hasNext(); ) {
                Long id = it.next();
                if (!allSales.contains(id)) {
                    it.remove();
                }
            }
        }
        return users;
    }
    public List<Long> getAllSalesUserIds(TenantParam tenantParam) throws ServiceException {
        Set<Long> uids = new HashSet<Long>();
        for (Depart depart : getAllSalesDepartments(tenantParam)) {
            for (GroupMember member : groupMemberService.getMembersByGroupAndStatus(depart.getGroupId(), (short) 0, tenantParam)) {
                uids.add(member.getUserId());
            }
        }
        return new ArrayList<Long>(uids);
    }
    private List<Depart> getAllSalesDepartments(TenantParam tenantParam) throws ServiceException {
        List<Depart> departs = new ArrayList<Depart>();
        for (Depart depart : departService.getDepartAll(tenantParam)) {
            if (depart.getDepartType().equals(Depart.DEPART_TYPE_SELL)) {
                departs.add(depart);
            }
        }
        return departs;
    }
    public List<Long> getCurrentUserDepartUserIds(TenantParam tenantParam,User currentUser) throws ServiceException {
        List<Long> uids = new ArrayList<Long>();
        Depart depart = departService.getDepartByUserId(currentUser.getId(), tenantParam);
        for (GroupMember member : groupMemberService.getMembersByGroupAndStatus(depart.getGroupId(), (short) 0, tenantParam)) {
            uids.add(member.getUserId());
        }
        return uids;
    }


}
