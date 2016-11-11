var hoursPerDayArray = [];  //this holds an array of the hours per day - each entry is an hoursPerDay
var currentDate;
var currentlySelectedDate;  //this will be a reference to the td element and is set in handleDayClick

//accepts a month and year (YYYY) value and returns an array (classDef.hoursPerDay)
function getHoursForMonth(selYearMonth){

    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
        } else {
        // code for IE6, IE5
         xhttp = new ActiveXObject("Microsoft.XMLHTTP");
     }

     xhttp.open("GET", "/calendar/data/" + selYearMonth, false);
     xhttp.send();
     //we get an array of days in the month and the hours, of the form
     //{"hours":8.25,"dayOfMonth":"19"}

     hoursPerDayArray = [];
     var databack = JSON.parse(xhttp.responseText);
     for (var i = 0; i < databack.length; i++) {
        var newEntry = new hoursPerDay(i, Number(databack[i].dayOfMonth), Number(databack[i].hours));
        hoursPerDayArray.push(newEntry);
     }

    return hoursPerDayArray;

}

//the result structure is a list of YearMonthDto objects

function getYearMonthDropdown() {
   $.ajax({url: "/calendar/init", async: false, success: function(result){
             loadMonthsDropDown(JSON.parse(result));
                 }});

}


function loadMonthsDropDown(monthData)
{
	$("#selMonth").empty();
	$("#selMonth").append("<option value=-1>Select a Month</option>");

	for (i = 0; i < monthData.length; i++ )
	{
		$("#selMonth").append("<option value=" + monthData[i].sortKey + ">" +
		                                         monthData[i].monthName + " " +
		                                         monthData[i].yearName + "</option>");
	}
}

//returns number of days in month. Trick: the incoming month is a 'normal' month
//i.e., jan = 1, feb = 2, etc. however, when creating a date object, the months are 0 - 11
//If you create a date using a day of '0', then it is the same as the last day of the prior
//month. So, if the incoming values are month = 2, year = 2016, this is February 2016.
//by creating a date with a '2', that is 'march' in the date object, but then setting
//the day to 0 has the effect of going back 1 day - to the last day of February. the
// 'getDate' function returns the day of the month - or in our case - the number of
//days in the month.
function daysInMonth(month, year) {
    
	return new Date(year,month,0).getDate();
}


function dateAsMMDDYYYY(inDate) {
  return inDate.getMonth() + 1 + "/" + inDate.getDate() + "/" + inDate.getYear();
}

function dateAsYYYYMMDD(inDate) {

    return inDate.getFullYear().toString() +
    ("00" + (inDate.getMonth() + 1).toString()).slice(-2) +
    ("00" + inDate.getDate().toString()).slice(-2);
}


function clearCalendar() {
// reset all days to the noDate class
    var dayIdx;
	for (dayIdx = 0; dayIdx <= 41; dayIdx++) {
	    dayVar = "#td" + dayIdx;
		$(dayVar).removeClass("withDate");
		$(dayVar).removeClass("selectedDate");
		$(dayVar).addClass("noDate");
		$(dayVar).text("");
		$(dayVar).removeData("date");
	}
	//clear out the week totals
	for (dayIdx = 1; dayIdx <=6; dayIdx++)
	{
	    dayVar = "#tt" + dayIdx;
	    $(dayVar).text("");
	}
    $("#monthTotal").text("");

}

function getHoursForDay(whichDay)
{
    var hours = 0.0;
    var idx;
    for (idx = 0; idx < hoursPerDayArray.length; idx++)
    {
        if (hoursPerDayArray[idx].dayOfMonth == whichDay)
        {
            hours = hoursPerDayArray[idx].hours;
            break;
        }
    }

    return hours;
}

