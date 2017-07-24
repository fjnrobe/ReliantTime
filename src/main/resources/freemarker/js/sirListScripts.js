function submitPage(action)
{
     $("#action").val(action);
     $("#frmSearchSirs").submit();
}

$(document).ready(function(){

	$("#btnGetSirList").click(function() {
	    submitPage("SEARCH");
	});

	$("#btnCloseSirs").click(function()
	{
	    submitPage("CLOSE");
	});

        $('#tblsirList').DataTable(
         {
               "searching": false,
              "order": [[ 2, "desc" ]]
          });


});