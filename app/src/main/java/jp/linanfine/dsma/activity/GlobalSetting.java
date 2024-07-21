package jp.linanfine.dsma.activity;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.dialog.DialogFromGate;
import jp.linanfine.dsma.dialog.DialogRefreshMusicList;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.GestureSettings;
import jp.linanfine.dsma.value._enum.PatternType;

public class GlobalSetting extends Activity {

    private boolean mAutoUpdateMusicList;
    private boolean mCloseCategoryOnBackKeyPressed;
    private boolean mUseAsyncDraw;
    private boolean mUseOldStyleDraw;
    private GateSetting mGateSetting;
    private AppearanceSettingsSp mAppearances;
    private GestureSettings mGestures;
    private Spinner mFilterSpinner;

    private Handler mHandler = new Handler();
    private View mHandledView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DialogFromGate.LoginRequestCode) {
            showDialogFromGate();
        }
    }

    private DialogFromGate mFromGate = null;
    private DialogRefreshMusicList mDialogRefreshMusicList = null;

    private void showDialogFromGate() {
        if (mFromGate != null) {
            mFromGate.cancel();
            mFromGate = null;
        }

        mFromGate = new DialogFromGate(GlobalSetting.this);

        mFromGate.setArguments(new AlertDialog.Builder(GlobalSetting.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("-_-")
                        .setView(mFromGate.getView())
                        .setCancelable(false)
                        .setNegativeButton(GlobalSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                            if (mFromGate != null) {
                                mFromGate.cancel();
                                mFromGate = null;
                            }
                        })
                        .show()
                , 1, PatternType.bSP, null, null);

        mFromGate.start();
    }

    int debugCount = -9;

    private void initialize() {
        final Button debug = (Button) this.findViewById(R.id.debug);
        debug.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.StatusActivity");
            startActivity(intent);
        });
        debug.setVisibility(View.GONE);

        this.findViewById(R.id.sou).setOnClickListener(v -> {
            ++debugCount;
            if (debugCount > 0) {
                debug.setVisibility(View.VISIBLE);
            }
        });

        mFilterSpinner = (Spinner) this.findViewById(R.id.filterPage);

        mFilterSpinner.setVisibility(View.GONE);

        mCloseCategoryOnBackKeyPressed = FileReader.readCloseCategoryOnBackKeyPressed(this);
        mAutoUpdateMusicList = FileReader.readAutoUpdateMusicList(this);
        mUseAsyncDraw = FileReader.readUseAsyncDraw(this);
        mUseOldStyleDraw = FileReader.readUseOldStyleDraw(this);
        mAppearances = FileReader.readAppearanceSettings(this);

        mGateSetting = FileReader.readGateSetting(this);
        ((CheckBox) this.findViewById(R.id.fromNewSite)).setChecked(mGateSetting.FromNewSite);
        ((CheckBox) this.findViewById(R.id.overwriteLife4)).setChecked(mGateSetting.OverWriteLife4);
        ((CheckBox) this.findViewById(R.id.overwriteLowerScores)).setChecked(mGateSetting.OverWriteLowerScores);
        ((CheckBox) this.findViewById(R.id.closeCategoryOnBackKeyPressed)).setChecked(mCloseCategoryOnBackKeyPressed);
        ((CheckBox) this.findViewById(R.id.autoUpdateMusicList)).setChecked(mAutoUpdateMusicList);
        ((CheckBox) this.findViewById(R.id.useAsyncDraw)).setChecked(mUseAsyncDraw);
        ((CheckBox) this.findViewById(R.id.useOldStyleDraw)).setChecked(mUseOldStyleDraw);
        ((CheckBox) this.findViewById(R.id.showMaxCombo)).setChecked(mAppearances.ShowMaxCombo);
        ((CheckBox) this.findViewById(R.id.showScore)).setChecked(mAppearances.ShowScore);
        ((CheckBox) this.findViewById(R.id.showDanceLevel)).setChecked(mAppearances.ShowDanceLevel);
        ((CheckBox) this.findViewById(R.id.showPlayCount)).setChecked(mAppearances.ShowPlayCount);
        ((CheckBox) this.findViewById(R.id.showClearCount)).setChecked(mAppearances.ShowClearCount);
        ((CheckBox) this.findViewById(R.id.showComment)).setChecked(mAppearances.ShowComments);
        ((CheckBox) this.findViewById(R.id.fullScreen)).setChecked(mAppearances.ShowFullScreen);
        ((CheckBox) this.findViewById(R.id.titleBarVisible)).setVisibility(View.GONE);
        ((CheckBox) this.findViewById(R.id.titleBarVisible)).setChecked(mAppearances.ShowTitleBar);
        ((TextView) this.findViewById(R.id.setpfc)).setText(TextUtil.getScoreText(mGateSetting.SetPfcScore));

        this.findViewById(R.id.showComment).setVisibility(View.GONE);

        View editPfc = this.findViewById(R.id.editSetPfc);
        editPfc.setOnClickListener(view -> {
            //テキスト入力を受け付けるビューを作成します。
            final EditText editView = (EditText) GlobalSetting.this.getLayoutInflater().inflate(R.layout.view_digit_edit_text, null).findViewById(R.id.editText);
            editView.setText(String.valueOf(mGateSetting.SetPfcScore));
            editView.setOnFocusChangeListener((view1, focused) -> {
                if (focused) {
                    mHandledView = view1;
                    mHandler.post(() -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                    });
                }
            });
            new AlertDialog.Builder(GlobalSetting.this)
                    .setIcon(drawable.ic_dialog_info)
                    .setTitle(GlobalSetting.this.getResources().getString(R.string.dialog_input_score))
                    //setViewにてビューを設定します。
                    .setView(editView)
                    .setPositiveButton(GlobalSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                        Editable text = editView.getText();
                        try {
                            int value = Integer.parseInt(text.toString());
                            if (0 <= value && value <= 1000000) {
                                mGateSetting.SetPfcScore = value;
                                FileReader.saveGateSetting(GlobalSetting.this, mGateSetting);
                                GlobalSetting.this.initialize();
                            }
                        } catch (Exception e) {
                        }
                    })
                    .setNegativeButton(GlobalSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                    })
                    .show();
        });

        Button logoutGate = (Button) this.findViewById(R.id.logout);
        logoutGate.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GateLoginManually");
            startActivity(intent);
        });

        OnCheckedChangeListener ccl = (arg0, arg1) -> {
            mGestures.GestureEnabled = ((CheckBox) GlobalSetting.this.findViewById(R.id.gestureEnableFlick)).isChecked();
            mAutoUpdateMusicList = ((CheckBox) GlobalSetting.this.findViewById(R.id.autoUpdateMusicList)).isChecked();
            mGateSetting.FromNewSite = ((CheckBox) GlobalSetting.this.findViewById(R.id.fromNewSite)).isChecked();
            mGateSetting.OverWriteLife4 = ((CheckBox) GlobalSetting.this.findViewById(R.id.overwriteLife4)).isChecked();
            mGateSetting.OverWriteLowerScores = ((CheckBox) GlobalSetting.this.findViewById(R.id.overwriteLowerScores)).isChecked();
            mAppearances.ShowMaxCombo = ((CheckBox) GlobalSetting.this.findViewById(R.id.showMaxCombo)).isChecked();
            mAppearances.ShowScore = ((CheckBox) GlobalSetting.this.findViewById(R.id.showScore)).isChecked();
            mAppearances.ShowDanceLevel = ((CheckBox) GlobalSetting.this.findViewById(R.id.showDanceLevel)).isChecked();
            mAppearances.ShowPlayCount = ((CheckBox) GlobalSetting.this.findViewById(R.id.showPlayCount)).isChecked();
            mAppearances.ShowClearCount = ((CheckBox) GlobalSetting.this.findViewById(R.id.showClearCount)).isChecked();
            mAppearances.ShowComments = ((CheckBox) GlobalSetting.this.findViewById(R.id.showComment)).isChecked();
            mAppearances.ShowFullScreen = ((CheckBox) GlobalSetting.this.findViewById(R.id.fullScreen)).isChecked();
            mAppearances.ShowTitleBar = ((CheckBox) GlobalSetting.this.findViewById(R.id.titleBarVisible)).isChecked();
            FileReader.saveGateSetting(GlobalSetting.this, mGateSetting);
            FileReader.saveAutoUpdateMusicList(GlobalSetting.this, mAutoUpdateMusicList);
            FileReader.saveCloseCategoryOnBackKeyPressed(GlobalSetting.this, ((CheckBox) GlobalSetting.this.findViewById(R.id.closeCategoryOnBackKeyPressed)).isChecked());
            FileReader.saveAppearanceSettings(GlobalSetting.this, mAppearances);
            FileReader.saveGestureSettings(GlobalSetting.this, mGestures);
            ActivitySetting.setTitleBarShown(GlobalSetting.this, GlobalSetting.this.findViewById(R.id.titleBar));
        };

        ((CheckBox) GlobalSetting.this.findViewById(R.id.gestureEnableFlick)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.fromNewSite)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.overwriteLife4)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.overwriteLowerScores)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.autoUpdateMusicList)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.closeCategoryOnBackKeyPressed)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.showMaxCombo)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.showScore)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.showDanceLevel)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.showPlayCount)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.showClearCount)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.showComment)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.fullScreen)).setOnCheckedChangeListener(ccl);
        ((CheckBox) GlobalSetting.this.findViewById(R.id.titleBarVisible)).setOnCheckedChangeListener(ccl);

        ((CheckBox) GlobalSetting.this.findViewById(R.id.useAsyncDraw)).setOnCheckedChangeListener((arg0, arg1) -> {
            mUseAsyncDraw = ((CheckBox) GlobalSetting.this.findViewById(R.id.useAsyncDraw)).isChecked();
            FileReader.saveUseAsyncDraw(GlobalSetting.this, mUseAsyncDraw);
        });

        ((CheckBox) GlobalSetting.this.findViewById(R.id.useOldStyleDraw)).setOnCheckedChangeListener((arg0, arg1) -> {
            mUseOldStyleDraw = ((CheckBox) GlobalSetting.this.findViewById(R.id.useOldStyleDraw)).isChecked();
            FileReader.saveUseOldStyleDraw(GlobalSetting.this, mUseOldStyleDraw);
            GlobalSetting.this.findViewById(R.id.showMaxCombo).setEnabled(!mUseOldStyleDraw);
            GlobalSetting.this.findViewById(R.id.showScore).setEnabled(!mUseOldStyleDraw);
            GlobalSetting.this.findViewById(R.id.showDanceLevel).setEnabled(!mUseOldStyleDraw);
            GlobalSetting.this.findViewById(R.id.showPlayCount).setEnabled(!mUseOldStyleDraw);
            GlobalSetting.this.findViewById(R.id.showClearCount).setEnabled(!mUseOldStyleDraw);
        });

        Button testSizeSetting = (Button) this.findViewById(R.id.textsizeSetting);
        testSizeSetting.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.TextSizeSettingDefaultViews");

            startActivityForResult(intent, 1);
        });

        ((Button) this.findViewById(R.id.updateMusicList)).setOnClickListener(view -> {

            FileReader.saveLastBootTime(GlobalSetting.this);

            mDialogRefreshMusicList = new DialogRefreshMusicList(GlobalSetting.this);

            mDialogRefreshMusicList.setArguments(new AlertDialog.Builder(GlobalSetting.this)
                    .setIcon(drawable.ic_dialog_info)
                    .setTitle(GlobalSetting.this.getResources().getString(R.string.strings____Dialog_UpdateMusicList____refreshingMusiclist))
                    .setView(mDialogRefreshMusicList.getView())
                    .setCancelable(false)
                    .setNegativeButton(GlobalSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                        if (mDialogRefreshMusicList != null) {
                            mDialogRefreshMusicList.cancel();
                            mDialogRefreshMusicList = null;
                        }
                    })
                    .setOnCancelListener(arg0 -> {

                    })
                    .show());

            mDialogRefreshMusicList.start();

        });

        Button pagerSetting = (Button) this.findViewById(R.id.filterSetting);
        pagerSetting.setVisibility(View.GONE);
        pagerSetting.setOnClickListener(view -> {

            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.FilterSortSetting");
            intent.putExtra("jp.linanfine.dsma.pagerid", mFilterSpinner.getSelectedItemPosition());

            startActivityForResult(intent, 1);

        });

        Button resetScores = (Button) this.findViewById(R.id.resetscores);
        resetScores.setOnClickListener(view -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GlobalSetting.this);
            // アラートダイアログのタイトルを設定します
            alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle(getResources().getString(R.string.global_setting_dialog_reset_scores));
            // アラートダイアログのメッセージを設定します
            alertDialogBuilder.setMessage(getResources().getString(R.string.global_setting_dialog_reset_scores_main));
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings_global____ok),
                    (dialog, which) -> {
                        if (FileReader.resetScores(GlobalSetting.this)) {
                            Toast.makeText(GlobalSetting.this, "done!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(GlobalSetting.this, "failed...", Toast.LENGTH_LONG).show();
                        }
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

        Button exportBackup = (Button) this.findViewById(R.id.exportbackupdata);
        exportBackup.setOnClickListener(view -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GlobalSetting.this);
            // アラートダイアログのタイトルを設定します
            alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle(getResources().getString(R.string.global_setting_dialog_backup));
            // アラートダイアログのメッセージを設定します
            alertDialogBuilder.setMessage(getResources().getString(R.string.global_setting_dialog_backuptoA) + Environment.getExternalStorageDirectory().getPath() + "/jp.linanfine.dsm/ddr_score_manager_score_backup.zds" + getResources().getString(R.string.global_setting_dialog_backuptoB));
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings_global____ok),
                    (dialog, which) -> FileReader.backupScores(GlobalSetting.this));
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

        Button importBackup = (Button) this.findViewById(R.id.importbackupdata);
        importBackup.setOnClickListener(view -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GlobalSetting.this);
            // アラートダイアログのタイトルを設定します
            alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle(getResources().getString(R.string.global_setting_dialog_restore));
            // アラートダイアログのメッセージを設定します
            alertDialogBuilder.setMessage(getResources().getString(R.string.global_setting_dialog_restorefromA) + Environment.getExternalStorageDirectory().getPath() + "/jp.linanfine.dsm/ddr_score_manager_score_backup.zds" + getResources().getString(R.string.global_setting_dialog_restorefromB));
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings_global____ok),
                    (dialog, which) -> FileReader.restoreScores(GlobalSetting.this));
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

        Button exportMyList = (Button) this.findViewById(R.id.exportmylist);
        exportMyList.setOnClickListener(view -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GlobalSetting.this);
            // アラートダイアログのタイトルを設定します
            alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle(getResources().getString(R.string.global_setting_dialog_backup_mylist));
            // アラートダイアログのメッセージを設定します
            alertDialogBuilder.setMessage(getResources().getString(R.string.global_setting_dialog_backuptoA_mylist) + Environment.getExternalStorageDirectory().getPath() + "/jp.linanfine.dsm/ddr_score_manager_mylist_backup.zds" + getResources().getString(R.string.global_setting_dialog_backuptoB_mylist));
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings_global____ok),
                    (dialog, which) -> FileReader.backupMyList(GlobalSetting.this));
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

        Button importMyList = (Button) this.findViewById(R.id.importmylist);
        importMyList.setOnClickListener(view -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GlobalSetting.this);
            // アラートダイアログのタイトルを設定します
            alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle(getResources().getString(R.string.global_setting_dialog_restore_mylist));
            // アラートダイアログのメッセージを設定します
            alertDialogBuilder.setMessage(getResources().getString(R.string.global_setting_dialog_restorefromA_mylist) + Environment.getExternalStorageDirectory().getPath() + "/jp.linanfine.dsm/ddr_score_manager_mylist_backup.zds" + getResources().getString(R.string.global_setting_dialog_restorefromB_mylist));
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings_global____ok),
                    (dialog, which) -> FileReader.restoreMyList(GlobalSetting.this));
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


        mGestures = FileReader.readGestureSettings(this);
        ((CheckBox) this.findViewById(R.id.gestureEnableFlick)).setChecked(mGestures.GestureEnabled);
        int gesturetypenum;
        gesturetypenum = FileReader.getGestureActionTypeNum(mGestures.OnItemClicked);
        ((Spinner) this.findViewById(R.id.gestureOnItemClicked)).setSelection(gesturetypenum);
        gesturetypenum = FileReader.getGestureActionTypeNum(mGestures.OnItemLongClicked);
        ((Spinner) this.findViewById(R.id.gestureOnItemLongClicked)).setSelection(gesturetypenum);

        ((Spinner) this.findViewById(R.id.gestureOnItemClicked)).setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int typenum;
                typenum = ((Spinner) GlobalSetting.this.findViewById(R.id.gestureOnItemClicked)).getSelectedItemPosition();
                mGestures.OnItemClicked = FileReader.getGestureAction(typenum);
                FileReader.saveGestureSettings(GlobalSetting.this, mGestures);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ((Spinner) this.findViewById(R.id.gestureOnItemLongClicked)).setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int typenum;
                typenum = ((Spinner) GlobalSetting.this.findViewById(R.id.gestureOnItemLongClicked)).getSelectedItemPosition();
                mGestures.OnItemLongClicked = FileReader.getGestureAction(typenum);
                FileReader.saveGestureSettings(GlobalSetting.this, mGestures);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        CheckBox showAd = (CheckBox) this.findViewById(R.id.showAd);
        showAd.setChecked(FileReader.readShowAd(this));
        showAd.setOnCheckedChangeListener((arg0, arg1) -> {
            SharedPreferences pref = getSharedPreferences("GeneralSetting", MODE_PRIVATE);
            Editor e = pref.edit();
            e.putBoolean("ShowAd", ((CheckBox) GlobalSetting.this.findViewById(R.id.showAd)).isChecked());
            e.apply();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySetting.setTitleBarShown(this, this.findViewById(R.id.titleBar));
        FileReader.requestAd((LinearLayout) this.findViewById(R.id.adContainer), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mainView = this.getLayoutInflater().inflate(
                R.layout.activity_global_setting, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        this.setContentView(mainView);

        initialize();
    }
}
