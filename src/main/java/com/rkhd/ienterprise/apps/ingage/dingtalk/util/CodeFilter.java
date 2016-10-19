package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeFilter
{
    private static final Logger LOG = LoggerFactory.getLogger(CodeFilter.class);
    public static String toHtml(String str)
    {
        if (str == null)
        {
            str = "";
            return str;
        }
        str = Replace(str.trim(), "&", "&amp;");
        str = Replace(str.trim(), "<", "&lt;");
        str = Replace(str.trim(), ">", "&gt;");
        str = Replace(str.trim(), "\t", "    ");
        str = Replace(str.trim(), "\r\n", "\n");
        str = Replace(str.trim(), "\n", "<br>");
        str = Replace(str.trim(), "  ", " &nbsp;");
        str = Replace(str.trim(), "'", "&#39;");
        str = Replace(str.trim(), "\\", "&#92;");
        return str;
    }

    public static String unHtml(String str)
    {
        str = Replace(str, "<br>", "\n");
        str = Replace(str, "</br>", "");
        str = Replace(str, "<p>", "");
        str = Replace(str, "</p>", "");
        str = Replace(str, "&nbsp;", "");
        str = Replace(str, "<strong>", "");
        str = Replace(str, "<div>", "");
        str = Replace(str, "</div>", "");
        str = Replace(str, "<span>", "");
        str = Replace(str, "</span>", "");
        str = Replace(str, "<", "");
        str = Replace(str, "/", "");
        str = Replace(str, ">", "");
        return str;
    }

    public static String htmlEncode(String str)
    {
        if (str == null)
        {
            str = "";
            return str;
        }
        str = str.trim();
        str = Replace(str, "\t", "    ");
        str = Replace(str, "\r\n", "\n");
        str = Replace(str, "\n", "<br>");
        str = Replace(str, "  ", " &nbsp;&nbsp;");
        str = Replace(str, "\"", "&#34;");
        return str;
    }

    public static String strEncode(String str)
    {
        if (str == null)
        {
            str = "";
            return str;
        }
        str = str.trim();
        str = Replace(str, "<", "&#60;");
        str = Replace(str, ">", "&#62;");
        str = Replace(str, "\t", "    ");
        str = Replace(str, "\r\n", "\n");
        str = Replace(str, "\n", "<br>");
        str = Replace(str, "  ", " &nbsp;&nbsp;");
        str = Replace(str, "'", "&#39;");
        str = Replace(str, "\\", "/");
        str = Replace(str, "\"", "&#34;");
        return str;
    }


    public static String Replace(String content, String beReplaceString, String replaceString)
    {
        StringBuffer stringBuffer = new StringBuffer();
        int i = content.length();
        int j = beReplaceString.length();
        int m;
//        for (int k = 0; (m = content.indexOf(beReplaceString, k)) >= 0; k = m + j)
//        {
//            stringBuffer.append(content.substring(k, m));
//            stringBuffer.append(replaceString);
//            if (k < i){
//                stringBuffer.append(content.substring(k));
//            }
//        }
        int k = 0 ;
        while (true){
            m = content.indexOf(beReplaceString, k);
            if( m >= 0){
                content = content.replaceAll(beReplaceString,replaceString);
            }else{
                break;
            }
        }

        return  content ;//stringBuffer.toString();
    }
    public  static <T> T xddTerminator(T entity){
        return entity;
//        if(entity == null){
//            return null;
//        }
//        String entityString = JSON.toJSONString(entity);
//
//        entityString = entityString.trim();
//        entityString = Replace(entityString, "<", "&#60;");
//        entityString = Replace(entityString, ">", "&#62;");
//
//        return (T) JSON.parseObject(entityString,entity.getClass());
    }

    public static void main(String[] paramArrayOfString)
    {

        String str1 = "<p>ddd</p> <br>nsss</br>";
        String str2 = unHtml(str1);
        System.out.println(str2);
    }
}
