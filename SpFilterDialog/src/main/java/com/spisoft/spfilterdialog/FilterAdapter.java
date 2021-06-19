package com.spisoft.spfilterdialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SFDialog.OnUpdateTaskListener updateTaskListener;
    private Context mContext;
    private List<SFDialog.FilterItem> mList;
    private OnItemClickListener mItemClickListener;
    private String ToText = " > ";

    public FilterAdapter(Context context) {
        this.mContext = context;
    }

    public void updateList(List<SFDialog.FilterItem> list, SFDialog.OnUpdateTaskListener mUpdateTaskListener) {
        this.updateTaskListener = mUpdateTaskListener;
        this.mList = list;
        notifyDataSetChanged();
    }

    private class CellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView textTitle, textDescription;
        private SwitchMaterial switchCheck;
        private RangeSeekBar vRangeSlider;

        public CellViewHolder(View view) {
            super(view);

            textTitle = (TextView) view.findViewById(R.id.itemFilterTitle);
            textDescription = (TextView) view.findViewById(R.id.itemDescription);
            switchCheck = (SwitchMaterial) view.findViewById(R.id.switchCheck);
            vRangeSlider = view.findViewById(R.id.rangSlider);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(view, getLayoutPosition());
                return true;
            }
            return false;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        if (mList.get(viewType).getMode() == SFDialog.SFD_Mode.Switch)
            return new CellViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filter_switch, viewGroup, false));
        if (mList.get(viewType).getMode() == SFDialog.SFD_Mode.SeekBar)
            return new CellViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filter_seekbar, viewGroup, false));
        else
            return new CellViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filter, viewGroup, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CellViewHolder holder = (CellViewHolder) viewHolder;

        SFDialog.FilterItem tItem = mList.get(position);
        holder.textTitle.setText(tItem.getTitle());

        if (tItem.isSelected() != null && tItem.isSelected())
            holder.textTitle.setTypeface(holder.textTitle.getTypeface(), Typeface.BOLD);
        else
            holder.textTitle.setTypeface(holder.textTitle.getTypeface(), Typeface.NORMAL);

        switch (tItem.getMode()) {
            case Switch:
                holder.switchCheck.setChecked(tItem.isSelected() != null ? tItem.isSelected() : false);
                holder.switchCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        tItem.setSelect(b);
                        if (updateTaskListener != null)
                            updateTaskListener.onEvent(mList);
                    }
                });
                break;

            case SeekBar:

                int min = 0;
                int max = 0;

                List<Integer> listItemsInt = new ArrayList<Integer>();
                List<String> listItems = new ArrayList<String>();

                for (SFDialog.FilterItemOption filterItemOption : tItem.getItems()) {
                    min = (min == 0 ? (int) filterItemOption.getValue() : Math.min((int) filterItemOption.getValue(), min));
                    max = Math.max((int) filterItemOption.getValue(), max);
//                    listItems.add(filterItemOption.getValue().toString());
                    listItemsInt.add((Integer) filterItemOption.getValue());
                }
                listItems.add(String.valueOf(min));
                listItems.add(String.valueOf(max));

                Set<Integer> hashSet = new LinkedHashSet(listItemsInt);
                ArrayList<Integer> removedDuplicates = new ArrayList(hashSet);

                Collections.sort(removedDuplicates);
                Integer LastVal = null;
                Integer minRange = max - min;
                for (Integer mVal : removedDuplicates) {
                    if (LastVal == null)
                        LastVal = mVal;
                    else {
                        if (minRange > mVal - LastVal)
                            minRange = mVal - LastVal;
                    }
                }

                holder.vRangeSlider.setRange(min, max);
                holder.vRangeSlider.setProgress(min, max);

                holder.vRangeSlider.setSteps((max - min) / minRange);
                final CharSequence[] charSequenceItems = listItems.toArray(new CharSequence[listItems.size()]);
                holder.vRangeSlider.setTickMarkTextArray(charSequenceItems);

                setProgressValue(holder, tItem);

                holder.vRangeSlider.setOnRangeChangedListener(new OnRangeChangedListener() {
                    @Override
                    public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                        holder.textDescription.setText(leftValue != rightValue ? (int) leftValue + ToText + (int) rightValue : "" + (int) leftValue);
                        for (SFDialog.FilterItemOption filterItemOption : tItem.getItems()) {
                            if (filterItemOption.getValue() != null && (int) filterItemOption.getValue() >= leftValue && (int) filterItemOption.getValue() <= rightValue)
                                filterItemOption.setSel(true);
                            else
                                filterItemOption.setSel(false);
                        }

                        if (isFromUser) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressValue(holder, tItem);
                                }
                            }, 500);
                        } else {
                            if (updateTaskListener != null)
                                updateTaskListener.onEvent(mList);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

                    }

                    @Override
                    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

                    }
                });
                break;

            default:
                if (tItem.getItems() != null) {
                    switch (tItem.getType()) {
                        case String:
                            StringBuilder stringBuilder = new StringBuilder();
                            for (SFDialog.FilterItemOption filterItemOption : tItem.getItems()) {
                                if (filterItemOption.isSel()) {
                                    tItem.setSelect(true);
                                    if (stringBuilder.length() != 0) stringBuilder.append(", ");
                                    stringBuilder.append(filterItemOption.getValue());
                                }
                            }
                            holder.textDescription.setText(stringBuilder);
                            break;
                        case Boolean:
                            holder.textDescription.setText("");
                            break;
                        case Numeric:
                            double minValue = -1;
                            double maxValue = -1;
                            for (SFDialog.FilterItemOption filterItemOption : tItem.getItems()) {
                                if (filterItemOption.isSel()) {
                                    if (minValue < 0 || (double) filterItemOption.getValue() < minValue) {
                                        minValue = (double) filterItemOption.getValue();
                                    }
                                    if (maxValue < 0 || (double) filterItemOption.getValue() > maxValue) {
                                        maxValue = (double) filterItemOption.getValue();
                                    }
                                }
                            }
                            holder.textDescription.setText(minValue + ToText + maxValue);
                            break;
                    }
                }
                break;
        }

    }

    private void setProgressValue(CellViewHolder holder, SFDialog.FilterItem tItem) {
        Integer minProgress = null;
        Integer maxProgress = null;

        for (SFDialog.FilterItemOption filterItemOption : tItem.getItems()) {
            if (filterItemOption.isSel() != null && filterItemOption.isSel()) {
                if (minProgress == null || minProgress > (int) filterItemOption.getValue())
                    minProgress = (int) filterItemOption.getValue();
                if (maxProgress == null || maxProgress < (int) filterItemOption.getValue())
                    maxProgress = (int) filterItemOption.getValue();
            }
        }
        if (minProgress != null) {
            tItem.setSelect(true);
//            if(holder.vRangeSlider.getMinProgress() != maxProgress || holder.vRangeSlider.getMaxProgress() != maxProgress)
//                Toast.makeText(mContext, "تعییر ", Toast.LENGTH_SHORT).show();
            holder.vRangeSlider.setProgress(minProgress, maxProgress);
            holder.textDescription.setText(!minProgress.equals(maxProgress) ? (minProgress + ToText + maxProgress) : "" + minProgress);
        } else
            tItem.setSelect(false);

        if (updateTaskListener != null)
            updateTaskListener.onEvent(mList);
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}