package cn.dujc.widget.tablayout;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.dujc.widget.R;

public class DefaultTabFactoryImpl<T> implements ITabFactory<T> {

    @Override
    public ITab<T> create() {
        ITab<T> tab = new TabImpl<T>();
        return tab;
    }

    public static class TabImpl<T> implements ITab<T> {

        protected View mItemView;
        protected TextView mItemTextView;

        @NonNull
        @Override
        public View getView(@NonNull ViewGroup parent, int viewType) {
            if (mItemView == null) {
                mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dujc_widget_default_tab, parent, false);
                mItemTextView = mItemView.findViewById(R.id.dujc_widget_tab_text);
            }
            return mItemView;
        }

        @Override
        public void onTabUpdate(int position, T data) {
            if (mItemTextView != null) {
                mItemTextView.setText(String.valueOf(data));
            }
        }

        @Override
        public void onTabSelected() {
            if (mItemView != null) {
                mItemView.setSelected(true);
            }
        }

        @Override
        public void onTabUnselected() {
            if (mItemView != null) {
                mItemView.setSelected(false);
            }
        }
    }
}
