
//***************************************Variables******************************************//
var dataPathHash = new Object;
var hashObjectArray = new Object;
var pathElementHeight = $(window).innerHeight() * 0.16;
var pathElementWidth = pathElementHeight / 3 * 4 + 30;





//***************************************Functions******************************************//
function readJSONPath(){
	$.getJSON("../data_json/sample_path.json", function(data){
		dataPathHash = data;
	});
}

function generatePathArray(){
	hashObjectArray[dataPathHash.path[0].name] = [];
	hashObjectArray[dataPathHash.path[0].name][0] = Object;
	hashObjectArray[dataPathHash.path[0].name][0].name = "empty";
	hashObjectArray[dataPathHash.path[0].name][0].slide_description = [];
	hashObjectArray[dataPathHash.path[0].name][1] = dataPathHash.path[0];
	for (var i = 2; i < dataPathHash.path.length; i = i + 2){
		hashObjectArray[dataPathHash.path[i].name] = [];
		hashObjectArray[dataPathHash.path[i].name][0] = dataPathHash.path[i];
		hashObjectArray[dataPathHash.path[i].name][1] = dataPathHash.path[i-1];
	}
	console.log("hashObjectArray", hashObjectArray);
	console.log(pathElementWidth, pathElementHeight);
}

function generateEmptyNode(position){
	var empty = Object;
	empty.name = "empty";
	empty.slide_description = [];
	position = empty;	
}

function generateNodeNavBar(){
	var NodeNavBar = "<ul class='nav nav-pills nav-justified'>";
	NodeNavBar += "<li class='active'><a href='#' class='" + dataPathHash.path[0].name + "'>"; 
	NodeNavBar += dataPathHash.path[0].name + "</a></li>";
	generateNodeElementBar(dataPathHash.path[0].name);
	for (var i = 2; i < dataPathHash.path.length; i=i+2){
		NodeNavBar += "<li><a href='#' class='" + dataPathHash.path[i].name + "'>"; 
		NodeNavBar += dataPathHash.path[i].name + "</a></li>";
	}
	NodeNavBar += "</ul>";
	$("#nodeNavBar").html(NodeNavBar);
	$(".nav-pills a").click(function(){
		$(".nav-pills li").removeClass("active");
		var classSelector = "." + $(this).attr("class");
		$(classSelector).parent().addClass("active");
		generateNodeElementBar($(this).attr("class"));
	});
}

function generateNodeElementBar(nodeName){
	$("#nodeElement").empty();
	$("#textDescription").html(hashObjectArray[nodeName][1].audio_text);
	console.log(hashObjectArray[nodeName]);
	var elementLength = (hashObjectArray[nodeName][0].slide_description.length + 
						hashObjectArray[nodeName][1].slide_description.length) * (pathElementWidth);
	console.log(elementLength);
	$("#nodeElement").css("width", elementLength + "px");
	for (var i = 0; i < hashObjectArray[nodeName][0].slide_description.length; i++){
		generateNodeElement(hashObjectArray[nodeName][0].slide_description[i]);
	}
	for (var i = 0; i < hashObjectArray[nodeName][1].slide_description.length; i++){
		generateNodeElement(hashObjectArray[nodeName][1].slide_description[i]);
	}
	$(".nodeElementContent").css("width", pathElementWidth + "px");
	$(".nodeElementContent").click(function(){
		$("#moviePreview").html("<img class='imgPreview' src='" + $(this).attr("id") + "'>");
	});
}

function generateNodeElement(node){
	console.log(node, "uri", node.data.uri);
	var element = "<div id='" + node.data.uri + "' class='nodeElementContent'>";
		element += "<img class='fullHeight' src='" + node.data.uri + "'/>"
		element += "</div>";
	$("#nodeElement").append(element);
	
}

//****************************************Function Calls***********************************//
$(window).load(function(){
	readJSONPath();
})
$("#generateVideoEditor").click(function(){
	$(this).remove();
	$("#nodeNavBar").css("display","block");
	$("#nodeElementBar").css("display", "block");
	$("#moviePreview").css("display", "block");
	$("#textDescription").css("display", "block");
	console.log(dataPathHash);
	generatePathArray();
	generateNodeNavBar();
});

