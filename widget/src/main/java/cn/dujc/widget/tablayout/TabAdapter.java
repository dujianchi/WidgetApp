package cn.dujc.widget.tablayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.dujc.widget.listener.OnDuItemClickListener;

public class TabAdapter<T> extends RecyclerView.Adapter<TabAdapter.TabHolder<T>> {

    private final SparseIntArray mItemWidths = new SparseIntArray();
    private RecyclerView mRecyclerView;

    private List<T> mList;
    @NonNull
    private ITabFactory<T> mTabFactory;
    private OnDuItemClickListener mOnDuItemClickListener;
    private int mCurrentPosition = 0;

    public TabAdapter(List<T> list, @NonNull ITabFactory<T> tabFactory) {
        mList = list;
        mTabFactory = tabFactory;
    }

    @NonNull
    @Override
    public TabHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TabHolder<T>(mTabFactory.create(), parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final TabHolder<T> holder, int position) {
        holder.mTab.onTabUpdate(position, getItemData(position));
        if (mCurrentPosition == position) holder.mTab.onTabSelected();
        else holder.mTab.onTabUnselected();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDuItemClickListener != null)
                    mOnDuItemClickListener.onDuItemClickListener(holder.itemView, holder.getAdapterPosition());
            }
        });
        expandWidth(holder);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    private void expandWidth(TabHolder<T> holder) {
        mItemWidths.put(holder.getAdapterPosition(), holder.itemView.getMeasuredWidth());
        final int itemCount = getItemCount();
        if (mRecyclerView != null
                && holder.getAdapterPosition() == itemCount - 1 && mItemWidths.size() == itemCount) {
            int all = 0;
            for (int index = 0; index < itemCount; index++) {
                all += mItemWidths.get(index);
            }
            if (mRecyclerView.getWidth() > all && !mRecyclerView.isComputingLayout()) notifyDataSetChanged();
        }
    }

    public T getItemData(int position) {
        if (position < 0 || position >= getItemCount()) return null;
        return mList.get(position);
    }

    public void updateCurrentPosition(int currentPosition) {
        if (mCurrentPosition != currentPosition) {
            int last = mCurrentPosition;
            notifyItemChanged(mCurrentPosition = currentPosition);
            notifyItemChanged(last);
        }
    }

    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        mList = list;
    }

    public ITabFactory<T> getTabFactory() {
        return mTabFactory;
    }

    public void setTabFactory(@NonNull ITabFactory<T> tabFactory) {
        mTabFactory = tabFactory;
    }

    public OnDuItemClickListener getOnDuItemClickListener() {
        return mOnDuItemClickListener;
    }

    public void setOnDuItemClickListener(OnDuItemClickListener onDuItemClickListener) {
        mOnDuItemClickListener = onDuItemClickListener;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }

    public static class TabHolder<T> extends RecyclerView.ViewHolder {

        private final ITab<T> mTab;

        public TabHolder(@NonNull ITab<T> tab, @NonNull ViewGroup parent, int viewType) {
            super(tab.getView(parent, viewType));
            mTab = tab;
        }
    }
}
