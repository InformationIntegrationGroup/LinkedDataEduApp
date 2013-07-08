/****************************DECLARE VARIABLES********************************/
var width = 1150,
	height = 515,
	status = 0,
	allNodes,
	allLinks,
	inputVal,
	firstIndex = -1,
	childIndex = [],
	request;
	
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
	if (window.XMLHttpRequest){
		request = new XMLHttpRequest();
	}
	else{
		request = new ActiveXObject("Microsoft.XMLHTTP");
	}
	inputVal = input.value;	
//	window.location.reload(true);
	request.open('GET', 'data1.json');
	request.onreadystatechange = function() {
		console.log("request.status: ", request.status);//----------------------------------------------Test
		console.log("request.readyState: ", request.readyState);
		if ((request.status === 200) && (request.readyState === 4)){
			info = JSON.parse(request.responseText);
			allNodes = info.nodes;
			allLinks = info.links;
			for (var i = 0; i < allNodes.length; i++){
				allNodes[i].index = i;
				if (allNodes[i].name == inputVal){
					firstIndex = i;
				}
			}
			
			console.log("info: ", info);//-------------------------------------------------------------Test
			console.log("allNodes: ", allNodes);
			console.log("allLinks: ", allLinks);
			console.log("firstIndex: ", firstIndex);
			
/****************************D3 Operation********************************/			
			d3.json("data1.json", function(error, json){
				for (var i = 0; i < allLinks.length; i++){
					if (allLinks[i].source == firstIndex){
						childIndex.push(allLinks[i].target);
					}
					if (allLinks[i].target == firstIndex){
						childIndex.push(allLinks[i].source);
					}
				}
				childIndex.push(firstIndex);
								
				force.nodes(json.nodes)
					 .links(json.links)
					 .start();
				
				var link = svg.selectAll(".link")
							  .data(json.links)
							  .text(json.links.value)
							  .enter()
							  .append("line")
				//			  .append("g")
							  .attr("class", "link")
							  .attr("id", function(d){
							  	return d.source.name + d.target.name;
							  });
							//  .append("text");
							
							  
/*					link.attr("text", function(d){
						console.log("here");
						
				  			var thisLink = document.getElementById(d.source.name + d.target.name);
							console.log ("TEST!!!!!!!");
							thisLink.innerHTML = thisLink.innerHTML + "<p>" + d.value + "</p>";
						
					});
							  
*/
					//		  .append("text")
					//		  .attr("text-anchor", "middle")
					//		  .attr("dy", "0.35em")
					//		  .text("1");
							  
					//		  .attr("marker-end", "url(#triangle)");
				
				var node = svg.selectAll(".node")
							  .data(json.nodes)
							  .enter()
							  .append("g")
							  .attr("class", "node")
							  .call(force.drag);
				node.attr("group", function(d){
					for(var i = 0; i < childIndex.length; i++){
						if (d.index == childIndex[i]){
							d.group = 0;
							break;
						}
					}
					if (d.index == firstIndex){
						d.group = -1;
					}
					//console.log("test1", d.name, d.index, d.group);
				})
 				

				console.log ("svgNode: ", node);//---------------------------------------------------------Test
				console.log ("svgLink: ", link);
				//console.log ("node[firstIndex]", node[0]);
				console.log ("childIndex: ", childIndex);
				
/****************************D3 Operation FORCE ON********************************/	
				function findNodeIndex(node, source) {
					for (var i = 0; i < source.length; i++){
						if (node.index == source[i]){
							return 1;
						}
					}
				}
				function findLinkSource(link) {
					if ((link.source.index == firstIndex) || (link.target.index == firstIndex) ){
						return 1;
					}
				}
				
				node.append("image")
      					  .attr("xlink:href", function(d){
      					  	if (d.group == 0 || d.group == -1){
								return "https://github.com/favicon.ico";
							}
      					  })
      					  .attr("x", -8)
      					  .attr("y", -8)
      					  .attr("width", 16)
      					  .attr("height", 16);
      				

      				 node.append("text")
     					 .attr("dx", 12)
   					   	 .attr("dy", ".35em")
 					     .text(function(d) { 
 					     	if (d.group == 0 || d.group == -1){
								return d.name ;
							} });

/*						.text(function(d){
							if ( (d.source.group == -1 && d.target.group == 0) || (d.target.group == -1 && d.source.group == 0) ){
								console.log(d.value);
								return d.value;
						}
						});
*/
					
				force.on("tick", function(){
					
					node.attr("transform", function(d){
					//	console.log("test2", d.name, d.index, d.group);
						if ((findNodeIndex(d, childIndex) == 1 )){
							
							return "translate(" + d.x + "," + d.y + ")";	
						}
					})

/*					node.append("image")
						.attr("xlink:href", function(d){
							if ((findNodeIndex(d, childIndex) == 1 )){
								return d.src;
							}	
						})
						.attr("x", -20)
						.attr("y", -20)
						.attr("width", 50)
						.attr("height", 50)

*/
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
			});//d3.json
		} //request.status
	}//onreadystatechange
	request.send();

}//search.onclick

reset.onclick = function(){
	window.location.reload(true);
}
























