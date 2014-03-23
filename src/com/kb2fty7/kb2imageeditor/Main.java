package com.kb2fty7.kb2imageeditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kb2fty7.kb2imageeditor.R;




public class Main extends SherlockActivity {	
	//Идентификатор для кнопки "I" в SherlockActionBar-е
	private static final int IDM_INFO=101;
	//Идентификатор информационого диалога
	private static final int IDM_DIALOG=202;
	//Идентификатор для намерения получения снимка с камеры
	private static final int TAKE_CAMERA_PICTURE = 301;
	//Идентификатор для намерения получения снимка с галереи
	private static final int TAKE_GALLERY_PICTURE = 302;
	
	
	
	
	private Button IBCamera;
	private Button IBGallery;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Скрывает заголовок приложения
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.activity_main); 
        //Скрывает статус бар
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        IBCamera=(Button) findViewById(R.id.Button_OpenCamera);
        IBCamera.setOnClickListener(clickCameraButton);
        IBGallery=(Button) findViewById(R.id.Button_OpenGallery);
        IBGallery.setOnClickListener(clickGalleryButton);
        
    }
  
    private View.OnClickListener clickCameraButton = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, TAKE_CAMERA_PICTURE);	
		}
	};
    
	private View.OnClickListener clickGalleryButton = new View.OnClickListener() {
	@Override
	public void onClick(View v) {
		Intent gallery_Intent = new Intent(Intent.ACTION_GET_CONTENT);
		gallery_Intent.setType("image/*"); 
        startActivityForResult(Intent.createChooser(gallery_Intent,
                "Select Picture"), TAKE_GALLERY_PICTURE);
	}
};
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { 
    	//Создает кнопку "I" c иконкой в SherlockActionBar
    	menu.add(Menu.NONE, IDM_INFO, Menu.NONE,"info").setIcon(R.drawable.ic_action_example).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    	return true;

    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch(requestCode){
    	case(TAKE_CAMERA_PICTURE):
    		if(resultCode==RESULT_OK){    			
    			Intent intent = getImageData(data);
    			startActivity(intent);
    			
    		}
    	break;
    	case(TAKE_GALLERY_PICTURE):
    		if(resultCode==RESULT_OK){    			
    			Intent intent = getImageData(data);
    			startActivity(intent);
    			
    	}
    	break;
    }
    }
    
    

	private Intent getImageData(Intent data){
		
			Uri selectedImage = data.getData();
			String[] filePathColumn = {MediaStore.Images.Media.DATA};

			
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			
			//=============================================вызвать класс редактора================
			Intent intent = new Intent().setClass(Main.this, Editor.class);
			//====================================================================================
			intent.putExtra("path", filePath);
			return intent;
		}
    
    
    public static void fileSave(InputStream is, FileOutputStream outputStream) {
        int i;
        try {
            while ((i = is.read()) != -1) {
                outputStream.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
   //Обработчик нажатия в SherlockActionBar  
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	//Обработчик кнопки "I" 
    	case IDM_INFO:
    		showDialog(IDM_DIALOG);
    		return true;
    	}
    	return false;
    }
    //Стандартный метод вызываемый при создании диалогового окна
    @Override
    protected Dialog onCreateDialog(int  id){
    	switch(id){
    	//Обработчик инф диалога
    	case IDM_DIALOG:
    		LayoutInflater inflater = getLayoutInflater();
    		View layout= inflater.inflate(R.layout.info_alert_dialog, (ViewGroup)findViewById(R.id.inf_dialog));
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setView(layout);
    		builder.setMessage(R.string.About);
    		builder.setNeutralButton(R.string.About_button, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					arg0.cancel();
					
				}
			});
    		builder.setCancelable(false);
    		return builder.create();
    	default:	
    		return null;
    	}
    }
    
    
    @Override
	public void onLowMemory() {
		super.onLowMemory();

		System.gc();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();


		System.gc();
	}
   
    
    
}
