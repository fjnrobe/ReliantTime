<!DOCTYPE html>
<html lang="en">
<head>
  <title>Financial Summary</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
   <script src="\js\financialSummaryScripts.js"></script>
</head>
<body>

 <#include "/header.ftl">
 <div class="container-fluid">

    <div class="panel-group">

        <#include "/errors.ftl">
        <div class="panel panel-primary">
            <div class="panel-heading">
                Revenue
            </div>
            <div class="panel-body">

                <div class="well">
                    <div class="row">
                        <div class="form-group">
                        <label class="control-label col-md-1" for="summaryYear">Year:</label>
                        <div class="col-md-2">
                            <select class="form-control" id="summaryYear" name="summaryYear">
                                <#list years as year>
                                    <#if currentYear == year>
                                        <option value="${year}" selected>${year}</option>
                                    <#else>
                                        <option value="${year}">${year}</option>
                                    </#if>
                                </#list>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <a href="#summary">View Net Income</a>
                        </div>
                    </div>
                </div>

                <div class="well">
                  <table class="table table-bordered" >
                    <tbody>
                        <tr>
                            <td></td>
                            <td style="text-align: right"><b>Hours</b></td>
                            <td style="text-align: right"><b>Revenue</b></td>
                        </tr>
                        <tr>
                            <td><b>Billed:</b></td>
                            <td style="text-align: right"><span id="billedHours">${summaryDto.billedHours}</span></td>
                            <td style="text-align: right"><span id="billedGross">${summaryDto.billedGross?string.currency}</span></td>
                        </tr>
                        <tr>
                            <td><b>Received:</b></td>
                            <td style="text-align: right"><span id="receivedHours">${summaryDto.receivedHours}</span></td>
                            <td style="text-align: right"><span id="receivedGross">${summaryDto.receivedGross?string.currency}</span></td>
                        </tr>
                        <tr>
                            <td><b>Due:</b></td>
                            <td style="text-align: right"><span id="dueHours">${summaryDto.dueHours}</span></td>
                            <td style="text-align: right"><span id="dueGross">${summaryDto.dueGross?string.currency}</span></td>
                        </tr>
                    </tbody>
                  </table>
                </div>
            </div>
        </div>

        <div class="panel panel-primary">
            <div class="panel-heading">
                Expenses
            </div>
            <div class="panel-body">

                <div class="well">

                    <table class="table table-bordered" id="tblExpense">
                         <thead>
                            <tr>
                                <th>Category</th>
                                <th>Type</th>
                                <th style="text-align: right">Amount</th>
                            </tr>
                         </thead>
                         <tbody>
                            <#if summaryDto.expenseList??>
                                <#assign totDeductions = 0>

                                 <#list summaryDto.expenseList as row>
                                    <tr>
                                        <td>${row.deductionCategoryDesc}</td>
                                        <td>${row.deductionTypeDesc}</td>
                                        <td style="text-align: right">${row.deductionDto.amount?string.currency}</td>
                                    </tr>
                                    <#assign totDeductions = totDeductions + row.deductionDto.amount>
                                 </#list>

                                 <tfoot>
                                     <tr>
                                        <td><b>Total Expenses</b></td>
                                        <td></td>
                                        <td style="text-align: right">
                                            <span id="totDeductions">
                                                <b>${totDeductions?string.currency}</b>
                                            </span>
                                        </td>
                                     </tr>
                                 </tfoot>
                            </#if>
                         </tbody>

                    </table>

                </div>
            </div>
        </div>

        <div class="panel panel-primary">
            <div class="panel-heading">
                 <a name="summary" style="color: white">Net Income</a>
            </div>
            <div class="panel-body">
                <div class="well">
                    <div class="row">
                        <table class="table table-bordered" >
                            <tr>
                                <#assign netIncome = summaryDto.billedGross - totDeductions>
                                <td><b>Net Income: </b></td>
                                <td><b><span id="netIncome">${netIncome?string.currency}</span></b></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div class="panel panel-primary">
            <div class="panel-heading">
                 Summary By Year
            </div>
            <div class="panel-body">
                <div class="well">
                    <div class="row">
                        <table class="table table-bordered" >

                           <#assign totGross = 0>
                           <#assign totHours = 0>
                           <#assign totDed = 0>
                           <#assign totNet = 0>
                           <#assign totYears = 0>
                            <tr>
                                <th>Year</th>
                                <th style="text-align: right">Total Hours</th>
                                <th style="text-align: right">Gross Income</th>
                                <th style="text-align: right">Total Deductions</th>
                                <th style="text-align: right">Net Income</th>
                            </tr>

                            <#list summaryDto.summaryByYear as row>
                               <tr>
                                   <td>${row.year?string}</td>
                                   <td style="text-align: right">${row.grossHours}</td>
                                   <td style="text-align: right">${row.grossIncome?string.currency}</td>
                                   <td style="text-align: right">${row.grossDeductions?string.currency}</td>
                                   <td style="text-align: right">${row.netIncome?string.currency}</td>
                               </tr>
                               <#assign totHours = totHours + row.grossHours>
                               <#assign totGross = totGross + row.grossIncome>
                               <#assign totDed = totDed + row.grossDeductions>
                               <#assign totNet = totNet + row.netIncome>
                               <#assign totYears = row?counter>
                            </#list>

                           <tr>
                               <td><b>Total</b></td>
                               <td style="text-align: right"><b>${totHours}</b></td>
                               <td style="text-align: right"><b>${totGross?string.currency}</b></td>
                               <td style="text-align: right"><b>${totDed?string.currency}</b></td>
                               <td style="text-align: right"><b>${totNet?string.currency}</b></td>
                           </tr>
                           <tr>
                               <td><b>Average</b></td>
                               <td style="text-align: right"><b>${totHours / totYears}</b></td>
                               <td style="text-align: right"><b>${(totGross / totYears)?string.currency}</b></td>
                               <td style="text-align: right"><b>${(totDed / totYears)?string.currency}</b></td>
                               <td style="text-align: right"><b>${(totNet / totYears)?string.currency}</b></td>
                           </tr>


                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
 </div>

</body>
</html>

