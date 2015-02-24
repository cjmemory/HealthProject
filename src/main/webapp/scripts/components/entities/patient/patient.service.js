'use strict';

angular.module('myhealthApp')
    .factory('Patient', function ($resource) {
        return $resource('api/createFolder', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });


//api/patients/:id
