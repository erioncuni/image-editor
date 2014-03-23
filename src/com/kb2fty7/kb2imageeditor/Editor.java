package com.kb2fty7.kb2imageeditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.xml.sax.DTDHandler;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Editor extends SherlockActivity {

	private static final int IDM_SAVE_OR_ACCEPT = 100;
	private static final int SAVE_FOR_WEB_DIALOG = 10;
	private static final int PROGRESS_DIALOG = 11;

	private static final int CODE_INTENT_FOR_ROTATE_ACTIVITY = 1001;

	private static boolean innerToolbar = false;
	private static int thisToolbar = 0;

	public static final int TOOLBAR = 0;
	public static final int TOOLBAR_ROTATE = 1;
	public static final int TOOLBAR_REFLECT = 2;
	public static final int TOOLBAR_CONTRAST = 3;
	public static final int TOOLBAR_BRIGHNESS = 4;
	public static final int TOOLBAR_EFFECTS = 5;

	private LinearLayout toolbar;
	private LinearLayout rotateToolbar;
	private LinearLayout reflectToolbar;
	private LinearLayout contrastToolbar;
	private LinearLayout brightnessToolbar;
	private LinearLayout effectsToolbar;

	private String pathImage = null;
	private static Bitmap editImage;
	private static Bitmap dynamicImage = null;

	private int indicatorContrast = 0;
	private int indicatorBrightness = 0;
	private int levelCompress=90;
	
	private boolean isEditingImage=false;

	private Menu button_s_a;

	private ImageView image_view_edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_editor);
		// apply title name for (Window) Activity
		setTitle(getString(R.string.title_activity_editor));
		// Скрывает статус бар
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		image_view_edit = (ImageView) findViewById(R.id.view_editor);
		pathImage = getIntent().getExtras().get("path").toString();
		BitmapDrawable bitdr = (BitmapDrawable) BitmapDrawable
				.createFromPath(pathImage);
		editImage = Bitmap.createBitmap(bitdr.getBitmap());
		bitdr = null;
		System.gc();

		image_view_edit.setImageBitmap(editImage);

		toolbar = (LinearLayout) findViewById(R.id.linear_layout_basic_toolbar);
		rotateToolbar = (LinearLayout) findViewById(R.id.toolbar_rotate);
		reflectToolbar = (LinearLayout) findViewById(R.id.toolbar_reflect);
		contrastToolbar = (LinearLayout) findViewById(R.id.toolbar_contrast);
		brightnessToolbar = (LinearLayout) findViewById(R.id.toolbar_brightness);
		effectsToolbar = (LinearLayout) findViewById(R.id.toolbar_effects);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_editor);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		image_view_edit.destroyDrawingCache();
		image_view_edit = null;
		toolbar = null;
		rotateToolbar = null;
		reflectToolbar = null;
		contrastToolbar = null;
		brightnessToolbar = null;
		effectsToolbar = null;
		System.gc();
		image_view_edit = (ImageView) findViewById(R.id.view_editor);
		toolbar = (LinearLayout) findViewById(R.id.linear_layout_basic_toolbar);
		rotateToolbar = (LinearLayout) findViewById(R.id.toolbar_rotate);
		reflectToolbar = (LinearLayout) findViewById(R.id.toolbar_reflect);
		contrastToolbar = (LinearLayout) findViewById(R.id.toolbar_contrast);
		brightnessToolbar = (LinearLayout) findViewById(R.id.toolbar_brightness);
		effectsToolbar = (LinearLayout) findViewById(R.id.toolbar_effects);

		if (dynamicImage != null)
			image_view_edit.setImageBitmap(dynamicImage);
		else
			image_view_edit.setImageBitmap(editImage);

		setVisibleAndEnableView(thisToolbar);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		button_s_a = menu;
		button_s_a.add(Menu.NONE, IDM_SAVE_OR_ACCEPT, Menu.NONE, "Accept")
				.setTitle(getString(R.string.name_menu_button))
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;

	}

	@Override
	public void onBackPressed() {
		if (innerToolbar) {
			setVisibleAndEnableView(TOOLBAR);
			if (dynamicImage != null) {
				dynamicImage.recycle();
				dynamicImage = null;
			}
			image_view_edit.destroyDrawingCache();
			System.gc();
			image_view_edit.setImageBitmap(editImage);

		} else {
			
			if (isEditingImage){
				
				AlertDialog.Builder ad;
				ad = new AlertDialog.Builder(Editor.this);
				ad.setTitle(getString(R.string.title_of_queshion));  // заголовок
				
				ad.setPositiveButton(getString(R.string.button_queshion_ok), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						isEditingImage=false;
						destroyActivity();
						dialog.cancel();
					}
				});
				ad.setNegativeButton(getString(R.string.button_queshion_cancel), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				
				ad.show();
				
				//========================================!!!!!!!!!!!!!!!dialog: do you Wont Save?
			
			}
			else
				destroyActivity();

			
			
		}
	}
	
	private void destroyActivity(){
if (!isEditingImage){
			
			if (dynamicImage != null) {
				dynamicImage.recycle();
				dynamicImage = null;
			}

			editImage.recycle();
			editImage = null;
			System.gc();
			super.onBackPressed();
			}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (innerToolbar) {
			if (dynamicImage != null) {
				editImage.recycle();
				System.gc();
				editImage = Bitmap.createBitmap(dynamicImage);
			}
			setVisibleAndEnableView(TOOLBAR);
			isEditingImage=true;
			
		} else {
			new SaveImageOfBitmap(levelCompress, editImage,this);
			
			isEditingImage=false;
			

			/*
			 * Time time = new Time(); try { File out = new File(folderToSave,
			 * Integer.toString(time.month) + Integer.toString(time.monthDay) +
			 * Integer.toString(time.hour) + Integer.toString(time.minute) +
			 * Integer.toString(time.second) + ".jpg"); out.createNewFile();
			 * FileOutputStream fos = new FileOutputStream(out);
			 * editImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
			 * fos.close(); } catch (Exception e) {
			 * Toast.makeText(getApplicationContext(), "error",
			 * Toast.LENGTH_LONG) .show(); e.printStackTrace(); }
			 */
		}
		return true;

	}

	public void clickButtonRotate(View view) {
		setVisibleAndEnableView(TOOLBAR_ROTATE);

	}

	public void clickButtonReflect(View view) {
		setVisibleAndEnableView(TOOLBAR_REFLECT);

	}

	public void clickButtonContrast(View view) {
		setVisibleAndEnableView(TOOLBAR_CONTRAST);

	}

	public void clickButtonBrightness(View view) {
		setVisibleAndEnableView(TOOLBAR_BRIGHNESS);

	}

	public void clickButtonEffects(View view) {
		setVisibleAndEnableView(TOOLBAR_EFFECTS);

	}

	public void clickButtonSaveForWeb(View view) {
		showDialog(SAVE_FOR_WEB_DIALOG);
	}

	// =========================rotateToolbar===========================================
	public void clickButtonRotate_90_left(View v) {
		showDialog(PROGRESS_DIALOG);
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(rotate(-90));
		removeDialog(PROGRESS_DIALOG);
	}

	public void clickButtonRotate_90_right(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(rotate(90));

	}

	public void clickButtonRotate_180(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(rotate(180));
	}

	public static Bitmap rotate(float degree) {

				// создаем новый объект Matrix
				Matrix matrix = new Matrix();
				// устанавливем угол поворота
				matrix.postRotate(degree);

				// возвращаем новый bitmap, повернутый при помощи matrix

				if (dynamicImage != null) {
					dynamicImage.recycle();
					dynamicImage = null;
					System.gc();
				}
				dynamicImage = Bitmap.createBitmap(editImage, 0, 0,
						editImage.getWidth(), editImage.getHeight(), matrix, true);
				
				
			
			
		return dynamicImage;
		
	}

	// =================================================================================

	// =================================reflectToolbar===================================

	public void clickButtonReflect_leftToRight(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(flip(-1));
	}

	public void clickButtonReflect_upToDown(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(flip(0));
	}

	public void clickButtonReflect_diagonally(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(flip(1));
	}

	public static Bitmap flip(int type) {
		// Получаем изображение с указанного ImageView
		int bmpWidth = editImage.getWidth();
		int bmpHeight = editImage.getHeight();
		Matrix matrix = new Matrix();
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}
		if (type == -1) {
			matrix.preScale(-1.0f, 1.0f);
			dynamicImage = Bitmap.createBitmap(editImage, 0, 0, bmpWidth,
					bmpHeight, matrix, true);
		} else if (type == 0) {
			matrix.preScale(1.0f, -1.0f);
			dynamicImage = Bitmap.createBitmap(editImage, 0, 0, bmpWidth,
					bmpHeight, matrix, true);
		} else if (type == 1) {
			matrix.preScale(-1.0f, -1.0f);
			dynamicImage = Bitmap.createBitmap(editImage, 0, 0, bmpWidth,
					bmpHeight, matrix, true);
		}
		return dynamicImage;

	}

	// ==================================================================================

	// ===================================contrastToolbar================================

	public void clickButtonContrast_increase(View v) {
		showDialog(PROGRESS_DIALOG);
		indicatorContrast = +20;
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(createContrast(indicatorContrast));
		removeDialog(PROGRESS_DIALOG);

	}

	public void clickButtonContrast_decrease(View v) {
		indicatorContrast = -20;
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(createContrast(indicatorContrast));
	}

	public static Bitmap createContrast(double value) {
		// Размеры изображения
		int width = editImage.getWidth();
		int height = editImage.getHeight();
		// подготавливаем финальное изображение
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}
		dynamicImage = Bitmap
				.createBitmap(width, height, editImage.getConfig());
		// информация о цвете пикселя
		int A, R, G, B;
		int pixel;
		// устанавливем значение контрастности
		double contrast = Math.pow((100 + (value)) / 100, 2);

		// проходим через каждый пиксель
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// получаем цвет пикселя
				pixel = editImage.getPixel(x, y);
				A = Color.alpha(pixel);
				// применяем контрастность к каждому каналу R, G, B
				R = Color.red(pixel);
				R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if (R < 0) {
					R = 0;
				} else if (R > 255) {
					R = 255;
				}

				G = Color.green(pixel);
				G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if (G < 0) {
					G = 0;
				} else if (G > 255) {
					G = 255;
				}

				B = Color.blue(pixel);
				B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if (B < 0) {
					B = 0;
				} else if (B > 255) {
					B = 255;
				}
				dynamicImage.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		// dynamicImage.recycle();
		// System.gc();
		return dynamicImage;
	}

	// ==================================================================================

	// ===============================brightnessToolbar===================================
	public void clickButtonBrightness_increase(View v) {
		indicatorBrightness = +20;
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(createBrightness(indicatorBrightness));

	}

	public void clickButtonBrightness_decrease(View v) {
		indicatorBrightness = -20;
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(createBrightness(indicatorBrightness));
	}

	public static Bitmap createBrightness(int value) {
		// размер изображения
		int width = editImage.getWidth();
		int height = editImage.getHeight();
		// подготавливаем финальное изображение
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}
		dynamicImage = Bitmap
				.createBitmap(width, height, editImage.getConfig());
		// информация о цвете пикселя
		int A, R, G, B;
		int pixel;

		// проходим через каждый пиксель
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// получаем цвет пикселя
				pixel = editImage.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// увеличиваем/уменьшаем каждый цветовой канал
				R += value;
				if (R > 255) {
					R = 255;
				} else if (R < 0) {
					R = 0;
				}

				G += value;
				if (G > 255) {
					G = 255;
				} else if (G < 0) {
					G = 0;
				}

				B += value;
				if (B > 255) {
					B = 255;
				} else if (B < 0) {
					B = 0;
				}

				// присваиваем новый цвет каждому пикселю финального изображения
				dynamicImage.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		// получаем финальное изображение
		return dynamicImage;
	}

	// ===================================================================================

	// =======================================effectsToolbar==============================

	public void clickButtonEffects_invert(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(doInvert());

	}

	public void clickButtonEffects_sepia(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(createSepia());

	}

	public void clickButtonEffects_blur(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(addBlur());

	}

	public void clickButtonEffects_sharpening(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(addSharpen());

	}

	public void clickButtonEffects_emboss(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(addEmboss());

	}

	public void clickButtonEffects_redBoost(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(addBoost(1, 100));

	}

	public void clickButtonEffects_greenBoost(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(addBoost(2, 100));

	}

	public void clickButtonEffects_blueBoost(View v) {
		image_view_edit.destroyDrawingCache();
		System.gc();
		image_view_edit.setImageBitmap(addBoost(3, 100));

	}

	public static Bitmap doInvert() {
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}

		dynamicImage = Bitmap.createBitmap(editImage.getWidth(),
				editImage.getHeight(), editImage.getConfig());
		// color info
		int A, R, G, B;
		int pixelColor;
		// image size
		int height = editImage.getHeight();
		int width = editImage.getWidth();

		// scan through every pixel
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// get one pixel
				pixelColor = editImage.getPixel(x, y);
				// saving alpha channel
				A = Color.alpha(pixelColor);
				// inverting byte for each R/G/B channel
				R = 255 - Color.red(pixelColor);
				G = 255 - Color.green(pixelColor);
				B = 255 - Color.blue(pixelColor);
				// set newly-inverted pixel to output image
				dynamicImage.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final bitmap
		return dynamicImage;
	}

	public static Bitmap createSepia() {

		int depth = 20;
		double red = 1.8;
		double green = 2.0;
		double blue = 1.5;
		// image size
		int width = editImage.getWidth();
		int height = editImage.getHeight();
		// create output bitmap
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}
		dynamicImage = Bitmap
				.createBitmap(width, height, editImage.getConfig());
		// constant grayscale
		final double GS_RED = 0.3;
		final double GS_GREEN = 0.59;
		final double GS_BLUE = 0.11;
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = editImage.getPixel(x, y);
				// get color on each channel
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// apply grayscale sample
				B = G = R = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);

				// apply intensity level for sepid-toning on each channel
				R += (depth * red);
				if (R > 255) {
					R = 255;
				}

				G += (depth * green);
				if (G > 255) {
					G = 255;
				}

				B += (depth * blue);
				if (B > 255) {
					B = 255;
				}

				// set new pixel color to output image
				dynamicImage.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return dynamicImage;
	}

	public static Bitmap addBlur() {
		double[][] GaussianBlurConfig = new double[][] { { 1, 2, 1 },
				{ 2, 4, 2 }, { 1, 2, 1 } };
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(GaussianBlurConfig);
		convMatrix.Factor = 16;
		convMatrix.Offset = 0;
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}

		return ConvolutionMatrix.computeConvolution3x3(editImage, dynamicImage,
				convMatrix);
	}

	public static Bitmap addSharpen() {
		double weight = 10;
		double[][] SharpConfig = new double[][] { { 0, -2, 0 },
				{ -2, weight, -2 }, { 0, -2, 0 } };
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(SharpConfig);
		convMatrix.Factor = weight - 8;
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}
		return ConvolutionMatrix.computeConvolution3x3(editImage, dynamicImage,
				convMatrix);
	}

	public static Bitmap addEmboss() {
		double[][] EmbossConfig = new double[][] { { -1, 0, -1 }, { 0, 4, 0 },
				{ -1, 0, -1 } };
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(EmbossConfig);
		convMatrix.Factor = 1;
		convMatrix.Offset = 16;
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}
		return ConvolutionMatrix.computeConvolution3x3(editImage, dynamicImage,
				convMatrix);
	}

	public static Bitmap addBoost(int type, float percent) {
		int width = editImage.getWidth();
		int height = editImage.getHeight();
		if (dynamicImage != null) {
			dynamicImage.recycle();
			dynamicImage = null;
			System.gc();
		}
		dynamicImage = Bitmap
				.createBitmap(width, height, editImage.getConfig());

		int A, R, G, B;
		int pixel;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				pixel = editImage.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				if (type == 1) {
					R = (int) (R * (1 + percent));
					if (R > 255)
						R = 255;
				} else if (type == 2) {
					G = (int) (G * (1 + percent));
					if (G > 255)
						G = 255;
				} else if (type == 3) {
					B = (int) (B * (1 + percent));
					if (B > 255)
						B = 255;
				}
				dynamicImage.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		return dynamicImage;
	}

	// ===================================================================================

	public void setVisibleAndEnableView(int NumberView) {
		switch (NumberView) {
		case (TOOLBAR): {
			rotateToolbar.setVisibility(-1);
			reflectToolbar.setVisibility(-1);
			contrastToolbar.setVisibility(-1);
			brightnessToolbar.setVisibility(-1);
			effectsToolbar.setVisibility(-1);
			toolbar.setVisibility(0);
			innerToolbar = false;
			button_s_a.getItem(0)
					.setTitle(getString(R.string.name_menu_button));
			setTitle(getString(R.string.title_activity_editor));
			thisToolbar = TOOLBAR;
			break;
		}
		case (TOOLBAR_ROTATE): {
			toolbar.setVisibility(-1);
			rotateToolbar.setVisibility(0);
			innerToolbar = true;
			button_s_a.getItem(0).setTitle(
					getString(R.string.name_menu_button_in));
			setTitle(getString(R.string.title_activity_rotate));
			thisToolbar = TOOLBAR_ROTATE;
			break;
		}
		case (TOOLBAR_REFLECT): {
			toolbar.setVisibility(-1);
			reflectToolbar.setVisibility(0);
			innerToolbar = true;
			button_s_a.getItem(0).setTitle(
					getString(R.string.name_menu_button_in));
			setTitle(getString(R.string.title_activity_reflect));
			thisToolbar = TOOLBAR_REFLECT;
			break;
		}
		case (TOOLBAR_CONTRAST): {
			toolbar.setVisibility(-1);
			contrastToolbar.setVisibility(0);
			innerToolbar = true;
			button_s_a.getItem(0).setTitle(
					getString(R.string.name_menu_button_in));
			setTitle(getString(R.string.title_activity_contrast));
			thisToolbar = TOOLBAR_CONTRAST;
			indicatorContrast = 0;
			break;
		}
		case (TOOLBAR_BRIGHNESS): {
			toolbar.setVisibility(-1);
			brightnessToolbar.setVisibility(0);
			innerToolbar = true;
			button_s_a.getItem(0).setTitle(
					getString(R.string.name_menu_button_in));
			setTitle(getString(R.string.title_activity_brightness));
			thisToolbar = TOOLBAR_BRIGHNESS;
			indicatorBrightness = 0;
			break;
		}
		case (TOOLBAR_EFFECTS): {
			toolbar.setVisibility(-1);
			effectsToolbar.setVisibility(0);
			innerToolbar = true;
			button_s_a.getItem(0).setTitle(
					getString(R.string.name_menu_button_in));
			setTitle(getString(R.string.title_activity_effects));
			thisToolbar = TOOLBAR_EFFECTS;
			break;
		}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		// Обработчик инф диалога
		case SAVE_FOR_WEB_DIALOG:
			LayoutInflater inflater = getLayoutInflater();

			View layout = inflater.inflate(R.layout.save_as_web_alert_dialog,
					(ViewGroup) findViewById(R.id.save_as_web));

			final EditText WSEditViewWidth = (EditText) layout
					.findViewById(R.id.ws_edit_text_width);
			final EditText WSEditViewHeight = (EditText) layout
					.findViewById(R.id.ws_edit_text_height);
			final CheckBox WSCheckBox = (CheckBox) layout
					.findViewById(R.id.sw_check_box);
			final SeekBar WSSeekBar = (SeekBar) layout
					.findViewById(R.id.sw_seek_bar);
			final TextView WSLevelCompress = (TextView) layout
					.findViewById(R.id.sw_values_of_level_compress);
			final int widthImage = editImage.getWidth();
			final int heightImage = editImage.getHeight();

			WSEditViewWidth.setText(String.valueOf(widthImage));
			WSEditViewHeight.setText(String.valueOf(heightImage));
			WSSeekBar.setProgress(levelCompress);
			WSLevelCompress.setText(String.valueOf(levelCompress));
			
		    Button WSButtonAccept =(Button) layout.findViewById(R.id.sw_accept);
		    Button WSButtonCancel =(Button) layout.findViewById(R.id.sw_cancel);
		    
		    WSButtonAccept.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
		            int width = editImage.getWidth();
		            int height = editImage.getHeight();
		            int newWidth = Integer.parseInt(WSEditViewWidth.getText().toString());
		            int newHeight = Integer.parseInt(WSEditViewHeight.getText().toString());
		            float scaleWidth = ((float) newWidth) / width;
		            float scaleHeight = ((float) newHeight) / height;
		         Matrix matrix = new Matrix();
		         
		         matrix.postScale(scaleWidth, scaleHeight);
		         dynamicImage = Bitmap.createBitmap(editImage, 0, 0, width, height, matrix, true);
		         editImage.recycle();
		         System.gc();
		         editImage=Bitmap.createBitmap(dynamicImage);
		         image_view_edit.destroyDrawingCache();
		         dynamicImage.recycle();
		         System.gc();
		         dynamicImage=null;
		         image_view_edit.setImageBitmap(editImage);
		         levelCompress = WSSeekBar.getProgress();
		         removeDialog(SAVE_FOR_WEB_DIALOG);
					
				}
			});
		    
		    WSButtonCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					removeDialog(SAVE_FOR_WEB_DIALOG);
				}
			});
			WSEditViewWidth.setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN
							&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
						if (WSEditViewWidth.getText().toString().equals("")) {
							WSEditViewWidth.setText(String.valueOf(widthImage));
						} else {
							if (widthImage <= Integer.valueOf(WSEditViewWidth
									.getText().toString())) {
								WSEditViewWidth.setText(String
										.valueOf(widthImage));
							}
						}

						if (WSCheckBox.isChecked()) {
							double levelSize = (widthImage + 0.0)
									/ Integer.valueOf(WSEditViewWidth.getText()
											.toString());
							WSEditViewHeight.setText(String.valueOf(Math
									.round(heightImage / levelSize)));
						}

						return true;
					}
					return false;
				}
			});
			WSEditViewHeight.setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN
							&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
						if (WSEditViewHeight.getText().toString().equals("")) {
							WSEditViewHeight.setText(String
									.valueOf(heightImage));
						} else {
							if (heightImage <= Integer.valueOf(WSEditViewHeight
									.getText().toString())) {
								WSEditViewHeight.setText(String
										.valueOf(heightImage));
							}
						}

						if (WSCheckBox.isChecked()) {
							double levelSize = (heightImage + 0.0)
									/ Integer.valueOf(WSEditViewHeight
											.getText().toString());
							WSEditViewWidth.setText(String.valueOf(Math
									.round(widthImage / levelSize)));
						}

						return true;
					}
					return false;
				}
			});

			WSCheckBox.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					double levelSize = (widthImage + 0.0)
							/ Integer.valueOf(WSEditViewWidth.getText()
									.toString());
					WSEditViewHeight.setText(String.valueOf(Math
							.round(heightImage / levelSize)));
				}
			});

			WSSeekBar
					.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							WSLevelCompress.setText(String
									.valueOf(WSSeekBar.getProgress()));
							if (WSSeekBar.getProgress() < 30)
								WSSeekBar.setProgress(30);

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							WSLevelCompress.setText(String
									.valueOf(WSSeekBar.getProgress()));
							if (WSSeekBar.getProgress() < 30)
								WSSeekBar.setProgress(30);

						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							WSLevelCompress.setText(String
									.valueOf(WSSeekBar.getProgress()));
							if (WSSeekBar.getProgress() < 30)
								WSSeekBar.setProgress(30);

						}
					});

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(layout);

			// builder.setMessage(R.string.button_save_for_web);
			/*
			 * builder.setNeutralButton(R.string.About_button, new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface arg0, int arg1) {
			 * // TODO Auto-generated method stub arg0.cancel();
			 * 
			 * } });
			 */
			// builder.setCancelable(false);
			return builder.create();
		case PROGRESS_DIALOG: {
			ProgressDialog wProgressDialog=new ProgressDialog(this);
			wProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			wProgressDialog.setMessage("Please wait");
			wProgressDialog.onStart();
		}
			
		default:
			return null;
		}
	}

}
