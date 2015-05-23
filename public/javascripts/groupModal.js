$(document).ready(function(){
    $("#createGroupSaveBtn").click(function(){
        var grpName = $("#newGroupName").val();
        var userId = $.cookie("user");
        $.ajax("users/"+userId+"/groups", {
            data: JSON.stringify({label: grpName}),
            success: function(data) {
             $("#groupItemList").prepend("<li>" + grpName + "</li>");
             },
            contentType: "application/json",
            type: "POST"
        })
    });
});