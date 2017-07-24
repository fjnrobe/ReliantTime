var revenueListArray;

function loadInvoices(year)
{
   $.ajax({url: "/revenue/data/" + year, async: false, success: function(result){
                revenueListArray = JSON.parse(result);
                loadInvoiceTable(revenueListArray);
          }});

}

function loadNextInvoiceData(year)
{
   var apple;
   var nextInvoiceData;
   $.ajax({url: "/revenue/nextInvoice", async: false, success: function(result){
                nextInvoiceData = JSON.parse(result);
          }});

    return nextInvoiceData;
}



function loadInvoiceTable(invoiceList)
{
     $("#tblRevenueList").empty();

    for (i = 0; i <invoiceList.length; i++)
     {
        var newRow = $("<tr>").appendTo( $("#tblRevenueList"));
        var newCol = $("<td>").appendTo(newRow);
        var desc = $("<p>" + invoiceList[i].monthYearAsString + "</p>").appendTo(newCol);
         newCol = $("<td>").appendTo(newRow);
        $("<p>" + invoiceList[i].poNumber + "</p>").appendTo(newCol);
        newCol = $("<td>").appendTo(newRow);
        $("<p>" + invoiceList[i].invoiceNumber + "</p>").appendTo(newCol);
        newCol = $("<td>").appendTo(newRow);
        $("<p>" + invoiceList[i].hours + "</p>").appendTo(newCol);
        newCol = $("<td>").appendTo(newRow);
        $("<p>" + asCurrency(invoiceList[i].totalGross) + "</p>").appendTo(newCol);

        newCol = $("<td>").appendTo(newRow);
        $("<p>" + dateAsMMDDYYYY(invoiceList[i].invoiceDate) + "</p>").appendTo(newCol);
        newCol = $("<td>").appendTo(newRow);
        $("<p>" + dateAsMMDDYYYY(invoiceList[i].receivedDate) + "</p>").appendTo(newCol);
        newCol = $("<td>").appendTo(newRow);
        var lnkEdit = $("<a>", {id: "lnkEdit&" + i}).appendTo(newCol);
        var glyphEdit = $("<span>", {class: "glyphicon glyphicon-pencil"}).appendTo(lnkEdit);

        lnkEdit.click(function() {
            editRevenueEntry(this);
        });

        newCol = $("<td>").appendTo(newRow);
        var lnkDelete = $("<a>", {id: "lnkDelete&" + i}).appendTo(newCol);
        var glyphDelete = $("<span>", {class: "glyphicon glyphicon-remove-sign"}).appendTo(lnkDelete);

        lnkDelete.click(function() {
           deleteRevenueEntry(this);
        });

        newCol = $("<td>").appendTo(newRow);
        var lnkDownload = $("<a>", {id: "lnkDownLoad&" + i, href: "/reports/invoice/" + invoiceList[i].invoiceNumber}).appendTo(newCol);
        var glyphDelete = $("<span>", {class: "glyphicon glyphicon-download"}).appendTo(lnkDownload);

        newCol = $("<td>").appendTo(newRow);
        var lnkEmail = $("<a>", {id: "lnkEmail&" + i}).appendTo(newCol);
        var glyphEmail = $("<span>", {class: "glyphicon glyphicon-envelope"}).appendTo(lnkEmail);

        lnkEmail.click(function() {
           openEmailPopup(this);
        });

     }

}

/* this function is called when a user clicks on the email link for a given invoice */
function openEmailPopup(emailEntry)
{
   var selectedValue = $(emailEntry).attr("id");
   var values = selectedValue.split('&');

   $("#emailInvoiceNumber").val(revenueListArray[values[1]].invoiceNumber);

   $("#emailEntryModal").modal("show");

}

/* this function is called when the user clicks on the add button to add a new invoice. by default
   the invoice will be initialized for the month/year following the last invoice created */
function addRevenueEntry()
{
    //retrieve the open PO Number and the next Invoice Number
    var nextInvoiceData = loadNextInvoiceData();

    $("#id").val("");
    $("#poNumber").val(nextInvoiceData.poNumber);
    $("#invoiceNumber").val(nextInvoiceData.invoiceNumber);
    $("#monthYear").val( monthYearAsYYYYMM(nextInvoiceData.monthYearAsString));
    $("#hours").val(nextInvoiceData.hours);
    $("#gross").val(nextInvoiceData.totalGross);
    $("#invDate").val(dateAsYYYYMMDD(nextInvoiceData.invoiceDate));
    $("#recvDate").val("");

    $("#editEntryModal").modal("show");

}

//called when the user clicks on the edit icon (pencil)
//incoming value is 'lnkEdit&{number}
function editRevenueEntry(revenueEntry)
{
    var selectedValue = $(revenueEntry).attr("id");
    var values = selectedValue.split('&');

    var invoiceDate = dateAsYYYYMMDD(revenueListArray[values[1]].invoiceDate);

    var receivedDate = dateAsYYYYMMDD(revenueListArray[values[1]].receivedDate);

    var monthYear = monthYearAsYYYYMM(revenueListArray[values[1]].monthYearAsString);

    $("#id").val(revenueListArray[values[1]].idAsString);
    $("#poNumber").val(revenueListArray[values[1]].poNumber);
    $("#invoiceNumber").val(revenueListArray[values[1]].invoiceNumber);
    $("#monthYear").val(monthYear);
    $("#hours").val(revenueListArray[values[1]].hours);
    $("#gross").val(revenueListArray[values[1]].totalGross);
    $("#adjGross").val(revenueListArray[values[1]].adjustedGross);

    $("#invDate").val(invoiceDate);
    $("#recvDate").val(receivedDate);

    $("#editEntryModal").modal("show");
}

function deleteRevenueEntry(deductionEntry)
{
        var selectedValue = $(deductionEntry).attr("id");
            var values = selectedValue.split('&');

    $("#delId").val(revenueListArray[values[1]].idAsString);

    $("#confirmDeleteLabel").html("Are you sure you want to delete this entry?");

    $("#deleteEntryModal").modal("show");
}

$(document).ready(function(){

    var tmp = $("#revenueListData").val();
    revenueListArray = JSON.parse(tmp);

    $("#dropYears").change(function() {
        loadInvoices($("#dropYears").val());
    });

});

