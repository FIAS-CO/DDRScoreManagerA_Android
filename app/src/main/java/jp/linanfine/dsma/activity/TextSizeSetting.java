package jp.linanfine.dsma.activity;

import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

abstract public class TextSizeSetting extends Activity {
	
	protected AppearanceSettingsSp mSettings; 
	protected LinearLayout mPreview;
	protected View mPreviewMain;
	
	abstract protected void resetPreview();
	
	private void initialize()
	{

        mSettings = FileReader.readAppearanceSettings(this);
        
        {
	        //mPreviewMain = this.getLayoutInflater().inflate(R.layout.view_text_size_preview, null);
	        mPreview = (LinearLayout)this.findViewById(R.id.preview);
	        //mPreview.addView(mPreviewMain);
        }
        
        Button restoreDefault = (Button)this.findViewById(R.id.restoreDefault);
        restoreDefault.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				FileReader.clearAppearanceSettings(TextSizeSetting.this);
				TextSizeSetting.this.initialize();
			}});
        
        SeekBar categoryTopSeeker = (SeekBar)this.findViewById(R.id.seekBarCategoryTop);
        categoryTopSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 8;
				mSettings.CategoryTopFontSize = value;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textCategoryTop)).setText(String.valueOf(value));
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        categoryTopSeeker.setProgress((int)mSettings.CategoryTopFontSize-8);

        SeekBar categorySubItemSeeker = (SeekBar)this.findViewById(R.id.seekBarCategorySubItem);
        categorySubItemSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 8;
				mSettings.CategorySubItemFontSize = value;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textCategorySubItem)).setText(String.valueOf(value));
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        categorySubItemSeeker.setProgress((int)mSettings.CategorySubItemFontSize-8);

        SeekBar itemMusicLevelSeeker = (SeekBar)this.findViewById(R.id.seekBarItemMusicLevel);
        itemMusicLevelSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 8;
				mSettings.ItemMusicLevelFontSize = value;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textItemMusicLevel)).setText(String.valueOf(value));
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        itemMusicLevelSeeker.setProgress((int)mSettings.ItemMusicLevelFontSize-8);

        SeekBar itemMusicLevelXSeeker = (SeekBar)this.findViewById(R.id.seekBarItemMusicLevelX);
        itemMusicLevelXSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 50;
				mSettings.ItemMusicLevelFontSizeX = value/100.0f;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textItemMusicLevelX)).setText(String.valueOf(value)+"%");
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        itemMusicLevelXSeeker.setProgress((int)(mSettings.ItemMusicLevelFontSizeX*100)-50);

        SeekBar itemMusicNameSeeker = (SeekBar)this.findViewById(R.id.seekBarItemMusicName);
        itemMusicNameSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 8;
				mSettings.ItemMusicNameFontSize = value;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textItemMusicName)).setText(String.valueOf(value));
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        itemMusicNameSeeker.setProgress((int)mSettings.ItemMusicNameFontSize-8);

        SeekBar itemMusicNameXSeeker = (SeekBar)this.findViewById(R.id.seekBarItemMusicNameX);
        itemMusicNameXSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 50;
				mSettings.ItemMusicNameFontSizeX = value/100.0f;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textItemMusicNameX)).setText(String.valueOf(value)+"%");
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        itemMusicNameXSeeker.setProgress((int)(mSettings.ItemMusicNameFontSizeX*100)-50);

        SeekBar itemMusicRankSeeker = (SeekBar)this.findViewById(R.id.seekBarItemMusicRank);
        itemMusicRankSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 8;
				mSettings.ItemMusicRankFontSize = value;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textItemMusicRank)).setText(String.valueOf(value));
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        itemMusicRankSeeker.setProgress((int)mSettings.ItemMusicRankFontSize-8);

        SeekBar itemMusicRankXSeeker = (SeekBar)this.findViewById(R.id.seekBarItemMusicRankX);
        itemMusicRankXSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 50;
				mSettings.ItemMusicRankFontSizeX = value/100.0f;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textItemMusicRankX)).setText(String.valueOf(value)+"%");
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        itemMusicRankXSeeker.setProgress((int)(mSettings.ItemMusicRankFontSizeX*100)-50);

        SeekBar itemMusicScoreSeeker = (SeekBar)this.findViewById(R.id.seekBarItemMusicScore);
        itemMusicScoreSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 8;
				mSettings.ItemMusicScoreFontSize = value;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textItemMusicScore)).setText(String.valueOf(value));
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        itemMusicScoreSeeker.setProgress((int)mSettings.ItemMusicScoreFontSize-8);

        SeekBar itemMusicScoreXSeeker = (SeekBar)this.findViewById(R.id.seekBarItemMusicScoreX);
        itemMusicScoreXSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar view, int value, boolean arg2) {
				value += 50;
				mSettings.ItemMusicScoreFontSizeX = value/100.0f;
				FileReader.saveAppearanceSettings(TextSizeSetting.this, mSettings);
				((TextView)TextSizeSetting.this.findViewById(R.id.textItemMusicScoreX)).setText(String.valueOf(value)+"%");
				resetPreview();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			});
        itemMusicScoreXSeeker.setProgress((int)(mSettings.ItemMusicScoreFontSizeX*100)-50);

	}

	@Override
	public void onResume()
	{
        super.onResume();
        ActivitySetting.setTitleBarShown(this.findViewById(R.id.titleBar));
		FileReader.requestAd((LinearLayout)this.findViewById(R.id.adContainer), this);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        setContentView(R.layout.activity_text_size_setting);
        
        initialize();
    }
}
