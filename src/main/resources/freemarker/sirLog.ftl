<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <title>New SIR</title>
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
                  </div>
              </#list>

                <div class="panel panel-primary">
                  <div class="panel-heading">
                    <#if sirId??>
                        Edit SIR
                    <#else>
                        Enter SIR
                    </#if>
                  </div>
                  <div class="panel-body">
                    <form class="form-horizontal" method="Post" action="/saveSir">

                        <input type="hidden" id="sirId" name="sirId" value="${sirId!""}" >
                        <input type="hidden" id="logId" name="logId" value="${logId!""}" >
                        <input type="hidden" id="logDate" name="logDate" value="${logDate!""}" >
                        <input type="hidden" id="sirCreateDate" name="sirCreateDate" <#if sirCreateDate??>value="${sirCreateDate}"</#if>>
                        <input type="hidden" id="sirUpdateDate" name="sirUpdateDate" <#if sirUpdateDate??>value="${sirUpdateDate}"></#if>>
                        <input type="hidden" id="logCreateDate" name="logCreateDate" <#if logCreateDate??>value="${logCreateDate}" </#if>>
                        <input type="hidden" id="logUpdateDate" name="logUpdateDate" <#if logUpdateDate??>value="${logUpdateDate}" </#if>>
                        <div class="well">
                            <div class="form-group">

                                <label class="control-label col-md-2" for="sirType">SIR Type:</label>
                                <div class="col-md-5">
                                    <select class="form-control" id="sirType" name="sirType">
                                        <option <#if (sirType!"") == "SIR">selected</#if>>SIR</option>
                                        <option <#if (sirType!"") == "PCR">selected</#if>>PCR</option>
                                        <option <#if (sirType!"") == "OTHER">selected</#if>>OTHER</option>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-md-2" for="subProcess">Sub Process:</label>
                                <div class="col-md-5">
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
                                <label class="control-label col-md-2" for="sirNumber">SIR/PCR Number:</label>
                                <div class="col-md-5">
                                   <Input type="text" class="form-control" id="sirNumber" name="sirNumber" value="${sirNumber!""}">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-md-2" for="nickName">Nick Name:</label>
                                <div class="col-md-5">
                                    <input Type="text" id="nickName" name="nickName" class="form-control" value="${nickName!""}">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-md-2" for="description">Description:</label>
                                <div class="col-md-5">
                                    <textarea id="description" name="description" class="form-control" rows="5">${description!""}</textarea>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-md-offset-2 col-md-5">
                                    <div class="checkbox">
                                        <label><input type="checkbox" value="Y" id="completed" name="completed"
                                                      <#if completed!false>checked</#if>>Completed?</label>
                                    </div>
                                </div>
                             </div>
                        </div>

                        <div class="well">
                            <div class="form-group">
                                 <div class="form-group">
                                    <div class="col-md-offset-2 col-md-5">
                                        <div class="checkbox">
                                            <label><input type="checkbox" value="Y" id="billable" name="billable"
                                            <#if billable!false>checked</#if>>Billable?</label>
                                        </div>
                                    </div>
                                 </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-md-2" for="date">Date:</label>
                                <div class="col-md-5">
                                    <#assign dt = date[0..3] + "-" + date[4..5] + "-" + date[6..7]>

                                        <input class="form-control"
                                               type="date" id="date" name="date"
                                               value="${dt}"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-md-2" for="startTime">Start Time:</label>
                                <div class="col-md-5">
                                    <input Type="text" id="startTime" name="startTime" class="form-control" value="${startTime!""}">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-md-2" for="endTime">End Time:</label>
                                <div class="col-md-5">
                                    <input Type="text" id="endTime" name="endTime" class="form-control" value="${endTime!""}">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-md-2" for="note">Note:</label>
                                <div class="col-md-5">
                                    <textarea id="note" name="note" class="form-control" rows="5">${note!""}</textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-md-2" for="primaVeraActivity">Primavera Activity:</label>
                                <div class="col-md-5">
                                    <select class="form-control" id="primaVeraActivity" name="primaVeraActivity">
                                        <option></option>
                                        <#list primaVeraTypes as type>
                                            <#if (primaVeraActivity!"") == type.lovDescription>
                                                <option value="${type.lovDescription}" selected>${type.lovDescription}</option>
                                            <#else>
                                                <option value="${type.lovDescription}">${type.lovDescription}</option>
                                            </#if>
                                        </#list>

                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-md-2" for="activity">Activity:</label>
                                <div class="col-md-5">
                                    <select class="form-control" id="activity" name="activity">
                                        <option></option>
                                        <#list activityTypes as type>
                                            <#if (activity!"") == type.lovDescription>
                                                <option value="${type.lovDescription}" selected>${type.lovDescription}</option>
                                            <#else>
                                                <option value="${type.lovDescription}">${type.lovDescription}</option>
                                            </#if>
                                        </#list>

                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="well">
                            <div class="form-group">
                                <div class="col-md-offset-2 col-md-5">
                                    <button type="submit" class="btn btn-default btn-sm" id="saveBtn">
                                    <span class="glyphicon glyphicon-ok"></span> Save</button>

                                    <button type="button" class="btn btn-default btn-sm" id="cancelBtn"
                                       onClick='cancelPage()'>
                                    <span class="glyphicon glyphicon-remove"></span> Cancel</button>

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