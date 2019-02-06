package cn.dujc.widgetapp.address;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class AddressXmlHandler extends DefaultHandler {

    private static final String PROVINCE = "province", CITY = "city", DISTRICT = "district";

    private final AddressSQLHelper mSQLHelper;
    private OnParseDone mOnParseDone;
    private List<IAddress> mProvinces = new ArrayList<>(), mCities = new ArrayList<>(), mDistricts = new ArrayList<>();
    private int mProvinceId = 1, mCityId = 100, mDistrictId = 10000;

    public AddressXmlHandler(Context context, String versionStr, @NonNull OnParseDone onParseDone) {
        mOnParseDone = onParseDone;
        int version = version2int(versionStr);
        mSQLHelper = new AddressSQLHelper(context, version);
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

    @Override
    public void startDocument() throws SAXException {
        //Log.i("-----", "startDocument");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //Log.i("-----", "startElement" + "   " + uri + "   " + localName + "   " + qName + "   " + attributes);
        ////Log.e("=====", "count = " + attributes.getLength() + "  " + qName + " = " + attributes.getValue(0));
        if (PROVINCE.equals(qName)) {
            mProvinces.add(new IAddress.Impl(mProvinceId + "", attributes.getValue(0), null));
        } else if (CITY.equals(qName)) {
            mCities.add(new IAddress.Impl(mCityId + "", attributes.getValue(0), mProvinceId + ""));
        } else if (DISTRICT.equals(qName)) {
            mDistricts.add(new IAddress.Impl(mDistrictId + "", attributes.getValue(0), mCityId + ""));
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //Log.i("-----", "endElement" + "   " + uri + "   " + localName + "   " + qName);
        if (PROVINCE.equals(qName)) {
            mProvinceId++;
        } else if (CITY.equals(qName)) {
            mCityId++;
        } else if (DISTRICT.equals(qName)) {
            mDistrictId++;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        mSQLHelper.resetData(mProvinces, mCities, mDistricts, mOnParseDone);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //Log.i("-----", "endElement" + "   " + Arrays.toString(ch) + "   " + start + "   " + length);
    }

}
