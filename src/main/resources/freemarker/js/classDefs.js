
function dateEntryItem(id, entryDate, dateEntryText) {    
    this.id = id;
	this.calendarDate = entryDate;
    this.entryText = dateEntryText;
}

function hoursPerDay(id, dayOfMonth, hours) {
    this.id = id;
    this.dayOfMonth = dayOfMonth;
    this.hours = hours;
}