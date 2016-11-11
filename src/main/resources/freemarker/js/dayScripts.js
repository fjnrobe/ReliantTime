function submitPage(dateToNavigateTo)
{
    var returnUrl = '/dayEntry';

    if (dateToNavigateTo == "input")
    {
        var d = new Date($("#dailyEntryDate").val);
        console.log($("#dailyEntryDate").val);
        var fd = d.toISOString().slice(0,10).replace(/-/g,"");

        $("#dailyEntry").attr("action",returnUrl + "/" + fd);
    }
    else
    {

        $("#dailyEntry").attr("action", returnUrl + "/" + dateToNavigateTo);

    }
    $("#dailyEntry").submit();
}

function editLog(logId)
{
    window.location.href = '/editLog/' + logId;
}

function getExistingSirsByStatus(status){

     var queryStatus;
     if (status)
     {
        queryStatus = "N";
     }
     else
     {
        queryStatus = "Y";
     }

     $.ajax({url: "/dayEntry/data/" + queryStatus, async: false, success: function(result){
             loadActivityDropdown(JSON.parse(result));
                 }});

 }

//we get an array of days in the month and the hours, of the form
//we get an of List<ExistingSIRUIDto>
function loadActivityDropdown(data)
{
     $("#existingActivity").empty();
     for (var i = 0; i < data.length; i++) {
         $("#existingActivity").append("<option value=" + data[i].id +
                               ">" + data[i].description + "</option>");
     }
}

//this function is called when the user clicks on the delete icon for a specific
//log entry. query db to see if this is the only log for the sir - if so, then
//we will prompt the user if the sir should be deleted as well as the log

function confirmDeleteLog(sirId, logId)
{
    $("#sirIdToDelete").val(sirId);
    $("#logIdToDelete").val(logId);

    $.ajax({url: "/sirGet/" + sirId, async: false, success: function(result){

         sirPcrDto = JSON.parse(result);
         if (sirPcrDto.logs.length > 1)
         {
            $("#deleteSir").hide();
            $("#deleteLog").show();
         }
         else
         {
            $("#deleteSir").show();
            $("#deleteLog").hide();

         }

         $("#deleteLogModal").modal("show");
            }});

}

//this method is called after the modal popup asks whether the user wants to delete
//a sir and the log
function deleteSir(sirId)
{
    //delete the sir
    $.ajax({url: "/sirDelete/" + sirId, async: false, success: function(result){
        $("#deleteLogModal").modal("hide");

        //then reload the page to reflect the update
        window.location.href = '/dayEntry/' + $("#dailyEntryDate").val();
    }});
}

//this method is called after the modal popup asks whether the user wants to delete
//a log
function deleteLog(logId)
{
    //delete the log
    $.ajax({url: "/logDelete/" + logId, async: false, success: function(result){
        $("#deleteLogModal").modal("hide");

        //then reload the page
        window.location.href = '/dayEntry/' + $("#dailyEntryDate").val();
    }});
}

//when logging time for an existing sir
function newLogForExistingSir()
{
    //default the log date in the form so it can be used as a default date for the new log
    $("#newLogDate").val($("#dailyEntryDate").val());
    $("#logOptions").submit();
}

$(document).ready(function(){

    //when the user clicks on the 'show active only checkbox' reload the dropdown
	$("#activeOnly").click(function() {
	    getExistingSirsByStatus($("#activeOnly").prop('checked'));
	});

    //when the user clicks on the logTime button
    $("#logTime").click(function(event) {
        event.preventDefault();
        newLogForExistingSir();
    });
});
