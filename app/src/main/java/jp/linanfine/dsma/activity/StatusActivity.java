package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.dialog.DialogFromGateStatus;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.StatusData;

public class StatusActivity extends Activity {

    private int debugCount;
    private View showstatisticstable;

    private void initialize() {
        LinearLayout mgrw = this.findViewById(R.id.mgr);
        StatusData mStatus = FileReader.readStatusData(this);
        mgrw.removeAllViews();
        MgrView mMgr = new MgrView(this);
        mgrw.addView(mMgr);

        this.findViewById(R.id.refresh).setOnClickListener(v -> showDialogFromGateStatus());

        this.findViewById(R.id.showstatisticstable).setOnClickListener(v -> openStatisticsTable());

        showstatisticstable = this.findViewById(R.id.showstatisticstable);
        showstatisticstable.setVisibility(View.GONE);

        debugCount = -9;
        this.findViewById(R.id.sou).setOnClickListener(v -> {
            ++debugCount;
            if (debugCount > 0) {
                showstatisticstable.setVisibility(View.VISIBLE);
            }
        });

        ((TextView) this.findViewById(R.id.dancerName)).setText(mStatus.DancerName);
        ((TextView) this.findViewById(R.id.ddrCode)).setText(mStatus.DdrCode);
        ((TextView) this.findViewById(R.id.area)).setText(mStatus.Todofuken);
        ((TextView) this.findViewById(R.id.enjoyLevel)).setText(String.valueOf(mStatus.EnjoyLevel));
        ((TextView) this.findViewById(R.id.enjoyLevelNext)).setText(String.valueOf(mStatus.EnjoyLevelNextExp));
        ((TextView) this.findViewById(R.id.playCount)).setText(String.valueOf(mStatus.PlayCount));
        ((TextView) this.findViewById(R.id.lastPlay)).setText(mStatus.LastPlay);
        ((TextView) this.findViewById(R.id.spPlayCount)).setText(String.valueOf(mStatus.PlayCountSingle));
        ((TextView) this.findViewById(R.id.spLastPlay)).setText(mStatus.LastPlaySingle);
        ((TextView) this.findViewById(R.id.dpPlayCount)).setText(String.valueOf(mStatus.PlayCountDouble));
        ((TextView) this.findViewById(R.id.dpLastPlay)).setText(mStatus.LastPlayDouble);
    }

    private void openStatisticsTable() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.StatisticsTable");

        startActivityForResult(intent, 1);
    }

    private DialogFromGateStatus mFromGateStatus = null;

    private void showDialogFromGateStatus() {
        if (mFromGateStatus != null) {
            mFromGateStatus.cancel();
            mFromGateStatus = null;
        }

        mFromGateStatus = new DialogFromGateStatus(StatusActivity.this);

        mFromGateStatus.setArguments(new AlertDialog.Builder(StatusActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(StatusActivity.this.getResources().getString(R.string.dialog_gettin_status))
                        .setView(mFromGateStatus.getView())
                        .setCancelable(false)
                        .setNegativeButton(StatusActivity.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                            if (mFromGateStatus != null) {
                                mFromGateStatus.cancel();
                                mFromGateStatus = null;
                            }
                        })
                        .setOnCancelListener(arg0 -> StatusActivity.this.initialize())
                        .show()
                , null);

        mFromGateStatus.start();
    }

    private void userActionShowSystemMenu() {
        //選択項目を準備する。
        ArrayList<String> str_items = new ArrayList<>();
        str_items.add(getResources().getString(R.string.menu_refresh_status));

        new AlertDialog.Builder(StatusActivity.this)
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                            if (which == 0) {
                                showDialogFromGateStatus();
                            }
                        }
                ).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            userActionShowSystemMenu();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySetting.setTitleBarShown(this, this.findViewById(R.id.titleBar));
        FileReader.requestAd(this.findViewById(R.id.adContainer), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = this.getLayoutInflater().inflate(R.layout.activity_status, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        this.setContentView(view);

        initialize();
    }

    public static class MgrView extends View {

        public MgrView(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int mMgrWidth = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(mMgrWidth, mMgrWidth);
        }

        private final Paint mPaint = new Paint();
        private final Rect mRect = new Rect();
        private final Path mClip = new Path();
        private final Path mSingle = new Path();
        private final Path mDouble = new Path();

        @Override
        public void onDraw(Canvas canvas) {
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DialogFromGateStatus.LoginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                showDialogFromGateStatus();
            }
        }
    }
}
