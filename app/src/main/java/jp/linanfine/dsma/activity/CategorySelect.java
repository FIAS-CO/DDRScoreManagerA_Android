package jp.linanfine.dsma.activity;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.dialog.DialogFromGateIds;
import jp.linanfine.dsma.dialog.DialogFromGateList;
import jp.linanfine.dsma.dialog.DialogRefreshMusicList;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.debug.DebugOutput;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.AppearanceSettingsSp;

public class CategorySelect extends Activity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private View mHandledView;

    private boolean mCloseCategoryOnBackKey = false;
    private int mGlobalI;
    private String mGlobalS;
    private int mOpenedCategory = -1;
    private int mScrPos = -1;

    private int debugCount;

    private void showLatestUpdate() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo("jp.linanfine.dsma", PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        String v = packageInfo.versionName;

        DebugOutput.getInstance(this).ToastLong(v + "\n" + FileReader.readLastVersion(this));

        if (!FileReader.readLastVersion(this).equals(v)) {
            FileReader.saveMusicListVersion(this, "Implemented File");
            FileReader.copyMusicIdList(this);
            FileReader.copyMusicNames(this);
            FileReader.copyShockArrowExists(this);

            new AlertDialog.Builder(CategorySelect.this)
                    .setTitle(this.getResources().getString(R.string.strings_____Dialog_UpdateInfo____versionInfoTitle))
                    .setMessage(getResources().getString(R.string.strings____Dialog_UpdateInfo____updates))
                    .setNegativeButton(this.getResources().getString(R.string.strings_global____ok), (dialog, which) -> {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    })
                    .show();

            userActionRefreshMusicList(true);

            FileReader.saveLastVersion(this, v);

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void userActionShowSystemMenu() {
        //選択項目を準備する。
        ArrayList<String> str_items = new ArrayList<>();
        str_items.add(getResources().getString(R.string.strings____Menu_System____preference));
        str_items.add(getResources().getString(R.string.strings____Menu_System____showStatus));
        str_items.add(getResources().getString(R.string.strings____Menu_System____manageRivals));
        str_items.add(getResources().getString(R.string.strings____Menu_System____ddrSa));
        str_items.add(getResources().getString(R.string.strings____Menu_System____getScore));
        //str_items.add(getResources().getString(R.string.strings____Menu_System____openOcrMode));

        new AlertDialog.Builder(CategorySelect.this)
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                    switch (which) {
                        case 0:
                            userActionOpenPreference();
                            break;
                        case 1:
                            userActionShowStatus();
                            break;
                        case 2:
                            userActionManageRivals();
                            break;
                        case 3:
                            userActionDdrSa();
                            break;
                        case 4:
                            userActionFromGateList();
                            break;
                    }
                }).show();
    }

    private void userActionOpenPreference() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GlobalSetting");

        startActivityForResult(intent, 1);
    }

    private DialogRefreshMusicList mDialogRefreshMusicList = null;

    private void userActionRefreshMusicList(boolean force) {
        if (!force) {
            if (FileReader.readLastVersion(this).equals("0.54.14080800")) {
                FileReader.saveAutoUpdateMusicList(this, true);
            }

            if (!FileReader.readAutoUpdateMusicList(this)) {
                return;
            }

            long now = new Date().getTime();
            long last = FileReader.readLastBootTime(this);

            if (now - last < 24 * 60 * 60 * 1000) {
                return;
            }
        }

        FileReader.saveLastBootTime(this);

        mDialogRefreshMusicList = new DialogRefreshMusicList(this);

        mDialogRefreshMusicList.setArguments(new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(this.getResources().getString(R.string.strings____Dialog_UpdateMusicList____refreshingMusiclist))
                .setView(mDialogRefreshMusicList.getView())
                .setCancelable(false)
                .setNegativeButton(this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                    if (mDialogRefreshMusicList != null) {
                        mDialogRefreshMusicList.cancel();
                        mDialogRefreshMusicList = null;
                    }
                })
                .setOnCancelListener(arg0 -> CategorySelect.this.initialize())
                .show());

        mDialogRefreshMusicList.start();
    }

    private void userActionDdrSa() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.DDRSA");

        startActivity(intent);
    }

    private void userActionShowStatus() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.StatusActivity");

        startActivityForResult(intent, 1);
    }

    private void userActionManageRivals() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ManageRivals");

        startActivityForResult(intent, 1);
    }

    private void userActionOpenOcrMode() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.OcrMode");

        startActivityForResult(intent, 1);
    }

    private void userActionFromGateList() {
        new AlertDialog.Builder(CategorySelect.this)
                .setTitle(getResources().getString(R.string.strings____Dialog_GetScores____getScore))
                .setMessage(getResources().getString(R.string.strings____Dialog_GetScores____getScoreMessage))
                .setPositiveButton(getResources().getString(R.string.strings____Dialog_GetScores____getScoreTypeSp), (dialog, which) -> {
                    mFromGateListGetDouble = false;
                    showDialogFromGateList();
                })
                .setNeutralButton(getResources().getString(R.string.strings____Dialog_GetScores____getScoreTypeDp), (dialog, which) -> {
                    mFromGateListGetDouble = true;
                    showDialogFromGateList();
                })
                .setNegativeButton(getResources().getString(R.string.strings_global____cancel), (dialog, which) -> {
                })
                .show();
    }

    private void initialize() {
        FileReader.convertOldFilterSortSetting(this);

        mCloseCategoryOnBackKey = FileReader.readCloseCategoryOnBackKeyPressed(this);

        mTopLevelLayout.removeAllViews();
        mMainView = this.getLayoutInflater().inflate(R.layout.view_category_select_inner, null);
        mTopLevelLayout.addView(mMainView);

        PackageInfo packageInfo = null;
        TextView tv = findViewById(R.id.version);
        try {
            packageInfo = getPackageManager().getPackageInfo("jp.linanfine.dsma", PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String ver;
        if (packageInfo != null) {
            ver = "App Version: " + packageInfo.versionName;
        } else {
            ver = "App Version: " + "invalid version";
        }
        ver = ver + "\nMusic List Version: " + FileReader.readMusicListVersion(this);
        tv.setText(ver);

        AppearanceSettingsSp ap = FileReader.readAppearanceSettings(this);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop1)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop2)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop3)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop4)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop5)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop6)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop7)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop8)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop9)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop10)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop11)).setTextSize(ap.CategoryTopFontSize);
        ((TextView) mMainView.findViewById(R.id.textCategoryTop12)).setTextSize(ap.CategoryTopFontSize);

        ((TextView) mMainView.findViewById(R.id.subItem1)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem2)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem3)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem4)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem5)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem6)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem7)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem8)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem9)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem10)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem11)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem12)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem13)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem14)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem15)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem16)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem17)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem18)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem19)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem20)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem104)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem21)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem22)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem23)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem24)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem25)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem26)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem27)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem28)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem29)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem30)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem31)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem32)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem33)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem34)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem35)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem36)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem37)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem38)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem39)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem40)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem41)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem42)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem43)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem44)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem45)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem46)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem47)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem48)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem49)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem50)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem51)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem52)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem53)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem54)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem55)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem56)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem57)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem58)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem59)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem60)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem61)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem62)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem63)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem64)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem65)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem66)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem67)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem68)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem69)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem70)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem71)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem72)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem73)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem105)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem74)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem75)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem76)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem77)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem78)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem79)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem80)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem81)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem82)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem83)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem84)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem85)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem86)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem87)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem88)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem106)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem89)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem90)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem91)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem92)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem93)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem94)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem95)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem96)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem97)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem98)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem99)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem100)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem101)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem102)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItem103)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemAAp)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemAAm)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemAp)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemAm)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemBp)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemBm)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemCp)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemCm)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemDp)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemAApRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemAAmRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemApRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemAmRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemBpRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemBmRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemCpRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemCmRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemDpRival)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemSeriesA)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemSeriesA20)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemSeriesA20PLUS)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemSeriesA3)).setTextSize(ap.CategorySubItemFontSize);
        ((TextView) mMainView.findViewById(R.id.subItemSeriesWorld)).setTextSize(ap.CategorySubItemFontSize);

        this.findViewById(R.id.menuButton).setOnClickListener(v -> userActionShowSystemMenu());

        LinearLayout categoryAllMusics = mMainView.findViewById(R.id.categoryAllMusics);
        categoryAllMusics.setOnClickListener(view -> {
            categoryClose(true);

            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
            intent.putExtra("jp.linanfine.dsma.category", "All Musics");

            startActivityForResult(intent, 1);
        });
        LinearLayout categoryRecents = mMainView.findViewById(R.id.categoryRecents);
        categoryRecents.setOnClickListener(view -> {

            categoryClose(true);

            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
            intent.putExtra("jp.linanfine.dsma.category", "Recents");

            startActivityForResult(intent, 1);
        });
        scrollView = mMainView.findViewById(R.id.scrollView);
        LinearLayout categoryDifficulty = mMainView.findViewById(R.id.categoryDifficulty);
        dificulties = mMainView.findViewById(R.id.difficulties);
        LinearLayout categorySeriesTitle = mMainView.findViewById(R.id.categorySeriesTitle);
        seriesTitles = mMainView.findViewById(R.id.seriesTitles);
        LinearLayout categoryABC = mMainView.findViewById(R.id.categoryABC);
        abcs = mMainView.findViewById(R.id.abcs);
        LinearLayout categoryRank = mMainView.findViewById(R.id.categoryRank);
        ranks = mMainView.findViewById(R.id.ranks);
        LinearLayout categoryFCtype = mMainView.findViewById(R.id.categoryFCtype);
        fctypes = mMainView.findViewById(R.id.fctypes);
        LinearLayout categoryRival = mMainView.findViewById(R.id.categoryRival);
        rivals = mMainView.findViewById(R.id.rivals);
        LinearLayout categoryWinLoseRival = mMainView.findViewById(R.id.categoryWinLoseRival);
        rivalwinlose = mMainView.findViewById(R.id.winloseRival);
        LinearLayout categoryRankRival = mMainView.findViewById(R.id.categoryRankRival);
        rivalranks = mMainView.findViewById(R.id.ranksRival);
        LinearLayout categoryFCtypeRival = mMainView.findViewById(R.id.categoryFCtypeRival);
        rivalfctypes = mMainView.findViewById(R.id.fctypesRival);
        LinearLayout categoryMyList = mMainView.findViewById(R.id.categoryMyList);
        mylists = mMainView.findViewById(R.id.mylists);
        categoryDifficulty.setOnClickListener(view -> {
            if (dificulties.getVisibility() == View.GONE) {
                categoryClose(true);
                mOpenedCategory = 0;
                dificulties.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                categoryClose(true);
            }
        });
        categorySeriesTitle.setOnClickListener(view -> {
            if (seriesTitles.getVisibility() == View.GONE) {
                categoryClose(true);
                mOpenedCategory = 1;
                seriesTitles.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                categoryClose(true);
            }
        });
        categoryABC.setOnClickListener(view -> {
            if (abcs.getVisibility() == View.GONE) {
                categoryClose(true);
                mOpenedCategory = 2;
                abcs.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                categoryClose(true);
            }
        });
        categoryRank.setOnClickListener(view -> {
            if (ranks.getVisibility() == View.GONE) {
                categoryClose(true);
                mOpenedCategory = 3;
                ranks.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                categoryClose(true);
            }
        });
        categoryFCtype.setOnClickListener(view -> {
            if (fctypes.getVisibility() == View.GONE) {
                categoryClose(true);
                mOpenedCategory = 4;
                fctypes.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                categoryClose(true);
            }
        });
        categoryRival.setOnClickListener(view -> {
            if (rivals.getVisibility() == View.GONE) {
                categoryClose(true);
                mOpenedCategory = 6;
                rivals.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                categoryClose(true);
            }
        });
        categoryWinLoseRival.setOnClickListener(view -> {
            if (rivalwinlose.getVisibility() == View.GONE) {
                rivalSubCategoryClose();
                mOpenedCategory = 7;
                rivalwinlose.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                rivalSubCategoryClose();
            }
        });
        categoryRankRival.setOnClickListener(view -> {
            if (rivalranks.getVisibility() == View.GONE) {
                rivalSubCategoryClose();
                mOpenedCategory = 8;
                rivalranks.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                rivalSubCategoryClose();
            }
        });
        categoryFCtypeRival.setOnClickListener(view -> {
            if (rivalfctypes.getVisibility() == View.GONE) {
                rivalSubCategoryClose();
                mOpenedCategory = 9;
                rivalfctypes.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                rivalSubCategoryClose();
            }
        });

        int activeRivalNo = FileReader.readActiveRival(this);
        if (activeRivalNo == -1) {
            this.findViewById(R.id.wrapperRival).setVisibility(View.GONE);
        } else {
            this.findViewById(R.id.wrapperRival).setVisibility(View.VISIBLE);
        }

        int mylistCount = FileReader.readMyListCount(this);
        if (mylistCount == 0) {
            this.findViewById(R.id.wrapperMyList).setVisibility(View.GONE);
        } else {
            this.findViewById(R.id.wrapperMyList).setVisibility(View.VISIBLE);
        }
        mylists.removeAllViews();
        for (int i = 0; i < mylistCount; i++) {
            View item = this.getLayoutInflater().inflate(R.layout.view_categorysubitem, null);
            TextView text = item.findViewById(R.id.text);
            text.setTextSize(ap.CategorySubItemFontSize);
            text.setText(FileReader.readMyListName(this, i));
            mylists.addView(item);
            item.setId(i);
            LayoutParams lp = item.getLayoutParams();
            android.widget.LinearLayout.LayoutParams llp = new android.widget.LinearLayout.LayoutParams(lp);
            llp.setMargins(20, 0, 0, 0);
            item.setLayoutParams(llp);
            item.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                intent.putExtra("jp.linanfine.dsma.category", "My List");
                intent.putExtra("jp.linanfine.dsma.mylistid", view.getId());

                startActivityForResult(intent, 1);
            });
            item.setOnLongClickListener(view -> {

                mGlobalS = ((TextView) view.findViewById(R.id.text)).getText().toString();
                mGlobalI = view.getId();

                //選択項目を準備する。
                ArrayList<String> str_items = new ArrayList<String>();
                str_items.add(getResources().getString(R.string.strings____Menu_MyList____editMylistName));
                str_items.add(getResources().getString(R.string.strings____Menu_MyList____deleteMylist));

                new AlertDialog.Builder(CategorySelect.this)
                        .setTitle(mGlobalS)
                        .setItems(str_items.toArray(new String[0]), (dialog, which) -> {

                            //選択したアイテムの番号(0～)がwhichに格納される
                            switch (which) {
                                case 0: {

                                    //テキスト入力を受け付けるビューを作成します。
                                    final EditText editView = CategorySelect.this.getLayoutInflater().inflate(R.layout.view_singleline_edit_text, null).findViewById(R.id.editText);
                                    editView.setText(mGlobalS);
                                    editView.setSelectAllOnFocus(true);
                                    editView.setOnFocusChangeListener((view1, focused) -> {
                                        if (focused) {
                                            mHandledView = view1;
                                            mHandler.post(() -> {
                                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                                            });
                                        }
                                    });
                                    new AlertDialog.Builder(CategorySelect.this)
                                            .setIcon(drawable.ic_dialog_info)
                                            .setTitle(CategorySelect.this.getResources().getString(R.string.strings____Dialog_MyList____editMylistName))
                                            //setViewにてビューを設定します。
                                            .setView(editView)
                                            .setPositiveButton("OK", (dialog14, whichButton) -> {
                                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                                                FileReader.saveMyListName(CategorySelect.this, mGlobalI, editView.getText().toString());
                                                initialize();
                                            })
                                            .setNegativeButton("Cancel", (dialog13, whichButton) -> {
                                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                                            })
                                            .show();

                                    break;
                                }
                                case 1: {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategorySelect.this);
                                    // アラートダイアログのタイトルを設定します
                                    alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
                                    alertDialogBuilder.setTitle(getResources().getString(R.string.strings____Dialog_MyList____deleteMylist));
                                    // アラートダイアログのメッセージを設定します
                                    alertDialogBuilder.setMessage(getResources().getString(R.string.strings____Dialog_MyList____deleteConferm));
                                    // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                                    alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings_global____ok),
                                            (dialog12, which12) -> {
                                                FileReader.deleteMyList(CategorySelect.this, mGlobalI);
                                                initialize();
                                            });
                                    // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                                    alertDialogBuilder.setNegativeButton(getResources().getString(R.string.strings_global____cancel),
                                            (dialog1, which1) -> {
                                            });
                                    // アラートダイアログのキャンセルが可能かどうかを設定します
                                    alertDialogBuilder.setCancelable(true);
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    // アラートダイアログを表示します
                                    alertDialog.show();

                                    break;
                                }
                            }
                        }).show();

                return false;
            });
        }
        categoryMyList.setOnClickListener(view -> {
            if (mylists.getVisibility() == View.GONE) {
                categoryClose(true);
                mOpenedCategory = 5;
                mylists.setVisibility(View.VISIBLE);
                (new AsyncTask<View, Void, View>() {
                    @Override
                    protected View doInBackground(View... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(View result) {
                        int sy = scrollView.getScrollY();
                        int[] loc = new int[2];
                        result.getLocationInWindow(loc);
                        int[] ll = new int[2];
                        mMainView.findViewById(R.id.scrollView).getLocationInWindow(ll);
                        scrollView.scrollTo(0, loc[1] - ll[1] + sy);
                    }
                }).execute(view);
            } else {
                categoryClose(true);
            }
        });

        {
            OnClickListener serClicked = view -> {
                String name = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();

                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                intent.putExtra("jp.linanfine.dsma.category", "Ser" + name);

                startActivityForResult(intent, 1);
            };
            mMainView.findViewById(R.id.seriesWorld).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesA3).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesA20PLUS).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesA20).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesA).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.series2014).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.series2013).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesX3).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesX3vs2ndMIX).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesX2).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesX).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesSuperNOVA2).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesSuperNOVA).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesEXTREME).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesMAX2).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.seriesMAX).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.series5th).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.series4th).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.series3rd).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.series2nd).setOnClickListener(serClicked);
            mMainView.findViewById(R.id.series1st).setOnClickListener(serClicked);
            OnClickListener difClicked = view -> {
                String name = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();

                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                intent.putExtra("jp.linanfine.dsma.category", "Dif" + name);

                startActivityForResult(intent, 1);
            };
            mMainView.findViewById(R.id.difficulty1).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty2).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty3).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty4).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty5).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty6).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty7).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty8).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty9).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty10).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty11).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty12).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty13).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty14).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty15).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty16).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty17).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty18).setOnClickListener(difClicked);
            mMainView.findViewById(R.id.difficulty19).setOnClickListener(difClicked);

            OnClickListener abcClicked = view -> {
                String name = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();

                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                if (name.equals("NUM")) {
                    intent.putExtra("jp.linanfine.dsma.category", "Abc***NUM***");
                } else {
                    intent.putExtra("jp.linanfine.dsma.category", "Abc" + name);
                }

                startActivityForResult(intent, 1);
            };

            mMainView.findViewById(R.id.abcNUM).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcA).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcB).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcC).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcD).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcE).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcF).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcG).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcH).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcI).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcJ).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcK).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcL).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcM).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcN).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcO).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcP).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcQ).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcR).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcS).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcT).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcU).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcV).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcW).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcX).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcY).setOnClickListener(abcClicked);
            mMainView.findViewById(R.id.abcZ).setOnClickListener(abcClicked);

            OnClickListener rankClicked = view -> {
                String name = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();

                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                intent.putExtra("jp.linanfine.dsma.category", "Rank" + name);

                startActivityForResult(intent, 1);
            };

            mMainView.findViewById(R.id.rankAAA).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankAAp).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankAA).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankAAm).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankAp).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankA).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankAm).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankBp).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankB).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankBm).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankCp).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankC).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankCm).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankDp).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankD).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankE).setOnClickListener(rankClicked);
            mMainView.findViewById(R.id.rankNoPlay).setOnClickListener(rankClicked);

            OnClickListener fctypeClicked = view -> {
                String name = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();

                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                intent.putExtra("jp.linanfine.dsma.category", "Fc" + name);

                startActivityForResult(intent, 1);
            };

            mMainView.findViewById(R.id.fcMFC).setOnClickListener(fctypeClicked);
            mMainView.findViewById(R.id.fcPFC).setOnClickListener(fctypeClicked);
            mMainView.findViewById(R.id.fcFC).setOnClickListener(fctypeClicked);
            mMainView.findViewById(R.id.fcGFC).setOnClickListener(fctypeClicked);
            mMainView.findViewById(R.id.fcLife4).setOnClickListener(fctypeClicked);
            mMainView.findViewById(R.id.fcCleared).setOnClickListener(fctypeClicked);
            mMainView.findViewById(R.id.fcFailed).setOnClickListener(fctypeClicked);
            mMainView.findViewById(R.id.fcNoPlay).setOnClickListener(fctypeClicked);

            OnClickListener rankRivalClicked = view -> {
                String name = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();

                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                intent.putExtra("jp.linanfine.dsma.category", "RankRival" + name);

                startActivityForResult(intent, 1);
            };

            mMainView.findViewById(R.id.rankAAARival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankAApRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankAARival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankAAmRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankApRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankARival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankAmRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankBpRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankBRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankBmRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankCpRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankCRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankCmRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankDpRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankDRival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankERival).setOnClickListener(rankRivalClicked);
            mMainView.findViewById(R.id.rankNoPlayRival).setOnClickListener(rankRivalClicked);

            OnClickListener fctypeRivalClicked = view -> {
                String name = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();

                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                intent.putExtra("jp.linanfine.dsma.category", "FcRival" + name);

                startActivityForResult(intent, 1);
            };

            mMainView.findViewById(R.id.fcMFCRival).setOnClickListener(fctypeRivalClicked);
            mMainView.findViewById(R.id.fcPFCRival).setOnClickListener(fctypeRivalClicked);
            mMainView.findViewById(R.id.fcFCRival).setOnClickListener(fctypeRivalClicked);
            mMainView.findViewById(R.id.fcGFCRival).setOnClickListener(fctypeRivalClicked);
            mMainView.findViewById(R.id.fcLife4Rival).setOnClickListener(fctypeRivalClicked);
            mMainView.findViewById(R.id.fcClearedRival).setOnClickListener(fctypeRivalClicked);
            mMainView.findViewById(R.id.fcFailedRival).setOnClickListener(fctypeRivalClicked);
            mMainView.findViewById(R.id.fcNoPlayRival).setOnClickListener(fctypeRivalClicked);

            OnClickListener winloseRivalClicked = view -> {
                String name = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();

                Intent intent = new Intent();
                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
                intent.putExtra("jp.linanfine.dsma.category", "WinLoseRival" + name);

                startActivityForResult(intent, 1);
            };

            mMainView.findViewById(R.id.winRival).setOnClickListener(winloseRivalClicked);
            mMainView.findViewById(R.id.loseRival).setOnClickListener(winloseRivalClicked);
            mMainView.findViewById(R.id.drawRival).setOnClickListener(winloseRivalClicked);
            mMainView.findViewById(R.id.winNoplayRival).setOnClickListener(winloseRivalClicked);
            mMainView.findViewById(R.id.loseNoplayRival).setOnClickListener(winloseRivalClicked);
            mMainView.findViewById(R.id.drawNoplayRival).setOnClickListener(winloseRivalClicked);
            mMainView.findViewById(R.id.winCloseRival).setOnClickListener(winloseRivalClicked);
            mMainView.findViewById(R.id.loseCloseRival).setOnClickListener(winloseRivalClicked);
            mMainView.findViewById(R.id.drawPlayedRival).setOnClickListener(winloseRivalClicked);
        }

        switch (mOpenedCategory) {
            case 0:
                dificulties.setVisibility(View.VISIBLE);
                break;
            case 1:
                seriesTitles.setVisibility(View.VISIBLE);
                break;
            case 2:
                abcs.setVisibility(View.VISIBLE);
                break;
            case 3:
                ranks.setVisibility(View.VISIBLE);
                break;
            case 4:
                fctypes.setVisibility(View.VISIBLE);
                break;
            case 5:
                mylists.setVisibility(View.VISIBLE);
                break;
            case 6:
                rivals.setVisibility(View.VISIBLE);
                break;
            case 7:
                rivals.setVisibility(View.VISIBLE);
                rivalwinlose.setVisibility(View.VISIBLE);
                break;
            case 8:
                rivals.setVisibility(View.VISIBLE);
                rivalranks.setVisibility(View.VISIBLE);
                break;
            case 9:
                rivals.setVisibility(View.VISIBLE);
                rivalfctypes.setVisibility(View.VISIBLE);
                break;
            default:
                categoryClose(true);
        }

        if (mScrPos > 0) {
            (new AsyncTask<Integer, Void, Integer>() {
                @Override
                protected Integer doInBackground(Integer... params) {
                    return params[0];
                }

                @Override
                protected void onPostExecute(Integer result) {
                    scrollView.scrollTo(0, result);
                }
            }).execute(mScrPos);
        }

        debugCount = -9;
        this.findViewById(R.id.sou).setOnClickListener(v -> {
            ++debugCount;
            if (debugCount > 0) {
                showDialogFromGateIds();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCloseCategoryOnBackKey && categoryClose(false)) {
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            userActionShowSystemMenu();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            initialize();
            return;
        }
        if (requestCode == DialogFromGateList.LoginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                showDialogFromGateList();
            }
        }
        if (requestCode == DialogFromGateIds.LoginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                showDialogFromGateIds();
            }
        }
    }

    LinearLayout dificulties;
    LinearLayout seriesTitles;
    LinearLayout abcs;
    LinearLayout ranks;
    LinearLayout fctypes;
    LinearLayout rivals;
    LinearLayout rivalwinlose;
    LinearLayout rivalranks;
    LinearLayout rivalfctypes;
    LinearLayout mylists;
    ScrollView scrollView;

    private boolean rivalSubCategoryClose() {
        boolean ret =
                (rivalwinlose.getVisibility() == View.VISIBLE) ||
                        (rivalranks.getVisibility() == View.VISIBLE) ||
                        (rivalfctypes.getVisibility() == View.VISIBLE);
        rivalwinlose.setVisibility(View.GONE);
        rivalranks.setVisibility(View.GONE);
        rivalfctypes.setVisibility(View.GONE);
        mOpenedCategory = -1;
        return ret;
    }

    private boolean categoryClose(boolean force) {
        if (!force && (
                (rivalwinlose.getVisibility() == View.VISIBLE) ||
                        (rivalranks.getVisibility() == View.VISIBLE) ||
                        (rivalfctypes.getVisibility() == View.VISIBLE)
        ))
            return rivalSubCategoryClose();

        boolean ret =
                (dificulties.getVisibility() == View.VISIBLE) ||
                        (seriesTitles.getVisibility() == View.VISIBLE) ||
                        (abcs.getVisibility() == View.VISIBLE) ||
                        (ranks.getVisibility() == View.VISIBLE) ||
                        (fctypes.getVisibility() == View.VISIBLE) ||
                        (rivals.getVisibility() == View.VISIBLE) ||
                        (rivalwinlose.getVisibility() == View.VISIBLE) ||
                        (rivalranks.getVisibility() == View.VISIBLE) ||
                        (rivalfctypes.getVisibility() == View.VISIBLE) ||
                        (mylists.getVisibility() == View.VISIBLE);
        dificulties.setVisibility(View.GONE);
        seriesTitles.setVisibility(View.GONE);
        abcs.setVisibility(View.GONE);
        ranks.setVisibility(View.GONE);
        fctypes.setVisibility(View.GONE);
        rivals.setVisibility(View.GONE);
        rivalwinlose.setVisibility(View.GONE);
        rivalranks.setVisibility(View.GONE);
        rivalfctypes.setVisibility(View.GONE);
        mylists.setVisibility(View.GONE);
        mOpenedCategory = -1;
        return ret;
    }

    private DialogFromGateIds mFromGateIds = null;

    private void showDialogFromGateIds() {
        if (mFromGateIds != null) {
            mFromGateIds.cancel();
            mFromGateIds = null;
        }

        mFromGateIds = new DialogFromGateIds(CategorySelect.this);

        mFromGateIds.setArguments(new AlertDialog.Builder(CategorySelect.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(CategorySelect.this.getResources().getString(R.string.strings_global____app_name))
                        .setView(mFromGateIds.getView())
                        .setCancelable(false)
                        .setNegativeButton(CategorySelect.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                            if (mFromGateIds != null) {
                                mFromGateIds.cancel();
                                mFromGateIds = null;
                            }
                        })
                        .show()
                , false, null, null);

        mFromGateIds.start();
    }

    private DialogFromGateList mFromGateList = null;
    private boolean mFromGateListGetDouble = false;

    private void showDialogFromGateList() {
        if (mFromGateList != null) {
            mFromGateList.cancel();
            mFromGateList = null;
        }

        mFromGateList = new DialogFromGateList(CategorySelect.this);

        mFromGateList.setArguments(new AlertDialog.Builder(CategorySelect.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(CategorySelect.this.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetScoreList) + " (" + (mFromGateListGetDouble ? "DP" : "SP") + ") ...")
                        .setView(mFromGateList.getView())
                        .setCancelable(false)
                        .setNegativeButton(CategorySelect.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                            if (mFromGateList != null) {
                                mFromGateList.cancel();
                                mFromGateList = null;
                            }
                        })
                        .show()
                , mFromGateListGetDouble, null, null);

        mFromGateList.start();
    }

    private LinearLayout mTopLevelLayout;
    private View mMainView;

    @Override
    public void onResume() {
        super.onResume();
        ActivitySetting.setTitleBarShown(this, mMainView.findViewById(R.id.titleBar));

        if (!new File(this.getFilesDir().getPath() + "/" + "WebMusicIds.txt").exists() ||
                !new File(this.getFilesDir().getPath() + "/" + "MusicNames.txt").exists() ||
                !new File(this.getFilesDir().getPath() + "/" + "ShockArrowExists.txt").exists()
        ) {
            FileReader.saveMusicListVersion(this, "Implemented File");
            FileReader.copyMusicIdList(this);
            FileReader.copyMusicNames(this);
            FileReader.copyShockArrowExists(this);
        }
        FileReader.requestAd(this.findViewById(R.id.adContainer), this);
        if (new Date().getTime() - FileReader.readLastAdTapTime(this) > 86400000) {
            this.findViewById(R.id.adNotif).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.adNotif).setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = this.getLayoutInflater().inflate(R.layout.activity_category_select, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        this.setContentView(view);
        mTopLevelLayout = view.findViewById(R.id.top);
        mMainView = this.getLayoutInflater().inflate(R.layout.view_category_select_inner, null);
        mTopLevelLayout.addView(mMainView);

        showLatestUpdate();

        userActionRefreshMusicList(false);

        FileReader.convertOldFilterSortSetting(this);

        initialize();
    }

    @Override
    public void onDestroy() {
        mScrPos = -1;
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDialogRefreshMusicList != null) {
            mDialogRefreshMusicList.cancel();
        }
    }
}
