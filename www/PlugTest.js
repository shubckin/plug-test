(function (window, undefined) {
    'use strict';

    var PlugTest =
        {
            testSomething: function (str, callback, onError) {
                cordova.exec(callback, onError, 'PlugTest', 'testSomething', [str]);
            },

        };

    cordova.addConstructor(function () {
        window.PlugTest = PlugTest;
        return window.PlugTest;
    });

})(window);
