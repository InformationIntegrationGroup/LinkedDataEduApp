var elementURI, elementID, elementSelector, mainWidth, mainHeight, thresholdHeight, tempIndicator, tempWidth, tempHeight, tempTopini, tempLeftini;
var append, moveindicator;
mainWidth = $("#videoEditor").css("width").replace(/[^-\d\.]/g, '');
mainHeight = $("#videoEditor").css("Height").replace(/[^-\d\.]/g, '');
thresholdHeight = mainHeight * 0.83;

enableElementBarContentOperation()


function enableElementBarContentOperation(){
	$(".nodeElementBarContent").on("dragstart", function(event){event.preventDefault();});
	$(".nodeElementBarContent").on("mousedown", function(e){
			var original = $(this);
			append = 0;
			moveindicator = 0;
			elementURI = $(this).attr("src");
			elementID = $(this).attr("name");
			elementSelector = "#" + elementID;
			tempWidth = $(this).css("width");
			tempHeight = $(this).css("height");
			tempTopini = e.pageY - tempHeight.replace(/[^-\d\.]/g, '') / 2 + "px";
			tempLeftini = e.pageX - tempWidth.replace(/[^-\d\.]/g, '') / 2 + "px";
			$(this).css("opacity", 0.5);
			console.log(tempTopini, tempLeftini, elementURI, tempWidth, tempHeight);	
			var newElement = '<img class="mainScreenTemp" src="' + elementURI + '">';
			if ($(".mainScreenTemp").length == 0){
				$("#videoEditor").append(newElement);
			}
			$(".mainScreenTemp").css("width", tempWidth)
								.css("height", tempHeight)
								.css("top", tempTopini)
								.css("left", tempLeftini)
								.on("dragstart", function(event){event.preventDefault();})
								.on("mousemove", function(e){
									tempTopini = e.pageY - tempHeight.replace(/[^-\d\.]/g, '') / 2 + "px";
									tempLeftini = e.pageX - tempWidth.replace(/[^-\d\.]/g, '') / 2 + "px";
									$(this).css("top", tempTopini)
										   .css("left", tempLeftini);
									appendToMovieNav(e, $(this));
								})
								.on("mouseup", function(){
									if (moveindicator == 1){
										original.parent().remove();
										original.remove();
										$(elementSelector + "").css("opacity", 1);
										enableNavOperation($(elementSelector + ""));
									}
									$(this).remove();
									$(".nodeElementBarContent").css("opacity", 1);
								});
	});
}
function appendToMovieNav(e, temp){
	var newNavElement = '<div id="' + elementID + '" class="movieNavElementWrap btn btn-default Enable"><img class="movieNavElement" src="' + elementURI + '"><div id="remove' + elementID + '" target1="' + elementURI + '" target2="'; 
	newNavElement += elementID + '" class="movieNavOperation">Remove</div></div>';
	if (e.pageY > thresholdHeight){
		moveindicator = 1;
		temp.css("opacity", 0.5);
		if (append == 0) {
			$("#movieNavBarWrap").append(newNavElement);
			$("#remove" + elementID).click(function(){
				var elementBack = '<div class="nodeElementBarContentWrap btn btn-default"><img class="nodeElementBarContent" name="' + $(this).attr("target2") + '" src="' + $(this).attr("target1") + '"></div>';
				$("#nodeElementBar").append(elementBack);
				enableElementBarContentOperation();
				$(this).parent().remove();
				
			});
			$(elementSelector + "").css("opacity", 0.5);
			append = 1;
		}
	}
	else {
		moveindicator = 0;
		temp.css("opacity", 1);
		if (append == 1){
			$(elementSelector + "").remove();
			append = 0;
		}	
	}
}

function enableNavOperation(element){
	console.log("here");
	console.log(element);
}


// .on("mouseup", function(){
	// console.log("here");
	// $("#videoEditor").remove($(".mainScreenTemp"));
	// $(this).css("opacity", 1);
// });
