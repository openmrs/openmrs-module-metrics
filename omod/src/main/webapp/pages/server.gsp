<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.13.0.js")
    ui.includeJavascript("uicommons", "angular-ui/ng-grid-2.0.7.min.js")
    ui.includeJavascript("uicommons", "angular-app.js")

    ui.includeJavascript("uicommons", "filters/serverDate.js")
    ui.includeJavascript("metrics", "configEmrMonitorServer.js")

    ui.includeCss("uicommons", "angular-ui/ng-grid.min.css")
%>

<style>
body {
    width: 99%;
    max-width: none;
}
#metric-table th {
    background-color: lightgray;
}
.gridStyle {
    border: 1px solid rgb(212,212,212);
    width: 200px;
    height: 600px;
}
#report-history-table {
    width: 100%;
}
#report-history-table td {
    vertical-align: top;
}
.selected {
    background-color: darkslategray;
    color: white;
}
.ngRowCountPicker {
    display: none;
}
.ngFooterTotalItems {
    display:none;
}
.ngPagerButton {
    width: 15px;
    padding: 2px 2px 2px 6px;
    text-align: center;
    vertical-align: middle;
}
.ngPagerCurrent {
    width:20px;
    padding: 2px;
}
</style>

${ ui.includeFragment("metrics", "menu") }

<div id="config-metric-server" class="container" ng-controller="ConfigMetricMonitorCtrl">

    <div ng-show="showServerMetricList">

        <h3>Monitored Server Metric List</h3>

        <form>
            <div class="form-group" style="display:table-row">
                <i style="display:table-cell; padding-right:10px;" class="fa fa-search"></i>
                <span style="display:table-cell">
                    <input type="text" placeholder="Search Metric" ng-model="searchMetric" size="50">
                </span>
            </div>
        </form>

        <br/>

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
                        Type
                    </a>
                </th>
                <th>
                    <a href="#">
                        Latest Report
                    </a>
                </th>
            </tr>
            </thead>
            <tr ng-repeat="server in metricsFound | filter:searchMetric">
                <td>{{ server.name }}</td>
                <td>{{ server.type }}</td>
                <td>{{ server.value }}</td>
            </tr>
        </tbody>

        </table>
    </div>



</div>

<script type="text/javascript">
    angular.bootstrap('#config-metric-server', [ 'ConfigMetricMonitor' ]);
</script>
