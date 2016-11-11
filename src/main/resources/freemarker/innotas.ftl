<!DOCTYPE html>
<html lang="en">
<head>
  <title>Bootstrap Example</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script src="\js\reportScripts.js"></script>
  <link rel="stylesheet" type="text/css" href="\css\calendar.css">
</head>
<body>

<div class="container-fluid">

  <#include "/header.ftl">
  <div class="panel panel-primary">
    <div class="panel-heading">Innotas Reporting</div>
        <div class="panel-body">
            <div class="well well-sm">
                <form class="form-inline" method="Get" id="frmInnotasWeek">
                    <div class="form-group">
                        <#assign dt = date[0..3] + "-" + date[4..5] + "-" + date[6..7]>
                        <label for="innotasWeek">Select Week:</label>
                        <input class="form-control"
                           type="date" id="innotasWeek" name="innotasWeek"
                           value="${dt}">
                    </div>
                    <div class="form-group">
                         <button type="button" class="btn btn-default btn-sm"
                                id="btnGetInnotasData"
                                onclick="submitInnotasPage()">
                            <span class="glyphicon glyphicon-refresh"></span>
                         </button>
                    </div>
                </form>
            </div>

            <div class="well well-sm">
              <table class="table table-bordered">
                <thead>
                  <tr>
                    <th>Description</th>
                    <th>Mon</th>
                    <th>Tues</th>
                    <th>Wed</th>
                    <th>Thur</th>
                    <th>Fri</th>
                    <th>Sat</th>
                    <th>Sun</th>
                    <th>Total</th>
                  </tr>
                </thead>
                <tbody id="tblInnotas">
                    <#assign day0 = 0>
                    <#assign day1 = 0>
                    <#assign day2 = 0>
                    <#assign day3 = 0>
                    <#assign day4 = 0>
                    <#assign day5 = 0>
                    <#assign day6 = 0>

                    <#list innotasHours as row>
                        <tr>
                            <td>${row.description}</td>
                            <td>${row.hoursPerDay[0]}</td>
                            <td>${row.hoursPerDay[1]}</td>
                            <td>${row.hoursPerDay[2]}</td>
                            <td>${row.hoursPerDay[3]}</td>
                            <td>${row.hoursPerDay[4]}</td>
                            <td>${row.hoursPerDay[5]}</td>
                            <td>${row.hoursPerDay[6]}</td>
                            <td>${row.hoursPerDay[0] +
                                  row.hoursPerDay[1] +
                                  row.hoursPerDay[2] +
                                  row.hoursPerDay[3] +
                                  row.hoursPerDay[4] +
                                  row.hoursPerDay[5] +
                                  row.hoursPerDay[6]}</td>
                        </tr>
                        <#assign day0 = day0 + row.hoursPerDay[0]>
                        <#assign day1 = day1 + row.hoursPerDay[1]>
                        <#assign day2 = day2 + row.hoursPerDay[2]>
                        <#assign day3 = day3 + row.hoursPerDay[3]>
                        <#assign day4 = day4 + row.hoursPerDay[4]>
                        <#assign day5 = day5 + row.hoursPerDay[5]>
                        <#assign day6 = day6 + row.hoursPerDay[6]>
                    </#list>
                    <#assign weekTotal = day0 + day1 + day2 + day3 + day4 + day5 + day6>
                    <tr>
                        <td>Totals</td>
                        <td>${day0}</td>
                        <td>${day1}</td>
                        <td>${day2}</td>
                        <td>${day3}</td>
                        <td>${day4}</td>
                        <td>${day5}</td>
                        <td>${day6}</td>
                        <td>${weekTotal}</td>
                    </tr>
                </tbody>
              </table>
            </div>
        </div>
    </div>
  </div>
</div>

</body>
</html>

