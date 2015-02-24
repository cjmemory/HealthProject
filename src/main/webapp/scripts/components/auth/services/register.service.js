'use strict';

angular.module('myhealthApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


