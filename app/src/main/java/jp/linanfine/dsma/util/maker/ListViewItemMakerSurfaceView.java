package jp.linanfine.dsma.util.maker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.flare.FlareSkillUpdater;
import jp.linanfine.dsma.value.AppearanceSettingsPx;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.value.ListViewItemArguments;
import jp.linanfine.dsma.value.MusicDataAdapterArguments;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

public class ListViewItemMakerSurfaceView extends ListViewItemMaker {

    private MgrView mMgr;
    private int mMgrWidth;
    private ListViewItemArguments mResult = null;
    private MusicDataAdapterArguments mArguments;
    private AppearanceSettingsPx mAppearance = null;

    public ListViewItemMakerSurfaceView(View convertView, AppearanceSettingsSp appearance, MusicDataAdapterArguments args) {
        super(convertView, appearance);
        mAppearance = appearance.toPx(convertView.getContext());
        mArguments = args;
    }

    @Override
    public void onPreExecute() {
        addToList();
        LinearLayout mgrw = (LinearLayout) mConvertView.findViewById(R.id.top);
        mgrw.removeAllViews();
        mMgr = new MgrView(mConvertView.getContext());
        mMgr.setId(R.id.mgr);
        mgrw.addView(mMgr);
    }

    @Override
    public void onPostExecute(ListViewItemArguments result) {
        mResult = result;
        removeFromList();
    }

    public class MgrView extends View {

        public MgrView(Context context) {
            super(context);
            mBackground = mConvertView.getContext().getResources().getDrawable(R.drawable.list_item_background_color);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mMgrWidth = MeasureSpec.getSize(widthMeasureSpec);
            int rscHeight = 0;
            int idmHeight = 0;
            FontMetrics fontMetrics;

            mPaint.setStyle(Style.FILL);
            mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
            mPaint.setTextSize(mAppearance.ItemMusicRankFontSize);
            mPaint.setStrokeWidth(mAppearance.ItemMusicRankFontSize / 10.0f);
            fontMetrics = mPaint.getFontMetrics();
            idmHeight = Math.max(idmHeight, (int) Math.ceil(fontMetrics.descent - fontMetrics.top));

            mPaint.setStyle(Style.FILL);
            mPaint.setTypeface(Typeface.DEFAULT);
            mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
            mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
            fontMetrics = mPaint.getFontMetrics();
            idmHeight = Math.max(idmHeight, (int) Math.ceil(fontMetrics.descent - fontMetrics.top));

            int idmPad = (int) Math.ceil(fontMetrics.descent);

            mPaint.setStyle(Style.FILL);
            mPaint.setTextSize(mAppearance.ItemMusicNameFontSize);
            mPaint.setTypeface(Typeface.DEFAULT);
            mPaint.setStrokeWidth(mAppearance.ItemMusicNameFontSize / 10.0f);
            mPaint.setColor(0xffffffff);
            fontMetrics = mPaint.getFontMetrics();
            rscHeight = Math.max(idmHeight, (int) Math.ceil(fontMetrics.descent - fontMetrics.top));

            if (mArguments.RivalName != null) {
                mRivalRankBottom = rscHeight + idmHeight;
                rscHeight += (idmHeight + idmPad);
            }

            mPaint.setStyle(Style.FILL_AND_STROKE);
            mPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
            mPaint.setTextSize(mAppearance.ItemMusicLevelFontSize);
            mPaint.setStrokeWidth(mAppearance.ItemMusicLevelFontSize / 10.0f);
            fontMetrics = mPaint.getFontMetrics();
            setMeasuredDimension(mMgrWidth, Math.max(rscHeight, (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent)));
        }

        private float mRivalRankBottom = 0;
        private Drawable mBackground;
        private Paint mPaint = new Paint();
        private Rect mRect = new Rect();

        public void setIsPressed(boolean pressed) {
            mBackground.setState(pressed ? PRESSED_FOCUSED_STATE_SET : ENABLED_STATE_SET);
            this.invalidate();
        }

