angular.module('ConfigMetricMonitor', [ "uicommons.filters", "ui.bootstrap", 'ngGrid' ])

    .controller('ConfigMetricMonitorCtrl', [ '$scope', '$http', "$q",
        function($scope, $http, $q) {

            $scope.searchMetric   = '';    // set the default search/filter term
            $scope.showServerMetricList = false;
            $scope.showServerMetrics = false;
            $scope.reportHistoryData = [];
            $scope.selectedServer = null;
            $scope.selectedReport = null;
            $scope.pagingInformation = '';
            $scope.showResultsSpinner = true;
            $scope.listServers =  function(){
                $http.get("/ms/ServerMetricServlet")
                    .success(function(data) {
                        var metricList = data.metrics;
                        var metricNameList = data.names;
                        var finalList = [];

                        for(var val in data.names){
                            var metricObject = {
                                name : data.names[val],
                                value :  metricList[data.names[val]].value,
                                type :  data.names[val].split(".")[1]
                            }
                            finalList.push(metricObject);
                        }

                        $scope.metricsFound = finalList;
                        $scope.showServerMetricList = true;
                        $scope.showServerMetrics = true;
                    });
            }

            $scope.listServers();

            $scope.totalReportHistoryItems = 0;

            $scope.filterOptions = {
                filterText: "",
                useExternalFilter: true
            };

        }])
