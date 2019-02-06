package cn.dujc.widgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.InputStream;
import java.util.Arrays;

import cn.dujc.widget.tablayout.TabLayout;
import cn.dujc.widgetapp.address.AddressSQLHelper;
import cn.dujc.widgetapp.address.OnParseDone;
import cn.dujc.widgetapp.address.Parser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tablayout = findViewById(R.id.tablayout);
        tablayout.setData(Arrays.asList("aaa", "bbb", "ccc"
                //, "bbb", "ccc", "bbb", "ccc", "bbb", "ccc", "bbb", "ccc", "bbb", "ccc", "bbb", "ccc"
        ));

    }

    public void scanQR(View view) {
        startActivity(new Intent(this, ScanActivity.class));
    }

    public void wheelPicker(View view) {
        startActivity(new Intent(this, WheelPickerActivity.class));
    }

    public void address(View view) {
        try {
            InputStream open = getResources().getAssets().open("area.xml");
            Parser.get().syncXml(this, "1.0.0", open, new OnParseDone() {
                @Override
                public void onParseDone(AddressSQLHelper helper) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
