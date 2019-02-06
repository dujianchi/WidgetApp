package cn.dujc.widgetapp.address;

import android.content.Context;
import android.support.annotation.NonNull;

import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author du
 * date: 2019/2/6 7:41 PM
 */
public class Parser {

    private Parser() {}

    private static final Parser INSTANCE = new Parser();

    public static Parser get() {
        return INSTANCE;
    }

    public void syncXml(Context context, String versionStr, InputStream xml, @NonNull OnParseDone onParseDone) {
        //①创建XML解析处理器
        DefaultHandler handler = new AddressXmlHandler(context, versionStr, onParseDone);
        //②得到SAX解析工厂
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            //③创建SAX解析器
            SAXParser parser = factory.newSAXParser();
            //④将xml解析处理器分配给解析器,对文档进行解析,将事件发送给处理器
            parser.parse(xml, handler);
            //handler.printList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
