package jp.linanfine.dsma.activity;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.MusicFilter;
import jp.linanfine.dsma.value._enum.ShockArrowInclude;

public class FilterSetting extends Activity {

    private int mPagerId;
    private MusicFilter mMusicFilter;

    Handler mHandler = new Handler();
    View mHandledView;

    private void save() {

        FileReader.saveMusicFilterName(this, mPagerId, ((TextView) FilterSetting.this.findViewById(R.id.tabName)).getText().toString());

        mMusicFilter.bSP = ((CheckBox) FilterSetting.this.findViewById(R.id.bSP)).isChecked();
        mMusicFilter.BSP = ((CheckBox) FilterSetting.this.findViewById(R.id.BSP)).isChecked();
        mMusicFilter.DSP = ((CheckBox) FilterSetting.this.findViewById(R.id.DSP)).isChecked();
        mMusicFilter.ESP = ((CheckBox) FilterSetting.this.findViewById(R.id.ESP)).isChecked();
        mMusicFilter.CSP = ((CheckBox) FilterSetting.this.findViewById(R.id.CSP)).isChecked();
        mMusicFilter.BDP = ((CheckBox) FilterSetting.this.findViewById(R.id.BDP)).isChecked();
        mMusicFilter.DDP = ((CheckBox) FilterSetting.this.findViewById(R.id.DDP)).isChecked();
        mMusicFilter.EDP = ((CheckBox) FilterSetting.this.findViewById(R.id.EDP)).isChecked();
        mMusicFilter.CDP = ((CheckBox) FilterSetting.this.findViewById(R.id.CDP)).isChecked();
        mMusicFilter.AllowOnlyChallenge = ((CheckBox) FilterSetting.this.findViewById(R.id.allowOnlyChallenge)).isChecked();
        mMusicFilter.AllowExpertWhenNoChallenge = ((CheckBox) FilterSetting.this.findViewById(R.id.allowExpertWhenNoChallenge)).isChecked();
        mMusicFilter.Dif1 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif1)).isChecked();
        mMusicFilter.Dif2 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif2)).isChecked();
        mMusicFilter.Dif3 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif3)).isChecked();
        mMusicFilter.Dif4 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif4)).isChecked();
        mMusicFilter.Dif5 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif5)).isChecked();
        mMusicFilter.Dif6 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif6)).isChecked();
        mMusicFilter.Dif7 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif7)).isChecked();
        mMusicFilter.Dif8 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif8)).isChecked();
        mMusicFilter.Dif9 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif9)).isChecked();
        mMusicFilter.Dif10 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif10)).isChecked();
        mMusicFilter.Dif11 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif11)).isChecked();
        mMusicFilter.Dif12 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif12)).isChecked();
        mMusicFilter.Dif13 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif13)).isChecked();
        mMusicFilter.Dif14 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif14)).isChecked();
        mMusicFilter.Dif15 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif15)).isChecked();
        mMusicFilter.Dif16 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif16)).isChecked();
        mMusicFilter.Dif17 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif17)).isChecked();
        mMusicFilter.Dif18 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif18)).isChecked();
        mMusicFilter.Dif19 = ((CheckBox) FilterSetting.this.findViewById(R.id.dif19)).isChecked();
        mMusicFilter.RankAAA = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAAA)).isChecked();
        mMusicFilter.RankAAp = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAAp)).isChecked();
        mMusicFilter.RankAA = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAA)).isChecked();
        mMusicFilter.RankAAm = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAAm)).isChecked();
        mMusicFilter.RankAp = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAp)).isChecked();
        mMusicFilter.RankA = ((CheckBox) FilterSetting.this.findViewById(R.id.rankA)).isChecked();
        mMusicFilter.RankAm = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAm)).isChecked();
        mMusicFilter.RankBp = ((CheckBox) FilterSetting.this.findViewById(R.id.rankBp)).isChecked();
        mMusicFilter.RankB = ((CheckBox) FilterSetting.this.findViewById(R.id.rankB)).isChecked();
        mMusicFilter.RankBm = ((CheckBox) FilterSetting.this.findViewById(R.id.rankBm)).isChecked();
        mMusicFilter.RankCp = ((CheckBox) FilterSetting.this.findViewById(R.id.rankCp)).isChecked();
        mMusicFilter.RankC = ((CheckBox) FilterSetting.this.findViewById(R.id.rankC)).isChecked();
        mMusicFilter.RankCm = ((CheckBox) FilterSetting.this.findViewById(R.id.rankCm)).isChecked();
        mMusicFilter.RankDp = ((CheckBox) FilterSetting.this.findViewById(R.id.rankDp)).isChecked();
        mMusicFilter.RankD = ((CheckBox) FilterSetting.this.findViewById(R.id.rankD)).isChecked();
        mMusicFilter.RankE = ((CheckBox) FilterSetting.this.findViewById(R.id.rankE)).isChecked();
        mMusicFilter.RankNoPlay = ((CheckBox) FilterSetting.this.findViewById(R.id.rankNoPlay)).isChecked();
        mMusicFilter.FcMFC = ((CheckBox) FilterSetting.this.findViewById(R.id.fcMFC)).isChecked();
        mMusicFilter.FcPFC = ((CheckBox) FilterSetting.this.findViewById(R.id.fcPFC)).isChecked();
        mMusicFilter.FcGFC = ((CheckBox) FilterSetting.this.findViewById(R.id.fcGFC)).isChecked();
        mMusicFilter.FcFC = ((CheckBox) FilterSetting.this.findViewById(R.id.fcFC)).isChecked();
        mMusicFilter.FcLife4 = ((CheckBox) FilterSetting.this.findViewById(R.id.fcLife4)).isChecked();
        mMusicFilter.FcNoFC = ((CheckBox) FilterSetting.this.findViewById(R.id.fcNoFC)).isChecked();

        mMusicFilter.FlareRankEX = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankEX)).isChecked();
        mMusicFilter.FlareRankIX = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankIX)).isChecked();
        mMusicFilter.FlareRankVIII = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankVIII)).isChecked();
        mMusicFilter.FlareRankVII = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankVII)).isChecked();
        mMusicFilter.FlareRankVI = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankVI)).isChecked();
        mMusicFilter.FlareRankV = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankV)).isChecked();
        mMusicFilter.FlareRankIV = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankIV)).isChecked();
        mMusicFilter.FlareRankIII = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankIII)).isChecked();
        mMusicFilter.FlareRankII = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankII)).isChecked();
        mMusicFilter.FlareRankI = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankI)).isChecked();
        mMusicFilter.FlareRank0 = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRank0)).isChecked();
        mMusicFilter.FlareRankNoRank = ((CheckBox) FilterSetting.this.findViewById(R.id.flareRankNoRank)).isChecked();

        mMusicFilter.RankAAArival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAAArival)).isChecked();
        mMusicFilter.RankAAprival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAAprival)).isChecked();
        mMusicFilter.RankAArival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAArival)).isChecked();
        mMusicFilter.RankAAmrival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAAmrival)).isChecked();
        mMusicFilter.RankAprival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAprival)).isChecked();
        mMusicFilter.RankArival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankArival)).isChecked();
        mMusicFilter.RankAmrival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankAmrival)).isChecked();
        mMusicFilter.RankBprival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankBprival)).isChecked();
        mMusicFilter.RankBrival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankBrival)).isChecked();
        mMusicFilter.RankBmrival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankBmrival)).isChecked();
        mMusicFilter.RankCprival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankCprival)).isChecked();
        mMusicFilter.RankCrival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankCrival)).isChecked();
        mMusicFilter.RankCmrival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankCmrival)).isChecked();
        mMusicFilter.RankDprival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankDprival)).isChecked();
        mMusicFilter.RankDrival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankDrival)).isChecked();
        mMusicFilter.RankErival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankErival)).isChecked();
        mMusicFilter.RankNoPlayrival = ((CheckBox) FilterSetting.this.findViewById(R.id.rankNoPlayrival)).isChecked();
        mMusicFilter.FcMFCrival = ((CheckBox) FilterSetting.this.findViewById(R.id.fcMFCrival)).isChecked();
        mMusicFilter.FcPFCrival = ((CheckBox) FilterSetting.this.findViewById(R.id.fcPFCrival)).isChecked();
        mMusicFilter.FcFCrival = ((CheckBox) FilterSetting.this.findViewById(R.id.fcFCrival)).isChecked();
        mMusicFilter.FcGFCrival = ((CheckBox) FilterSetting.this.findViewById(R.id.fcGFCrival)).isChecked();
        mMusicFilter.FcLife4rival = ((CheckBox) FilterSetting.this.findViewById(R.id.fcLife4rival)).isChecked();
        mMusicFilter.FcNoFCrival = ((CheckBox) FilterSetting.this.findViewById(R.id.fcNoFCrival)).isChecked();
        mMusicFilter.RivalWin = ((CheckBox) FilterSetting.this.findViewById(R.id.rivalWin)).isChecked();
        mMusicFilter.RivalLose = ((CheckBox) FilterSetting.this.findViewById(R.id.rivalLose)).isChecked();
        mMusicFilter.RivalDraw = ((CheckBox) FilterSetting.this.findViewById(R.id.rivalDraw)).isChecked();
        mMusicFilter.SerWorld = ((CheckBox) FilterSetting.this.findViewById(R.id.serWorld)).isChecked();
        mMusicFilter.SerA3 = ((CheckBox) FilterSetting.this.findViewById(R.id.serA3)).isChecked();
        mMusicFilter.SerA20PLUS = ((CheckBox) FilterSetting.this.findViewById(R.id.serA20PLUS)).isChecked();
        mMusicFilter.SerA20 = ((CheckBox) FilterSetting.this.findViewById(R.id.serA20)).isChecked();
        mMusicFilter.SerA = ((CheckBox) FilterSetting.this.findViewById(R.id.serA)).isChecked();
        mMusicFilter.Ser2014 = ((CheckBox) FilterSetting.this.findViewById(R.id.ser2014)).isChecked();
        mMusicFilter.Ser2013 = ((CheckBox) FilterSetting.this.findViewById(R.id.ser2013)).isChecked();
        mMusicFilter.SerX3 = ((CheckBox) FilterSetting.this.findViewById(R.id.serX3)).isChecked();
        mMusicFilter.SerX3vs2ndMIX = ((CheckBox) FilterSetting.this.findViewById(R.id.serX3vs2nd)).isChecked();
        mMusicFilter.SerX2 = ((CheckBox) FilterSetting.this.findViewById(R.id.serX2)).isChecked();
        mMusicFilter.SerX = ((CheckBox) FilterSetting.this.findViewById(R.id.serX)).isChecked();
        mMusicFilter.SerSuperNova2 = ((CheckBox) FilterSetting.this.findViewById(R.id.serSN2)).isChecked();
        mMusicFilter.SerSuperNova = ((CheckBox) FilterSetting.this.findViewById(R.id.serSN)).isChecked();
        mMusicFilter.SerEXTREME = ((CheckBox) FilterSetting.this.findViewById(R.id.serEXTREME)).isChecked();
        mMusicFilter.SerMAX2 = ((CheckBox) FilterSetting.this.findViewById(R.id.serMAX2)).isChecked();
        mMusicFilter.SerMAX = ((CheckBox) FilterSetting.this.findViewById(R.id.serMAX)).isChecked();
        mMusicFilter.Ser5th = ((CheckBox) FilterSetting.this.findViewById(R.id.ser5th)).isChecked();
        mMusicFilter.Ser4th = ((CheckBox) FilterSetting.this.findViewById(R.id.ser4th)).isChecked();
        mMusicFilter.Ser3rd = ((CheckBox) FilterSetting.this.findViewById(R.id.ser3rd)).isChecked();
        mMusicFilter.Ser2nd = ((CheckBox) FilterSetting.this.findViewById(R.id.ser2nd)).isChecked();
        mMusicFilter.Ser1st = ((CheckBox) FilterSetting.this.findViewById(R.id.ser1st)).isChecked();
        mMusicFilter.ShockArrowSP = ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowSPOnly)).isChecked() ? ShockArrowInclude.Only : ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowSPInclude)).isChecked() ? ShockArrowInclude.Include : ShockArrowInclude.Exclude;
        mMusicFilter.ShockArrowDP = ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowDPOnly)).isChecked() ? ShockArrowInclude.Only : ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowDPInclude)).isChecked() ? ShockArrowInclude.Include : ShockArrowInclude.Exclude;
        mMusicFilter.Deleted = ((CheckBox) FilterSetting.this.findViewById(R.id.othersDeleted)).isChecked();

        FileReader.saveMusicFilter(this, mPagerId, mMusicFilter);

        setItemStatuses();
    }

    private void openKeyboard() {
        if (mHandledView == null || mHandledView.getWindowToken() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
    }

    private void closeKeyboard() {
        if (mHandledView == null || mHandledView.getWindowToken() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
    }

    private void setItemStatuses() {

        mMusicFilter = FileReader.readMusicFilter(this, mPagerId);
        ((CheckBox) this.findViewById(R.id.bSP)).setChecked(mMusicFilter.bSP);
        ((CheckBox) this.findViewById(R.id.BSP)).setChecked(mMusicFilter.BSP);
        ((CheckBox) this.findViewById(R.id.DSP)).setChecked(mMusicFilter.DSP);
        ((CheckBox) this.findViewById(R.id.ESP)).setChecked(mMusicFilter.ESP);
        ((CheckBox) this.findViewById(R.id.CSP)).setChecked(mMusicFilter.CSP);
        ((CheckBox) this.findViewById(R.id.BDP)).setChecked(mMusicFilter.BDP);
        ((CheckBox) this.findViewById(R.id.DDP)).setChecked(mMusicFilter.DDP);
        ((CheckBox) this.findViewById(R.id.EDP)).setChecked(mMusicFilter.EDP);
        ((CheckBox) this.findViewById(R.id.CDP)).setChecked(mMusicFilter.CDP);
        ((CheckBox) this.findViewById(R.id.allowOnlyChallenge)).setChecked(mMusicFilter.AllowOnlyChallenge);
        ((CheckBox) this.findViewById(R.id.allowExpertWhenNoChallenge)).setChecked(mMusicFilter.AllowExpertWhenNoChallenge);

        if (mMusicFilter.ShockArrowSP == ShockArrowInclude.Only) {
            ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowSPOnly)).setChecked(true);
        } else if (mMusicFilter.ShockArrowSP == ShockArrowInclude.Include) {
            ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowSPInclude)).setChecked(true);
        } else {
            ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowSPExclude)).setChecked(true);
        }

        FilterSetting.this.findViewById(R.id.shockArrowSPOnly).setEnabled(mMusicFilter.CSP);
        FilterSetting.this.findViewById(R.id.shockArrowSPInclude).setEnabled(mMusicFilter.CSP);
        FilterSetting.this.findViewById(R.id.shockArrowSPExclude).setEnabled(mMusicFilter.CSP);

        if (mMusicFilter.ShockArrowDP == ShockArrowInclude.Only) {
            ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowDPOnly)).setChecked(true);
        } else if (mMusicFilter.ShockArrowDP == ShockArrowInclude.Include) {
            ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowDPInclude)).setChecked(true);
        } else {
            ((RadioButton) FilterSetting.this.findViewById(R.id.shockArrowDPExclude)).setChecked(true);
        }

        FilterSetting.this.findViewById(R.id.shockArrowDPOnly).setEnabled(mMusicFilter.CDP);
        FilterSetting.this.findViewById(R.id.shockArrowDPInclude).setEnabled(mMusicFilter.CDP);
        FilterSetting.this.findViewById(R.id.shockArrowDPExclude).setEnabled(mMusicFilter.CDP);

        if ((mMusicFilter.CSP && !mMusicFilter.ESP) || (mMusicFilter.CDP && !mMusicFilter.EDP)) {
            this.findViewById(R.id.allowExpertWhenNoChallenge).setEnabled(true);
        } else {
            this.findViewById(R.id.allowExpertWhenNoChallenge).setEnabled(false);
        }

        if (((mMusicFilter.bSP || mMusicFilter.BSP || mMusicFilter.DSP || mMusicFilter.ESP) && !mMusicFilter.CSP) ||
                ((mMusicFilter.BDP || mMusicFilter.DDP || mMusicFilter.EDP) && !mMusicFilter.CDP)) {
            this.findViewById(R.id.allowOnlyChallenge).setEnabled(true);
        } else {
            this.findViewById(R.id.allowOnlyChallenge).setEnabled(false);
        }

        ((CheckBox) this.findViewById(R.id.dif1)).setChecked(mMusicFilter.Dif1);
        ((CheckBox) this.findViewById(R.id.dif2)).setChecked(mMusicFilter.Dif2);
        ((CheckBox) this.findViewById(R.id.dif3)).setChecked(mMusicFilter.Dif3);
        ((CheckBox) this.findViewById(R.id.dif4)).setChecked(mMusicFilter.Dif4);
        ((CheckBox) this.findViewById(R.id.dif5)).setChecked(mMusicFilter.Dif5);
        ((CheckBox) this.findViewById(R.id.dif6)).setChecked(mMusicFilter.Dif6);
        ((CheckBox) this.findViewById(R.id.dif7)).setChecked(mMusicFilter.Dif7);
        ((CheckBox) this.findViewById(R.id.dif8)).setChecked(mMusicFilter.Dif8);
        ((CheckBox) this.findViewById(R.id.dif9)).setChecked(mMusicFilter.Dif9);
        ((CheckBox) this.findViewById(R.id.dif10)).setChecked(mMusicFilter.Dif10);
        ((CheckBox) this.findViewById(R.id.dif11)).setChecked(mMusicFilter.Dif11);
        ((CheckBox) this.findViewById(R.id.dif12)).setChecked(mMusicFilter.Dif12);
        ((CheckBox) this.findViewById(R.id.dif13)).setChecked(mMusicFilter.Dif13);
        ((CheckBox) this.findViewById(R.id.dif14)).setChecked(mMusicFilter.Dif14);
        ((CheckBox) this.findViewById(R.id.dif15)).setChecked(mMusicFilter.Dif15);
        ((CheckBox) this.findViewById(R.id.dif16)).setChecked(mMusicFilter.Dif16);
        ((CheckBox) this.findViewById(R.id.dif17)).setChecked(mMusicFilter.Dif17);
        ((CheckBox) this.findViewById(R.id.dif18)).setChecked(mMusicFilter.Dif18);
        ((CheckBox) this.findViewById(R.id.dif19)).setChecked(mMusicFilter.Dif19);

        ((CheckBox) this.findViewById(R.id.rankAAA)).setChecked(mMusicFilter.RankAAA);
        ((CheckBox) this.findViewById(R.id.rankAAp)).setChecked(mMusicFilter.RankAAp);
        ((CheckBox) this.findViewById(R.id.rankAA)).setChecked(mMusicFilter.RankAA);
        ((CheckBox) this.findViewById(R.id.rankAAm)).setChecked(mMusicFilter.RankAAm);
        ((CheckBox) this.findViewById(R.id.rankAp)).setChecked(mMusicFilter.RankAp);
        ((CheckBox) this.findViewById(R.id.rankA)).setChecked(mMusicFilter.RankA);
        ((CheckBox) this.findViewById(R.id.rankAm)).setChecked(mMusicFilter.RankAm);
        ((CheckBox) this.findViewById(R.id.rankBp)).setChecked(mMusicFilter.RankBp);
        ((CheckBox) this.findViewById(R.id.rankB)).setChecked(mMusicFilter.RankB);
        ((CheckBox) this.findViewById(R.id.rankBm)).setChecked(mMusicFilter.RankBm);
        ((CheckBox) this.findViewById(R.id.rankCp)).setChecked(mMusicFilter.RankCp);
        ((CheckBox) this.findViewById(R.id.rankC)).setChecked(mMusicFilter.RankC);
        ((CheckBox) this.findViewById(R.id.rankCm)).setChecked(mMusicFilter.RankCm);
        ((CheckBox) this.findViewById(R.id.rankDp)).setChecked(mMusicFilter.RankDp);
        ((CheckBox) this.findViewById(R.id.rankD)).setChecked(mMusicFilter.RankD);
        ((CheckBox) this.findViewById(R.id.rankE)).setChecked(mMusicFilter.RankE);
        ((CheckBox) this.findViewById(R.id.rankNoPlay)).setChecked(mMusicFilter.RankNoPlay);

        ((CheckBox) this.findViewById(R.id.fcMFC)).setChecked(mMusicFilter.FcMFC);
        ((CheckBox) this.findViewById(R.id.fcPFC)).setChecked(mMusicFilter.FcPFC);
        ((CheckBox) this.findViewById(R.id.fcGFC)).setChecked(mMusicFilter.FcGFC);
        ((CheckBox) this.findViewById(R.id.fcFC)).setChecked(mMusicFilter.FcFC);
        ((CheckBox) this.findViewById(R.id.fcLife4)).setChecked(mMusicFilter.FcLife4);
        ((CheckBox) this.findViewById(R.id.fcNoFC)).setChecked(mMusicFilter.FcNoFC);

        ((CheckBox) this.findViewById(R.id.rivalWin)).setChecked(mMusicFilter.RivalWin);
        ((CheckBox) this.findViewById(R.id.rivalLose)).setChecked(mMusicFilter.RivalLose);
        ((CheckBox) this.findViewById(R.id.rivalDraw)).setChecked(mMusicFilter.RivalDraw);

        ((CheckBox) this.findViewById(R.id.rankAAArival)).setChecked(mMusicFilter.RankAAArival);
        ((CheckBox) this.findViewById(R.id.rankAAprival)).setChecked(mMusicFilter.RankAAprival);
        ((CheckBox) this.findViewById(R.id.rankAArival)).setChecked(mMusicFilter.RankAArival);
        ((CheckBox) this.findViewById(R.id.rankAAmrival)).setChecked(mMusicFilter.RankAAmrival);
        ((CheckBox) this.findViewById(R.id.rankAprival)).setChecked(mMusicFilter.RankAprival);
        ((CheckBox) this.findViewById(R.id.rankArival)).setChecked(mMusicFilter.RankArival);
        ((CheckBox) this.findViewById(R.id.rankAmrival)).setChecked(mMusicFilter.RankAmrival);
        ((CheckBox) this.findViewById(R.id.rankBprival)).setChecked(mMusicFilter.RankBprival);
        ((CheckBox) this.findViewById(R.id.rankBrival)).setChecked(mMusicFilter.RankBrival);
        ((CheckBox) this.findViewById(R.id.rankBmrival)).setChecked(mMusicFilter.RankBmrival);
        ((CheckBox) this.findViewById(R.id.rankCprival)).setChecked(mMusicFilter.RankCprival);
        ((CheckBox) this.findViewById(R.id.rankCrival)).setChecked(mMusicFilter.RankCrival);
        ((CheckBox) this.findViewById(R.id.rankCmrival)).setChecked(mMusicFilter.RankCmrival);
        ((CheckBox) this.findViewById(R.id.rankDprival)).setChecked(mMusicFilter.RankDprival);
        ((CheckBox) this.findViewById(R.id.rankDrival)).setChecked(mMusicFilter.RankDrival);
        ((CheckBox) this.findViewById(R.id.rankErival)).setChecked(mMusicFilter.RankErival);
        ((CheckBox) this.findViewById(R.id.rankNoPlayrival)).setChecked(mMusicFilter.RankNoPlayrival);

        ((CheckBox) this.findViewById(R.id.fcMFCrival)).setChecked(mMusicFilter.FcMFCrival);
        ((CheckBox) this.findViewById(R.id.fcPFCrival)).setChecked(mMusicFilter.FcPFCrival);
        ((CheckBox) this.findViewById(R.id.fcFCrival)).setChecked(mMusicFilter.FcFCrival);
        ((CheckBox) this.findViewById(R.id.fcGFCrival)).setChecked(mMusicFilter.FcGFCrival);
        ((CheckBox) this.findViewById(R.id.fcLife4rival)).setChecked(mMusicFilter.FcLife4rival);
        ((CheckBox) this.findViewById(R.id.fcNoFCrival)).setChecked(mMusicFilter.FcNoFCrival);

        ((CheckBox) this.findViewById(R.id.othersDeleted)).setChecked(mMusicFilter.Deleted);

        final OnFocusChangeListener showKeyBoardOnFocusChange = (view, focused) -> {
            if (focused) {
                mHandledView = view;
                mHandler.post(FilterSetting.this::openKeyboard);
            }
        };

        this.findViewById(R.id.editScore).setOnClickListener(view -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            minText.setText(String.valueOf(mMusicFilter.ScoreMin));
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            maxText.setText(String.valueOf(mMusicFilter.ScoreMax));
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.ScoreMin;
                        int max = mMusicFilter.ScoreMax;
                        try {
                            min = Integer.parseInt(minT.toString());
                            max = Integer.parseInt(maxT.toString());
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.ScoreMin = min;
                        mMusicFilter.ScoreMax = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editFlareSkill).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                // テキスト入力を受け付けるビューを作成します。
                final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit, null);
                final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
                minText.setText(String.valueOf(mMusicFilter.FlareSkillMin));
                minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
                final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
                maxText.setText(String.valueOf(mMusicFilter.FlareSkillMax));
                maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);

                new AlertDialog.Builder(FilterSetting.this)
                        // setViewにてビューを設定します。
                        .setView(mainView)
                        .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FilterSetting.this.closeKeyboard();

                                Editable minT = minText.getText();
                                Editable maxT = maxText.getText();

                                int min = mMusicFilter.FlareSkillMin;
                                int max = mMusicFilter.FlareSkillMax;
                                try {
                                    min = Integer.valueOf(minT.toString());
                                    max = Integer.valueOf(maxT.toString());
                                } catch (Exception e) {
                                    Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.flare_skill_edit_error1), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (min > max) {
                                    Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.flare_skill_edit_error2), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                mMusicFilter.FlareSkillMin = min;
                                mMusicFilter.FlareSkillMax = max;

                                FilterSetting.this.save();
                            }
                        })
                        .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FilterSetting.this.closeKeyboard();
                            }
                        })
                        .show();
            }
        });

        this.findViewById(R.id.editScoreRival).setOnClickListener(view -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            minText.setText(String.valueOf(mMusicFilter.ScoreMinRival));
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            maxText.setText(String.valueOf(mMusicFilter.ScoreMaxRival));
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.ScoreMinRival;
                        int max = mMusicFilter.ScoreMaxRival;
                        try {
                            min = Integer.parseInt(minT.toString());
                            max = Integer.parseInt(maxT.toString());
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.ScoreMinRival = min;
                        mMusicFilter.ScoreMaxRival = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editCombo).setOnClickListener(view -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg0, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.MaxComboMin < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.MaxComboMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg0, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.MaxComboMax == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.MaxComboMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.MaxComboMin;
                        int max = mMusicFilter.MaxComboMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.MaxComboMin = min;
                        mMusicFilter.MaxComboMax = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editComboRival).setOnClickListener(view -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg0, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.MaxComboMinRival < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.MaxComboMinRival));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg0, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.MaxComboMaxRival == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.MaxComboMaxRival));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.MaxComboMinRival;
                        int max = mMusicFilter.MaxComboMaxRival;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.MaxComboMinRival = min;
                        mMusicFilter.MaxComboMaxRival = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editPlayCount).setOnClickListener(view -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg0, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.PlayCountMin < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.PlayCountMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg0, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.PlayCountMax == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.PlayCountMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.PlayCountMin;
                        int max = mMusicFilter.PlayCountMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.PlayCountMin = min;
                        mMusicFilter.PlayCountMax = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editPlayCountRival).setOnClickListener(view -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg0, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.PlayCountMinRival < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.PlayCountMinRival));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg0, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.PlayCountMaxRival == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.PlayCountMaxRival));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.PlayCountMinRival;
                        int max = mMusicFilter.PlayCountMaxRival;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.PlayCountMinRival = min;
                        mMusicFilter.PlayCountMaxRival = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editClearCount).setOnClickListener(view -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg0, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.ClearCountMin < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.ClearCountMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg0, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.ClearCountMax == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.ClearCountMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.ClearCountMin;
                        int max = mMusicFilter.ClearCountMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.ClearCountMin = min;
                        mMusicFilter.ClearCountMax = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editClearCountRival).setOnClickListener(view -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_max_min_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg0, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.ClearCountMinRival < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.ClearCountMinRival));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg0, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.ClearCountMaxRival == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.ClearCountMaxRival));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.ClearCountMinRival;
                        int max = mMusicFilter.ClearCountMaxRival;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.ClearCountMinRival = min;
                        mMusicFilter.ClearCountMaxRival = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editScoreDifferenceM).setOnClickListener(arg0 -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_difference_edit, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            minText.setText(String.valueOf(-mMusicFilter.ScoreDifferenceMinusMin));
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            maxText.setText(String.valueOf(-mMusicFilter.ScoreDifferenceMinusMax));
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = -mMusicFilter.ScoreDifferenceMinusMin;
                        int max = -mMusicFilter.ScoreDifferenceMinusMax;
                        try {
                            min = Integer.parseInt(minT.toString());
                            max = Integer.parseInt(maxT.toString());
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.ScoreDifferenceMinusMin = -min;
                        mMusicFilter.ScoreDifferenceMinusMax = -max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editScoreDifferenceP).setOnClickListener(arg0 -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_difference_edit, null);
            ((TextView) mainView.findViewById(R.id.pm1)).setText("+");
            ((TextView) mainView.findViewById(R.id.pm2)).setText("+");
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            minText.setText(String.valueOf(mMusicFilter.ScoreDifferencePlusMin));
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            maxText.setText(String.valueOf(mMusicFilter.ScoreDifferencePlusMax));
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.ScoreDifferencePlusMin;
                        int max = mMusicFilter.ScoreDifferencePlusMax;
                        try {
                            min = Integer.parseInt(minT.toString());
                            max = Integer.parseInt(maxT.toString());
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.ScoreDifferencePlusMin = min;
                        mMusicFilter.ScoreDifferencePlusMax = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editComboDifferenceM).setOnClickListener(arg0 -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_difference_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg0112, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.MaxComboDifferenceMinusMin > 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(-mMusicFilter.MaxComboDifferenceMinusMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg0111, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.MaxComboDifferenceMinusMax == Integer.MIN_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(-mMusicFilter.MaxComboDifferenceMinusMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = -mMusicFilter.MaxComboDifferenceMinusMin;
                        int max = mMusicFilter.MaxComboDifferenceMinusMax == Integer.MIN_VALUE ? Integer.MAX_VALUE : -mMusicFilter.MaxComboDifferenceMinusMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.MaxComboDifferenceMinusMin = -min;
                        mMusicFilter.MaxComboDifferenceMinusMax = max == Integer.MAX_VALUE ? Integer.MIN_VALUE : -max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editComboDifferenceP).setOnClickListener(arg0 -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_difference_edit_with_checkbox, null);
            ((TextView) mainView.findViewById(R.id.pm1)).setText("+");
            ((TextView) mainView.findViewById(R.id.pm2)).setText("+");
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg0110, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.MaxComboDifferencePlusMin < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.MaxComboDifferencePlusMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg019, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.MaxComboDifferencePlusMax == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.MaxComboDifferencePlusMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.MaxComboDifferencePlusMin;
                        int max = mMusicFilter.MaxComboDifferencePlusMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.MaxComboDifferencePlusMin = min;
                        mMusicFilter.MaxComboDifferencePlusMax = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editPlayCountDifferenceM).setOnClickListener(arg0 -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_difference_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg018, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.PlayCountDifferenceMinusMin > 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(-mMusicFilter.PlayCountDifferenceMinusMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg017, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.PlayCountDifferenceMinusMax == Integer.MIN_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(-mMusicFilter.PlayCountDifferenceMinusMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = -mMusicFilter.PlayCountDifferenceMinusMin;
                        int max = mMusicFilter.PlayCountDifferenceMinusMax == Integer.MIN_VALUE ? Integer.MAX_VALUE : -mMusicFilter.PlayCountDifferenceMinusMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.PlayCountDifferenceMinusMin = -min;
                        mMusicFilter.PlayCountDifferenceMinusMax = max == Integer.MAX_VALUE ? Integer.MIN_VALUE : -max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editPlayCountDifferenceP).setOnClickListener(arg0 -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_difference_edit_with_checkbox, null);
            ((TextView) mainView.findViewById(R.id.pm1)).setText("+");
            ((TextView) mainView.findViewById(R.id.pm2)).setText("+");
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg016, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.PlayCountDifferencePlusMin < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.PlayCountDifferencePlusMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg015, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.PlayCountDifferencePlusMax == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.PlayCountDifferencePlusMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.PlayCountDifferencePlusMin;
                        int max = mMusicFilter.PlayCountDifferencePlusMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.PlayCountDifferencePlusMin = min;
                        mMusicFilter.PlayCountDifferencePlusMax = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editClearCountDifferenceM).setOnClickListener(arg0 -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_difference_edit_with_checkbox, null);
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg014, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.ClearCountDifferenceMinusMin > 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(-mMusicFilter.ClearCountDifferenceMinusMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg013, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.ClearCountDifferenceMinusMax == Integer.MIN_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(-mMusicFilter.ClearCountDifferenceMinusMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = -mMusicFilter.ClearCountDifferenceMinusMin;
                        int max = mMusicFilter.ClearCountDifferenceMinusMax == Integer.MIN_VALUE ? Integer.MAX_VALUE : -mMusicFilter.ClearCountDifferenceMinusMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.ClearCountDifferenceMinusMin = -min;
                        mMusicFilter.ClearCountDifferenceMinusMax = max == Integer.MAX_VALUE ? Integer.MIN_VALUE : -max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        this.findViewById(R.id.editClearCountDifferenceP).setOnClickListener(arg0 -> {

            //テキスト入力を受け付けるビューを作成します。
            final View mainView = FilterSetting.this.getLayoutInflater().inflate(R.layout.view_difference_edit_with_checkbox, null);
            ((TextView) mainView.findViewById(R.id.pm1)).setText("+");
            ((TextView) mainView.findViewById(R.id.pm2)).setText("+");
            final EditText minText = (EditText) mainView.findViewById(R.id.minScore);
            final CheckBox minEnabled = (CheckBox) mainView.findViewById(R.id.minEnabled);
            minEnabled.setOnCheckedChangeListener((arg012, arg1) -> minText.setEnabled(arg1));
            if (mMusicFilter.ClearCountDifferencePlusMin < 0) {
                minEnabled.setChecked(false);
                minText.setText("");
            } else {
                minEnabled.setChecked(true);
                minText.setText(String.valueOf(mMusicFilter.ClearCountDifferencePlusMin));
            }
            minText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            final EditText maxText = (EditText) mainView.findViewById(R.id.maxScore);
            final CheckBox maxEnabled = (CheckBox) mainView.findViewById(R.id.maxEnabled);
            maxEnabled.setOnCheckedChangeListener((arg01, arg1) -> maxText.setEnabled(arg1));
            if (mMusicFilter.ClearCountDifferencePlusMax == Integer.MAX_VALUE) {
                maxEnabled.setChecked(false);
                maxText.setText("");
            } else {
                maxEnabled.setChecked(true);
                maxText.setText(String.valueOf(mMusicFilter.ClearCountDifferencePlusMax));
            }
            maxText.setOnFocusChangeListener(showKeyBoardOnFocusChange);
            new AlertDialog.Builder(FilterSetting.this)
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(FilterSetting.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        FilterSetting.this.closeKeyboard();

                        Editable minT = minText.getText();
                        Editable maxT = maxText.getText();

                        int min = mMusicFilter.ClearCountDifferencePlusMin;
                        int max = mMusicFilter.ClearCountDifferencePlusMax;
                        try {
                            min = minEnabled.isChecked() ? Integer.parseInt(minT.toString()) : -1;
                            max = maxEnabled.isChecked() ? Integer.parseInt(maxT.toString()) : Integer.MAX_VALUE;
                        } catch (Exception e) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error1), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (min > max) {
                            Toast.makeText(FilterSetting.this, FilterSetting.this.getResources().getString(R.string.score_list_rival_difference_edit_error2), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMusicFilter.ClearCountDifferencePlusMin = min;
                        mMusicFilter.ClearCountDifferencePlusMax = max;

                        FilterSetting.this.save();
                    })
                    .setNegativeButton(FilterSetting.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> FilterSetting.this.closeKeyboard())
                    .show();
        });

        ((TextView) this.findViewById(R.id.scoreMin)).setText(TextUtil.getScoreText(mMusicFilter.ScoreMin));
        ((TextView) this.findViewById(R.id.scoreMax)).setText(TextUtil.getScoreText(mMusicFilter.ScoreMax));

        ((TextView) this.findViewById(R.id.flareSkillMin)).setText(String.valueOf(mMusicFilter.FlareSkillMin));
        ((TextView) this.findViewById(R.id.flareSkillMax)).setText(String.valueOf(mMusicFilter.FlareSkillMax));

        ((TextView) this.findViewById(R.id.scoreMinRival)).setText(TextUtil.getScoreText(mMusicFilter.ScoreMinRival));
        ((TextView) this.findViewById(R.id.scoreMaxRival)).setText(TextUtil.getScoreText(mMusicFilter.ScoreMaxRival));

        ((TextView) this.findViewById(R.id.comboMin)).setText(mMusicFilter.MaxComboMin < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.MaxComboMin));
        ((TextView) this.findViewById(R.id.comboMax)).setText(mMusicFilter.MaxComboMax == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.MaxComboMax));
        ((TextView) this.findViewById(R.id.comboMinRival)).setText(mMusicFilter.MaxComboMinRival < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.MaxComboMinRival));
        ((TextView) this.findViewById(R.id.comboMaxRival)).setText(mMusicFilter.MaxComboMaxRival == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.MaxComboMaxRival));

        ((TextView) this.findViewById(R.id.playCountMin)).setText(mMusicFilter.PlayCountMin < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.PlayCountMin));
        ((TextView) this.findViewById(R.id.playCountMax)).setText(mMusicFilter.PlayCountMax == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.PlayCountMax));
        ((TextView) this.findViewById(R.id.playCountMinRival)).setText(mMusicFilter.PlayCountMinRival < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.PlayCountMinRival));
        ((TextView) this.findViewById(R.id.playCountMaxRival)).setText(mMusicFilter.PlayCountMaxRival == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.PlayCountMaxRival));

        ((TextView) this.findViewById(R.id.clearCountMin)).setText(mMusicFilter.ClearCountMin < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.ClearCountMin));
        ((TextView) this.findViewById(R.id.clearCountMax)).setText(mMusicFilter.ClearCountMax == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.ClearCountMax));
        ((TextView) this.findViewById(R.id.clearCountMinRival)).setText(mMusicFilter.ClearCountMinRival < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.ClearCountMinRival));
        ((TextView) this.findViewById(R.id.clearCountMaxRival)).setText(mMusicFilter.ClearCountMaxRival == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.ClearCountMaxRival));

        ((TextView) this.findViewById(R.id.scoreDifferenceMinusMin)).setText(TextUtil.getScoreText(-mMusicFilter.ScoreDifferenceMinusMin));
        ((TextView) this.findViewById(R.id.scoreDifferenceMinusMax)).setText(TextUtil.getScoreText(-mMusicFilter.ScoreDifferenceMinusMax));
        ((TextView) this.findViewById(R.id.scoreDifferencePlusMin)).setText(TextUtil.getScoreText(mMusicFilter.ScoreDifferencePlusMin));
        ((TextView) this.findViewById(R.id.scoreDifferencePlusMax)).setText(TextUtil.getScoreText(mMusicFilter.ScoreDifferencePlusMax));

        ((TextView) this.findViewById(R.id.comboDifferenceMinusMin)).setText(mMusicFilter.MaxComboDifferenceMinusMin > 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(-mMusicFilter.MaxComboDifferenceMinusMin));
        ((TextView) this.findViewById(R.id.comboDifferenceMinusMax)).setText(mMusicFilter.MaxComboDifferenceMinusMax == Integer.MIN_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(-mMusicFilter.MaxComboDifferenceMinusMax));
        ((TextView) this.findViewById(R.id.comboDifferencePlusMin)).setText(mMusicFilter.MaxComboDifferencePlusMin < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.MaxComboDifferencePlusMin));
        ((TextView) this.findViewById(R.id.comboDifferencePlusMax)).setText(mMusicFilter.MaxComboDifferencePlusMax == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.MaxComboDifferencePlusMax));

        ((TextView) this.findViewById(R.id.playCountDifferenceMinusMin)).setText(mMusicFilter.PlayCountDifferenceMinusMin > 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(-mMusicFilter.PlayCountDifferenceMinusMin));
        ((TextView) this.findViewById(R.id.playCountDifferenceMinusMax)).setText(mMusicFilter.PlayCountDifferenceMinusMax == Integer.MIN_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(-mMusicFilter.PlayCountDifferenceMinusMax));
        ((TextView) this.findViewById(R.id.playCountDifferencePlusMin)).setText(mMusicFilter.PlayCountDifferencePlusMin < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.PlayCountDifferencePlusMin));
        ((TextView) this.findViewById(R.id.playCountDifferencePlusMax)).setText(mMusicFilter.PlayCountDifferencePlusMax == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.PlayCountDifferencePlusMax));

        ((TextView) this.findViewById(R.id.clearCountDifferenceMinusMin)).setText(mMusicFilter.ClearCountDifferenceMinusMin > 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(-mMusicFilter.ClearCountDifferenceMinusMin));
        ((TextView) this.findViewById(R.id.clearCountDifferenceMinusMax)).setText(mMusicFilter.ClearCountDifferenceMinusMax == Integer.MIN_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(-mMusicFilter.ClearCountDifferenceMinusMax));
        ((TextView) this.findViewById(R.id.clearCountDifferencePlusMin)).setText(mMusicFilter.ClearCountDifferencePlusMin < 0 ? this.getResources().getString(R.string.filter_setting_nomin) : String.valueOf(mMusicFilter.ClearCountDifferencePlusMin));
        ((TextView) this.findViewById(R.id.clearCountDifferencePlusMax)).setText(mMusicFilter.ClearCountDifferencePlusMax == Integer.MAX_VALUE ? this.getResources().getString(R.string.filter_setting_nomax) : String.valueOf(mMusicFilter.ClearCountDifferencePlusMax));

        ((CheckBox) this.findViewById(R.id.serWorld)).setChecked(mMusicFilter.SerWorld);
        ((CheckBox) this.findViewById(R.id.serA3)).setChecked(mMusicFilter.SerA3);
        ((CheckBox) this.findViewById(R.id.serA20PLUS)).setChecked(mMusicFilter.SerA20PLUS);
        ((CheckBox) this.findViewById(R.id.serA20)).setChecked(mMusicFilter.SerA20);
        ((CheckBox) this.findViewById(R.id.serA)).setChecked(mMusicFilter.SerA);
        ((CheckBox) this.findViewById(R.id.ser2014)).setChecked(mMusicFilter.Ser2014);
        ((CheckBox) this.findViewById(R.id.ser2013)).setChecked(mMusicFilter.Ser2013);
        ((CheckBox) this.findViewById(R.id.serX3)).setChecked(mMusicFilter.SerX3);
        ((CheckBox) this.findViewById(R.id.serX3vs2nd)).setChecked(mMusicFilter.SerX3vs2ndMIX);
        ((CheckBox) this.findViewById(R.id.serX2)).setChecked(mMusicFilter.SerX2);
        ((CheckBox) this.findViewById(R.id.serX)).setChecked(mMusicFilter.SerX);
        ((CheckBox) this.findViewById(R.id.serSN2)).setChecked(mMusicFilter.SerSuperNova2);
        ((CheckBox) this.findViewById(R.id.serSN)).setChecked(mMusicFilter.SerSuperNova);
        ((CheckBox) this.findViewById(R.id.serEXTREME)).setChecked(mMusicFilter.SerEXTREME);
        ((CheckBox) this.findViewById(R.id.serMAX2)).setChecked(mMusicFilter.SerMAX2);
        ((CheckBox) this.findViewById(R.id.serMAX)).setChecked(mMusicFilter.SerMAX);
        ((CheckBox) this.findViewById(R.id.ser5th)).setChecked(mMusicFilter.Ser5th);
        ((CheckBox) this.findViewById(R.id.ser4th)).setChecked(mMusicFilter.Ser4th);
        ((CheckBox) this.findViewById(R.id.ser3rd)).setChecked(mMusicFilter.Ser3rd);
        ((CheckBox) this.findViewById(R.id.ser2nd)).setChecked(mMusicFilter.Ser2nd);
        ((CheckBox) this.findViewById(R.id.ser1st)).setChecked(mMusicFilter.Ser1st);

        ((CheckBox) this.findViewById(R.id.flareRankEX)).setChecked(mMusicFilter.FlareRankEX);
        ((CheckBox) this.findViewById(R.id.flareRankIX)).setChecked(mMusicFilter.FlareRankIX);
        ((CheckBox) this.findViewById(R.id.flareRankVIII)).setChecked(mMusicFilter.FlareRankVIII);
        ((CheckBox) this.findViewById(R.id.flareRankVII)).setChecked(mMusicFilter.FlareRankVII);
        ((CheckBox) this.findViewById(R.id.flareRankVI)).setChecked(mMusicFilter.FlareRankVI);
        ((CheckBox) this.findViewById(R.id.flareRankV)).setChecked(mMusicFilter.FlareRankV);
        ((CheckBox) this.findViewById(R.id.flareRankIV)).setChecked(mMusicFilter.FlareRankIV);
        ((CheckBox) this.findViewById(R.id.flareRankIII)).setChecked(mMusicFilter.FlareRankIII);
        ((CheckBox) this.findViewById(R.id.flareRankII)).setChecked(mMusicFilter.FlareRankII);
        ((CheckBox) this.findViewById(R.id.flareRankI)).setChecked(mMusicFilter.FlareRankI);
        ((CheckBox) this.findViewById(R.id.flareRank0)).setChecked(mMusicFilter.FlareRank0);
        ((CheckBox) this.findViewById(R.id.flareRankNoRank)).setChecked(mMusicFilter.FlareRankNoRank);
    }

    private void initialize() {

        Intent intent = this.getIntent();
        if (intent == null) {
            return;
        }

        mPagerId = intent.getIntExtra("jp.linanfine.dsma.pagerid", 0);
        intent.putExtra("jp.linanfine.dsma.pagerid", mPagerId);
        this.setResult(RESULT_OK, intent);

        OnClickListener ccl = v -> FilterSetting.this.save();

        this.findViewById(R.id.bSP).setOnClickListener(ccl);
        this.findViewById(R.id.BSP).setOnClickListener(ccl);
        this.findViewById(R.id.DSP).setOnClickListener(ccl);
        this.findViewById(R.id.ESP).setOnClickListener(ccl);
        this.findViewById(R.id.CSP).setOnClickListener(ccl);
        this.findViewById(R.id.BDP).setOnClickListener(ccl);
        this.findViewById(R.id.DDP).setOnClickListener(ccl);
        this.findViewById(R.id.EDP).setOnClickListener(ccl);
        this.findViewById(R.id.CDP).setOnClickListener(ccl);
        this.findViewById(R.id.allowOnlyChallenge).setOnClickListener(ccl);
        this.findViewById(R.id.allowExpertWhenNoChallenge).setOnClickListener(ccl);
        this.findViewById(R.id.shockArrowSPOnly).setOnClickListener(ccl);
        this.findViewById(R.id.shockArrowSPInclude).setOnClickListener(ccl);
        this.findViewById(R.id.shockArrowSPExclude).setOnClickListener(ccl);
        this.findViewById(R.id.shockArrowDPOnly).setOnClickListener(ccl);
        this.findViewById(R.id.shockArrowDPInclude).setOnClickListener(ccl);
        this.findViewById(R.id.shockArrowDPExclude).setOnClickListener(ccl);

        this.findViewById(R.id.dif1).setOnClickListener(ccl);
        this.findViewById(R.id.dif2).setOnClickListener(ccl);
        this.findViewById(R.id.dif3).setOnClickListener(ccl);
        this.findViewById(R.id.dif4).setOnClickListener(ccl);
        this.findViewById(R.id.dif5).setOnClickListener(ccl);
        this.findViewById(R.id.dif6).setOnClickListener(ccl);
        this.findViewById(R.id.dif7).setOnClickListener(ccl);
        this.findViewById(R.id.dif8).setOnClickListener(ccl);
        this.findViewById(R.id.dif9).setOnClickListener(ccl);
        this.findViewById(R.id.dif10).setOnClickListener(ccl);
        this.findViewById(R.id.dif11).setOnClickListener(ccl);
        this.findViewById(R.id.dif12).setOnClickListener(ccl);
        this.findViewById(R.id.dif13).setOnClickListener(ccl);
        this.findViewById(R.id.dif14).setOnClickListener(ccl);
        this.findViewById(R.id.dif15).setOnClickListener(ccl);
        this.findViewById(R.id.dif16).setOnClickListener(ccl);
        this.findViewById(R.id.dif17).setOnClickListener(ccl);
        this.findViewById(R.id.dif18).setOnClickListener(ccl);
        this.findViewById(R.id.dif19).setOnClickListener(ccl);

        this.findViewById(R.id.rankAAA).setOnClickListener(ccl);
        this.findViewById(R.id.rankAAp).setOnClickListener(ccl);
        this.findViewById(R.id.rankAA).setOnClickListener(ccl);
        this.findViewById(R.id.rankAAm).setOnClickListener(ccl);
        this.findViewById(R.id.rankAp).setOnClickListener(ccl);
        this.findViewById(R.id.rankA).setOnClickListener(ccl);
        this.findViewById(R.id.rankAm).setOnClickListener(ccl);
        this.findViewById(R.id.rankBp).setOnClickListener(ccl);
        this.findViewById(R.id.rankB).setOnClickListener(ccl);
        this.findViewById(R.id.rankBm).setOnClickListener(ccl);
        this.findViewById(R.id.rankCp).setOnClickListener(ccl);
        this.findViewById(R.id.rankC).setOnClickListener(ccl);
        this.findViewById(R.id.rankCm).setOnClickListener(ccl);
        this.findViewById(R.id.rankDp).setOnClickListener(ccl);
        this.findViewById(R.id.rankD).setOnClickListener(ccl);
        this.findViewById(R.id.rankE).setOnClickListener(ccl);
        this.findViewById(R.id.rankNoPlay).setOnClickListener(ccl);

        this.findViewById(R.id.flareRankEX).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankIX).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankVIII).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankVII).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankVI).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankV).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankIV).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankIII).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankII).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankI).setOnClickListener(ccl);
        this.findViewById(R.id.flareRank0).setOnClickListener(ccl);
        this.findViewById(R.id.flareRankNoRank).setOnClickListener(ccl);

        this.findViewById(R.id.fcMFC).setOnClickListener(ccl);
        this.findViewById(R.id.fcPFC).setOnClickListener(ccl);
        this.findViewById(R.id.fcGFC).setOnClickListener(ccl);
        this.findViewById(R.id.fcFC).setOnClickListener(ccl);
        this.findViewById(R.id.fcLife4).setOnClickListener(ccl);
        this.findViewById(R.id.fcNoFC).setOnClickListener(ccl);

        this.findViewById(R.id.rankAAArival).setOnClickListener(ccl);
        this.findViewById(R.id.rankAAprival).setOnClickListener(ccl);
        this.findViewById(R.id.rankAArival).setOnClickListener(ccl);
        this.findViewById(R.id.rankAAmrival).setOnClickListener(ccl);
        this.findViewById(R.id.rankAprival).setOnClickListener(ccl);
        this.findViewById(R.id.rankArival).setOnClickListener(ccl);
        this.findViewById(R.id.rankAmrival).setOnClickListener(ccl);
        this.findViewById(R.id.rankBprival).setOnClickListener(ccl);
        this.findViewById(R.id.rankBrival).setOnClickListener(ccl);
        this.findViewById(R.id.rankBmrival).setOnClickListener(ccl);
        this.findViewById(R.id.rankCprival).setOnClickListener(ccl);
        this.findViewById(R.id.rankCrival).setOnClickListener(ccl);
        this.findViewById(R.id.rankCmrival).setOnClickListener(ccl);
        this.findViewById(R.id.rankDprival).setOnClickListener(ccl);
        this.findViewById(R.id.rankDrival).setOnClickListener(ccl);
        this.findViewById(R.id.rankErival).setOnClickListener(ccl);
        this.findViewById(R.id.rankNoPlayrival).setOnClickListener(ccl);

        this.findViewById(R.id.fcMFCrival).setOnClickListener(ccl);
        this.findViewById(R.id.fcPFCrival).setOnClickListener(ccl);
        this.findViewById(R.id.fcFCrival).setOnClickListener(ccl);
        this.findViewById(R.id.fcGFCrival).setOnClickListener(ccl);
        this.findViewById(R.id.fcLife4rival).setOnClickListener(ccl);
        this.findViewById(R.id.fcNoFCrival).setOnClickListener(ccl);

        this.findViewById(R.id.rivalWin).setOnClickListener(ccl);
        this.findViewById(R.id.rivalLose).setOnClickListener(ccl);
        this.findViewById(R.id.rivalDraw).setOnClickListener(ccl);

        CheckBox worldCheckBox = this.findViewById(R.id.serWorld);
        CheckBox a3CheckBox = this.findViewById(R.id.serA3);
        CheckBox a20PlusCheckBox = this.findViewById(R.id.serA20PLUS);
        CheckBox a20CheckBox = this.findViewById(R.id.serA20);
        CheckBox aCheckBox = this.findViewById(R.id.serA);
        CheckBox v2014CheckBox = this.findViewById(R.id.ser2014);
        CheckBox v2013CheckBox = this.findViewById(R.id.ser2013);
        CheckBox x3CheckBox = this.findViewById(R.id.serX3);
        CheckBox x3vs2ndCheckBox = this.findViewById(R.id.serX3vs2nd);
        CheckBox x2CheckBox = this.findViewById(R.id.serX2);
        CheckBox xCheckBox = this.findViewById(R.id.serX);
        CheckBox sn2CheckBox = this.findViewById(R.id.serSN2);
        CheckBox snCheckBox = this.findViewById(R.id.serSN);
        CheckBox extremeCheckBox = this.findViewById(R.id.serEXTREME);
        CheckBox max2CheckBox = this.findViewById(R.id.serMAX2);
        CheckBox maxCheckBox = this.findViewById(R.id.serMAX);
        CheckBox v5thCheckBox = this.findViewById(R.id.ser5th);
        CheckBox v4thCheckBox = this.findViewById(R.id.ser4th);
        CheckBox v3rdCheckBox = this.findViewById(R.id.ser3rd);
        CheckBox v2ndCheckBox = this.findViewById(R.id.ser2nd);
        CheckBox v1stCheckBox = this.findViewById(R.id.ser1st);

        worldCheckBox.setOnClickListener(ccl);
        a3CheckBox.setOnClickListener(ccl);
        a20PlusCheckBox.setOnClickListener(ccl);
        a20CheckBox.setOnClickListener(ccl);
        aCheckBox.setOnClickListener(ccl);
        v2014CheckBox.setOnClickListener(ccl);
        v2013CheckBox.setOnClickListener(ccl);
        x3CheckBox.setOnClickListener(ccl);
        x3vs2ndCheckBox.setOnClickListener(ccl);
        x2CheckBox.setOnClickListener(ccl);
        xCheckBox.setOnClickListener(ccl);
        sn2CheckBox.setOnClickListener(ccl);
        snCheckBox.setOnClickListener(ccl);
        extremeCheckBox.setOnClickListener(ccl);
        max2CheckBox.setOnClickListener(ccl);
        maxCheckBox.setOnClickListener(ccl);
        v5thCheckBox.setOnClickListener(ccl);
        v4thCheckBox.setOnClickListener(ccl);
        v3rdCheckBox.setOnClickListener(ccl);
        v2ndCheckBox.setOnClickListener(ccl);
        v1stCheckBox.setOnClickListener(ccl);

        this.findViewById(R.id.othersDeleted).setOnClickListener(ccl);

        List<CheckBox> goldCheckBoxes = Arrays.asList(worldCheckBox, a3CheckBox, a20PlusCheckBox, a20CheckBox);
        this.findViewById(R.id.selectAllButtonGold)
                .setOnClickListener(v -> setAllCheckBoxes(goldCheckBoxes, true));
        this.findViewById(R.id.deselectAllButtonGold)
                .setOnClickListener(v -> setAllCheckBoxes(goldCheckBoxes, false));

        List<CheckBox> whiteCheckBoxes = Arrays.asList(aCheckBox, v2014CheckBox, v2013CheckBox);
        this.findViewById(R.id.selectAllButtonWhite)
                .setOnClickListener(v -> setAllCheckBoxes(whiteCheckBoxes, true));
        this.findViewById(R.id.deselectAllButtonWhite)
                .setOnClickListener(v -> setAllCheckBoxes(whiteCheckBoxes, false));

        List<CheckBox> classicCheckBoxes = Arrays.asList(
                x3CheckBox,
                x3vs2ndCheckBox,
                x2CheckBox,
                xCheckBox,
                sn2CheckBox,
                snCheckBox,
                extremeCheckBox,
                max2CheckBox,
                maxCheckBox,
                v5thCheckBox,
                v4thCheckBox,
                v3rdCheckBox,
                v2ndCheckBox,
                v1stCheckBox
        );
        this.findViewById(R.id.selectAllButtonClassic)
                .setOnClickListener(v -> setAllCheckBoxes(classicCheckBoxes, true));
        this.findViewById(R.id.deselectAllButtonClassic)
                .setOnClickListener(v -> setAllCheckBoxes(classicCheckBoxes, false));

        setItemStatuses();

        ((TextView) FilterSetting.this.findViewById(R.id.tabName)).setText(FileReader.readMusicFilterName(this, mPagerId));
        View editTabName = this.findViewById(R.id.editTabName);
        editTabName.setOnClickListener(view -> {
            //テキスト入力を受け付けるビューを作成します。
            final EditText editView = (EditText) FilterSetting.this.getLayoutInflater().inflate(R.layout.view_singleline_edit_text, null).findViewById(R.id.editText);
            editView.setText(((TextView) FilterSetting.this.findViewById(R.id.tabName)).getText());
            editView.setOnFocusChangeListener((view1, focused) -> {
                if (focused) {
                    mHandledView = view1;
                    mHandler.post(() -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                    });
                }
            });
            new AlertDialog.Builder(FilterSetting.this)
                    .setIcon(drawable.ic_dialog_info)
                    .setTitle(FilterSetting.this.getResources().getString(R.string.edit_filter_name))
                    //setViewにてビューを設定します。
                    .setView(editView)
                    .setPositiveButton("OK", (dialog, whichButton) -> {
                        closeKeyboard();
                        ((TextView) FilterSetting.this.findViewById(R.id.tabName)).setText(editView.getText());
                        FilterSetting.this.save();
                    })
                    .setNegativeButton("Cancel", (dialog, whichButton) -> {
                        closeKeyboard();
                    })
                    .show();
        });

        View deleteFilter = this.findViewById(R.id.delete);
        if (mPagerId == 0) deleteFilter.setVisibility(View.GONE);
        deleteFilter.setOnClickListener(view -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FilterSetting.this);
            // アラートダイアログのタイトルを設定します
            alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle(getResources().getString(R.string.filter_setting_delete));
            // アラートダイアログのメッセージを設定します
            alertDialogBuilder.setMessage(getResources().getString(R.string.filter_setting_deleteconferm));
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings_global____ok),
                    (dialog, which) -> {
                        FileReader.deleteMusicFilter(FilterSetting.this, mPagerId);
                        FileReader.saveActiveMusicFilter(FilterSetting.this, mPagerId - 1);
                        FilterSetting.this.finish();
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

    private void setAllCheckBoxes(List<CheckBox> checkBoxes, boolean isChecked) {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(isChecked);
        }
        save();
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
        setContentView(R.layout.activity_filter);

        FileReader.requestAd((LinearLayout) this.findViewById(R.id.adContainer), this);
        initialize();
    }
}
