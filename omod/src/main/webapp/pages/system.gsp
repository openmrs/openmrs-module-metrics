<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")
    ui.includeJavascript("uicommons", "moment.js")
    ui.includeJavascript("metrics", "metricMonitorServers.js")

    ui.includeJavascript("metrics", "ui-grid.js")
    ui.includeCss("metrics", "ui-grid.css")

%>

<style>
    body {
        width: 99%;
        max-width: none;
    }
</style>

${ ui.includeFragment("metrics", "menu") }

<h3>Metrics</h3>
<div id="metric-monitor-servers-app" class="container" ng-controller="MetricMonitorServersCtrl">

    Select Event Action:

    <select ng-model="metricModel.selectedMetricAction" ng-change="changeMetric(metricModel.selectedMetricAction,
    metricModel.selectedMetricDateRange, metricModel.selectedMetricClazz)">
        <option ng-selected="{{metricModel.selectedMetricAction == metric}}" ng-repeat="metric in metricActionList" value="{{ metric }}">
            {{ metric }}
        </option>
    </select>

    Select Event Date Range:

    <select ng-model="metricModel.selectedMetricDateRange" ng-change="changeMetric(metricModel.selectedMetricAction,
    metricModel.selectedMetricDateRange, metricModel.selectedMetricClazz)">
        <option ng-selected="{{metricModel.selectedMetricDateRange == metric.range}}"  ng-repeat="metric in metricDateRange" value="{{ metric.range }}">
            {{ metric.label }}
        </option>
    </select>

    Select Event Clazz:

    <select ng-model="metricModel.selectedMetricClazz" ng-change="changeMetric(metricModel.selectedMetricAction,
    metricModel.selectedMetricDateRange, metricModel.selectedMetricClazz)">
        <option ng-selected="{{metricModel.selectedMetricClazz == metric.clazz}}" ng-repeat="metric in metricClazzList" value="{{ metric.clazz }}">
            {{ metric.value }}
        </option>
    </select>

    <br/>
    <br/>

    <div ng-show="showServerMetricList">
        <table id="metric-table" class="table table-bordered table-striped">
            <tbody>
            <thead>
            <tr>
                <th>
                    <a href="#">
                        Name
                    </a>
                </th>
                <th>
                    <a href="#">
                        Value
                    </a>
                </th>
            </tr>
            </thead>
            <tr ng-repeat="server in metricsFound">
                <td>{{ server.name }}</td>
                <td>{{ server.value }}</td>
            </tr>
        </tbody>
        </table>
    </div>

 </div>

<script type="text/javascript">
    angular.bootstrap('#metric-monitor-servers-app', [ 'metricMonitorServers' ]);
</script>
