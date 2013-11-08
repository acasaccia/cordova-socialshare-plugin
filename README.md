cordova-socialshare-plugin
==========================

Cordova Social Share Plugin for Android

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
