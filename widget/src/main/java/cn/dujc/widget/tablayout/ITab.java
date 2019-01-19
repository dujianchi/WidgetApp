package cn.dujc.widget.tablayout;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public interface ITab<T> {

    @NonNull
    public View getView(@NonNull ViewGroup parent, int viewType);

    public void onTabUpdate(int position, T data);
    public void onTabSelected();
    public void onTabUnselected();

}
