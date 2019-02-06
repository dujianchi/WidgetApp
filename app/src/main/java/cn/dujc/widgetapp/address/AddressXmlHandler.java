package cn.dujc.widgetapp.address;

import android.content.Context;
import android.support.annotation.NonNull;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class AddressXmlHandler extends DefaultHandler {

    private static final String PROVINCE = "province", CITY = "city", DISTRICT = "district";

    private final AddressSQLHelper mSQLHelper;
    private final String mVersion;
    private OnParseDone mOnParseDone;
    private List<IAddress> mProvinces = new ArrayList<>(), mCities = new ArrayList<>(), mDistricts = new ArrayList<>();
    private int mProvinceId = 1, mCityId = 100, mDistrictId = 10000;

    public AddressXmlHandler(Context context, @NonNull String version, OnParseDone onParseDone) {
        mOnParseDone = onParseDone;
        mVersion = version;
        mSQLHelper = new AddressSQLHelper(context);
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
        mSQLHelper.updateVersion(mVersion);
        mSQLHelper.resetData(mProvinces, mCities, mDistricts, mOnParseDone);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //Log.i("-----", "endElement" + "   " + Arrays.toString(ch) + "   " + start + "   " + length);
    }

}
