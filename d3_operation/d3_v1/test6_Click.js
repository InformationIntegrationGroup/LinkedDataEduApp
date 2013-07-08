// VARIABLES DECLEARATION
var width = 1240,
	height = 1000,
	//status = 0,
	imgCount = 0,
	//txtCount = 0,
	//allNodes,
	//allLinks,
	//inputVal,
	lineDrawn1,
	lineDrawn2,
	firstIndex = -1,
	childIndex = [],
	appendNode = [],
	appendList = [],
	appendLink = [],
	appendLinkList = [],
	keys = [],
	request,
	startX = 500,
	startY = 130,
	//node,
	//link,
	turn = 0;

var	input = document.getElementById("input"),
	search = document.getElementById("search"),
	reset = document.getElementById("reset");

var	svg = d3.select("#canvas")
			.append("svg")
			.attr("width", width)
			.attr("height", height)

	
var	force = d3.layout.force()
			  .gravity(0)
			  .linkDistance(150)
			  .charge(-2)
			  .size([500, 300]);

// BUTTON FUNCTION CALLS
search.onclick = function(){
	var key = input.value;
	var draw = drawSVG(key);
};

reset.onclick = function(){
	window.location.reload(true);
}

//D3 OPERATION
function drawSVG(key){
	d3.json("miserables.json", function(error, json){
//Record nodes that have been drawn
		var selector1 = ".link" + turn + '';
		var selector2 = ".node" + turn + '';
		var class1 = "link link" + turn + '';
		var class2 = "node node" + turn + '';
		var keyWord = key;
		keys.push(key);
		console.log("svg: ", svg);
		lineDrawn1 = keys[keys.length - 1] + keys[keys.length - 2];
		lineDrawn2 = keys[keys.length - 2] + keys[keys.length - 1];
//		console.log("lineDrawn", lineDrawn);
		if (turn != 0){
			for (var i = 0; i < svg[0][0].childNodes.length; i++){
				if ( svg[0][0].childNodes[i].__data__.status == 1 && svg[0][0].childNodes[i].__data__.name != undefined ){
					appendNode.push(svg[0][0].childNodes[i].__data__.name);
					appendNode.push(svg[0][0].childNodes[i].__data__.x);
					appendNode.push(svg[0][0].childNodes[i].__data__.y);
console.log(appendNode);
					if (svg[0][0].childNodes[i].__data__.name == keyWord){
						startX = svg[0][0].childNodes[i].__data__.x;
						startY = svg[0][0].childNodes[i].__data__.y;
					}
					appendList.push(appendNode);
					appendNode = [];
				}
				
				if (svg[0][0].childNodes[i].id == lineDrawn1 || svg[0][0].childNodes[i].id == lineDrawn2){
					svg[0][0].childNodes[i].className.animVal += " lineDrawn";
					svg[0][0].childNodes[i].className.baseVal += " lineDrawn";
				}
				svg[0][0].childNodes[i].__data__.status == 100;
			}
		}
		
		
console.log("keyWord", keyWord);	

		
		force.nodes(json.nodes)
			 .links(json.links)
			 .start();
		
		link = svg.selectAll(selector1)
				  .data(json.links)
				  .text(json.links.value)
				  .enter()
				  .append("line")
				  .attr("class", class1);
				  
//Mark nodes and links	
		node = svg.selectAll(selector2)
				  .data(json.nodes)
				  .enter()
				  .append("g")
				  .attr("class", class2)
				  .call(force.drag)
				  .attr("group", function(d){
				  	d.status = -1;
				  	d.group = 0;
				  	for (var i = 0; i < appendList.length; i++){
				  		if (d.name == appendList[i][0]){
				  			d.status = 2;
				  		}
				  	}
				  	if (d.name == keyWord){
				  		firstIndex = d.index;
				  		childIndex.push(d.index);
				  		d.group = 2;
				  	}
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
			if (d.source.index == firstIndex){
console.log(d.source.name, "~", d.target.name, "->", d.source.group);
				d.target.group = 1;
				childIndex.push(d.target.index);
			}
			else if (d.target.index == firstIndex){
console.log(d.source.name, "~", d.target.name, "->", d.target.group);
				d.source.group = 1;
				childIndex.push(d.source.index);
			}
			return d.source.name + d.target.name;
		});

//append img and text	
		node.append("image")
			.attr("xlink:href", function(d){
				if (d.group == 2){
					if (d.status == -1){
						d.status = 1;
					}
					return "lesMiserables.png";
				}
				if ( d.group == 1){
					if (imgCount < 5){
						if (d.status == -1){
							d.status = 1;
							imgCount ++;
						}
						return "lesMiserables.png";
					}
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
				if (d.status == 1 || d.status == 2){
					return "drawSVG('" + d.name + "')";
				}
			});

//Mark links	
		link.attr("status", function(d){
			if (d.source.group != d.target.group && d.source.group != 0 && d.target.group != 0){
				if ( d.source.status > 0 && d.target.status > 0 ){
					d.status = 1;
					if (d.source.name == keyWord){
						d.target.text = ": is the " + d.value + " of " + d.source.name;
					}
					else if (d.target.name == keyWord){
						d.source.text = ": is " + d.value + "ed by " + d.target.name;
					}
				}
			}
			
console.log("Turn: ", turn, " link: ", d.source.name,"~",d.target.name, d.status);
		});
		
		
		node.append("text")
			.attr("dx", 12)
			.attr("dy", ".35em")
			.text(function(d){
console.log("Turn: ", turn, "Name: ", d.name, "Group: ", d.group, "Status: ", d.status, d.x, d.y);
				if (d.status == 1){
					d.weight = 0.5;
					if (d.group == 1){
						return d.name + d.text;
					}
					else if (d.group == 2){
						return d.name;
					}
				}
				else {
					d.weight = 0;
				}
			});
	
		
		
console.log("appendList: ",appendList)		
		
//Turn force on
		force.on("tick", function(){
//node operation
			node.attr("transform", function(d){
				if (d.group == 1 || d.group == 2){
					if (d.name == keyWord){
						d.x = startX;
						d.y = startY;
						return "translate(" + d.x + "," + d.y + ")";
					}
					if (d.status == 2){
						for (var i = 0; i < appendList.length; i++){
							if (d.name == appendList[i][0]){
								d.x = appendList[i][1];
								d.y = appendList[i][2];
								return "translate(" + d.x + "," + d.y + ")";
							}
						}
					}
					if (d.status == 1){
						if(d.x < 10){
							d.x = 10;
						}
						else if (d.x > 1230){
							d.x = 1230;
						}
						if (d.y < 15){
							d.y = 15;
						}
						else if (d.y > 990){
							d.y = 990;
						}
						return "translate(" + d.x + "," + d.y + ")";
					}
					
				} 
			});
			
//link operation
			link.attr("x1", function(d){
					if (d.status == 1){
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
				.attr("y1", function(d){
					if (d.status == 1){
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
				.attr("x2", function(d){
					if (d.status == 1){
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
				.attr("y2", function(d){
					if (d.status == 1){
						if (d.target.status == 2){
							for (var i = 0; i < appendList.length; i++){
								if (d.target.name == appendList[i][0]){
									return appendList[i][2];
								}
							}
						}
						else {
							return d.target.y;
						}
					}
				});
		
		});//force.on
		turn++;
		firstIndex = -1;
		childIndex = [];
	//	appendList = [];
		imgCount = 0;
		console.log("break==========================================");
	});//d3.json
}//function.drawSVG(key)
	
							  					  



