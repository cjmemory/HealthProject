'use strict';

angular.module('myhealthApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('person', {
                parent: 'entity',
                url: '/person',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/person/persons.html',
                        controller: 'PersonController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('person');
                        return $translate.refresh();
                    }]
                }
            })
            .state('personDetail', {
                parent: 'entity',
                url: '/person/:id',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/person/person-detail.html',
                        controller: 'PersonDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('person');
                        return $translate.refresh();
                    }]
                }
            });
    });
