(function (window, undefined) {
    'use strict';

    var PlugTest =
        {
            setKeyboardCallback: function (callback, onError) {
                cordova.exec(callback, onError, 'PlugTest', 'setKeyboardCallback');
            },
        };

    cordova.addConstructor(function () {
        window.PlugTest = PlugTest;
        return window.PlugTest;
    });

})(window);
