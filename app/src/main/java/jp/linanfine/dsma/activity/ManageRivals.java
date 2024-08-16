package jp.linanfine.dsma.activity;

import android.R.drawable;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.dialog.DialogFromGateList;
import jp.linanfine.dsma.dialog.DialogFromGateRivalList;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.RivalData;
import jp.linanfine.dsma.value.adapter.RivalListAdapter;

public class ManageRivals extends Activity {

    private final Handler mHandler = new Handler();        //Handlerのインスタンス生成
    private ListView mRivalListView;

    RivalListAdapter mAdapter;
    ArrayList<RivalData> mRivals;
    int mActiveRival;
    int mScrollPosition = 0;
    View mHandledView;

    private void listRefresh() {
        mAdapter = new RivalListAdapter(this, mRivals, mActiveRival);
        mRivalListView.setFastScrollEnabled(true);
        mRivalListView.setAdapter(mAdapter);
        mRivalListView.setSelectionFromTop(mAdapter.getCount() - 1, 0);
        mRivalListView.setSelectionFromTop(mScrollPosition, 0);
    }

    private void userActionFromGate() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ManageRivals.this);
        // アラートダイアログのタイトルを設定します
        alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
        alertDialogBuilder.setTitle(getResources().getString(R.string.manage_rivals_from_gate_title));
        // アラートダイアログのメッセージを設定します
        alertDialogBuilder.setMessage(getResources().getString(R.string.manage_rivals_from_gate));
        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します

        // アラートダイアログの中立ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        alertDialogBuilder.setNeutralButton(getResources().getString(R.string.manage_rivals_from_gate_add),
                (dialog, which) -> showDialogFromGateRivalList());
        // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.strings_global____cancel),
                (dialog, which) -> {
                });
        // アラートダイアログのキャンセルが可能かどうかを設定します
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        // アラートダイアログを表示します
        alertDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private String scoreDbFromClipboard() {
        String scoreDb = "";
        if (Build.VERSION.SDK_INT < 11) {
            ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            scoreDb = cbm.getText().toString();
        } else {
            ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData cd = cbm.getPrimaryClip();
            if (cd != null) {
                ClipData.Item item = cd.getItemAt(0);
                scoreDb = item.getText().toString();
            }
        }
        return scoreDb;
    }

    private void userActionAddRival() {
        //テキスト入力を受け付けるビューを作成します。
        final View mainView = ManageRivals.this.getLayoutInflater().inflate(R.layout.view_rival_edit, null);
        final EditText ddrCodeText = mainView.findViewById(R.id.ddrCode);
        final EditText dancerNameText = mainView.findViewById(R.id.dancerName);
        OnFocusChangeListener ofcl = (view, focused) -> {
            if (focused) {
                mHandledView = view;
                mHandler.post(() -> {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                });
            }
        };
        ddrCodeText.setOnFocusChangeListener(ofcl);
        dancerNameText.setOnFocusChangeListener(ofcl);
        final ArrayList<RivalData> rivals = FileReader.readRivals(ManageRivals.this);
        new AlertDialog.Builder(ManageRivals.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(ManageRivals.this.getResources().getString(R.string.manage_rivals_add_rival))
                //setViewにてビューを設定します。
                .setView(mainView)
                .setPositiveButton(ManageRivals.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                    Editable text = ddrCodeText.getText();
                    String tt = text.toString();
                    String ddrCode = "";

                    {
                        if (tt.contains("-")) {
                            String front = "";
                            String back = "";
                            try {
                                String t = tt.substring(0, 4);
                                Integer.valueOf(t);
                                front = t;
                            } catch (Exception ignored) {
                            }
                            try {
                                String t = tt.substring(5, 9);
                                Integer.valueOf(t);
                                back = t;
                            } catch (Exception ignored) {
                            }
                            if (front.length() < 4 || back.length() < 4) {
                                Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.manage_rivals_invalid_ddr_code), Toast.LENGTH_LONG).show();
                                return;
                            }
                            ddrCode = front + back;
                            if (ddrCode.equals("00000000")) {
                                Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.manage_rivals_invalid_ddr_code), Toast.LENGTH_LONG).show();
                                return;
                            }
                        } else {
                            try {
                                Integer.valueOf(tt);
                                ddrCode = tt;
                            } catch (Exception ignored) {
                            }
                            if (ddrCode.length() < 8) {
                                Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.manage_rivals_invalid_ddr_code), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        RivalData r = new RivalData();
                        r.Id = ddrCode;
                        r.Name = dancerNameText.getText().toString();
                        rivals.add(r);
                    }
                    FileReader.saveRivals(ManageRivals.this, rivals, false);
                    ManageRivals.this.initialize();
                })
                .setNegativeButton(ManageRivals.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                    if (mHandledView == null || mHandledView.getWindowToken() == null) {
                        return;
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                })
                .show();

    }

    public void initialize() {
        mRivals = FileReader.readRivals(this);
        mRivals.add(0, null);
        RivalData r = new RivalData();
        r.Id = "00000000";
        mRivals.add(1, r);
        mActiveRival = FileReader.readActiveRival(this);

        if (FileReader.readActiveRival(ManageRivals.this) >= mRivals.size()) {
            FileReader.saveActiveRival(ManageRivals.this, -1);
        }

        this.findViewById(R.id.fromGateButton).setOnClickListener(v -> userActionFromGate());

        this.findViewById(R.id.menuButton).setOnClickListener(v -> userActionAddRival());

        mRivalListView = this.findViewById(R.id.rivalList);
        mRivalListView.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                mScrollPosition = mRivalListView.getFirstVisiblePosition();
            }

            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }
        });
        mRivalListView.setOnItemClickListener((parent, view, position, id) -> {

            if (position == 0) {
                FileReader.saveActiveRival(ManageRivals.this, -1);
                ManageRivals.this.initialize();
                return;
            } else if (position == 1) {
                TreeMap<Integer, MusicScore> scores = FileReader.readScoreList(ManageRivals.this, "00000000");
                if (scores.isEmpty()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ManageRivals.this);
                    // アラートダイアログのタイトルを設定します
                    alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
                    alertDialogBuilder.setTitle(getResources().getString(R.string.strings____ManageRivals____readfromclip_title));
                    // アラートダイアログのメッセージを設定します
                    alertDialogBuilder.setMessage(getResources().getString(R.string.strings____ManageRivals____readfromclip_info));
                    // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                    alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings____Menu_ManageRivals____readfromclip),
                            (dialog, which) -> {
                                String scoreDb = scoreDbFromClipboard();
                                TreeMap<Integer, MusicScore> scores12 = FileReader.scoreFromScoreDb(ManageRivals.this, scoreDb);
                                if (scores12.isEmpty()) {
                                    Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.strings____ManageRivals____cantreadfromclip), Toast.LENGTH_LONG).show();
                                } else {
                                    FileReader.saveScoreData(ManageRivals.this, "00000000", scores12);
                                    FileReader.saveActiveRival(ManageRivals.this, -2);
                                    Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.strings____ManageRivals____readfromclip_done), Toast.LENGTH_LONG).show();
                                    ManageRivals.this.initialize();
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
                } else {
                    //選択項目を準備する。
                    ArrayList<String> str_items = new ArrayList<>();
                    str_items.add(getResources().getString(R.string.strings____Menu_ManageRivals____active));
                    str_items.add(getResources().getString(R.string.strings____Menu_ManageRivals____readfromclip));
                    new AlertDialog.Builder(ManageRivals.this)
                            //.setTitle(mTpat.Pattern.toString() + " : " + mTmusicData.Name)
                            .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                                switch (which) {
                                    case 0: {
                                        FileReader.saveActiveRival(ManageRivals.this, -2);
                                        ManageRivals.this.initialize();
                                        break;
                                    }
                                    case 1: {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ManageRivals.this);
                                        // アラートダイアログのタイトルを設定します
                                        alertDialogBuilder.setIcon(drawable.ic_dialog_alert);
                                        alertDialogBuilder.setTitle(getResources().getString(R.string.strings____ManageRivals____readfromclip_title));
                                        // アラートダイアログのメッセージを設定します
                                        alertDialogBuilder.setMessage(getResources().getString(R.string.strings____ManageRivals____readfromclip_info));
                                        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                                        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.strings____Menu_ManageRivals____readfromclip),
                                                (dialog17, which15) -> {
                                                    String scoreDb = scoreDbFromClipboard();
                                                    TreeMap<Integer, MusicScore> scores1 = FileReader.scoreFromScoreDb(ManageRivals.this, scoreDb);
                                                    if (scores1.isEmpty()) {
                                                        Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.strings____ManageRivals____cantreadfromclip), Toast.LENGTH_LONG).show();
                                                    } else {
                                                        FileReader.saveScoreData(ManageRivals.this, "00000000", scores1);
                                                        FileReader.saveActiveRival(ManageRivals.this, -2);
                                                        Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.strings____ManageRivals____readfromclip_done), Toast.LENGTH_LONG).show();
                                                        ManageRivals.this.initialize();
                                                    }
                                                });
                                        // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                                        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.strings_global____cancel),
                                                (dialog16, which14) -> {
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
                }
                return;
            }

            final ArrayList<RivalData> rivals = FileReader.readRivals(ManageRivals.this);
            final int targetRival = position - 2;

            //選択項目を準備する。
            ArrayList<String> str_items = new ArrayList<>();
            str_items.add(getResources().getString(R.string.strings____Menu_ManageRivals____active));
            str_items.add(getResources().getString(R.string.strings____Menu_ManageRivals____edit));
            str_items.add(getResources().getString(R.string.strings____Menu_ManageRivals____getScore));
            str_items.add(getResources().getString(R.string.strings____Menu_ManageRivals____moveUp));
            str_items.add(getResources().getString(R.string.strings____Menu_ManageRivals____moveDown));
            str_items.add(getResources().getString(R.string.strings____Menu_ManageRivals____removeRival));

            new AlertDialog.Builder(ManageRivals.this)
                    .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                        switch (which) {
                            case 0: {
                                FileReader.saveActiveRival(ManageRivals.this, targetRival);
                                ManageRivals.this.initialize();
                                break;
                            }
                            case 2: {

                                new AlertDialog.Builder(ManageRivals.this)
                                        .setTitle(getResources().getString(R.string.strings____Dialog_GetScores____getScore))
                                        .setMessage(getResources().getString(R.string.strings____Dialog_GetScores____getScoreMessage))
                                        .setPositiveButton(getResources().getString(R.string.strings____Dialog_GetScores____getScoreTypeSp), (dialog15, which13) -> {
                                            mFromGateListGetDouble = false;
                                            mTargetRival = targetRival;
                                            showDialogFromGateList();
                                        })
                                        .setNeutralButton(getResources().getString(R.string.strings____Dialog_GetScores____getScoreTypeDp), (dialog14, which12) -> {
                                            mFromGateListGetDouble = true;
                                            mTargetRival = targetRival;
                                            showDialogFromGateList();
                                        })
                                        .setNegativeButton(getResources().getString(R.string.strings_global____cancel), (dialog13, which1) -> {
                                        })
                                        .show();


                                break;
                            }
                            case 3: {
                                if (targetRival == 0) {
                                    break;
                                }
                                int active = FileReader.readActiveRival(ManageRivals.this);
                                ArrayList<RivalData> rivals1 = FileReader.readRivals(ManageRivals.this);
                                RivalData t = rivals1.get(targetRival);
                                rivals1.set(targetRival, rivals1.get(targetRival - 1));
                                rivals1.set(targetRival - 1, t);
                                FileReader.saveRivals(ManageRivals.this, rivals1, false);
                                if (active == targetRival) {
                                    FileReader.saveActiveRival(ManageRivals.this, targetRival - 1);
                                } else if (active == targetRival - 1) {
                                    FileReader.saveActiveRival(ManageRivals.this, targetRival);
                                } else {
                                    FileReader.saveActiveRival(ManageRivals.this, active);
                                }
                                ManageRivals.this.initialize();
                                break;
                            }
                            case 4: {
                                ArrayList<RivalData> rivals1 = FileReader.readRivals(ManageRivals.this);
                                if (targetRival == rivals1.size() - 1) {
                                    break;
                                }
                                int active = FileReader.readActiveRival(ManageRivals.this);
                                RivalData t = rivals1.get(targetRival);
                                rivals1.set(targetRival, rivals1.get(targetRival + 1));
                                rivals1.set(targetRival + 1, t);
                                FileReader.saveRivals(ManageRivals.this, rivals1, false);
                                if (active == targetRival) {
                                    FileReader.saveActiveRival(ManageRivals.this, targetRival + 1);
                                } else if (active == targetRival + 1) {
                                    FileReader.saveActiveRival(ManageRivals.this, targetRival);
                                } else {
                                    FileReader.saveActiveRival(ManageRivals.this, active);
                                }
                                ManageRivals.this.initialize();
                                break;
                            }
                            case 1: {
                                //テキスト入力を受け付けるビューを作成します。
                                final View mainView = ManageRivals.this.getLayoutInflater().inflate(R.layout.view_rival_edit, null);
                                final EditText ddrCodeText = mainView.findViewById(R.id.ddrCode);
                                final EditText dancerNameText = mainView.findViewById(R.id.dancerName);
                                OnFocusChangeListener ofcl = (view1, focused) -> {
                                    if (focused) {
                                        mHandledView = view1;
                                        mHandler.post(() -> {
                                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                                        });
                                    }
                                };
                                ddrCodeText.setOnFocusChangeListener(ofcl);
                                dancerNameText.setOnFocusChangeListener(ofcl);
                                RivalData r12 = rivals.get(targetRival);
                                ddrCodeText.setText(r12.Id.substring(0, 4) + "-" + r12.Id.substring(4, 8));
                                dancerNameText.setText(r12.Name);
                                new AlertDialog.Builder(ManageRivals.this)
                                        .setIcon(drawable.ic_dialog_info)
                                        .setTitle(ManageRivals.this.getResources().getString(R.string.manage_rivals_edit_rival))
                                        //setViewにてビューを設定します。
                                        .setView(mainView)
                                        .setPositiveButton(ManageRivals.this.getResources().getString(R.string.strings_global____ok), (dialog12, whichButton) -> {
                                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                                            Editable text = ddrCodeText.getText();
                                            String tt = text.toString();
                                            String ddrCode = "";
                                            if (tt.contains("-")) {
                                                String front = "";
                                                String back = "";
                                                try {
                                                    String t = tt.substring(0, 4);
                                                    Integer.valueOf(t);
                                                    front = t;
                                                } catch (Exception ignored) {
                                                }
                                                try {
                                                    String t = tt.substring(5, 9);
                                                    Integer.valueOf(t);
                                                    back = t;
                                                } catch (Exception ignored) {
                                                }
                                                if (front.length() < 4 || back.length() < 4) {
                                                    Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.manage_rivals_invalid_ddr_code), Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                ddrCode = front + back;
                                            } else {
                                                try {
                                                    Integer.valueOf(tt);
                                                    ddrCode = tt;
                                                } catch (Exception ignored) {
                                                }
                                                if (ddrCode.length() < 8) {
                                                    Toast.makeText(ManageRivals.this, ManageRivals.this.getResources().getString(R.string.manage_rivals_invalid_ddr_code), Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                            }
                                            RivalData r1 = rivals.get(targetRival);
                                            r1.Id = ddrCode;
                                            r1.Name = dancerNameText.getText().toString();
                                            rivals.set(targetRival, r1);
                                            FileReader.saveRivals(ManageRivals.this, rivals, false);
                                            ManageRivals.this.initialize();
                                        })
                                        .setNegativeButton(ManageRivals.this.getResources().getString(R.string.strings_global____cancel), (dialog1, whichButton) -> {
                                            if (mHandledView == null || mHandledView.getWindowToken() == null) {
                                                return;
                                            }
                                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                                        })
                                        .show();
                                break;
                            }
                            case 5: {
                                rivals.remove(targetRival);
                                FileReader.saveRivals(ManageRivals.this, rivals, false);
                                ManageRivals.this.initialize();
                                break;
                            }
                        }
                    }).show();
        });

        listRefresh();
    }

    private DialogFromGateList mFromGateList = null;
    private boolean mFromGateListGetDouble = false;
    private int mTargetRival = -1;

    private void showDialogFromGateList() {
        if (mFromGateList != null) {
            mFromGateList.cancel();
            mFromGateList = null;
        }

        mFromGateList = new DialogFromGateList(ManageRivals.this);

        ArrayList<RivalData> rivals = FileReader.readRivals(ManageRivals.this);
        RivalData r = rivals.get(mTargetRival);
        Log.e("DSM", r.Id);

        mFromGateList.setArguments(new AlertDialog.Builder(ManageRivals.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(ManageRivals.this.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetScoreList) + " (" + (mFromGateListGetDouble ? "DP" : "SP") + ") ...")
                        .setView(mFromGateList.getView())
                        .setCancelable(false)
                        .setNegativeButton(ManageRivals.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                            if (mFromGateList != null) {
                                mFromGateList.cancel();
                                mFromGateList = null;
                            }
                        })
                        .setOnCancelListener(arg0 -> ManageRivals.this.initialize())
                        .show()
                , mFromGateListGetDouble, r.Id, r.Name);

        mFromGateList.start();
    }

    private DialogFromGateRivalList mFromGateRivalList = null;

    private void showDialogFromGateRivalList() {
        if (mFromGateRivalList != null) {
            mFromGateRivalList.cancel();
            mFromGateRivalList = null;
        }

        mFromGateRivalList = new DialogFromGateRivalList(ManageRivals.this);

        mFromGateRivalList.setArguments(new AlertDialog.Builder(ManageRivals.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(ManageRivals.this.getResources().getString(R.string.dialog_log_get_rival_list))
                .setView(mFromGateRivalList.getView())
                .setCancelable(false)
                .setNegativeButton(ManageRivals.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                    if (mFromGateRivalList != null) {
                        mFromGateRivalList.cancel();
                        mFromGateRivalList = null;
                    }
                })
                .setOnCancelListener(arg0 -> ManageRivals.this.initialize())
                .show());

        mFromGateRivalList.start();
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
        if (requestCode == DialogFromGateRivalList.LoginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                showDialogFromGateRivalList();
            }
        }
    }

    private void userActionShowSystemMenu() {
        //選択項目を準備する。
        ArrayList<String> str_items = new ArrayList<>();
        str_items.add(getResources().getString(R.string.manage_rivals_from_gate_title));
        str_items.add(getResources().getString(R.string.manage_rivals_add_rival));

        new AlertDialog.Builder(ManageRivals.this)
                .setItems(str_items.toArray(new String[0]), (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    userActionFromGate();
                                    break;
                                case 1:
                                    userActionAddRival();
                                    break;
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
        View mainView = this.getLayoutInflater().inflate(R.layout.activity_manage_rivals, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        this.setContentView(mainView);

        initialize();
    }
}
