function submitInnotasPage()
{
    var x = $("#innotasWeek").val();
    var d = new Date(x);
    var fd = d.toISOString().slice(0,10).replace(/-/g,"");
    $("#frmInnotasWeek").attr("action", "/reports/innotas/" + fd);
     $("#frmInnotasWeek").submit();
}

function getMonthlyStatus()
{
    var monthYear = $("#monthYears").val();


}

// Download a file form a url.
function getMonthlyStatus() {
  var monthYear = $("#monthYears").val();

  var filename = "Monthly Status Report For " + $("#monthYears option:selected").text() + " J_Robertson.xls";
  var xhr = new XMLHttpRequest();
  xhr.responseType = "blob";

  xhr.onload = function() {
    var a = document.createElement('a');
    a.href = window.URL.createObjectURL(xhr.response); // xhr.response is a blob
    a.download = filename; // Set the file name.
    a.style.display = 'none';
    document.body.appendChild(a);
    a.click();
     setTimeout(function(){
         document.body.removeChild(a);
         window.URL.revokeObjectURL(url);
     }, 100);
  };
  xhr.open('GET',"/reports/monthlyStatus/" + monthYear);
  xhr.send();
}

