package jp.linanfine.dsma.util.debug;

import android.content.Context;
import android.widget.Toast;

public class DebugOutput 
{
	
	private static boolean DEBUG_MODE = false;

	private static DebugOutput mInstance = null;
	private static Context mContext = null;
	
	private DebugOutput(){}
	
	public static DebugOutput getInstance(Context context)
	{
		if(mInstance == null)
		{
			mInstance = DEBUG_MODE?new DebugMode():new DebugOutput();
		}
		mInstance.mContext = context;
		return mInstance;
	}
	
	public void ToastLong(CharSequence text){}
	
	private static class DebugMode extends DebugOutput 
	{
		public void ToastLong(CharSequence text) {
			Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
		}
	}
	
}
