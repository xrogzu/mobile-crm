package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by dell on 2015/12/17.
 */
public class APIResultUtil {

    /**
     * 解析返回结果
     * @param apiResultString
     * @return
     */
    public static EntityReturnData executeResult(String apiResultString){
        EntityReturnData  returnData = new EntityReturnData();
        if(StringUtils.isBlank(apiResultString)){
            return returnData;
        }
        JSONObject json =  JSON.parseObject(apiResultString);
        if(json.containsKey("error_code") &&  0 != json.getIntValue("error_code") ){
            String errorCode = json.getString("error_code");
            String  errorMsg = json.getString("message");
            returnData.setErrorCode(errorCode);
            returnData.setMsg(errorMsg);
            return returnData;
        }else{
            returnData.setSuccess(true);
            returnData.setEntity(json);
        }
        return returnData;
    }
    public static EntityReturnData networkErrorMsg(){
        EntityReturnData  returnData = new EntityReturnData();
        returnData.setMsg("网络异常");
        return returnData;
    }
    /**
     * 解析异常原因
     */
    public static String errorParse2Msg(String errorcode){
        String errorMsg = null;
        if(StringUtils.isBlank(errorcode)){
            return errorMsg;
        }
        if("100000".equals(errorcode)){
            errorMsg = "系统异常";
        }else if("100001".equals(errorcode)){
            errorMsg = "公司（租户）权限错误";
        }else if("100002".equals(errorcode)){
            errorMsg = "参数不完整";
        }else if("100003".equals(errorcode)){
            errorMsg = "参数错误";
        }else if("100004".equals(errorcode)){
            errorMsg = "数据读取异常";
        }else if("100005".equals(errorcode)){
            errorMsg = "已经弃用的接口";
        }else if("110001".equals(errorcode)){
            errorMsg = "用户名密码不匹配";
        }else if("110002".equals(errorcode)){
            errorMsg = "无效的app_key";
        }else if("110003".equals(errorcode)){
            errorMsg = "用户临时授权码过期";
        }else if("110004".equals(errorcode)){
            errorMsg = "无效的用户临时授权码";
        }else if("110005".equals(errorcode)){
            errorMsg = "获取access token失败";
        }else if("110010".equals(errorcode)){
            errorMsg = "无效的access token";
        }else if("110011".equals(errorcode)){
            errorMsg = "用户没有访问权限";
        }else if("110012".equals(errorcode)){
            errorMsg = "license已经过期";
        }else if("110013".equals(errorcode)){
            errorMsg = "没有license授权";
        }else if("110020".equals(errorcode)){
            errorMsg = "用户未激活";
        }else if("110021".equals(errorcode)){
            errorMsg = "用户已删除";
        }else if("110022".equals(errorcode)){
            errorMsg = "用户离职";
        }else if("110023".equals(errorcode)){
            errorMsg = "用户创建失败";
        }else if("110024".equals(errorcode)){
            errorMsg = "邮件格式错误";
        }else if("110025".equals(errorcode)){
            errorMsg = "手机格式错误";
        }else if("110026".equals(errorcode)){
            errorMsg = "日期格式错误";
        }else if("200002".equals(errorcode)){
            errorMsg = "对象不存在";
        }else if("200003".equals(errorcode)){
            errorMsg = "指定的对象为空";
        }else if("200004".equals(errorcode)){
            errorMsg = "操作的数据太多";
        }else if("200005".equals(errorcode)){
            errorMsg = "不匹配的错误";
        }else if("200006".equals(errorcode)){
            errorMsg = "长度超出";
        }else if("290000".equals(errorcode)){
            errorMsg = "Soql表达式为空";
        }else if("290001".equals(errorcode)){
            errorMsg = "Soql语法格式错误";
        }else if("290002".equals(errorcode)){
            errorMsg = "不支持查询所有列";
        }else if("290003".equals(errorcode)){
            errorMsg = "列''{0}''不存在";
        }else if("290004".equals(errorcode)){
            errorMsg = "不支持的关键字''{0}''";
        }else if("290005".equals(errorcode)){
            errorMsg = "不支持的操作";
        }else if("290006".equals(errorcode)){
            errorMsg = "不支持的表达式";
        }else if("290007".equals(errorcode)){
            errorMsg = "不支持的取值";
        }else if("290008".equals(errorcode)){
            errorMsg = "不支持子查询";
        }else if("290009".equals(errorcode)){
            errorMsg = "不支持的列类型或聚合函数";
        }else if("290010".equals(errorcode)){
            errorMsg = "中文转换错误";
        }else if("290011".equals(errorcode)){
            errorMsg = "对象''{0}''不存在";
        }else if("290012".equals(errorcode)){
            errorMsg = "对象名称错误";
        }else if("290013".equals(errorcode)){
            errorMsg = "Soql表达式长度超出,最大为{0}";
        }else if("290014".equals(errorcode)){
            errorMsg = "自定义业务实体必须指定belongId,且与其它条件为and关系";
        }else if("290015".equals(errorcode)){
            errorMsg = "belongId对应的自定义业务实体不存在";
        }else if("300001".equals(errorcode)){
            errorMsg = "没有操作权限";
        }else if("300002".equals(errorcode)){
            errorMsg = "业务对象不存在";
        }else if("300003".equals(errorcode)){
            errorMsg = "业务对象重复";
        }else if("300004".equals(errorcode)){
            errorMsg = "{0}已经存在";
        }else if("300005".equals(errorcode)){
            errorMsg = "未启用的维度";
        }else if("300006".equals(errorcode)){
            errorMsg = "{0}不存在";
        }else if("300007".equals(errorcode)){
            errorMsg = "字段''{0}''类型错误";
        }else if("300008".equals(errorcode)){
            errorMsg = "不能变更分组";
        }else if("310007".equals(errorcode)){
            errorMsg = "没有客户池分组";
        }else if("310008".equals(errorcode)){
            errorMsg = "没有线索池分组";
        }else if("310101".equals(errorcode)){
            errorMsg = "上级公司是当前客户本身";
        }else if("310102".equals(errorcode)){
            errorMsg = "上级公司是当前客户的子公司";
        }else if("310103".equals(errorcode)){
            errorMsg = "关联客户最多只能设置10级";
        }else if("313001".equals(errorcode)){
            errorMsg = "用户无权创建线索";
        }else if("313007".equals(errorcode)){
            errorMsg = "没有线索池分组";
        }else if("314001".equals(errorcode)){
            errorMsg = "仅能修改未生效的订单";
        }else if("314002".equals(errorcode)){
            errorMsg = "仅能删除未生效的订单";
        }else if("315001".equals(errorcode)){
            errorMsg = "部门编码重复";
        }else if("315002".equals(errorcode)){
            errorMsg = "部门已经被关联,不可删除";
        }else if("315003".equals(errorcode)){
            errorMsg = "上级部门不能设定为当前部门的子部门";
        }else if("315101".equals(errorcode)){
            errorMsg = "用户已经激活不能删除";
        }else if("315102".equals(errorcode)){
            errorMsg = "登录帐号不可修改";
        }
        return errorMsg;
    }
}
