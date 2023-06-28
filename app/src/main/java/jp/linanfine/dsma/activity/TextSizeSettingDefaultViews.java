package jp.linanfine.dsma.activity;

import jp.linanfine.dsma.R;
import android.view.View;
import android.widget.TextView;

public class TextSizeSettingDefaultViews extends TextSizeSetting {

	protected void resetPreview()
	{
		mPreview.removeView(mPreviewMain);
        mPreviewMain = this.getLayoutInflater().inflate(R.layout.view_text_size_preview, null);
        mPreview.addView(mPreviewMain);
		((TextView)mPreviewMain.findViewById(R.id.categoryTop)).setTextSize(mSettings.CategoryTopFontSize);
		((TextView)mPreviewMain.findViewById(R.id.categorySubItem)).setTextSize(mSettings.CategorySubItemFontSize);
		((TextView)mPreviewMain.findViewById(R.id.musicLevel)).setTextSize(mSettings.ItemMusicLevelFontSize);
		((TextView)mPreviewMain.findViewById(R.id.musicLevel)).setTextScaleX(mSettings.ItemMusicLevelFontSizeX);
		//((TextView)mPreviewMain.findViewById(R.id.musicLevel)).setWidth((int)(mSettings.ItemMusicLevelFontSize*mSettings.ItemMusicLevelFontSizeX/2.0f));
		((TextView)mPreviewMain.findViewById(R.id.patternTypeD)).setHeight((int)(mSettings.ItemMusicLevelFontSize/2.5));
		((TextView)mPreviewMain.findViewById(R.id.patternTypeS)).setHeight((int)(mSettings.ItemMusicLevelFontSize/2.5));
		((TextView)mPreviewMain.findViewById(R.id.patternTypeD2)).setHeight((int)(mSettings.ItemMusicLevelFontSize/2.5));
		((TextView)mPreviewMain.findViewById(R.id.patternTypeD)).setWidth((int)(mSettings.ItemMusicLevelFontSize*mSettings.ItemMusicLevelFontSizeX/4.0f));
		((TextView)mPreviewMain.findViewById(R.id.patternTypeS)).setWidth((int)(mSettings.ItemMusicLevelFontSize*mSettings.ItemMusicLevelFontSizeX/4.0f));
		((TextView)mPreviewMain.findViewById(R.id.patternTypeD2)).setWidth((int)(mSettings.ItemMusicLevelFontSize*mSettings.ItemMusicLevelFontSizeX/4.0f));
		((TextView)mPreviewMain.findViewById(R.id.musicName)).setTextSize(mSettings.ItemMusicNameFontSize);
		((TextView)mPreviewMain.findViewById(R.id.musicName)).setTextScaleX(mSettings.ItemMusicNameFontSizeX);
		((TextView)mPreviewMain.findViewById(R.id.heightSetter)).setTextSize(mSettings.ItemMusicRankFontSize);
		((TextView)mPreviewMain.findViewById(R.id.musicRank)).setTextSize(mSettings.ItemMusicRankFontSize);
		((TextView)mPreviewMain.findViewById(R.id.musicRank)).setTextScaleX(mSettings.ItemMusicRankFontSizeX);
		((TextView)mPreviewMain.findViewById(R.id.musicScore)).setTextSize(mSettings.ItemMusicScoreFontSize);
		((TextView)mPreviewMain.findViewById(R.id.musicScore)).setTextScaleX(mSettings.ItemMusicScoreFontSizeX);
		//((TextView)mPreviewMain.findViewById(R.id.musicId)).setTextSize(mSettings.ItemMusicScoreFontSize);
		((TextView)mPreviewMain.findViewById(R.id.musicId)).setVisibility(View.GONE);
	}

}
