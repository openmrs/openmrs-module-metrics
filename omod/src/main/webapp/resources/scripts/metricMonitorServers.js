angular.module('metricMonitorServers', ['ui.grid', 'ui.grid.selection', 'ui.grid.pinning', 'ui.grid.exporter', 'ui.grid.moveColumns', 'ui.grid.resizeColumns', 'ui.bootstrap'])

    .controller('MetricMonitorServersCtrl', ['$scope', '$http', 'uiGridConstants',
        function ($scope, $http, uiGridConstants) {

            $scope.showServerMetricList = false;
            $scope.showServerMetrics = false;
            $scope.servers = [];
            $scope.metricNames = [];
            $scope.metricActionList = ["CREATED", "UPDATED", "RETIRED", "UNRETIRED", "VOIDED", "UNVOIDED", "DELETED"];
            $scope.metricClazzList = [
                {
                    "clazz":"org.openmrs.User",
                    "value":"User"
                },
                {
                    "clazz":"org.openmrs.Person",
                    "value":"Person"
                },
                {
                    "clazz":"org.openmrs.Patient",
                    "value":"Patient"
                },
                {
                    "clazz":"org.openmrs.Encounter",
                    "value":"Encounter"
                },
                {
                    "clazz":"org.openmrs.Obs",
                    "value":"Obs"
                }];
            $scope.metricDateRange = [
                {
                    "range" : "today",
                    "label" : "Today"
                },
                {
                    "range" : "this_week",
                    "label" : "This week"
                },
                {
                    "range" : "last_week",
                    "label" : "Last Week"
                },
                {
                    "range" : "this_month",
                    "label" : "This month"
                },
                {
                    "range" : "last_month",
                    "label" : "Last month"
                },
                {
                    "range" : "this_year",
                    "label" : "This year"
                },
                {
                    "range" : "last_year",
                    "label" : "Last year"
                }];

            $scope.metricModel = {
                selectedMetricAction:$scope.metricActionList[0],
                selectedMetricDateRange:$scope.metricDateRange[0].range,
                selectedMetricClazz:$scope.metricClazzList[0].clazz
            };

            $scope.load = function () {
                $http.get("/ms/SystemMetricServlet?class="+ $scope.metricModel.selectedMetricClazz+"&dateRange="+
                    $scope.metricModel.selectedMetricDateRange+"&action="+$scope.metricModel.selectedMetricAction)
                    .success(function (data) {
                        var metricList = data.metrics;
                        var metricNameList = data.names;
                        var finalList = [];

                        for(var val in data.names){
                            var metricObject = {
                                name :  data.names[val].split(" ")[1],
                                value :  metricList[data.names[val]].value
                            }
                            finalList.push(metricObject);
                        }

                        $scope.metricsFound = finalList;
                        $scope.showServerMetricList = true;
                        $scope.showServerMetrics = true;
                    });
            }
            $scope.load();
            $scope.changeMetric = function(m,n,f) {
                $scope.load();

            };
        }
    ])
