package jp.linanfine.dsma.util.file;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.value.AppearanceSettingsSp;
import jp.linanfine.dsma.value.BooleanPair;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.GestureSettings;
import jp.linanfine.dsma.value.IdToWebMusicIdList;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicFilter;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.MusicSort;
import jp.linanfine.dsma.value.RecentData;
import jp.linanfine.dsma.value.RivalData;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.StatusData;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.value.WebMusicId;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.GestureAction;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.MusicSortType;
import jp.linanfine.dsma.value._enum.PatternType;
import jp.linanfine.dsma.value._enum.SeriesTitle;
import jp.linanfine.dsma.value._enum.ShockArrowInclude;
import jp.linanfine.dsma.value._enum.SortOrder;

public class FileReader {
    public static boolean mEnableAd = true;

    public static String readLastVersion(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Version", Context.MODE_PRIVATE);
        return pref.getString("LastVersion", "0.0.00000000");
    }

    public static void saveLastVersion(Context context, String version) {
        SharedPreferences pref = context.getSharedPreferences("Version", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("LastVersion", version);
        e.commit();
    }

    public static String readMusicListVersion(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Version", Context.MODE_PRIVATE);
        return pref.getString("MusicListVersion", "Implemented File");
    }

    public static void saveMusicListVersion(Context context, String version) {
        SharedPreferences pref = context.getSharedPreferences("Version", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("MusicListVersion", version);
        e.commit();
    }

    public static long readLastBootTime(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Version", Context.MODE_PRIVATE);
        return pref.getLong("LastBootTime", 0);
    }

    public static void saveLastBootTime(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Version", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putLong("LastBootTime", new Date().getTime());
        e.commit();
    }

    public static String[] readCopyFormats(Context context) {
        String[] ret = new String[3];
        SharedPreferences pref = context.getSharedPreferences("CopyFormats.txt", Context.MODE_PRIVATE);
        ret[0] = pref.getString("Format0", "%t% %p:b:B:D:E:C%%y:SP:DP% %s0,%");
        ret[1] = pref.getString("Format1", "%t% %p:b:B:D:E:C%%y:SP:DP% %l:AAA:AA+:AA:AA-:A+:A:A-:B+:B:B-:C+:C:C-:D+:D:E:-%");
        ret[2] = pref.getString("Format2", "%t% %p:b:B:D:E:C%%y:SP:DP% (%d0%) %l:AAA:AA+:AA:AA-:A+:A:A-:B+:B:B-:C+:C:C-:D+:D:E:-% %s0,%/%c0% %f:MFC:PFC:FC:GFC:Life4:NoFC% (%e0%/%a0%)");
        return ret;
    }

    public static void saveCopyFormats(Context context, String[] formats) {
        SharedPreferences pref = context.getSharedPreferences("CopyFormats.txt", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("Format0", formats[0]);
        e.putString("Format1", formats[1]);
        e.putString("Format2", formats[2]);
        e.commit();
    }

    public static void convertOldFilterSortSetting(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_PRIVATE);
        int pagerCount = pref.getInt("PagerCount", -1);
        int activePager = pref.getInt("ActivePager", -1);
        if (pagerCount < 0 || activePager < 0) {
            return;
        }
        saveMusicFilterCount(context, pagerCount);
        saveActiveMusicFilter(context, activePager);
        saveMusicSortCount(context, pagerCount);
        saveActiveMusicSort(context, activePager);
        for (int i = 0; i < pagerCount; ++i) {
            String pagerName = readPagerName(context, i);
            saveMusicFilterName(context, i, pagerName);
            saveMusicSortName(context, i, pagerName);
        }
        Editor e = pref.edit();
        e.clear();
        e.commit();
    }

    public static long readLastAdTapTime(Context context) {
        SharedPreferences pref = context.getSharedPreferences("AdSetting", Context.MODE_PRIVATE);
        return pref.getLong("LastOpenedTime", 0);
    }

    public static void requestAd(LinearLayout adContainer, Activity activity) {
        final Activity act = activity;
        //if(readShowAd(activity))
        {
            if (mEnableAd) {
                adContainer.removeAllViews();
                long now = new Date().getTime();
                long lastopened = readLastAdTapTime(act.getApplicationContext());
                if (now - lastopened > 86400000) {
                    AdView adGoogle = new AdView(activity);
                    adGoogle.setAdSize(AdSize.BANNER);

                    // Sample AdMob app ID: ca-app-pub-3940256099942544/6300978111
                    // product app ID: ca-app-pub-8151928728657048/2728579739
                    adGoogle.setAdUnitId("ca-app-pub-8151928728657048/2728579739");
                    adGoogle.setAdListener(new AdListener() {
                        public void onAdLeftApplication() {
                            SharedPreferences pref = act.getApplicationContext().getSharedPreferences("AdSetting", Context.MODE_PRIVATE);
                            Editor e = pref.edit();
                            e.putLong("LastOpenedTime", new Date().getTime());
                            e.commit();
                        }
                    });
                    adContainer.addView(adGoogle);
                    AdRequest adr = new AdRequest.Builder().build();
                    //adr.addTestDevice("");
                    adGoogle.loadAd(adr);
                    adContainer.setVisibility(View.VISIBLE);
                } else {
                    adContainer.setVisibility(View.GONE);
                }
            } else {
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }
        }
        //else
        {
            //	adContainer.removeAllViews();
            //	adContainer.setVisibility(View.GONE);
        }
    }

    public static boolean readShowAd(Context context) {
        //SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        return pref.getBoolean("ShowAd", true);
    }

    public static void backupMyList(Context context) {
        int count = readMyListCount(context);
        if (count == 0) {
            Toast.makeText(context, context.getResources().getString(R.string.global_setting_exportnoone_mylist), Toast.LENGTH_LONG).show();
            return;
        }

        String fileName = "ddr_score_manager_mylist_backup.zds";
        String path;

        {
            path = Environment.getExternalStorageDirectory().toString() + "/jp.linanfine.dsm/";
        }

        File pathDir = new File(path);
        if (!pathDir.exists()) {
            pathDir.mkdir();
        }

        ZipOutputStream zos = null;
        //byte[] buf = new byte[1024];

        try {
            zos = new ZipOutputStream(new FileOutputStream(path + fileName));
        } catch (FileNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(R.string.global_setting_exportfailed_mylist), Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < count; ++i) {
            String name = readMyListName(context, i);
            ArrayList<UniquePattern> myList = readMyList(context, i);

            String entryName = "MyList" + String.valueOf(i) + ".txt";

            ZipEntry ze = new ZipEntry(entryName);
            try {
                zos.putNextEntry(ze);
            } catch (IOException e1) {
                continue;
            }

            //BufferedWriter bw;

            try {
                zos.write((name + "\n").getBytes());
            } catch (IOException e1) {
                continue;
            }

            for (UniquePattern up : myList) {
                Log.e("DSM", String.valueOf(up.MusicId));
                try {
                    zos.write((String.valueOf(up.MusicId) + "," + String.valueOf(getPatternTypeNum(up.Pattern)) + "\n").getBytes());
                } catch (IOException e1) {
                    continue;
                }
            }

            try {
                zos.closeEntry();
            } catch (IOException e) {
                continue;
            }

        }

        try {
            zos.close();
        } catch (IOException e) {
            return;
        }

        Toast.makeText(context, context.getResources().getString(R.string.global_setting_exportedA_mylist) + path + fileName + context.getResources().getString(R.string.global_setting_exportedB_mylist), Toast.LENGTH_LONG).show();

    }

    @SuppressWarnings("resource")
    public static void restoreMyList(Context context) {

        String fileName = "ddr_score_manager_mylist_backup.zds";
        String path;

        {
            path = Environment.getExternalStorageDirectory().toString() + "/jp.linanfine.dsm/";
        }

        ZipInputStream zin = null;
        //BufferedOutputStream out = null;
        @SuppressWarnings("unused")
        ZipEntry zipEntry = null;

        try {
            zin = new ZipInputStream(new FileInputStream(path + fileName));
        } catch (FileNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(R.string.global_setting_importfailed_mylist), Toast.LENGTH_LONG).show();
            return;
        }

        int myListCount = readMyListCount(context);
        int firstCount = myListCount;
        while (true) {

            try {
                if ((zipEntry = zin.getNextEntry()) == null) {
                    break;
                }
            } catch (IOException e1) {
                break;
            }

            {
                BufferedReader br;
                br = new BufferedReader(new InputStreamReader(zin));
                String line;
                ArrayList<UniquePattern> myList = new ArrayList<UniquePattern>();
                try {
                    if ((line = br.readLine()) != null) {
                        saveMyListCount(context, myListCount + 1);
                        saveMyListName(context, myListCount, line);
                    }
                    while ((line = br.readLine()) != null) {
                        String[] sp = line.split(",");
                        UniquePattern up = new UniquePattern();
                        up.MusicId = Integer.valueOf(sp[0]);
                        up.Pattern = getPatternType(Integer.valueOf(sp[1]));
                        myList.add(up);
                    }
                } catch (IOException e1) {
                    continue;
                }
                saveMyList(context, myListCount, myList);
                myListCount++;
                //try {
                //	br.close();
                //} catch (IOException e) { continue; }
            }

            try {
                zin.closeEntry();
            } catch (IOException e) {
                continue;
            }

        }

        try {
            zin.close();
        } catch (IOException e) {
            return;
        }

        if (firstCount != myListCount) {
            Toast.makeText(context, context.getResources().getString(R.string.global_setting_importedA) + path + fileName + context.getResources().getString(R.string.global_setting_importedB), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.global_setting_importnoone_mylist), Toast.LENGTH_LONG).show();
        }

    }

    public static void backupScores(Context context) {
        String fileName = "ddr_score_manager_score_backup.zds";
        String path;

        {
            path = Environment.getExternalStorageDirectory().toString() + "/jp.linanfine.dsm/";
        }

        File pathDir = new File(path);
        if (!pathDir.exists()) {
            pathDir.mkdir();
        }

        InputStream is = null;
        ZipOutputStream zos = null;
        byte[] buf = new byte[1024];

        //Log.e("DSM", path+fileName);

        try {
            zos = new ZipOutputStream(new FileOutputStream(path + fileName));
        } catch (FileNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(R.string.global_setting_importfailed), Toast.LENGTH_LONG).show();
            return;
        }

