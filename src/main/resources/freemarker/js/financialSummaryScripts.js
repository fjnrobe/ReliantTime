function currencyFormat (num) {

    if (num == null)
    {
        num = 0;
    }
    return "$" + num.toFixed(2).replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,")
}

function loadFinancialsForYear(year) {
    $.ajax({url: "/financialSummary/data/" + year, async: false, success: function(result){
                 var financialData = JSON.parse(result);
                 loadFinancialData(financialData);
                     }});
}


function loadFinancialData(data)
{
     var totDeductions = 0;

     $("#billedHours").text(data.billedHours);
     $("#billedGross").text(currencyFormat(data.billedGross));
     $("#receivedHours").text(data.receivedHours);
     $("#receivedGross").text(currencyFormat(data.receivedGross));
     $("#dueHours").text(data.dueHours);
     $("#dueGross").text(currencyFormat(data.dueGross));

      $("#tblExpense").empty();

      var newRow = $("<tr>").appendTo( $("#tblExpense"));
      var newCol = $("<td>").appendTo(newRow);
      $("<p><b>Category</b></p>").appendTo(newCol);
      newCol = $("<td>").appendTo(newRow);
      $("<p><b>Type</b></p>").appendTo(newCol);
      newCol = $("<td style='text-align: right'>").appendTo(newRow);
      $("<p><b>Amount</b></p>").appendTo(newCol);

      for (i = 0; i <data.expenseList.length; i++)
      {
         newRow = $("<tr>").appendTo( $("#tblExpense"));
         newCol = $("<td>").appendTo(newRow);
         $("<p>" + data.expenseList[i].deductionCategoryDesc + "</p>").appendTo(newCol);
         newCol = $("<td>").appendTo(newRow);
         $("<p>" + data.expenseList[i].deductionTypeDesc + "</p>").appendTo(newCol);
         newCol = $("<td style='text-align: right'>").appendTo(newRow);
         $("<p>" + currencyFormat(data.expenseList[i].deductionDto.amount) + "</p>").appendTo(newCol);

         totDeductions = totDeductions + data.expenseList[i].deductionDto.amount;
      }

      newRow = $("<tr>").appendTo( $("#tblExpense"));
      newCol = $("<td>").appendTo(newRow);
      $("<p><b>Total</b></p>").appendTo(newCol);
      newCol = $("<td>").appendTo(newRow);
      newCol = $("<td style='text-align: right'>").appendTo(newRow);
       $("<p><b>" + currencyFormat(totDeductions) + "</b></p>").appendTo(newCol);

      $("#netIncome").html("<b>" + currencyFormat(data.billedGross - totDeductions) + "</b>")
}

$(document).ready(function(){

    $("#summaryYear").change(function() {
        loadFinancialsForYear($("#summaryYear").val());
    });

});
