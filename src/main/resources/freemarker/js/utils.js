function dateAsMMDDYYYY(inDate) {
  var dt = new Date(inDate);
  return dt.getMonth() + 1 + "/" + dt.getDate() + "/" + dt.getFullYear();
}

function dateAsYYYYMMDD(inDate) {
  var date = new Date(inDate);
  return date.getFullYear() + "-" +
                    String("0" + (date.getMonth() + 1)).slice(-2) + "-" +
                    String("0" + date.getDate()).slice(-2);

}

function monthYearAsYYYYMM(inMonthYear)
{
    return inMonthYear.slice(0,4) + "-" +
           inMonthYear.slice(4);

}

function asCurrency(value)
{

    return value.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
}