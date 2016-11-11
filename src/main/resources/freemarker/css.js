$(document).ready(function(){

	$("#selListItemStyle").change(function() {
			$("#ulSample").css("list-style-type", $("#selListItemStyle").find("option:selected").text());
		});
	$("#selOrderedListItemStyle").change(function() {
		$("#olSample").attr("type", $("#selOrderedListItemStyle").val());
	});
});