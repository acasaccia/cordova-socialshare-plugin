var SocialShare = function() {};


// Call this to register for push notifications. Content of [options] depends on whether we are working with APNS (iOS) or GCM (Android)
SocialShare.prototype.share = function(successCallback, errorCallback, options) {
	cordova.exec(
		successCallback,
		errorCallback,
		'SocialShare',
		'share',
		[
			{
				action: 'android.intent.action.SEND',
				type: 'text/plain',
				extras: { 
					'android.intent.extra.TEXT' : options.text.replace("{{url}}", options.url),
					'dialogTitle' : options.dialogTitle,
					'fbShareUrl' : options.url,
					'mailSubject' : options.mailSubject
				}
			}
		]
	);
};

//-------------------------------------------------------------------

if(!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.SocialShare) {
    window.plugins.SocialShare = new SocialShare();
}

if (module.exports) {
  module.exports = SocialShare;
}
