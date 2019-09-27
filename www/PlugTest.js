(function (window, undefined) {
    'use strict';

    var PlugTest =
        {
            SYSTEM_UI_FLAG_FULLSCREEN: 4,
            SYSTEM_UI_FLAG_HIDE_NAVIGATION: 2,
            SYSTEM_UI_FLAG_IMMERSIVE: 2048,
            SYSTEM_UI_FLAG_IMMERSIVE_STICKY: 4096,
            SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN: 1024,
            SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION: 512,
            SYSTEM_UI_FLAG_LAYOUT_STABLE: 256,
            SYSTEM_UI_FLAG_LIGHT_STATUS_BAR: 8192,
            SYSTEM_UI_FLAG_LOW_PROFILE: 1,
            SYSTEM_UI_FLAG_VISIBLE: 0,

            testSomething: function (str, callback, onError) {
                cordova.exec(callback, onError, 'PlugTest', 'testSomething', [str]);
            },

            isSupported: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'isSupported', []);
            },

            isImmersiveModeSupported: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'isImmersiveModeSupported', []);
            },

            immersiveWidth: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'immersiveWidth', []);
            },

            immersiveHeight: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'immersiveHeight', []);
            },

            leanMode: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'leanMode', []);
            },

            showSystemUI: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'showSystemUI', []);
            },

            showUnderStatusBar: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'showUnderStatusBar', []);
            },

            showUnderSystemUI: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'showUnderSystemUI', []);
            },

            immersiveMode: function (successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'immersiveMode', []);
            },

            /**
             * @see    https://developer.android.com/reference/android/view/View.html#setSystemUiVisibility(int)
             */
            setSystemUiVisibility: function (visibility, successFunction, errorFunction) {
                cordova.exec(successFunction, errorFunction, 'PlugTest', 'setSystemUiVisibility', [visibility || 0]);
            }

        };

    cordova.addConstructor(function () {
        window.PlugTest = PlugTest;
        return window.PlugTest;
    });

})(window);
