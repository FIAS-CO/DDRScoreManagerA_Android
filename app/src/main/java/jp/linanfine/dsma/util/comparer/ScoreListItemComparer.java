package jp.linanfine.dsma.util.comparer;

import java.util.Comparator;

import jp.linanfine.dsma.value._enum.ScoreListCompareMode;
import jp.linanfine.dsma.value._enum.SortOrder;
import jp.linanfine.dsma.view.ScoreListItem;

public class ScoreListItemComparer implements Comparator<ScoreListItem> {
	
	private SortOrder mSortOrder = SortOrder.Ascending;
	private ScoreListCompareMode mCompareMode = ScoreListCompareMode.Id;

	public void setSortOrder(SortOrder order)
	{
		mSortOrder = order;
	}

	public void setCompareMode(ScoreListCompareMode mode)
	{
		mCompareMode = mode;
	}

	public int compare(ScoreListItem p, ScoreListItem m) {
		int order = (mSortOrder==SortOrder.Ascending?1:-1);
		switch(mCompareMode)
		{
			case Id:
				return order*(p.mMusicData.Id - m.mMusicData.Id);
			case Name:
				return order*(p.mMusicData.Name.compareTo(m.mMusicData.Name));
		}
		return order;
	}

}
