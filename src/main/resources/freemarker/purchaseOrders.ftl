<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
         <script src="\js\purchaseOrderScripts.js"></script>
        <title>Purchase Orders</title>
    </head>

    <body>
        <#include "/header.ftl">
        <div class="container-fluid">

            <div class="panel-group">

              <#list errors!"">
                  <div class="panel panel-danger">
                      <div class="panel-heading">The Following Errors Occurred</div>
                          <div class="panel-body">
                            <#items as error>
                                <p>${error.errorMessage}</p>
                            </#items>
                          </div>
                      </div>
                  </div>
              </#list>

              <div class="panel panel-primary">
                  <div class="panel-heading">
                       Purchase Order
                  </div>
                  <div class="panel-body">

                    <form class="form-horizontal" method="Post" action="/purchaseOrders">

                        <input type="hidden" id="id" name="id" value="${poDto.id!""}" />

                        <div class="form-group">
                            <label class="control-label col-md-1" for="poDropdown">Select PO:</label>
                            <div class="col-md-5">
                                <select class="form-control" id="poDropdown" name="poDropdown">
                                    <option></option>
                                    <#if poList??>
                                        <#list poList as row>

                                            <#assign txt = row.poNumber + " - " + row.startDate?date + " to " + row.endDate?date>

                                            <#if row.poNumber == currentPoNumber>

                                                <option value="${row.poNumber}" selected>${txt}</option>
                                            <#else>
                                                <option value="${row.poNumber}">${txt}</option>

                                            </#if>
                                        </#list>
                                    </#if>
                                </select>
                            </div>
                            <div class="col-md-1">
                                <input class="btn btn-default" id="btnAddPo" type="button" value="Add New">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-md-1" for="poNumber">PO Number:</label>
                            <div class="col-md-3">
                                <input Type="text" id="poNumber" name="poNumber"
                                    class="form-control" value="${poDto.poNumber!""}">
                            </div>

                            <label class="control-label col-md-2" for="poTitle">PO Title:</label>
                            <div class="col-md-6">
                                <input Type="text" id="poTitle" name="poTitle" class="form-control"
                                        value="${poDto.poTitle!""}">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-md-1" for="poHours">Total Hours:</label>
                            <div class="col-md-3">
                                <input Type="text" id="poHours" name="poHours" class="form-control"
                                    value="${poDto.totalHours!0.0?c}">
                            </div>

                            <label class="control-label col-md-1" for="hourlyRate">Hourly Rate:</label>
                            <div class="col-md-3">
                                <input Type="number" id="hourlyRate" name="hourlyRate" class="form-control"
                                    value="${poDto.hourlyRate!0.0?c}">
                            </div>

                            <label class="control-label col-md-1" for="passthruRate">Passthru Rate:</label>
                            <div class="col-md-3">
                                <input Type="number" id="passthruRate" name="passthruRate" class="form-control"
                                    value="${poDto.passthruRate!0.0?c}">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-md-1" for="startDate">Start Date:</label>
                            <div class="col-md-3">

                                <#if poDto.startDate??>
                                    <input Type="date" id="startDate" name="startDate" class="form-control"
                                    value="${poDto.startDate?date?string.iso}">
                                <#else>
                                    <input Type="date" id="startDate" name="startDate" class="form-control"
                                                                    value="">
                                </#if>
                            </div>

                            <label class="control-label col-md-1" for="endDate">End Date:</label>
                            <div class="col-md-3">
                                <#if poDto.endDate??>
                                    <input Type="date" id="endDate" name="endDate" class="form-control"
                                        value="${poDto.endDate?date?string.iso}">
                                <#else>
                                    <input Type="date" id="endDate" name="endDate" class="form-control"
                                                                        value="">
                                </#if>
                            </div>
                            <div class="col-md-offset-3 col-md-1">
                                <button class="btn btn-default" type="submit">Save</button>
                            </div>
                        </div>
                    </form>

                    <div class="well">
                        <table class="table table-bordered" id="tblPoSummary">
                            <thead>
                               <tr>
                                 <th></th>
                                 <th style="text-align: right">Hours</th>
                                 <th style="text-align: right">Gross</th>
                                 <th style="text-align: right">Passthru</th>
                                 <th style="text-align: right">Adjusted Gross</th>
                               </tr>
                            </thead>

                            <tbody>
                                <tr>
                                    <td><b>Total</b></td>
                                    <td style="text-align: right"><span id="totalHours">${totalHours!0.0}</span></td>
                                    <td style="text-align: right"><span id="totalGross">${(totalGross!0.0)?string.currency}</span></td>
                                    <td style="text-align: right"><span id="totalPassthru">${(totalPassthru!0.0)?string.currency}</span></td>
                                    <td style="text-align: right"><span id="totalAdjustedGross">${(totalAdjustedGross!0.0)?string.currency}</span></td>
                                </tr>
                                <tr>
                                    <td><b>Billed</b></td>
                                    <td style="text-align: right"><span id="billedHours">${billedHours!0.0}</span></td>
                                    <td style="text-align: right"><span id="billedGross">${(billedGross!0.0)?string.currency}</span></td>
                                    <td style="text-align: right"><span id="billedPassthru">${(billedPassthru!0.0)?string.currency}</span></td>
                                    <td style="text-align: right"><span id="billedAdjustedGross">${(billedAdjustedGross!0.0)?string.currency}</span></td>
                                </tr>
                                <tr>
                                    <td><b>Due</b></td>
                                    <td style="text-align: right"><span id="dueHours">${dueHours!0.0}</span></td>
                                    <td style="text-align: right"><span id="dueGross">${(dueGross!0.0)?string.currency}</span></td>
                                    <td style="text-align: right"><span id="duePassthru">${(duePassthru!0.0)?string.currency}</span></td>
                                    <td style="text-align: right"><span id="dueAdjustedGross">${(dueAdjustedGross!0.0)?string.currency}</span></td>
                                </tr>
                                <tr>
                                    <td><b>Remaining</b></td>
                                    <td style="text-align: right"><span id="remHours">${remHours!0.0}</span></td>
                                    <td style="text-align: right"><span id="remGross">${(remGross!0.0)?string.currency}</span></td>
                                    <td style="text-align: right"><span id="remPassthru">${(remPassthru!0.0)?string.currency}</span></td>
                                    <td style="text-align: right"><span id="remAdjustedGross">${(remAdjustedGross!0.0)?string.currency}</span></td>
                                </tr>

                            </tbody>
                        </table>
                    </well>
                  </div>
              </div>
            </div>
            <div class="panel panel-primary">
                  <div class="panel-heading">
                       Invoices
                  </div>
                  <div class="panel-body">
                        <table class="table table-bordered" >
                              <thead>
                                 <tr>
                                   <th>Invoice #</th>
                                   <th>Month/Year</th>
                                   <th style="text-align: right">Start Hrs</th>
                                   <th style="text-align: right">Start Amt</th>
                                   <th style="text-align: right">Billed Hrs</th>
                                   <th style="text-align: right">Billed Amt</th>
                                   <th style="text-align: right">Rem Hrs</th>
                                   <th style="text-align: right">Rem Amt</th>
                                 </tr>
                              </thead>

                              <tbody id="tblInvoiceList">

                                <#if invoices??>
                                    <#list invoices as row>
                                        <tr>
                                          <td>${row.invoiceNumber!""}</td>
                                          <td>${row.monthYearAsString!""}</td>
                                          <td style="text-align: right">${row.priorHoursRemaining}</td>
                                          <td style="text-align: right">${row.priorAmtRemaining?string.currency}</td>
                                          <td style="text-align: right">${row.hours!0.0}</td>
                                          <td style="text-align: right">${row.totalGross?string.currency}</td>
                                          <td style="text-align: right">${row.hoursRemaining!0.0}</td>
                                          <td style="text-align: right">${row.amtRemaining?string.currency}</td>
                                        </tr>
                                    </#list>
                                 </#if>

                              </tbody>
                        </table>
                  </div>
            </div>
        </div>
        <input type="hidden" id="poData" value='${poData!""}'>
    </body>
</html>