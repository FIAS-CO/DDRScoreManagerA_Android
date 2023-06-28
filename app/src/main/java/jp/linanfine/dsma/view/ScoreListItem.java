package jp.linanfine.dsma.view;

import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.R;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ScoreListItem extends View {
	
	public MusicData mMusicData;
	public View mItemView;
	
	public View getView()
	{
		return mItemView;
	}
	
	public void setMusicData(MusicData musicData)
	{
		mMusicData = musicData;
		((TextView)mItemView.findViewById(R.id.musicId)).setText(Integer.toString(musicData.Id));
		((TextView)mItemView.findViewById(R.id.musicName)).setText(musicData.Name);
	}
	
	public ScoreListItem(Context activity) {
		super(activity);
		mItemView = ((Activity)activity).getLayoutInflater().inflate(R.layout.view_score_list_item, null);
	}

}
