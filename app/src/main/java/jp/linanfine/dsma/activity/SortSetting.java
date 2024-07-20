package jp.linanfine.dsma.activity;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.MusicSort;
import jp.linanfine.dsma.value._enum.MusicSortType;
import jp.linanfine.dsma.value._enum.SortOrder;

public class SortSetting extends Activity {

    private int mPagerId;
    private MusicSort mMusicSort;

    Handler mHandler = new Handler();
    View mHandledView;

    public static int getMusicSortTypeNum(MusicSortType sorttype) {
        return
                sorttype == MusicSortType.MusicName ? 0 :
                        sorttype == MusicSortType.Score ? 1 :
                                sorttype == MusicSortType.Rank ? 2 :
                                        sorttype == MusicSortType.FullComboType ? 3 :
                                                sorttype == MusicSortType.ComboCount ? 4 :
                                                        sorttype == MusicSortType.PlayCount ? 5 :
                                                                sorttype == MusicSortType.ClearCount ? 6 :
                                                                        sorttype == MusicSortType.Difficulty ? 7 :
                                                                                sorttype == MusicSortType.Pattern ? 8 :
                                                                                        sorttype == MusicSortType.SPDP ? 9 :
                                                                                                sorttype == MusicSortType.ID ? 10 :
                                                                                                        sorttype == MusicSortType.RivalScore ? 11 :
                                                                                                                sorttype == MusicSortType.RivalRank ? 12 :
                                                                                                                        sorttype == MusicSortType.RivalFullComboType ? 13 :
                                                                                                                                sorttype == MusicSortType.RivalComboCount ? 14 :
                                                                                                                                        sorttype == MusicSortType.RivalScoreDifference ? 15 :
                                                                                                                                                sorttype == MusicSortType.RivalScoreDifferenceAbs ? 16 :
                                                                                                                                                        sorttype == MusicSortType.BpmMax ? 17 :
                                                                                                                                                                sorttype == MusicSortType.BpmMin ? 18 :
                                                                                                                                                                        sorttype == MusicSortType.BpmAve ? 19 :
                                                                                                                                                                                sorttype == MusicSortType.SeriesTitle ? 20 :
                                                                                                                                                                                        0;
    }

    public static MusicSortType getMusicSortType(int typenum) {
        return
                typenum == 0 ? MusicSortType.MusicName :
                        typenum == 1 ? MusicSortType.Score :
                                typenum == 2 ? MusicSortType.Rank :
                                        typenum == 3 ? MusicSortType.FullComboType :
                                                typenum == 4 ? MusicSortType.ComboCount :
                                                        typenum == 5 ? MusicSortType.PlayCount :
                                                                typenum == 6 ? MusicSortType.ClearCount :
                                                                        typenum == 7 ? MusicSortType.Difficulty :
                                                                                typenum == 8 ? MusicSortType.Pattern :
                                                                                        typenum == 9 ? MusicSortType.SPDP :
                                                                                                typenum == 10 ? MusicSortType.ID :
                                                                                                        typenum == 11 ? MusicSortType.RivalScore :
                                                                                                                typenum == 12 ? MusicSortType.RivalRank :
                                                                                                                        typenum == 13 ? MusicSortType.RivalFullComboType :
                                                                                                                                typenum == 14 ? MusicSortType.RivalComboCount :
                                                                                                                                        typenum == 15 ? MusicSortType.RivalScoreDifference :
                                                                                                                                                typenum == 16 ? MusicSortType.RivalScoreDifferenceAbs :
                                                                                                                                                        typenum == 17 ? MusicSortType.BpmMax :
                                                                                                                                                                typenum == 18 ? MusicSortType.BpmMin :
                                                                                                                                                                        typenum == 19 ? MusicSortType.BpmAve :
                                                                                                                                                                                typenum == 20 ? MusicSortType.SeriesTitle :
                                                                                                                                                                                        MusicSortType.MusicName;
    }

