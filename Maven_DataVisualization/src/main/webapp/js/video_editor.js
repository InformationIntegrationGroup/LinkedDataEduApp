
//***************************************Variables******************************************//
var dataPathHash = new Object;







//***************************************Functions******************************************//
function readJSONPath(){
	$.getJSON("../data_json/sample_path.json", function(data){
		dataPathHash = data;
	});
}





//****************************************Function Calls***********************************//
$(window).load(function(){
	readJSONPath();
})
$("#generateVideoEditor").click(function(){
	$(this).remove();
	console.log(dataPathHash);
})