        do {
            ArrayList<RivalData> rivals = readRivals(context);

            ZipEntry ze = new ZipEntry("RivalList.txt");
            try {
                zos.putNextEntry(ze);
            } catch (IOException e1) {
                continue;
            }

            for (RivalData r : rivals) {
                try {
                    zos.write(r.Id.getBytes());
                    zos.write(",".getBytes());
                    zos.write(r.Name.getBytes());
                    zos.write("\n".getBytes());
                } catch (IOException e) {
                    continue;
                }
            }

            try {
                zos.closeEntry();
            } catch (IOException e) {
                continue;
            }

        } while (false);

        for (File item : context.getFilesDir().listFiles()) {
            if (!item.isFile()) {
                continue;
            }

            String entryName = item.toString();
            entryName = entryName.substring(entryName.lastIndexOf('/') + 1);

            if (!entryName.startsWith("ScoreData")) {
                continue;
            }

            try {
                is = new FileInputStream(item);
            } catch (FileNotFoundException e) {
                continue;
            }

            ZipEntry ze = new ZipEntry(entryName);
            try {
                zos.putNextEntry(ze);
            } catch (IOException e1) {
                continue;
            }

            int len = 0;
            try {
                while ((len = is.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
            } catch (IOException e1) {
            }

            try {
                is.close();
            } catch (IOException e) {
                continue;
            }

            try {
                zos.closeEntry();
            } catch (IOException e) {
                continue;
            }

        }

        try {
            zos.close();
        } catch (IOException e) {
            return;
        }

        Toast.makeText(context, context.getResources().getString(R.string.global_setting_exportedA) + path + fileName + context.getResources().getString(R.string.global_setting_exportedB), Toast.LENGTH_LONG).show();

    }

    @SuppressWarnings("resource")
    public static void restoreScores(Context context) {
        String fileName = "ddr_score_manager_score_backup.zds";
        String path;

        {
            path = Environment.getExternalStorageDirectory().toString() + "/jp.linanfine.dsm/";
        }

        ZipInputStream zin = null;
        BufferedOutputStream out = null;
        ZipEntry zipEntry = null;

        try {
            zin = new ZipInputStream(new FileInputStream(path + fileName));
        } catch (FileNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(R.string.global_setting_importfailed), Toast.LENGTH_LONG).show();
            return;
        }

        while (true) {

            try {
                if ((zipEntry = zin.getNextEntry()) == null) {
                    break;
                }
            } catch (IOException e1) {
                break;
            }

            File newfile = new File(zipEntry.getName());

            //Log.e("DSM", newfile.getName());
            if (newfile.getName().startsWith("ScoreData")) {

                // 出力用ファイルストリームの生成
                try {
                    out = new BufferedOutputStream(new FileOutputStream(context.getFilesDir().toString() + "/" + newfile.getName()));
                } catch (FileNotFoundException e) {
                    continue;
                }

                // エントリの内容を出力
                int len = 0;
                byte[] buffer = new byte[1024];
                try {
                    while ((len = zin.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    continue;
                }

                try {
                    out.close();
                } catch (IOException e) {
                    continue;
                }

                out = null;

            } else if (newfile.getName().startsWith("RivalList")) {
                ArrayList<RivalData> rivals = readRivals(context);
                BufferedReader br;
                br = new BufferedReader(new InputStreamReader(zin));
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        String[] sp = line.split(",");
                        RivalData r = new RivalData();
                        r.Id = sp[0];
                        r.Name = sp[1];
                        rivals.add(r);
                    }
                } catch (IOException e1) {
                    continue;
                }
                saveRivals(context, rivals, true);
                //try {
                //	br.close();
                //} catch (IOException e) { continue; }
            }

            try {
                zin.closeEntry();
            } catch (IOException e) {
                continue;
            }

        }

        try {
            zin.close();
        } catch (IOException e) {
            return;
        }

        Toast.makeText(context, context.getResources().getString(R.string.global_setting_importedA) + path + fileName + context.getResources().getString(R.string.global_setting_importedB), Toast.LENGTH_LONG).show();

    }

    public static StatusData readStatusData(Context context) {
        StatusData ret = new StatusData();
        SharedPreferences pref = context.getSharedPreferences("StatusData", Context.MODE_PRIVATE);
        ret.DancerName = pref.getString("DancerName", "");
        ret.DdrCode = pref.getString("DdrCode", "");
        ret.Todofuken = pref.getString("Todofuken", "");
        ret.EnjoyLevel = pref.getInt("EnjoyLevel", 0);
        ret.EnjoyLevelNextExp = pref.getInt("EnjoyLevelNextExp", 0);
        ret.PlayCount = pref.getInt("PlayCount", 0);
        ret.LastPlay = pref.getString("LastPlay", "");
        ret.PlayCountSingle = pref.getInt("SinglePlayCount", 0);
        ret.LastPlaySingle = pref.getString("SingleLastPlay", "");
        ret.PlayCountDouble = pref.getInt("DoublePlayCount", 0);
        ret.LastPlayDouble = pref.getString("DoubleLastPlay", "");
        return ret;
    }