    private void save() {

        FileReader.saveMusicSortName(this, mPagerId, ((TextView) SortSetting.this.findViewById(R.id.tabName)).getText().toString());

        int typenum;
        typenum = ((Spinner) SortSetting.this.findViewById(R.id.sort1type)).getSelectedItemPosition();
        mMusicSort._1stType = getMusicSortType(typenum);
        mMusicSort._1stOrder = ((RadioButton) SortSetting.this.findViewById(R.id.sort1a)).isChecked() ? SortOrder.Ascending : SortOrder.Desending;
        typenum = ((Spinner) SortSetting.this.findViewById(R.id.sort2type)).getSelectedItemPosition();
        mMusicSort._2ndType = getMusicSortType(typenum);
        mMusicSort._2ndOrder = ((RadioButton) SortSetting.this.findViewById(R.id.sort2a)).isChecked() ? SortOrder.Ascending : SortOrder.Desending;
        typenum = ((Spinner) SortSetting.this.findViewById(R.id.sort3type)).getSelectedItemPosition();
        mMusicSort._3rdType = getMusicSortType(typenum);
        mMusicSort._3rdOrder = ((RadioButton) SortSetting.this.findViewById(R.id.sort3a)).isChecked() ? SortOrder.Ascending : SortOrder.Desending;
        typenum = ((Spinner) SortSetting.this.findViewById(R.id.sort4type)).getSelectedItemPosition();
        mMusicSort._4thType = getMusicSortType(typenum);
        mMusicSort._4thOrder = ((RadioButton) SortSetting.this.findViewById(R.id.sort4a)).isChecked() ? SortOrder.Ascending : SortOrder.Desending;
        typenum = ((Spinner) SortSetting.this.findViewById(R.id.sort5type)).getSelectedItemPosition();
        mMusicSort._5thType = getMusicSortType(typenum);
        mMusicSort._5thOrder = ((RadioButton) SortSetting.this.findViewById(R.id.sort5a)).isChecked() ? SortOrder.Ascending : SortOrder.Desending;
        FileReader.saveMusicSort(this, mPagerId, mMusicSort);

        setItemStatuses();
    }

    private void setItemStatuses() {
        mMusicSort = FileReader.readMusicSort(this, mPagerId);
        int sorttypenum;
        sorttypenum = getMusicSortTypeNum(mMusicSort._1stType);
        ((Spinner) this.findViewById(R.id.sort1type)).setSelection(sorttypenum);
        ((RadioButton) this.findViewById(R.id.sort1a)).setChecked(mMusicSort._1stOrder == SortOrder.Ascending);
        ((RadioButton) this.findViewById(R.id.sort1d)).setChecked(mMusicSort._1stOrder == SortOrder.Desending);
        sorttypenum = getMusicSortTypeNum(mMusicSort._2ndType);
        ((Spinner) this.findViewById(R.id.sort2type)).setSelection(sorttypenum);
        ((RadioButton) this.findViewById(R.id.sort2a)).setChecked(mMusicSort._2ndOrder == SortOrder.Ascending);
        ((RadioButton) this.findViewById(R.id.sort2d)).setChecked(mMusicSort._2ndOrder == SortOrder.Desending);
        sorttypenum = getMusicSortTypeNum(mMusicSort._3rdType);
        ((Spinner) this.findViewById(R.id.sort3type)).setSelection(sorttypenum);
        ((RadioButton) this.findViewById(R.id.sort3a)).setChecked(mMusicSort._3rdOrder == SortOrder.Ascending);
        ((RadioButton) this.findViewById(R.id.sort3d)).setChecked(mMusicSort._3rdOrder == SortOrder.Desending);
        sorttypenum = getMusicSortTypeNum(mMusicSort._4thType);
        ((Spinner) this.findViewById(R.id.sort4type)).setSelection(sorttypenum);
        ((RadioButton) this.findViewById(R.id.sort4a)).setChecked(mMusicSort._4thOrder == SortOrder.Ascending);
        ((RadioButton) this.findViewById(R.id.sort4d)).setChecked(mMusicSort._4thOrder == SortOrder.Desending);
        sorttypenum = getMusicSortTypeNum(mMusicSort._5thType);
        ((Spinner) this.findViewById(R.id.sort5type)).setSelection(sorttypenum);
        ((RadioButton) this.findViewById(R.id.sort5a)).setChecked(mMusicSort._5thOrder == SortOrder.Ascending);
        ((RadioButton) this.findViewById(R.id.sort5d)).setChecked(mMusicSort._5thOrder == SortOrder.Desending);
    }

