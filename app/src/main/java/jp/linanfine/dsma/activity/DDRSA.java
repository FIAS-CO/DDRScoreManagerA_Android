package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.dialog.DialogDdrSaAuthenticate;
import jp.linanfine.dsma.dialog.DialogDdrSaExport;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;

public class DDRSA extends Activity {

    private static String sSaUri = "http://skillattack.com/sa4/";

    private Handler mHandler = new Handler();
    private View mHandledView;

    private DialogDdrSaAuthenticate mDdrSaAuthenticate = null;
    private DialogDdrSaExport mDdrSaExport = null;

    private void initialize() {

        View authenticate = this.findViewById(R.id.authenticate);
        View deleteAuthenticate = this.findViewById(R.id.deleteauthentication);
        View openWebPage = this.findViewById(R.id.open);
        View openUserPage = this.findViewById(R.id.openUserPage);
        View exportScores = this.findViewById(R.id.export);
        TextView authenticatedId = (TextView) this.findViewById(R.id.authenticatedId);

        final String ddrCode;
        final String encryptedPassword;

        ddrCode = FileReader.readDdrSaAuthenticationDdrCode(this);
        encryptedPassword = FileReader.readDdrSaAuthenticationEncryptedPassword(this);

        if (ddrCode.equals("") || encryptedPassword.equals("")) {
            authenticatedId.setText(DDRSA.this.getResources().getString(R.string.strings____Activity_DdrSa____authenticatedIdDefault));
            authenticate.setEnabled(true);
            deleteAuthenticate.setEnabled(false);
            exportScores.setEnabled(false);
            openUserPage.setEnabled(false);
        } else {
            authenticatedId.setText(ddrCode);
            authenticate.setEnabled(false);
            deleteAuthenticate.setEnabled(true);
            exportScores.setEnabled(true);
            openUserPage.setEnabled(true);
        }


        openWebPage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sSaUri));
            startActivity(intent);
        });

        openUserPage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sSaUri + "dancer_profile.php?ddrcode=" + ddrCode));
            startActivity(intent);
        });

        authenticate.setOnClickListener(view -> {
            //テキスト入力を受け付けるビューを作成します。
            final View mainView = DDRSA.this.getLayoutInflater().inflate(R.layout.view_ddr_sa_id_pass_edit, null);
            final EditText ddrCodeText = (EditText) mainView.findViewById(R.id.ddrCode);
            final EditText passwordText = (EditText) mainView.findViewById(R.id.password);

            mainView.findViewById(R.id.visiblePassword).setOnClickListener(v -> {
                if (((CheckBox) mainView.findViewById(R.id.visiblePassword)).isChecked()) {
                    ((TextView) mainView.findViewById(R.id.password)).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    ((TextView) mainView.findViewById(R.id.password)).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            });

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
            passwordText.setOnFocusChangeListener(ofcl);
            new AlertDialog.Builder(DDRSA.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(DDRSA.this.getResources().getString(R.string.input_ddr_sa_id_password))
                    //setViewにてビューを設定します。
                    .setView(mainView)
                    .setPositiveButton(DDRSA.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        closeKeyboard();
                        Editable text = ddrCodeText.getText();
                        String tt = text.toString();
                        String ddrCode1 = "";
                        if (tt.contains("-")) {
                            String front = "";
                            String back = "";
                            try {
                                String t = tt.substring(0, 4);
                                Integer.valueOf(t);
                                front = t;
                            } catch (Exception e) {
                            }
                            try {
                                String t = tt.substring(5, 9);
                                Integer.valueOf(t);
                                back = t;
                            } catch (Exception e) {
                            }
                            if (front.length() < 4 || back.length() < 4) {
                                Toast.makeText(DDRSA.this, DDRSA.this.getResources().getString(R.string.manage_rivals_invalid_ddr_code), Toast.LENGTH_LONG).show();
                                return;
                            }
                            ddrCode1 = front + back;
                        } else {
                            try {
                                Integer.valueOf(tt);
                                ddrCode1 = tt;
                            } catch (Exception e) {
                            }
                            if (ddrCode1.length() < 8) {
                                Toast.makeText(DDRSA.this, DDRSA.this.getResources().getString(R.string.manage_rivals_invalid_ddr_code), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        text = passwordText.getText();
                        String password = text.toString();

                        mDdrSaAuthenticate = new DialogDdrSaAuthenticate(DDRSA.this);

                        mDdrSaAuthenticate.setArguments(new AlertDialog.Builder(DDRSA.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle(DDRSA.this.getResources().getString(R.string.authenticating))
                                .setView(mDdrSaAuthenticate.getView())
                                .setCancelable(false)
                                .setNegativeButton(DDRSA.this.getResources().getString(R.string.strings_global____cancel), (dialog12, whichButton12) -> {
                                    if (mDdrSaAuthenticate != null) {
                                        mDdrSaAuthenticate.cancel();
                                        mDdrSaAuthenticate = null;
                                    }
                                })
                                .setOnCancelListener(arg0 -> DDRSA.this.initialize())
                                .show(), sSaUri, ddrCode1, password);

                        mDdrSaAuthenticate.start();
                    })
                    .setNegativeButton(DDRSA.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                        closeKeyboard();
                    })
                    .show();
        });

        deleteAuthenticate.setOnClickListener(view -> new AlertDialog.Builder(DDRSA.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(DDRSA.this.getResources().getString(R.string.strings____Dialog_DdrSaDeleteAuthenticate____deleteAuthenticationData))
                //setViewにてビューを設定します。
                .setMessage(DDRSA.this.getResources().getString(R.string.strings____Dialog_DdrSaDeleteAuthenticate____confirm))
                .setPositiveButton(DDRSA.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                    FileReader.saveDdrSaAuthentication(DDRSA.this, "", "");
                    DDRSA.this.initialize();
                    Toast.makeText(DDRSA.this, DDRSA.this.getResources().getString(R.string.strings____Dialog_DdrSaDeleteAuthenticate____deleteSucceed), Toast.LENGTH_LONG).show();
                })
                .setNegativeButton(DDRSA.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {

                })
                .show());

        exportScores.setOnClickListener(view -> new AlertDialog.Builder(DDRSA.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(DDRSA.this.getResources().getString(R.string.export_check_title))
                //setViewにてビューを設定します。
                .setMessage(DDRSA.this.getResources().getString(R.string.export_check))
                .setPositiveButton(DDRSA.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {

                    mDdrSaExport = new DialogDdrSaExport(DDRSA.this);

                    mDdrSaExport.setArguments(new AlertDialog.Builder(DDRSA.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle(DDRSA.this.getResources().getString(R.string.exporting))
                            .setView(mDdrSaExport.getView())
                            .setCancelable(false)
                            .setNegativeButton(DDRSA.this.getResources().getString(R.string.strings_global____cancel), (dialog1, whichButton1) -> {
                                if (mDdrSaExport != null) {
                                    mDdrSaExport.cancel();
                                    mDdrSaExport = null;
                                }
                            })
                            .setOnCancelListener(arg0 -> {
                            })
                            .show(), sSaUri, ddrCode, encryptedPassword);

                    mDdrSaExport.start();

                })
                .setNegativeButton(DDRSA.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {

                })
                .show());
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
        setContentView(R.layout.activity_export_to_ddrsa);

        initialize();
    }

    private void closeKeyboard() {
        if (mHandledView == null || mHandledView.getWindowToken() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
    }
}
