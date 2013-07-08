var dataset = [5, 10, 15, 20, 25, 30, 35];

d3.select("body")
	.selectAll("p")
	.data(dataset)
	.enter()
	.append("p")
	.text(function(d) {
		if ( d % 2 == 0){
			return "I can count up to " + d;
		}
		else {
			return d/5;
		}
	})
	.style("color", function(d) {
		if (d > 15) {//Threshold of 15
			return "red";
		} 
		else {
			return "black";
		}
	}); 