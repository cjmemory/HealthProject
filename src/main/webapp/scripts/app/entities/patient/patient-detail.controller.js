'use strict';

angular.module('myhealthApp')
    .controller('PatientDetailController', function ($scope, $stateParams, Patient) {

        $scope.path = "/root";
        $scope.file ={};

        $scope.patient = {};
        $scope.load = function (id) {
            Patient.get({id: id}, function(result) {
              $scope.patient = result;
            });
        };
        $scope.load($stateParams.id);



        $scope.createFolder = function (newFolderName) {
            $scope.file.newFolderName = newFolderName;
            $scope.file.path = $scope.path;

            Patient.save($scope.file);


        };
    });
