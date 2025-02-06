package kr.co.clipsoft.util;

import java.util.Calendar;

import org.apache.cordova.CallbackContext;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.DatePicker;

public class CallDatepicker {
	
	Context context;
	Calendar calendar;
	private static String TAG_NAME = "Datepicker";
	private OnDateSetListener dateSetListener;
	
	public CallDatepicker(Context context, final CallbackContext callbackContext){
		this.context = context;	
		calendar = Calendar.getInstance();	
		dateSetListener = new OnDateSetListener() {			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub				
				String month =  monthOfYear+1<=9?"0"+(monthOfYear+1):String.valueOf(monthOfYear+1);
				String day = dayOfMonth<=9?"0"+dayOfMonth:String.valueOf(dayOfMonth);				
		        String date = year+"-"+month+"-"+day;
		        callbackContext.success(date);
			}
		};
	}
	
	// datepicker 초기값 셋팅 
	public void showDatepicker(String date){
		Log.i(TAG_NAME, "[showDatepicker] date : " + date);		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		if(date!= null && date != "" && date.length() == 10) {
			year = Integer.parseInt(date.split("-")[0]);
			month = (Integer.parseInt(date.split("-")[1])-1); //android month는 +1이 되기 때문에 -1을 해줌 
			day = Integer.parseInt(date.split("-")[2]);
		}
		Log.i(TAG_NAME, "[showDatepicker] year : " + year);		
		Log.i(TAG_NAME, "[showDatepicker] month : " + month);
		Log.i(TAG_NAME, "[showDatepicker] day : " + day);
		
		Context themeContext = new ContextThemeWrapper(context, android.R.style.Theme_DeviceDefault_Light);
		
		new DatePickerDialog(themeContext, dateSetListener, year, month, day).show();
	}
}