//draw the calendar for the incoming date (yyyymm)
function drawCalendar(whichMonth) {


    var dayIdx;
	var dayVar;
    var currDayIdx;

    var selectedYear = whichMonth.slice(0,4);
    var selectedMonth = whichMonth.slice(4)
	var selectedDate = new Date(selectedYear, parseInt(selectedMonth) - 1, 1);

	var hoursInMonth = getHoursForMonth(whichMonth);

	//get the 1st day of the week for the month
	var firstDay = selectedDate.getDay();
	currDayIdx = firstDay;
	
	//get the number of days in the month
	var numberOfDays = daysInMonth(parseInt(selectedMonth), selectedYear);
	var totalHours = 0.0;

	clearCalendar();
	
	//cycle through the calendar table and 
	// 1) change the class to withDate
	// 2) set the text to the loop variable	for the day of the month
	for (dayIdx = 0; dayIdx < numberOfDays; dayIdx++) {

        var thisDate = new Date(selectedYear, parseInt(selectedMonth) - 1, dayIdx + 1);

        var dayOfWeek = thisDate.getDay();

        //see if there are any hours for this day
        var hours = getHoursForDay(dayIdx + 1);

		dayVar = "#td" + currDayIdx;
		$(dayVar).addClass("withDate");
        if (((dayOfWeek == 0) || (dayOfWeek == 6)) && (hours == 0))
        {
            //do nothing - this is a normal weekend
        }
        else
        {
            if (hours < 8)
            {
                $(dayVar).css("color", "red");
            }
            else if (hours > 8)
            {
                $(dayVar).css("color", "green");
            }
            else
            {
                $(dayVar).css("color", "black");
            }
        }

        //each day will be a link that passes the current date back in yyyymmdd format

        $('<a>',{
            text: dayIdx + 1,
            href:'/dayEntry/' + dateAsYYYYMMDD(thisDate)

        }).appendTo($(dayVar));
		var defaultDayText = "<br><br>" + hours + " hour(s)";

		$(dayVar).html($(dayVar).html() + defaultDayText);
		//keep a reference to the current date
		$(dayVar).data("date", thisDate);

        //the hours for each day are added to the tt{x} entry based on the currDayIdx:
        //days 0 - 6 --> tt1
        //days 7 - 13 --> tt2
        //days 14 - 20 --> tt3
        //days 21 - 27 --> tt4
        //days 28 - 34 --> tt5
        //days 35 - 41 --> tt6
        //to do this, just divide the currDayIdx by 7 , take the integer portion and add 1
        var weekToTotal = parseInt(currDayIdx/7) + 1;
        var totalVar = "#tt" + weekToTotal;
        $(totalVar).text((Number($(totalVar).text()) + hours).toFixed(2));

        totalHours += hours;
		currDayIdx++;
	}

	$("#monthTotal").text(totalHours.toFixed(2));
	currentlySelectedDate = undefined;


}

function handleDayClick(whichDay) {
	
	var idx;

	//if the user clicked on a day not in the month, do nothing
	if ( $(whichDay).data("date") != undefined ) {
		
		//if there is already a currentlySelectedDate, then we need to flip it back to the 'withDate' class - the user
		//is clicking between dates
		if (currentlySelectedDate != undefined) {
			$(currentlySelectedDate).removeClass("selectedDate");
			$(currentlySelectedDate).addClass("withDate");
		}
		
		//change the background color to indicate the date has been selected
		$(whichDay).removeClass("withDate");
		$(whichDay).addClass("selectedDate");
		currentlySelectedDate = whichDay;
		currentDate = $(whichDay).data("date")

	} else {

		//if there is already a currentlySelectedDate, then we need to flip it back to the 'withDate' class - the user
		//is clicking between dates
		if (currentlySelectedDate != undefined) {
			$(currentlySelectedDate).removeClass("selectedDate");
			$(currentlySelectedDate).addClass("withDate");
		}
	}	
}

function initCalendar() {
	
	var dayIdx;
	
	clearCalendar();
	
	for (dayIdx = 0; dayIdx <= 41; dayIdx++) {
	    var dayVar = "#td" + dayIdx;
		$(dayVar).click(function () {
			handleDayClick(this);
		});
	}

    $("#selMonth").val($("#defaultYearMonth").text());
	drawCalendar($("#defaultYearMonth").text());
	$("#defaultYearMonth").hide();
}


$(document).ready(function(){
	
	$("#selMonth").change(function() {
	    drawCalendar($("#selMonth").val());
	});

	getYearMonthDropdown();

	//initialize a click handler for all days of the calendar
	initCalendar();

	
});
