<!DOCTYPE html>
<html lang="en">
<head>
  <title>Deductions</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
   <script src="\js\deductionsScripts.js"></script>
</head>
<body>

 <#include "/header.ftl"/>
 <div class="container-fluid">

    <div class="panel-group">

        <#include "/errors.ftl">
        <div class="panel panel-primary">
            <div class="panel-heading">
                Edit Deductions
            </div>
            <div class="panel-body">
                <form class="form-horizontal"
                       id="frmDeduction"
                       method="Post"
                       action="/deductions">
                    <div class="well">
                        <div class="row">
                            <div class="form-group col-md-4">
                                <label class="control-label" for="deductionCategory">Deduction Category:</label>
                                <select class="form-control" id="deductionCategory" name="deductionCategory">
                                    <option value=""></option>
                                    <#list deductionCategoryList as entry>
                                        <#if entry.deductionCode == defaultCategoryCode!"">
                                            <option value="${entry.deductionCode}" selected>${entry.lovDescription}</option>
                                        <#else>
                                            <option value="${entry.deductionCode}">${entry.lovDescription}</option>
                                        </#if>
                                    </#list>
                                </select>
                            </div>

                            <div class="form-group col-md-4">
                                <label class="control-label" for="deductionType">Deduction Type:</label>
                                <select class="form-control" id="deductionType" name="deductionType">
                                    <option value=""></option>
                                    <#list deductionTypeList as entry>
                                        <#if entry.deductionTypeCode == defaultDeductionType!"">
                                            <option value="${entry.deductionTypeCode}" selected>${entry.lovDescription}</option>
                                        <#else>
                                            <option value="${entry.deductionTypeCode}">${entry.lovDescription}</option>
                                        </#if>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="form-group col-md-4">
                                <label class="control-label" for="startDate">Start Date:</label>

                                 <input class="form-control"
                                        type="date" id="startDate" name="startDate" value="${defaultStartDate!""}">
                            </div>
                             <div class="form-group col-md-4">
                                 <label class="control-label" for "endDate"> to </label>

                                 <input class="form-control"
                                        type="date" id="endDate" name="endDate" value="${defaultEndDate!""}">
                            </div>
                        </div>

                        <div class="row">
                            <div class="form-group col-md-2">
                                <label class="control-label" for "btnGetDeductions"></label>
                                 <button type="submit" class="btn btn-default btn-sm"
                                        id="btnGetDeductions">
                                    <span class="glyphicon glyphicon-refresh"></span>
                                 </button>
                            </div>
                            <div class="form-group col-md-offset-9 col-md-2">
                                <a href="#summary">View Summary</a>
                            </div>
                        </div>
                    </div>
                </form>

                <div class="well">
                  <button class="btn btn-sm" id="btnAddDeduction"
                           onclick="addDeductionEntry()">Add Deduction
                                            <span class="glyphicon glyphicon-plus">
                                           </span>
                  </button>
                  <table class="table table-bordered" >
                    <thead>
                      <tr>
                        <th>Category</th>
                        <th>Type</th>
                        <th>Post Date</th>
                        <th>Amount</th>
                        <th>Note</th>
                      </tr>
                    </thead>
                    <tbody  id="tblDeductionList">
                        <#if deductionList??>
                            <#list deductionList as row>
                                <tr>
                                    <td>${row.deductionCategoryDesc}</td>
                                    <td>${row.deductionTypeDesc}</td>
                                    <td>${row.deductionDto.postDate?date}</td>
                                    <td style="text-align: right">${row.deductionDto.amount?string.currency}</td>
                                    <td>${row.deductionDto.note!""}</td>
                                    <td><a id="lnkEdit&${row?index}" onclick="editDeductionEntry(this)">
                                        <span class="glyphicon glyphicon-pencil"></span>
                                    </td>
                                    <td><a id="lnkDelete&${row?index}" onclick="deleteDeductionEntry(this)">
                                        <span class="glyphicon glyphicon-remove-sign"></span>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                    </tbody>
                  </table>
                  <input type="hidden" id="deductionListData" value='${deductionListData!""}'>
                </div>

                <#if deductionSummary??>
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            <a name="summary">Deduction Summary</a>
                        </div>
                        <div class="panel-body">
                            <div class="well">
                             <table class="table table-bordered" >
                                 <thead>
                                   <tr>
                                     <th>Category</th>
                                     <th>Type</th>
                                     <th>Year</th>
                                     <th>Amount</th>
                                   </tr>
                                 </thead>
                                 <tbody  id="tblDeductionSummary">
                                     <#if deductionSummary??>
                                         <#assign totDeductions = 0>
                                         <#list deductionSummary as row>
                                             <tr>
                                                 <td>${row.deductionCategoryDesc}</td>
                                                 <td>${row.deductionTypeDesc}</td>
                                                 <td>${row.deductionDto.postDate?date}</td>
                                                 <td style="text-align: right">${row.deductionDto.amount?string.currency}</td>
                                             </tr>
                                             <#assign totDeductions = totDeductions + row.deductionDto.amount>
                                         </#list>
                                         <tfoot>
                                             <tr>
                                                <td><b>All Categories</b></td>
                                                <td><b>All Types</b></td>
                                                <td><b>All Years</b></td>
                                                <td style="text-align: right"><b>${totDeductions?string.currency}</b></td>
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
             <h4 class="modal-title">Add/Edit Deduction</h4>
         </div>
         <div class="modal-body">
             <form  class="form-horizontal"
                    id="editDeductionForm"
                    method="Post" action="/deductions/update">

                    <input type="hidden" id="deductionId" name="deductionId" value="${deductionId!""}">


                     <div class="form-group">

                        <label class="control-label col-md-3" for="deductionCategoryCode">Category:</label>
                        <div class="col-md-9">
                             <select class="form-control" id="deductionCategoryCode" name="deductionCategoryCode">

                                <#list deductionCategoryList as entry>
                                    <#if entry == defaultCategory!"">
                                        <option value="${entry.deductionCode}" selected>${entry.lovDescription}</option>
                                    <#else>
                                        <option value="${entry.deductionCode}">${entry.lovDescription}</option>
                                    </#if>
                                </#list>
                            </select>
                        </div>
                     </div>


                     <div class="form-group">
                         <label class="control-label col-md-3" for="deductionCategoryType">Type:</label>
                         <div class="col-md-9">
                              <select class="form-control" id="deductionCategoryType" name="deductionCategoryType">

                                 <#list deductionTypeList as entry>
                                     <#if entry == defaultTypeCode!"">
                                         <option value="${entry.deductionTypeCode}" selected>${entry.lovDescription}</option>
                                     <#else>
                                         <option value="${entry.deductionTypeCode}">${entry.lovDescription}</option>
                                     </#if>
                                 </#list>
                             </select>
                         </div>
                      </div>

                      <div class="form-group">

                          <label class="control-label col-md-3" for="postDate">Date Posted:</label>
                          <div class="col-md-9">
                              <input class="form-control"
                                 type="date" id="postDate" name="postDate" required>
                          </div>
                      </div>

                      <div class="form-group">
                          <label class="control-label col-md-3" for="amount">Amount:</label>
                          <div class="col-md-9">
                              <input class="form-control"
                                 type="number" min="0" step=".01" required id="amount" name="amount" required>
                          </div>
                      </div>

                      <div class="form-group">
                          <label class="control-label col-md-3" for="note">Note:</label>
                          <div class="col-md-9">
                              <input class="form-control"
                                 type="text" id="note" name="note">
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
                     id="deleteDeductionForm"
                     method="Post" action="/deductions/delete">

                  <input type="hidden" id="delDeductionId" name="delDeductionId">

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

