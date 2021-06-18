package com.spisoft.spfilterdialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SFDialog extends RelativeLayout {
    private View rootView;
    private RecyclerView vListFilter;
    private FilterAdapter mFilterAdapter;
    private LinearLayoutManager layoutManager;
    private List<FilterItem> myFilterItems = new ArrayList<>();
    private OptionAdapter mOptionAdapter;
    private int _LastPosition = -1;
    private View vFilterSet, vFilterClear;
    private TextView vFilterSetTxt, vFilterClearTxt, vFilterProductCount;
    private OnUpdateTaskListener mUpdateTaskListener;
    private OnCompleteListener mCompleteListener;
    private SpinKitView vSpinKitView;
    private Context context;
    private View vFooter;
    private String mTextFilter = "";

    public enum SFD_Mode {
        CheckList(0),
        OptionList(1),
        Switch(2),
        SeekBar(3);
        private final int code;

        SFD_Mode(int code) {
            this.code = code;
        }

        public int Code() {
            return this.code;
        }
    }

    public enum SFD_Type {
        String(0),
        Boolean(1),
        Numeric(2);
        private final int code;

        SFD_Type(int code) {
            this.code = code;
        }

        public int Code() {
            return this.code;
        }
    }

    public enum SFD_Status {
        Stable(0),
        Process(1);
        private final int code;

        SFD_Status(int code) {
            this.code = code;
        }

        public int Code() {
            return this.code;
        }
    }

    public SFDialog(Context context) {
        super(context);
        initView(context, null, -1, -1);
    }

    public SFDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, -1, -1);
    }

    public SFDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SFDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleAttr1) {

        rootView = inflate(context, R.layout.sps_filter_dialog, this);
        vListFilter = rootView.findViewById(R.id.listFilter);
        vFilterSet = rootView.findViewById(R.id.bFilterSet);
        vFilterSetTxt = rootView.findViewById(R.id.filterSet);
        vFilterClear = rootView.findViewById(R.id.bFilterClear);
        vFilterClearTxt = rootView.findViewById(R.id.filterCancel);
        vFilterProductCount = rootView.findViewById(R.id.filterProductCount);
        vSpinKitView = rootView.findViewById(R.id.spin_kit);
        vFooter = rootView.findViewById(R.id.footer);

        vFilterSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_LastPosition < 0) {
//                    _LastPosition = -1;
//                    mFilterAdapter = new FilterAdapter(context);
//                    vListFilter.setAdapter(mFilterAdapter);
//                    mFilterAdapter.updateList(myFilterItems, mUpdateTaskListener);

                    if (mUpdateTaskListener != null)
                        mUpdateTaskListener.onEvent(myFilterItems);

                    if (mCompleteListener != null)
                        mCompleteListener.onEvent();
                } else {
                    vFilterSetTxt.setText(mTextFilter);
                    SetListToMain(context);
                }
            }
        });

        vFilterSet.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mCompleteListener != null)
                    mCompleteListener.onEvent();
                return true;
            }
        });

