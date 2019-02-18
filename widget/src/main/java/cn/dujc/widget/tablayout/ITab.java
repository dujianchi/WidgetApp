package cn.dujc.widget.tablayout;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public interface ITab<T> {

    public ITab<T> create();

    @NonNull
    public View getView(@NonNull ViewGroup parent);

    public void onTabUpdate(int position, T data);
    public void onTabSelected(int position);
    public void onTabUnselected(int position);

}
