<!DOCTYPE html>
<html lang="en">
<head>
  <title>Reliant Software LLC</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script src="\js\reportScripts.js"></script>
</head>
<body>

<div class="container-fluid">

  <#include "/header.ftl"/>
  <div class="panel panel-primary">
    <div class="panel-heading">Monthly Status Report</div>
        <div class="panel-body">
            <div class="well well-sm">
                <form class="form-inline" method="Post" id="frmMonthlyStatus" action="/reports/monthlyStatus">
                     <div class="col-md-3">
                          <select class="form-control" id="monthYears" name="monthYears">
                              <#list monthYears as monthYear>
                                    <option value=${monthYear.sortKey}>${monthYear.monthName} ${monthYear.yearName}</option>
                              </#list>
                          </select>
                    </div>
                    <div class="form-group">
                         <button type="submit" class="btn btn-default btn-sm"
                                id="btnCreateReport">
                            Generate Report
                         </button>
                    </div>
                </form>
            </div>

            <div class="well">
                <table class="table table-bordered" >
                    <thead>
                    <tr>
                        <th>File Name</th>
                        <th>Email</th>
                    </tr>
                    </thead>
                    <tbody  id="tblFileList">
                    <#if fileList??>
                        <#list fileList as row>
                            <tr>
                                <td><a href="/reports/monthlyStatus/getFile/${row.fileName}">${row.fileName}</a></td>
                                <td><a id="lnkEmail&${row.fileName}" onclick="openEmailPopup(this)"/>
                                    <span class="glyphicon glyphicon-envelope"></span>
                                </td>
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

<!-- email Modal -->
<div id="emailEntryModal" class="modal" role="dialog">
    <div class="modal-dialog modal-md">

        <!-- Modal content-->
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Email Monthly Status</h4>
            </div>
            <div class="modal-body">
                <form  class="form-horizontal"
                       id="emailForm"
                       method="Post" action="/reports/monthlyStatus/emailFile">

                    <input type="hidden" id="emailFileName" name="emailFileName"/>
                    <div class="form-group">

                        <label class="control-label col-md-3" for="toEmail">Send To:</label>
                        <div class="col-md-9">
                            <input type="text" id="toEmail" name="toEmail" list="emailAddressList" />
                            <datalist id="emailAddressList">
                                <#if toAddresses??>
                                    <#list toAddresses as entry>
                                        <option value="${entry}">${entry}</option>

                                    </#list>
                                </#if>
                            </datalist>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-md-3" for="subjectText">Subject:</label>
                        <div class="col-md-9">
                            <input type="text" id="subjectText" name="subjectText"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-md-3" for="bodyText">Message:</label>
                        <div class="col-md-9">
                            <input type="textArea" id="bodyText" name="bodyText" rows="5"/>
                        </div>
                    </div>
                    <button type="submit" id="btnSendEmail" name="btnSendEmail" class="btn btn-primary">Send</button>

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