    public static void saveStatusData(Context context, StatusData status) {
        SharedPreferences pref = context.getSharedPreferences("StatusData", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("DancerName", status.DancerName);
        e.putString("DdrCode", status.DdrCode);
        e.putString("Todofuken", status.Todofuken);
        e.putInt("EnjoyLevel", status.EnjoyLevel);
        e.putInt("EnjoyLevelNextExp", status.EnjoyLevelNextExp);
        e.putInt("PlayCount", status.PlayCount);
        e.putString("LastPlay", status.LastPlay);
        e.putInt("SinglePlayCount", status.PlayCountSingle);
        e.putString("SingleLastPlay", status.LastPlaySingle);
        e.putInt("DoublePlayCount", status.PlayCountDouble);
        e.putString("DoubleLastPlay", status.LastPlayDouble);
        e.apply();
    }

    public static GestureAction getGestureAction(int typenum) {
        return
                typenum == 0 ? GestureAction.ShowItemMenu :
                        typenum == 1 ? GestureAction.OpenOwnMusic :
                                typenum == 2 ? GestureAction.FromGate :
                                        typenum == 3 ? GestureAction.DirectEdit :
                                                typenum == 4 ? GestureAction.RivalFromGate :
                                                        typenum == 5 ? GestureAction.RivalDirectEdit :
                                                                typenum == 6 ? GestureAction.SelectRivalAction :
                                                                        typenum == 7 ? GestureAction.AddToMyList :
                                                                                typenum == 8 ? GestureAction.RemoveFromMyList :
                                                                                        GestureAction.None;
    }

    public static GestureSettings readGestureSettings(Context context) {
        GestureSettings ret = new GestureSettings();
        SharedPreferences pref = context.getSharedPreferences("GestureSetting", Context.MODE_PRIVATE);
        ret.GestureEnabled = pref.getBoolean("GestureEnabled", true);
        ret.OnItemClicked = getGestureAction(pref.getInt("OnItemClicked", 0));
        ret.OnItemLongClicked = getGestureAction(pref.getInt("OnItemLongClicked", 1));
        return ret;
    }

    public static int getGestureActionTypeNum(GestureAction action) {
        return
                action == GestureAction.ShowItemMenu ? 0 :
                        action == GestureAction.OpenOwnMusic ? 1 :
                                action == GestureAction.FromGate ? 2 :
                                        action == GestureAction.DirectEdit ? 3 :
                                                action == GestureAction.RivalFromGate ? 4 :
                                                        action == GestureAction.RivalDirectEdit ? 5 :
                                                                action == GestureAction.SelectRivalAction ? 6 :
                                                                        action == GestureAction.AddToMyList ? 7 :
                                                                                action == GestureAction.RemoveFromMyList ? 8 :
                                                                                        -1;
    }

    public static void saveGestureSettings(Context context, GestureSettings gestures) {
        SharedPreferences pref = context.getSharedPreferences("GestureSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putBoolean("GestureEnabled", gestures.GestureEnabled);
        e.putInt("OnItemClicked", getGestureActionTypeNum(gestures.OnItemClicked));
        e.putInt("OnItemLongClicked", getGestureActionTypeNum(gestures.OnItemLongClicked));
        e.commit();
    }

    public static void saveActiveRival(Context context, int rivalNo) {
        SharedPreferences pref = context.getSharedPreferences("Rivals", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt("Active", rivalNo);
        e.commit();
    }

    public static int readActiveRival(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Rivals", Context.MODE_PRIVATE);
        return pref.getInt("Active", -1);
    }

    public static void saveRivals(Context context, ArrayList<RivalData> rivals, boolean noNameChangeMessage) {
        {
            ArrayList<RivalData> sr = new ArrayList<RivalData>();
            ArrayList<String> keys = new ArrayList<String>();
            for (RivalData r : rivals) {
                if (keys.contains(r.Id)) {
                    int index = keys.indexOf(r.Id);
                    if (!noNameChangeMessage) {
                        String nameA = sr.get(index).Name;
                        String nameB = r.Name;
                        if (nameA != null && !nameA.equals(nameB)) {
                            Toast.makeText(context, context.getResources().getString(R.string.edit_rival_modified) + "\n" + nameA + " -> " + nameB, Toast.LENGTH_LONG).show();
                        }
                    }
                    sr.set(index, r);
                } else {
                    keys.add(r.Id);
                    sr.add(r);
                }
            }
            rivals = sr;
        }
        SharedPreferences pref = context.getSharedPreferences("Rivals", Context.MODE_PRIVATE);
        int active = readActiveRival(context);
        Editor e = pref.edit();
        e.clear();
        int count = rivals.size();
        e.putInt("RivalCount", count);
        for (int i = 0; i < count; ++i) {
            e.putString("RivalId" + String.valueOf(i), rivals.get(i).Id);
            e.putString("RivalName" + String.valueOf(i), rivals.get(i).Name);
        }
        e.commit();
        if (count <= active) {
            saveActiveRival(context, count - 1);
        }
        String[] fileList = context.fileList();
        for (String file : fileList) {
            if (!"ScoreData.txt".equals(file) && file.startsWith("ScoreData")) {
                String id = file.substring(9, 17);
                boolean exists = false;
                for (RivalData r : rivals) {
                    if (id.equals(r.Id)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    context.deleteFile(file);
                }
            }
        }
    }

    public static ArrayList<RivalData> readRivals(Context context) {
        ArrayList<RivalData> ret = new ArrayList<RivalData>();
        SharedPreferences pref = context.getSharedPreferences("Rivals", Context.MODE_PRIVATE);
        int count = pref.getInt("RivalCount", 0);
        for (int i = 0; i < count; ++i) {
            RivalData r = new RivalData();
            r.Id = pref.getString("RivalId" + String.valueOf(i), null);
            r.Name = pref.getString("RivalName" + String.valueOf(i), null);
            ret.add(r);
        }
        return ret;
    }

    public static void clearAppearanceSettings(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Appearances", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.clear();
        e.commit();
    }

    public static void saveAppearanceSettings(Context context, AppearanceSettingsSp appearances) {
        SharedPreferences pref = context.getSharedPreferences("Appearances", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putBoolean("ShowMaxCombo", appearances.ShowMaxCombo);
        e.putBoolean("ShowScore", appearances.ShowScore);
        e.putBoolean("ShowDanceLevel", appearances.ShowDanceLevel);
        e.putBoolean("ShowPlayCount", appearances.ShowPlayCount);
        e.putBoolean("ShowClearCount", appearances.ShowClearCount);
        e.putBoolean("ShowComments", appearances.ShowComments);
        e.putBoolean("ShowFullScreen", appearances.ShowFullScreen);
        e.putBoolean("ShowTitleBar", appearances.ShowTitleBar);
        e.putBoolean("ShowFlareRank", appearances.ShowFlareRank);
        e.putBoolean("ShowFlareSkill", appearances.ShowFlareSkill);
        e.putInt("CategoryTopFontSize", (int) appearances.CategoryTopFontSize);
        e.putInt("CategorySubItemFontSize", (int) appearances.CategorySubItemFontSize);
        e.putInt("ItemMusicLevelFontSize", (int) appearances.ItemMusicLevelFontSize);
        e.putInt("ItemMusicNameFontSize", (int) appearances.ItemMusicNameFontSize);
        e.putInt("ItemMusicRankFontSize", (int) appearances.ItemMusicRankFontSize);
        e.putInt("ItemMusicScoreFontSize", (int) appearances.ItemMusicScoreFontSize);
        e.putFloat("ItemMusicLevelFontSizeX", appearances.ItemMusicLevelFontSizeX);
        e.putFloat("ItemMusicNameFontSizeX", appearances.ItemMusicNameFontSizeX);
        e.putFloat("ItemMusicRankFontSizeX", appearances.ItemMusicRankFontSizeX);
        e.putFloat("ItemMusicScoreFontSizeX", appearances.ItemMusicScoreFontSizeX);
        e.apply();
    }

    public static AppearanceSettingsSp readAppearanceSettings(Context context) {
        AppearanceSettingsSp ret = new AppearanceSettingsSp();
        SharedPreferences pref = context.getSharedPreferences("Appearances", Context.MODE_PRIVATE);
        ret.ShowMaxCombo = pref.getBoolean("ShowMaxCombo", false);
        ret.ShowScore = pref.getBoolean("ShowScore", true);
        ret.ShowDanceLevel = pref.getBoolean("ShowDanceLevel", true);
        ret.ShowPlayCount = pref.getBoolean("ShowPlayCount", false);
        ret.ShowClearCount = pref.getBoolean("ShowClearCount", false);
        ret.ShowComments = pref.getBoolean("ShowComments", true);
        ret.ShowFullScreen = pref.getBoolean("ShowFullScreen", false);
        ret.ShowTitleBar = pref.getBoolean("ShowTitleBar", true);
        ret.ShowFlareRank = pref.getBoolean("ShowFlareRank", true);
        ret.ShowFlareSkill = pref.getBoolean("ShowFlareSkill", true);
        ret.CategoryTopFontSize = pref.getInt("CategoryTopFontSize", 35);
        ret.CategorySubItemFontSize = pref.getInt("CategorySubItemFontSize", 25);
        ret.ItemMusicLevelFontSize = pref.getInt("ItemMusicLevelFontSize", 23);
        ret.ItemMusicNameFontSize = pref.getInt("ItemMusicNameFontSize", 23);
        ret.ItemMusicRankFontSize = pref.getInt("ItemMusicRankFontSize", 25);
        ret.ItemMusicScoreFontSize = pref.getInt("ItemMusicScoreFontSize", 16);
        ret.ItemMusicLevelFontSizeX = pref.getFloat("ItemMusicLevelFontSizeX", 1.0f);
        ret.ItemMusicNameFontSizeX = pref.getFloat("ItemMusicNameFontSizeX", 1.0f);
        ret.ItemMusicRankFontSizeX = pref.getFloat("ItemMusicRankFontSizeX", 1.0f);
        ret.ItemMusicScoreFontSizeX = pref.getFloat("ItemMusicScoreFontSizeX", 1.0f);
        return ret;
    }

    public static void saveRecentList(Context context, List<RecentData> recent) {
        FileOutputStream out = null;
        try {
            String fileName = "Recent.txt";
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            int count = recent.size();
            for (int i = 0; i < count; ++i) {
                out.write((recent.get(i).Id + "\t" + recent.get(i).PatternType_.toString() + "\n").getBytes());
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(out);
        }
    }

    public static ArrayList<RecentData> readRecentList(Context context) {
        ArrayList<RecentData> ret = new ArrayList<RecentData>();
        FileInputStream in = null;
        try {
            String fileName = "Recent.txt";
            in = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;
            while ((str = reader.readLine()) != null) {
                //Log.d("", str);
                String[] sp = str.split("\t");
                RecentData data = new RecentData();
                data.Id = Integer.valueOf(sp[0]);
                data.PatternType_ = PatternType.valueOf(sp[1]);
                ret.add(data);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
        }
        return ret;
    }

    public static void saveDdrSaAuthentication(Context context, String ddrCode, String encryptedPassword) {
        SharedPreferences pref = context.getSharedPreferences("DdrSaAuthentication", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("DdrCode", ddrCode);
        e.putString("EncryptedPassword", encryptedPassword);
        e.commit();
    }

    public static String readDdrSaAuthenticationDdrCode(Context context) {
        SharedPreferences pref = context.getSharedPreferences("DdrSaAuthentication", Context.MODE_PRIVATE);
        return pref.getString("DdrCode", "");
    }

    public static String readDdrSaAuthenticationEncryptedPassword(Context context) {
        SharedPreferences pref = context.getSharedPreferences("DdrSaAuthentication", Context.MODE_PRIVATE);
        return pref.getString("EncryptedPassword", "");
    }

    public static void saveAutoUpdateMusicList(Context context, boolean isEnabled) {
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putBoolean("AutoUpdateMusicList", isEnabled);
        e.commit();
    }

    public static boolean readAutoUpdateMusicList(Context context) {
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        return pref.getBoolean("AutoUpdateMusicList", true);
    }

    public static void saveUseOldStyleDraw(Context context, boolean isEnabled) {
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putBoolean("UseOldStyleDraw", isEnabled);
        e.commit();
    }

    public static boolean readUseOldStyleDraw(Context context) {
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        return pref.getBoolean("UseOldStyleDraw", false);
    }

    public static void saveUseAsyncDraw(Context context, boolean isEnabled) {
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putBoolean("UseAsyncDraw", isEnabled);
        e.commit();
    }

    public static boolean readUseAsyncDraw(Context context) {
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        return pref.getBoolean("UseAsyncDraw", false);
    }

    public static void saveCloseCategoryOnBackKeyPressed(Context context, boolean isEnabled) {
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putBoolean("CloseCategoryOnBackKeyPressed", isEnabled);
        e.commit();
    }

    public static boolean readCloseCategoryOnBackKeyPressed(Context context) {
        SharedPreferences pref = context.getSharedPreferences("GeneralSetting", Context.MODE_PRIVATE);
        return pref.getBoolean("CloseCategoryOnBackKeyPressed", false);
    }

    public static void saveGateSetting(Context context, GateSetting setting) {
        SharedPreferences pref = context.getSharedPreferences("GateSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putBoolean("FromNewSite", setting.FromNewSite);
        e.putBoolean("OWL4", setting.OverWriteLife4);
        e.putBoolean("OWFC", setting.OverWriteFullCombo);
        e.putBoolean("OWLS", setting.OverWriteLowerScores);
        e.putInt("SetPFC", setting.SetPfcScore);
        e.putString("FromSite", setting.FromSite.toString());
        e.apply();
    }

    public static GateSetting readGateSetting(Context context) {
        GateSetting ret = new GateSetting();
        SharedPreferences pref = context.getSharedPreferences("GateSetting", Context.MODE_PRIVATE);
        ret.FromNewSite = pref.getBoolean("FromNewSite", true);
        ret.OverWriteLife4 = pref.getBoolean("OWL4", false);
        ret.OverWriteFullCombo = pref.getBoolean("OWFC", false);
        ret.OverWriteLowerScores = pref.getBoolean("OWLS", true);
        ret.SetPfcScore = pref.getInt("SetPFC", 999990);

        String defaultSite = ret.FromNewSite ? "WORLD" : "A3";
        String fromSite = pref.getString("FromSite", defaultSite);
        ret.FromSite = GateSetting.SiteVersion.fromString(fromSite);
        return ret;
    }

    public static int readMyListCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences("MyListSetting", Context.MODE_PRIVATE);
        return pref.getInt("MyListCount", 0);
    }

    public static void saveMyListCount(Context context, int myListCount) {
        SharedPreferences pref = context.getSharedPreferences("MyListSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt("MyListCount", myListCount);
        e.commit();
    }

    public static void saveMyListName(Context context, int id, String name) {
        SharedPreferences pref = context.getSharedPreferences("MyListSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("MyListName" + String.valueOf(id), name);
        e.commit();
    }

    public static String readMyListName(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences("MyListSetting", Context.MODE_PRIVATE);
        return pref.getString("MyListName" + String.valueOf(id), "MyList" + String.valueOf(id));
    }

    private static PatternType getPatternType(int patnum) {
        return
                (patnum == 0) ? PatternType.bSP :
                        (patnum == 1) ? PatternType.BSP :
                                (patnum == 2) ? PatternType.DSP :
                                        (patnum == 3) ? PatternType.ESP :
                                                (patnum == 4) ? PatternType.CSP :
                                                        (patnum == 5) ? PatternType.BDP :
                                                                (patnum == 6) ? PatternType.DDP :
                                                                        (patnum == 7) ? PatternType.EDP :
                                                                                (patnum == 8) ? PatternType.CDP :
                                                                                        PatternType.bSP;
    }

    public static ArrayList<UniquePattern> readMyList(Context context, int id) {
        ArrayList<UniquePattern> ret = new ArrayList<UniquePattern>();
        SharedPreferences pref = context.getSharedPreferences("MyList" + String.valueOf(id), Context.MODE_PRIVATE);
        int count = pref.getInt("PatternCount", 0);
        //Log.d("ReadCount", String.valueOf(count));
        for (int i = 0; i < count; i++) {
            int musicId = pref.getInt("Id" + String.valueOf(i), 0);
            int patnum = pref.getInt("Pattern" + String.valueOf(i), 0);
            PatternType patternType = getPatternType(patnum);
            UniquePattern pat = new UniquePattern();
            pat.MusicId = musicId;
            pat.Pattern = patternType;
            ret.add(pat);
        }
        return ret;
    }

    private static int getPatternTypeNum(PatternType type) {
        return
                type == PatternType.bSP ? 0 :
                        type == PatternType.BSP ? 1 :
                                type == PatternType.DSP ? 2 :
                                        type == PatternType.ESP ? 3 :
                                                type == PatternType.CSP ? 4 :
                                                        type == PatternType.BDP ? 5 :
                                                                type == PatternType.DDP ? 6 :
                                                                        type == PatternType.EDP ? 7 :
                                                                                type == PatternType.CDP ? 8 :
                                                                                        0;
    }

    public static void saveMyList(Context context, int id, ArrayList<UniquePattern> list) {
        SharedPreferences pref = context.getSharedPreferences("MyList" + String.valueOf(id), Context.MODE_PRIVATE);
        Editor e = pref.edit();
        int count = list.size();
        //Log.d("SaveCount", String.valueOf(count));
        e.putInt("PatternCount", count);
        for (int i = 0; i < count; i++) {
            UniquePattern up = list.get(i);
            e.putInt("Id" + String.valueOf(i), up.MusicId);
            int patnum = getPatternTypeNum(up.Pattern);
            e.putInt("Pattern" + String.valueOf(i), patnum);
        }
        e.commit();
    }

    public static void deleteMyList(Context context, int id) {
        int count = readMyListCount(context);
        for (int i = id + 1; i < count; i++) {
            String myListName = readMyListName(context, i);
            ArrayList<UniquePattern> myList = readMyList(context, i);
            saveMyListName(context, i - 1, myListName);
            saveMyList(context, i - 1, myList);
        }
        saveMyListName(context, count - 1, "");
        saveMyList(context, count - 1, new ArrayList<UniquePattern>());
        saveMyListCount(context, readMyListCount(context) - 1);
    }

    @SuppressWarnings("unused")
    private static void saveActivePager(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt("ActivePager", id);
        e.commit();
    }

    @SuppressWarnings("unused")
    private static int readActivePager(Context context) {
        //SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_PRIVATE);
        return pref.getInt("ActivePager", 0);
    }

    private static void savePagerCount(Context context, int pagerCount) {
        SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt("PagerCount", pagerCount);
        e.commit();
    }

    private static int readPagerCount(Context context) {
        //SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_PRIVATE);
        return pref.getInt("PagerCount", 1);
    }

    @SuppressWarnings("unused")
    private static void deletePager(Context context, int id) {
        int count = readPagerCount(context);
        for (int i = id + 1; i < count; i++) {
            String pagerName = readPagerName(context, i);
            MusicFilter filter = readMusicFilter(context, i);
            MusicSort sort = readMusicSort(context, i);
            savePagerName(context, i - 1, pagerName);
            saveMusicFilter(context, i - 1, filter);
            saveMusicSort(context, i - 1, sort);
        }
        savePagerName(context, count - 1, "");
        saveMusicFilter(context, count - 1, new MusicFilter());
        saveMusicSort(context, count - 1, new MusicSort());
        savePagerCount(context, readPagerCount(context) - 1);
    }

    private static void savePagerName(Context context, int id, String name) {
        SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("PagerName" + String.valueOf(id), name);
        e.commit();
    }

    private static String readPagerName(Context context, int id) {
        //SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        SharedPreferences pref = context.getSharedPreferences("PagerSetting", Context.MODE_PRIVATE);
        return pref.getString("PagerName" + String.valueOf(id), "Filter" + String.valueOf(id));
    }

    public static void saveActiveMusicSort(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences("MusicSortSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt("ActiveMusicSort", id);
        e.commit();
    }

    public static int readActiveMusicSort(Context context) {
        SharedPreferences pref = context.getSharedPreferences("MusicSortSetting", Context.MODE_PRIVATE);
        return pref.getInt("ActiveMusicSort", 0);
    }

    public static void saveMusicSortCount(Context context, int pagerCount) {
        SharedPreferences pref = context.getSharedPreferences("MusicSortSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt("MusicSortCount", pagerCount);
        e.commit();
    }

    public static int readMusicSortCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences("MusicSortSetting", Context.MODE_PRIVATE);
        int count = pref.getInt("MusicSortCount", 0);
        if (count == 0) {
            saveMusicSortCount(context, 1);
            saveMusicSortName(context, 0, "Default");
            count = 1;
        }
        return count;
    }

    public static void deleteMusicSort(Context context, int id) {
        int count = readMusicSortCount(context);
        for (int i = id + 1; i < count; i++) {
            String pagerName = readMusicSortName(context, i);
            MusicSort sort = readMusicSort(context, i);
            saveMusicSortName(context, i - 1, pagerName);
            saveMusicSort(context, i - 1, sort);
        }
        saveMusicSortName(context, count - 1, "");
        saveMusicSort(context, count - 1, new MusicSort());
        saveMusicSortCount(context, count - 1);
    }

    public static void saveMusicSortName(Context context, int id, String name) {
        SharedPreferences pref = context.getSharedPreferences("MusicSortSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("MusicSortName" + String.valueOf(id), name);
        e.commit();
    }

    public static String readMusicSortName(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences("MusicSortSetting", Context.MODE_PRIVATE);
        return pref.getString("MusicSortName" + String.valueOf(id), "Sort" + String.valueOf(id));
    }

    private static int getMusicSortTypeNum(MusicSortType sorttype) {
        return
                sorttype == MusicSortType.MusicName ? 0 :
                        sorttype == MusicSortType.Score ? 1 :
                                sorttype == MusicSortType.Rank ? 2 :
                                        sorttype == MusicSortType.FullComboType ? 3 :
                                                sorttype == MusicSortType.Difficulty ? 4 :
                                                        sorttype == MusicSortType.Pattern ? 5 :
                                                                sorttype == MusicSortType.SPDP ? 6 :
                                                                        sorttype == MusicSortType.ID ? 7 :
                                                                                sorttype == MusicSortType.RivalScore ? 8 :
                                                                                        sorttype == MusicSortType.RivalRank ? 9 :
                                                                                                sorttype == MusicSortType.RivalFullComboType ? 10 :
                                                                                                        sorttype == MusicSortType.RivalScoreDifference ? 11 :
                                                                                                                sorttype == MusicSortType.RivalScoreDifferenceAbs ? 12 :
                                                                                                                        sorttype == MusicSortType.BpmMax ? 13 :
                                                                                                                                sorttype == MusicSortType.BpmMin ? 14 :
                                                                                                                                        sorttype == MusicSortType.BpmAve ? 15 :
                                                                                                                                                sorttype == MusicSortType.SeriesTitle ? 16 :
                                                                                                                                                        sorttype == MusicSortType.ComboCount ? 17 :
                                                                                                                                                                sorttype == MusicSortType.PlayCount ? 18 :
                                                                                                                                                                        sorttype == MusicSortType.ClearCount ? 19 :
                                                                                                                                                                                sorttype == MusicSortType.RivalComboCount ? 20 :
                                                                                                                                                                                        sorttype == MusicSortType.RivalPlayCount ? 21 :
                                                                                                                                                                                                sorttype == MusicSortType.RivalClearCount ? 22 :
                                                                                                                                                                                                        sorttype == MusicSortType.FlareRank ? 23 :
                                                                                                                                                                                                                sorttype == MusicSortType.FlareSkill ? 24 :
                                                                                                                                                                                                                        0;
    }

    public static void saveMusicSort(Context context, int id, MusicSort sort) {
        //pref = getSharedPreferences("MusicSort"+String.valueOf(mPagerId), MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        SharedPreferences pref = context.getSharedPreferences("MusicSort" + String.valueOf(id), Context.MODE_PRIVATE);
        Editor e = pref.edit();
        int typenum;
        typenum = getMusicSortTypeNum(sort._1stType);
        e.putInt("1stType", typenum);
        e.putBoolean("1stOrder", sort._1stOrder == SortOrder.Ascending);
        typenum = getMusicSortTypeNum(sort._2ndType);
        e.putInt("2ndType", typenum);
        e.putBoolean("2ndOrder", sort._2ndOrder == SortOrder.Ascending);
        typenum = getMusicSortTypeNum(sort._3rdType);
        e.putInt("3rdType", typenum);
        e.putBoolean("3rdOrder", sort._3rdOrder == SortOrder.Ascending);
        typenum = getMusicSortTypeNum(sort._4thType);
        e.putInt("4thType", typenum);
        e.putBoolean("4thOrder", sort._4thOrder == SortOrder.Ascending);
        typenum = getMusicSortTypeNum(sort._5thType);
        e.putInt("5thType", typenum);
        e.putBoolean("5thOrder", sort._5thOrder == SortOrder.Ascending);
        e.commit();

    }

    public static void saveActiveMusicFilter(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences("MusicFilterSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt("ActiveMusicFilter", id);
        e.commit();
    }

    public static int readActiveMusicFilter(Context context) {
        SharedPreferences pref = context.getSharedPreferences("MusicFilterSetting", Context.MODE_PRIVATE);
        return pref.getInt("ActiveMusicFilter", 0);
    }

    public static void saveMusicFilterCount(Context context, int pagerCount) {
        SharedPreferences pref = context.getSharedPreferences("MusicFilterSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putInt("MusicFilterCount", pagerCount);
        e.commit();
    }

    public static int readMusicFilterCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences("MusicFilterSetting", Context.MODE_PRIVATE);
        int count = pref.getInt("MusicFilterCount", 0);
        if (count == 0) {
            saveMusicFilterCount(context, 1);
            saveMusicFilterName(context, 0, "Default");
            count = 1;
        }
        return count;
    }

    public static void deleteMusicFilter(Context context, int id) {
        int count = readMusicFilterCount(context);
        for (int i = id + 1; i < count; i++) {
            String pagerName = readMusicFilterName(context, i);
            MusicFilter filter = readMusicFilter(context, i);
            saveMusicFilterName(context, i - 1, pagerName);
            saveMusicFilter(context, i - 1, filter);
        }
        saveMusicFilterName(context, count - 1, "");
        saveMusicFilter(context, count - 1, new MusicFilter());
        saveMusicFilterCount(context, count - 1);
    }

    public static void saveMusicFilterName(Context context, int id, String name) {
        SharedPreferences pref = context.getSharedPreferences("MusicFilterSetting", Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("MusicFilterName" + String.valueOf(id), name);
        e.commit();
    }

    public static String readMusicFilterName(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences("MusicFilterSetting", Context.MODE_PRIVATE);
        return pref.getString("MusicFilterName" + String.valueOf(id), "Filter" + String.valueOf(id));
    }

    public static void saveMusicFilter(Context context, int id, MusicFilter filter) {
        //pref = getSharedPreferences("MusicFilter"+String.valueOf(mPagerId), MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        SharedPreferences pref = context.getSharedPreferences("MusicFilter" + String.valueOf(id), Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putBoolean("bSP", filter.bSP);
        e.putBoolean("BSP", filter.BSP);
        e.putBoolean("DSP", filter.DSP);
        e.putBoolean("ESP", filter.ESP);
        e.putBoolean("CSP", filter.CSP);
        e.putBoolean("BDP", filter.BDP);
        e.putBoolean("DDP", filter.DDP);
        e.putBoolean("EDP", filter.EDP);
        e.putBoolean("CDP", filter.CDP);
        e.putBoolean("AllowOnlyChallenge", filter.AllowOnlyChallenge);
        e.putBoolean("AllowExpertWhenNoChallenge", filter.AllowExpertWhenNoChallenge);
        e.putInt("ShockArrowSP", filter.ShockArrowSP == ShockArrowInclude.Only ? 1 : filter.ShockArrowSP == ShockArrowInclude.Include ? 2 : 3);
        e.putInt("ShockArrowDP", filter.ShockArrowDP == ShockArrowInclude.Only ? 1 : filter.ShockArrowDP == ShockArrowInclude.Include ? 2 : 3);
        e.putBoolean("Dif1", filter.Dif1);
        e.putBoolean("Dif2", filter.Dif2);
        e.putBoolean("Dif3", filter.Dif3);
        e.putBoolean("Dif4", filter.Dif4);
        e.putBoolean("Dif5", filter.Dif5);
        e.putBoolean("Dif6", filter.Dif6);
        e.putBoolean("Dif7", filter.Dif7);
        e.putBoolean("Dif8", filter.Dif8);
        e.putBoolean("Dif9", filter.Dif9);
        e.putBoolean("Dif10", filter.Dif10);
        e.putBoolean("Dif11", filter.Dif11);
        e.putBoolean("Dif12", filter.Dif12);
        e.putBoolean("Dif13", filter.Dif13);
        e.putBoolean("Dif14", filter.Dif14);
        e.putBoolean("Dif15", filter.Dif15);
        e.putBoolean("Dif16", filter.Dif16);
        e.putBoolean("Dif17", filter.Dif17);
        e.putBoolean("Dif18", filter.Dif18);
        e.putBoolean("Dif19", filter.Dif19);
        e.putBoolean("RankAAA", filter.RankAAA);
        e.putBoolean("RankAA+", filter.RankAAp);
        e.putBoolean("RankAA", filter.RankAA);
        e.putBoolean("RankAA-", filter.RankAAm);
        e.putBoolean("RankA+", filter.RankAp);
        e.putBoolean("RankA", filter.RankA);
        e.putBoolean("RankA-", filter.RankAm);
        e.putBoolean("RankB+", filter.RankBp);
        e.putBoolean("RankB", filter.RankB);
        e.putBoolean("RankB-", filter.RankBm);
        e.putBoolean("RankC+", filter.RankCp);
        e.putBoolean("RankC", filter.RankC);
        e.putBoolean("RankC-", filter.RankCm);
        e.putBoolean("RankD+", filter.RankDp);
        e.putBoolean("RankD", filter.RankD);
        e.putBoolean("RankE", filter.RankE);
        e.putBoolean("RankNoPlay", filter.RankNoPlay);
        e.putBoolean("FcMFC", filter.FcMFC);
        e.putBoolean("FcPFC", filter.FcPFC);
        e.putBoolean("FcFC", filter.FcGFC);
        e.putBoolean("FcGFC", filter.FcFC);
        e.putBoolean("FcLife4", filter.FcLife4);
        e.putBoolean("FcNoFC", filter.FcNoFC);

        e.putBoolean("FlareRankEX", filter.FlareRankEX);
        e.putBoolean("FlareRankIX", filter.FlareRankIX);
        e.putBoolean("FlareRankVIII", filter.FlareRankVIII);
        e.putBoolean("FlareRankVII", filter.FlareRankVII);
        e.putBoolean("FlareRankVI", filter.FlareRankVI);
        e.putBoolean("FlareRankV", filter.FlareRankV);
        e.putBoolean("FlareRankIV", filter.FlareRankIV);
        e.putBoolean("FlareRankIII", filter.FlareRankIII);
        e.putBoolean("FlareRankII", filter.FlareRankII);
        e.putBoolean("FlareRankI", filter.FlareRankI);
        e.putBoolean("FlareRank0", filter.FlareRank0);
        e.putBoolean("FlareRankNoRank", filter.FlareRankNoRank);

        e.putBoolean("RankAAArival", filter.RankAAArival);
        e.putBoolean("RankAA+rival", filter.RankAAprival);
        e.putBoolean("RankAArival", filter.RankAArival);
        e.putBoolean("RankAA-rival", filter.RankAAmrival);
        e.putBoolean("RankA+rival", filter.RankAprival);
        e.putBoolean("RankArival", filter.RankArival);
        e.putBoolean("RankA-rival", filter.RankAmrival);
        e.putBoolean("RankB+rival", filter.RankBprival);
        e.putBoolean("RankBrival", filter.RankBrival);
        e.putBoolean("RankB-rival", filter.RankBmrival);
        e.putBoolean("RankC+rival", filter.RankCprival);
        e.putBoolean("RankCrival", filter.RankCrival);
        e.putBoolean("RankC-rival", filter.RankCmrival);
        e.putBoolean("RankD+rival", filter.RankDprival);
        e.putBoolean("RankDrival", filter.RankDrival);
        e.putBoolean("RankErival", filter.RankErival);
        e.putBoolean("RankNoPlayrival", filter.RankNoPlayrival);
        e.putBoolean("FcMFCrival", filter.FcMFCrival);
        e.putBoolean("FcPFCrival", filter.FcPFCrival);
        e.putBoolean("FcFCrival", filter.FcFCrival);
        e.putBoolean("FcGFCrival", filter.FcGFCrival);
        e.putBoolean("FcLife4rival", filter.FcLife4rival);
        e.putBoolean("FcNoFCrival", filter.FcNoFCrival);
        e.putBoolean("RivalWin", filter.RivalWin);
        e.putBoolean("RivalLose", filter.RivalLose);
        e.putBoolean("RivalDraw", filter.RivalDraw);
        e.putInt("ScoreMin", filter.ScoreMin);
        e.putInt("ScoreMax", filter.ScoreMax);

        e.putInt("FlareSkillMin", filter.FlareSkillMin);
        e.putInt("FlareSkillMax", filter.FlareSkillMax);

        e.putInt("ScoreMinRival", filter.ScoreMinRival);
        e.putInt("ScoreMaxRival", filter.ScoreMaxRival);
        e.putInt("MaxComboMin", filter.MaxComboMin);
        e.putInt("MaxComboMax", filter.MaxComboMax);
        e.putInt("MaxComboMinRival", filter.MaxComboMinRival);
        e.putInt("MaxComboMaxRival", filter.MaxComboMaxRival);
        e.putInt("PlayCountMin", filter.PlayCountMin);
        e.putInt("PlayCountMax", filter.PlayCountMax);
        e.putInt("PlayCountMinRival", filter.PlayCountMinRival);
        e.putInt("PlayCountMaxRival", filter.PlayCountMaxRival);
        e.putInt("ClearCountMin", filter.ClearCountMin);
        e.putInt("ClearCountMax", filter.ClearCountMax);
        e.putInt("ClearCountMinRival", filter.ClearCountMinRival);
        e.putInt("ClearCountMaxRival", filter.ClearCountMaxRival);
        e.putInt("ScoreDifferenceMinusMin", filter.ScoreDifferenceMinusMin);
        e.putInt("ScoreDifferenceMinusMax", filter.ScoreDifferenceMinusMax);
        e.putInt("ScoreDifferencePlusMin", filter.ScoreDifferencePlusMin);
        e.putInt("ScoreDifferencePlusMax", filter.ScoreDifferencePlusMax);
        e.putInt("MaxComboDifferenceMinusMin", filter.MaxComboDifferenceMinusMin);
        e.putInt("MaxComboDifferenceMinusMax", filter.MaxComboDifferenceMinusMax);
        e.putInt("MaxComboDifferencePlusMin", filter.MaxComboDifferencePlusMin);
        e.putInt("MaxComboDifferencePlusMax", filter.MaxComboDifferencePlusMax);
        e.putInt("PlayCountDifferenceMinusMin", filter.PlayCountDifferenceMinusMin);
        e.putInt("PlayCountDifferenceMinusMax", filter.PlayCountDifferenceMinusMax);
        e.putInt("PlayCountDifferencePlusMin", filter.PlayCountDifferencePlusMin);
        e.putInt("PlayCountDifferencePlusMax", filter.PlayCountDifferencePlusMax);
        e.putInt("ClearCountDifferenceMinusMin", filter.ClearCountDifferenceMinusMin);
        e.putInt("ClearCountDifferenceMinusMax", filter.ClearCountDifferenceMinusMax);
        e.putInt("ClearCountDifferencePlusMin", filter.ClearCountDifferencePlusMin);
        e.putInt("ClearCountDifferencePlusMax", filter.ClearCountDifferencePlusMax);

        // すでに使っているユーザーのフィルターのチェックが勝手に変わらないよう、SerWorldのまま
        // できれば"SerWORLD"にしたかった
        e.putBoolean("SerWorld", filter.SerWorld);
        e.putBoolean("SerA3", filter.SerA3);
        e.putBoolean("SerA20PLUS", filter.SerA20PLUS);
        e.putBoolean("SerA20", filter.SerA20);
        e.putBoolean("SerA", filter.SerA);
        e.putBoolean("Ser2014", filter.Ser2014);
        e.putBoolean("Ser2013", filter.Ser2013);
        e.putBoolean("SerX3", filter.SerX3);
        e.putBoolean("SerX3vs2ndMIX", filter.SerX3vs2ndMIX);
        e.putBoolean("SerX2", filter.SerX2);
        e.putBoolean("SerX", filter.SerX);
        e.putBoolean("SerSuperNova2", filter.SerSuperNova2);
        e.putBoolean("SerSuperNova", filter.SerSuperNova);
        e.putBoolean("SerEXTREME", filter.SerEXTREME);
        e.putBoolean("SerMAX2", filter.SerMAX2);
        e.putBoolean("SerMAX", filter.SerMAX);
        e.putBoolean("Ser5th", filter.Ser5th);
        e.putBoolean("Ser4th", filter.Ser4th);
        e.putBoolean("Ser3rd", filter.Ser3rd);
        e.putBoolean("Ser2nd", filter.Ser2nd);
        e.putBoolean("Ser1st", filter.Ser1st);
        e.putBoolean("Deleted", filter.Deleted);
        e.apply();
    }

    public static MusicFilter readMusicFilter(Context context, int id) {
        MusicFilter ret = new MusicFilter();
        //SharedPreferences pref = context.getSharedPreferences("MusicFilter"+String.valueOf(id), Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        SharedPreferences pref = context.getSharedPreferences("MusicFilter" + String.valueOf(id), Context.MODE_PRIVATE);
        ret.bSP = pref.getBoolean("bSP", true);
        ret.BSP = pref.getBoolean("BSP", true);
        ret.DSP = pref.getBoolean("DSP", true);
        ret.ESP = pref.getBoolean("ESP", true);
        ret.CSP = pref.getBoolean("CSP", true);
        ret.BDP = pref.getBoolean("BDP", true);
        ret.DDP = pref.getBoolean("DDP", true);
        ret.EDP = pref.getBoolean("EDP", true);
        ret.CDP = pref.getBoolean("CDP", true);
        ret.AllowOnlyChallenge = pref.getBoolean("AllowOnlyChallenge", true);
        ret.AllowExpertWhenNoChallenge = pref.getBoolean("AllowExpertWhenNoChallenge", true);
        int shock;
        shock = pref.getInt("ShockArrowSP", 2);
        ret.ShockArrowSP = shock == 1 ? ShockArrowInclude.Only : shock == 2 ? ShockArrowInclude.Include : ShockArrowInclude.Exclude;
        shock = pref.getInt("ShockArrowDP", 2);
        ret.ShockArrowDP = shock == 1 ? ShockArrowInclude.Only : shock == 2 ? ShockArrowInclude.Include : ShockArrowInclude.Exclude;
        ret.Dif1 = pref.getBoolean("Dif1", true);
        ret.Dif2 = pref.getBoolean("Dif2", true);
        ret.Dif3 = pref.getBoolean("Dif3", true);
        ret.Dif4 = pref.getBoolean("Dif4", true);
        ret.Dif5 = pref.getBoolean("Dif5", true);
        ret.Dif6 = pref.getBoolean("Dif6", true);
        ret.Dif7 = pref.getBoolean("Dif7", true);
        ret.Dif8 = pref.getBoolean("Dif8", true);
        ret.Dif9 = pref.getBoolean("Dif9", true);
        ret.Dif10 = pref.getBoolean("Dif10", true);
        ret.Dif11 = pref.getBoolean("Dif11", true);
        ret.Dif12 = pref.getBoolean("Dif12", true);
        ret.Dif13 = pref.getBoolean("Dif13", true);
        ret.Dif14 = pref.getBoolean("Dif14", true);
        ret.Dif15 = pref.getBoolean("Dif15", true);
        ret.Dif16 = pref.getBoolean("Dif16", true);
        ret.Dif17 = pref.getBoolean("Dif17", true);
        ret.Dif18 = pref.getBoolean("Dif18", true);
        ret.Dif19 = pref.getBoolean("Dif19", true);
        ret.RankAAA = pref.getBoolean("RankAAA", true);
        ret.RankAAp = pref.getBoolean("RankAA+", true);
        ret.RankAA = pref.getBoolean("RankAA", true);
        ret.RankAAm = pref.getBoolean("RankAA-", true);
        ret.RankAp = pref.getBoolean("RankA+", true);
        ret.RankA = pref.getBoolean("RankA", true);
        ret.RankAm = pref.getBoolean("RankA-", true);
        ret.RankBp = pref.getBoolean("RankB+", true);
        ret.RankB = pref.getBoolean("RankB", true);
        ret.RankBm = pref.getBoolean("RankB-", true);
        ret.RankCp = pref.getBoolean("RankC+", true);
        ret.RankC = pref.getBoolean("RankC", true);
        ret.RankCm = pref.getBoolean("RankC-", true);
        ret.RankDp = pref.getBoolean("RankD+", true);
        ret.RankD = pref.getBoolean("RankD", true);
        ret.RankE = pref.getBoolean("RankE", true);
        ret.RankNoPlay = pref.getBoolean("RankNoPlay", true);
        ret.FcMFC = pref.getBoolean("FcMFC", true);
        ret.FcPFC = pref.getBoolean("FcPFC", true);
        // 本来はGreatFC = GFC, GoodFC = FCにしたいが、ここのキーのみ逆にする
        ret.FcGFC = pref.getBoolean("FcFC", true);
        ret.FcFC = pref.getBoolean("FcGFC", true);
        ret.FcLife4 = pref.getBoolean("FcLife4", true);
        ret.FcNoFC = pref.getBoolean("FcNoFC", true);

        ret.FlareRankEX = pref.getBoolean("FlareRankEX", true);
        ret.FlareRankIX = pref.getBoolean("FlareRankIX", true);
        ret.FlareRankVIII = pref.getBoolean("FlareRankVIII", true);
        ret.FlareRankVII = pref.getBoolean("FlareRankVII", true);
        ret.FlareRankVI = pref.getBoolean("FlareRankVI", true);
        ret.FlareRankV = pref.getBoolean("FlareRankV", true);
        ret.FlareRankIV = pref.getBoolean("FlareRankIV", true);
        ret.FlareRankIII = pref.getBoolean("FlareRankIII", true);
        ret.FlareRankII = pref.getBoolean("FlareRankII", true);
        ret.FlareRankI = pref.getBoolean("FlareRankI", true);
        ret.FlareRank0 = pref.getBoolean("FlareRank0", true);
        ret.FlareRankNoRank = pref.getBoolean("FlareRankNoRank", true);

        ret.RankAAArival = pref.getBoolean("RankAAArival", true);
        ret.RankAAprival = pref.getBoolean("RankAA+rival", true);
        ret.RankAArival = pref.getBoolean("RankAArival", true);
        ret.RankAAmrival = pref.getBoolean("RankAA-rival", true);
        ret.RankAprival = pref.getBoolean("RankA+rival", true);
        ret.RankArival = pref.getBoolean("RankArival", true);
        ret.RankAmrival = pref.getBoolean("RankA-rival", true);
        ret.RankBprival = pref.getBoolean("RankB+rival", true);
        ret.RankBrival = pref.getBoolean("RankBrival", true);
        ret.RankBmrival = pref.getBoolean("RankB-rival", true);
        ret.RankCprival = pref.getBoolean("RankC+rival", true);
        ret.RankCrival = pref.getBoolean("RankCrival", true);
        ret.RankCmrival = pref.getBoolean("RankC-rival", true);
        ret.RankDprival = pref.getBoolean("RankD+rival", true);
        ret.RankDrival = pref.getBoolean("RankDrival", true);
        ret.RankErival = pref.getBoolean("RankErival", true);
        ret.RankNoPlayrival = pref.getBoolean("RankNoPlayrival", true);
        ret.RivalWin = pref.getBoolean("RivalWin", true);
        ret.RivalLose = pref.getBoolean("RivalLose", true);
        ret.RivalDraw = pref.getBoolean("RivalDraw", true);
        ret.FcMFCrival = pref.getBoolean("FcMFCrival", true);
        ret.FcPFCrival = pref.getBoolean("FcPFCrival", true);
        ret.FcFCrival = pref.getBoolean("FcFCrival", true);
        ret.FcGFCrival = pref.getBoolean("FcGFCrival", true);
        ret.FcLife4rival = pref.getBoolean("FcLife4rival", true);
        ret.FcNoFCrival = pref.getBoolean("FcNoFCrival", true);
        ret.ScoreMin = pref.getInt("ScoreMin", 0);
        ret.ScoreMax = pref.getInt("ScoreMax", 1000000);
        ret.FlareSkillMin = pref.getInt("FlareSkillMin", 0);
        ret.FlareSkillMax = pref.getInt("FlareSkillMax", 1064);
        ret.ScoreMinRival = pref.getInt("ScoreMinRival", 0);
        ret.ScoreMaxRival = pref.getInt("ScoreMaxRival", 1000000);
        ret.MaxComboMin = pref.getInt("MaxComboMin", -1);
        ret.MaxComboMax = pref.getInt("MaxComboMax", Integer.MAX_VALUE);
        ret.MaxComboMinRival = pref.getInt("MaxComboMinRival", -1);
        ret.MaxComboMaxRival = pref.getInt("MaxComboMaxRival", Integer.MAX_VALUE);
        ret.PlayCountMin = pref.getInt("PlayCountMin", -1);
        ret.PlayCountMax = pref.getInt("PlayCountMax", Integer.MAX_VALUE);
        ret.PlayCountMinRival = pref.getInt("PlayCountMinRival", -1);
        ret.PlayCountMaxRival = pref.getInt("PlayCountMaxRival", Integer.MAX_VALUE);
        ret.ClearCountMin = pref.getInt("ClearCountMin", -1);
        ret.ClearCountMax = pref.getInt("ClearCountMax", Integer.MAX_VALUE);
        ret.ClearCountMinRival = pref.getInt("ClearCountMinRival", -1);
        ret.ClearCountMaxRival = pref.getInt("ClearCountMaxRival", Integer.MAX_VALUE);
        ret.ScoreDifferenceMinusMin = pref.getInt("ScoreDifferenceMinusMin", 0);
        ret.ScoreDifferenceMinusMax = pref.getInt("ScoreDifferenceMinusMax", -1000000);
        ret.ScoreDifferencePlusMin = pref.getInt("ScoreDifferencePlusMin", 0);
        ret.ScoreDifferencePlusMax = pref.getInt("ScoreDifferencePlusMax", 1000000);
        ret.MaxComboDifferenceMinusMin = pref.getInt("MaxComboDifferenceMinusMin", 1);
        ret.MaxComboDifferenceMinusMax = pref.getInt("MaxComboDifferenceMinusMax", Integer.MIN_VALUE);
        ret.MaxComboDifferencePlusMin = pref.getInt("MaxComboDifferencePlusMin", -1);
        ret.MaxComboDifferencePlusMax = pref.getInt("MaxComboDifferencePlusMax", Integer.MAX_VALUE);
        ret.PlayCountDifferenceMinusMin = pref.getInt("PlayCountDifferenceMinusMin", 1);
        ret.PlayCountDifferenceMinusMax = pref.getInt("PlayCountDifferenceMinusMax", Integer.MIN_VALUE);
        ret.PlayCountDifferencePlusMin = pref.getInt("PlayCountDifferencePlusMin", -1);
        ret.PlayCountDifferencePlusMax = pref.getInt("PlayCountDifferencePlusMax", Integer.MAX_VALUE);
        ret.ClearCountDifferenceMinusMin = pref.getInt("ClearCountDifferenceMinusMin", 1);
        ret.ClearCountDifferenceMinusMax = pref.getInt("ClearCountDifferenceMinusMax", Integer.MIN_VALUE);
        ret.ClearCountDifferencePlusMin = pref.getInt("ClearCountDifferencePlusMin", -1);
        ret.ClearCountDifferencePlusMax = pref.getInt("ClearCountDifferencePlusMax", Integer.MAX_VALUE);

        // すでに使っているユーザーのフィルターのチェックが勝手に変わらないよう、SerWorldのまま
        // できれば"SerWORLD"にしたかった
        ret.SerWorld = pref.getBoolean("SerWorld", true);
        ret.SerA3 = pref.getBoolean("SerA3", true);
        ret.SerA20PLUS = pref.getBoolean("SerA20PLUS", true);
        ret.SerA20 = pref.getBoolean("SerA20", true);
        ret.SerA = pref.getBoolean("SerA", true);
        ret.Ser2014 = pref.getBoolean("Ser2014", true);
        ret.Ser2013 = pref.getBoolean("Ser2013", true);
        ret.SerX3 = pref.getBoolean("SerX3", true);
        ret.SerX3vs2ndMIX = pref.getBoolean("SerX3vs2ndMIX", true);
        ret.SerX2 = pref.getBoolean("SerX2", true);
        ret.SerX = pref.getBoolean("SerX", true);
        ret.SerSuperNova2 = pref.getBoolean("SerSuperNova2", true);
        ret.SerSuperNova = pref.getBoolean("SerSuperNova", true);
        ret.SerEXTREME = pref.getBoolean("SerEXTREME", true);
        ret.SerMAX2 = pref.getBoolean("SerMAX2", true);
        ret.SerMAX = pref.getBoolean("SerMAX", true);
        ret.Ser5th = pref.getBoolean("Ser5th", true);
        ret.Ser4th = pref.getBoolean("Ser4th", true);
        ret.Ser3rd = pref.getBoolean("Ser3rd", true);
        ret.Ser2nd = pref.getBoolean("Ser2nd", true);
        ret.Ser1st = pref.getBoolean("Ser1st", true);
        ret.Deleted = pref.getBoolean("Deleted", true);
        return ret;
    }

    private static MusicSortType getMusicSortType(int typenum) {
        return
                typenum == 0 ? MusicSortType.MusicName :
                        typenum == 1 ? MusicSortType.Score :
                                typenum == 2 ? MusicSortType.Rank :
                                        typenum == 3 ? MusicSortType.FullComboType :
                                                typenum == 4 ? MusicSortType.Difficulty :
                                                        typenum == 5 ? MusicSortType.Pattern :
                                                                typenum == 6 ? MusicSortType.SPDP :
                                                                        typenum == 7 ? MusicSortType.ID :
                                                                                typenum == 8 ? MusicSortType.RivalScore :
                                                                                        typenum == 9 ? MusicSortType.RivalRank :
                                                                                                typenum == 10 ? MusicSortType.RivalFullComboType :
                                                                                                        typenum == 11 ? MusicSortType.RivalScoreDifference :
                                                                                                                typenum == 12 ? MusicSortType.RivalScoreDifferenceAbs :
                                                                                                                        typenum == 13 ? MusicSortType.BpmMax :
                                                                                                                                typenum == 14 ? MusicSortType.BpmMin :
                                                                                                                                        typenum == 15 ? MusicSortType.BpmAve :
                                                                                                                                                typenum == 16 ? MusicSortType.SeriesTitle :
                                                                                                                                                        typenum == 17 ? MusicSortType.ComboCount :
                                                                                                                                                                typenum == 18 ? MusicSortType.PlayCount :
                                                                                                                                                                        typenum == 19 ? MusicSortType.ClearCount :
                                                                                                                                                                                typenum == 20 ? MusicSortType.RivalComboCount :
                                                                                                                                                                                        typenum == 21 ? MusicSortType.RivalPlayCount :
                                                                                                                                                                                                typenum == 22 ? MusicSortType.RivalClearCount :
                                                                                                                                                                                                        typenum == 23 ? MusicSortType.FlareRank :
                                                                                                                                                                                                                typenum == 24 ? MusicSortType.FlareSkill :
                                                                                                                                                                                                                        MusicSortType.MusicName;
    }

    public static MusicSort readMusicSort(Context context, int id) {
        MusicSort ret = new MusicSort();
        //SharedPreferences pref = context.getSharedPreferences("MusicSort"+String.valueOf(id), Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        SharedPreferences pref = context.getSharedPreferences("MusicSort" + String.valueOf(id), Context.MODE_PRIVATE);
        int typenum;
        typenum = pref.getInt("1stType", 0);
        ret._1stType = getMusicSortType(typenum);
        ret._1stOrder = pref.getBoolean("1stOrder", true) ? SortOrder.Ascending : SortOrder.Desending;
        typenum = pref.getInt("2ndType", 0);
        ret._2ndType = getMusicSortType(typenum);
        ret._2ndOrder = pref.getBoolean("2ndOrder", true) ? SortOrder.Ascending : SortOrder.Desending;
        typenum = pref.getInt("3rdType", 0);
        ret._3rdType = getMusicSortType(typenum);
        ret._3rdOrder = pref.getBoolean("3rdOrder", true) ? SortOrder.Ascending : SortOrder.Desending;
        typenum = pref.getInt("4thType", 0);
        ret._4thType = getMusicSortType(typenum);
        ret._4thOrder = pref.getBoolean("4thOrder", true) ? SortOrder.Ascending : SortOrder.Desending;
        typenum = pref.getInt("5thType", 0);
        ret._5thType = getMusicSortType(typenum);
        ret._5thOrder = pref.getBoolean("5thOrder", true) ? SortOrder.Ascending : SortOrder.Desending;
        return ret;
    }

    public static TreeMap<Integer, MusicData> readMusicList(Context context) {
        TreeMap<Integer, BooleanPair> shockArrows = readShock(context);
        TreeMap<Integer, MusicData> ret = new TreeMap<Integer, MusicData>();
        FileInputStream in = null;
        try {
            String fileName = "MusicNames.txt";
            {
                in = context.openFileInput(fileName);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;
            while ((str = reader.readLine()) != null) {
                {
                    int curr = 0;
                    String[] sp = str.split("\t");
                    MusicData md = new MusicData();
                    md.ShockArrowExists = shockArrows;
                    md.Id = Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Name = sp[curr];
                    ++curr;
                    md.Ruby = sp[curr];
                    ++curr;
                    md.SeriesTitle = (
                            sp[curr].equals("1st") ? SeriesTitle._1st :
                                    sp[curr].equals("2nd") ? SeriesTitle._2nd :
                                            sp[curr].equals("3rd") ? SeriesTitle._3rd :
                                                    sp[curr].equals("4th") ? SeriesTitle._4th :
                                                            sp[curr].equals("5th") ? SeriesTitle._5th :
                                                                    sp[curr].equals("MAX") ? SeriesTitle.MAX :
                                                                            sp[curr].equals("MAX2") ? SeriesTitle.MAX2 :
                                                                                    sp[curr].equals("EXTREME") ? SeriesTitle.EXTREME :
                                                                                            sp[curr].equals("Super Nova") ? SeriesTitle.SuperNOVA :
                                                                                                    sp[curr].equals("Super Nova2") ? SeriesTitle.SuperNOVA2 :
                                                                                                            sp[curr].equals("X") ? SeriesTitle.X :
                                                                                                                    sp[curr].equals("X2") ? SeriesTitle.X2 :
                                                                                                                            sp[curr].equals("X3") ? SeriesTitle.X3 :
                                                                                                                                    sp[curr].equals("X3(2nd Mix)") ? SeriesTitle.X3_2ndMix :
                                                                                                                                            sp[curr].equals("2013") ? SeriesTitle._2013 :
                                                                                                                                                    sp[curr].equals("2014") ? SeriesTitle._2014 :
                                                                                                                                                            sp[curr].equals("A") ? SeriesTitle.A :
                                                                                                                                                                    sp[curr].equals("A20") ? SeriesTitle.A20 :
                                                                                                                                                                            sp[curr].equals("A20 PLUS") ? SeriesTitle.A20PLUS :
                                                                                                                                                                                    sp[curr].equals("A3") ? SeriesTitle.A3 :
                                                                                                                                                                                            SeriesTitle.World);
                    ++curr;
                    md.MinBPM = "?".equals(sp[curr]) ? 0 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.MaxBPM = "?".equals(sp[curr]) ? 0 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_bSP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_BSP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_DSP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_ESP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_CSP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_BDP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_DDP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_EDP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ++curr;
                    md.Difficulty_CDP = "?".equals(sp[curr]) ? -1 : Integer.parseInt(sp[curr]);
                    ret.put(md.Id, md);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
        }
        return ret;
    }

    public static TreeMap<Integer, BooleanPair> readShock(Context context) {
        TreeMap<Integer, BooleanPair> ret = new TreeMap<Integer, BooleanPair>();
        FileInputStream in = null;
        try {
            String fileName = "ShockArrowExists.txt";
            {
                in = context.openFileInput(fileName);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;
            while ((str = reader.readLine()) != null) {
                {
                    int curr = 0;
                    String[] sp = str.split("\t");
                    BooleanPair bp = new BooleanPair();
                    int id = Integer.parseInt(sp[curr]);
                    ++curr;
                    if (sp.length > curr) {
                        if (sp[curr].length() >= 2) {
                            bp.b1 = sp[curr].charAt(0) == '1';
                            bp.b2 = sp[curr].charAt(1) == '1';
                            ret.put(id, bp);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
        }
        return ret;
    }

    private static void close(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void close(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readChangelog(Context context) {
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            try {
                is = context.getAssets().open("UpdatesGP.txt");
                br = new BufferedReader(new InputStreamReader(is));

                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }
        } catch (IOException e) {

        }
        return sb.toString();
    }

    public static TreeMap<Integer, String[]> readComments(Context context) {
        TreeMap<Integer, String[]> ret = new TreeMap<Integer, String[]>();
        ret.put(27, new String[]{"BEG用コメント", "BSC", "踊", "EXPERT", "Cha", "BDP", "DDP", "EDP", "DP鬼"});
        return ret;
    }

    public static boolean saveComments(Context context, TreeMap<Integer, String> comments) {
        return true;
    }

    public static TreeMap<Integer, MusicScore> scoreFromScoreDb(Context context, String scoreDbText) {
        TreeMap<Integer, MusicScore> ret = new TreeMap<Integer, MusicScore>();
        String[] lines = scoreDbText.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            String[] sp = lines[i].split("\t");
            if (sp.length < 10) {
                continue;
            }
            int id = Integer.parseInt(sp[0]);
            MusicScore ms = new MusicScore();
            int spi = 0;
            ++spi;
            ms.bSP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.bSP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.bSP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.bSP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.BSP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.BSP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.BSP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.BSP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.DSP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.DSP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.DSP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.DSP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.ESP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.ESP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.ESP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.ESP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.CSP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.CSP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.CSP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.CSP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.BDP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.BDP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.BDP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.BDP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.DDP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.DDP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.DDP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.DDP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.EDP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.EDP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.EDP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.EDP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.CDP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.CDP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.CDP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.CDP.MaxCombo = Integer.valueOf(sp[spi]);
            ret.put(id, ms);
        }
        return ret;
    }

    public static TreeMap<Integer, MusicScore> readScoreList(Context context, String rivalId) {
        String fileName = "ScoreData" + (rivalId != null ? rivalId : "") + ".txt";

        if (!fileExists(context, fileName)) {
            return new TreeMap<>();
        }

        String content = readFileContent(context, fileName);
        TreeMap<Integer, MusicScore> scores = loadMusicScoresToTreeMap(content);
        migrateScoreDataIfNeeded(context, rivalId, scores);
        return scores;
    }


    private static boolean fileExists(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }

    private static String readFileContent(Context context, String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(fileName), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static TreeMap<Integer, MusicScore> loadMusicScoresToTreeMap(String content) {
        TreeMap<Integer, MusicScore> scores = new TreeMap<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            String[] sp = line.split("\t");
            if (sp.length > 0) {
                int id = Integer.parseInt(sp[0]);
                MusicScore ms = parseMusicScore(sp);
                scores.put(id, ms);
            }
        }
        return scores;
    }

    private static MusicScore parseMusicScore(String[] sp) {
        MusicScore ms = new MusicScore();
        int spi = 0;

        ScoreData[] scoreDataArray = {ms.bSP, ms.BSP, ms.DSP, ms.ESP, ms.CSP, ms.BDP, ms.DDP, ms.EDP, ms.CDP};
        for (ScoreData sd : scoreDataArray) {
            sd.Rank = parseRank(sp[++spi]);
            sd.Score = Integer.valueOf(sp[++spi]);
            sd.FullComboType = parseFullComboType(sp[++spi]);
            sd.MaxCombo = Integer.valueOf(sp[++spi]);
        }

        if (sp.length > spi + 1) {
            for (ScoreData sd : scoreDataArray) {
                sd.PlayCount = Integer.valueOf(sp[++spi]);
                sd.ClearCount = Integer.valueOf(sp[++spi]);
            }
        }

        if (sp.length > spi + 9) {
            for (ScoreData sd : scoreDataArray) {
                sd.FlareRank = Integer.valueOf(sp[++spi]);
            }
        } else {
            for (ScoreData sd : scoreDataArray) {
                sd.FlareRank = -1;
            }
        }

        return ms;
    }

    private static void migrateScoreDataIfNeeded(Context context, String rivalId, TreeMap<Integer, MusicScore> musicScores) {
        String key = "FlareSkillMigrationCompleted_" + (rivalId != null ? rivalId : "self");
        SharedPreferences prefs = context.getSharedPreferences("MigrationPrefs", Context.MODE_PRIVATE);

        if (!prefs.getBoolean(key, false)) {
            if (saveScoreData(context, rivalId, musicScores)) {
                prefs.edit().putBoolean(key, true).apply();
                Log.d("FileReader", "Score data migration completed successfully for " + (rivalId != null ? rivalId : "self"));
            } else {
                Log.e("FileReader", "Failed to migrate score data for " + (rivalId != null ? rivalId : "self"));
            }
        }
    }

    private static MusicRank parseRank(String rankString) {
        switch (rankString) {
            case "AAA":
                return MusicRank.AAA;
            case "AA+":
                return MusicRank.AAp;
            case "AA":
                return MusicRank.AA;
            case "AA-":
                return MusicRank.AAm;
            case "A+":
                return MusicRank.Ap;
            case "A":
                return MusicRank.A;
            case "A-":
                return MusicRank.Am;
            case "B+":
                return MusicRank.Bp;
            case "B":
                return MusicRank.B;
            case "B-":
                return MusicRank.Bm;
            case "C+":
                return MusicRank.Cp;
            case "C":
                return MusicRank.C;
            case "C-":
                return MusicRank.Cm;
            case "D+":
                return MusicRank.Dp;
            case "D":
                return MusicRank.D;
            case "E":
                return MusicRank.E;
            default:
                return MusicRank.Noplay;
        }
    }

    private static FullComboType parseFullComboType(String fcString) {
        switch (fcString) {
            case "MerverousFullCombo":
                return FullComboType.MerverousFullCombo;
            case "PerfectFullCombo":
                return FullComboType.PerfectFullCombo;
            case "FullCombo":
                return FullComboType.FullCombo;
            case "GoodFullCombo":
                return FullComboType.GoodFullCombo;
            case "Life4":
                return FullComboType.Life4;
            default:
                return FullComboType.None;
        }
    }


    public static boolean saveScoreData(Context context, String rivalId, TreeMap<Integer, MusicScore> scores) {
        String fileName = "ScoreData" + (rivalId != null ? rivalId : "") + ".txt";

        try (FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            for (Integer id : scores.keySet()) {
                String line = TextUtil.getScoreBackupText(id, scores) + "\n";
                out.write(line.getBytes());
                out.flush();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean resetScores(Context context) {
        String[] files = context.fileList();
        for (String file : files) {
            if (file.startsWith("ScoreData") && file.endsWith(".txt")) {
                context.deleteFile(file);
            }
        }
        return true;
    }

    public static boolean copyMusicNames(Context context) {
        FileOutputStream out = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            try {
                is = context.getAssets().open("MusicNames.txt");
                br = new BufferedReader(new InputStreamReader(is));

                //FileOutputStream out = context.openFileOutput( "ScoreData.txt", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE );
                String fileName = "MusicNames";
                fileName += ".txt";
                {
                    out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                }
                String str;
                while ((str = br.readLine()) != null) {
                    str = str + "\r\n";
                    out.write(str.getBytes());
                    out.flush();
                }
            } finally {
                close(out);
                close(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean copyShockArrowExists(Context context) {
        FileOutputStream out = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            try {
                is = context.getAssets().open("ShockArrowExists.txt");
                br = new BufferedReader(new InputStreamReader(is));

                //FileOutputStream out = context.openFileOutput( "ScoreData.txt", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE );
                String fileName = "ShockArrowExists";
                fileName += ".txt";
                {
                    out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                }
                String str;
                while ((str = br.readLine()) != null) {
                    str = str + "\r\n";
                    out.write(str.getBytes());
                    out.flush();
                }
            } finally {
                close(out);
                close(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean copyMusicIdList(Context context) {
        FileOutputStream out = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            try {
                is = context.getAssets().open("WebMusicIds.txt");
                br = new BufferedReader(new InputStreamReader(is));

                //FileOutputStream out = context.openFileOutput( "ScoreData.txt", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE );
                String fileName = "WebMusicIds";
                fileName += ".txt";
                {
                    out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                }
                String str;
                while ((str = br.readLine()) != null) {
                    str = str + "\r\n";
                    out.write(str.getBytes());
                    out.flush();
                }
            } finally {
                close(out);
                close(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static IdToWebMusicIdList readWebMusicIds(Context context) {
        IdToWebMusicIdList ret = new IdToWebMusicIdList();
        FileInputStream in = null;
        try {
            String fileName = "WebMusicIds";
            fileName += ".txt";
            {
                in = context.openFileInput(fileName);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;
            while ((str = reader.readLine()) != null) {
                String[] sp = str.split("\t");
                int id = Integer.parseInt(sp[0]);
                WebMusicId wmi = new WebMusicId();
                int spi = 0;
                ++spi;
                wmi.idOnWebPage = sp[spi];
                ++spi;
                wmi.titleOnWebPage = sp[spi];
                ++spi;
                wmi.isDeleted = !sp[spi].equals("0");
                ret.put(id, wmi);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
        }
        return ret;
    }

    public static boolean saveWebMusicIds(Context context, IdToWebMusicIdList webMusicIds) {
        FileOutputStream out = null;
        try {
            //FileOutputStream out = context.openFileOutput( "ScoreData.txt", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE );
            StringBuilder sb = new StringBuilder();
            String fileName = "WebMusicIds";
            fileName += ".txt";
            {
                out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            }
            for (Integer id : webMusicIds.keySet()) {
                sb.append(String.valueOf(id));
                sb.append("\t");
                WebMusicId musicId = webMusicIds.get(id);
                sb.append(String.valueOf(musicId.idOnWebPage));
                sb.append("\t");
                sb.append(String.valueOf(musicId.titleOnWebPage));
                sb.append("\t");
                sb.append(musicId.isDeleted ? "1" : "0");
                sb.append("\t");
                sb.append("\n");
                out.write(sb.toString().getBytes());
                out.flush();
                sb.delete(0, sb.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(out);
        }
        return true;
    }

    public static boolean saveText(Context context, String text, String name) {
        FileOutputStream out = null;
        try {
            String fileName = name;
            {
                out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            }
            out.write(text.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(out);
        }
        return true;
    }

    public static String readText(Context context, String fileName) {
        StringBuilder ret = new StringBuilder();
        FileInputStream in = null;
        try {
            {
                in = context.openFileInput(fileName);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;
            while ((str = reader.readLine()) != null) {
                ret.append(str);
                ret.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
        }
        return ret.toString();
    }

    public static void saveFlareSkillNotePlayerId(Context context, String id, String name) {
        SharedPreferences prefs = context.getSharedPreferences("FlareSkillNotePlayerData", Context.MODE_PRIVATE);
        Editor e = prefs.edit();
        e.putString("id", id);
        e.putString("name", name);

        e.apply();
    }

    public static String readFlareSkillNotePlayerName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FlareSkillNotePlayerData", Context.MODE_PRIVATE);
        return prefs.getString("name", "");
    }

    public static String readFlareSkillNotePlayerId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FlareSkillNotePlayerData", Context.MODE_PRIVATE);
        return prefs.getString("id", "");
    }
}
