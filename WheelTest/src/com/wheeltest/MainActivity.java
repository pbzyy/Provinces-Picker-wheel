package com.wheeltest;

import java.io.IOException;
import java.util.Map;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final String DBNAME = "dqxx.sqlite";
	private static final String TABLE_NAME = "dqxx";
	private SQLiteDatabase db;
	private Map<String, Integer> provinceMap;
	private Map<String, Integer> cityMap;
	private Map<String, Integer> areaMap;
	
	private String[] provinceArray;
	private String[] cityArray;
	private String[] areaArray;
	
	private WheelView provinceWheelView;
	private WheelView cityWheelView;
	private WheelView areaWheelView;
	
	private ProviceCityAreaAdapter provinceAdapter;
	private ProviceCityAreaAdapter cityAdapter;
	private ProviceCityAreaAdapter areaAdapter;
	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initWheelView();
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						StringBuilder sb = new StringBuilder();
						sb.append(provinceArray[provinceWheelView.getCurrentItem()]);
						if (provinceArray[provinceWheelView.getCurrentItem()].endsWith("市")) {
							sb.append("市辖区");
						}else {
							sb.append(cityArray[cityWheelView.getCurrentItem()]);
						}
						sb.append(areaArray[areaWheelView.getCurrentItem()]);
						
						Toast.makeText(MainActivity.this, sb.toString()+" key:"+DqxxUtils.findPrimaryKey(db, TABLE_NAME, sb.toString()), Toast.LENGTH_SHORT).show();
					}
				}, 400);
				
			}
		});
	}
	
	public void initWheelView() {
		provinceWheelView = (WheelView)findViewById(R.id.provice);
		cityWheelView = (WheelView)findViewById(R.id.city);
		areaWheelView = (WheelView)findViewById(R.id.area);
		
		//初始化省滚轮列表选择器
		initProviceMap();
		provinceAdapter = new ProviceCityAreaAdapter(MainActivity.this, provinceArray, 0);
		provinceWheelView.setViewAdapter(provinceAdapter);
		provinceWheelView.setCurrentItem(0);
		provinceWheelView.addScrollingListener(privinceScrollListener);
		
		//初始化城市滚轮列表选择器
		String provinceName = provinceArray[0];
		int dqx_dqxx01 = provinceMap.get(provinceName);
		if (provinceName.endsWith("市")) {
			initCityMap(dqx_dqxx01, false);
		}else {
			initCityMap(dqx_dqxx01, true);
		}
		cityAdapter = new ProviceCityAreaAdapter(MainActivity.this, cityArray, 0);
		cityWheelView.setViewAdapter(cityAdapter);
		cityWheelView.setCurrentItem(0);
		cityWheelView.addScrollingListener(cityScrollListener);
		
		//初始化地区滚轮列表选择器
		String cityName = cityArray[0];
		int dqx_dqxx01_2 = cityMap.get(cityName);
		provinceName = cityArray[0];
		if (provinceName.endsWith("市")) {
			dqx_dqxx01_2 = dqx_dqxx01_2 * 100 +1;
		}
		initAreaMap(dqx_dqxx01_2);
		areaAdapter = new ProviceCityAreaAdapter(MainActivity.this, areaArray, 0);
		areaWheelView.setViewAdapter(areaAdapter);
		areaWheelView.setCurrentItem(0);
		
	}
	
	
	OnWheelScrollListener privinceScrollListener = new OnWheelScrollListener() {
		
		@Override
		public void onScrollingStarted(WheelView wheel) {
		}
		
		@Override
		public void onScrollingFinished(WheelView wheel) {
			int currentItem = wheel.getCurrentItem();
			String provinceName = provinceArray[currentItem];
			int dqxx01 = provinceMap.get(provinceName);
			if (provinceName.endsWith("市")) {
				initCityMap(dqxx01, false);
			}else {
				initCityMap(dqxx01, true);
			}
			
			cityAdapter = new ProviceCityAreaAdapter(MainActivity.this, cityArray, 0);
			cityWheelView.setViewAdapter(cityAdapter);
			cityWheelView.setCurrentItem(0);
			
			String cityName = cityArray[0];
			int dqx_dqxx01_2 = cityMap.get(cityName);
			if (provinceName.endsWith("市")) {
				dqx_dqxx01_2 = dqx_dqxx01_2 * 100 +1;
			}
			initAreaMap(dqx_dqxx01_2);
			areaAdapter = new ProviceCityAreaAdapter(MainActivity.this, areaArray, 0);
			areaWheelView.setViewAdapter(areaAdapter);
			areaWheelView.setCurrentItem(0);
		}
	};
	
	
	OnWheelScrollListener cityScrollListener = new OnWheelScrollListener() {
		
		@Override
		public void onScrollingStarted(WheelView wheel) {
		}
		
		@Override
		public void onScrollingFinished(WheelView wheel) {
			String provinceName = provinceArray[provinceWheelView.getCurrentItem()];
			int dqx_dqxx01 = cityMap.get(cityArray[wheel.getCurrentItem()]);
			if (provinceName.endsWith("市")) {
				dqx_dqxx01 = dqx_dqxx01 * 100 +1;
			}
			initAreaMap(dqx_dqxx01);
			areaAdapter = new ProviceCityAreaAdapter(MainActivity.this, areaArray, 0);
			areaWheelView.setViewAdapter(areaAdapter);
			areaWheelView.setCurrentItem(0);
		}
	};
	
	
	public void initProviceMap() {
		try {
			DqxxUtils.copyDB(MainActivity.this, DBNAME);
			if (db == null) {
				db = openOrCreateDatabase(getFilesDir().getAbsolutePath() + "/" +DBNAME, Context.MODE_PRIVATE, null);
			}
			provinceMap = DqxxUtils.getProvince(db, TABLE_NAME);
			provinceArray = provinceMap.keySet().toArray(new String[provinceMap.size()]);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initCityMap(int dqx_dqxx01, boolean municipalities) {
		try {
			DqxxUtils.copyDB(MainActivity.this, DBNAME);
			if (db == null) {
				db = openOrCreateDatabase(getFilesDir().getAbsolutePath() + "/" +DBNAME, Context.MODE_PRIVATE, null);
			}
			cityMap = DqxxUtils.getCity(db, TABLE_NAME, dqx_dqxx01, municipalities);
			cityArray = cityMap.keySet().toArray(new String[cityMap.size()]);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initAreaMap(int dqx_dqxx01) {
		try {
			DqxxUtils.copyDB(MainActivity.this, DBNAME);
			if (db == null) {
				db = openOrCreateDatabase(getFilesDir().getAbsolutePath() + "/" +DBNAME, Context.MODE_PRIVATE, null);
			}
			areaMap = DqxxUtils.getArea(db, TABLE_NAME, dqx_dqxx01);
			areaArray = areaMap.keySet().toArray(new String[areaMap.size()]);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public class ProviceCityAreaAdapter extends ArrayWheelAdapter<String> {
		private int currentItem;
		private int currentValue;
		
		public ProviceCityAreaAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentValue = current;
		}
		
		
		public void setCurrentValue(int value){
			this.currentValue = value;
		}
		
		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
//			if (currentItem == currentValue) {
//				view.setTextColor(0xFF0000F0);
//			}
			view.setTypeface(Typeface.SANS_SERIF);
		}
		
		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, convertView, parent);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		if (db != null) {
			db.close();
			db = null;
		}
		super.onDestroy();
	}
	
}
