package dev.kb.mgd.newslot.gameslot;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class LayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public LayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {

        isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {

        return isScrollEnabled && super.canScrollVertically();
    }
}
