package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

/**
 * Created by dell on 2016/1/13.
 */
public class XsyUtils {

    public static final int defalut_page_limit = 20;//默认分页查询查询数量


    public static boolean isEmojiCharacter(char codePoint) {
        return !(
                (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }
    public static boolean containEmojiCharacter(String text){
        for(int i=0;i<text.length();i++){
            if(isEmojiCharacter(text.charAt(i))){
                return true;
            }
        }
        return false;
    }
//    去除非法字符
    public static String beautifyString(String text){
        if(text == null){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<text.length();i++){
            if(!isEmojiCharacter(text.charAt(i))){
                stringBuffer.append(text.charAt(i));
            }
        }
        return stringBuffer.toString();
    }
}
