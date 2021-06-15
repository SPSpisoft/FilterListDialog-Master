package com.spisoft.spfilterdialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private List<SFDialog.FilterItemOption> mList;
    private OnItemClickListener mItemClickListener;
    private int mParent;
    private SFDialog.SFD_Mode mMode;

    public OptionAdapter(Context context) {
        this.mContext = context;
    }

//    public void updateList(List<SFDialog.FilterItemOption> list) {
//        this.mList = list;
//        notifyDataSetChanged();
//    }

    public void updateList(List<SFDialog.FilterItemOption> list, SFDialog.SFD_Mode mode, int parentPosition) {
        this.mList = list;
        this.mMode = mode;
        this.mParent = parentPosition;
        notifyDataSetChanged();
    }

    private class CellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView vIcon;
        private TextView vTextView;
        private MaterialCheckBox vCheckBox;
        private MaterialRadioButton vRadio;
        private InputMethodManager imm;

        public CellViewHolder(View view) {
            super(view);

            vTextView = (TextView) view.findViewById(R.id.titleOption);
            vIcon = (ImageView) view.findViewById(R.id.iconOption);
            vCheckBox = view.findViewById(R.id.checkbox);
            vRadio = view.findViewById(R.id.radio);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        }

        @Override
        public void onClick(View view) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition(), mParent);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(view, getLayoutPosition(), mParent);
                return true;
            }
            return false;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {

        switch (this.mMode)
        {
            case CheckList:
                return new CellViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_option_check, viewGroup, false));
            case OptionList:
                return new CellViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_option_radio, viewGroup, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CellViewHolder holder = (CellViewHolder) viewHolder;

        SFDialog.FilterItemOption tItem = mList.get(position);

        holder.vTextView.setText(tItem.getValue().toString());

        switch (mMode){
            case CheckList:
                holder.vCheckBox.setChecked(tItem.isSel());
                break;
            case OptionList:
                holder.vRadio.setChecked(tItem.isSel());
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, int parent);

        void onItemLongClick(View view, int position, int parent);
    }

    // for both short and long click
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}