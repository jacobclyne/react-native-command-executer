
package com.imokhles.sncommandexecutor;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.util.Log;

import org.json.JSONObject;

public class SNCommandExecutorModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public SNCommandExecutorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "SNCommandExecutor";
    }

    @ReactMethod
    public boolean executeCommand(String command, final Callback callback) {
        Runtime runtime = Runtime.getRuntime();
        Process localProcess = null;
        OutputStreamWriter osw = null;
        BufferedReader reader = null;
        StringBuilder output = new StringBuilder();

        try {
            localProcess = runtime.exec("su");
            osw = new OutputStreamWriter(localProcess.getOutputStream());
            osw.write(command);
            osw.flush();
            osw.close();

            reader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            WritableMap map = new WritableNativeMap();
            map.putBoolean("status", false);
            callback.invoke(map);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    WritableMap map = new WritableNativeMap();
                    map.putBoolean("status", false);
                    callback.invoke(map);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    WritableMap map = new WritableNativeMap();
                    map.putBoolean("status", false);
                    callback.invoke(map);
                }
            }
        }
        try {
            if (localProcess != null) {
                localProcess.waitFor();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            WritableMap map = new WritableNativeMap();
            map.putBoolean("status", false);
            callback.invoke(map);
        }
        if (localProcess != null && localProcess.exitValue() == 0) {
            WritableMap map = new WritableNativeMap();
            map.putBoolean("status", true);
            map.putString("output", output.toString())
            callback.invoke(map);
        }
    }

    @ReactMethod
    public void verifyRootStatus(final Callback callback) {
        WritableMap map = new WritableNativeMap();
        map.putBoolean("isRooted", isRooted());
        callback.invoke(map);
    }

    public static boolean isRooted() {
        return canExecute("/system/xbin/which su")
                || canExecute("/system/bin/which su")
                || canExecute("which su")
                || canExecute("su")
                || canExecute("busybox which su");
    }

    private static boolean canExecute(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String info = in.readLine();
            if (info != null) return true;
            return false;
        } catch (Exception e) {

        } finally {
            if (process != null) process.destroy();
        }
        return false;
    }

}