//        vFooter.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mCompleteListener != null)
//                    mCompleteListener.onEvent();
//
//                vFilterSetTxt.setText(mTextFilter);
//                SetListToMain(context);
//            }
//        });

        vFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUpdateTaskListener != null)
                    mUpdateTaskListener.onEvent(myFilterItems);

                if (mCompleteListener != null)
                    mCompleteListener.onEvent();
            }
        });

        vFilterClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_LastPosition < 0) {
                    for (FilterItem filterItem : myFilterItems) {
                        switch (filterItem.getMode()) {
                            case Switch:
                                filterItem.setSel(false);
                                break;
                            case SeekBar:
                                break;
                            default:
                                for (FilterItemOption filterItemOption : filterItem.getItems()) {
                                    filterItemOption.setSel(false);
                                }
                                break;
                        }
                    }
                    mFilterAdapter.notifyDataSetChanged();
                } else {
                    for (FilterItemOption filterItemOption : myFilterItems.get(_LastPosition).getItems()) {
                        filterItemOption.setSel(false);
                    }
                    mOptionAdapter.notifyDataSetChanged();
                }
            }
        });

        vListFilter.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        vListFilter.setLayoutManager(layoutManager);

        SetListToMain(context);

        if (attrs != null) {
            @SuppressLint("Recycle") final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SFDialog, 0, 0);

            vSpinKitView.setColor(typedArray.getColor(R.styleable.SFDialog_KitColor, Color.GRAY));
            mTextFilter = typedArray.getString(R.styleable.SFDialog_TextFilter);
            if (mTextFilter != null) vFilterSetTxt.setText(mTextFilter);
            String mTextFilterClear = typedArray.getString(R.styleable.SFDialog_TextFilterClear);
            if (mTextFilterClear != null) vFilterClearTxt.setText(mTextFilterClear);
            vFooter.setBackgroundColor(typedArray.getColor(R.styleable.SFDialog_FooterColor, Color.GRAY));
            vFilterProductCount.setTextColor(typedArray.getColor(R.styleable.SFDialog_FooterTextColor, Color.WHITE));

            typedArray.recycle();
            invalidate();
        }
    }

    private void SetListToSub(Context context, FilterItem mFilterItem, int position) {
        _LastPosition = position;
        mOptionAdapter = new OptionAdapter(context);
        vListFilter.setAdapter(mOptionAdapter);
        mOptionAdapter.updateList(mFilterItem.Items, mFilterItem.getMode(), position);
        vFilterSetTxt.setText(mFilterItem.getTitle());

        mOptionAdapter.SetOnItemClickListener(new OptionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int parent) {
                OptionClick(parent, position);
            }

            @Override
            public void onItemLongClick(View view, int position, int parent) {

            }
        });
    }

    private void OptionClick(int parent, int position) {
        FilterItem mItem = myFilterItems.get(parent);
        switch (mItem.getMode()) {
            case CheckList:
                mItem.getItems().get(position).setSel(!mItem.getItems().get(position).isSel());
                mOptionAdapter.notifyDataSetChanged();
                break;
            case OptionList:
                for (int i = 0; i < mItem.getItems().size(); i++) {
                    if (i == position)
                        mItem.getItems().get(i).setSel(true);
                    else
                        mItem.getItems().get(i).setSel(false);
                }
                mOptionAdapter.notifyDataSetChanged();
                break;
        }

    }

    private void SetListToMain(Context context) {
        _LastPosition = -1;
        mFilterAdapter = new FilterAdapter(context);
        vListFilter.setAdapter(mFilterAdapter);
        mFilterAdapter.updateList(myFilterItems, mUpdateTaskListener);

        mFilterAdapter.SetOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FilterItem mFilterItem = myFilterItems.get(position);
                switch (mFilterItem.getMode()) {
                    case CheckList:
                        SetListToSub(context, mFilterItem, position);
                        break;
                    case OptionList:
                        SetListToSub(context, mFilterItem, position);
                        break;
                    case SeekBar:
                        break;
                    case Switch:
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        if (mUpdateTaskListener != null)
            mUpdateTaskListener.onEvent(myFilterItems);
    }

    public SFDialog SetItems(List<FilterItem> filterItems) {
        SetStatus(SFD_Status.Process, null);
        myFilterItems = filterItems;
        mFilterAdapter.updateList(myFilterItems, mUpdateTaskListener);
        SetStatus(SFDialog.SFD_Status.Stable, "" + myFilterItems.size());
        return this;
    }

    public static class FilterItem {
        private String Id;
        private String Field;
        private String Title;
        private Boolean Sel;
        private SFD_Mode Mode; //todo: 0:list 1:bool 2:seekBar
        private SFD_Type Type; //todo: 0:string 1:bool 2:numeric
        private List<FilterItemOption> Items;

        public FilterItem() {
        }

        public FilterItem(String id, String field, String title, SFD_Mode mode, Boolean sel, SFD_Type type, List<FilterItemOption> items) {
            this.Id = id;
            this.Field = field;
            this.Title = title;
            this.Sel = sel;
            this.Mode = mode;
            this.Type = type;
            this.Items = items;
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getField() {
            return Field;
        }

        public void setField(String field) {
            Field = field;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public Boolean Selected() {
            return Sel;
        }

        public void setSel(Boolean sel) {
            Sel = sel;
        }

        public SFD_Mode getMode() {
            return Mode;
        }

        public void setMode(SFD_Mode mode) {
            Mode = mode;
        }

        public SFD_Type getType() {
            return Type;
        }

        public void setType(SFD_Type type) {
            Type = type;
        }

        public List<FilterItemOption> getItems() {
            return Items;
        }

        public void setItems(List<FilterItemOption> items) {
            Items = items;
        }
    }

    public static class FilterItemOption {
        private String Id;
        private Object Value;
        private Boolean Sel;

        public FilterItemOption(){

        }

        public FilterItemOption(String id, Object value, boolean sel) {
            this.Id = id;
            this.Value = value;
            this.Sel = sel;
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public Object getValue() {
            return Value;
        }

        public void setValue(Object value) {
            Value = value;
        }

        public Boolean isSel() {
            return Sel;
        }

        public void setSel(Boolean sel) {
            Sel = sel;
        }
    }

    public void SetStatus(SFD_Status status, String mText) {
        switch (status) {
            case Stable:
                vSpinKitView.setVisibility(GONE);
                vFilterProductCount.setVisibility(VISIBLE);
                vFilterProductCount.setText(mText);
                break;
            case Process:
                vSpinKitView.setVisibility(VISIBLE);
                vFilterProductCount.setVisibility(GONE);
                break;
        }
    }

    public interface OnUpdateTaskListener {
        void onEvent(List<FilterItem> filterItems);
    }

    public void setOnUpdateTaskListener(OnUpdateTaskListener eventListener) {
        mUpdateTaskListener = eventListener;
        mFilterAdapter.updateList(myFilterItems, mUpdateTaskListener);
    }

    public interface OnCompleteListener {
        void onEvent();
    }

    public void setOnCompleteListener(OnCompleteListener eventListener) {
        mCompleteListener = eventListener;
    }
}
