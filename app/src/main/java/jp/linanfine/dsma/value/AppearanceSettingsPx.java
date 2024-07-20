package jp.linanfine.dsma.value;

import android.content.Context;

public class AppearanceSettingsPx extends AppearanceSettings {

	public AppearanceSettingsSp toSp(Context context)
	{
		float density = context.getResources().getDisplayMetrics().scaledDensity;
		AppearanceSettingsSp ret = new AppearanceSettingsSp();
		ret.ShowMaxCombo = this.ShowMaxCombo;
		ret.ShowScore = this.ShowScore;
		ret.ShowFlareRank = this.ShowFlareRank;
		ret.ShowDanceLevel = this.ShowDanceLevel;
		ret.ShowPlayCount = this.ShowPlayCount;
		ret.ShowClearCount = this.ShowClearCount;
		ret.ShowComments = this.ShowComments;
		ret.ShowFullScreen = this.ShowFullScreen;
		ret.ShowTitleBar = this.ShowTitleBar;
		ret.CategoryTopFontSize = this.CategoryTopFontSize/density;
		ret.CategorySubItemFontSize = this.CategorySubItemFontSize/density;
		ret.ItemMusicLevelFontSize = this.ItemMusicLevelFontSize/density;
		ret.ItemMusicNameFontSize = this.ItemMusicNameFontSize/density;
		ret.ItemMusicRankFontSize = this.ItemMusicRankFontSize/density;
		ret.ItemMusicScoreFontSize = this.ItemMusicScoreFontSize/density;
		ret.ItemMusicLevelFontSizeX = this.ItemMusicLevelFontSizeX;
		ret.ItemMusicNameFontSizeX = this.ItemMusicNameFontSizeX;
		ret.ItemMusicRankFontSizeX = this.ItemMusicRankFontSizeX;
		ret.ItemMusicScoreFontSizeX = this.ItemMusicScoreFontSizeX;
		return ret;
	}

}
