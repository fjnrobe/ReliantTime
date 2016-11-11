var lovListArray;
function getLovList(lovCode) {
   $.ajax({url: "/lovTables/lovList/" + lovCode, async: false, success: function(result){
             lovListArray = JSON.parse(result);
             loadLovTable(lovListArray);
                 }});
}

//called when the user clicks on the edit icon (pencil)
//incoming value is 'lnkEdit&{number}
function editLovEntry(lovEntry)
{
    var selectedValue = $(lovEntry).attr("id");
    var values = selectedValue.split('&');

    $("#lovCode").val(lovListArray[values[1]].lovBaseDto.lovCode);
    $("#originalDescription").val(lovListArray[values[1]].lovBaseDto.lovDescription);

    $("#editEntryModal").modal("show");

}

function deleteLovEntry(lovEntry)
{
    var selectedValue = $(lovEntry).attr("id");
    var values = selectedValue.split('&');

    $("#delLovCode").val(lovListArray[values[1]].lovBaseDto.lovCode);
    $("#delLovDesc").val(lovListArray[values[1]].lovBaseDto.lovDescription);
    $("#confirmDeleteLabel").html("Are you sure you want to delete (" +
            lovListArray[values[1]].lovBaseDto.lovDescription + ")");

    $("#deleteEntryModal").modal("show");
}

//the list is an array of LovCodeUIDto objercts
function loadLovTable(lovList) {
     $("#tblLovList").empty();

     for (i = 0; i <lovList.length; i++)
     {
        var newRow = $("<tr>").appendTo( $("#tblLovList"));
        var newCol = $("<td>").appendTo(newRow);
        var desc = $("<p>" + lovList[i].lovBaseDto.lovDescription + "</p>").appendTo(newCol);

        newCol = $("<td>").appendTo(newRow);
        var lnkEdit = $("<a>", {id: "lnkEdit&" + i}).appendTo(newCol);

        lnkEdit.click(function() {
            editLovEntry(this);
        });

        var glyphEdit = $("<span>", {class: "glyphicon glyphicon-pencil"}).appendTo(lnkEdit);

        if (lovList[i].canBeDeleted)
        {
            newCol = $("<td>").appendTo(newRow);
            var lnkDelete = $("<a>", {id: "lnkDelete&" + i}).appendTo(newCol);
            var glyphDelete = $("<span>", {class: "glyphicon glyphicon-remove-sign"}).appendTo(lnkDelete);

            lnkDelete.click(function() {
               deleteLovEntry(this);
            });
        }
        else
        {
            newCol = $("<td>").appendTo(newRow);

        }
     }
}

//once a lov code entry is edited or deleted, the page will reload with a pre-selected lov code
//and the page will be pre-loaded with that lov code's entries
function initSelectedLovCode()
{
    if ($("#lovList").val() != "-1")
    {
        getLovList($("#lovList").val());
    }
}

$(document).ready(function(){

	$("#lovList").change(function() {
	   getLovList($("#lovList").val());
	});

    initSelectedLovCode();

});