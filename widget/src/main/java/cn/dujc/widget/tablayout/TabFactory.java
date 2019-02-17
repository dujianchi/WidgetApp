package cn.dujc.widget.tablayout;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.dujc.widget.R;

/**
 * @author du
 * date: 2019/2/17 4:13 PM
 */
public final class TabFactory<T> {

    private final SparseArray<ITab> mCachedTabs = new SparseArray<>();

    private ITab<T> mInstaller;
    private int mCurrent = 0;

    public TabFactory() {
        mInstaller = new TabImpl<>();
    }

    public int getCurrent() {
        return mCurrent;
    }

    public void update(int index, boolean selected) {
        ITab iTab = mCachedTabs.get(index);
        if (iTab != null) {
            if (selected) iTab.onTabSelected();
            else iTab.onTabUnselected();
        }
        if (selected) mCurrent = index;
    }

    public void setTabClick(View view, final int index) {
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update(getCurrent(), false);
                    update(index, true);
                }
            });
        }
    }

    public void setInstaller(ITab<T> installer) {
        if (installer != null) mInstaller = installer;
    }

    public ITab<T> get(int index) {
        ITab iTab = mCachedTabs.get(index);
        if (iTab == null) {
            iTab = mInstaller.create();
            mCachedTabs.put(index, iTab);
        }
        return iTab;
    }

    public static class TabImpl<T> implements ITab<T> {

        protected View mItemView;
        protected TextView mItemTextView;

        @Override
        public ITab<T> create() {
            return new TabImpl<>();
        }

        @NonNull
        @Override
        public View getView(@NonNull ViewGroup parent) {
            if (mItemView == null) {
                mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_default_tab, parent, false);
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
