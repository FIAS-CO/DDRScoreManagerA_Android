package jp.linanfine.dsma.activity;

import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.PatternType;
import jp.linanfine.dsma.R;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CopyFormatList extends Activity {

	 Handler mHandler = new Handler();
	 View mHandledView;

	 String[] mFormats;
	 UniquePattern mTargetPattern;
	 MusicData mTargetMusicData;
	 ScoreData mTargetScoreData;

	 private void initialize()
	 {

	        Intent intent = getIntent();
	        if(intent == null)
	        {
	        	return;
	        }

			 Button cancel = (Button)this.findViewById(R.id.cancel);
			 cancel.setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						CopyFormatList.this.finish();
					}
		        });

			 Button ok = (Button)this.findViewById(R.id.ok);
			 ok.setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						FileReader.saveCopyFormats(CopyFormatList.this, mFormats);
						CopyFormatList.this.finish();
					}
		        });

			 mFormats = FileReader.readCopyFormats(this);

			 mTargetPattern = new UniquePattern();
			 mTargetPattern.Pattern = PatternType.ESP;
			 mTargetMusicData = new MusicData();
			 mTargetMusicData.Difficulty_ESP = 8;
			 mTargetMusicData.Name = "MusicName";
			 mTargetScoreData = new ScoreData();
			 mTargetScoreData.ClearCount = 7;
			 mTargetScoreData.PlayCount = 9;
			 mTargetScoreData.FullComboType = FullComboType.None;
			 mTargetScoreData.MaxCombo = 34;
			 mTargetScoreData.Rank = MusicRank.Ap;
			 mTargetScoreData.Score = 98760;

			 final EditText format0 = (EditText)this.findViewById(R.id.format0);
			 final TextView preview0 = (TextView)this.findViewById(R.id.preview0);
			 format0.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable arg0) {
					mFormats[0] = format0.getText().toString();
					preview0.setText(TextUtil.textFromCopyFormat(mFormats[0], mTargetPattern, mTargetMusicData, mTargetScoreData));
				}
				public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
				public void onTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
			 });
			 format0.setText(mFormats[0]);
			 final EditText format1 = (EditText)this.findViewById(R.id.format1);
			 final TextView preview1 = (TextView)this.findViewById(R.id.preview1);
			 format1.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable arg0) {
					mFormats[1] = format1.getText().toString();
					preview1.setText(TextUtil.textFromCopyFormat(mFormats[1], mTargetPattern, mTargetMusicData, mTargetScoreData));
				}
				public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
				public void onTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
			 });
			 format1.setText(mFormats[1]);
			 final EditText format2 = (EditText)this.findViewById(R.id.format2);
			 final TextView preview2 = (TextView)this.findViewById(R.id.preview2);
			 format2.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable arg0) {
					mFormats[2] = format2.getText().toString();
					preview2.setText(TextUtil.textFromCopyFormat(mFormats[2], mTargetPattern, mTargetMusicData, mTargetScoreData));
				}
				public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
				public void onTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
			 });
			 format2.setText(mFormats[2]);

	 }

		@Override
		public void onResume()
		{
	        super.onResume();
	        ActivitySetting.setTitleBarShown(this.findViewById(R.id.titleBar));
			FileReader.requestAd((LinearLayout)this.findViewById(R.id.adContainer), this);
		}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        ActivitySetting.setFullScreen(this);
		setContentView(R.layout.activity_copy_format_list);

		initialize();
	}

}
