/*global $:false, device:false */
//#########################
// entry.js
//#########################
"use strict";
var APP;
(function () {


    // Set to "true" if you want the console.log messages to appear.
    var instance,
        initDebug = function () {

            console.log("in APP.initDebug");

            if (window.device ) {
                console.log("device.version: " + device.version);
                console.log("device.model: " + device.model);
                console.log("device.platform: " + device.platform);
                console.log("device.version: " + device.version);
            }
        },
        // Using a splash screen is optional. This function will not fail if none is present.
        // This is also a simple study in the art of multi-platform device API detection.
        hideSplashScreen = function () {

            // see https://github.com/01org/appframework/blob/master/documentation/detail/%24.ui.launch.md
            // Do the following if you disabled App Framework autolaunch (in index.html, for example)
            // $.ui.launch() ;

            if (navigator.splashscreen && navigator.splashscreen.hide) {   // Cordova API detected
                navigator.splashscreen.hide();
            }
        };
    instance = {
        initDebug: initDebug,
        hideSplashScreen: hideSplashScreen
    };
    APP = instance;

}());

window.MILEAGE = APP || {};

/* Constructor
(function (APP) {
    "use strict";
    var MILEAGE = {};
    APP.MILEAGE = MILEAGE;

}(this));*/
