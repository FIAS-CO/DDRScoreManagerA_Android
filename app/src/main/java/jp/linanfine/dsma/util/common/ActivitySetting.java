package jp.linanfine.dsma.util.common;

import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

public class ActivitySetting {
	public static void setFullScreen(Activity activity)
	{
		AppearanceSettingsSp app = FileReader.readAppearanceSettings(activity);
		if(app.ShowFullScreen)
		{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		else
		{
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
	public static void setTitleBarShown(Activity activity, View titleBarView)
	{
		//AppearanceSettingsSp app = FileReader.readAppearanceSettings(activity);
		//if(!app.ShowTitleBar)
		//{
		//	titleBarView.setVisibility(View.GONE);
		//}
		//else
		{
			titleBarView.setVisibility(View.VISIBLE);
		}
	}
}
