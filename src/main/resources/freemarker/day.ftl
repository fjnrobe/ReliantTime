<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
         <script src="\js\dayScripts.js"></script>
         <link rel="stylesheet" type="text/css" href="\css\calendar.css">
        <title>Daily Entry</title>
    </head>

<style>




#dailyEntryMatrix {

	height:300px;

	border:solid 2px black;
	overflow:scroll;
	overflow-y:scroll;
	overflow-x:scroll;
	white-space: nowrap; 
}

#dailyEntryActivity {
	float:left;
	margin:50px;
}

#dailyEntryLog {
	float:left;
	margin:50px;
}

.matrixHeaderCell {	
	width:35px;
	background-color: blue;
	color:white;
	border:0px;
}

 .workedButton{
    height:25px;
    width:100%;
}


</style>

 <body>

    <div class="container-fluid">
        <#include "/header.ftl">
        <div class="panel panel-primary">
             <div class="panel-heading">Daily Activity</div>
             <div class="panel-body">

                <div class="well well-sm">
                    <form class="form-inline" method="Get" action="" id="dailyEntry">
                        <div class="form-group">

                            <button type="button" class="btn btn-default btn-sm" id="prevDay"
                                      onclick="submitPage(${prevDay})">
                                <span class="glyphicon glyphicon-backward"></span> Prev Day
                            </button>
                        </div>
                        <div class="form-group">
                            <#assign dt = currentDay[0..3] + "-" + currentDay[4..5] + "-" + currentDay[6..7]>

                                <input class="form-control"
                                       type="date" id="dailyEntryDate" name="dailyEntryDate"
                                       value="${dt}"/>

                        </div>
                        <div class="form-group">
                            <button type="button" class="btn btn-default btn-sm" id="currDay"
                                    onclick="submitPage('input')">
                                <span class="glyphicon glyphicon-refresh"></span>
                            </button>
                        </div>
                        <div class="form-group">
                            <button type="button" id="nextDay" class="btn btn-default btn-sm"
                                onclick="submitPage(${nextDay})">
                                Next Day <span class="glyphicon glyphicon-forward"></span>
                                </button>
                        </div>

                    </form>

                </div>


                <div class="well well-sm">
                    <div id="dailyEntryMatrix">
                        <div class="row">
                            <div class="col-md-2">
                                Total Hours: ${totalHours!0}
                            </div>
                        </div>
                        <table>
                            <thead>
                                <th style="width: 120px;" class="matrixHeaderCell">Activity</th>
                                <th class="matrixHeaderCell">6am</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">7am</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">8am</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">9am</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">10am</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">11am</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">12pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">1pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">2pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">3pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">4pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">5pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">6pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">7pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">8pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">9pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell"10pm</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                                <th class="matrixHeaderCell">&nbsp;</th>
                            </thead>
                            <tbody>

                                <#list matrixConfig as sir>

                                    <tr>
                                        <td><a href="/sirDetail/${sir.id}/${sir.linkParms}">${sir.sirNickname!"" }</a></td>

                                        <#list sir.spaceBeforeEntry as nextLogSpace>
                                            <#if nextLogSpace gt 0>
                                                <td colspan="${nextLogSpace}">&nbsp;</td>
                                            </#if>
                                            <#assign idx = nextLogSpace?index>
                                            <td colspan="${sir.entryWidth[idx]}">
                                                <button type=button
                                                  class="workedButton"
                                                  onClick='editLog("${sir.logId[idx]}")'>
                                                  ${sir.entryLabel[idx]}

                                                </button>
                                                <a href="#">
                                                <span class="glyphicon glyphicon-remove-sign"
                                                   onClick='confirmDeleteLog("${sir.id}","${sir.logId[idx]}")'></span>
                                                </a>
                                            </td>
                                        </#list>
                                    </tr>
                                </#list>

                            </tbody>
                        </table>
                    </div>
                </div>
             </table>

            <form id="logOptions" method="Post" action="/newLog">
                <input type="hidden" id="newLogDate" name="newLogDate">
                <div class="well well-sm">
                    <div class="row">
                        <div class="col-md-offset-2 col-md-3">
                             <div class="checkbox">
                                <label><input type="checkbox" value="Y"
                                               id="activeOnly"
                                               name="activeOnly"
                                               checked=${activeOnly?c}>Show Active Only?</label>
                             </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-2">
                            <a href="/newSir/${currentDay}">New Activity</a>
                        </div>
                        <div class="col-md-3">
                              <select class="form-control" id="existingActivity" name="existingActivity">
                              <#list existingSirs as sir>
                                    <option value=${sir.id}>${sir.description}</option>
                              </#list>
                              </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" id="logTime" class="btn btn-default btn-sm">Log Time</button>
                        </div>
                    </div>
                </div>
            </form>
            <a href="/calendar/default/${currentYearMonth}">Return to Calendar</a>
        </div>
    </div>

    <!-- Modal -->
    <div id="deleteLogModal" class="modal fade" role="dialog">
      <div class="modal-dialog modal-sm">

        <!-- Modal content-->
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Delete Log Entry?</h4>
            </div>
            <div class="modal-body">
                <form id="deleteSirForm" method="Get">
                    <input type="hidden" id="sirIdToDelete">
                    <input type="hidden" id="logIdToDelete">

                    <div id="deleteSir">
                        <p>Do you want to delete the SIR in addition to the log?</p>
                        <button type="button" class="btn btn-primary" onClick='deleteSir($("#sirIdToDelete").val())'>Yes</button>
                        <button type="button" class="btn btn-default" onClick='deleteLog($("#logIdToDelete").val())'>No - Just The Log</button>
                    </div>
                    <div id="deleteLog">
                        <p>Are you sure you want to delete this entry?</p>
                        <button type="button" class="btn btn-primary" onClick='deleteLog($("#logIdToDelete").val())'>Yes</button>
                    </div>
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
