/*jshint browser:true
*/
/*global Chart,console
*/
(function(){

	'use strict';
	window.chartJSChart = {};

	//To re-size the chart according to screen size of windows
	Chart.defaults.global.responsive = true;

	function init() {

	var chartArray = findCharts();
	initalizeCharts(chartArray);

	}
	document.addEventListener('app.Ready', init, false);



	function findCharts() {
		  var charts = [];
		  var chartQuery = document.querySelectorAll('[data-uib="media/chartjs"]');
		  for(var i = 0; i < chartQuery.length; i++) {
					var chartsData = {
										  chartDOMNode: null,
										  chartType: null,
										  jsonFile: null,
										  id: null
									  };
					var elem = chartQuery[i];
					chartsData.chartDOMNode = elem;
					chartsData.chartType = elem.getAttribute('data-chart-type');
					chartsData.jsonFile = elem.getAttribute('data-json-file');
					chartsData.id = elem.getAttribute('id');
					charts.push(chartsData);
		 }

	 return charts;
	 }


   function initalizeCharts(chartArray) {

		   chartArray.forEach(function(chart){
		   createChart(chart);
		   });
   }


	function createChart(chartObject) {

	var filePath = chartObject.jsonFile;
	 $.getJSON(filePath)
	.then(function(chartData){

		var canvas = document.createElement('canvas');
		canvas.id = 'canvas_' +  chartObject.id;
		document.getElementById(chartObject.id).appendChild(canvas);
		var chartContext = document.getElementById(canvas.id).getContext('2d');
		if(chartObject.chartType == "Line"){

			new Chart(chartContext).Line(chartData);

		}else if(chartObject.chartType == "Bar"){

			new Chart(chartContext).Bar(chartData);

		}else{

			new Chart(chartContext).Pie(chartData);
		}


	}).fail(function() {
		console.log("Failed to load JSON file!");
	 });

}

})();
