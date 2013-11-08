cordova-socialshare-plugin
==========================

Cordova Social Share Plugin for Android

Sends a snippet of text (or just an url for Facebook) to be shared via an android intent to a bunch of whitelisted applications:

* Facebook
* System mail
* Gmail
* Twitter official
* Twidroid
* Tweecaster
* TweetDeck
* WhatsApp

Usage example:

```js
window.plugins.SocialShare.share(
    successCallback,
    failureCallback,
    {
        dialogTitle : 'Share using:',         // Title of the native dialog
        url : "http://is.gd/DikVaQ",          // Url to be shared
        text : "Take a look at this {{url}}", // Text to be shared where possible (not in Facebook)
                                              // {{url}} will be replaced by given url
        mailSubject : "Great news for you"    // Subject of the email if users select a mail client
    }
);
```
