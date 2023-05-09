
package com.imokhles.sncommandexecutor;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;

import java.lang.Process;
import java.lang.ProcessBuilder;


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
    public void executeCommand(String command, Callback callback) {
        new Thread(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder("su", "-c", command);
            StringBuilder output = new StringBuilder();
            boolean success = true;

            try {
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    success = false;
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                success = false;
            }

            WritableMap map = new WritableNativeMap();
            map.putBoolean("status", success);
            map.putString("output", output.toString());
            callback.invoke(map);
        }).start();
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
