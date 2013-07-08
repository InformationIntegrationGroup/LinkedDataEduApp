/****************************DECLARE VARIABLES********************************/
var width = 1150,
	height = 515,
	status = 0,
	allNodes,
	allLinks,
	inputVal,
	firstIndex = -1,
	childIndex = [],
	request,
	link,
	node;
	
var	input = document.getElementById("input"),
	search = document.getElementById("search"),
	reset = document.getElementById("reset");
	
var	svg = d3.select("#canvas")
			.append("svg")
			.attr("width", width)
			.attr("height", height)

	
var	force = d3.layout.force()
			  .gravity(0)
			  .distance(200)
			  .charge(-3)
			  .size([500, 500])

/****************************SET JSON REQUEST********************************/			


/****************************Search Operation********************************/
search.onclick = function(){

/****************************D3 Operation********************************/			
			d3.json("data1.json", function(error, json){
								
				force.nodes(json.nodes)
					 .links(json.links)
					 .start();
				
				link = svg.selectAll(".link")
							  .data(json.links)
							  .text(json.links.value)
							  .enter()
							  .append("line")
				//			  .append("g")
							  .attr("class", "link")
							  .attr("id", function(d){
							  	return d.source.name + d.target.name;
							  });

				
				node = svg.selectAll(".node")
							  .data(json.nodes)
							  .enter()
							  .append("g")
							  .attr("class", "node")
							  .call(force.drag)
							  .attr("group", function(d){
								d.group = 1;
					//console.log("test1", d.name, d.index, d.group);
							   });
 				


				
/****************************D3 Operation FORCE ON********************************/	

				
				node.append("image")
      					  .attr("xlink:href", function(d){
								return "https://github.com/favicon.ico";
      					  })
      					  .attr("x", -8)
      					  .attr("y", -8)
      					  .attr("width", 16)
      					  .attr("height", 16);
      				

      				 node.append("text")
     					 .attr("dx", 12)
   					   	 .attr("dy", ".35em")
 					     .text(function(d) { 
								return d.name ;
						  });
					console.log("link(Inside function): ", link);
					console.log("node(Inside function): ", node);
/*
				force.on("tick", function(){
					
					node.attr("transform", function(d){
					//	console.log("test2", d.name, d.index, d.group);
						if ((findNodeIndex(d, childIndex) == 1 )){
							
							return "translate(" + d.x + "," + d.y + ")";	
						}
					})


					link.attr("fixed", function(d){
						if (d.source.index == firstIndex){
							d.source.fixed = true;
						//d.target.fixed = true;
						}
					});
					
					link.attr("x1", function(d) {
						//	console.log("Link Test: ", d);
							if ( (d.source.group == -1 && d.target.group == 0) || (d.target.group == -1 && d.source.group == 0) ){
								return d.source.x;
						}
						})
			 			.attr("y1", function(d) { 
			 				if ((d.source.group == -1 && d.target.group == 0) || (d.target.group == -1 && d.source.group == 0)){
			 					return d.source.y; 
			 				}
						})
			 			.attr("x2", function(d) { 
	 						if ((d.source.group == -1 && d.target.group == 0) || (d.target.group == -1 && d.source.group == 0)){
			 					return d.target.x; 
			 				}
						})
						.attr("y2", function(d) { 
			 				if ((d.source.group == -1 && d.target.group == 0) || (d.target.group == -1 && d.source.group == 0)){
			 					return d.target.y; 			 				
							}
	 					});
					
				}); //force.on
				
*/
			});//d3.json


}//search.onclick

reset.onclick = function(){
	window.location.reload(true);
}
























