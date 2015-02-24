'use strict';

angular.module('myhealthApp')
    .factory('Person', function ($resource) {
        return $resource('api/persons/:id', {}, {
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