        @Override
        public void onDraw(Canvas canvas) {

            if (mResult == null) {
                return;
            }
            mPaint.setAntiAlias(true);
            mRect = canvas.getClipBounds();
            mBackground.setBounds(mRect);
            mBackground.draw(canvas);
            FontMetrics fontMetrics;

            {

                Context context = mConvertView.getContext();

                float nextLeft = 0;

                mPaint.setStyle(Style.FILL_AND_STROKE);
                mPaint.setTextAlign(Align.LEFT);
                mPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
                mPaint.setTextSize(mAppearance.ItemMusicLevelFontSize);
                mPaint.setTextScaleX(mAppearance.ItemMusicLevelFontSizeX);
                mPaint.setStrokeWidth(mAppearance.ItemMusicLevelFontSize / 10.0f);
                fontMetrics = mPaint.getFontMetrics();
                if (mResult.Level.charAt(0) != '0') {
                    mPaint.setStyle(Style.FILL_AND_STROKE);
                    mPaint.setColor(mResult.LevelTextColor - 0x99000000);
                    canvas.drawText(mResult.Level, 0, 1, nextLeft, -fontMetrics.top, mPaint);
                    mPaint.setStyle(Style.FILL);
                    mPaint.setColor(mResult.LevelTextColor);
                    canvas.drawText(mResult.Level, 0, 1, nextLeft, -fontMetrics.top, mPaint);
                }
                nextLeft += mPaint.measureText(mResult.Level, 0, 1);
                mPaint.setStyle(Style.FILL_AND_STROKE);
                mPaint.setColor(mResult.LevelTextColor - 0x99000000);
                canvas.drawText(mResult.Level, 1, 2, nextLeft, -fontMetrics.top, mPaint);
                mPaint.setStyle(Style.FILL);
                mPaint.setColor(mResult.LevelTextColor);
                canvas.drawText(mResult.Level, 1, 2, nextLeft, -fontMetrics.top, mPaint);
                nextLeft += mPaint.measureText(mResult.Level, 1, 2);

                float leveltop = fontMetrics.top;

                nextLeft += mAppearance.ItemMusicLevelFontSize / 6.0f / 2.0f;

                mPaint.setStyle(Style.FILL_AND_STROKE);
                mPaint.setColor(mResult.PatternDoubleBackColor & 0x99ffffff);
                canvas.drawRect(nextLeft, 0, nextLeft + mAppearance.ItemMusicLevelFontSize / 6.0f / 2.0f, -leveltop / 3.0f, mPaint);
                mPaint.setStyle(Style.FILL);
                mPaint.setColor(mResult.PatternDoubleBackColor);
                canvas.drawRect(nextLeft, 0, nextLeft + mAppearance.ItemMusicLevelFontSize / 6.0f / 2.0f, -leveltop / 3.0f, mPaint);
                nextLeft += mAppearance.ItemMusicLevelFontSize / 6.0f;

                mPaint.setStyle(Style.FILL_AND_STROKE);
                mPaint.setColor(mResult.PatternSingleBackColor & 0x99ffffff);
                canvas.drawRect(nextLeft, 0, nextLeft + mAppearance.ItemMusicLevelFontSize / 6.0f / 2.0f, -leveltop / 3.0f, mPaint);
                mPaint.setStyle(Style.FILL);
                mPaint.setColor(mResult.PatternSingleBackColor);
                canvas.drawRect(nextLeft, 0, nextLeft + mAppearance.ItemMusicLevelFontSize / 6.0f / 2.0f, -leveltop / 3.0f, mPaint);
                nextLeft += mAppearance.ItemMusicLevelFontSize / 6.0f;

                mPaint.setStyle(Style.FILL_AND_STROKE);
                mPaint.setColor(mResult.PatternDoubleBackColor & 0x99ffffff);
                canvas.drawRect(nextLeft, 0, nextLeft + mAppearance.ItemMusicLevelFontSize / 6.0f / 2.0f, -leveltop / 3.0f, mPaint);
                mPaint.setStyle(Style.FILL);
                mPaint.setColor(mResult.PatternDoubleBackColor);
                canvas.drawRect(nextLeft, 0, nextLeft + mAppearance.ItemMusicLevelFontSize / 6.0f / 2.0f, -leveltop / 3.0f, mPaint);
                nextLeft += mAppearance.ItemMusicLevelFontSize / 6.0f;

                nextLeft += mAppearance.ItemMusicLevelFontSize / 8.0f;

                {
                    float myRankBottom;
                    float nextRight = mMgrWidth - mAppearance.ItemMusicRankFontSize / 5.0f;
                    float nextRightRival = nextRight;

                    mPaint.setStyle(Style.FILL);
                    mPaint.setTextSize(mAppearance.ItemMusicRankFontSize);
                    mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
                    mPaint.setStrokeWidth(mAppearance.ItemMusicRankFontSize / 10.0f);
                    fontMetrics = mPaint.getFontMetrics();

                    myRankBottom = (fontMetrics.descent / 4.0f) - fontMetrics.top;

                    if (mAppearance.ShowMaxCombo) {
                        {

                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.DEFAULT);
                            mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                            mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                            mPaint.setColor(0xff999999);
                            fontMetrics = mPaint.getFontMetrics();
                            String combo = String.valueOf(mScoreData.MaxCombo);
                            float nextRightT = nextRight - mPaint.measureText(combo);
                            canvas.drawText(combo, 0, combo.length(), nextRightT, myRankBottom, mPaint);
                            nextRight -= mPaint.measureText("000");
                            if (mAppearance.ShowScore) {
                                mPaint.setColor(0xff666666);
                                nextRight -= mPaint.measureText("/");
                                canvas.drawText("/", 0, 1, nextRight, myRankBottom, mPaint);
                            }

                        }

                        if (mArguments.RivalName != null) {

                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.DEFAULT);
                            mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                            mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                            mPaint.setColor(0xff999999);
                            fontMetrics = mPaint.getFontMetrics();
                            String combo = String.valueOf(mRivalScoreData.MaxCombo);
                            float nextRightT = nextRightRival - mPaint.measureText(combo);
                            canvas.drawText(combo, 0, combo.length(), nextRightT, mRivalRankBottom, mPaint);
                            nextRightRival -= mPaint.measureText("000");
                            if (mAppearance.ShowScore) {
                                mPaint.setColor(0xff666666);
                                nextRightRival -= mPaint.measureText("/");
                                canvas.drawText("/", 0, 1, nextRightRival, mRivalRankBottom, mPaint);
                            }
                        }

                        nextRight = Math.min(nextRight, nextRightRival);
                        nextRightRival = nextRight;
                    }

                    // フレアスキルの表示
                    if (mAppearance.ShowFlareSkill) {
                        mPaint.setStyle(Style.FILL);
                        mPaint.setTextAlign(Align.LEFT);
                        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                        mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);

                        String flareSkillText = String.format("%04d", mScoreData.flareSkill);
                        float flareSkillWidth = mPaint.measureText("0000");
                        nextRight -= flareSkillWidth + mAppearance.ItemMusicScoreFontSize / 4.0f; // スコアとフレアスキルの間隔

                        int leadingZeros = flareSkillText.length() - flareSkillText.replaceAll("^0+", "").length();

                        if (leadingZeros > 0) {
                            mPaint.setColor(0x33ffffff); // 薄い色
                            canvas.drawText(flareSkillText.substring(0, leadingZeros), nextRight, myRankBottom, mPaint);
                        }

                        if (leadingZeros < 4) {
                            mPaint.setColor(0xff999999); // 通常の色
                            canvas.drawText(flareSkillText.substring(leadingZeros), nextRight + mPaint.measureText("0") * leadingZeros, myRankBottom, mPaint);
                        }

                        nextRight -= mAppearance.ItemMusicScoreFontSize / 2.0f;
                    }

