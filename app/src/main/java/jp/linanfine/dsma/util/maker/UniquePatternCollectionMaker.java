package jp.linanfine.dsma.util.maker;

import jp.linanfine.dsma.value.MusicDataAdapterArguments;
import jp.linanfine.dsma.value.adapter.MusicDataAdapter;
import jp.linanfine.dsma.value.collection.UniquePatternCollection;
import android.os.AsyncTask;

public class UniquePatternCollectionMaker extends AsyncTask<MusicDataAdapterArguments, Void, UniquePatternCollection> {
	
	private MusicDataAdapter mTarget;
	
	public UniquePatternCollectionMaker(MusicDataAdapter target)
	{
		mTarget = target;
	}

	@Override
	protected UniquePatternCollection doInBackground(MusicDataAdapterArguments... params) 
	{
		return new UniquePatternCollection(params[0]);
	}

    @Override  
    protected void onPostExecute(UniquePatternCollection result) 
    {
    	mTarget.setCollection(result);
     }  
    
}
