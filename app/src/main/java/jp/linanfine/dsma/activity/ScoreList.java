package jp.linanfine.dsma.activity;

import android.R.drawable;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.dialog.DialogFromGate;
import jp.linanfine.dsma.dialog.DialogFromGateList;
import jp.linanfine.dsma.dialog.DialogFromGateRecent;
import jp.linanfine.dsma.dialog.DialogFromGateSou;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.util.flare.FlareSkillUpdater;
import jp.linanfine.dsma.util.maker.ListViewItemMakerSurfaceView;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.value.GestureSettings;
import jp.linanfine.dsma.value.IdToWebMusicIdList;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicDataAdapterArguments;
import jp.linanfine.dsma.value.MusicFilter;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.MusicSort;
import jp.linanfine.dsma.value.MusicSortRecent;
import jp.linanfine.dsma.value.RecentData;
import jp.linanfine.dsma.value.RivalData;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.value._enum.GestureAction;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.MusicSortType;
import jp.linanfine.dsma.value._enum.PatternType;
import jp.linanfine.dsma.value._enum.SeriesTitle;
import jp.linanfine.dsma.value._enum.SortOrder;
import jp.linanfine.dsma.value._enum.UserActionFrom;
import jp.linanfine.dsma.value.adapter.MusicDataAdapter;
import jp.linanfine.dsma.value.adapter.MusicDataAdapterSurfaceView;

public class ScoreList extends Activity {

//add by taiheisan start
    /**
     * Series Title配列
     */
    private static final String[] SERIES_TITLE_LIST = {"SerWORLD", "SerA3", "SerA20 PLUS", "SerA20", "SerA", "Ser2014", "Ser2013", "SerX3", "SerX2", "SerX",
            "SerSuperNOVA 2", "SerSuperNOVA", "SerEXTREME", "SerMAX2", "SerMAX", "Ser5th", "Ser4th", "Ser3rd",
            "Ser2nd", "Ser1st"};

    /**
     * ABC配列
     */
    private static final String[] ABC_LIST = {"Abc***NUM***", "AbcA", "AbcB", "AbcC", "AbcD", "AbcE", "AbcF", "AbcG",
            "AbcH", "AbcI", "AbcJ", "AbcK", "AbcL", "AbcM", "AbcN", "AbcO", "AbcP", "AbcQ", "AbcR", "AbcS", "AbcT",
            "AbcU", "AbcV", "AbcW", "AbcX", "AbcY", "AbcZ"};

    /**
     * Difficulty配列
     */
    private static final String[] DIFF_LIST = {"Dif1", "Dif2", "Dif3", "Dif4", "Dif5", "Dif6", "Dif7", "Dif8", "Dif9",
            "Dif10", "Dif11", "Dif12", "Dif13", "Dif14", "Dif15", "Dif16", "Dif17", "Dif18", "Dif19"};

    /**
     * Dance Level配列
     */
    private static final String[] DANCE_LEVEL_LIST = {"RankAAA", "RankAA+", "RankAA", "RankAA-", "RankA+", "RankA", "RankA-", "RankB+", "RankB", "RankB-", "RankC+", "RankC", "RankC-", "RankD+", "RankD",
            "RankE", "RankNoPlay"};

    /**
     * Full Combo Type配列
     */
    private static final String[] FULL_COMBO_TYPE_LIST = {"FcMFC", "FcPFC", "FcGFC", "FcFC", "FcLife4", "FcNoFC", "FcFailed",
            "FcNoPlay"};
//add by taiheisan end

    private Handler mHandler = new Handler();

    ////////////////////////　View　////////////////////////
    private ListView mScoreListView = null;
    private Spinner mFilterSpinner = null;
    private Spinner mSortSpinner = null;
    private ProgressDialog mSouProgressDialog = null;

    ////////////////////////　設定　////////////////////////
    private MusicFilter mMusicFilter = null;
    private MusicSort mMusicSort = null;
    private AppearanceSettingsSp mAppearance = null;
    private GestureSettings mGestures = null;
    private boolean mUseAsyncDraw = true;
    private boolean mUseOldStyleDraw = false;

    ////////////////////////　インテント情報　////////////////////////
    private String mCategory = null;
    private int mCategoryOwnMusicMusicId = 0;
    private int mCategoryMyListMylistId = 0;

    ////////////////////////　データ　////////////////////////
    private IdToWebMusicIdList mWebMusicIds = null;
    private TreeMap<Integer, MusicData> mMusicList = null;
    private TreeMap<Integer, MusicScore> mScoreList = null;
    private String mActiveRivalId = null;
    private String mActiveRivalName = null;
    private TreeMap<Integer, MusicScore> mActiveRivalScoreList = null;
    private TreeMap<Integer, String[]> mComments = null;
    private MusicDataAdapter mListViewAdapter = null;

    ////////////////////////　スレッド命令　////////////////////////
    private boolean mCancelSouThread = false;

    ////////////////////////　一時情報　////////////////////////
    private boolean mScrollPositionUpdateLock = false;
    private int mScrollPosition = 0;
    private int mMyListCount = 0;
    //private int                      mGlobalI                 = 0;
    private int mSouNext = 0;
    private boolean mSouTargetRival = false;
    private ArrayList<UniquePattern> mSouList = new ArrayList<>();
    private UniquePattern mSelectedItemPattern = null;
    private MusicData mSelectedMusicData = null;
    private EditText mTemporaryDialogEditText = null;

    public void actionOfListRefreshCompleted() {
        setListedCount();
        restoreScrollPosition();
    }

    private boolean execUserAction(UserActionFrom from) {
        GestureAction action;
        switch (from) {
            case ItemClick:
                action = mGestures.OnItemClicked;
                break;
            case ItemLongClick:
                action = mGestures.OnItemLongClicked;
                break;
            default:
                action = GestureAction.None;
        }
        switch (action) {
            case ShowItemMenu:
                userActionShowItemMenu();
                break;
            case OpenOwnMusic:
                userActionOpenOwnMusic();
                break;
            case FromGate:
                userActionFromGate();
                break;
            case DirectEdit:
                userActionDirectEdit();
                break;
            case SelectRivalAction:
                userActionSelectRivalAction();
                break;
            case RivalFromGate:
                userActionRivalFromGate();
                break;
            case RivalDirectEdit:
                userActionRivalDirectEdit();
                break;
            case AddToMyList:
                userActionAddToMyList();
                break;
            case RemoveFromMyList:
                userActionRemoveFromMyList();
                break;
            default:
                break;
        }
        return false;
    }

