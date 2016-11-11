function dateAsMMDDYYYY(inDate) {
  var dt = new Date(inDate);
  return dt.getMonth() + 1 + "/" + dt.getDate() + "/" + dt.getFullYear();
}

function asCurrency(value)
{

    return value.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
}