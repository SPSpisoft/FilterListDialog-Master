package com.spisoft.filterlistdialog_master;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.spisoft.spfilterdialog.SFDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SFDialog spDialog = findViewById(R.id.sdialg);
        List<SFDialog.FilterItem> mList = new ArrayList<>();

        SFDialog.FilterItem mItem1 = new SFDialog.FilterItem();
        mItem1.setId("Item_1");
        mItem1.setTitle("Item 1");
//        List<Map<String, Boolean>> mListOptions = new ArrayList<>();
//        Map<String, Boolean> mOption1 = new HashMap<>();
//        mOption1.put("Opt1", false);
//        mOption1.put("Opt2", true);
//        mOption1.put("Opt3", false);
//        mOption1.put("Opt4", true);
//        mListOptions.add(mOption1);

        List<SFDialog.FilterItemOption> mListOptions = new ArrayList<>();
        SFDialog.FilterItemOption mOption1 = new SFDialog.FilterItemOption();
        mOption1.setId("Opt1");
        mOption1.setSel(true);
        mOption1.setValue("Option_1");
        mListOptions.add(mOption1);

        SFDialog.FilterItemOption mOption2 = new SFDialog.FilterItemOption();
        mOption2.setId("Opt2");
        mOption2.setSel(false);
        mOption2.setValue("Option_2");
        mListOptions.add(mOption2);

        SFDialog.FilterItemOption mOption3 = new SFDialog.FilterItemOption();
        mOption3.setId("Opt3");
        mOption3.setSel(true);
        mOption3.setValue("Option_3");
        mListOptions.add(mOption3);

        mItem1.setItems(mListOptions);
        mItem1.setType(SFDialog.SFD_Type.String);
        mItem1.setMode(SFDialog.SFD_Mode.OptionList);
        mList.add(mItem1);

        SFDialog.FilterItem mItem2 = new SFDialog.FilterItem();
        mItem2.setMode(SFDialog.SFD_Mode.Switch);
        mItem2.setType(SFDialog.SFD_Type.String);
        mItem2.setId("Item_2");
        mItem2.setTitle("Item 2");
        mList.add(mItem2);

        SFDialog.FilterItem mItem3 = new SFDialog.FilterItem();
        mItem3.setMode(SFDialog.SFD_Mode.SeekBar);
        mItem3.setId("Item_3");
        mItem3.setTitle("Item 3");
        mItem3.setPlusOption(false);
        mItem3.setType(SFDialog.SFD_Type.Numeric);

        List<SFDialog.FilterItemOption> mValues = new ArrayList<>();
        SFDialog.FilterItemOption filterItemOption1 = new SFDialog.FilterItemOption();
        filterItemOption1.setValue(2000000);
        mValues.add(filterItemOption1);
        SFDialog.FilterItemOption filterItemOption2 = new SFDialog.FilterItemOption();
        filterItemOption2.setValue(1000000);
        mValues.add(filterItemOption2);
        SFDialog.FilterItemOption filterItemOption3 = new SFDialog.FilterItemOption();
        filterItemOption3.setValue(5000000);
        mValues.add(filterItemOption3);
        SFDialog.FilterItemOption filterItemOption4 = new SFDialog.FilterItemOption();
        filterItemOption4.setValue(8000000);
        mValues.add(filterItemOption4);

        mItem3.setItems(mValues);
        mList.add(mItem3);

        List<SFDialog.FilterItemOption> I4_Items = new ArrayList<>();

        SFDialog.FilterItem mItem4 = new SFDialog.FilterItem("Item4", "SSS", "تست", SFDialog.SFD_Mode.CheckList, true,
                SFDialog.SFD_Type.Numeric, I4_Items);

        mList.add(mItem4);
        spDialog.SetItems(mList);

        spDialog.setOnUpdateTaskListener(new SFDialog.OnUpdateTaskListener() {
            @Override
            public void onEvent(List<SFDialog.FilterItem> filterItems) {
                spDialog.SetStatus(SFDialog.SFD_Status.Process, null);
                for (SFDialog.FilterItem filterItem : filterItems){

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spDialog.SetStatus(SFDialog.SFD_Status.Stable, "1234");
                    }
                }, 3000);
            }
        });

        spDialog.setOnCompleteListener(new SFDialog.OnCompleteListener() {
            @Override
            public void onEvent() {
                Toast.makeText(MainActivity.this, "COMPLETED", Toast.LENGTH_SHORT).show();
            }
        });
    }
}