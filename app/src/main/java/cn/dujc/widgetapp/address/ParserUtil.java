package cn.dujc.widgetapp.address;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author du
 * date: 2019/2/6 7:41 PM
 */
public class ParserUtil {

    private ParserUtil() {}

    public void update(Context context, String url, String newVersion, @Nullable OnParseDone onParseDone) {
        String oldVersion = new AddressSQLHelper(context).getVersionName();
        if (TextUtils.isEmpty(oldVersion)) {
            //当数据库中的版本号为空，说明没有数据，则先从本地xml读取一份
            try {
                InputStream open = context.getResources().getAssets().open("area.xml");
                String version = getAssetsVersion(context);
                syncXml(context, open, version, onParseDone);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (oldVersion.equals(newVersion) || TextUtils.isEmpty(newVersion) || TextUtils.isEmpty(url)) {
            //如果旧版本号不为空，且与新版本号一致或新版本号为空，或者url，则无需操作
            if (onParseDone != null) onParseDone.onParseDone(true);
            return;
        }//当新旧版本号不一致，则下载xml，并将其替换进数据库
        final HttpURLConnection connection = createByUrl(url);
        if (connection != null) {
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            try {
                connection.setRequestMethod("GET");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                connection.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                final InputStream inputStream = connection.getInputStream();
                syncXml(context, inputStream, newVersion, onParseDone);
            } catch (Exception e) {
                e.printStackTrace();
                if (onParseDone != null) onParseDone.onParseDone(false);
            }
            connection.disconnect();
        }
    }

    private void syncXml(Context context, InputStream xml, String version, OnParseDone onParseDone) {
        //①创建XML解析处理器
        DefaultHandler handler = new AddressXmlHandler(context, version, onParseDone);
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
        } finally {
            try {
                xml.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final ParserUtil INSTANCE = new ParserUtil();

    public static ParserUtil get() {
        return INSTANCE;
    }

    public static int version2int(String versionStr) {
        if (!TextUtils.isEmpty(versionStr)) {
            int v = 0;
            String[] split = versionStr.split("\\.");
            final int length = split.length;
            double s = Math.pow(10, length - 1);
            for (int index = 0; index < length; index++) {
                int len = split[index].length();
                if (len > 1) s *= Math.pow(10, len - 1);
            }
            for (int index = 0; index < length && s >= 1; index++, s /= 10) {
                int i = 1;
                try {
                    i = Integer.valueOf(split[index]);
                    if (i >= 10) {
                        s /= Math.pow(10, split[index].length() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                v += s * i;
            }
            if (v > 0) return v;
        }
        return 1;
    }

    public static String getAssetsVersion(Context context) {
        try {
            InputStream open = context.getResources().getAssets().open("area.version");
            StringBuilder out = new StringBuilder();
            final byte[] buffer = new byte[512];
            int length;
            while ((length = open.read(buffer)) != -1) {
                out.append(new String(buffer, 0, length));
            }
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }

    static HttpURLConnection createByUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            if (useSSL(urlStr)) {
                final SSLSocketFactory socketFactory = DefaultSSLSocketFactory.create().getSslSocketFactory();
                if (socketFactory != null) {
                    final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
                    httpsURLConnection.setSSLSocketFactory(socketFactory);
                    return httpsURLConnection;
                }
            } else {
                return (HttpURLConnection) connection;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static boolean useSSL(String url) {
        return url != null && url.startsWith("https://");
    }

    static class DefaultSSLSocketFactory {

        private final X509TrustManager trustManager;
        private SSLContext sslContext;
        private SSLSocketFactory sslSocketFactory;

        private DefaultSSLSocketFactory() {
            trustManager = new DefaultTrustManager();
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                sslSocketFactory = sslContext.getSocketFactory();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }

        public static DefaultSSLSocketFactory create() {
            return new DefaultSSLSocketFactory();
        }

        public X509TrustManager getTrustManager() {
            return trustManager;
        }

        public SSLContext getSslContext() {
            return sslContext;
        }

        public SSLSocketFactory getSslSocketFactory() {
            return sslSocketFactory;
        }
    }

    static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
