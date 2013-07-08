var w = 1240,
	h = 1000,
	node,
	link,
	history,
	historyMessage = [],
	stopPoint = -1,
	appendList = [],//store every node and its children, if any
	detect = 1,
	root;
var draw = document.getElementById("draw");
var showNode = document.getElementById("showRoot");
var addData = document.getElementById("add");
var input = document.getElementById("input");
var ref = document.getElementById("ref");

var force = d3.layout.force()
	.linkDistance(120)
	.charge(-100)
	.gravity(0)
    .on("tick", tick)
    .friction(0.75)
    .size([500, 250]);

var vis = d3.select("#chart").append("svg:svg")
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
//	appendList = [];
//	history.push(keyWord);
//	console.log(keyWord);

	d3.json(keyWord, function(json){
//console.log("Json", json);
	if (json != null){
		if (appendList[0] == undefined){
			history = json;
//console.log("json", json, "history", history);
			appendList.push(json);
			for (var i = 0; i < json.children.length; i++){
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
		var result = drawSVGGraph(history);
	}
	else {
		alert("no available data for ' " + name + " '");
	}

	});
//console.log("history2:", history);
//	root = jQuery.extend(true, {}, history);

console.log("===========================================================");
}

function drawSVGGraph(data){
		root = jQuery.extend(true, {}, data);
		update();
}

function update() {
if ($("g").length != 0){
	$("g").remove();
}
console.log("root", root);
//console.log("svg: ", $("svg"));
//console.log("historyMessage", historyMessage);
var historyOutput = '<h2>History: </h2><p>';

	for (var i = 0; i < historyMessage.length; i++){
		historyOutput += historyMessage[i];
		historyOutput += ' -> <br>'
	}

historyOutput += '</p>';
$("#history").html(historyOutput);



  var nodes = flatten(root),
      links = d3.layout.tree().links(nodes);
//console.log("root", root);
//console.log("json", json);
	root.fixed = true;
	root.x = w / 2;
	root.y = 300;
  // Restart the force layout.
  force
      .nodes(nodes)
      .links(links)
      .start();
  // Update the nodes…
   node = vis.selectAll("circle.node")
      .data(nodes, function(d) { return d.id; })
      .style("fill", color);

  // Enter any new nodes.
  node.enter()
  	  .append("g")
      .attr("class", "node")
      .attr("transform", function(d){
      	return "translate(" + d.x + "," + d.y + ")"; 
      })
      .on("click", click)
      .call(force.drag);

  node.append("svg:circle")
  	  .attr("x", -10)
  	  .attr("y", -10)
  	  .attr("r", function(d) {return Math.log(d.size) * 3.5; })
  	  .style("fill", color);
 //     .append("text")
 //     .text(function(d){
 //     	return d.name;
 //     })

  
  
  node.attr("name", function(d){
//console.log("node.d", d);
      	return d.name;
    });
 //    .style("fill", color);
  node.append("text")
		.attr("dx", 14)
		.attr("dy", 0)
		.text(function(d){
//console.log("data: ", d);
//console.log(d.name);
			return d.name;
		});
  
  


  // Update the links…
   link = vis.selectAll("line.link")
      .data(links, function(d) { return d.target.id; });

  // Enter any new links.
  link.enter()//.insert("svg:g", ".node")
  	  .append("g")
      .attr("class", "link")
      .attr("transform", function(d){
      	return "translate(" + d.source.x + "," + d.source.y + "," + d.target.x + "," + d.target.y + ")";
      })

  // Exit any old links.
  link.exit().remove();
  
 link.append("line");

  
  
  $("circle").mouseover(function(){
		var messageOutput = '<p>Name: ';
			messageOutput += $(this).attr("name");
			messageOutput += '</p>';
		$("#message").append(messageOutput);
	});
  $("circle").mouseleave(function(){
  	var messageOutput = '<h2>Message: </h2>';
  	$("#message").html(messageOutput);
  })
}

function tick() {
  

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
	  });
  
  link.attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });



}

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
//alert(d.name);
//console.log("click.d", d);
if (d.search == 1){
  	if (d.children) {
//  		stopPoint = historyMessage.indexOf(d.name);
		historyMessage.pop();
    	d._children = d.children;
	    d.children = null;
  	} else {
  		historyMessage.push(d.name);
	    d.children = d._children;
	    d._children = null;
  	}
  	update();
}
else if (d.search == 0){
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
	var result = add(name);
}
ref.onclick = function(){
	var result = append();
};
