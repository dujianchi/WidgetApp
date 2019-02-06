package cn.dujc.widgetapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import cn.dujc.widget.wheelpicker.IWheelPicker;
import cn.dujc.widget.wheelpicker.WheelPicker;
import cn.dujc.widgetapp.address.AddressSQLHelper;
import cn.dujc.widgetapp.address.IAddress;
import cn.dujc.widgetapp.address.OnParseDone;
import cn.dujc.widgetapp.address.Parser;

/**
 * @author du
 * date: 2019/2/6 1:14 PM
 */
public class WheelPickerActivity extends AppCompatActivity {

    private WheelPicker mWpProvince;
    private WheelPicker mWpCity;
    private WheelPicker mWpDistrict;
    private AddressSQLHelper mSQLHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_picker);
        mWpProvince = (WheelPicker) findViewById(R.id.wp_province);
        mWpCity = (WheelPicker) findViewById(R.id.wp_city);
        mWpDistrict = (WheelPicker) findViewById(R.id.wp_district);

        mWpProvince.setOnItemSelectedListener(new IWheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                IAddress province = (IAddress) data;
                List<IAddress> cities = mSQLHelper.getCities(province.getId());
                mWpCity.setData(cities);
                mWpCity.setSelectedItemPosition(0, false);
                if (cities != null && cities.size() > 0) {
                    IAddress city = cities.get(0);
                    mWpDistrict.setData(mSQLHelper.getDistricts(city.getId()));
                    mWpDistrict.setSelectedItemPosition(0, false);
                } else {
                    mWpDistrict.setData(Collections.emptyList());
                    mWpDistrict.setSelectedItemPosition(0, false);
                }
            }
        });
        mWpCity.setOnItemSelectedListener(new IWheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                IAddress city = (IAddress) data;
                mWpDistrict.setData(mSQLHelper.getDistricts(city.getId()));
                mWpDistrict.setSelectedItemPosition(0, false);
            }
        });
        try {
            Parser.get().syncXml(this
                    , "1.0.0"
                    , getResources().getAssets().open("area.xml")
                    , new OnParseDone() {
                        @Override
                        public void onParseDone(AddressSQLHelper helper) {
                            mSQLHelper = helper;
                            mWpProvince.setData(helper.getProvinces());
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