    private void userActionShowSystemMenu() {

        //選択項目を準備する。
        ArrayList<String> str_items = new ArrayList<>();
        str_items.add(getResources().getString(R.string.strings____Menu_System____preference));
        str_items.add(getResources().getString(R.string.strings____Menu_System____statistics));
        str_items.add(getResources().getString(R.string.strings____Menu_System____showStatus));
        str_items.add(getResources().getString(R.string.strings____Menu_System____manageRivals));
        str_items.add(getResources().getString(R.string.dialog_copy_to_clipboard));
        str_items.add(getResources().getString(R.string.strings____Menu_System____getScore));
        str_items.add(getResources().getString(R.string.strings____Menu_System____getScoreDetail));
        str_items.add(getResources().getString(R.string.strings____Menu_System____omikuji));
        str_items.add(getResources().getString(R.string.flarenote_uploader));
        if (mCategory.equals("Recents")) {
            str_items.add(getResources().getString(R.string.menu_refresh_recent));
        }

        new AlertDialog.Builder(ScoreList.this)
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    userActionOpenPreference();
                                    break;
                                case 1:
                                    userActionShowStatistics();
                                    break;
                                case 2:
                                    userActionShowStatus();
                                    break;
                                case 3:
                                    userActionManageRivals();
                                    break;
                                case 4:
                                    userActionCopyToClipboardList();
                                    break;
                                case 5:
                                    userActionFromGateList();
                                    break;
                                case 6:
                                    userActionSou();
                                    break;
                                case 7:
                                    userActionOmikuji();
                                    break;
                                case 8:
                                    userActionFlareSkillNote();
                                    break;
                                case 9:
                                    userActionOpenFromGateRecent();
                                    break;
                            }
                        }
                ).show();

    }

    private void userActionOpenPreference() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GlobalSetting");

        startActivityForResult(intent, 1);
    }

    private ScoreData getScoreDataOfPattern(MusicScore ms, PatternType pat) {
        switch (pat) {
            case bSP:
                return ms.bSP;
            case BSP:
                return ms.BSP;
            case DSP:
                return ms.DSP;
            case ESP:
                return ms.ESP;
            case CSP:
                return ms.CSP;
            case BDP:
                return ms.BDP;
            case DDP:
                return ms.DDP;
            case EDP:
                return ms.EDP;
            case CDP:
                return ms.CDP;
            default:
                return new ScoreData();
        }
    }

    private int getDifficultyOfPattern(int musicId, PatternType pat) {
        switch (pat) {
            case bSP:
                return mMusicList.get(musicId).Difficulty_bSP;
            case BSP:
                return mMusicList.get(musicId).Difficulty_BSP;
            case DSP:
                return mMusicList.get(musicId).Difficulty_DSP;
            case ESP:
                return mMusicList.get(musicId).Difficulty_ESP;
            case CSP:
                return mMusicList.get(musicId).Difficulty_CSP;
            case BDP:
                return mMusicList.get(musicId).Difficulty_BDP;
            case DDP:
                return mMusicList.get(musicId).Difficulty_DDP;
            case EDP:
                return mMusicList.get(musicId).Difficulty_EDP;
            case CDP:
                return mMusicList.get(musicId).Difficulty_CDP;
            default:
                return 0;
        }
    }

    private int getTypeNoOfPattern(PatternType pat) {
        switch (pat) {
            case bSP:
                return 0;
            case BSP:
                return 1;
            case DSP:
                return 2;
            case ESP:
                return 3;
            case CSP:
                return 4;
            case BDP:
                return 5;
            case DDP:
                return 6;
            case EDP:
                return 7;
            case CDP:
                return 8;
            default:
                return -1;
        }
    }

    private int getNoOfSeriesTitle(SeriesTitle ser) {
        switch (ser) {
            case _1st:
                return 0;
            case _2nd:
                return 1;
            case _3rd:
                return 2;
            case _4th:
                return 3;
            case _5th:
                return 4;
            case MAX:
                return 5;
            case MAX2:
                return 6;
            case EXTREME:
                return 7;
            case SuperNOVA:
                return 8;
            case SuperNOVA2:
                return 9;
            case X:
                return 10;
            case X2:
                return 11;
            case X3:
                return 12;
            case _2013:
                return 13;
            case _2014:
                return 14;
            case A:
                return 15;
            case A20:
                return 16;
            case A20PLUS:
                return 17;
            case A3:
                return 18;
            case World:
                return 19;
            default:
                return -1;
        }
    }

    private void userActionShowStatistics() {
        int count = mListViewAdapter.getCount();
        int scoreMed = -1;
        int scoreMin = count > 0 ? 1000000 : 0;
        int scoreMax = 0;
        long scoreTotal = 0;
        double scoreAve = -1.0d;
        double scoreVarTotal = 0.0d;
        double scoreVar = -1.0d;
        int danceLevelAAA = 0;
        int danceLevelAAp = 0;
        int danceLevelAA = 0;
        int danceLevelAAm = 0;
        int danceLevelAp = 0;
        int danceLevelA = 0;
        int danceLevelAm = 0;
        int danceLevelBp = 0;
        int danceLevelB = 0;
        int danceLevelBm = 0;
        int danceLevelCp = 0;
        int danceLevelC = 0;
        int danceLevelCm = 0;
        int danceLevelDp = 0;
        int danceLevelD = 0;
        int danceLevelE = 0;
        int danceLevelNoPlay = 0;
        int fctypeMFC = 0;
        int fctypePFC = 0;
        int fctypeFC = 0;
        int fctypeGFC = 0;
        int fctypeLife4 = 0;
        int fctypeNoFC = 0;
        int flareRankNoRank = 0;
        int flareRank0 = 0;
        int flareRankI = 0;
        int flareRankII = 0;
        int flareRankIII = 0;
        int flareRankIV = 0;
        int flareRankV = 0;
        int flareRankVI = 0;
        int flareRankVII = 0;
        int flareRankVIII = 0;
        int flareRankIX = 0;
        int flareRankEX = 0;
        int[] difficulties = new int[21];
        int[] patternTypes = new int[9];
        int[] versions = new int[20];
        ArrayList<Integer> scores = new ArrayList<>();
        UniquePattern pat;
        ScoreData noScore = new ScoreData();
        for (int i = 0; i < count; ++i) {
            pat = mListViewAdapter.getItem(i);
            ScoreData score;
            if (mScoreList.containsKey(pat.MusicId)) {
                MusicScore ms = mScoreList.get(pat.MusicId);
                score = getScoreDataOfPattern(ms, pat.Pattern);
            } else {
                score = noScore;
            }
            scores.add(score.Score);
            if (score.Score < scoreMin) {
                scoreMin = score.Score;
            }
            if (scoreMax < score.Score) {
                scoreMax = score.Score;
            }
            scoreTotal += score.Score;
            switch (score.Rank) {
                case AAA:
                    ++danceLevelAAA;
                    break;
                case AAp:
                    ++danceLevelAAp;
                    break;
                case AA:
                    ++danceLevelAA;
                    break;
                case AAm:
                    ++danceLevelAAm;
                    break;
                case Ap:
                    ++danceLevelAp;
                    break;
                case A:
                    ++danceLevelA;
                    break;
                case Am:
                    ++danceLevelAm;
                    break;
                case Bp:
                    ++danceLevelBp;
                    break;
                case B:
                    ++danceLevelB;
                    break;
                case Bm:
                    ++danceLevelBm;
                    break;
                case Cp:
                    ++danceLevelCp;
                    break;
                case C:
                    ++danceLevelC;
                    break;
                case Cm:
                    ++danceLevelCm;
                    break;
                case Dp:
                    ++danceLevelDp;
                    break;
                case D:
                    ++danceLevelD;
                    break;
                case E:
                    ++danceLevelE;
                    break;
                case Noplay:
                    ++danceLevelNoPlay;
                    break;
            }
            switch (score.FullComboType) {
                case MerverousFullCombo:
                    ++fctypeMFC;
                    break;
                case PerfectFullCombo:
                    ++fctypePFC;
                    break;
                case FullCombo:
                    ++fctypeFC;
                    break;
                case GoodFullCombo:
                    ++fctypeGFC;
                    break;
                case Life4:
                    ++fctypeLife4;
                    break;
                case None:
                    ++fctypeNoFC;
                    break;
            }
            switch (score.FlareRank) {
                case -1:  // NoRank
                    ++flareRankNoRank;
                    break;
                case 0:  // FlareRank 0
                    ++flareRank0;
                    break;
                case 1:  // FlareRank I
                    ++flareRankI;
                    break;
                case 2:  // FlareRank II
                    ++flareRankII;
                    break;
                case 3:  // FlareRank III
                    ++flareRankIII;
                    break;
                case 4:  // FlareRank IV
                    ++flareRankIV;
                    break;
                case 5:  // FlareRank V
                    ++flareRankV;
                    break;
                case 6:  // FlareRank VI
                    ++flareRankVI;
                    break;
                case 7:  // FlareRank VII
                    ++flareRankVII;
                    break;
                case 8:  // FlareRank VIII
                    ++flareRankVIII;
                    break;
                case 9:  // FlareRank IX
                    ++flareRankIX;
                    break;
                case 10:  // FlareRank EX
                    ++flareRankEX;
                    break;
                default:
                    // Handle unexpected values if necessary
                    break;
            }
            ++difficulties[getDifficultyOfPattern(pat.MusicId, pat.Pattern)];
            ++patternTypes[getTypeNoOfPattern(pat.Pattern)];
            ++versions[getNoOfSeriesTitle(mMusicList.get(pat.MusicId).SeriesTitle)];
        }
        scoreAve = (double) scoreTotal / count;
        Collections.sort(scores);
        scoreMed = count > 0 ? scores.get(count / 2) : 0;
        for (int i = 0; i < count; ++i) {
            scoreVarTotal += Math.pow(scores.get(i) - scoreAve, 2);
        }
        scoreVar = Math.sqrt(scoreVarTotal / (count > 0 ? count : 1));
        String c = mCategory;
        if (c.equals("Abc***NUM***")) c = "AbcNUM";
        c = c
                .replace("Abc", "Name ")
                .replace("Dif", "Difficulty ")
                .replace("SerA3", "DDR A3")
                .replace("SerA20 PLUS", "DDR A20 PLUS")
                .replace("SerA20", "DDR A20")
                .replace("SerA", "DDR A")
                .replace("Ser2014", "DDR 2014")
                .replace("Ser2013", "DDR 2013")
                .replace("Ser", "")
                .replace("WinLoseRival", "Rival ")
                .replace("RankRival", "Rival ")
                .replace("FcRival", "Rival ")
                .replace("Rank", "Dance Level ")
                .replace("Fc", "")
        ;
        if (c.equals("Own Music")) {
            Intent intent = getIntent();
            if (intent == null) {
                return;
            }
            int itemId = intent.getIntExtra("jp.linanfine.dsma.musicid", -1);
            c = mMusicList.get(itemId).Name;
        }
        if (c.equals("My List")) {
            Intent intent = getIntent();
            if (intent == null) {
                return;
            }
            int mylistId = intent.getIntExtra("jp.linanfine.dsma.mylistid", -1);
            c = FileReader.readMyListName(this, mylistId);
        }

        View v = ScoreList.this.getLayoutInflater().inflate(R.layout.view_statistics, null);
        ((TextView) v.findViewById(R.id.scoreMin)).setText(TextUtil.getScoreText(scoreMin));
        ((TextView) v.findViewById(R.id.scoreMax)).setText(TextUtil.getScoreText(scoreMax));
        ((TextView) v.findViewById(R.id.scoreMed)).setText(TextUtil.getScoreText(scoreMed));
        ((TextView) v.findViewById(R.id.scoreAve)).setText(TextUtil.getScoreText((int) scoreAve));
        ((TextView) v.findViewById(R.id.scoreVar)).setText(TextUtil.getScoreText((int) scoreVar));
        ((TextView) v.findViewById(R.id.dancelevelAAA)).setText(String.valueOf(danceLevelAAA));
        ((TextView) v.findViewById(R.id.dancelevelAAp)).setText(String.valueOf(danceLevelAAp));
        ((TextView) v.findViewById(R.id.dancelevelAA)).setText(String.valueOf(danceLevelAA));
        ((TextView) v.findViewById(R.id.dancelevelAAm)).setText(String.valueOf(danceLevelAAm));
        ((TextView) v.findViewById(R.id.dancelevelAp)).setText(String.valueOf(danceLevelAp));
        ((TextView) v.findViewById(R.id.dancelevelA)).setText(String.valueOf(danceLevelA));
        ((TextView) v.findViewById(R.id.dancelevelAm)).setText(String.valueOf(danceLevelAm));
        ((TextView) v.findViewById(R.id.dancelevelBp)).setText(String.valueOf(danceLevelBp));
        ((TextView) v.findViewById(R.id.dancelevelB)).setText(String.valueOf(danceLevelB));
        ((TextView) v.findViewById(R.id.dancelevelBm)).setText(String.valueOf(danceLevelBm));
        ((TextView) v.findViewById(R.id.dancelevelCp)).setText(String.valueOf(danceLevelCp));
        ((TextView) v.findViewById(R.id.dancelevelC)).setText(String.valueOf(danceLevelC));
        ((TextView) v.findViewById(R.id.dancelevelCm)).setText(String.valueOf(danceLevelCm));
        ((TextView) v.findViewById(R.id.dancelevelDp)).setText(String.valueOf(danceLevelDp));
        ((TextView) v.findViewById(R.id.dancelevelD)).setText(String.valueOf(danceLevelD));
        ((TextView) v.findViewById(R.id.dancelevelE)).setText(String.valueOf(danceLevelE));
        ((TextView) v.findViewById(R.id.dancelevelNoPlay)).setText(String.valueOf(danceLevelNoPlay));
        ((TextView) v.findViewById(R.id.fctypeMFC)).setText(String.valueOf(fctypeMFC));
        ((TextView) v.findViewById(R.id.fctypePFC)).setText(String.valueOf(fctypePFC));
        ((TextView) v.findViewById(R.id.fctypeFC)).setText(String.valueOf(fctypeFC));
        ((TextView) v.findViewById(R.id.fctypeGFC)).setText(String.valueOf(fctypeGFC));
        ((TextView) v.findViewById(R.id.fctypeLife4)).setText(String.valueOf(fctypeLife4));
        ((TextView) v.findViewById(R.id.fctypeNoFC)).setText(String.valueOf(fctypeNoFC));
        ((TextView) v.findViewById(R.id.difficulty01)).setText(String.valueOf(difficulties[1]));
        ((TextView) v.findViewById(R.id.difficulty02)).setText(String.valueOf(difficulties[2]));
        ((TextView) v.findViewById(R.id.difficulty03)).setText(String.valueOf(difficulties[3]));
        ((TextView) v.findViewById(R.id.difficulty04)).setText(String.valueOf(difficulties[4]));
        ((TextView) v.findViewById(R.id.difficulty05)).setText(String.valueOf(difficulties[5]));
        ((TextView) v.findViewById(R.id.difficulty06)).setText(String.valueOf(difficulties[6]));
        ((TextView) v.findViewById(R.id.difficulty07)).setText(String.valueOf(difficulties[7]));
        ((TextView) v.findViewById(R.id.difficulty08)).setText(String.valueOf(difficulties[8]));
        ((TextView) v.findViewById(R.id.difficulty09)).setText(String.valueOf(difficulties[9]));
        ((TextView) v.findViewById(R.id.difficulty10)).setText(String.valueOf(difficulties[10]));
        ((TextView) v.findViewById(R.id.difficulty11)).setText(String.valueOf(difficulties[11]));
        ((TextView) v.findViewById(R.id.difficulty12)).setText(String.valueOf(difficulties[12]));
        ((TextView) v.findViewById(R.id.difficulty13)).setText(String.valueOf(difficulties[13]));
        ((TextView) v.findViewById(R.id.difficulty14)).setText(String.valueOf(difficulties[14]));
        ((TextView) v.findViewById(R.id.difficulty15)).setText(String.valueOf(difficulties[15]));
        ((TextView) v.findViewById(R.id.difficulty16)).setText(String.valueOf(difficulties[16]));
        ((TextView) v.findViewById(R.id.difficulty17)).setText(String.valueOf(difficulties[17]));
        ((TextView) v.findViewById(R.id.difficulty18)).setText(String.valueOf(difficulties[18]));
        ((TextView) v.findViewById(R.id.difficulty19)).setText(String.valueOf(difficulties[19]));
        ((TextView) v.findViewById(R.id.patternSingleBeginner)).setText(String.valueOf(patternTypes[0]));
        ((TextView) v.findViewById(R.id.patternSingleBasic)).setText(String.valueOf(patternTypes[1]));
        ((TextView) v.findViewById(R.id.patternSingleDifficult)).setText(String.valueOf(patternTypes[2]));
        ((TextView) v.findViewById(R.id.patternSingleExpert)).setText(String.valueOf(patternTypes[3]));
        ((TextView) v.findViewById(R.id.patternSingleChallenge)).setText(String.valueOf(patternTypes[4]));
        ((TextView) v.findViewById(R.id.patternDoubleBasic)).setText(String.valueOf(patternTypes[5]));
        ((TextView) v.findViewById(R.id.patternDoubleDifficult)).setText(String.valueOf(patternTypes[6]));
        ((TextView) v.findViewById(R.id.patternDoubleExpert)).setText(String.valueOf(patternTypes[7]));
        ((TextView) v.findViewById(R.id.patternDoubleChallenge)).setText(String.valueOf(patternTypes[8]));
        ((TextView) v.findViewById(R.id.version1st)).setText(String.valueOf(versions[0]));
        ((TextView) v.findViewById(R.id.version2nd)).setText(String.valueOf(versions[1]));
        ((TextView) v.findViewById(R.id.version3rd)).setText(String.valueOf(versions[2]));
        ((TextView) v.findViewById(R.id.version4th)).setText(String.valueOf(versions[3]));
        ((TextView) v.findViewById(R.id.version5th)).setText(String.valueOf(versions[4]));
        ((TextView) v.findViewById(R.id.versionMAX)).setText(String.valueOf(versions[5]));
        ((TextView) v.findViewById(R.id.versionMAX2)).setText(String.valueOf(versions[6]));
        ((TextView) v.findViewById(R.id.versionEXTREME)).setText(String.valueOf(versions[7]));
        ((TextView) v.findViewById(R.id.versionSuperNOVA)).setText(String.valueOf(versions[8]));
        ((TextView) v.findViewById(R.id.versionSuperNOVA2)).setText(String.valueOf(versions[9]));
        ((TextView) v.findViewById(R.id.versionX)).setText(String.valueOf(versions[10]));
        ((TextView) v.findViewById(R.id.versionX2)).setText(String.valueOf(versions[11]));
        ((TextView) v.findViewById(R.id.versionX3)).setText(String.valueOf(versions[12]));
        ((TextView) v.findViewById(R.id.version2013)).setText(String.valueOf(versions[13]));
        ((TextView) v.findViewById(R.id.version2014)).setText(String.valueOf(versions[14]));
        ((TextView) v.findViewById(R.id.versionA)).setText(String.valueOf(versions[15]));
        ((TextView) v.findViewById(R.id.versionA20)).setText(String.valueOf(versions[16]));
        ((TextView) v.findViewById(R.id.versionA20PLUS)).setText(String.valueOf(versions[17]));
        ((TextView) v.findViewById(R.id.versionA3)).setText(String.valueOf(versions[18]));
        ((TextView) v.findViewById(R.id.versionWorld)).setText(String.valueOf(versions[19]));

        ((TextView) v.findViewById(R.id.rankEXCount)).setText(String.valueOf(flareRankEX));
        ((TextView) v.findViewById(R.id.rank9Count)).setText(String.valueOf(flareRankIX));
        ((TextView) v.findViewById(R.id.rank8Count)).setText(String.valueOf(flareRankVIII));
        ((TextView) v.findViewById(R.id.rank7Count)).setText(String.valueOf(flareRankVII));
        ((TextView) v.findViewById(R.id.rank6Count)).setText(String.valueOf(flareRankVI));
        ((TextView) v.findViewById(R.id.rank5Count)).setText(String.valueOf(flareRankV));
        ((TextView) v.findViewById(R.id.rank4Count)).setText(String.valueOf(flareRankIV));
        ((TextView) v.findViewById(R.id.rank3Count)).setText(String.valueOf(flareRankIII));
        ((TextView) v.findViewById(R.id.rank2Count)).setText(String.valueOf(flareRankII));
        ((TextView) v.findViewById(R.id.rank1Count)).setText(String.valueOf(flareRankI));
        ((TextView) v.findViewById(R.id.rank0Count)).setText(String.valueOf(flareRank0));
        ((TextView) v.findViewById(R.id.noRankCount)).setText(String.valueOf(flareRankNoRank));

        FileReader.requestAd(v.findViewById(R.id.adContainer), this);

        new AlertDialog.Builder(ScoreList.this)
                .setTitle(c + " (" + FileReader.readMusicFilterName(this, mFilterSpinner.getSelectedItemPosition()) + ")")
                .setView(v)
                .setNegativeButton(this.getResources().getString(R.string.strings_global____ok), (dialog, which) -> {
                    if (dialog != null) {
                        dialog.cancel();
                        dialog = null;
                    }
                })
                .show();
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

    private void userActionFromGateList() {

        new AlertDialog.Builder(ScoreList.this)
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

    private void userActionSou() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScoreList.this);
        alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
        alertDialogBuilder.setTitle(getResources().getString(R.string.sou_dialog_title_lang));
        alertDialogBuilder.setMessage(getResources().getString(R.string.sou_dialog_message_lang));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_sou_myscore),
                (dialog, which) -> {
                    mSouNext = 0;
                    mSouTargetRival = false;
                    mSouList.clear();
                    MusicDataAdapter mda = ((MusicDataAdapter) mScoreListView.getAdapter());
                    int count = mda.getCount();
                    for (int i = 0; i < count; i++) {
                        mSouList.add(mda.getItem(i));
                    }
                    showDialogFromGateSou();
                });
        if (mActiveRivalId != null && mActiveRivalId != "00000000") {
            alertDialogBuilder.setNeutralButton(getResources().getString(R.string.dialog_sou_rivalscore),
                    (dialog, which) -> {
                        mSouNext = 0;
                        mSouTargetRival = true;
                        mSouList.clear();
                        MusicDataAdapter mda = ((MusicDataAdapter) mScoreListView.getAdapter());
                        int count = mda.getCount();
                        for (int i = 0; i < count; i++) {
                            mSouList.add(mda.getItem(i));
                        }
                        showDialogFromGateSou();
                    });
        }
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.strings_global____cancel),
                (dialog, which) -> {
                });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void userActionOpenFilterSetting() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.FilterSetting");
        intent.putExtra("jp.linanfine.dsma.pagerid", mFilterSpinner.getSelectedItemPosition());

        startActivityForResult(intent, 1);
    }

    private void userActionOpenSortSetting() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.SortSetting");
        intent.putExtra("jp.linanfine.dsma.pagerid", mSortSpinner.getSelectedItemPosition());

        startActivityForResult(intent, 1);
    }

    private void userActionSelectFilter() {
        int id = mFilterSpinner.getSelectedItemPosition();
        if (id == mFilterSpinner.getAdapter().getCount() - 1) {
            FileReader.saveMusicFilterCount(ScoreList.this, id + 1);
            FileReader.saveMusicFilterName(ScoreList.this, id, "Filter" + String.valueOf(id));
            FileReader.saveActiveMusicFilter(ScoreList.this, id);

            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.FilterSetting");
            intent.putExtra("jp.linanfine.dsma.pagerid", id);

            startActivityForResult(intent, 1);
        } else {
            if (FileReader.readActiveMusicFilter(ScoreList.this) != id) {
                FileReader.saveActiveMusicFilter(ScoreList.this, id);
                ScoreList.this.initialize();
            }
        }
    }

    private void userActionSelectSort() {
        int id = mSortSpinner.getSelectedItemPosition();
        if (id == mSortSpinner.getAdapter().getCount() - 1) {
            FileReader.saveMusicSortCount(ScoreList.this, id + 1);
            FileReader.saveMusicSortName(ScoreList.this, id, "Sort" + String.valueOf(id));
            FileReader.saveActiveMusicSort(ScoreList.this, id);

            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.SortSetting");
            intent.putExtra("jp.linanfine.dsma.pagerid", id);

            startActivityForResult(intent, 1);
        } else {
            if (FileReader.readActiveMusicSort(ScoreList.this) != id) {
                FileReader.saveActiveMusicSort(ScoreList.this, id);
                ScoreList.this.initialize();
            }
        }
    }

    private void userActionFromGate() {
        mFromGatePattern = mSelectedItemPattern;
        mFromGateRivalId = null;
        mFromGateRivalName = null;
        showDialogFromGate();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void copyText(String text) {
        if (Build.VERSION.SDK_INT < 11) {
            ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cbm.setText(text);
        } else {
            ClipData.Item item = new ClipData.Item(text);
            String[] mimeType = new String[1];
            mimeType[0] = ClipDescription.MIMETYPE_TEXT_PLAIN;
            ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
            ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cbm.setPrimaryClip(cd);
        }
    }

    private void userActionCopyToClipboardList() {
        ArrayList<String> str_items = new ArrayList<>();
        final MusicDataAdapter mda = ((MusicDataAdapter) mScoreListView.getAdapter());
        if (mda.getCount() < 1) {
            return;
        }
        UniquePattern pat = mda.getItem(0);
        MusicData md = pat.musics.get(pat.MusicId);
        ScoreData sd;
        if (!mScoreList.containsKey(pat.MusicId)) {
            sd = new ScoreData();
        } else {
            sd = getScoreDataOfPattern(mScoreList.get(pat.MusicId), pat.Pattern);
        }
        final String[] formats = FileReader.readCopyFormats(this);
        str_items.add(TextUtil.textFromCopyFormat(formats[0], pat, md, sd));
        str_items.add(TextUtil.textFromCopyFormat(formats[1], pat, md, sd));
        str_items.add(TextUtil.textFromCopyFormat(formats[2], pat, md, sd));
        str_items.add(this.getResources().getString(R.string.editcopyformats));

        new AlertDialog.Builder(ScoreList.this)
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                            if (which == 3) {
                                Intent intent = new Intent();
                                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.CopyFormatList");
                                startActivityForResult(intent, 1);
                                return;
                            }
                            String copyText = "";
                            for (int index = 0; index < mda.getCount(); ++index) {
                                UniquePattern pat1 = mda.getItem(index);
                                MusicData md1 = pat1.musics.get(pat1.MusicId);
                                ScoreData sd1;
                                if (!mScoreList.containsKey(pat1.MusicId)) {
                                    sd1 = new ScoreData();
                                } else {
                                    sd1 = getScoreDataOfPattern(mScoreList.get(pat1.MusicId), pat1.Pattern);
                                }
                                copyText = copyText + TextUtil.textFromCopyFormat(formats[which], pat1, md1, sd1) + "\n";
                            }
                            copyText(copyText);
                            Toast.makeText(ScoreList.this, ScoreList.this.getResources().getString(R.string.dialog_copied_to_clipboard), Toast.LENGTH_LONG).show();
                        }
                ).show();
    }

    private void userActionCopyToClipboard() {
        final ArrayList<String> str_items = new ArrayList<>();
        MusicData md = mMusicList.get(mSelectedItemPattern.MusicId);
        ScoreData sd;
        if (!mScoreList.containsKey(mSelectedItemPattern.MusicId)) {
            sd = new ScoreData();
        } else {
            sd = getScoreDataOfPattern(mScoreList.get(mSelectedItemPattern.MusicId), mSelectedItemPattern.Pattern);
        }
        final String[] formats = FileReader.readCopyFormats(this);
        str_items.add(TextUtil.textFromCopyFormat(formats[0], mSelectedItemPattern, md, sd));
        str_items.add(TextUtil.textFromCopyFormat(formats[1], mSelectedItemPattern, md, sd));
        str_items.add(TextUtil.textFromCopyFormat(formats[2], mSelectedItemPattern, md, sd));
        str_items.add(this.getResources().getString(R.string.editcopyformats));

        new AlertDialog.Builder(ScoreList.this)
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                            if (which == 3) {
                                Intent intent = new Intent();
                                intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.CopyFormatList");
                                startActivityForResult(intent, 1);
                                return;
                            }
                            copyText(str_items.get(which));
                            Toast.makeText(ScoreList.this, ScoreList.this.getResources().getString(R.string.dialog_copied_to_clipboard), Toast.LENGTH_LONG).show();
                        }
                ).show();
    }

    private void userActionDirectEdit() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreEdit");
        intent.putExtra("jp.linanfine.dsma.musicid", mSelectedItemPattern.MusicId);
        intent.putExtra("jp.linanfine.dsma.pattern", mSelectedItemPattern.Pattern);

        startActivityForResult(intent, 1);
    }

    private void userActionRivalFromGate() {
        mFromGatePattern = mSelectedItemPattern;
        mFromGateRivalId = mActiveRivalId;
        mFromGateRivalName = mActiveRivalName;
        showDialogFromGate();
    }

    private void userActionRivalDirectEdit() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreEdit");
        intent.putExtra("jp.linanfine.dsma.musicid", mSelectedItemPattern.MusicId);
        intent.putExtra("jp.linanfine.dsma.pattern", mSelectedItemPattern.Pattern);
        intent.putExtra("jp.linanfine.dsma.rivalid", mActiveRivalId);
        intent.putExtra("jp.linanfine.dsma.rivalname", mActiveRivalName);

        startActivityForResult(intent, 1);
    }

    private void userActionAddToMyList() {
        ArrayList<String> items = new ArrayList<>();
        int count = FileReader.readMyListCount(ScoreList.this);
        for (int i = 0; i < count; i++) {
            items.add(FileReader.readMyListName(ScoreList.this, i));
        }
        items.add(getResources().getString(R.string.dialog_new_list));

        mMyListCount = count;

        new AlertDialog.Builder(ScoreList.this)
                .setTitle(getResources().getString(R.string.dialog_add_to_list))
                .setItems(items.toArray(new String[0]), (dialog, which) -> {
                            if (which == mMyListCount) {
                                //テキスト入力を受け付けるビューを作成します。
                                mTemporaryDialogEditText = (EditText) ScoreList.this.getLayoutInflater().inflate(R.layout.view_singleline_edit_text, null);
                                mTemporaryDialogEditText.setText("MyList" + mMyListCount);
                                mTemporaryDialogEditText.setSelectAllOnFocus(true);
                                new AlertDialog.Builder(ScoreList.this)
                                        .setIcon(drawable.ic_dialog_info)
                                        .setTitle(ScoreList.this.getResources().getString(R.string.strings____Dialog_MyList____editMylistName))
                                        //setViewにてビューを設定します。
                                        .setView(mTemporaryDialogEditText)
                                        .setPositiveButton("OK", (dialog12, whichButton) -> {
                                            FileReader.saveMyListCount(ScoreList.this, mMyListCount + 1);
                                            String name = mTemporaryDialogEditText.getText().toString();
                                            FileReader.saveMyListName(ScoreList.this, mMyListCount, name);
                                            ArrayList<UniquePattern> list = new ArrayList<>();
                                            list.add(mSelectedItemPattern);
                                            FileReader.saveMyList(ScoreList.this, mMyListCount, list);
                                            Toast.makeText(ScoreList.this, getResources().getString(R.string.toast_added_to_mylist1) + name + getResources().getString(R.string.toast_added_to_mylist2), Toast.LENGTH_SHORT).show();
                                        })
                                        .setNegativeButton("Cancel", (dialog1, whichButton) -> {
                                        })
                                        .show();
                            } else {
                                ArrayList<UniquePattern> list = FileReader.readMyList(ScoreList.this, which);
                                list.add(mSelectedItemPattern);
                                FileReader.saveMyList(ScoreList.this, which, list);
                                Toast.makeText(ScoreList.this, getResources().getString(R.string.toast_added_to_mylist1) + FileReader.readMyListName(ScoreList.this, which) + getResources().getString(R.string.toast_added_to_mylist2), Toast.LENGTH_SHORT).show();
                            }
                        }
                ).show();
    }

    private void userActionRemoveFromMyList() {
        ArrayList<UniquePattern> pats = FileReader.readMyList(ScoreList.this, mCategoryMyListMylistId);
        int count = pats.size();
        int r = -1;
        for (int i = 0; i < count; i++) {
            UniquePattern pat = pats.get(i);
            if (pat.MusicId == mSelectedItemPattern.MusicId && pat.Pattern == mSelectedItemPattern.Pattern) {
                r = i;
            }
        }
        if (r != -1) {
            pats.remove(r);
        }
        FileReader.saveMyList(ScoreList.this, mCategoryMyListMylistId, pats);

        initialize();
    }

    private boolean userActionOpenOwnMusic() {
        if (!mCategory.equals("Own Music")) {
            Intent intent = new Intent();
            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.ScoreList");
            intent.putExtra("jp.linanfine.dsma.category", "Own Music");
            intent.putExtra("jp.linanfine.dsma.musicid", mSelectedItemPattern.MusicId);

            startActivityForResult(intent, 1);

            return true;
        }
        return false;
    }

    private void userActionSelectRivalAction() {
        ArrayList<String> str_items = new ArrayList<>();
        str_items.add(getResources().getString(R.string.dialog_from_gate));
        str_items.add(getResources().getString(R.string.dialog_direct_edit));

        new AlertDialog.Builder(ScoreList.this)
                .setTitle(mActiveRivalName)
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                    switch (which) {
                        case 0:
                            userActionRivalFromGate();
                            break;
                        case 1:
                            userActionRivalDirectEdit();
                            break;
                    }
                }).show();
    }

    @SuppressWarnings("unused")
    private void userActionShare() {
        ArrayList<String> str_items = new ArrayList<>();
        str_items.add(getResources().getString(R.string.menu_music_share_copy));
        str_items.add(getResources().getString(R.string.menu_music_share_action_send));

        new AlertDialog.Builder(ScoreList.this)
                .setTitle(getResources().getString(R.string.menu_music_share))
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                            MusicData m = mSelectedItemPattern.musics.get(mSelectedItemPattern.MusicId);
                            MusicScore scoredata = mScoreList.get(mSelectedItemPattern.MusicId);
                            ScoreData escore;
                            switch (mSelectedItemPattern.Pattern) {
                                case bSP:
                                    escore = scoredata.bSP;
                                    break;
                                case BSP:
                                    escore = scoredata.BSP;
                                    break;
                                case DSP:
                                    escore = scoredata.DSP;
                                    break;
                                case ESP:
                                    escore = scoredata.ESP;
                                    break;
                                case CSP:
                                    escore = scoredata.CSP;
                                    break;
                                case BDP:
                                    escore = scoredata.BDP;
                                    break;
                                case DDP:
                                    escore = scoredata.DDP;
                                    break;
                                case EDP:
                                    escore = scoredata.EDP;
                                    break;
                                case CDP:
                                    escore = scoredata.CDP;
                                    break;
                                default:
                                    escore = new ScoreData();
                                    break;
                            }
                            String copyText = m.Name + " [" + mSelectedItemPattern.Pattern.toString() + "] " + String.valueOf(escore.Score);
                        }
                ).show();
    }

    private void userActionShowItemMenu() {
        ArrayList<String> str_items = new ArrayList<>();
        str_items.add(getResources().getString(R.string.dialog_copy_to_clipboard));
        str_items.add(getResources().getString(R.string.dialog_from_gate));
        str_items.add(getResources().getString(R.string.dialog_direct_edit));
        if (mActiveRivalId != null && mActiveRivalId != "00000000") {
            str_items.add(getResources().getString(R.string.menu_music_rivals));
        }
        str_items.add(getResources().getString(R.string.dialog_add_to_list));

        if (mCategory.equals("My List")) {
            int count = mMusicFilter.MusicIdList.size();
            boolean exists = false;
            for (int i = 0; i < count; i++) {
                if (mSelectedItemPattern.MusicId == mMusicFilter.MusicIdList.get(i) && mSelectedItemPattern.Pattern.equals(mMusicFilter.MusicPatternList.get(i))) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                str_items.add(getResources().getString(R.string.dialog_remove_from_list));
            }
        }

        if (!mCategory.equals("Own Music")) {
            str_items.add(getResources().getString(R.string.dialog_disp_all_pattern));
        }

        str_items.add(getResources().getString(R.string.dialog_disp_song_memo));

        new AlertDialog.Builder(ScoreList.this)
                .setTitle(mSelectedItemPattern.Pattern.toString() + " : " + mSelectedMusicData.Name)
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                            if (mCategory.equals("My List")) {
                                if (mActiveRivalId != null && !mActiveRivalId.equals("00000000")) {
                                    switch (which) {
                                        case 0:
                                            userActionCopyToClipboard();
                                            break;
                                        case 1:
                                            userActionFromGate();
                                            break;
                                        case 2:
                                            userActionDirectEdit();
                                            break;
                                        case 3:
                                            userActionSelectRivalAction();
                                            break;
                                        case 4:
                                            userActionAddToMyList();
                                            break;
                                        case 5:
                                            userActionRemoveFromMyList();
                                            break;
                                        case 6:
                                            userActionOpenOwnMusic();
                                            break;
                                        case 7:
                                            userActionSongMemo();
                                    }
                                } else {
                                    switch (which) {
                                        case 0:
                                            userActionCopyToClipboard();
                                            break;
                                        case 1:
                                            userActionFromGate();
                                            break;
                                        case 2:
                                            userActionDirectEdit();
                                            break;
                                        case 3:
                                            userActionAddToMyList();
                                            break;
                                        case 4:
                                            userActionRemoveFromMyList();
                                            break;
                                        case 5:
                                            userActionOpenOwnMusic();
                                            break;
                                        case 6:
                                            userActionSongMemo();
                                    }
                                }
                            } else {
                                if (mActiveRivalId != null && mActiveRivalId != "00000000") {
                                    switch (which) {
                                        case 0:
                                            userActionCopyToClipboard();
                                            break;
                                        case 1:
                                            userActionFromGate();
                                            break;
                                        case 2:
                                            userActionDirectEdit();
                                            break;
                                        case 3:
                                            userActionSelectRivalAction();
                                            break;
                                        case 4:
                                            userActionAddToMyList();
                                            break;
                                        case 5:
                                            userActionOpenOwnMusic();
                                            break;
                                        case 6:
                                            userActionSongMemo();
                                    }
                                } else {
                                    switch (which) {
                                        case 0:
                                            userActionCopyToClipboard();
                                            break;
                                        case 1:
                                            userActionFromGate();
                                            break;
                                        case 2:
                                            userActionDirectEdit();
                                            break;
                                        case 3:
                                            userActionAddToMyList();
                                            break;
                                        case 4:
                                            userActionOpenOwnMusic();
                                            break;
                                        case 5:
                                            userActionSongMemo();
                                    }
                                }
                            }
                        }
                ).show();
    }

    private void userActionOpenFromGateRecent() {
        showDialogFromGateRecent();
    }

    private void userActionOmikuji() {
        MusicDataAdapter mda = ((MusicDataAdapter) mScoreListView.getAdapter());
        if (mda.getCount() > 0) {
            int index = new Random().nextInt(mda.getCount());
            View view = ScoreList.this.getLayoutInflater().inflate(R.layout.view_omikuji, null);
            View iv = mda.getView(index, null, null);
            ((LinearLayout) view.findViewById(R.id.itemContainer)).addView(iv);
            UniquePattern pat = mda.getItem(index);
            MusicData md = pat.musics.get(pat.MusicId);
            String spdp = "";
            String pattern = "";
            int level = 0;
            int textColor = 0x00000000;
            switch (pat.Pattern) {
                case bSP:
                    textColor = 0xff66ffff;
                    level = md.Difficulty_bSP;
                    spdp = "SINGLE";
                    pattern = "BEGINNER";
                    break;
                case BSP:
                    textColor = 0xffff9900;
                    level = md.Difficulty_BSP;
                    spdp = "SINGLE";
                    pattern = "BASIC";
                    break;
                case DSP:
                    textColor = 0xffff0000;
                    level = md.Difficulty_DSP;
                    spdp = "SINGLE";
                    pattern = "DIFFICULT";
                    break;
                case ESP:
                    textColor = 0xff00ff00;
                    level = md.Difficulty_ESP;
                    spdp = "SINGLE";
                    pattern = "EXPERT";
                    break;
                case CSP:
                    textColor = 0xffff00ff;
                    level = md.Difficulty_CSP;
                    spdp = "SINGLE";
                    pattern = "CHALLENGE";
                    break;
                case BDP:
                    textColor = 0xffff9900;
                    level = md.Difficulty_BDP;
                    spdp = "DOUBLE";
                    pattern = "BASIC";
                    break;
                case DDP:
                    textColor = 0xffff0000;
                    level = md.Difficulty_DDP;
                    spdp = "DOUBLE";
                    pattern = "DIFFICULT";
                    break;
                case EDP:
                    textColor = 0xff00ff00;
                    level = md.Difficulty_EDP;
                    spdp = "DOUBLE";
                    pattern = "EXPERT";
                    break;
                case CDP:
                    textColor = 0xffff00ff;
                    level = md.Difficulty_CDP;
                    spdp = "DOUBLE";
                    pattern = "CHALLENGE";
                    break;
            }
            ((TextView) view.findViewById(R.id.name)).setText(md.Name);
            ((TextView) view.findViewById(R.id.pattern)).setTextColor(textColor);
            ((TextView) view.findViewById(R.id.pattern)).setText(spdp + " " + pattern + " (" + level + ")");

            new AlertDialog.Builder(ScoreList.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(ScoreList.this.getResources().getString(R.string.strings____Dialog_Omikuji____omikuji))
                    .setView(view)
                    .setCancelable(true)
                    .setNegativeButton(ScoreList.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                    })
                    .show();
        } else {
            new AlertDialog.Builder(ScoreList.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(ScoreList.this.getResources().getString(R.string.strings____Dialog_Omikuji____omikuji))
                    .setMessage(ScoreList.this.getResources().getString(R.string.global_setting_importzero))
                    .setCancelable(true)
                    .setNegativeButton(ScoreList.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                    })
                    .show();
        }
    }

    private void userActionSongMemo() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.SongMemo");
        intent.putExtra("jp.linanfine.dsma.musicid", mSelectedItemPattern.MusicId);
        intent.putExtra("jp.linanfine.dsma.musicname", mSelectedMusicData.Name);

        startActivityForResult(intent, 1);
    }

    private void userActionFlareSkillNote() {
        Intent intent = new Intent();
        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.FlareSkillNote");

        startActivityForResult(intent, 1);
    }

    private void initialize() {

        this.findViewById(R.id.noItems).setVisibility(View.GONE);
        this.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        mAppearance = FileReader.readAppearanceSettings(this);
        mGestures = FileReader.readGestureSettings(this);
        mUseAsyncDraw = FileReader.readUseAsyncDraw(this);
        mUseOldStyleDraw = FileReader.readUseOldStyleDraw(this);

        mSouProgressDialog = new ProgressDialog(this);
        mSouProgressDialog.setTitle(getResources().getString(R.string.sou_progress_title));
        mSouProgressDialog.setMessage(getResources().getString(R.string.sou_progress_message));
        mSouProgressDialog.setIndeterminate(false);
        mSouProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mSouProgressDialog.setCancelable(false);
        mSouProgressDialog.setOnCancelListener(dialog -> mCancelSouThread = true);
        mSouProgressDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                (dialog, which) -> {
                    if (dialog != null) {
                        dialog.cancel();
                        dialog = null;
                    }
                }
        );

        this.findViewById(R.id.menuButton).setOnClickListener(v -> userActionShowSystemMenu());

        this.findViewById(R.id.filterSetting).setOnClickListener(v -> userActionOpenFilterSetting());

        this.findViewById(R.id.sortSetting).setOnClickListener(v -> userActionOpenSortSetting());

        {
            mFilterSpinner = this.findViewById(R.id.filterSelect);
            mSortSpinner = this.findViewById(R.id.sortSelect);

            mFilterSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    userActionSelectFilter();
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            mSortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    userActionSelectSort();
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }

        mScoreListView = this.findViewById(R.id.musicList);
        mScoreListView.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                int s = mScoreListView.getFirstVisiblePosition();
                if (!mScrollPositionUpdateLock && s >= 0) {
                    mScrollPosition = mScoreListView.getFirstVisiblePosition();
                }
            }

            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }
        });
        mScoreListView.setOnItemClickListener((parent, view, position, id) -> {

            mSelectedItemPattern = (UniquePattern) mScoreListView.getAdapter().getItem(position);
            mSelectedMusicData = mMusicList.get(mSelectedItemPattern.MusicId);

            execUserAction(UserActionFrom.ItemClick);
        });
        mScoreListView.setOnItemLongClickListener((parent, view, position, id) -> {

            mSelectedItemPattern = (UniquePattern) mScoreListView.getAdapter().getItem(position);
            mSelectedMusicData = mMusicList.get(mSelectedItemPattern.MusicId);

            return !execUserAction(UserActionFrom.ItemLongClick);
        });

