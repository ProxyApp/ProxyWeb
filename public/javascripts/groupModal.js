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

    $(".groupName").click(function(){
        var grpName = $(this).text();
        $("#currentGroup").text(grpName);
    })

    $(".deleteGroupBtn").click(function(){
        var userId = $.cookie("user");
        var groupId = $(this).parent().attr("id");

        $.ajax("users/"+userId+"/groups/"+groupId, {
            success: function(data) {
               $("#"+groupId).remove();
            },
            type: "DELETE"
        })
    })


});