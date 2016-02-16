package com.test.ct;

import java.util.ArrayList;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class Main extends ListActivity {

    ArrayList<TestItem> mList = new ArrayList<TestItem>();
    MyAdapter mAdapter = new MyAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mList.add(new TestItem(ViewTest.class, R.string.ac_doov_sky, R.layout.doov_sky));
        mList.add(new TestItem(ViewTest.class, R.string.ac_flicker, R.layout.flicker_view));
        mList.add(new TestItem(ViewTest.class, R.string.ac_grass_land, R.layout.grass_land));
//        mList.add(new TestItem(ViewTest.class, R.string.ac_aquarius, R.layout.aquarius));
        mList.add(new TestItem(AquariusTest.class,R.string.ac_aquarius,R.layout.aquarius));
        setListAdapter(mAdapter);

//        Log.d("cting","contentResolver:"+getContentResolver().getPackageName());
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // TODO Auto-generated method stub
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TestItem item = mAdapter.getItem(position);
        Intent intent = new Intent();
        intent.setClass(this, item.clz);
        intent.putExtra("titleId", item.titleId);
        intent.putExtra("layoutId", item.layoutId);
        startActivity(intent);
    }

    public static boolean processIntent(Activity act) {
        Intent intent = act.getIntent();
        if (intent != null && act != null) {
            int titleId = intent.getIntExtra("titleId", -1);
            int layoutId = intent.getIntExtra("layoutId", -1);
            if (titleId > 0) {
                act.setTitle(titleId);
            }
            if (layoutId > 0) {
                act.setContentView(layoutId);
                return true;
            }
        }
        return false;

    }

    class TestItem {
        Class clz;
        int titleId = 0;
        int layoutId = 0;

        //		View layoutView;
        public TestItem(Class<?> clz) {
            this.clz = clz;
        }

        public TestItem(Class<?> clz, int titleId, int layoutId) {
            this.clz = clz;
            this.titleId = titleId;
            this.layoutId = layoutId;
        }
//		public TestItem(Class<?>clz,View view){
//			this.clz=clz;
//			this.layoutView=view;
//		}
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return mList.size();
        }

        public TestItem getItem(int position) {
            return mList.get(position);
        }

        public Class<?> getClz(int position) {
            return getItem(position).clz;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.main_list_item, null);
            }
            TextView label = (TextView) convertView.findViewById(R.id.app_label);
//			Class clz=((TestItem) getItem(position)).clz;
//			if(clz!=null){
//				label.setText(clz.getSimpleName());
            TestItem item = getItem(position);
            String title = item.titleId > 0 ? getString(item.titleId) : item.clz.getSimpleName();
            if (!TextUtils.isEmpty(title)) {
                label.setText(title);
            } else {
                label.setText("UNKONW " + position);
            }
            return convertView;
        }

    }


}
