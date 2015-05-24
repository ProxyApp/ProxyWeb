$(document).ready(function(){
    $("#createGroupSaveBtn").click(function(){
        var grpName = $("#newGroupName").val();
        var userId = $.cookie("user");
        $.ajax("users/"+userId+"/groups", {
            data: JSON.stringify({label: grpName}),
            success: function(data) {
                location.reload();
             }
             ,
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
        var groupId = $(this).parent().parent().attr("id");

        $.ajax("users/"+userId+"/groups/"+groupId, {
            success: function(data) {
               $("#"+groupId).remove();
            },
            type: "DELETE"
        })
    })


});