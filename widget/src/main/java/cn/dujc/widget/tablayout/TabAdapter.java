package cn.dujc.widget.tablayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabHolder> {

    @NonNull
    @Override
    public TabHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TabHolder tabHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class TabHolder extends RecyclerView.ViewHolder {
        public TabHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
