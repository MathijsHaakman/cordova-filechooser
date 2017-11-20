package com.megster.cordova;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FileChooser extends CordovaPlugin {

    private static final String TAG = "FileChooser";
    private static final String ACTION_OPEN = "open";
    private static final int PICK_FILE_REQUEST = 1;
    CallbackContext callback;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_OPEN)) {
            chooseFile(callbackContext, args);
            return true;
        }

        return false;
    }

    public void chooseFile(CallbackContext callbackContext, CordovaArgs args) throws JSONException {
        boolean PICK_MULTIPLE_FILES = false;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        JSONObject arguments =  args.getJSONObject(0);
        if(arguments.has("mimeTypes")) {
            Object mimeTypesObj = arguments.get("mimeTypes");
            List<String> stringList = new ArrayList<String>();
            if (mimeTypesObj instanceof JSONArray) {
                JSONArray mimeTypesArr = arguments.getJSONArray("mimeTypes");
                int arrLen = mimeTypesArr.length();
                if (arrLen == 1) {
                    intent.setType(mimeTypesArr.getString(0));
                } else if (arrLen > 1) {
                    for (int i = 0; i < arrLen; i++) {
                        if (!mimeTypesArr.getString(i).equals("")) {
                            stringList.add(mimeTypesArr.getString(i));
                        }
                    }
                }
            } else if (mimeTypesObj instanceof String) {
                String mimeType = arguments.getString("mimeTypes");
                if (!mimeType.equals("")) {
                    intent.setType(mimeType);
                }
            }
            if (stringList.size() > 0) {
                String[] mimeTypes = stringList.toArray(new String[]{});
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        }
        if(arguments.has("multipleFiles")) {
            PICK_MULTIPLE_FILES = arguments.optBoolean("multipleFiles");
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, PICK_MULTIPLE_FILES);

        Intent chooser = Intent.createChooser(intent, "Select File");
        cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && callback != null && resultCode == Activity.RESULT_CANCELED) {
            callback.error("No file selected");
        } else if (requestCode == PICK_FILE_REQUEST && callback != null && resultCode == Activity.RESULT_OK) {
            List<String> retrievedUris = new ArrayList<String>();
            ClipData clipData = data.getClipData();
            if(!(clipData == null)) {
                for(int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    retrievedUris.add(item.getUri().toString());
                }
                JSONArray jsonArray = new JSONArray(retrievedUris);
                callback.success(jsonArray);
            }
            String uri = data.getDataString();
            if (uri != null) {
                Log.w(TAG, uri);
                retrievedUris.add(uri);
                JSONArray jsonArray = new JSONArray(retrievedUris);
                callback.success(jsonArray);
            } else {
                callback.error("File uri was null");
            }
        } else if (callback != null) {
            callback.error(resultCode);
        } else {
            Log.w(TAG, "Can't give URI to cordova because resultCode is null");
        }
    }
}
