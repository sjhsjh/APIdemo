package com.example.apidemo.utils;

import android.util.Log;
import android.util.Xml;
import com.example.apidemo.data.Student;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class XmlUtils {

    /**
     * Pull解析器的运行方式与 SAX 解析器相似。
     * Android官方推荐。
     * 读取到xml的声明返回 START_DOCUMENT  0
     * 读取到xml的开始标签返回 START_TAG  2
     * 读取到xml的文本返回 TEXT          4
     * 读取到xml的结束标签返回 END_TAG  3
     * 读取到xml的结束返回 END_DOCUMENT  1
     * parser.getLineNumber()
     * parser.getColumnNumber()
     */
    public List<Student> pull2xml(InputStream is) throws Exception {
        List<Student> list = null;
        Student student = null;
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");

        //读取xml标签的类型
        int type = parser.getEventType();
        // 顺序读取 XML 标签
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:
                    if ("students".equals(parser.getName())) {
                        list = new ArrayList<>();
                    } else if ("student".equals(parser.getName())) {
                        student = new Student();
                    } else if ("name".equals(parser.getName())) {
                        //获取sex属性
                        String sex = parser.getAttributeValue(null, "sex");
                        student.setSex(sex);
                        //获取name值, 执行parser.nextText()读取该标签内容后，parser指针会到达END_TAG的尾部，因此就不再触发当前标签的END_TAG了！！！
                        String name = parser.nextText();    // 获取当前标签的内容！！，这是在START_TAG内读取的标签内容，而SAX解析是在endElement读取的。
                        student.setName(name);
                    } else if ("nickname".equals(parser.getName())) {
                        //获取nickName值
                        String nickName = parser.nextText();
                        student.setNickName(nickName);
                    }
                    break;
                // 与SAX的characters一样，读取任意两个xml之间的内容，包括不匹配的xml标签，包括换行、空格
                case XmlPullParser.TEXT:
                    Log.d("sjh3", "getLineNumber() = " + parser.getLineNumber() + " text length = " + parser.getText().length() + "  getText() = " + parser.getText());
                    break;
                //结束标签
                case XmlPullParser.END_TAG:
                    if ("student".equals(parser.getName())) {
                        list.add(student);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        return list;
    }


    /**
     * SAX是一个解析速度快并且占用内存少的xml解析器，SAX解析XML文件采用的是事件驱动，它并不需要解析完整个文档，而是按内容顺序解析文档的过程
     */
    public List<Student> sax2xml(InputStream is) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        //初始化Sax解析器
        SAXParser sp = spf.newSAXParser();
        //新建解析处理器
        MyHandler handler = new MyHandler();
        //将解析交给处理器
        sp.parse(is, handler);
        //返回List
        return handler.getList();
    }

    public class MyHandler extends DefaultHandler {
        private List<Student> list;
        private Student student;
        //用于存储读取的所有xml标签之间的内容，包括“\n”，“   ”.
        private String dataBetweenElement;

        /**
         * 解析到文档开始调用，一般做初始化操作
         *
         * @throws SAXException
         */
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            list = new ArrayList<>();
        }

        /**
         * 解析到文档末尾调用，一般做回收操作
         *
         * @throws SAXException
         */
        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        /**
         * 开始解析某个结点时调用, 每读到一个元素就调用该方法
         *
         * @param uri
         * @param localName
         * @param qName
         * @param attributes
         * @throws SAXException
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("student".equals(qName)) {
                //读到student标签
                student = new Student();
            } else if ("name".equals(qName)) {
                //获取name里面的属性
                String sex = attributes.getValue("sex");    // 读取当前前缀结点标签的值！！
                student.setSex(sex);
            }
            super.startElement(uri, localName, qName, attributes);
        }

        /**
         * 读取任意两个xml之间的内容，包括不匹配的xml标签，包括换行、空格：“\n”，“   ”.
         *
         * @param ch
         * @param start
         * @param length
         * @throws SAXException
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            dataBetweenElement = new String(ch, start, length);
            super.characters(ch, start, length);
        }

        /**
         * 读到元素的结尾调用
         *
         * @param uri
         * @param localName
         * @param qName
         * @throws SAXException
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("student".equals(qName)) {
                list.add(student);
            }
            if ("name".equals(qName)) {
                student.setName(dataBetweenElement);    // endElement 上一个标签内容肯定是该标签的内容！！
            } else if ("nickname".equals(qName)) {
                student.setNickName(dataBetweenElement);
            }
            super.endElement(uri, localName, qName);
        }

        public List<Student> getList() {
            return list;
        }

    }


    /**
     * DOM解析XML文件时，会将XML文件的所有内容读取到内存中（内存的消耗比较大），然后允许您使用DOM API遍历XML树、检索所需的数据
     */
    public List<Student> dom2xml(InputStream is) throws Exception {
        //一系列的初始化
        List<Student> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        //获得Document对象
        Document document = documentBuilder.parse(is);

        //获得student的List
        NodeList studentList = document.getElementsByTagName("student");
        //遍历student标签
        for (int i = 0; i < studentList.getLength(); i++) {
            //获得student标签
            Node node_student = studentList.item(i);
            //获得student标签里面的标签
            NodeList childNodes = node_student.getChildNodes();
            Student student = new Student();
            //遍历student标签里面的标签
            for (int j = 0; j < childNodes.getLength(); j++) {
                //获得name和nickName标签
                Node childNode = childNodes.item(j);
                //判断是name还是nickName
                if ("name".equals(childNode.getNodeName())) {
                    String name = childNode.getTextContent();
                    student.setName(name);

                    //获取name的属性
                    NamedNodeMap namedNodeMap = childNode.getAttributes();
                    //获取sex属性，由于只有一个属性，所以取0
                    Node n = namedNodeMap.item(0);
                    student.setSex(n.getTextContent());
                } else if ("nickname".equals(childNode.getNodeName())) {
                    String nickName = childNode.getTextContent();
                    student.setNickName(nickName);
                }
            }
            list.add(student);
        }

        return list;
    }


}
