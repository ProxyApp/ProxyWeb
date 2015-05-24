$(document).ready(function() {

$(".sectionCheck").click(function() {
    if ($(this).is(":checked")) {
        var group = ".sectionCheck"
        var group = "input:checkbox[name='" + $(this).attr("name") + "']";
        $(group).prop("checked", false);
        $(this).prop("checked", true);
    } else {
        $(this).prop("checked", false);
    }
});

    $("#createChanelSave").click(function(){

    })
})