package jp.linanfine.dsma.util.common;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.core.view.WindowCompat;

import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.AppearanceSettingsSp;

public class ActivitySetting {
    public static void setFullScreen(Activity activity) {
        AppearanceSettingsSp app = FileReader.readAppearanceSettings(activity);
        if (app.ShowFullScreen) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // SDK 35 以降 Edge-to-Edgeが強制になったのでステータスバー、ナビゲーションバーで見切れないよう修正
        // 上のフルスクリーン設定のあとに実行しないと見切れが解消されないのでここでやってます
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.getWindow().setDecorFitsSystemWindows(true);
        } else {
            WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), true);
        }
    }

    // TODO: このメソッド消してもいいのでは？常にバー表示してるので。
    public static void setTitleBarShown(View titleBarView) {
        titleBarView.setVisibility(View.VISIBLE);

    }
}
