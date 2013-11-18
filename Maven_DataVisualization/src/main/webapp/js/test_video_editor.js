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
    // $("#dragtest").on("dragstart", function(event){event.preventDefault();})
    			  // .on("mousedown", function(e){
			    	// var x = e.pageX, y = e.pageY, w=$(this).css("width");
			    	// var temp = '<div id="dragtesttemp" class="nodeElementBarContentWrap btn btn-default"><img class="nodeElementBarContent" name="spimg" src="../img/sample_img.jpg"></div>';
			    	// $("#videoEditor").append(temp);
			    	// $("#dragtesttemp").css("position", "absolute")
			    					  // .css("top", y/2)
			    					  // .css("left", x/2)
			    					  // .css("width", w)
			    					  // .draggable({ containment: "#videoEditor", scroll: false })
			    					  // .on("mouseup", function(){
			    					  	// $(this).remove();
			    					  // })
//     	
    // }); containment: "#videoEditor", scroll: false, revert: true, 
    $("#dragtest").draggable({
    	helper: "clone" , 
    	opcaity: 0.5, 
    	revert: true, 
    	scroll: false, 
    	appendTo: "#videoEditor",
    	drag: function(event, ui){
    		ui.helper.css({
    			"width":$(this).css("width"),
    			"height": $(this).css("height")
    		});
    	}
    });

}
