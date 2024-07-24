package jp.linanfine.dsma.util.maker;

import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;

import jp.linanfine.dsma.util.html.CharSequenceFormatter;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.value.ListViewItemArguments;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

abstract public class ListViewItemMaker extends AsyncTask<UniquePattern, Void, ListViewItemArguments> {

    protected static ArrayList<View> sProcessingConvertViewList = new ArrayList<View>();
    protected static ArrayList<View> sWaitingConvertViewList = new ArrayList<View>();

    protected View mConvertView;
    protected AppearanceSettingsSp mAppearance;
    protected ScoreData mScoreData;
    protected ScoreData mRivalScoreData;

    public ListViewItemMaker(View convertView, AppearanceSettingsSp appearance) {
        mConvertView = convertView;
        mAppearance = appearance;
    }

    protected void addToList() {
        if (sProcessingConvertViewList.contains(mConvertView)) {
            sWaitingConvertViewList.add(mConvertView);
        } else {
            sProcessingConvertViewList.add(mConvertView);
        }
    }

    protected void removeFromList() {
        if (sProcessingConvertViewList.contains(mConvertView)) {
            sProcessingConvertViewList.remove(mConvertView);
        }
        if (sWaitingConvertViewList.contains(mConvertView)) {
            sProcessingConvertViewList.add(mConvertView);
            sWaitingConvertViewList.remove(mConvertView);
        }
    }

    protected boolean checkAndNotifyForWaiting() {
        if (sWaitingConvertViewList.contains(mConvertView)) {
            this.cancel(true);
            return true;
        }
        return false;
    }

    abstract public void onPreExecute();

    @Override
    protected void onCancelled() {
        removeFromList();
    }

