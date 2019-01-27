package saarland.cispa.bletrackerlib.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RemoteSettings {
    private static volatile RemoteSettings instance = new RemoteSettings();
    private SharedPreferences prefManager;

    private RemoteSettings(){
    }

    public static void Init(Context context)
    {
        getInstance().prefManager = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static RemoteSettings getInstance() {
        return instance;
    }



    public static boolean GetSubmit()
    {
        return getInstance().prefManager.getBoolean("SUBMIT", true);
    }
    public static void SetSubmit(boolean submit)
    {
        SharedPreferences.Editor e = getInstance().prefManager.edit();
        e.putBoolean("SUBMIT", submit);
        e.apply();
    }

    public static int GetConfirmations()
    {
        return getInstance().prefManager.getInt("CONFIRMATIONS", 1);
    }

    public static void SetConfirmations(int confirmations)
    {
        SharedPreferences.Editor e = getInstance().prefManager.edit();
        e.putInt("CONFIRMATIONS", confirmations);
        e.apply();
    }


}
