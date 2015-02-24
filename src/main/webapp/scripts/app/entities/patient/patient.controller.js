'use strict';

angular.module('myhealthApp')
    .controller('PatientController', function ($scope, Patient) {
        $scope.patients = [];
        $scope.loadAll = function() {
            Patient.query(function(result) {
               $scope.patients = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Patient.save($scope.patient,
                function () {
                    $scope.loadAll();
                    $('#savePatientModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.patient = Patient.get({id: id});
            $('#savePatientModal').modal('show');
        };

        $scope.delete = function (id) {
            $scope.patient = Patient.get({id: id});
            $('#deletePatientConfirmation').modal('show');
        };

        $scope.confirmDelete = function (id) {
            Patient.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePatientConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.patient = {firstName: null, lastName: null, id: null};
        };
    });