    @Override
    public ListViewItemArguments doInBackground(UniquePattern... patterns) {

        synchronized (mConvertView) {

            int myScore = 0;
            int rScore = 0;

            UniquePattern pat = patterns[0];
            ListViewItemArguments lvia = new ListViewItemArguments();

            if (pat.MusicId < 0) {
                return null;
            }

            {
                MusicData item = pat.musics.get(pat.MusicId);

                MusicScore scoredata;
                if (pat.scores.containsKey(pat.MusicId)) {
                    scoredata = pat.scores.get(pat.MusicId);
                } else {
                    scoredata = new MusicScore();
                }

                if (checkAndNotifyForWaiting()) return null;

                lvia.Name = item.Name;
                lvia.Id = String.valueOf(item.Id);

                MusicRank rankData = MusicRank.Noplay;
                int level = 0;
                int score = 0;
                boolean shock = false;
                FullComboType fc = FullComboType.None;
                switch (pat.Pattern) {
                    case bSP:
                        mScoreData = scoredata.bSP;
                        level = (item.Difficulty_bSP);
                        rankData = scoredata.bSP.Rank;
                        fc = scoredata.bSP.FullComboType;
                        score = scoredata.bSP.Score;
                        lvia.LevelTextColor = 0xff66ffff;
                        lvia.PatternSingleBackColor = 0xff66ffff;
                        lvia.PatternDoubleBackColor = 0x00000000;
                        break;
                    case BSP:
                        mScoreData = scoredata.BSP;
                        level = (item.Difficulty_BSP);
                        rankData = scoredata.BSP.Rank;
                        fc = scoredata.BSP.FullComboType;
                        score = scoredata.BSP.Score;
                        lvia.LevelTextColor = 0xffff9900;
                        lvia.PatternSingleBackColor = 0xffff9900;
                        lvia.PatternDoubleBackColor = 0x00000000;
                        break;
                    case DSP:
                        mScoreData = scoredata.DSP;
                        level = (item.Difficulty_DSP);
                        rankData = scoredata.DSP.Rank;
                        fc = scoredata.DSP.FullComboType;
                        score = scoredata.DSP.Score;
                        lvia.LevelTextColor = 0xffff0000;
                        lvia.PatternSingleBackColor = 0xffff0000;
                        lvia.PatternDoubleBackColor = 0x00000000;
                        break;
                    case ESP:
                        mScoreData = scoredata.ESP;
                        level = (item.Difficulty_ESP);
                        rankData = scoredata.ESP.Rank;
                        fc = scoredata.ESP.FullComboType;
                        score = scoredata.ESP.Score;
                        lvia.LevelTextColor = 0xff00ff00;
                        lvia.PatternSingleBackColor = 0xff00ff00;
                        lvia.PatternDoubleBackColor = 0x00000000;
                        break;
                    case CSP:
                        mScoreData = scoredata.CSP;
                        level = (item.Difficulty_CSP);
                        rankData = scoredata.CSP.Rank;
                        fc = scoredata.CSP.FullComboType;
                        score = scoredata.CSP.Score;
                        lvia.LevelTextColor = 0xffff00ff;
                        lvia.PatternSingleBackColor = 0xffff00ff;
                        lvia.PatternDoubleBackColor = 0x00000000;
                        if (item.ShockArrowExists.containsKey(pat.MusicId)) {
                            shock = item.ShockArrowExists.get(pat.MusicId).b1;
                        }
                        break;
                    case BDP:
                        mScoreData = scoredata.BDP;
                        level = (item.Difficulty_BDP);
                        rankData = scoredata.BDP.Rank;
                        fc = scoredata.BDP.FullComboType;
                        score = scoredata.BDP.Score;
                        lvia.LevelTextColor = 0xffff9900;
                        lvia.PatternDoubleBackColor = 0xffff9900;
                        lvia.PatternSingleBackColor = 0x00000000;
                        break;
                    case DDP:
                        mScoreData = scoredata.DDP;
                        level = (item.Difficulty_DDP);
                        rankData = scoredata.DDP.Rank;
                        fc = scoredata.DDP.FullComboType;
                        score = scoredata.DDP.Score;
                        lvia.LevelTextColor = 0xffff0000;
                        lvia.PatternDoubleBackColor = 0xffff0000;
                        lvia.PatternSingleBackColor = 0x00000000;
                        break;
                    case EDP:
                        mScoreData = scoredata.EDP;
                        level = (item.Difficulty_EDP);
                        rankData = scoredata.EDP.Rank;
                        fc = scoredata.EDP.FullComboType;
                        score = scoredata.EDP.Score;
                        lvia.LevelTextColor = 0xff00ff00;
                        lvia.PatternDoubleBackColor = 0xff00ff00;
                        lvia.PatternSingleBackColor = 0x00000000;
                        break;
                    case CDP:
                        mScoreData = scoredata.CDP;
                        level = (item.Difficulty_CDP);
                        rankData = scoredata.CDP.Rank;
                        fc = scoredata.CDP.FullComboType;
                        score = scoredata.CDP.Score;
                        lvia.LevelTextColor = 0xffff00ff;
                        lvia.PatternDoubleBackColor = 0xffff00ff;
                        lvia.PatternSingleBackColor = 0x00000000;
                        if (item.ShockArrowExists.containsKey(pat.MusicId)) {
                            shock = item.ShockArrowExists.get(pat.MusicId).b2;
                        }
                        break;
                    default:
                        break;
                }
                lvia.ShockArrowExists = shock;

                if (checkAndNotifyForWaiting()) return null;

                lvia.Level = CharSequenceFormatter.formatLevel(level);

                CharSequenceFormatter.FormattedRankAndFC formattedRankAndFC = CharSequenceFormatter.formatRankAndFC(rankData, fc, (int) mAppearance.ItemMusicRankFontSize);
                if (checkAndNotifyForWaiting()) return null;

                lvia.Rank = formattedRankAndFC.text;
                lvia.RankTextSize = formattedRankAndFC.textSize;

                if (checkAndNotifyForWaiting()) return null;
                lvia.Score = CharSequenceFormatter.formatScore(score, rankData);
                myScore = score;
            }

            if (checkAndNotifyForWaiting()) return null;

            if (pat.rivalScores != null) {
                {
                    lvia.RivalName = pat.RivalName;

                    MusicScore scoredata;

                    if (pat.rivalScores.containsKey(pat.MusicId)) {
                        scoredata = pat.rivalScores.get(pat.MusicId);
                    } else {
                        scoredata = new MusicScore();
                    }

                    if (checkAndNotifyForWaiting()) return null;

                    MusicRank rankData = MusicRank.Noplay;
                    int score = 0;
                    FullComboType fc = FullComboType.None;
                    switch (pat.Pattern) {
                        case bSP:
                            mRivalScoreData = scoredata.bSP;
                            rankData = scoredata.bSP.Rank;
                            fc = scoredata.bSP.FullComboType;
                            score = scoredata.bSP.Score;
                            break;
                        case BSP:
                            mRivalScoreData = scoredata.BSP;
                            rankData = scoredata.BSP.Rank;
                            fc = scoredata.BSP.FullComboType;
                            score = scoredata.BSP.Score;
                            break;
                        case DSP:
                            mRivalScoreData = scoredata.DSP;
                            rankData = scoredata.DSP.Rank;
                            fc = scoredata.DSP.FullComboType;
                            score = scoredata.DSP.Score;
                            break;
                        case ESP:
                            mRivalScoreData = scoredata.ESP;
                            rankData = scoredata.ESP.Rank;
                            fc = scoredata.ESP.FullComboType;
                            score = scoredata.ESP.Score;
                            break;
                        case CSP:
                            mRivalScoreData = scoredata.CSP;
                            rankData = scoredata.CSP.Rank;
                            fc = scoredata.CSP.FullComboType;
                            score = scoredata.CSP.Score;
                            break;
                        case BDP:
                            mRivalScoreData = scoredata.BDP;
                            rankData = scoredata.BDP.Rank;
                            fc = scoredata.BDP.FullComboType;
                            score = scoredata.BDP.Score;
                            break;
                        case DDP:
                            mRivalScoreData = scoredata.DDP;
                            rankData = scoredata.DDP.Rank;
                            fc = scoredata.DDP.FullComboType;
                            score = scoredata.DDP.Score;
                            break;
                        case EDP:
                            mRivalScoreData = scoredata.EDP;
                            rankData = scoredata.EDP.Rank;
                            fc = scoredata.EDP.FullComboType;
                            score = scoredata.EDP.Score;
                            break;
                        case CDP:
                            mRivalScoreData = scoredata.CDP;
                            rankData = scoredata.CDP.Rank;
                            fc = scoredata.CDP.FullComboType;
                            score = scoredata.CDP.Score;
                            break;
                        default:
                            break;
                    }

                    if (checkAndNotifyForWaiting()) return null;

                    CharSequenceFormatter.FormattedRankAndFC formattedRivalRankAndFC =
                            CharSequenceFormatter.formatRivalRankAndFC(rankData, fc, mAppearance.ItemMusicRankFontSize);
                    lvia.RankRival = formattedRivalRankAndFC.text;
                    lvia.RankTextSizeRival = formattedRivalRankAndFC.textSize;

                    if (checkAndNotifyForWaiting()) return null;

                    lvia.ScoreRival = CharSequenceFormatter.formatRivalScore(score, rankData);

                    rScore = score;
                }

                if (checkAndNotifyForWaiting()) return null;

                lvia.ScoreDifference = CharSequenceFormatter.formatScoreDifference(myScore, rScore);
            }

            lvia.Comment = pat.Comment;

            if (checkAndNotifyForWaiting()) return null;

            return lvia;
        }
    }

    abstract public void onPostExecute(ListViewItemArguments result);
}
