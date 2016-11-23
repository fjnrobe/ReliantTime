<!DOCTYPE html>
<html lang="en">
<head>
  <title>Revenue</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script src="\js\utils.js"></script>
   <script src="\js\receiptsScripts.js"></script>
</head>
<body>

 <#include "/header.ftl">
 <div class="container-fluid">

    <div class="panel-group">

        <#include "/errors.ftl">
        <div class="panel panel-primary">
            <div class="panel-heading">
                Edit Invoices
            </div>
            <div class="panel-body">

                <div class="well">
                    <div class="form-group">
                       <label class="form-label col-md-1" for="dropYears">Year:</label>
                       <div class="col-md-4">
                           <select class="form-control" id="dropYears" name="dropYears">
                              <#if yearList??>
                                  <#list yearList as year>
                                      <#if (selectedYear!"") == year>
                                          <option value="${year}" selected>${year}</option>
                                      <#else>
                                        <option value="${year}">${year}</option>
                                      </#if>
                                  </#list>
                              </#if>
                           </select>
                      </div>

                      <input class="btn btn-default" id="btnInvoice" type="button" value="Add New"
                      onclick="addRevenueEntry()">

                      <a href="#summary">View Summary</a>
                    </div>
                  <table class="table table-bordered" >
                    <thead>
                      <tr>
                        <th>Month/Year</th>
                        <th>PO #</th>
                        <th>Invoice #</th>
                        <th>Total Hours</th>
                        <th>Gross Revenue</th>
                        <th>Invoice Date</th>
                        <th>Received Date</th>
                      </tr>
                    </thead>
                    <tbody  id="tblRevenueList">
                        <#if revenueList??>
                            <#list revenueList as row>
                                <tr>
                                    <td>${row.monthYear.sortKey}</td>
                                    <td>${row.poNumber}</td>
                                    <td>${row.invoiceNumber}</td>
                                    <td style="text-align: right">${row.hours}</td>
                                     <td style="text-align: right">${row.totalGross?string.currency}</td>

                                    <td>${row.invoiceDate?date}</td>
                                    <td>${row.receivedDate?date}</td>
                                    <td><a id="lnkEdit&${row?index}" onclick="editRevenueEntry(this)">
                                        <span class="glyphicon glyphicon-pencil"></span>
                                    </td>
                                    <td><a id="lnkDelete&${row?index}" onclick="deleteRevenueEntry(this)">
                                        <span class="glyphicon glyphicon-remove-sign"></span>
                                    </td>
                                    <td>
                                      <a id="lnkDownLoad&${row?index}"
                                         href="/reports/invoice/${row.invoiceNumber}">
                                         <span class="glyphicon glyphicon-download"></span>
                                    </td>
                            </#list>
                        </#if>
                    </tbody>
                  </table>
                   <input type="hidden" id="revenueListData" value='${revenueListData!""}'>
                </div>

                <#if revenueSummary??>
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            <a name="summary">Invoice Summary</a>
                        </div>
                        <div class="panel-body">
                            <div class="well">
                             <table class="table table-bordered" >
                                 <thead>
                                   <tr>
                                     <th>Year</th>
                                     <th style="text-align: right">Gross Revenue</th>
                                     <th style="text-align: right">Received Revenue</th>

                                   </tr>
                                 </thead>
                                 <tbody  id="tblRevenueSummary">
                                     <#if revenueSummary??>
                                         <#assign totGrossRevenue = 0>
                                         <#assign totRecvRevenue = 0>
                                         <#list revenueSummary as row>
                                             <tr>

                                                <td>${row.monthYear.sortKey}</td>
                                                <td style="text-align: right">${row.totalGross?string.currency}</td>
                                                <td style="text-align: right">${row.totalRecvGross?string.currency}</td>
                                              </tr>

                                             <#assign totGrossRevenue = totGrossRevenue + row.totalGross>
                                             <#assign totRecvRevenue = totRecvRevenue + row.totalRecvGross>


                                         </#list>
                                         <tfoot>
                                             <tr>
                                                <td><b>All Years</b></td>
                                                <td style="text-align: right"><b>${totGrossRevenue?string.currency}</b></td>
                                                <td style="text-align: right"><b>${totRecvRevenue?string.currency}</b></td>

                                             </tr>
                                         </tfoot>
                                     </#if>
                                 </tbody>
                            </div>
                        </div>
                    </div>
                </#if>
            </div>
        </div>
    </div>
 </div>

 <!-- Edit Modal -->
 <div id="editEntryModal" class="modal" role="dialog">
   <div class="modal-dialog modal-md">

     <!-- Modal content-->
     <div class="modal-content">

         <div class="modal-header">
             <button type="button" class="close" data-dismiss="modal">&times;</button>
             <h4 class="modal-title">Add/Edit Revenue</h4>
         </div>
         <div class="modal-body">
             <form  class="form-horizontal"
                    id="editRevenueForm"
                    method="Post" action="/revenue/update">

                    <input type="hidden" id="id" name="id" value="${id!""}">

                      <div class="form-group">
                        <label class="control-label col-md-3" for="poNumber">PO Number</label>
                        <div class="col-md-9">
                            <input class="form-control" type="text" id="poNumber" name="poNumber" required>
                        </div>
                      </div>
                      <div class="form-group">
                          <label class="control-label col-md-3" for="invoiceNumber">Invoice Number</label>
                          <div class="col-md-9">
                              <input class="form-control" type="text" id="invoiceNumber" name="invoiceNumber" required>
                          </div>
                      </div>
                      <div class="form-group">
                          <label class="control-label col-md-3" for="monthYear">Month / Year:</label>
                          <div class="col-md-9">
                              <input class="form-control"
                                 type="month" id="monthYear" name="monthYear" required>
                          </div>
                      </div>

                      <div class="form-group">
                          <label class="control-label col-md-3" for="hours">Hours:</label>
                          <div class="col-md-9">
                              <input class="form-control"
                                 type="number" min="0" step=".01" required id="hours" name="hours" required>
                          </div>
                      </div>
                      <div class="form-group">
                          <label class="control-label col-md-3" for="gross">Gross:</label>
                          <div class="col-md-9">
                              <input class="form-control"
                                 type="number" min="0" step=".01" required id="gross" name="gross" required>
                          </div>
                      </div>
                      <div class="form-group">
                          <label class="control-label col-md-3" for="invDate">Invoice Date:</label>
                          <div class="col-md-9">
                          <input class="form-control"
                             type="date" id="invDate" name="invDate" required>
                          </div>
                      </div>
                      <div class="form-group">
                        <label class="control-label col-md-3" for="recvDate">Received Date:</label>
                        <div class="col-md-9">
                            <input class="form-control"
                             type="date" id="recvDate" name="recvDate" >
                        </div>
                      </div>

                 <button type="submit" class="btn btn-primary">Save</button>

             </form>
         </div>
         <div class="modal-footer">
            <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Cancel</button>
         </div>
     </div>
   </div>
 </div>

 <!-- Delete Modal -->
  <div id="deleteEntryModal" class="modal" role="dialog">
    <div class="modal-dialog modal-md">

      <!-- Modal content-->
      <div class="modal-content">

          <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal">&times;</button>
              <h4 class="modal-title">Confirm Delete</h4>
          </div>
          <div class="modal-body">
              <form
                     id="deleteRevenueForm"
                     method="Post" action="/revenue/delete">

                  <input type="hidden" id="delId" name="delId">

                  <p id="confirmDeleteLabel">Are you sure you want to delete</p>
                  <button type="submit" class="btn btn-primary">Delete</button>

              </form>
          </div>
          <div class="modal-footer">
             <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Cancel</button>
          </div>
      </div>
    </div>
  </div>

</body>
</html>

