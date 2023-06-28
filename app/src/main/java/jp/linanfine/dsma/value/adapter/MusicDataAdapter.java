package jp.linanfine.dsma.value.adapter;

import java.util.Iterator;

import jp.linanfine.dsma.activity.ScoreList;
import jp.linanfine.dsma.util.maker.ListViewItemMaker;
import jp.linanfine.dsma.util.maker.ListViewItemMakerDefaultViews;
import jp.linanfine.dsma.util.maker.UniquePatternCollectionMaker;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.value.MusicDataAdapterArguments;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.value.collection.UniquePatternCollection;
import jp.linanfine.dsma.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

abstract public class MusicDataAdapter extends ArrayAdapter<UniquePattern> {
		
	 protected LayoutInflater layoutInflater_;
	 protected UniquePatternCollectionMaker mUpcm;
	 protected MusicDataAdapterArguments mArguments;
	 protected ScoreList mScoreListActivity;
	 protected AppearanceSettingsSp mAppearance;
	 
	 public MusicDataAdapter(ScoreList scoreListActivity, AppearanceSettingsSp appearance, MusicDataAdapterArguments args) {
		 super(scoreListActivity, 0, new UniquePatternCollection());
		 mScoreListActivity = scoreListActivity;
		 mAppearance = appearance;
		 mArguments = args;
		 layoutInflater_ = (LayoutInflater) mScoreListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 mUpcm = new UniquePatternCollectionMaker(this);
		 mUpcm.execute(args);
	 }
	 
	 public void setCollection(UniquePatternCollection collection)
	 {
		 //try
		 //{
		//	 super.addAll(collection);
		 //}
		 //catch(NoSuchMethodError e)
		 {
			 Iterator<UniquePattern> items = collection.iterator();
			 while(items.hasNext())
			 {
				 super.add(items.next());
			 }
		 }
		 mScoreListActivity.actionOfListRefreshCompleted();
	 }

	 abstract public View getView(int position, View convertView, ViewGroup parent);

}