// add by taiheisan start
        // ListViewタッチイベント
        mScoreListView.setOnTouchListener(new View.OnTouchListener() {

            // add by linanfine start
            private ListViewItemMakerSurfaceView.MgrView lastPressed = null;
            // add by linanfine end

            private float lastTouchX;
            private float currentX;

            // フリックの遊び部分（最低限移動しないといけない距離）
            private float adjust = 150;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // add by linanfine start
                        if (lastPressed != null) {
                            lastPressed.setIsPressed(false);
                        }
                        int pressed = mScoreListView.pointToPosition((int) event.getX(), (int) event.getY()) - mScoreListView.getFirstVisiblePosition();
                        if (pressed >= 0 && pressed < mScoreListView.getCount()) {
                            View c = mScoreListView.getChildAt(pressed);
                            if (c != null) {
                                c = c.findViewById(R.id.mgr);
                                if (c instanceof ListViewItemMakerSurfaceView.MgrView) {
                                    lastPressed = (ListViewItemMakerSurfaceView.MgrView) c;
                                    lastPressed.setIsPressed(true);
                                }
                            }
                        }
                        // add by linanfine end
                        lastTouchX = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        // add by linanfine start
                        if (lastPressed != null) {
                            lastPressed.setIsPressed(false);
                            lastPressed = null;
                        }
                        if (!mGestures.GestureEnabled) {
                            break;
                        }
                        // add by linanfine end
                        currentX = event.getX();
                        if (lastTouchX + adjust < currentX) {
                            // 右フリック（一つ前に戻る）
                            initializeByFlick(-1);
                            return true;
                        } else if (lastTouchX > currentX + adjust) {
                            // 左フリック（次へ進む）
                            initializeByFlick(1);
                            return true;
                        }
                        break;

                }
                return false;
            }

            /**
             * フリック操作の画面初期表示（カテゴリ再設定）
             * @param val 増加値(左：+1、右：-1)
             */
            private void initializeByFlick(int val) {
                String[] categoryArray = null;    // カテゴリ配列格納
                int categoryIndex = 0;                // カテゴリ配列Index

                // 現在どのカテゴリかにより分岐
                if (mCategory.startsWith("Ser")) {
                    // Series Title
                    categoryArray = SERIES_TITLE_LIST;

                } else if (mCategory.startsWith("Abc")) {
                    // ABC
                    categoryArray = ABC_LIST;

                } else if (mCategory.startsWith("Dif")) {
                    // Difficulty
                    categoryArray = DIFF_LIST;

                } else if (mCategory.startsWith("Rank")) {
                    // Dance Level
                    categoryArray = DANCE_LEVEL_LIST;

                } else if (mCategory.startsWith("Fc")) {
                    // Full Combo Type
                    categoryArray = FULL_COMBO_TYPE_LIST;

                } else {
                    // 何もしないで終了
                    return;
                }

                // 各リストから現在選択されているカテゴリのIndexを設定
                for (int i = 0; i < categoryArray.length; i++) {
                    if (categoryArray[i].equals(mCategory)) {
                        categoryIndex = i;
                        break;
                    }
                }
                // カテゴリ配列Indexを右or左にずらす
                categoryIndex += val;

                // 最初・最後でフリックされた場合は、逆側を表示する
                if (categoryIndex < 0) {
                    // 0より小さい場合は最後のIndexにする
                    categoryIndex = categoryArray.length - 1;
                } else if (categoryIndex >= categoryArray.length) {
                    // 最後まで言った場合は最初に戻る
                    categoryIndex = 0;
                }

                // カテゴリをセット
                mCategory = categoryArray[categoryIndex];
// add by linanfine start
                mScrollPosition = 0;
                ScoreList.this.restoreScrollPosition();
                mScrollPositionUpdateLock = true;
// add by linanfine end
                // 再表示
                initialize();
            }
        });