                    // フレアランクの表示
                    if (mAppearance.ShowFlareRank) {
                        mPaint.setStyle(Style.FILL);
                        mPaint.setTextAlign(Align.RIGHT);  // 右揃えに変更
                        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                        mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                        mPaint.setColor(0xff999999);

                        String flareRankText = FlareSkillUpdater.getFlareRankText(mScoreData.flareRank);
                        float maxFlareRankWidth = mPaint.measureText("VIII");  // 最大幅を "VIII" の幅に設定
                        nextRight -= maxFlareRankWidth + mAppearance.ItemMusicScoreFontSize / 4.0f; // スコアとフレアランクの間隔
                        canvas.drawText(flareRankText, nextRight + maxFlareRankWidth, myRankBottom, mPaint);

                        nextRight -= mAppearance.ItemMusicScoreFontSize / 2.0f;
                    }

                    if (mAppearance.ShowScore) {
                        {
                            float rankBottom = myRankBottom;

                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.DEFAULT);
                            mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                            mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                            if (mScoreData.Rank == MusicRank.Noplay) {
                                mPaint.setColor(0x33ffffff);
                            } else {
                                mPaint.setColor(0xffffffff);
                            }
                            fontMetrics = mPaint.getFontMetrics();
                            int i = 8;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);
                            if (mScoreData.Score < 10) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);
                            if (mScoreData.Score < 100) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);
                            if (mScoreData.Score < 1000) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);
                            --i;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);
                            if (mScoreData.Score < 10000) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);
                            if (mScoreData.Score < 100000) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);
                            if (mScoreData.Score < 1000000) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);
                            --i;
                            nextRight -= mPaint.measureText(mResult.Score, i, i + 1);
                            canvas.drawText(mResult.Score, i, i + 1, nextRight, (rankBottom - (-fontMetrics.top)) - fontMetrics.top, mPaint);

                        }

                        if (mArguments.RivalName != null) {
                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.DEFAULT);
                            mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                            mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                            if (mRivalScoreData.Rank == MusicRank.Noplay) {
                                mPaint.setColor(0x33ffffff);
                            } else {
                                mPaint.setColor(0xffffffff);
                            }
                            fontMetrics = mPaint.getFontMetrics();
                            int i = 8;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);
                            if (mRivalScoreData.Score < 10) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);
                            if (mRivalScoreData.Score < 100) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);
                            if (mRivalScoreData.Score < 1000) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);
                            --i;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);
                            if (mRivalScoreData.Score < 10000) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);
                            if (mRivalScoreData.Score < 100000) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);
                            if (mRivalScoreData.Score < 1000000) {
                                mPaint.setColor(0x33ffffff);
                            }
                            --i;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);
                            --i;
                            nextRightRival -= mPaint.measureText(mResult.ScoreRival, i, i + 1);
                            canvas.drawText(mResult.ScoreRival, i, i + 1, nextRightRival, mRivalRankBottom, mPaint);

                        }

                        nextRight = Math.min(nextRight, nextRightRival);
                        nextRightRival = nextRight;
                    }

                    if (mAppearance.ShowDanceLevel) {
                        {

                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
                            mPaint.setTextSize(mAppearance.ItemMusicRankFontSize);
                            mPaint.setTextScaleX(mAppearance.ItemMusicRankFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicRankFontSize / 10.0f);
                            mPaint.setColor(
                                    mScoreData.FullComboType == FullComboType.MerverousFullCombo ? 0xffffffff :
                                            mScoreData.FullComboType == FullComboType.PerfectFullCombo ? 0xffffff00 :
                                                    mScoreData.FullComboType == FullComboType.FullCombo ? 0xff00ff00 :
                                                            mScoreData.FullComboType == FullComboType.GoodFullCombo ? 0xff0099ff :
                                                                    mScoreData.FullComboType == FullComboType.Life4 ? 0xffff6633 :
                                                                            0x00000000);
                            fontMetrics = mPaint.getFontMetrics();
                            nextRight -= mPaint.measureText("゜") / 2;
                            canvas.drawText("゜", 0, 1, nextRight, myRankBottom, mPaint);

                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
                            mPaint.setTextScaleX(mAppearance.ItemMusicRankFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicRankFontSize / 10.0f);
                            mPaint.setTextSize(mAppearance.ItemMusicRankFontSize);
                            float nextRightT = nextRight - mPaint.measureText("AAA", 0, "AAA".length());
                            mPaint.setTextSize(mScoreData.Rank == MusicRank.Noplay ? mAppearance.ItemMusicRankFontSize / 2.0f : mAppearance.ItemMusicRankFontSize);
                            mPaint.setColor(
                                    mScoreData.Rank == MusicRank.AAA ? 0xffffffcc :
                                            mScoreData.Rank == MusicRank.AAp ? 0xffffff66 :
                                                    mScoreData.Rank == MusicRank.AA ? 0xffffff66 :
                                                            mScoreData.Rank == MusicRank.AAm ? 0xffffff66 :
                                                                    mScoreData.Rank == MusicRank.Ap ? 0xffffff00 :
                                                                            mScoreData.Rank == MusicRank.A ? 0xffffff00 :
                                                                                    mScoreData.Rank == MusicRank.Am ? 0xffffff00 :
                                                                                            mScoreData.Rank == MusicRank.Bp ? 0xff0099ff :
                                                                                                    mScoreData.Rank == MusicRank.B ? 0xff0099ff :
                                                                                                            mScoreData.Rank == MusicRank.Bm ? 0xff0099ff :
                                                                                                                    mScoreData.Rank == MusicRank.Cp ? 0xffff00cc :
                                                                                                                            mScoreData.Rank == MusicRank.C ? 0xffff00cc :
                                                                                                                                    mScoreData.Rank == MusicRank.Cm ? 0xffff00cc :
                                                                                                                                            mScoreData.Rank == MusicRank.Dp ? 0xffff0000 :
                                                                                                                                                    mScoreData.Rank == MusicRank.D ? 0xffff0000 :
                                                                                                                                                            mScoreData.Rank == MusicRank.E ? 0xffcccccc :
                                                                                                                                                                    0xff666666);
                            String rankText =
                                    mScoreData.Rank == MusicRank.AAA ? "AAA" :
                                            mScoreData.Rank == MusicRank.AAp ? "AA+" :
                                                    mScoreData.Rank == MusicRank.AA ? "AA" :
                                                            mScoreData.Rank == MusicRank.AAm ? "AA-" :
                                                                    mScoreData.Rank == MusicRank.Ap ? "A+" :
                                                                            mScoreData.Rank == MusicRank.A ? "A" :
                                                                                    mScoreData.Rank == MusicRank.Am ? "A-" :
                                                                                            mScoreData.Rank == MusicRank.Bp ? "B+" :
                                                                                                    mScoreData.Rank == MusicRank.B ? "B" :
                                                                                                            mScoreData.Rank == MusicRank.Bm ? "B-" :
                                                                                                                    mScoreData.Rank == MusicRank.Cp ? "C+" :
                                                                                                                            mScoreData.Rank == MusicRank.C ? "C" :
                                                                                                                                    mScoreData.Rank == MusicRank.Cm ? "C-" :
                                                                                                                                            mScoreData.Rank == MusicRank.Dp ? "D+" :
                                                                                                                                                    mScoreData.Rank == MusicRank.D ? "D" :
                                                                                                                                                            mScoreData.Rank == MusicRank.E ? "E" :
                                                                                                                                                                    "NoPlay";
                            String rankTextMeasure =
                                    mScoreData.Rank == MusicRank.AAA ? "AAA" :
                                            mScoreData.Rank == MusicRank.AAp ? "AA" :
                                                    mScoreData.Rank == MusicRank.AA ? "AA" :
                                                            mScoreData.Rank == MusicRank.AAm ? "AA" :
                                                                    mScoreData.Rank == MusicRank.Ap ? "A" :
                                                                            mScoreData.Rank == MusicRank.A ? "A" :
                                                                                    mScoreData.Rank == MusicRank.Am ? "A" :
                                                                                            mScoreData.Rank == MusicRank.Bp ? "B" :
                                                                                                    mScoreData.Rank == MusicRank.B ? "B" :
                                                                                                            mScoreData.Rank == MusicRank.Bm ? "B" :
                                                                                                                    mScoreData.Rank == MusicRank.Cp ? "C" :
                                                                                                                            mScoreData.Rank == MusicRank.C ? "C" :
                                                                                                                                    mScoreData.Rank == MusicRank.Cm ? "C" :
                                                                                                                                            mScoreData.Rank == MusicRank.Dp ? "D" :
                                                                                                                                                    mScoreData.Rank == MusicRank.D ? "D" :
                                                                                                                                                            mScoreData.Rank == MusicRank.E ? "E" :
                                                                                                                                                                    "NoPlay";
                            fontMetrics = mPaint.getFontMetrics();
                            canvas.drawText(rankText, 0, rankText.length(), nextRight - mPaint.measureText(rankTextMeasure, 0, rankTextMeasure.length()), myRankBottom, mPaint);

                            nextRight = nextRightT;

                        }

                        if (mArguments.RivalName != null) {

                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
                            mPaint.setTextSize(mAppearance.ItemMusicRankFontSize);
                            mPaint.setTextScaleX(mAppearance.ItemMusicRankFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicRankFontSize / 10.0f);
                            mPaint.setColor(
                                    mRivalScoreData.FullComboType == FullComboType.MerverousFullCombo ? 0xffffffff :
                                            mRivalScoreData.FullComboType == FullComboType.PerfectFullCombo ? 0xffffff00 :
                                                    mRivalScoreData.FullComboType == FullComboType.FullCombo ? 0xff00ff00 :
                                                            mRivalScoreData.FullComboType == FullComboType.GoodFullCombo ? 0xff0099ff :
                                                                    mRivalScoreData.FullComboType == FullComboType.Life4 ? 0xffff6633 :
                                                                            0x00000000);
                            fontMetrics = mPaint.getFontMetrics();
                            nextRightRival -= mPaint.measureText("゜", 0, 1) / 2;
                            canvas.drawText("゜", 0, 1, nextRightRival, mRivalRankBottom, mPaint);

                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
                            mPaint.setTextScaleX(mAppearance.ItemMusicRankFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicRankFontSize / 10.0f);
                            mPaint.setTextSize(mAppearance.ItemMusicRankFontSize);
                            float nextRightRivalT = nextRightRival - mPaint.measureText("AAA", 0, "AAA".length());
                            mPaint.setTextSize(mRivalScoreData.Rank == MusicRank.Noplay ? mAppearance.ItemMusicRankFontSize / 2.0f : mAppearance.ItemMusicRankFontSize);
                            mPaint.setColor(
                                    mRivalScoreData.Rank == MusicRank.AAA ? 0xffffffcc :
                                            mRivalScoreData.Rank == MusicRank.AAp ? 0xffffff66 :
                                                    mRivalScoreData.Rank == MusicRank.AA ? 0xffffff66 :
                                                            mRivalScoreData.Rank == MusicRank.AAm ? 0xffffff66 :
                                                                    mRivalScoreData.Rank == MusicRank.Ap ? 0xffffff00 :
                                                                            mRivalScoreData.Rank == MusicRank.A ? 0xffffff00 :
                                                                                    mRivalScoreData.Rank == MusicRank.Am ? 0xffffff00 :
                                                                                            mRivalScoreData.Rank == MusicRank.Bp ? 0xff0099ff :
                                                                                                    mRivalScoreData.Rank == MusicRank.B ? 0xff0099ff :
                                                                                                            mRivalScoreData.Rank == MusicRank.Bm ? 0xff0099ff :
                                                                                                                    mRivalScoreData.Rank == MusicRank.Cp ? 0xffff00cc :
                                                                                                                            mRivalScoreData.Rank == MusicRank.C ? 0xffff00cc :
                                                                                                                                    mRivalScoreData.Rank == MusicRank.Cm ? 0xffff00cc :
                                                                                                                                            mRivalScoreData.Rank == MusicRank.Dp ? 0xffff0000 :
                                                                                                                                                    mRivalScoreData.Rank == MusicRank.D ? 0xffff0000 :
                                                                                                                                                            mRivalScoreData.Rank == MusicRank.E ? 0xffcccccc :
                                                                                                                                                                    0xff666666);
                            String rankText =
                                    mRivalScoreData.Rank == MusicRank.AAA ? "AAA" :
                                            mRivalScoreData.Rank == MusicRank.AAp ? "AA+" :
                                                    mRivalScoreData.Rank == MusicRank.AA ? "AA" :
                                                            mRivalScoreData.Rank == MusicRank.AAm ? "AA-" :
                                                                    mRivalScoreData.Rank == MusicRank.Ap ? "A+" :
                                                                            mRivalScoreData.Rank == MusicRank.A ? "A" :
                                                                                    mRivalScoreData.Rank == MusicRank.Am ? "A-" :
                                                                                            mRivalScoreData.Rank == MusicRank.Bp ? "B+" :
                                                                                                    mRivalScoreData.Rank == MusicRank.B ? "B" :
                                                                                                            mRivalScoreData.Rank == MusicRank.Bm ? "B-" :
                                                                                                                    mRivalScoreData.Rank == MusicRank.Cp ? "C+" :
                                                                                                                            mRivalScoreData.Rank == MusicRank.C ? "C" :
                                                                                                                                    mRivalScoreData.Rank == MusicRank.Cm ? "C-" :
                                                                                                                                            mRivalScoreData.Rank == MusicRank.Dp ? "D+" :
                                                                                                                                                    mRivalScoreData.Rank == MusicRank.D ? "D" :
                                                                                                                                                            mRivalScoreData.Rank == MusicRank.E ? "E" :
                                                                                                                                                                    "NoPlay";
                            String rankTextMeasure =
                                    mRivalScoreData.Rank == MusicRank.AAA ? "AAA" :
                                            mRivalScoreData.Rank == MusicRank.AAp ? "AA" :
                                                    mRivalScoreData.Rank == MusicRank.AA ? "AA" :
                                                            mRivalScoreData.Rank == MusicRank.AAm ? "AA" :
                                                                    mRivalScoreData.Rank == MusicRank.Ap ? "A" :
                                                                            mRivalScoreData.Rank == MusicRank.A ? "A" :
                                                                                    mRivalScoreData.Rank == MusicRank.Am ? "A" :
                                                                                            mRivalScoreData.Rank == MusicRank.Bp ? "B" :
                                                                                                    mRivalScoreData.Rank == MusicRank.B ? "B" :
                                                                                                            mRivalScoreData.Rank == MusicRank.Bm ? "B" :
                                                                                                                    mRivalScoreData.Rank == MusicRank.Cp ? "C" :
                                                                                                                            mRivalScoreData.Rank == MusicRank.C ? "C" :
                                                                                                                                    mRivalScoreData.Rank == MusicRank.Cm ? "C" :
                                                                                                                                            mRivalScoreData.Rank == MusicRank.Dp ? "D" :
                                                                                                                                                    mRivalScoreData.Rank == MusicRank.D ? "D" :
                                                                                                                                                            mRivalScoreData.Rank == MusicRank.E ? "E" :
                                                                                                                                                                    "NoPlay";
                            fontMetrics = mPaint.getFontMetrics();
                            //mRivalRankBottom = myRankBottom-fontMetrics.top;
                            canvas.drawText(rankText, 0, rankText.length(), nextRightRival - mPaint.measureText(rankTextMeasure, 0, rankTextMeasure.length()), mRivalRankBottom, mPaint);
                            nextRightRival = nextRightRivalT;

                        }

                        nextRight = Math.min(nextRight, nextRightRival);
                        nextRightRival = nextRight;
                    }

                    if (mAppearance.ShowPlayCount) {
                        {

                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.DEFAULT);
                            mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                            mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                            mPaint.setColor(0xff999999);
                            nextRight -= mPaint.measureText("0");
                            fontMetrics = mPaint.getFontMetrics();
                            String combo = String.valueOf(mScoreData.PlayCount);
                            float nextRightT = nextRight - mPaint.measureText(combo);
                            canvas.drawText(combo, 0, combo.length(), nextRightT, myRankBottom, mPaint);
                            nextRight -= mPaint.measureText("000");
                            if (mAppearance.ShowClearCount) {
                                mPaint.setColor(0xff666666);
                                nextRight -= mPaint.measureText("/");
                                canvas.drawText("/", 0, 1, nextRight, myRankBottom, mPaint);
                            }
                        }

                        nextRight = Math.min(nextRight, nextRightRival);
                    }

                    if (mAppearance.ShowClearCount) {
                        {
                            mPaint.setStyle(Style.FILL);
                            mPaint.setTextAlign(Align.LEFT);
                            mPaint.setTypeface(Typeface.DEFAULT);
                            mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                            mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                            mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                            mPaint.setColor(0xff999999);
                            fontMetrics = mPaint.getFontMetrics();
                            String combo = String.valueOf(mScoreData.ClearCount);
                            float nextRightT = nextRight - mPaint.measureText(combo);
                            canvas.drawText(combo, 0, combo.length(), nextRightT, myRankBottom, mPaint);
                            nextRight -= mPaint.measureText("000");

                        }

                        nextRight = Math.min(nextRight, nextRightRival);
                    }

                    {
                        nextRight -= mAppearance.ItemMusicLevelFontSize / 2.0f;
                        mPaint.setStyle(Style.FILL);
                        mPaint.setTextAlign(Align.LEFT);
                        mPaint.setTextSize(mAppearance.ItemMusicNameFontSize);
                        mPaint.setTypeface(Typeface.DEFAULT);
                        mPaint.setTextScaleX(mAppearance.ItemMusicNameFontSizeX);
                        mPaint.setStrokeWidth(mAppearance.ItemMusicNameFontSize / 10.0f);
                        mPaint.setColor(0xffffffff);
                        fontMetrics = mPaint.getFontMetrics();
                        CharSequence musicName = mResult.Name;
                        if (mPaint.measureText(musicName, 0, musicName.length()) + nextLeft >= nextRight) {
                            do {
                                musicName = musicName.subSequence(0, musicName.length() - 1);
                            } while (musicName.length() > 1 && mPaint.measureText(musicName + "...", 0, musicName.length() + 3) + nextLeft >= nextRight);
                            musicName = musicName.subSequence(0, musicName.length()) + "...";
                        }
                        canvas.drawText(musicName, 0, musicName.length(), nextLeft, -fontMetrics.top, mPaint);
                    }

                    if (mArguments.RivalName != null) {

                        nextRightRival -= mAppearance.ItemMusicLevelFontSize / 2.0f;
                        mPaint.setStyle(Style.FILL);
                        mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                        mPaint.setTypeface(Typeface.DEFAULT);
                        mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                        mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                        fontMetrics = mPaint.getFontMetrics();
                        float scoreDiffLen = mPaint.measureText("+0,000,000", 0, 9);

                        mPaint.setStyle(Style.FILL);
                        mPaint.setTextAlign(Align.LEFT);
                        mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                        mPaint.setTypeface(Typeface.DEFAULT);
                        mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                        mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                        mPaint.setColor(0xff999999);
                        fontMetrics = mPaint.getFontMetrics();
                        CharSequence rivalName = mArguments.RivalName;
                        if (mPaint.measureText(rivalName + ":", 0, rivalName.length() + 1) + nextLeft + scoreDiffLen >= nextRightRival) {
                            do {
                                rivalName = rivalName.subSequence(0, rivalName.length() - 1);
                            } while (rivalName.length() > 1 && mPaint.measureText(rivalName + "...:", 0, rivalName.length() + 4) + nextLeft + scoreDiffLen >= nextRightRival);
                            rivalName = rivalName.subSequence(0, rivalName.length()) + "...";
                        }
                        rivalName = rivalName + ":";
                        nextRightRival -= mPaint.measureText(rivalName, 0, rivalName.length());
                        canvas.drawText(rivalName, 0, rivalName.length(), nextRightRival, mRivalRankBottom, mPaint);

                        nextRightRival -= mAppearance.ItemMusicScoreFontSize / 2.0f;
                        mPaint.setStyle(Style.FILL);
                        mPaint.setTextAlign(Align.LEFT);
                        mPaint.setTextSize(mAppearance.ItemMusicScoreFontSize);
                        mPaint.setTypeface(Typeface.DEFAULT);
                        mPaint.setTextScaleX(mAppearance.ItemMusicScoreFontSizeX);
                        mPaint.setStrokeWidth(mAppearance.ItemMusicScoreFontSize / 10.0f);
                        mPaint.setColor(mScoreData.Score > mRivalScoreData.Score ? 0xff99ccff : mScoreData.Score < mRivalScoreData.Score ? 0xffff6633 : 0xffcccccc);
                        fontMetrics = mPaint.getFontMetrics();
                        nextRightRival -= mPaint.measureText(mResult.ScoreDifference, 0, mResult.ScoreDifference.length());
                        canvas.drawText(mResult.ScoreDifference, 0, mResult.ScoreDifference.length(), nextRightRival, mRivalRankBottom, mPaint);
                    }
                }
            }
        }
    }
}
