<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <title>SIR Detail</title>
    </head>

    <script>

        function cancelPage()
        {
             var returnDate = $("#logDate").val();
             if (returnDate == null) {
                var today = new Date();
                var dd = today.getDate();
                var mm = today.getMonth()+1; //January is 0!
                var yyyy = today.getFullYear();

                if(dd<10) {
                    dd='0'+dd
                }

                if(mm<10) {
                    mm='0'+mm
                }

                returnDate = yyyy + mm +dd;
             }

             window.location.href = '/dayEntry/' + returnDate;
        }

    </script>
    <body>

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

              </#list>

              <div class="panel panel-primary">
                  <div class="panel-heading">
                       SIR Detail
                  </div>
                  <div class="panel-body">
                    <form class="form-horizontal" method="Post" action="/updateSir">

                        <input type="hidden" id="sirId" name="sirId" value="${sirId!""}" >
                        <input type="hidden" id="logId" name="logId" value="${logId!""}" >
                        <input type="hidden" id="logDate" name="logDate" value="${logDate!""}" >
                        <input type="hidden" id="sirCreateDate" name="sirCreateDate" <#if sirCreateDate??>value="${sirCreateDate?datetime}"</#if>>
                        <input type="hidden" id="sirUpdateDate" name="sirUpdateDate" <#if sirUpdateDate??>value="${sirUpdateDate?datetime}"</#if>>
                        <input type="hidden" id="logCreateDate" name="logCreateDate" <#if logCreateDate??>value="${logCreateDate?datetime}"</#if>>
                        <input type="hidden" id="logUpdateDate" name="logUpdateDate" <#if logUpdateDate??>value="${logUpdateDate?datetime}"</#if>>
                        <div class="well">
                          <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-3" for="sirType">SIR Type:</label>
                                    <div class="col-md-9">
                                         <select class="form-control" id="sirType" name="sirType">
                                            <option <#if (sirType!"") == "SIR">selected</#if>>SIR</option>
                                            <option <#if (sirType!"") == "PCR">selected</#if>>PCR</option>
                                            <option <#if (sirType!"") == "OTHER">selected</#if>>OTHER</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="control-label col-md-3" for="subProcess">Sub Process:</label>
                                    <div class="col-md-9">
                                        <select class="form-control" id="subProcess" name="subProcess">
                                            <option></option>
                                            <#list subProcessTypes as type>
                                                <#if (subProcess!"") == type.lovDescription>
                                                    <option value="${type.lovDescription}" selected>${type.lovDescription}</option>
                                                 <#else>
                                                     <option value="${type.lovDescription}">${type.lovDescription}</option>
                                                  </#if>
                                            </#list>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-md-3" for="sirNumber">SIR/PCR Number:</label>
                                    <div class="col-md-9">
                                       <Input type="text" class="form-control" id="sirNumber" name="sirNumber" value="${sirNumber!""}">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-md-3" for="nickName">Nick Name:</label>
                                    <div class="col-md-9">
                                        <input Type="text" id="nickName" name="nickName" class="form-control" value="${nickName!""}">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-md-3" for="description">Description:</label>
                                    <div class="col-md-9">
                                        <textarea id="description" name="description" class="form-control" rows="5">${description!""}</textarea>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-offset-3 col-md-9">
                                        <div class="checkbox">
                                            <label><input type="checkbox" value="Y" id="completed" name="completed"
                                                          <#if completed!false>checked</#if>>Completed?</label>
                                        </div>
                                    </div>
                                 </div>

                                <div class="form-group">
                                    <div class="col-md-offset-3 col-md-9">
                                        <button type="submit" class="btn btn-default btn-sm" id="saveBtn">
                                        <span class="glyphicon glyphicon-ok"></span> Save</button>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">

                                <div class="form-group">
                                    <label class="control-label col-md-2" for="sirNote">Notes:</label>
                                    <div class="col-md-12">
                                        <textarea id="sirNote" name="sirNote" class="form-control" rows="20">${sirNote!""}</textarea>
                                    </div>
                                </div>
                            </div>
                          </div>
                        </div>

                        <div class="panel panel-primary">
                          <div class="panel-heading">
                               Logs
                          </div>
                          <div class="panel-body">
                            <div class="well well-sm">
                                Total Hours:&nbsp;${totHours}
                                <table class="table table-bordered">
                                    <thead>
                                       <tr>
                                         <th>Date</th>
                                         <th>Start Time</th>
                                         <th>End Time</th>
                                         <th style="text-align: right">Hours</th>
                                         <th>Activity</th>
                                         <th>Note</th>
                                       </tr>
                                    </thead>
                                    <tbody>
                                    <#if logList??>
                                        <#list logList as row>
                                            <tr>
                                              <td>${row.logDate[4..5]}/${row.logDate[6..7]}/${row.logDate[0..3]}</td>
                                              <td>${row.startTime[0..1]}:${row.startTime[2..3]}</td>
                                              <td>${row.endTime[0..1]}:${row.endTime[2..3]}</td>
                                              <td style="text-align: right">${row.hours}</td>
                                              <td>${row.activityDesc!""}</td>
                                              <td>${row.note}</td>
                                          </tr>
                                      </#list>
                                    </#if>
                                </table>
                            </div>
                          </div>
                        </div>

                        <input type="hidden" id="returnPageParms" name="returnPageParms" value="${returnPageParms}">
                    </form>
                  </div>

              </div>
            </div>
        </div>
    </body>
</html>