<!DOCTYPE html>
<html lang="en">
<head>
  <title>Search Sirs</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
   <script src="\js\sirListScripts.js"></script>
  <link rel="stylesheet" type="text/css" href="\css\calendar.css">
</head>
<body>

<div class="container-fluid">

  <#include "/header.ftl">
  <div class="panel panel-primary">
    <div class="panel-heading">Search for SIRs</div>
        <div class="panel-body">
            <form class="form-inline" method="post" id="frmSearchSirs" action="/sirList">
                <div class="well">
                    <input type="hidden" id="action" name="action">
                    <input type="hidden" id="sirCount" name="sirCount" value="${sirCount!0}">

                    <div class="row">
                        <div class="form-group col-md-3">
                           <label class="control-label" for="sirStatus">Status:</label>

                           <select class="form-control" id="sirStatus" name="sirStatus">
                               <option value="All" <#if (sirStatus!"") == "All">selected</#if>>All</option>
                               <option value="Open" <#if (sirStatus!"") == "Open">selected</#if>>Open</option>
                               <option value="Closed" <#if (sirStatus!"") == "Closed">selected</#if>>Closed</option>
                           </select>

                        </div>
                        <div class="form-group col-md-3">
                           <label class="control-label" for="sirType">SIR Type:</label>

                           <select class="form-control" id="sirType" name="sirType">
                                <option value="ALL" <#if (sirType!"") == "ALL">selected</#if>>ALL</option>
                               <option value="SIR" <#if (sirType!"") == "SIR">selected</#if>>SIR</option>
                               <option value="PCR" <#if (sirType!"") == "PCR">selected</#if>>PCR</option>
                               <option value="OTHER" <#if (sirType!"") == "OTHER">selected</#if>>OTHER</option>
                           </select>

                        </div>
                        <div class="form-group col-md-4">
                            <label class="control-label" for="sirNumber">Number:</label>

                            <Input type="text" class="form-control" id="sirNumber" name="sirNumber" value="${sirNumber!""}">

                        </div>
                        <div class="form-group col-md-2">
                             <button type="submit" class="btn btn-default btn-sm" id="btnGetSirList">
                                Search
                                <span class="glyphicon glyphicon-refresh"></span>
                             </button>
                        </div>
                    </div>
                </div>

                <div class="well well-sm">
                  <table class="table table-bordered">
                    <thead>
                      <tr>
                        <th>Close ?</th>
                        <th>SIR Type</th>
                        <th>SIR Number</th>
                        <th>Nick Name</th>
                        <th>Description</th>
                        <th>Closed ?</th>
                      </tr>
                    </thead>
                    <tbody id="tblsirList">

                        <#if sirList??>
                            <div class="form-group">
                                <#list sirList as row>
                                    <tr>
                                        <td>
                                          <#if (row.sirPcrDto.completedInd?c) == 'false'>
                                            <input type="checkbox" name="sirId${row?counter}" value="${row.sirPcrDto.sirPcrNumber}">
                                          </#if>
                                        </td>
                                        <td>${row.sirPcrDto.sirType}</td>
                                        <td><a href="/sirDetail/${row.sirPcrDto.id}/${row.linkParms}">${row.sirPcrDto.sirPcrNumber}</a></td>
                                        <td>${row.sirPcrDto.nickName!""}</td>
                                        <td>${row.sirPcrDto.sirDesc[0..*30]!""}</td>
                                        <td>${row.sirPcrDto.completedInd?c}</td>
                                    </tr>
                                </#list>
                            </div>
                            <div class="form-group">
                                 <button type="submit" class="btn btn-default btn-sm" id="btnCloseSirs">
                                    Close
                                 </button>
                            </div>
                        </#if>
                    </tbody>
                  </table>
                </div>
            </form>
        </div>
    </div>
  </div>
</div>

</body>
</html>

