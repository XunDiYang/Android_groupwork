package com.yff.myapplication.message;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Pull解析Xml
 */
public class PullParser {

    /**
     * @param is inputStream
     * @return
     * @throws Exception
     */
    public static List<Message> pull2xml(InputStream is) throws Exception {
        List<Message> list = null;
        Message msg = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");
        int type = parser.getEventType();//一行一行判断 直到最后一行
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始类标签
                case XmlPullParser.START_TAG:
                    if ("messages".equals(parser.getName())) {
                        list = new ArrayList<>();//新Array
                    } else if ("message".equals(parser.getName())) {
                        msg = new Message();
                    } else if ("title".equals(parser.getName())) {
                        //获取title
                        String isOfficial = parser.getAttributeValue(null, "isOfficial");
                        msg.setOfficial("true".equals(isOfficial));
                        String title = parser.nextText();
                        msg.setTitle(title);
                    } else if ("time".equals(parser.getName())) {
                        //获取time
                        String time = parser.nextText();
                        msg.setTime(time);
                    } else if ("hashtag".equals(parser.getName())) {
                        //获取hashTag
                        String hashTag = parser.nextText();
                        msg.setDescription(hashTag);
                    } else if ("icon".equals(parser.getName())) {
                        //获取icon
                        String icon = parser.nextText();
                        msg.setIcon(icon);
                    }
                    break;
                //结束标签 把这个信息加进去list
                case XmlPullParser.END_TAG:
                    if ("message".equals(parser.getName())) {
                        list.add(msg);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        return list;
    }
}
