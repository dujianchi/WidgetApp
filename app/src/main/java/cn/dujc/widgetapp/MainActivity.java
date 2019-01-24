package cn.dujc.widgetapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;

import cn.dujc.widget.tablayout.TabLayout;

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
}
