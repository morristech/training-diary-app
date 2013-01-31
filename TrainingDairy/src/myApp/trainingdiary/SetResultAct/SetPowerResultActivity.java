package myApp.trainingdiary.SetResultAct;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import myApp.trainingdiary.R;
import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/*
 * ��������� ��� ������ ���������� ������� ���������� 
 */

public class SetPowerResultActivity extends Activity implements OnClickListener
{
	
	TextView tvnameEx, tvEndedRep;
	String strNameEx;
	String strNameTr;
	DBHelper dbHelper;
	final int MENU_DEL_LAST_SET = 1;
	final int MENU_SHOW_LAST_RESULT = 2;
	
	//forms
	Button btnW1p, btnW2p, btnW3p, btnW1m, btnW2m, btnW3m, btnW4p, btnW4m, btnRepp, btnRepm, btnSet;
	EditText editTextW1, editTextW2, editTextW3, editTextW4, editTextRep;
	//

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_power_result);
		tvnameEx = (TextView)findViewById(R.id.tvNameEx);
		tvEndedRep = (TextView)findViewById(R.id.tvEndedRep);
		strNameEx = getIntent().getExtras().getString("nameEx");
		strNameTr = getIntent().getExtras().getString("nameTr");
		tvnameEx.setText(strNameEx);
		//
		btnW1p = (Button)findViewById(R.id.btnW1p);
		btnW2p = (Button)findViewById(R.id.btnW2p);
		btnW3p = (Button)findViewById(R.id.btnW3p);
		btnW1m = (Button)findViewById(R.id.btnW1m);
		btnW2m = (Button)findViewById(R.id.btnW2m);
		btnW3m = (Button)findViewById(R.id.btnW3m);
		btnRepp = (Button)findViewById(R.id.btnRepp);
		btnRepm = (Button)findViewById(R.id.btnRepm);
		btnSet = (Button)findViewById(R.id.btnSet);
		btnW4p = (Button)findViewById(R.id.btnW4p);
		btnW4m = (Button)findViewById(R.id.btnW4m);
		
		editTextW1 = (EditText)findViewById(R.id.editTextW1);
		editTextW2 = (EditText)findViewById(R.id.editTextW2);
		editTextW3 = (EditText)findViewById(R.id.editTextW3);
		editTextRep = (EditText)findViewById(R.id.editTextRep);
		editTextW4 = (EditText)findViewById(R.id.editTextW4);
				
		btnW1p.setOnClickListener(this);
		btnW2p.setOnClickListener(this);
		btnW3p.setOnClickListener(this);
		btnW1m.setOnClickListener(this);
		btnW2m.setOnClickListener(this);
		btnW3m.setOnClickListener(this);
		btnRepp.setOnClickListener(this);
		btnRepm.setOnClickListener(this);
		btnSet.setOnClickListener(this);
		btnW4p.setOnClickListener(this);
		btnW4m.setOnClickListener(this);
		//
		editTextW1.setText("0");
		editTextW2.setText("0");
		editTextW3.setText("0");
		editTextRep.setText("0");
		editTextW4.setText("0");
		
		RefreshTvEndedRep();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
			menu.add(0, MENU_DEL_LAST_SET, 1, "������� ��������� ������");
			menu.add(0, MENU_SHOW_LAST_RESULT, 1, "�������� ������� ����������");
			return true;
	}

	@Override
	public void onClick(View arg0)
	{

	    switch (arg0.getId()) 
	    {
	    case R.id.btnW1p:
	    	changeCountET(editTextW1, "+");
	      break;
	    case R.id.btnW1m:
	    	changeCountET(editTextW1, "-");
	      break;
	    case R.id.btnW2p:
	    	changeCountET(editTextW2, "+");
	      break;
	    case R.id.btnW2m:
	    	changeCountET(editTextW2, "-");
		      break;
	    case R.id.btnW3p:
	    	changeCountET(editTextW3, "+");
	      break;
	    case R.id.btnW3m:
	    	changeCountET(editTextW3, "-");
		      break;
	    case R.id.btnRepp:
			String i = editTextRep.getText().toString();
			int j = Integer.parseInt(i);
			j = j + 1;
			String res = Integer.toString(j);
			editTextRep.setText(res);
	      break;
	    case R.id.btnRepm:	    	
			String a = editTextRep.getText().toString();
			int b = Integer.parseInt(a);
			b = b - 1;			
			if(b < 0)
			{
				b = 0;
			}
			String res2 = Integer.toString(b);
			editTextRep.setText(res2);
		   break;
	    case R.id.btnSet:
	    	SetRepOnDB();
	    	RefreshTvEndedRep();
		   break;
	    case R.id.btnW4p:
	    	editTextW4.setText("5");
	      break;
	    case R.id.btnW4m:
	    	editTextW4.setText("0");
		  break;
	    default:
	     break;
	    }
		
	}
	
	private void changeCountET(EditText Et, String type)
	{
		if(type.equalsIgnoreCase("+"))
		{
			String i = Et.getText().toString();
			int j = Integer.parseInt(i);
			j = j + 1;
			if(j > 9)
			{
				j = 0;
			}
			String res = Integer.toString(j);
			Et.setText(res);
		}
		else if(type.equalsIgnoreCase("-"))
		{
			String i = Et.getText().toString();
			int j = Integer.parseInt(i);
			j = j - 1;			
			if(j < 0)
			{
				j = 0;
			}
			String res = Integer.toString(j);
			Et.setText(res);
		}
	}	
	
	@SuppressLint("SimpleDateFormat")
	private void SetRepOnDB()
	{
		dbHelper = new DBHelper(this);
	    ContentValues cv = new ContentValues();	    	    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
	    //������� ����
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");		
		String Date = sdf.format(Calendar.getInstance().getTime());
		
		//������� ���
		String strPower = editTextW1.getText().toString() + editTextW2.getText().toString() + editTextW3.getText().toString() + "." + editTextW4.getText().toString();
		Float floPower = (float) Float.parseFloat(strPower);
		
		//������� ����������
		String strRep = editTextRep.getText().toString();
		int intRep = Integer.parseInt(strRep);
			
	    cv.put("trainingdate", Date);
	    cv.put("exercise", strNameEx);
	    cv.put("power", floPower);
	    cv.put("count", intRep);
	    cv.put("trainingday", strNameTr);
	    cv.put("exercisetype", "1");
	    db.insert("TrainingStat", null, cv);
	    
	    //db.close();
	    dbHelper.close();	    
	}
	
	@SuppressLint("SimpleDateFormat")
	private void RefreshTvEndedRep()
	{
		dbHelper = new DBHelper(this);    	    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");		
		String Date = sdf.format(Calendar.getInstance().getTime());
									   
	    String sqlQuery  = "select power, count from TrainingStat where trainingdate = ? and exercise = ?";
	    String[] args = {Date,strNameEx};	    	    
        Cursor c = db.rawQuery(sqlQuery, args);
       
        String result = "";
        
        if(c.moveToFirst())
        {     
        int exNameIndex = c.getColumnIndex("power");
        int exNameIndex2 = c.getColumnIndex("count");
        int i = 0;
        	do 
        	{  	        		
        		Float pow = c.getFloat(exNameIndex);
        		int cou = c.getInt(exNameIndex2);
        		result = result +pow+"x"+cou+"; "; 
        		i++;
        	} while (c.moveToNext()); 
        	
        	result = result + "\n����� ��������: "+i; 
        }
        
        c.close();
        dbHelper.close();        
        tvEndedRep.setText(result);      
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
		switch (item.getItemId()) 
		{
			case MENU_DEL_LAST_SET:
			
			DelDialog();	
				
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
	
    private void DelDialog() {
    	
    	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
	      adb.setTitle("�������� �������!!!");
	      adb.setMessage("������� ��������� ������?");    
	      adb.setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   DelLastEx();
               }
           });
        adb.setNegativeButton(getResources().getString(R.string.NO), new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
               }
           });        	    
	    adb.create().show();
	}
    
    @SuppressLint("SimpleDateFormat")
	private void DelLastEx() {
    	
		dbHelper = new DBHelper(this);    	    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();			    
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");		
		String Date = sdf.format(Calendar.getInstance().getTime());
		//�������� ������ ���������� ������� � ����������
	    String sqlQuery  = "SELECT " +
			    		"id " +
			    		"FROM TrainingStat " +
			    		"WHERE trainingdate = ? AND exercise = ? " +
			    		"ORDER BY id DESC LIMIT 1";	
	    
	    String[] args = {Date,strNameEx};				    			    
        Cursor c = db.rawQuery(sqlQuery, args);
        c.moveToFirst();
        int index = c.getColumnIndex("id");
        int idEx = c.getInt(index);
        //������� ������		        
        db.delete("TrainingStat", "id = " + idEx, null);
        //��������� ������� ��������				
		RefreshTvEndedRep();

	}
	
}
