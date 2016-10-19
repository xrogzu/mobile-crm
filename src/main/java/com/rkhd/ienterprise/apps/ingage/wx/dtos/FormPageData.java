package com.rkhd.ienterprise.apps.ingage.wx.dtos;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * form表单页面需要展示的信息
 */
public class FormPageData implements Serializable {

    private static final long serialVersionUID = -1522613058650465808L;

    private JSONObject structure; //结构体对象

    private JSONObject data;//实际数据

    private JSONObject expandPro;//扩展属性

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public JSONObject getExpandPro() {
        return expandPro;
    }

    public void setExpandPro(JSONObject expandPro) {
        this.expandPro = expandPro;
    }

    public JSONObject getStructure() {
        return structure;
    }

    public void setStructure(JSONObject structure) {
        this.structure = structure;
    }
}
