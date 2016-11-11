var months = ["January","February","March","April","May","June","July","August","September","October","November","December"];
var dateEntries = [];  //this will hold the date entries - each entry will be a dateEntryItem
var currentDate;
var currentlySelectedDate;  //this will be a reference to the td element and is set inh andleDayClick

function sortOnFirstChar(a, b) 
{
	var ac = a.substr(0,1);
	var bc = b.substr(0,1);
	return ac < bc ? -1 : ac > bc ? 1 : 0;
}

function sortOnSecondChar(a, b) 
{
	
	var ac = a.substr(1,1);
	var bc = b.substr(1,1);	
	var ret = ac < bc ? -1 : ac > bc ? 1 : 0;
	return ret;
}

function sortOnThirdChar(a, b) 
{
	var ac = a.substr(2,1);
	var bc = b.substr(2,1);	
	var ret = ac < bc ? -1 : ac > bc ? 1 : 0;
	return ret;
}

function loadMonthsDropdown()
{
	$("#selMonth").empty();
	$("#selMonth").append("<option value=-1>Select a Month</option>");
	
	for (i = 0; i < 12; i++)
	{
		$("#selMonth").append("<option value=" + i + ">" + months[i] + "</option>");
	}
}

function sortOnChar(sortCol) {
	
	if (sortCol == 0)
	{	
		months.sort(sortOnFirstChar);
	} else if (sortCol == 1) {
	
		months.sort(sortOnSecondChar);
	} else {	
		months.sort(sortOnThirdChar);
	}
	
	loadMonthsDropdown();	
}	

function daysInMonth(month, year) {
    
	return new Date(year,month+1,0).getDate();
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
	$("#pnlDateEntry").hide();
	$("#tblExistingEntries").html("Exising Entries<br><hr>");
}

function getEntriesForDate(whichDate) {
	var entriesForDate = [];
	var idx;
	for (idx = 0; idx < dateEntries.length; idx++) {
		if (whichDate.getTime() == dateEntries[idx].calendarDate.getTime()) {
			var entryCopy = new  dateEntryItem(dateEntries[idx].calendarDate, dateEntries[idx].entryText);
			entriesForDate.push(entryCopy);
		}
	}
	
	return entriesForDate;
}
 
function drawCalendar(whichMonth) {

    var dayIdx;
	var dayVar;
    var currDayIdx;
	var selectedMonth = new Date(2016, whichMonth, 1);
	
	//get the 1st day of the week for the month
	var firstDay = selectedMonth.getDay();
	currDayIdx = firstDay;
	
	//get the number of days in the month
	var numberOfDays = daysInMonth(parseInt(whichMonth), 2016);
	
	clearCalendar();
	
	//cycle through the calendar table and 
	// 1) change the class to withDate
	// 2) set the text to the loop variable	for the day of the month
	for (dayIdx = 0; dayIdx < numberOfDays; dayIdx++) {
		dayVar = "#td" + currDayIdx;		
		$(dayVar).addClass("withDate");
		
		var defaultDayText = dayIdx + 1;
		
		//see if there are any saved entries for this date
		var thisDate = new Date(2016, whichMonth, dayIdx + 1);
		var existingEntries = getEntriesForDate(thisDate);
		if (existingEntries.length > 0) {
			defaultDayText += "<br><br>(" + existingEntries.length + ") entry(s)";
		}
		
		$(dayVar).html(defaultDayText);
		//keep a reference to the current date
		$(dayVar).data("date", thisDate);
			
		currDayIdx++;
	}	
	
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
		
		//show the existing entries in the <p> section to the right
		$("#tblExistingEntries").html("<b>Existing Entries<b><br><hr><br>");
		var existingEntries = getEntriesForDate(currentDate);
		for (idx = 0; idx < existingEntries.length; idx++) {
		    $("#tblExistingEntries").html($("#tblExistingEntries").html() + existingEntries[idx].entryText + "<br>");
		}
		
			
		//show the date entry panel and gather the info for the day
		$("#pnlDateEntry").show();
		$("#lblDateEntry").text("What would you like to do on " + currentDate.toDateString() + "?");
	} else {
		$("#pnlDateEntry").hide();
		$("#tblExistingEntries").text("Existing Entries");
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
	
	
}

function storeDateEntryItem() {
    var entryText = $("#txtDateEntry").val();	
	var newEntry = new dateEntryItem(currentDate, entryText);
	dateEntries.push(newEntry);
	$("#txtDateEntry").val("");
	$("#pnlDateEntry").hide();
	
	drawCalendar($("#selMonth").val());
}
	
$(document).ready(function(){

	var idx;
	
  
   $("#btnSetDate").click(function() {
      $("#demo").html(Date())
	  $("#demo").addClass("important");
	});
	  
	$("#lightBulb").click(function() {
	     var image = $("#lightBulb").attr("src");
		 
		if (image.match("bulbon")) {
			 $("#lightBulb").attr("src","http://www.w3schools.com/js/pic_bulboff.gif");
		} else {
			$("#lightBulb").attr("src","http://www.w3schools.com/js/pic_bulbon.gif");
		}
		
	});
	
	$("#btnValNumber").click(function() {
		var nbr = $("#numb").val();
		if (isNaN(nbr) || nbr < 1 || nbr > 10) 
		{
			$("#numberMsg").text("Input not valid");
		}
		else
		{
			$("#numberMsg").text("Input valid");
		}
	});
	
	$("#selSortChar").change(function() {
	
		sortOnChar($("#selSortChar").val());
			
	});
	
	$("#selMonth").change(function() {
	    drawCalendar($("#selMonth").val());
	});
	
	$("#btnAnimate").click(function() {
		$("#animate").animate({left: '0px',
		                      top: '0px'},
							  "fast");
		$("#animate").animate({left: '350px',
		                      top: '350px'},
							  5000);
	});
	
	$("#btnDateEntry").click(function() {
	    storeDateEntryItem();
	});
	
	$("input").on ({
	
	    focus: function() {
			$(this).css("background-color", "#cccccc");
		},
		blur: function() {
			$(this).css("background-color", "#ffffff");
		},
	    change: function() {
		    $(this).val($(this).val().toUpperCase());
		}
	});
	
	//initialization code for the month dropdown	
	loadMonthsDropdown();
	
	//initialize a click handler for all days of the calendar
	initCalendar();
	
});
