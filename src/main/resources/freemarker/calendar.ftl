<!DOCTYPE html>
<html>


<head>
  <script src="\js\jquery-2.2.1.js"></script>
  <script src="\js\calendarScripts.js"></script>
  <script src="\js\classDefs.js"></script>
   <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
   <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <link rel="stylesheet" type="text/css" href="\css\calendar.css">
	
</head>
<body>


 <div class="container-fluid">
        <#include "/header.ftl">
        <div class="panel panel-primary">
             <div class="panel-heading">My Calendar</div>
             <span id="defaultYearMonth">${defaultYearMonth}</span>
             <div class="panel-body">
                 <div class="well well-sm">
                    <select id="selMonth"></select>

                    <div id="tblCalendar">
                        <table>
                            <tr>
                                <td class="dayHeader">Sunday</td>
                                <td class="dayHeader">Monday</td>
                                <td class="dayHeader">Tuesday</td>
                                <td class="dayHeader">Wednesday</td>
                                <td class="dayHeader">Thursday</td>
                                <td class="dayHeader">Friday</td>
                                <td class="dayHeader">Saturday</td>
                                <td class="weekTotal">Total:&nbsp;</td>
                                <td id="monthTotal"></td>
                            </tr>
                            <tr>
                                <td id="td0" class="noDate"></td>
                                <td id="td1" class="noDate"></td>
                                <td id="td2" class="noDate"></td>
                                <td id="td3" class="noDate"></td>
                                <td id="td4" class="noDate"></td>
                                <td id="td5" class="noDate"></td>
                                <td id="td6" class="noDate"></td>
                                <td id="tt1" class="weekTotal"></td>
                            </tr>
                            <tr>
                                <td id="td7" class="noDate"></td>
                                <td id="td8" class="noDate"></td>
                                <td id="td9" class="noDate"></td>
                                <td id="td10" class="noDate"></td>
                                <td id="td11" class="noDate"></td>
                                <td id="td12" class="noDate"></td>
                                <td id="td13" class="noDate"></td>
                                <td id="tt2" class="weekTotal"></td>
                            </tr>
                            <tr>
                                <td id="td14" class="noDate"></td>
                                <td id="td15" class="noDate"></td>
                                <td id="td16" class="noDate"></td>
                                <td id="td17" class="noDate"></td>
                                <td id="td18" class="noDate"></td>
                                <td id="td19" class="noDate"></td>
                                <td id="td20" class="noDate"></td>
                                <td id="tt3" class="weekTotal"></td>
                            </tr>
                            <tr>
                                <td id="td21" class="noDate"></td>
                                <td id="td22" class="noDate"></td>
                                <td id="td23" class="noDate"></td>
                                <td id="td24" class="noDate"></td>
                                <td id="td25" class="noDate"></td>
                                <td id="td26" class="noDate"></td>
                                <td id="td27" class="noDate"></td>
                                <td id="tt4" class="weekTotal"></td>
                            </tr>
                            <tr>
                                <td id="td28" class="noDate"></td>
                                <td id="td29" class="noDate"></td>
                                <td id="td30" class="noDate"></td>
                                <td id="td31" class="noDate"></td>
                                <td id="td32" class="noDate"></td>
                                <td id="td33" class="noDate"></td>
                                <td id="td34" class="noDate"></td>
                                <td id="tt5" class="weekTotal"></td>
                            </tr>
                            <tr>
                                <td id="td35" class="noDate"></td>
                                <td id="td36" class="noDate"></td>
                                <td id="td37" class="noDate"></td>
                                <td id="td38" class="noDate"></td>
                                <td id="td39" class="noDate"></td>
                                <td id="td40" class="noDate"></td>
                                <td id="td41" class="noDate"></td>
                                <td id="tt6" class="weekTotal"></td>
                            </tr>
                        </table>
                    </div>
                 </div>
             </div>
        </div>
 </div>
</body>
</html> 
