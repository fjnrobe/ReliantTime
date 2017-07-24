<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript" src="https://cdn.datatables.net/v/bs/jqc-1.12.4/dt-1.10.15/datatables.min.js"></script>

    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs-3.3.7/jqc-1.12.4/dt-1.10.15/b-1.3.1/datatables.min.css"/>

    <title>Activity List</title>
</head>

<script>

        $(document).ready(function(){


             $('[data-toggle="popover"]').popover();
        });

    </script>
<body>

<div class="container-fluid">

    <div class="panel-group">

        <div class="panel panel-primary">
            <div class="panel-heading">
                Log Detail
            </div>
            <div class="panel-body">
                <form class="form-inline">

                    <div class="well">
                        <table class="table">
                            <td>
                                Activity Type:&nbsp;${activityDesc}
                            </td>
                            <td>
                                Date Range:&nbsp;
                                ${fromDate[4..5]}/${fromDate[6..7]}/${fromDate[0..3]} to
                                ${toDate[4..5]}/${toDate[6..7]}/${toDate[0..3]}
                            </td>
                            <td>
                                Total Hours:&nbsp;${totHours}
                            </td>
                        </table>
                    </div>

                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            Logs
                        </div>
                        <div class="panel-body">
                            <div class="well well-sm">
                                <table id="tblLogList" class="table table-bordered">
                                    <thead>
                                    <tr>
                                        <th>SubProcess</th>
                                        <th>Date</th>
                                        <th>Start Time</th>
                                        <th>End Time</th>
                                        <th style="text-align: right">Hours</th>
                                        <th>Note</th>
                                        <th>NickName</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <#if logList??>
                                        <#list logList as row>
                                            <tr>
                                                <td>${row.sirPcrDto.subProcessDesc}</td>
                                                <td>${row.logDto.logDate[4..5]}/${row.logDto.logDate[6..7]}/${row.logDto.logDate[0..3]}</td>
                                                <td>${row.logDto.startTime[0..1]}:${row.logDto.startTime[2..3]}</td>
                                                <td>${row.logDto.endTime[0..1]}:${row.logDto.endTime[2..3]}</td>
                                                <td style="text-align: right">${row.logDto.hours}</td>
                                                <td>${row.logDto.note}</td>
                                                <td>
                                                    <a href="#" data-toggle="popover" title="${row.sirPcrDto.nickName}"
                                                       data-placement="left"
                                                       data-trigger="hover"
                                                       data-content="${row.sirPcrDto.sirDesc}">${row.sirPcrDto.nickName}</a>
                                                </td>
                                            </tr>
                                        </#list>
                                    </#if>
                                </table>
                            </div>
                        </div>
                    </div>

                </form>
            </div>

        </div>
    </div>
</div>
</body>
</html>