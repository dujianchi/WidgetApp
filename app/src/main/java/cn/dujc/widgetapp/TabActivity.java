package cn.dujc.widgetapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import cn.dujc.widget.tablayout.TabFactory;
import cn.dujc.widget.tablayout.TabLayout;

public class TabActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        TabLayout tabLayout = findViewById(R.id.widget_tab_layout);
        List tabs = Arrays.asList(new TabFactory.IndexTabImpl("123", R.mipmap.ic_launcher)
                ,new TabFactory.IndexTabImpl("456", R.mipmap.ic_launcher)
                ,new TabFactory.IndexTabImpl("789", R.mipmap.ic_launcher));
        tabLayout.setInstaller(new TabFactory.IndexImpl());
        tabLayout.setData(tabs);
    }
}
