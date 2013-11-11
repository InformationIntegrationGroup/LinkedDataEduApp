$("#generateVideoEditor").click(function(){
	$(this).remove();
	$("#nodeNavBar").css("display","block");
	$("#nodeElementBar").css("display", "block");
	$("#moviePreview").css("display", "block");
	$("#audioDescriptionWrap").css("display", "block");
	$("#movieNavBarWrap").css("display", "block");
	$("#stepNavigator").css("display", "block");
	$("#nodeElementOperation").css("display", "block");
	$(".slideProgressBar").css("display", "block");

	enableCSSResponse();
});

function enableCSSResponse(){
	$(".nav-pills a").click(function(){
		//$("#movieNavBarWrap").empty();
		$(".nav-pills li").removeClass("active");
		var classSelector = "#" + $(this).attr("id");
		$(classSelector).parent().addClass("active");
		generateNodeElementBar($(this).attr("id"));
		durationRecords = [];
		IDRecords = [];
	});
	$( "#slider-range-min" ).slider({
      range: "min",
      value: 500,
      min: 1,
      max: 700,
      slide: function( event, ui ) {
        $( "#amount" ).val( "$" + ui.value );
      }
    });
    $( "#amount" ).val( "$" + $( "#slider-range-min" ).slider( "value" ) );
}
