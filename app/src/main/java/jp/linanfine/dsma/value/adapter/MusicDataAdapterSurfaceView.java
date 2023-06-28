package jp.linanfine.dsma.value.adapter;

import android.view.View;
import android.view.ViewGroup;
import jp.linanfine.dsma.activity.ScoreList;
import jp.linanfine.dsma.util.maker.ListViewItemMaker;
import jp.linanfine.dsma.util.maker.ListViewItemMakerDefaultViews;
import jp.linanfine.dsma.util.maker.ListViewItemMakerSurfaceView;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.value.MusicDataAdapterArguments;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.R;

public class MusicDataAdapterSurfaceView extends MusicDataAdapter {

	public MusicDataAdapterSurfaceView(ScoreList scoreListActivity, AppearanceSettingsSp appearance, MusicDataAdapterArguments args) 
	{
		super(scoreListActivity, appearance, args);
	}

	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 
		 // 特定の行(position)のデータを得る
		 UniquePattern pat = (UniquePattern)getItem(position);
		 
		 // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		 if (null == convertView) {
			 convertView = layoutInflater_.inflate(R.layout.view_score_list_item_sfv, null);
		 }
		 
		 ListViewItemMaker lvim = new ListViewItemMakerSurfaceView(convertView, mAppearance, mArguments);
		 if(mArguments.UseAsyncDraw)
		 {
			 lvim.execute(pat);
		 }
		 else
		 {
			 lvim.onPreExecute();
			 lvim.onPostExecute(lvim.doInBackground(pat));
		 }
		 
		 return convertView;
	 }

}
