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
</head>
<body>

<div class="container-fluid">

  <#include "/header.ftl">
  <div class="panel panel-primary">
    <div class="panel-heading">Monthly Status Report</div>
        <div class="panel-body">
            <div class="well well-sm">
                <form class="form-inline" method="Get" id="frmMonthlyStatus">
                     <div class="col-md-3">
                          <select class="form-control" id="monthYears" name="monthYears">
                              <#list monthYears as monthYear>
                                    <option value=${monthYear.sortKey}>${monthYear.monthName} ${monthYear.yearName}</option>
                              </#list>
                          </select>
                    </div>
                    <div class="form-group">
                         <button type="button" class="btn btn-default btn-sm"
                                id="btnGetInnotasData"
                                onclick="getMonthlyStatus()">
                            Get Report
                         </button>
                    </div>


                </form>
            </div>
        </div>
    </div>
  </div>
</div>

</body>
</html>

