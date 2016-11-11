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
     }

}

function addRevenueEntry()
{
    //retrieve the open PO Number and the next Invoice Number
    var nextInvoiceData = loadNextInvoiceData();

    $("#id").val("");
    $("#poNumber").val(nextInvoiceData.poNumber);
    $("#invoiceNumber").val(nextInvoiceData.invoiceNumber);
    $("#monthYear").val(nextInvoiceData.monthYearAsString);
    $("#hours").val(nextInvoiceData.hours);
    $("#gross").val(nextInvoiceData.gross);
    $("#invDate").val(nextInvoiceData.invoiceDate);
    $("#recvDate").val("");

    $("#editEntryModal").modal("show");

}

//called when the user clicks on the edit icon (pencil)
//incoming value is 'lnkEdit&{number}
function editRevenueEntry(revenueEntry)
{
    var selectedValue = $(revenueEntry).attr("id");
    var values = selectedValue.split('&');

   // var tmp = $("#revenueListData").val();
   // revenueListArray = JSON.parse(tmp);
    var date = new Date(revenueListArray[values[1]].invoiceDate);
    var invoiceDate =  date.getFullYear() + "-" +
                    String("0" + (date.getMonth() + 1)).slice(-2) + "-" +
                    String("0" + date.getDate()).slice(-2);

    date = new Date(revenueListArray[values[1]].receivedDate);
    var receivedDate =  date.getFullYear() + "-" +
                    String("0" + (date.getMonth() + 1)).slice(-2) + "-" +
                    String("0" + date.getDate()).slice(-2);

    var monthYear = revenueListArray[values[1]].monthYearAsString.slice(0,4) + "-" +
                     revenueListArray[values[1]].monthYearAsString.slice(4);

    $("#id").val(revenueListArray[values[1]].idAsString);
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

      //      var tmp = $("#revenueListData").val();
      //      revenueListArray = JSON.parse(tmp);

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

