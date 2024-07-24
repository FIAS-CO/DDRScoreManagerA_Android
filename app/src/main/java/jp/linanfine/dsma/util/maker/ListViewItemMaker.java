package jp.linanfine.dsma.util.maker;

import android.os.AsyncTask;
import android.text.Html;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
                String levelText = "";
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
                if (level < 0) {
                    levelText = "<font color=\"#000000\">?</font>" + "?";
                } else {
                    levelText = (level < 10 ? "<font color=\"#000000\">0</font>" : "") + String.valueOf(level);
                }

                if (checkAndNotifyForWaiting()) return null;

                String fctext;
                switch (fc) {
                    case MerverousFullCombo:
                        fctext = "<font color=\"#ffffff\">/font>";
                        break;
                    case PerfectFullCombo:
                        fctext = "<font color=\"#ffff00\">゜</font>";
                        break;
                    case FullCombo:
                        fctext = "<font color=\"#33ff33\">゜</font>";
                        break;
                    case GoodFullCombo:
                        fctext = "<font color=\"#6699ff\">゜</font>";
                        break;
                    case Life4:
                        fctext = "<font color=\"#ff6633\">゜</font>";
                        break;
                    default:
                        fctext = "゜";
                        break;
                }

                if (checkAndNotifyForWaiting()) return null;

                lvia.RankTextSize = (int) mAppearance.ItemMusicRankFontSize;
                String ranktext;
                if (rankData == MusicRank.Noplay) {
                    ranktext = "<font color=\"#666666\">" + rankData.toString() + "</font>";
                    lvia.RankTextSize = (int) mAppearance.ItemMusicRankFontSize * 3 / 7;
                } else if (rankData == MusicRank.E) {
                    ranktext = "<font color=\"#999999\">" + rankData.toString() + "</font>";
                } else if (rankData == MusicRank.Dp || rankData == MusicRank.D) {
                    ranktext = "<font color=\"#ff0000\">" + rankData.toString() + "</font>";
                } else if (rankData == MusicRank.Cp || rankData == MusicRank.C || rankData == MusicRank.Cm) {
                    ranktext = "<font color=\"#ff00ff\">" + rankData.toString() + "</font>";
                } else if (rankData == MusicRank.Bp || rankData == MusicRank.B || rankData == MusicRank.Bm) {
                    ranktext = "<font color=\"#6666ff\">" + rankData.toString() + "</font>";
                } else if (rankData == MusicRank.Ap || rankData == MusicRank.A || rankData == MusicRank.Am) {
                    ranktext = "<font color=\"#ffff00\">" + rankData.toString() + "</font>";
                } else if (rankData == MusicRank.AAp || rankData == MusicRank.AA || rankData == MusicRank.AAm) {
                    ranktext = "<font color=\"#ffff66\">" + rankData.toString() + "</font>";
                } else if (rankData == MusicRank.AAA) {
                    ranktext = "<font color=\"#ffffcc\">" + rankData.toString() + "</font>";
                } else {
                    ranktext = "";
                }

                if (checkAndNotifyForWaiting()) return null;

                lvia.Rank = Html.fromHtml(ranktext + fctext);
                lvia.Level = Html.fromHtml(levelText);

                String scoreText = new DecimalFormat("0,000,000").format(score);
                StringBuilder html = new StringBuilder();
                if (score == 0) {
                    if (rankData != MusicRank.Noplay) {
                        html.append("0,000,00");
                        html.append("<font color=\"#ffffff\">");
                        html.append("0");
                        html.append("</font>");
                    } else {
                        html.append(scoreText);
                    }
                } else if (score < 100) {
                    html.append(scoreText.charAt(0));
                    html.append(scoreText.charAt(1));
                    html.append(scoreText.charAt(2));
                    html.append(scoreText.charAt(3));
                    html.append(scoreText.charAt(4));
                    html.append(scoreText.charAt(5));
                    html.append(scoreText.charAt(6));
                    html.append("<font color=\"#ffffff\">");
                    html.append(scoreText.charAt(7));
                    html.append(scoreText.charAt(8));
                    html.append("</font>");
                } else if (score < 1000) {
                    html.append(scoreText.charAt(0));
                    html.append(scoreText.charAt(1));
                    html.append(scoreText.charAt(2));
                    html.append(scoreText.charAt(3));
                    html.append(scoreText.charAt(4));
                    html.append(scoreText.charAt(5));
                    html.append("<font color=\"#ffffff\">");
                    html.append(scoreText.charAt(6));
                    html.append(scoreText.charAt(7));
                    html.append(scoreText.charAt(8));
                    html.append("</font>");
                } else if (score < 10000) {
                    html.append(scoreText.charAt(0));
                    html.append(scoreText.charAt(1));
                    html.append(scoreText.charAt(2));
                    html.append(scoreText.charAt(3));
                    html.append("<font color=\"#ffffff\">");
                    html.append(scoreText.charAt(4));
                    html.append(scoreText.charAt(5));
                    html.append(scoreText.charAt(6));
                    html.append(scoreText.charAt(7));
                    html.append(scoreText.charAt(8));
                    html.append("</font>");
                } else if (score < 100000) {
                    html.append(scoreText.charAt(0));
                    html.append(scoreText.charAt(1));
                    html.append(scoreText.charAt(2));
                    html.append("<font color=\"#ffffff\">");
                    html.append(scoreText.charAt(3));
                    html.append(scoreText.charAt(4));
                    html.append(scoreText.charAt(5));
                    html.append(scoreText.charAt(6));
                    html.append(scoreText.charAt(7));
                    html.append(scoreText.charAt(8));
                    html.append("</font>");
                } else if (score < 1000000) {
                    html.append(scoreText.charAt(0));
                    html.append(scoreText.charAt(1));
                    html.append("<font color=\"#ffffff\">");
                    html.append(scoreText.charAt(2));
                    html.append(scoreText.charAt(3));
                    html.append(scoreText.charAt(4));
                    html.append(scoreText.charAt(5));
                    html.append(scoreText.charAt(6));
                    html.append(scoreText.charAt(7));
                    html.append(scoreText.charAt(8));
                    html.append("</font>");
                } else {
                    html.append("<font color=\"#ffffff\">");
                    html.append(scoreText);
                    html.append("</font>");
                }

                if (checkAndNotifyForWaiting()) return null;

                lvia.Score = Html.fromHtml(html.toString());

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

                    String fctext;
                    switch (fc) {
                        case MerverousFullCombo:
                            fctext = "<font color=\"#ffffff\">゜</font>";
                            break;
                        case PerfectFullCombo:
                            fctext = "<font color=\"#ffff00\">゜</font>";
                            break;
                        case FullCombo:
                            fctext = "<font color=\"#33ff33\">゜</font>";
                            break;
                        case GoodFullCombo:
                            fctext = "<font color=\"#6699ff\">゜</font>";
                            break;
                        case Life4:
                            fctext = "<font color=\"#ff6633\">゜</font>";
                            break;
                        default:
                            fctext = "゜";
                            break;
                    }

                    if (checkAndNotifyForWaiting()) return null;

                    lvia.RankTextSizeRival = (int) mAppearance.ItemMusicRankFontSize;
                    String ranktext;
                    if (rankData == MusicRank.Noplay) {
                        ranktext = "<font color=\"#666666\">" + rankData.toString() + "</font>";
                        lvia.RankTextSizeRival = (int) mAppearance.ItemMusicRankFontSize * 3 / 7;
                    } else if (rankData == MusicRank.E) {
                        ranktext = "<font color=\"#999999\">" + rankData.toString() + "</font>";
                    } else if (rankData == MusicRank.Dp || rankData == MusicRank.D) {
                        ranktext = "<font color=\"#ff0000\">" + rankData.toString() + "</font>";
                    } else if (rankData == MusicRank.Cp || rankData == MusicRank.C || rankData == MusicRank.Cm) {
                        ranktext = "<font color=\"#ff00ff\">" + rankData.toString() + "</font>";
                    } else if (rankData == MusicRank.Bp || rankData == MusicRank.B || rankData == MusicRank.Bm) {
                        ranktext = "<font color=\"#6666ff\">" + rankData.toString() + "</font>";
                    } else if (rankData == MusicRank.Ap || rankData == MusicRank.A || rankData == MusicRank.Am) {
                        ranktext = "<font color=\"#ffff00\">" + rankData.toString() + "</font>";
                    } else if (rankData == MusicRank.AAp || rankData == MusicRank.AA || rankData == MusicRank.AAm) {
                        ranktext = "<font color=\"#ffff66\">" + rankData.toString() + "</font>";
                    } else if (rankData == MusicRank.AAA) {
                        ranktext = "<font color=\"#ffffcc\">" + rankData.toString() + "</font>";
                    } else {
                        ranktext = "";
                    }

                    if (checkAndNotifyForWaiting()) return null;

                    lvia.RankRival = Html.fromHtml(ranktext + fctext);
                    String scoreText = new DecimalFormat("0,000,000").format(score);
                    StringBuilder html = new StringBuilder();
                    if (score == 0) {
                        if (rankData != MusicRank.Noplay) {
                            html.append("0,000,00");
                            html.append("<font color=\"#ffffff\">");
                            html.append("0");
                            html.append("</font>");
                        } else {
                            html.append(scoreText);
                        }
                    } else if (score < 100) {
                        html.append(scoreText.charAt(0));
                        html.append(scoreText.charAt(1));
                        html.append(scoreText.charAt(2));
                        html.append(scoreText.charAt(3));
                        html.append(scoreText.charAt(4));
                        html.append(scoreText.charAt(5));
                        html.append(scoreText.charAt(6));
                        html.append("<font color=\"#ffffff\">");
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                        html.append("</font>");
                    } else if (score < 1000) {
                        html.append(scoreText.charAt(0));
                        html.append(scoreText.charAt(1));
                        html.append(scoreText.charAt(2));
                        html.append(scoreText.charAt(3));
                        html.append(scoreText.charAt(4));
                        html.append(scoreText.charAt(5));
                        html.append("<font color=\"#ffffff\">");
                        html.append(scoreText.charAt(6));
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                        html.append("</font>");
                    } else if (score < 10000) {
                        html.append(scoreText.charAt(0));
                        html.append(scoreText.charAt(1));
                        html.append(scoreText.charAt(2));
                        html.append(scoreText.charAt(3));
                        html.append("<font color=\"#ffffff\">");
                        html.append(scoreText.charAt(4));
                        html.append(scoreText.charAt(5));
                        html.append(scoreText.charAt(6));
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                        html.append("</font>");
                    } else if (score < 100000) {
                        html.append(scoreText.charAt(0));
                        html.append(scoreText.charAt(1));
                        html.append(scoreText.charAt(2));
                        html.append("<font color=\"#ffffff\">");
                        html.append(scoreText.charAt(3));
                        html.append(scoreText.charAt(4));
                        html.append(scoreText.charAt(5));
                        html.append(scoreText.charAt(6));
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                        html.append("</font>");
                    } else if (score < 1000000) {
                        html.append(scoreText.charAt(0));
                        html.append(scoreText.charAt(1));
                        html.append("<font color=\"#ffffff\">");
                        html.append(scoreText.charAt(2));
                        html.append(scoreText.charAt(3));
                        html.append(scoreText.charAt(4));
                        html.append(scoreText.charAt(5));
                        html.append(scoreText.charAt(6));
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                        html.append("</font>");
                    } else {
                        html.append("<font color=\"#ffffff\">");
                        html.append(scoreText);
                        html.append("</font>");
                    }

                    if (checkAndNotifyForWaiting()) return null;

                    lvia.ScoreRival = Html.fromHtml(html.toString());

                    rScore = score;
                }

                if (checkAndNotifyForWaiting()) return null;

                {
                    int dfscore = myScore - rScore;
                    int score = Math.abs(dfscore);
                    String scoreText = new DecimalFormat("0,000,000").format(score);
                    StringBuilder html = new StringBuilder();
                    if (dfscore > 0) {
                        html.append("<font color=\"#9999ff\">+");
                    } else if (dfscore < 0) {
                        html.append("<font color=\"#ff6666\">-");
                    } else {
                        html.append("<font color=\"#ffffff\">+");
                    }
                    if (score == 0) {
                        html.append("0");
                    } else if (score < 100) {
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                    } else if (score < 1000) {
                        html.append(scoreText.charAt(6));
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                    } else if (score < 10000) {
                        html.append(scoreText.charAt(4));
                        html.append(scoreText.charAt(5));
                        html.append(scoreText.charAt(6));
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                    } else if (score < 100000) {
                        html.append(scoreText.charAt(3));
                        html.append(scoreText.charAt(4));
                        html.append(scoreText.charAt(5));
                        html.append(scoreText.charAt(6));
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                    } else if (score < 1000000) {
                        html.append(scoreText.charAt(2));
                        html.append(scoreText.charAt(3));
                        html.append(scoreText.charAt(4));
                        html.append(scoreText.charAt(5));
                        html.append(scoreText.charAt(6));
                        html.append(scoreText.charAt(7));
                        html.append(scoreText.charAt(8));
                    } else {
                        html.append(scoreText);
                    }

                    html.append("</font>");

                    lvia.ScoreDifference = Html.fromHtml(html.toString());

                }

            }

            lvia.Comment = pat.Comment;

            if (checkAndNotifyForWaiting()) return null;

            return lvia;
        }
    }

    abstract public void onPostExecute(ListViewItemArguments result);
}
