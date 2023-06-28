package jp.linanfine.dsma.util.comparer;

import java.util.Comparator;

import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value._enum.MusicDataCompareMode;
import jp.linanfine.dsma.value._enum.SortOrder;

public class MusicDataComparer implements Comparator<MusicData> {
	
	private SortOrder mSortOrder = SortOrder.Ascending;
	private MusicDataCompareMode mCompareMode = MusicDataCompareMode.Id;

	public void setSortOrder(SortOrder order)
	{
		mSortOrder = order;
	}

	public void setCompareMode(MusicDataCompareMode mode)
	{
		mCompareMode = mode;
	}

	public int compare(MusicData p, MusicData m) {
		int order = (mSortOrder==SortOrder.Ascending?1:-1);
		switch(mCompareMode)
		{
			case Id:
				return order*(p.Id - m.Id);
			case Name:
				return order*(p.Name.compareToIgnoreCase(m.Name));
		}
		return order;
	}

}
