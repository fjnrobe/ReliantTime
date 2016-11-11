function loadPoData(poNumber)
{
   window.location.href = '/purchaseOrders/data/' + poNumber;
}

function resetPoData()
{
    $("#poDropdown").val("");
     $("#poNumber").val("");
    $("#poTitle").val("");
    $("#poHours").val(0);
    $("#hourlyRate").val(0);
    $("#passthruRate").val(0);
    $("#startDate").val("");
    $("#endDate").val("");
    $("#id").val("");
    $("#totalHours").html("");
    $("#totalGross").html("");
    $("#totalPassthru").html("");
    $("#totalAdjustedGross").html("");
    $("#billedHours").html("");
    $("#billedGross").html("");
    $("#billedPassthru").html("");
    $("#billedAdjustedGross").html("");
    $("#dueHours").html("");
    $("#dueGross").html("");
    $("#duePassthru").html("");
    $("#dueAdjustedGross").html("");
    $("#tblInvoiceList").html("");
}

$(document).ready(function(){

    $("#btnAddPo").click(function() {
        resetPoData();
    });
    $("#poDropdown").change(function() {
        loadPoData($("#poDropdown").val());
    });


});
