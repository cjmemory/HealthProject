'use strict';

angular.module('myhealthApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
