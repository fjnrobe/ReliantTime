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

//     $.ajax({url: "/reports/monthlyStatus/" + monthYear, async: false, success: function(result){
//                 alert("got " + results);
//                     }});

//                     $.get("/reports/monthlyStatus/" + monthYear, function(data, status){
//                            alert("got " + data);
//                         });
}

// Download a file form a url.
function getMonthlyStatus() {
  var monthYear = $("#monthYears").val();

  var filename = "Monthly Status Report For " + $("#monthYears option:selected").text() + " J_Robertson_PO_xxxx.xls";
  var xhr = new XMLHttpRequest();
  xhr.responseType = "blob";
  //xhr.overrideMimeType("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

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

//function getMonthlyStatus() {
//
//  var monthYear = $("#monthYears").val();
//
//  var filename = "Monthly Status Report For " + $("#monthYears option:selected").text() + " J_Robertson_PO_xxxx.xlsx";
//
//var xhr = new XMLHttpRequest();
//xhr.onreadystatechange = function(){
//    if (this.readyState == 4 && this.status == 200){
//        var blobURL = window.URL.createObjectURL(this.response);
//        var anchor = document.createElement('a');
//        anchor.download = 'filename.xlsx';
//        anchor.href = blobURL;
//        anchor.click();
//    }
//}
//xhr.open('GET', "/reports/monthlyStatus/" + monthYear);
//xhr.responseType = 'blob'; // Get a binary response
//xhr.send();
//  }