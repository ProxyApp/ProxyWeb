$(document).ready(function() {

$(".sectionCheck").click(function() {
    if ($(this).is(":checked")) {
        var group = ".sectionCheck"
        $(group).prop("checked", false);
        $(this).prop("checked", true);
    } else {
        $(this).prop("checked", false);
    }
});

    $("#createChanelSave").click(function(){
        var label = $("#channelLabel").val();
        var section = checkedSection();
        var type = channelType();

        var ch = newChannel(section, type, label);
        var userId = $.cookie("user");
        $.ajax("../users/"+userId+'/channels', {
            data: JSON.stringify(ch),
            success: function(data){
                location.reload();
            },
            contentType: "application/json",
            type: "POST"
        })
    })
})

function newChannel(section, type, label){
    switch (type) {
        case "web":
            var address = $("#webUrl").val();
            return {section: section, url: address, label: label};
        case "phone":
            alert("Phone not supported yet");
    }
}

function channelType() {
    return $("ul#createTabs li.active").attr("id");
}

function checkedSection() {
    var idSelection = function() {return this.id;};
    var checked =  $(".sectionCheck:checked").map(idSelection).get();
    if(!checked) {
        return "General"
    } else {
        return checked[0].split("_")[1];
    }
}