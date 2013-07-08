/****************************DECLARE VARIABLES********************************/
var width = 1240,
	height = 1000,
	status = 0,
	imgCount = 0,
	allNodes,
	allLinks,
	inputVal,
	firstIndex = -1,
	childIndex = [],
	appendNode = [],
	appendList = [],
	request,
	startX = 500,
	startY = 100,
	node,
	link,
	turn = 0;
//	link,
//	node;
	
var	input = document.getElementById("input"),
	search = document.getElementById("search"),
	reset = document.getElementById("reset");


	
var	svg = d3.select("#canvas")
			.append("svg")
			.attr("width", width)
			.attr("height", height)

	
var	force = d3.layout.force()
			  .gravity(0.1)
			  .distance(300)
			  .charge(-2)
			  .size([500, 300])

/****************************D3 Operation********************************/		
search.onclick = function(){
	var key = input.value;
	var draw = drawSVG(key,parent);
};

function drawSVG(key){
	console.log("Begin");
	
	d3.json("miserables.json", function(error, json){
		if (turn != 0){
			for (var i = 0; i < svg[0][0].childNodes.length; i++){
				if (svg[0][0].childNodes[i].__data__.status == 1){
					appendNode.push(svg[0][0].childNodes[i].__data__.name);
					appendNode.push(svg[0][0].childNodes[i].__data__.x);
					appendNode.push(svg[0][0].childNodes[i].__data__.y);
					appendList.push(appendNode);
					appendNode = [];
				//	svg[0][0].childNodes[i].__data__.status = 2;
				}
				svg[0][0].childNodes[i].__data__.status = 100;
			}
			for (var i = 0; i < appendList.length; i++){
				if (appendList[i][0] == key){
					startX = appendList[i][1];
					startY = appendList[i][2];
				}
			}
		}
	console.log("svg[0][0]: ", svg[0][0].childNodes.length);
	console.log("appendList: ", appendList);	
		
console.log("turn: ", turn, "part1");
for (var i = 0; i < svg[0][0].childNodes.length; i++){
	console.log("name: ", svg[0][0].childNodes[i].__data__.name, "; status: ", svg[0][0].childNodes[i].__data__);
}		

		var selector1 = ".link" + turn + '';
		var selector2 = ".node" + turn + '';
		var class1 = "link link" + turn + '';
		var class2 = "node node" + turn + '';

console.log(startX, startY);

		var keyWord = key;
				
			force.nodes(json.nodes)
				 .links(json.links)
				 .start();
			
			link = svg.selectAll(selector1)
						  .data(json.links)
						  .text(json.links.value)
						  .enter()
						  .append("line")
						  .attr("class", class1);
							  					  
			node = svg.selectAll(selector2)
						  .data(json.nodes)
						  .enter()
						  .append("g")
						  .attr("class", class2)
						  .call(force.drag)
						  .attr("group", function(d){
						  	for (var i = 0; i < appendList.length; i++){
						  		if (d.name == appendList[i][0]){
console.log(d.name, ": Yes!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						  			d.status = 2;
						  		}
						  	}
						  	if (d.status != 2){
						  		d.status = -1;
						  	}
						 	if (d.name == keyWord){
						  		firstIndex = d.index;
						  		childIndex.push(d.index);
						  	}
							d.group = 0;
						   })
						  .attr("fixed", function(d){
							if (d.index == firstIndex){
						 		d.fixed = true;
						 	}
						  	else {
						  		d.fixed = false;
						  	}
						  });
			link.attr("id", function(d){
					if(d.source.index == firstIndex){
						childIndex.push(d.target.index);
				  	}
				 	else if (d.target.index == firstIndex){
						childIndex.push(d.source.index);
					}
					return d.source.name + d.target.name;
			});	

			node.attr("group", function(d){					
				for(var i = 0; i < childIndex.length; i++){
					if (d.index == childIndex[i]){
						d.group = 1;
					}
				}
				if (d.index == firstIndex){
					d.group = 2;
				}
			});
				
			
console.log("turn: ", turn, "part2");
for (var i = 0; i < svg[0][0].childNodes.length; i++){
	console.log("name: ", svg[0][0].childNodes[i].__data__.name, "; status: ", svg[0][0].childNodes[i].__data__);
}	
			
			node.append("image")
      			.attr("xlink:href", function(d){
					if ( (d.group == 1 || d.group == 2) && d.status == -1 ){
						
						return "lesMiserables.png";
					}
      				})
      			.attr("id", function(d){
      				return d.name;
      			 })
      			.attr("x", -15)
      			.attr("y", -13)
      			.attr("width", 30)
      			.attr("height", 30)
      			.attr("class", "img")
      			.attr("onclick", function(d){
      					return "drawSVG('" + d.name + "')";
      				});
      			
      				

      		node.append("text")
     			.attr("dx", 12)
   				.attr("dy", ".35em")
 				.text(function(d) { 
 					if ( (d.group == 1 || d.group == 2) && d.status == -1 ){
 						if (d.status == -1){
 							d.status = 1;
 						}
 						return d.name;
 					}
				});
console.log("Here");
				//console.log("Turn", turn, "checkPoint 2");
					
			force.on("tick", function(){
					
				node.attr("transform", function(d){
					if ( (d.group == 1 || d.group == 2) ){
						if(d.x < 10){
							d.x = 10;
						}
						else if (d.x > 1230){
							d.x = 1230;
						}
						if (d.y < 10){
							d.y = 10;
						}
						else if (d.y > 990){
							d.y = 990;
						}
						if (d.group == 2){
							d.x = startX;
							d.y = startY;
						}
						if (d.status == 2){
							for (var i = 0; i < appendList.length; i++){
								if (d.name == appendList[i][0]){
									if (d.group == 2){
										d.x = startX;
										d.y = startY;
									}
									else {
										d.x = appendList[i][1];
										d.y = appendList[i][2];
									}
								}
							}						
						}
						return "translate(" + d.x + "," + d.y + ")";
					}
			
				});
					
				link.attr("x1", function(d) {
						if ( ((d.source.group == 1 && d.target.group == 2) || (d.source.group == 2 && d.target.group == 1)) && (d.status == 0) ){
							if (d.source.status == 2){
								for (var i = 0; i < appendList.length; i++){
									if (d.source.name == appendList[i][0]){
										return appendList[i][1];
									}
								}
							}
							else {
								return d.source.x;
							}
					}
					})
			 		.attr("y1", function(d) { 
			 			if ( ((d.source.group == 1 && d.target.group == 2) || (d.source.group == 2 && d.target.group == 1)) && (d.status == 0) ){
			 				if (d.source.status == 2){
								for (var i = 0; i < appendList.length; i++){
									if (d.source.name == appendList[i][0]){
										return appendList[i][2];
									}
								}
							}
							else {
								return d.source.y;
							}
						}
					})
		 			.attr("x2", function(d) { 
	 					if ( ((d.source.group == 1 && d.target.group == 2) || (d.source.group == 2 && d.target.group == 1)) && (d.status == 0) ){
			 				if (d.target.status == 2){
								for (var i = 0; i < appendList.length; i++){
									if (d.target.name == appendList[i][0]){
										return appendList[i][1];
									}
								}
							}
							else {
								return d.target.x;
							}
			 			}
					})
					.attr("y2", function(d) { 
		 				if ( ((d.source.group == 1 && d.target.group == 2) || (d.source.group == 2 && d.target.group == 1)) && (d.status == 0) ){
		 					if (d.target.status == 2){
								for (var i = 0; i < appendList.length; i++){
									if (d.target.name == appendList[i][0]){
										return appendList[i][2];
									}
								}
							}
							else {
								return d.target.y;
							}		 											}
	 				});
					
			}); //force.on
			turn++;
			firstIndex = -1;
			childIndex = [];	
console.log("break=============================================");
		});//d3.json
}// drawSVG

reset.onclick = function(){
	window.location.reload(true);
}
























