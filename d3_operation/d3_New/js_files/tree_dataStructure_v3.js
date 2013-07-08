var w = 1240,
	h = 1000,
	node,
	link,
	history,
	historyMessage = [],
	popTemp = [],
	stopPoint = -1,
	appendList = [],//store every node and its children, if any
	detect = 1,
	root;
var x = w / 2;
var y = 300;
var draw = document.getElementById("draw");
var showNode = document.getElementById("showRoot");
var addData = document.getElementById("add");
var input = document.getElementById("input");
var ref = document.getElementById("ref");

var force = d3.layout.force()
	.linkDistance(100)
	.charge(-60)
	.gravity(0)
	.linkStrength(1)
	.friction(0.75)
  //  .on("tick", tick)
    .size([500, 250]);

var vis = d3.select("#chart")
			.append("svg:svg")
    		.attr("width", w)
    		.attr("height", h);


//=============================================================================      
function append() {
d3.json("data_json/dataTree.json", function(json) {
	root = json;
	update();
});
}


function add(name) {
	var keyWord = "data_json/";
	keyWord += name;
	keyWord += ".json";
	historyMessage.push(name);

	d3.json(keyWord, function(error, json){
//console.log("Json", json);
	if (json != null){
		if (appendList[0] == undefined){
			history = json;
//console.log("json", json, "history", history);
			appendList.push(json);
			for (var i = 0; i < json.children.length; i++){
				json.children[i].draw = 0;
				//json.children[i].position = i;
				appendList.push(json.children[i]);
			}
		}
		else {
			for (var i = 0; i < json.children.length; i++){
			//	json.children[i].children = null;
				appendList.push(json.children[i]);
			}
			for (var i = 0; i < appendList.length; i++){
				if (name == appendList[i].name){
					appendList[i].children = json.children;
				}
			}
		}
		for (var i = 0; i < appendList.length; i++){
			appendList[i].size = 10;
			if (appendList[i].children != undefined){
				appendList[i].search = 1;
			}
			else {
				appendList[i].search = 0;
			}
//console.log(appendList[i].name, appendList[i]);
		}
		for (var i = appendList.length - 1; i > -1; i--){
			if (appendList[i].children != undefined){
				for (var n = 0; n < appendList[i].children.length; n++){
					appendList[i].size += appendList[i].children[n].size;
				}
			}
		}
console.log("history1:", history);
		var result = drawSVGGraph(history, name);
	}
	else {
		alert("no available data for ' " + name + " '");
	}

	});

console.log("===========================================================");
}

function drawSVGGraph(data, name){

		root = jQuery.extend(true, {}, data);

		update(name);

}

function update(name) {




  var nodes = flatten(root),
      links = d3.layout.tree().links(nodes);

	root.fixed = true;
	root.x = w / 2;
	root.y = 300;

  // Restart the force layout.
  force
      .nodes(nodes)
      .links(links)
      .start();
      
      
      
    // Update the links…
  link = vis.selectAll(".link")
      .data(links, function(d) { return d.target.id; });

  // Enter any new links.
  link.enter()//.insert("svg:line", ".node")
  	  .append("line")
      .attr("class", "link");
 //     .attr("x1", function(d) { return d.source.x; })
  //    .attr("y1", function(d) { return d.source.y; })
   //   .attr("x2", function(d) { return d.target.x; })
    //  .attr("y2", function(d) { return d.target.y; });

  // Exit any old links.
  link.exit().remove();    
      
      
   
  
  
  
  
  // Update the nodes…
  node = vis.selectAll(".node")
      .data(nodes, function(d) { return d.id; });
    //  .style("fill", "#3182bd");

var xCompensation = -50;
var yCompensationBase = - 14 / 8 * Math.PI;
var yCompensation;
  // Enter any new nodes.
  node.enter()
 	  .append("g")
 	  .attr("class", "node")
 	  .call(force.drag)
	  .on("click", click);
 	   
  
  
  node.append("svg:circle")
      .attr("class", "node")
      .attr("x", -10)
      .attr("y", -10)
      .attr("r", function(d) {return Math.log(d.size) * 3.5; })
      .style("fill", "#3182bd");
   // Exit any old nodes.
  node.exit().remove();   
    //  .call(force.drag);
	node.append("text")
		.attr("dx", 12)
		.attr("dy", 0)
		.text(function(d){
console.log("data: ", d);
console.log(d.name);
			return d.name;
		});
  
  
  node.attr("name", function(d){
      	return d.name;
    });









  
 

  
  
  $("g").mouseover(function(){
		var messageOutput = '<p>Name: ';
			messageOutput += $(this).attr("name");
			messageOutput += '</p>';
		$("#message").append(messageOutput);
	});
  $("g").mouseleave(function(){
  	var messageOutput = '<h2>Message: </h2>';
  	$("#message").html(messageOutput);
  })
  
  

  
  
  
  
}

	




force.on("tick", function(){
  

  node.attr("transform", function(d) {
  //	console.log(d.name, d.x, d.y); 
  		if (d.x < 15){
  			d.x = 15;
  		}
  		else if (d.x > 1220){
  			d.x = 1220;
  		}
  		if (d.y < 15){
      		d.y = 15;
      	}
      	else if (d.y > 985){
      		d.y = 985;
      	}

  		return "translate(" + d.x + "," + d.y + ")"; 
	  	})
  
  link.attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });



});

// Color leaf nodes orange, and packages white or blue.
function color(d) {
	if (d.name == history.name){
  		return "#ff0000";
	}
	else {
		return d._children ? "#3182bd" : d.children ? "#c6dbef" : "#fd8d3c";
	}
}

// Toggle children on click.
function click(d) {

if (d.search == 1){
  	if (d.children) {
    	d._children = d.children;
	    d.children = null;
  	} else {
	    d.children = d._children;
	    d._children = null;
  	}
  	update(d.name);
}
else if (d.search == 0){
	y += 50;
	add(d.name);
}
}

// Returns a list of all nodes under the root.
function flatten(root) {
  var nodes = [], i = 0;

  function recurse(node) {
    if (node.children) node.children.forEach(recurse);
    if (!node.id) node.id = ++i;
    nodes.push(node);
  }

  recurse(root);
  return nodes;
}






//=============================================================================   
draw.onclick = function(){
	var result = drawSVGGraph(history);
};
showNode.onclick = function(){
	console.log("appendList: ", appendList);
	console.log("root: ", root);
}  
addData.onclick = function(){
	var name = input.value;
	var result = add(name, w/2, 300);
}
ref.onclick = function(){
	var result = append();
};
