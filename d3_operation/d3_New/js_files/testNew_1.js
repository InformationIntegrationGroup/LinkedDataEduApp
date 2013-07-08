var width = 1240,
    height = 1000

var	svg = d3.select("#canvas")
			.append("svg")
			.attr("width", width)
			.attr("height", height)
			
var force = d3.layout.force()
    .gravity(0)
    .distance(80)
    .charge(-80)
    .size([300, 100]);

d3.json("data_json/data1.json", function(error, json) {
  force
      .nodes(json.nodes)
      .links(json.links)
      .start();

  var link = svg.selectAll(".link")
      .data(json.links)
    .enter().append("line")
      .attr("class", "link");

  var node = svg.selectAll(".node")
      .data(json.nodes)
    .enter().append("g")
      .attr("class", "node")
      .attr("fixed", function(d){
   //   	d.weight = 0;
      	console.log(d);
		if (d.index == 0){
			d.fixed = true;
		}
	 	else {
			d.fixed = false;
		}
		})
      .call(force.drag);

  node.append("image")
      .attr("xlink:href", "lesMiserables.png")
      .attr("x", -8)
      .attr("y", -8)
      .attr("width", 25)
      .attr("height", 25);

  node.append("text")
      .attr("dx", 12)
      .attr("dy", ".35em")
      .text(function(d) { return d.name });

  force.on("tick", function() {
  	node.attr("transform", function(d) { 
    	if (d.index == 0){
    		d.x = 500;
    		d.y = 150;
    	}
    	return "translate(" + d.x + "," + d.y + ")"; 
    });
    link.attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });

    
  });
});