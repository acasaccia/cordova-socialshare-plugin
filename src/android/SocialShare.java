package com.gestionaleauto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cordova.DroidGap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

/**
 * 
 */
public class SocialShare extends CordovaPlugin {

    private String onNewIntentCallback = null;

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
        
        List<Intent> intentList = new ArrayList<Intent>();
        
        String[] packages = {
        	"com.android.mms",			// System messaging
        	"com.facebook.katana",		// Facebook
        	"com.android.email",		// System mail
        	"com.google.android.gm",	// Gmail
        	"com.twitter.android",		// Twitter official
            "com.twidroid", 			// Twidroid
            "com.handmark.tweetcaster", // Tweecaster
            "com.thedeck.android", 		// TweetDeck
        	"com.whatsapp"				// WhatsApp
        };
        
        Intent mainIntent = new Intent(action);
        mainIntent.setPackage(packages[0]);
        mainIntent.setType(type);
        mainIntent.putExtra(Intent.EXTRA_TEXT, extras.get(Intent.EXTRA_TEXT));
        
        for (int c=1; c<packages.length; c++) {
        	
        	Intent i = new Intent(action).setPackage(packages[c]);
        	i.setType(type);
        	
        	if (packages[c] == "com.facebook.katana") {
        		i.putExtra(Intent.EXTRA_TEXT, extras.get("fbShareUrl"));
        	} else if (packages[c] == "com.android.email" || packages[c] == "com.google.android.gm") {
        		i.putExtra(Intent.EXTRA_SUBJECT , extras.get("mailSubject"));
        		i.putExtra(Intent.EXTRA_TEXT, extras.get(Intent.EXTRA_TEXT));
        	} else {
        		i.putExtra(Intent.EXTRA_TEXT, extras.get(Intent.EXTRA_TEXT));
        	}
        	
        	intentList.add(i);
        }
        
        Intent[] extraIntents = intentList.toArray( new Intent[ intentList.size() ]);
                        
        ((DroidGap)this.cordova.getActivity()).startActivity(Intent.createChooser(mainIntent, extras.get("dialogTitle")).putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents));
        
    }
}
