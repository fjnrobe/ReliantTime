var deductionListArray;


function loadDeductionTypesForCategory(deductionCategory) {
    $.ajax({url: "/deductions/categoryTypes/" + deductionCategory, async: false, success: function(result){
                 var deductionTypeArray = JSON.parse(result);
                 loadDeductionTypes(deductionTypeArray);
                     }});
}


function loadDeductionTypes(data)
{
     $("#deductionType").empty();
        $("#deductionType").append("<option></option>")
         for (var i = 0; i < data.length; i++) {
             $("#deductionType").append("<option value=" + data[i].deductionTypeCode +
                                   ">" + data[i].lovDescription + "</option>");
         }
}

function loadDeductionTypesForEditCategory(deductionCategory) {
    $.ajax({url: "/deductions/categoryTypes/" + deductionCategory, async: false, success: function(result){
                 var deductionTypeArray = JSON.parse(result);
                 loadEditDeductionTypes(deductionTypeArray);
                     }});
}


function loadEditDeductionTypes(data)
{
     $("#deductionCategoryType").empty();
         for (var i = 0; i < data.length; i++) {
             $("#deductionCategoryType").append("<option value=" + data[i].deductionTypeCode +
                                   ">" + data[i].lovDescription + "</option>");
         }
}

function addDeductionEntry()
{
    $("#deductionId").val("");
    $("#deductionCategoryCode").val("");
    $("#deductionCategoryType").val("");
    $("#postDate").val("");
    $("#amount").val(0);
    $("#note").val("");

    $("#editEntryModal").modal("show");

}

//called when the user clicks on the edit icon (pencil)
//incoming value is 'lnkEdit&{number}
function editDeductionEntry(deductionEntry)
{
    var selectedValue = $(deductionEntry).attr("id");
    var values = selectedValue.split('&');

    var tmp = $("#deductionListData").val();
    deductionListArray = JSON.parse(tmp);
    var date = new Date(deductionListArray[values[1]].deductionDto.postDate);
    var postDate =  date.getFullYear() + "-" +
                    String("0" + (date.getMonth() + 1)).slice(-2) + "-" +
                    String("0" + date.getDate()).slice(-2);

    $("#deductionId").val(deductionListArray[values[1]].deductionDto.idAsString);
    $("#deductionCategoryCode").val(deductionListArray[values[1]].deductionDto.deductionCategory);
    $("#deductionCategoryType").val(deductionListArray[values[1]].deductionDto.deductionType);
    $("#postDate").val(postDate);
    $("#amount").val(deductionListArray[values[1]].deductionDto.amount);
    $("#note").val(deductionListArray[values[1]].deductionDto.note);

    $("#editEntryModal").modal("show");

}

function deleteDeductionEntry(deductionEntry)
{
    var selectedValue = $(deductionEntry).attr("id");
       var values = selectedValue.split('&');

       var tmp = $("#deductionListData").val();
       deductionListArray = JSON.parse(tmp);
       var date = new Date(deductionListArray[values[1]].deductionDto.postDate);
       var postDate =  date.getFullYear() + "-" +
                       String("0" + (date.getMonth() + 1)).slice(-2) + "-" +
                       String("0" + date.getDate()).slice(-2);

       $("#delDeductionId").val(deductionListArray[values[1]].deductionDto.idAsString);
//       $("#deductionCategoryCode").val(deductionListArray[values[1]].deductionDto.deductionCategory);
//       $("#deductionCategoryType").val(deductionListArray[values[1]].deductionDto.deductionType);
//       $("#postDate").val(postDate);
//       $("#amount").val(deductionListArray[values[1]].deductionDto.amount);
//       $("#note").val(deductionListArray[values[1]].deductionDto.note);

    $("#confirmDeleteLabel").html("Are you sure you want to delete this deduction?");

    $("#deleteEntryModal").modal("show");
}



$(document).ready(function(){

    $("#deductionCategory").change(function() {
        loadDeductionTypesForCategory($("#deductionCategory").val());
    });

    $("#deductionCategoryCode").change(function() {
        loadDeductionTypesForEditCategory($("#deductionCategoryCode").val());
    });

});
