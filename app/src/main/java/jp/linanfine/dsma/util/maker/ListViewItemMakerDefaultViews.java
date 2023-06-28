package jp.linanfine.dsma.util.maker;

import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.value.ListViewItemArguments;
import jp.linanfine.dsma.R;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewItemMakerDefaultViews extends ListViewItemMaker {

	private TextView     mComment;
	private LinearLayout mRivalData1;
	private LinearLayout mRivalData2;
	private TextView     mRivalDifference;
	private TextView     mRivalName;
	private TextView     mRivalScore;
	private TextView     mWidthSetter;
	private TextView     mHeightSetterRival;
	private TextView     mWidthSetterRank;
	private TextView     mRankRival;
	private TextView     mMusicName;
	private TextView     mMusicId;
	private TextView     mPatternTypeS;
	private TextView     mPatternTypeD1;
	private TextView     mPatternTypeD2;
	private TextView     mLevel;
	private TextView     mScore;
	private TextView     mRank;
	private TextView     mHeightSetter;
	private TextView     mShockArrow;

	public ListViewItemMakerDefaultViews(View convertView, AppearanceSettingsSp appearance) 
	{
		super(convertView, appearance);
	}

    @Override  
    public void onPreExecute()
    {
    	addToList();
    	mComment = (TextView)mConvertView.findViewById(R.id.comment);
    	mRivalData1 = (LinearLayout)mConvertView.findViewById(R.id.rivalData);
    	mRivalData2 = (LinearLayout)mConvertView.findViewById(R.id.rivalData2);
   	 	mRivalDifference = (TextView)mConvertView.findViewById(R.id.rivalDifference);
   	 	mRivalName = (TextView)mConvertView.findViewById(R.id.rivalName);
		mRivalScore = (TextView)mConvertView.findViewById(R.id.musicScoreRival);
		mWidthSetter = ((TextView)mConvertView.findViewById(R.id.widthSetterScore));
		mHeightSetterRival = ((TextView)mConvertView.findViewById(R.id.heightSetterRival));
		mWidthSetterRank = ((TextView)mConvertView.findViewById(R.id.widthSetterRank));
		mRankRival = (TextView)mConvertView.findViewById(R.id.musicRankRival);
		mMusicName = (TextView)mConvertView.findViewById(R.id.musicName);
		mMusicId = (TextView)mConvertView.findViewById(R.id.musicId);
		mPatternTypeS = (TextView)mConvertView.findViewById(R.id.patternTypeS);
		mPatternTypeD1 = (TextView)mConvertView.findViewById(R.id.patternTypeD);
		mPatternTypeD2 = (TextView)mConvertView.findViewById(R.id.patternTypeD2);
		mLevel = (TextView)mConvertView.findViewById(R.id.musicLevel);
		mScore = (TextView)mConvertView.findViewById(R.id.musicScore);
		mRank = (TextView)mConvertView.findViewById(R.id.musicRank);
		mHeightSetter = (TextView)mConvertView.findViewById(R.id.heightSetter);
		mShockArrow = (TextView)mConvertView.findViewById(R.id.shockArrow);
		//mRivalData1.setVisibility(View.GONE);
		//mRivalData2.setVisibility(View.GONE);
		//mRivalDifference.setText("");
		//mRivalScore.setText("0,000,000");
		//mRankRival.setText("");
		//mMusicName.setText("");
		//mMusicId.setText("0");
		//mPatternTypeS.setBackgroundColor(0x00000000);
		//mPatternTypeD1.setBackgroundColor(0x00000000);
		//mPatternTypeD2.setBackgroundColor(0x00000000);
		//mLevel.setText("");
		//mScore.setText("0,000,000");
		//mRank.setText("");
		
		mRivalName.setTextSize(mAppearance.ItemMusicScoreFontSize);
		mRivalName.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
		mRivalDifference.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
		mRankRival.setTextScaleX(mAppearance.ItemMusicRankFontSizeX);
		mRivalScore.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
		
		mRivalDifference.setTextSize(mAppearance.ItemMusicScoreFontSize);
		mRivalScore.setTextSize(mAppearance.ItemMusicScoreFontSize);
		//mRankRival.setTextSize(mAppearance.ItemMusicRankFontSize);
		mHeightSetterRival.setTextSize(mAppearance.ItemMusicRankFontSize);
		
		mWidthSetter.setTextSize(mAppearance.ItemMusicScoreFontSize);
		mWidthSetter.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
		mWidthSetterRank.setTextSize(mAppearance.ItemMusicRankFontSize);
		mWidthSetterRank.setTextScaleX(mAppearance.ItemMusicRankFontSizeX);
		
		mMusicName.setTextScaleX(mAppearance.ItemMusicNameFontSizeX);
		mLevel.setTextScaleX(mAppearance.ItemMusicLevelFontSizeX);
		
		mComment.setTextSize(mAppearance.ItemMusicNameFontSize);
		mComment.setTextScaleX(mAppearance.ItemMusicNameFontSizeX);
		
		mMusicName.setTextSize(mAppearance.ItemMusicNameFontSize);
		//mMusicId.setTextSize(mAppearance.ItemMusicScoreFontSize);
		mPatternTypeS.setHeight((int)(mAppearance.ItemMusicLevelFontSize/2.5));
		mPatternTypeD1.setHeight((int)(mAppearance.ItemMusicLevelFontSize/2.5));
		mPatternTypeD2.setHeight((int)(mAppearance.ItemMusicLevelFontSize/2.5));
		mPatternTypeS.setWidth((int)(mAppearance.ItemMusicLevelFontSize*mAppearance.ItemMusicLevelFontSizeX/4.0f));
		mPatternTypeD1.setWidth((int)(mAppearance.ItemMusicLevelFontSize*mAppearance.ItemMusicLevelFontSizeX/4.0f));
		mPatternTypeD2.setWidth((int)(mAppearance.ItemMusicLevelFontSize*mAppearance.ItemMusicLevelFontSizeX/4.0f));
		mLevel.setTextSize(mAppearance.ItemMusicLevelFontSize);
		mShockArrow.setTextSize((int)(mAppearance.ItemMusicLevelFontSize*mAppearance.ItemMusicLevelFontSizeX));
		//mLevel.setWidth((int)(mAppearance.ItemMusicLevelFontSize*mAppearance.ItemMusicLevelFontSizeX/2.0f));
		
		mScore.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
		mRank.setTextScaleX(mAppearance.ItemMusicRankFontSizeX);
		
		mScore.setTextSize(mAppearance.ItemMusicScoreFontSize);
		//mRank.setTextSize(mAppearance.ItemMusicRankFontSize);
		mHeightSetter.setTextSize(mAppearance.ItemMusicRankFontSize);
		
		mShockArrow.setVisibility(View.GONE);
		//mComment.setVisibility(View.GONE);
    }

    @Override  
    public void onPostExecute(ListViewItemArguments result) 
    {
    	if(result == null)
    	{
    		mComment.setVisibility(View.GONE);
			mRivalData1.setVisibility(View.GONE);
			mRivalData2.setVisibility(View.GONE);
			 mPatternTypeS.setBackgroundColor(0x00000000);
			 mPatternTypeD1.setBackgroundColor(0x00000000);
			 mPatternTypeD2.setBackgroundColor(0x00000000);
			 mMusicName.setText("");
			 mMusicId.setText("");
			 mRank.setText("");
			 mLevel.setText("");
			 mScore.setText("");
   		 	removeFromList();
    		return;
    	}
    	
    	if(mAppearance.ShowComments && result.Comment != null)
    	{
    		//mComment.setVisibility(View.VISIBLE);
    		//mComment.setText(result.Comment);
    	}
    	else
    	{
    		mComment.setVisibility(View.GONE);
    	}
    	
		 if(result.RivalName == null)
		 {
			mRivalData1.setVisibility(View.GONE);
			mRivalData2.setVisibility(View.GONE);
		 }
		 else
		 {
			 
			 mRivalData1.setVisibility(View.VISIBLE);
			 mRivalData2.setVisibility(View.VISIBLE);
			 
			 mRankRival.setTextSize(result.RankTextSizeRival);
			 
			 mRankRival.setText(result.RankRival);
			 mRivalScore.setText(result.ScoreRival);
			 mRivalName.setText(result.RivalName+":");
			 mRivalDifference.setText(result.ScoreDifference);
			 
		 }
		 
		 {
			 
			 mLevel.setTextColor(result.LevelTextColor);
			 mPatternTypeS.setBackgroundColor(result.PatternSingleBackColor);
			 mPatternTypeD1.setBackgroundColor(result.PatternDoubleBackColor);
			 mPatternTypeD2.setBackgroundColor(result.PatternDoubleBackColor);
			 
			 mRank.setTextSize(result.RankTextSize);
			 
			 mMusicName.setText(result.Name);
			 mMusicId.setText(result.Id);
			 mRank.setText(result.Rank);
			 mLevel.setText(result.Level);
			 mScore.setText(result.Score);
			 
			 if(result.ShockArrowExists)
			 {
				 mShockArrow.setVisibility(View.VISIBLE);
			 }
			 
		 }
		 
		 removeFromList();
		 
    }

}