    private void initialize() {
        Intent intent = this.getIntent();
        if (intent == null) {
            return;
        }

        mPagerId = intent.getIntExtra("jp.linanfine.dsma.pagerid", 0);
        intent.putExtra("jp.linanfine.dsma.pagerid", mPagerId);
        this.setResult(RESULT_OK, intent);

        OnItemSelectedListener isl = new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                SortSetting.this.save();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                SortSetting.this.save();
            }
        };
        OnClickListener ccl = v -> SortSetting.this.save();

        ((Spinner) this.findViewById(R.id.sort1type)).setOnItemSelectedListener(isl);
        ((RadioButton) this.findViewById(R.id.sort1a)).setOnClickListener(ccl);
        ((RadioButton) this.findViewById(R.id.sort1d)).setOnClickListener(ccl);
        ((Spinner) this.findViewById(R.id.sort2type)).setOnItemSelectedListener(isl);
        ((RadioButton) this.findViewById(R.id.sort2a)).setOnClickListener(ccl);
        ((RadioButton) this.findViewById(R.id.sort2d)).setOnClickListener(ccl);
        ((Spinner) this.findViewById(R.id.sort3type)).setOnItemSelectedListener(isl);
        ((RadioButton) this.findViewById(R.id.sort3a)).setOnClickListener(ccl);
        ((RadioButton) this.findViewById(R.id.sort3d)).setOnClickListener(ccl);
        ((Spinner) this.findViewById(R.id.sort4type)).setOnItemSelectedListener(isl);
        ((RadioButton) this.findViewById(R.id.sort4a)).setOnClickListener(ccl);
        ((RadioButton) this.findViewById(R.id.sort4d)).setOnClickListener(ccl);
        ((Spinner) this.findViewById(R.id.sort5type)).setOnItemSelectedListener(isl);
        ((RadioButton) this.findViewById(R.id.sort5a)).setOnClickListener(ccl);
        ((RadioButton) this.findViewById(R.id.sort5d)).setOnClickListener(ccl);

        setItemStatuses();

        ((TextView) SortSetting.this.findViewById(R.id.tabName)).setText(FileReader.readMusicSortName(this, mPagerId));
        View editTabName = this.findViewById(R.id.editTabName);
        editTabName.setOnClickListener(view -> {
            //テキスト入力を受け付けるビューを作成します。
            final EditText editView = (EditText) SortSetting.this.getLayoutInflater().inflate(R.layout.view_singleline_edit_text, null).findViewById(R.id.editText);
            editView.setText(((TextView) SortSetting.this.findViewById(R.id.tabName)).getText());
            editView.setOnFocusChangeListener((view1, focused) -> {
                if (focused) {
                    mHandledView = view1;
                    mHandler.post(() -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                    });
                }
            });
            new AlertDialog.Builder(SortSetting.this)
                    .setIcon(drawable.ic_dialog_info)
                    .setTitle(SortSetting.this.getResources().getString(R.string.edit_sort_name))
                    //setViewにてビューを設定します。
                    .setView(editView)
                    .setPositiveButton("OK", (dialog, whichButton) -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                        ((TextView) SortSetting.this.findViewById(R.id.tabName)).setText(editView.getText());
                        SortSetting.this.save();
                    })
                    .setNegativeButton("Cancel", (dialog, whichButton) -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                    })
                    .show();
        });

        View deleteFilter = this.findViewById(R.id.delete);
        if (mPagerId == 0) deleteFilter.setVisibility(View.GONE);
        deleteFilter.setOnClickListener(view -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SortSetting.this);
            // アラートダイアログのタイトルを設定します
            alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle(getResources().getString(R.string.filter_setting_delete));
            // アラートダイアログのメッセージを設定します
            alertDialogBuilder.setMessage(getResources().getString(R.string.filter_setting_deleteconferm));
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings_global____ok),
                    (dialog, which) -> {
                        FileReader.deleteMusicSort(SortSetting.this, mPagerId);
                        FileReader.saveActiveMusicSort(SortSetting.this, mPagerId - 1);
                        SortSetting.this.finish();
                    });
            // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.strings_global____cancel),
                    (dialog, which) -> {
                    });
            // アラートダイアログのキャンセルが可能かどうかを設定します
            alertDialogBuilder.setCancelable(true);
            AlertDialog alertDialog = alertDialogBuilder.create();
            // アラートダイアログを表示します
            alertDialog.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        FileReader.requestAd((LinearLayout) this.findViewById(R.id.adContainer), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        setContentView(R.layout.activity_sort);

        initialize();
    }
}
