<!DOCTYPE html>
<html lang="en">
<head>
    <title>Reliant Software LLC</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="\js\reportScripts.js"></script>
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs-3.3.7/jqc-1.12.4/dt-1.10.15/b-1.3.1/datatables.min.css"/>
    <script type="text/javascript" src="https://cdn.datatables.net/v/bs/jqc-1.12.4/dt-1.10.15/datatables.min.js"></script>
</head>
<body>

<div class="container-fluid">

    <#include "/header.ftl"/>
    <div class="panel panel-primary">
        <div class="panel-heading">Activity Summary By Date Range</div>
        <div class="panel-body">
            <div class="well well-sm">
                <form class="form-inline" method="Post" id="frmActivitySummary" action="/reports/activitySummary">
                    <#assign aDateTime = .now>

                    <div class="form-group">
                            <label for="fromDate">From Date:</label>
                            <input class="form-control"
                                   type="date" id="fromDate" name="fromDate"
                                   value="${startDate!aDateTime?iso_utc}">
                    </div>
                    <div class="form-group">
                        <label for="toDate">To Date:</label>
                        <input class="form-control"
                               type="date" id="toDate" name="toDate"
                               value="${endDate!aDateTime?iso_utc}">
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-default btn-sm"
                                id="btnGetActivitySummary">
                            <span class="glyphicon glyphicon-refresh"></span>
                        </button>
                    </div>
                </form>
            </div>

            <div class="well">
                <table id="tblSubprocessActivity" class="table table-bordered"  >
                    <thead>
                    <tr>
                        <th>Subprocess / Activity</th>
                        <th>Activity Type</th>
                        <th>Hours</th>
                    </tr>
                    </thead>
                    <tbody  id="tblSubprocessActivityList">
                    <#if summarySubProcessActivityList??>
                        <#list summarySubProcessActivityList as row>
                            <tr>
                                <td>${row.subprocessDesc}</td>
                                <td>${row.activityDesc}</td>
                                <td>${row.hours}</td>
                            </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>

            <div class="well">
                <table id="tblActivity" class="table table-bordered" >
                    <thead>
                    <tr>
                        <th>Activity Type</th>
                        <th>Hours</th>
                    </tr>
                    </thead>
                    <tbody  id="tblActivityList">
                    <#if summaryActivityList??>
                        <#assign sDate = startDate[0..3] + startDate[5..6] + startDate[8..9]>
                        <#assign eDate = endDate[0..3] + endDate[5..6] + endDate[8..9]>

                        <#list summaryActivityList as row>
                            <tr>
                                <td><a href="/reports/activityList/${row.activityDesc}/${sDate}/${eDate}">${row.activityDesc}</a></td>
                                <td>${row.hours}</td>
                            </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>

        </div>
    </div>
</div>
</div>

</body>
</html>

