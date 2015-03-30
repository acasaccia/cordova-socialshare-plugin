package com.gestionaleauto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

/**
 * 
 */
public class SocialShare extends CordovaPlugin {

    private static final String TAG = "SocialSharePlugin";

    /**
     * Executes the request and returns PluginResult.
     * 
     * @param action
     *            The action to execute.
     * @param args
     *            JSONArray of arguments for the plugin.
     * @param callbackContext
     *            The callback id used when calling back into JavaScript.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        
        try {
            if (action.equals("share")) {
                if (args.length() != 1) {
                    callbackContext.error("Invalid action");
                    return false;
                }

                // Parse the arguments
                JSONObject obj = args.getJSONObject(0);
                String type = obj.has("type") ? obj.getString("type") : null;
                JSONObject extras = obj.has("extras") ? obj.getJSONObject("extras") : null;
                Map<String, String> extrasMap = new HashMap<String, String>();

                // Populate the extras if any exist
                if (extras != null) {
                    JSONArray extraNames = extras.names();
                    for (int i = 0; i < extraNames.length(); i++) {
                        String key = extraNames.getString(i);
                        String value = extras.getString(key);
                        extrasMap.put(key, value);
                    }
                }

                share(obj.getString("action"), type, extrasMap);
                callbackContext.success();
                return true;

            }
            callbackContext.error("Invalid action");
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("JSON exception");
            return false;
        }
    }

    void share(String action, String type, Map<String, String> extras) {
        
        //Log.v(TAG, String.format("In share() method action: [%s], type: [%s]", action, type));
        
        List<Intent> intentList = new ArrayList<Intent>();
        
        String[] packages = {
            "com.android.email",        // System mail
            "com.android.mms",          // System messaging
            "com.google.android.talk",  // Google Nexus system messaging
            "com.google.android.gm",    // GMail
            "com.google.android.gms",   // Google Nexus GMail
            "com.facebook.katana",      // Facebook
            "com.twitter.android",      // Twitter official
            "com.twidroid",             // Twidroid
            "com.handmark.tweetcaster", // Tweecaster
            "com.thedeck.android",      // TweetDeck
            "com.whatsapp"              // WhatsApp
        };

        int first_available_index = -1; 
        
        for (int c=0; c<packages.length; c++) {
            
            Intent i = new Intent(action).setPackage(packages[c]);
            i.setType(type);
            
            switch (packages[c]) {
                case "com.facebook.katana":
                    i.putExtra(Intent.EXTRA_TEXT, extras.get("fbShareUrl"));
                    break;
                case "com.android.email":
                case "com.google.android.gm":
                case "com.google.android.gms":
                    i.putExtra(Intent.EXTRA_SUBJECT , extras.get("mailSubject"));
                    i.putExtra(Intent.EXTRA_TEXT, extras.get(Intent.EXTRA_TEXT));
                    break;
                default:
                    i.putExtra(Intent.EXTRA_TEXT, extras.get(Intent.EXTRA_TEXT));
            }
            
            intentList.add(i);
            if (first_available_index == -1 && this.isPackageInstalled(packages[c])) {
                first_available_index = intentList.size() - 1;
            }
            
        }
        
        Intent mainIntent = intentList.remove(first_available_index > -1 ? first_available_index : 0);
        
        Intent[] extraIntents = intentList.toArray( new Intent[ intentList.size() ]);

        Intent chooser = Intent.createChooser(
            mainIntent, 
            extras.get("dialogTitle")
        );
        
        // if there is at least an application in the list that can manage the main intent
        // then we can set the EXTRA_INITIAL_INTENTS to propose alternatives.
        // If none of the whitelisted applications can manage the main intent, then we should
        // start the main intent and let the system propose all the applications it has.
        if (first_available_index > -1) {
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        }
        
        this.cordova.getActivity().startActivity(chooser);
        
    }
    
    private boolean isPackageInstalled(String packagename) {
        Context context = this.cordova.getActivity().getApplicationContext();
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
    
}