// add by taiheisan end

        if (mCategory.equals("Recents")) {
            ImageView refreshButton = this.findViewById(R.id.refresh);
            refreshButton.setVisibility(View.VISIBLE);

            refreshButton.setOnClickListener(v -> userActionOpenFromGateRecent());
        }
        mScoreList = FileReader.readScoreList(this, null);
        mMusicList = FileReader.readMusicList(this);

        // フレアスキルの更新
        FlareSkillUpdater.updateAllFlareSkills(mMusicList, mScoreList);

        mWebMusicIds = FileReader.readWebMusicIds(this);
        int activeRival = FileReader.readActiveRival(this);
        Log.d("hoge", String.valueOf(activeRival));
        if (activeRival == -1) {
            mActiveRivalScoreList = null;
            mActiveRivalId = null;
            mActiveRivalName = null;
        } else if (activeRival == -2) {
            mActiveRivalId = "00000000";
            mActiveRivalName = "DDR2014";
            mActiveRivalScoreList = FileReader.readScoreList(ScoreList.this, mActiveRivalId);
        } else {
            RivalData r = FileReader.readRivals(this).get(activeRival);
            mActiveRivalId = r.Id;
            mActiveRivalName = r.Name;
            mActiveRivalScoreList = FileReader.readScoreList(ScoreList.this, mActiveRivalId);
        }
        if (mAppearance.ShowComments) {
            mComments = FileReader.readComments(this);
        }
        filterSortRefresh();
        listRefresh();
    }

    private void restoreScrollPosition() {
        mScoreListView.setSelectionFromTop(mListViewAdapter.getCount() - 1, 0);
        mScoreListView.setSelectionFromTop(mScrollPosition, 0);
        this.findViewById(R.id.progressBar).setVisibility(View.GONE);
        int count = mListViewAdapter.getCount();
        if (count == 0) {
            this.findViewById(R.id.noItems).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.noItems).setVisibility(View.GONE);
        }
        mScrollPositionUpdateLock = false;
    }

    private void setListedCount() {
        int count = mListViewAdapter.getCount();
        int clearCount = 0;
        for (int i = 0; i < count; i++) {
            UniquePattern pat = mListViewAdapter.getItem(i);
            if (mScoreList.containsKey(pat.MusicId)) {
                MusicScore ms = mScoreList.get(pat.MusicId);
                ScoreData score = getScoreDataOfPattern(ms, pat.Pattern);
                if (score.Rank != MusicRank.Noplay && score.Rank != MusicRank.E) {
                    clearCount++;
                }
            }
        }
        String c = mCategory;
        if (c.equals("Abc***NUM***")) c = "AbcNUM";
        c = c
                .replace("Abc", "Name ")
                .replace("Dif", "Difficulty ")
                .replace("SerWORLD", "DDR WORLD")
                .replace("SerA3", "DDR A3")
                .replace("SerA20 PLUS", "DDR A20 PLUS")
                .replace("SerA20", "DDR A20")
                .replace("SerA", "DDR A")
                .replace("Ser2014", "DDR 2014")
                .replace("Ser2013", "DDR 2013")
                .replace("Ser", "")
                .replace("WinLoseRival", "Rival ")
                .replace("RankRival", "Rival ")
                .replace("FcRival", "Rival ")
                .replace("Rank", "Dance Level ")
                .replace("Fc", "")
        ;
        if (c.equals("Own Music")) {
            Intent intent = getIntent();
            if (intent == null) {
                return;
            }
            int itemId = intent.getIntExtra("jp.linanfine.dsma.musicid", -1);
            c = mMusicList.get(itemId).Name;
        }
        if (c.equals("My List")) {
            Intent intent = getIntent();
            if (intent == null) {
                return;
            }
            int mylistId = intent.getIntExtra("jp.linanfine.dsma.mylistid", -1);
            c = FileReader.readMyListName(this, mylistId);
        }
        ((TextView) this.findViewById(R.id.musicName)).setText(c + " (" + String.valueOf(clearCount) + "/" + String.valueOf(count) + ")");
    }

    private void listRefresh() {
        MusicDataAdapterArguments mdaa = new MusicDataAdapterArguments();
        mdaa.UseAsyncDraw = mUseAsyncDraw;
        mdaa.Musics = mMusicList;
        mdaa.WebMusicIds = mWebMusicIds;
        mdaa.Scores = mScoreList;
        mdaa.Comments = mComments;
        mdaa.Filter = mMusicFilter;
        mdaa.Sort = mMusicSort;
        mdaa.RivalName = mActiveRivalName;
        mdaa.RivalScores = mActiveRivalScoreList;
        mScrollPositionUpdateLock = true;
        mListViewAdapter = new MusicDataAdapterSurfaceView(this, mAppearance, mdaa);
        mScoreListView.setFastScrollEnabled(true);
        mScoreListView.setAdapter(mListViewAdapter);
    }

    private void filterSortRefresh() {
        {
            int count = FileReader.readMusicFilterCount(this);
            ArrayList<String> items = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                items.add(FileReader.readMusicFilterName(this, i));
            }
            items.add(getResources().getString(R.string.filter_new));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFilterSpinner.setAdapter(adapter);
        }

        {
            int count = FileReader.readMusicSortCount(this);
            ArrayList<String> items = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                items.add(FileReader.readMusicSortName(this, i));
            }
            items.add(getResources().getString(R.string.sort_new));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSortSpinner.setAdapter(adapter);
        }

        if (mCategory.equals("Own Music")) {
            this.findViewById(R.id.filterLayout).setVisibility(View.GONE);
            mMusicSort = new MusicSort();
            mMusicSort._1stType = MusicSortType.SPDP;
            mMusicSort._1stOrder = SortOrder.Ascending;
            mMusicSort._2ndType = MusicSortType.Pattern;
            mMusicSort._2ndOrder = SortOrder.Ascending;
            mMusicFilter = new MusicFilter();
            mMusicFilter.Title = mMusicList.get(mCategoryOwnMusicMusicId).Name;
        } else {
            int activeFilter = FileReader.readActiveMusicFilter(this);
            int activeSort = FileReader.readActiveMusicSort(this);
            mMusicFilter = FileReader.readMusicFilter(this, activeFilter);
            mMusicSort = FileReader.readMusicSort(this, activeSort);
            mFilterSpinner.setSelection(activeFilter);
            mSortSpinner.setSelection(activeSort);
            if (mCategory.equals("Recents")) {
                ScoreList.this.findViewById(R.id.sortWrapper).setVisibility(View.GONE);
                ArrayList<RecentData> recents = FileReader.readRecentList(this);
                mMusicFilter.MusicIdList = new ArrayList<>();
                mMusicFilter.MusicPatternList = new ArrayList<>();
                int count = recents.size();
                for (int i = 0; i < count; ++i) {
                    RecentData mid = recents.get(i);
                    mMusicFilter.MusicIdList.add(mid.Id);
                    mMusicFilter.MusicPatternList.add(mid.PatternType_);
                }
                mMusicSort = new MusicSortRecent(mMusicSort, recents);
            } else if (mCategory.equals("My List")) {
                ArrayList<UniquePattern> pats = FileReader.readMyList(this, mCategoryMyListMylistId);
                mMusicFilter.MusicIdList = new ArrayList<>();
                mMusicFilter.MusicPatternList = new ArrayList<>();
                int count = pats.size();
                for (int i = 0; i < count; i++) {
                    UniquePattern pat = pats.get(i);
                    mMusicFilter.MusicIdList.add(pat.MusicId);
                    mMusicFilter.MusicPatternList.add(pat.Pattern);
                }
            } else if (mCategory.startsWith("Abc")) {
                mMusicFilter.StartsWith = mCategory.substring(3);
            } else if (mCategory.startsWith("Dif")) {
                if (!mCategory.equals("Dif1")) mMusicFilter.Dif1 = false;
                if (!mCategory.equals("Dif2")) mMusicFilter.Dif2 = false;
                if (!mCategory.equals("Dif3")) mMusicFilter.Dif3 = false;
                if (!mCategory.equals("Dif4")) mMusicFilter.Dif4 = false;
                if (!mCategory.equals("Dif5")) mMusicFilter.Dif5 = false;
                if (!mCategory.equals("Dif6")) mMusicFilter.Dif6 = false;
                if (!mCategory.equals("Dif7")) mMusicFilter.Dif7 = false;
                if (!mCategory.equals("Dif8")) mMusicFilter.Dif8 = false;
                if (!mCategory.equals("Dif9")) mMusicFilter.Dif9 = false;
                if (!mCategory.equals("Dif10")) mMusicFilter.Dif10 = false;
                if (!mCategory.equals("Dif11")) mMusicFilter.Dif11 = false;
                if (!mCategory.equals("Dif12")) mMusicFilter.Dif12 = false;
                if (!mCategory.equals("Dif13")) mMusicFilter.Dif13 = false;
                if (!mCategory.equals("Dif14")) mMusicFilter.Dif14 = false;
                if (!mCategory.equals("Dif15")) mMusicFilter.Dif15 = false;
                if (!mCategory.equals("Dif16")) mMusicFilter.Dif16 = false;
                if (!mCategory.equals("Dif17")) mMusicFilter.Dif17 = false;
                if (!mCategory.equals("Dif18")) mMusicFilter.Dif18 = false;
                if (!mCategory.equals("Dif19")) mMusicFilter.Dif19 = false;
            } else if (mCategory.startsWith("Ser")) {
                if (!mCategory.equals("SerWORLD")) mMusicFilter.SerWorld = false;
                if (!mCategory.equals("SerA3")) mMusicFilter.SerA3 = false;
                if (!mCategory.equals("SerA20 PLUS")) mMusicFilter.SerA20PLUS = false;
                if (!mCategory.equals("SerA20")) mMusicFilter.SerA20 = false;
                if (!mCategory.equals("SerA")) mMusicFilter.SerA = false;
                if (!mCategory.equals("Ser2014")) mMusicFilter.Ser2014 = false;
                if (!mCategory.equals("Ser2013")) mMusicFilter.Ser2013 = false;
                if (!mCategory.equals("SerX3")) mMusicFilter.SerX3 = false;
                if (!mCategory.equals("Servs 2nd MIX")) mMusicFilter.SerX3vs2ndMIX = false;
                if (!mCategory.equals("SerX2")) mMusicFilter.SerX2 = false;
                if (!mCategory.equals("SerX")) mMusicFilter.SerX = false;
                if (!mCategory.equals("SerSuperNOVA 2")) mMusicFilter.SerSuperNova2 = false;
                if (!mCategory.equals("SerSuperNOVA")) mMusicFilter.SerSuperNova = false;
                if (!mCategory.equals("SerEXTREME")) mMusicFilter.SerEXTREME = false;
                if (!mCategory.equals("SerMAX2")) mMusicFilter.SerMAX2 = false;
                if (!mCategory.equals("SerMAX")) mMusicFilter.SerMAX = false;
                if (!mCategory.equals("Ser5th")) mMusicFilter.Ser5th = false;
                if (!mCategory.equals("Ser4th")) mMusicFilter.Ser4th = false;
                if (!mCategory.equals("Ser3rd")) mMusicFilter.Ser3rd = false;
                if (!mCategory.equals("Ser2nd")) mMusicFilter.Ser2nd = false;
                if (!mCategory.equals("Ser1st")) mMusicFilter.Ser1st = false;
            } else if (mCategory.startsWith("RankRival")) {
                if (!mCategory.equals("RankRivalAAA")) mMusicFilter.RankAAArival = false;
                if (!mCategory.equals("RankRivalAA+")) mMusicFilter.RankAAprival = false;
                if (!mCategory.equals("RankRivalAA")) mMusicFilter.RankAArival = false;
                if (!mCategory.equals("RankRivalAA-")) mMusicFilter.RankAAmrival = false;
                if (!mCategory.equals("RankRivalA+")) mMusicFilter.RankAprival = false;
                if (!mCategory.equals("RankRivalA")) mMusicFilter.RankArival = false;
                if (!mCategory.equals("RankRivalA-")) mMusicFilter.RankAmrival = false;
                if (!mCategory.equals("RankRivalB+")) mMusicFilter.RankBprival = false;
                if (!mCategory.equals("RankRivalB")) mMusicFilter.RankBrival = false;
                if (!mCategory.equals("RankRivalB-")) mMusicFilter.RankBmrival = false;
                if (!mCategory.equals("RankRivalC+")) mMusicFilter.RankCprival = false;
                if (!mCategory.equals("RankRivalC")) mMusicFilter.RankCrival = false;
                if (!mCategory.equals("RankRivalC-")) mMusicFilter.RankCmrival = false;
                if (!mCategory.equals("RankRivalD+")) mMusicFilter.RankDprival = false;
                if (!mCategory.equals("RankRivalD")) mMusicFilter.RankDrival = false;
                if (!mCategory.equals("RankRivalE")) mMusicFilter.RankErival = false;
                if (!mCategory.equals("RankRivalNoPlay")) mMusicFilter.RankNoPlayrival = false;
            } else if (mCategory.startsWith("FcRival")) {
                if (!mCategory.equals("FcRivalMFC")) mMusicFilter.FcMFCrival = false;
                if (!mCategory.equals("FcRivalPFC")) mMusicFilter.FcPFCrival = false;
                if (!mCategory.equals("FcRivalGFC")) mMusicFilter.FcGFCrival = false;
                if (!mCategory.equals("FcRivalFC")) mMusicFilter.FcFCrival = false;
                if (!mCategory.equals("FcRivalLife4")) mMusicFilter.FcLife4rival = false;
                if (!mCategory.equals("FcRivalNoFC")) mMusicFilter.FcNoFCrival = false;
                if (!mCategory.equals("FcRivalFailed")) mMusicFilter.RankErival = false;
                if (!mCategory.equals("FcRivalNoPlay")) mMusicFilter.RankNoPlayrival = false;
            } else if (mCategory.startsWith("Rank")) {
                if (!mCategory.equals("RankAAA")) mMusicFilter.RankAAA = false;
                if (!mCategory.equals("RankAA+")) mMusicFilter.RankAAp = false;
                if (!mCategory.equals("RankAA")) mMusicFilter.RankAA = false;
                if (!mCategory.equals("RankAA-")) mMusicFilter.RankAAm = false;
                if (!mCategory.equals("RankA+")) mMusicFilter.RankAp = false;
                if (!mCategory.equals("RankA")) mMusicFilter.RankA = false;
                if (!mCategory.equals("RankA-")) mMusicFilter.RankAm = false;
                if (!mCategory.equals("RankB+")) mMusicFilter.RankBp = false;
                if (!mCategory.equals("RankB")) mMusicFilter.RankB = false;
                if (!mCategory.equals("RankB-")) mMusicFilter.RankBm = false;
                if (!mCategory.equals("RankC+")) mMusicFilter.RankCp = false;
                if (!mCategory.equals("RankC")) mMusicFilter.RankC = false;
                if (!mCategory.equals("RankC-")) mMusicFilter.RankCm = false;
                if (!mCategory.equals("RankD+")) mMusicFilter.RankDp = false;
                if (!mCategory.equals("RankD")) mMusicFilter.RankD = false;
                if (!mCategory.equals("RankE")) mMusicFilter.RankE = false;
                if (!mCategory.equals("RankNoPlay")) mMusicFilter.RankNoPlay = false;
            } else if (mCategory.equals("FcFailed")) {
                mMusicFilter.RankAAA = false;
                mMusicFilter.RankAAp = false;
                mMusicFilter.RankAA = false;
                mMusicFilter.RankAAm = false;
                mMusicFilter.RankAp = false;
                mMusicFilter.RankA = false;
                mMusicFilter.RankAm = false;
                mMusicFilter.RankBp = false;
                mMusicFilter.RankB = false;
                mMusicFilter.RankBm = false;
                mMusicFilter.RankCp = false;
                mMusicFilter.RankC = false;
                mMusicFilter.RankCm = false;
                mMusicFilter.RankDp = false;
                mMusicFilter.RankD = false;
                mMusicFilter.RankE = true;
                mMusicFilter.RankNoPlay = false;
                mMusicFilter.ClearCountMax = 0;
            } else if (mCategory.equals("FcNoPlay")) {
                mMusicFilter.RankAAA = false;
                mMusicFilter.RankAAp = false;
                mMusicFilter.RankAA = false;
                mMusicFilter.RankAAm = false;
                mMusicFilter.RankAp = false;
                mMusicFilter.RankA = false;
                mMusicFilter.RankAm = false;
                mMusicFilter.RankBp = false;
                mMusicFilter.RankB = false;
                mMusicFilter.RankBm = false;
                mMusicFilter.RankCp = false;
                mMusicFilter.RankC = false;
                mMusicFilter.RankCm = false;
                mMusicFilter.RankDp = false;
                mMusicFilter.RankD = false;
                mMusicFilter.RankE = false;
                mMusicFilter.RankNoPlay = true;
            } else if (mCategory.startsWith("Fc")) {
                if (!mCategory.equals("FcMFC")) mMusicFilter.FcMFC = false;
                if (!mCategory.equals("FcPFC")) mMusicFilter.FcPFC = false;
                if (!mCategory.equals("FcGFC")) mMusicFilter.FcGFC = false;
                if (!mCategory.equals("FcFC")) mMusicFilter.FcFC = false;
                if (!mCategory.equals("FcLife4")) mMusicFilter.FcLife4 = false;
                if (!mCategory.equals("FcNoFC")) mMusicFilter.FcNoFC = false;
                if (mCategory.startsWith("FcNoFC")) {
                    mMusicFilter.RankNoPlay = false;
                    mMusicFilter.FcMFC = false;
                    mMusicFilter.FcPFC = false;
                    mMusicFilter.FcGFC = false;
                    mMusicFilter.FcFC = false;
                }
            } else if (mCategory.equals("WinLoseRivalWin (Close)")) {
                mMusicFilter.RivalLose = false;
                mMusicFilter.RivalDraw = false;
                mMusicFilter.ScoreDifferencePlusMax = Math.min(mMusicFilter.ScoreDifferencePlusMax, 1000);
                mMusicFilter.ScoreDifferenceMinusMax = 0;
                mMusicFilter.ScoreDifferenceMinusMin = 0;
            } else if (mCategory.equals("WinLoseRivalLose (Close)")) {
                mMusicFilter.RivalWin = false;
                mMusicFilter.RivalDraw = false;
                mMusicFilter.ScoreDifferenceMinusMax = Math.max(mMusicFilter.ScoreDifferenceMinusMax, -1000);
                mMusicFilter.ScoreDifferencePlusMax = 0;
                mMusicFilter.ScoreDifferencePlusMin = 0;
            } else if (mCategory.equals("WinLoseRivalDraw (Played)")) {
                mMusicFilter.RivalWin = false;
                mMusicFilter.RivalLose = false;
                mMusicFilter.RankNoPlay = false;
                mMusicFilter.RankNoPlayrival = false;

            } else if (mCategory.equals("WinLoseRivalWin (Rival NoPlay)")) {
                mMusicFilter.RivalLose = false;
                mMusicFilter.RivalDraw = false;
                mMusicFilter.RankAAArival = false;
                mMusicFilter.RankAAprival = false;
                mMusicFilter.RankAArival = false;
                mMusicFilter.RankAAmrival = false;
                mMusicFilter.RankAprival = false;
                mMusicFilter.RankArival = false;
                mMusicFilter.RankAmrival = false;
                mMusicFilter.RankBprival = false;
                mMusicFilter.RankBrival = false;
                mMusicFilter.RankBmrival = false;
                mMusicFilter.RankCprival = false;
                mMusicFilter.RankCrival = false;
                mMusicFilter.RankCmrival = false;
                mMusicFilter.RankDprival = false;
                mMusicFilter.RankDrival = false;
                mMusicFilter.RankErival = false;
            } else if (mCategory.equals("WinLoseRivalLose (Player NoPlay)")) {
                mMusicFilter.RivalWin = false;
                mMusicFilter.RivalDraw = false;
                mMusicFilter.RankAAA = false;
                mMusicFilter.RankAAp = false;
                mMusicFilter.RankAA = false;
                mMusicFilter.RankAAm = false;
                mMusicFilter.RankAp = false;
                mMusicFilter.RankA = false;
                mMusicFilter.RankAm = false;
                mMusicFilter.RankBp = false;
                mMusicFilter.RankB = false;
                mMusicFilter.RankBm = false;
                mMusicFilter.RankCp = false;
                mMusicFilter.RankC = false;
                mMusicFilter.RankCm = false;
                mMusicFilter.RankDp = false;
                mMusicFilter.RankD = false;
                mMusicFilter.RankE = false;
            } else if (mCategory.equals("WinLoseRivalDraw (NoPlay)")) {
                mMusicFilter.RivalWin = false;
                mMusicFilter.RivalLose = false;
                mMusicFilter.RankAAA = false;
                mMusicFilter.RankAAp = false;
                mMusicFilter.RankAA = false;
                mMusicFilter.RankAAm = false;
                mMusicFilter.RankAp = false;
                mMusicFilter.RankA = false;
                mMusicFilter.RankAm = false;
                mMusicFilter.RankBp = false;
                mMusicFilter.RankB = false;
                mMusicFilter.RankBm = false;
                mMusicFilter.RankCp = false;
                mMusicFilter.RankC = false;
                mMusicFilter.RankCm = false;
                mMusicFilter.RankDp = false;
                mMusicFilter.RankD = false;
                mMusicFilter.RankE = false;
                mMusicFilter.RankAAArival = false;
                mMusicFilter.RankAAprival = false;
                mMusicFilter.RankAArival = false;
                mMusicFilter.RankAAmrival = false;
                mMusicFilter.RankAprival = false;
                mMusicFilter.RankArival = false;
                mMusicFilter.RankAmrival = false;
                mMusicFilter.RankBprival = false;
                mMusicFilter.RankBrival = false;
                mMusicFilter.RankBmrival = false;
                mMusicFilter.RankCprival = false;
                mMusicFilter.RankCrival = false;
                mMusicFilter.RankCmrival = false;
                mMusicFilter.RankDprival = false;
                mMusicFilter.RankDrival = false;
                mMusicFilter.RankErival = false;
            } else if (mCategory.startsWith("WinLoseRival")) {
                if (!mCategory.equals("WinLoseRivalWin")) mMusicFilter.RivalWin = false;
                if (!mCategory.equals("WinLoseRivalLose")) mMusicFilter.RivalLose = false;
                if (!mCategory.equals("WinLoseRivalDraw")) mMusicFilter.RivalDraw = false;
            }
        }
    }

    private DialogFromGateList mFromGateList = null;
    private boolean mFromGateListGetDouble = false;

    private void showDialogFromGateList() {
        if (mFromGateList != null) {
            mFromGateList.cancel();
            mFromGateList = null;
        }

        mFromGateList = new DialogFromGateList(ScoreList.this);

        mFromGateList.setArguments(new AlertDialog.Builder(ScoreList.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(ScoreList.this.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetScoreList) + " (" + (mFromGateListGetDouble ? "DP" : "SP") + ") ...")
                        .setView(mFromGateList.getView())
                        .setCancelable(false)
                        .setNegativeButton(ScoreList.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                            if (mFromGateList != null) {
                                mFromGateList.cancel();
                                mFromGateList = null;
                            }
                        })
                        .setOnCancelListener(arg0 -> ScoreList.this.initialize())
                        .show()
                , mFromGateListGetDouble, null, null);

        mFromGateList.start();
    }

    private DialogFromGate mFromGate = null;
    private UniquePattern mFromGatePattern = null;
    private String mFromGateRivalId = null;
    private String mFromGateRivalName = null;

    private void showDialogFromGate() {
        if (mFromGate != null) {
            mFromGate.cancel();
            mFromGate = null;
        }

        mFromGate = new DialogFromGate(ScoreList.this);

        final MusicData md = mFromGatePattern.musics.get(mFromGatePattern.MusicId);
        if (!mFromGate.setArguments(new AlertDialog.Builder(ScoreList.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(mFromGatePattern.Pattern.toString() + " : " + md.Name)
                        .setView(mFromGate.getView())
                        .setCancelable(false)
                        .setNegativeButton(ScoreList.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                            if (mFromGate != null) {
                                mFromGate.cancel();
                                mFromGate = null;
                            }
                        })
                        .setOnCancelListener(arg0 -> ScoreList.this.initialize())
                        .show()
                , mFromGatePattern.MusicId, mFromGatePattern.Pattern, mFromGateRivalId, mFromGateRivalName)) {
            mFromGate.cancel();
            mFromGate = null;
            new AlertDialog.Builder(ScoreList.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(ScoreList.this.getResources().getString(R.string.illegal_id_alert))
                    .setCancelable(true)
                    .setPositiveButton(ScoreList.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {

                    }).show();
        } else {
            mFromGate.start();
        }
    }

    private DialogFromGateSou mFromGateSou = null;

    private void showDialogFromGateSou() {
        if (mFromGateSou != null) {
            mFromGateSou.cancel();
            mFromGateSou = null;
        }

        if (mListViewAdapter.getCount() <= 0) {
            new AlertDialog.Builder(ScoreList.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(ScoreList.this.getResources().getString(R.string.error_noitem))
                    .setCancelable(true)
                    .setPositiveButton(ScoreList.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {

                    }).show();
            return;
        }

        String rivalId = null;
        String rivalName = null;
        if (mSouTargetRival) {
            rivalId = mActiveRivalId;
            rivalName = mActiveRivalName;
        }

        mFromGateSou = new DialogFromGateSou(ScoreList.this);

        if (!mFromGateSou.setArguments(new AlertDialog.Builder(ScoreList.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(ScoreList.this.getResources().getString(R.string.sou_progress_title))
                        .setView(mFromGateSou.getView())
                        .setCancelable(false)
                        .setNegativeButton(ScoreList.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                            if (mFromGateSou != null) {
                                mFromGateSou.cancel();
                                mFromGateSou = null;
                            }
                        })
                        .setOnCancelListener(arg0 -> ScoreList.this.initialize())
                        .show()
                , mSouList, rivalId, rivalName)) {
            mFromGateSou.cancel();
            mFromGateSou = null;
            new AlertDialog.Builder(ScoreList.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(ScoreList.this.getResources().getString(R.string.illegal_id_alert))
                    .setCancelable(true)
                    .setPositiveButton(ScoreList.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {

                    }).show();
        } else {
            mFromGateSou.start();
        }
    }

    private DialogFromGateRecent mFromGateRecent = null;

    private void showDialogFromGateRecent() {
        if (mFromGateRecent != null) {
            mFromGateRecent.cancel();
            mFromGateRecent = null;
        }

        mFromGateRecent = new DialogFromGateRecent(ScoreList.this);

        mFromGateRecent.setArguments(new AlertDialog.Builder(ScoreList.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(ScoreList.this.getResources().getString(R.string.dialog_title_get_recent))
                .setView(mFromGateRecent.getView())
                .setCancelable(false)
                .setNegativeButton(ScoreList.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                    if (mFromGateRecent != null) {
                        mFromGateRecent.cancel();
                        mFromGateRecent = null;
                    }
                })
                .setOnCancelListener(arg0 -> ScoreList.this.initialize())
                .show());

        mFromGateRecent.start();
    }

    private void MoveNextPage() {
        Toast.makeText(this, "Swipe Right (TestMessage)", Toast.LENGTH_LONG).show();
    }

    private void MovePrevPage() {
        Toast.makeText(this, "Swipe Left (TestMessage)", Toast.LENGTH_LONG).show();
    }

    private GestureDetector m_clGestureDetector;

    private final SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            return super.onDoubleTap(event);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            return super.onDoubleTapEvent(event);
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return super.onDown(event);
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if (isSwipeLeft(event1, event2, velocityX)) {
                ScoreList.this.MovePrevPage();
                return true;
            } else if (isSwipeRight(event1, event2, velocityX)) {
                ScoreList.this.MoveNextPage();
                return true;
            } else {
                return super.onFling(event1, event2, velocityX, velocityY);
            }
        }

        @Override
        public void onLongPress(MotionEvent event) {
            super.onLongPress(event);
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            return super.onScroll(event1, event2, distanceX, distanceY);
        }

        @Override
        public void onShowPress(MotionEvent event) {
            super.onShowPress(event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            return super.onSingleTapConfirmed(event);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return super.onSingleTapUp(event);
        }

        private boolean isSwipeDown(MotionEvent e1, MotionEvent e2, float velocityY) {
            return isSwipe(e2.getY(), e1.getY(), velocityY);
        }

        private boolean isSwipeUp(MotionEvent e1, MotionEvent e2, float velocityY) {
            return isSwipe(e1.getY(), e2.getY(), velocityY);
        }

        private boolean isSwipeLeft(MotionEvent e1, MotionEvent e2, float velocityX) {
            return isSwipe(e1.getX(), e2.getX(), velocityX);
        }

        private boolean isSwipeRight(MotionEvent e1, MotionEvent e2, float velocityX) {
            return isSwipe(e2.getX(), e1.getX(), velocityX);
        }

        private boolean isSwipeDistance(float coordinateFirst, float coordinateLast) {
            return (coordinateFirst - coordinateLast) > SWIPE_MIN_DISTANCE;
        }

        private boolean isSwipeSpeed(float velocity) {
            return Math.abs(velocity) > SWIPE_THRESHOLD_VELOCITY;
        }

        private boolean isSwipe(float coordinateFirst, float coordinateLast, float velocity) {
            return isSwipeDistance(coordinateFirst, coordinateLast) && isSwipeSpeed(velocity);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
        if (requestCode == DialogFromGate.LoginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                showDialogFromGate();
            }
        }
        if (requestCode == DialogFromGateSou.LoginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                showDialogFromGateSou();
            }
        }
        if (requestCode == DialogFromGateRecent.LoginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                showDialogFromGateRecent();
            }
        }
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
        m_clGestureDetector = new GestureDetector(this, simpleOnGestureListener);
        View mainView = this.getLayoutInflater().inflate(R.layout.activity_score_list, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        this.setContentView(mainView);


//add by taiheisan end
        // 画面遷移パラメータはOnCreateでのみ取得（Flick操作によるカテゴリ変更を有効にする）
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mCategory = intent.getStringExtra("jp.linanfine.dsma.category");
        mCategoryOwnMusicMusicId = intent.getIntExtra("jp.linanfine.dsma.musicid", -1);
        mCategoryMyListMylistId = intent.getIntExtra("jp.linanfine.dsma.mylistid", -1);
//add by taiheisan end

        initialize();
    }
}
