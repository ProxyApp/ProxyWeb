$(document).ready(function(){
    $("#createGroupSaveBtn").click(function(){
        var grpName = $("#newGroupName").text();
        $("#newGroupButton").append("<li>" + grpName + "</li>");
    });
});