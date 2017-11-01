package com.megster.cordova;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
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

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        JSONObject arguments =  args.getJSONObject(0);
        if(arguments.has("mimeTypes")) {
            Object aObj = arguments.get("mimeTypes");
            List<String> stringList = new ArrayList<String>();
            if(aObj instanceof String){
                String mimeType = arguments.getString("mimeTypes");
                if(!mimeType.equals("")) {
                    stringList.add(mimeType);
                }
            } else if (aObj instanceof JSONArray) {
                JSONArray arrMimeTypes = arguments.getJSONArray("mimeTypes");
                int len = arrMimeTypes.length();
                if(len > 0) {
                    for(int i = 0; i < len; i++ ) {
                        if(!arrMimeTypes.getString(i).equals("")) {
                            stringList.add(arrMimeTypes.getString(i));
                        }
                    }
                }
            }
            if(stringList.size() > 0) {
                String[] mimeTypes = stringList.toArray( new String[] {} );
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        Intent chooser = Intent.createChooser(intent, "Select File");
        cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FILE_REQUEST && callback != null) {

            if (resultCode == Activity.RESULT_OK) {

                Uri uri = data.getData();

                if (uri != null) {

                    Log.w(TAG, uri.toString());
                    callback.success(uri.toString());

                } else {

                    callback.error("File uri was null");
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                callback.error("No file selected");
            } else {

                callback.error(resultCode);
            }
        }
    }
}
