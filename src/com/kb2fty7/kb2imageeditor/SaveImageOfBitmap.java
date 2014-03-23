package com.kb2fty7.kb2imageeditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.Time;
import android.view.View;
import android.widget.EditText;

public class SaveImageOfBitmap  extends SherlockActivity{
	private String folderToSave = Environment.getExternalStorageDirectory()
			.toString();
	private AlertDialog.Builder ad;
	private String nameFileOfImage="";
	
	
	
	
	public SaveImageOfBitmap(int levelCompress, Bitmap image, Context context) {
		siCreateSaveDialog(context);
		
		try {
			FileOutputStream fos = new FileOutputStream(siCreatFile(nameFileOfImage));
			image.compress(Bitmap.CompressFormat.JPEG, levelCompress, fos);
			fos.close();
		}
			 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	
	private File siCreatFile(String nameImage){
		if (nameImage==""){
			Time time = new Time();
			nameImage=Integer.toString(time.month)
			+ Integer.toString(time.monthDay)
			+ Integer.toString(time.hour)
			+ Integer.toString(time.minute)
			+ Integer.toString(time.second);
			
		}
		File out = new File(folderToSave, nameImage + ".jpg");
		try {
			out.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
		
	}
	
	private void siCreateSaveDialog(Context context){
		
		//String title = getString(R.string.title_of_save_dialog);
		String title = context.getString(R.string.title_of_save_dialog);
		String message = context.getString(R.string.text_of_save_dialog);
		String button1String = context.getString(R.string.button_of_save_dialog);;
		
		ad = new AlertDialog.Builder(context);
		ad.setTitle(title);  // заголовок
		ad.setMessage(message); // сообщение
		final EditText nameFileEditText = new EditText(context);
		ad.setView(nameFileEditText);
		ad.setPositiveButton(button1String, new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				nameFileOfImage=nameFileEditText.getText().toString();
				dialog.cancel();
			}
		});
		
		
		
		ad.show();
		
	}

}
