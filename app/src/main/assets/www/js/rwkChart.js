/*global $, Chart, APP*/

/*
The chartJS interface will always have a div surrounding the canvas element of the chart.
The div will always have a data-uib="media/chartjs", a data-chart-type set to a type of chart and
a data-chart-data set to the function to retreive the data from the database
*/
//###################################
// rwkChart.js
//###################################
"use strict";
(function (APP) {

  var instance,
      chartDataArray = [],
      // returns a copy of the chart array
      getChartDataArray = function () {
        return chartDataArray;
      },
      testData = {
        labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
        data: [12, 19, 3, 5, 2, 3]
      },
      // find all charts on a page
      //findChartObjects = function () {
      initChartObjects = function () {

        var charts = [],
            chartQuery = document.querySelectorAll('[data-uib="media/chartjs"]'),
            //chartQuery = document.querySelectorAll('div[data-uib="media/chartjs"]'), ## try this once everything else works!
            i,
            chartObject,
            elem;

        for (i = 0; i < chartQuery.length; i = i + 1) {
          chartObject = {
            chartDOMNode: null,
            chartType: null,
            chartFunction: null,
            id: null,
            chartRef: null
          };

          elem = chartQuery[i];
          chartObject.chartDOMNode = elem;
          chartObject.chartType = elem.getAttribute('data-chart-type');// i.e. line or bar
          chartObject.chartFunction = elem.getAttribute('data-chart-func');// the function to process the chart
          chartObject.id = elem.getAttribute('id').toLowerCase();
          charts.push(chartObject);
        }
        return charts;
      },
      //  createa the chart from the data sent
      createChart = function (chartObject, chartData, chartOptions) {

        var chartContext = document.getElementById('canvas-' +  chartObject.id),

            c = new Chart(chartContext, {
              type: chartObject.chartType.toLowerCase(),
              data: chartData,
              options: chartOptions
            });
        chartObject.chartRef = c;
      },
      // creates chart canvas and pushes it into chartDataArray
      createChartCanvas = function (chartObject) {
        /*  An asynchronous callback is not synchronous, regardless of how much you want it to be.
                Just move all the code the depends on the result into the callback*/

        if (chartObject !== null) {

          var canvas = document.createElement('canvas'),
              canvasParent;

          canvas.id = 'canvas-' +  chartObject.id;

          document.getElementById(chartObject.id).appendChild(canvas);
          canvasParent = canvas.parentElement;

          // create click event handler for chart
          /*$(canvas).on("click", function () {
                    console.log("canvas clicked");
                    //window.onChartClicked(evt);
                });*/

          canvas.width = canvasParent.offsetWidth;
          canvas.height = canvasParent.offsetHeight;
          chartDataArray.push(chartObject);
          return true;
        }
        return false;

      },
      // initialize all charts on this page
      initCharts = function () {

        var chartArray = initChartObjects();
        chartArray.forEach(function (chartObject) {
          if (createChartCanvas(chartObject)) {
            // get the data for the chart from the function specified in the HTML [data-chart-func] attribute
            APP.helpers.executeFunctionByName(chartObject.chartFunction, APP, chartObject);
          }
        });
      },
      // find a chartObject from its id
      findChart = function (elem) {

        var chart = null,
            canvasId;

        if (elem.is("canvas")) {
          canvasId = $(elem).attr('id');
          canvasId = canvasId.replace("canvas-", "");

          chart = chartDataArray.find(function (chartObject) {
            if (chartObject.id === canvasId) {
              return chartObject.chartRef;
            }
          });
        }
        return chart;
      },
      // returns single chart object from chartDataArray
      getChartObject = function (chartName) {

        chartName = chartName.toLowerCase();
        if (!chartName.endsWith('chart')) {
          chartName = chartName + '-chart';
        }
        var chartObject = chartDataArray.filter(function (e) {
          return e.id === chartName;//'mileage-chart';
        });
        return chartObject[0];
      },
      //updates all charts on current page
      updateAllCharts = function () {
        var chartObject;

        chartDataArray.forEach ( function (e) {
          if (document.getElementById(e.id)) {

            chartObject = e;
            if (chartObject.chartRef) {
              chartObject.chartRef.destroy();
              chartObject.chartRef = null;
              //mileageData(chartObject);
              // get the data for the chart from the function specified in the HTML [data-chart-func] attribute
              APP.helpers.executeFunctionByName(chartObject.chartFunction, APP, chartObject);
              console.log("chartDataArray.length: " + chartDataArray.length);
            }
          }
        });
      },
      //update chart from a chartObject
      updateChart = function (chartObject) {

        //var chartObj;

        if (typeof chartObject === 'string') {

          chartObject = getChartObject(chartObject);
        }

        if (chartObject.chartRef) {
          chartObject.chartRef.clear();
          chartObject.chartRef.destroy();
          chartObject.chartRef = null;
        }
        // get the data for the chart from the function specified in the HTML [data-chart-func] attribute
        APP.helpers.executeFunctionByName(chartObject.chartFunction, APP, chartObject);
      },
      //test data callback function
      testChartCB = function (testData, passthru) {

        var chartToUpdate = passthru[0],
            chartData = {labels: [], datasets: [{label: null, fill: null, backgroundColor: null, data: []}]},
            chartOptions = {
              pan: {
                enabled: false,
                mode: 'xy'
              },
              zoom: {
                enabled: false,
                mode: 'xy'
              },
              scales: {
                yAxes: [{
                  ticks: {
                    min: null
                  }
                }],
                xAxes: [{
                  categoryPercentage: null,
                  barPercentage: null
                }]
              }
            };

        chartData.datasets[0].label = "Test";
        chartData.datasets[0].fill = false;
        chartData.datasets[0].backgroundColor = "rgba(91, 206, 30, 0.4)";
        chartOptions.scales.yAxes[0].ticks.min = 0;
        chartOptions.scales.xAxes[0].categoryPercentage = 0.9;
        chartOptions.scales.xAxes[0].barPercentage = 0.2;

        console.log("chartToUpdate.function: " + chartToUpdate.chartFunction);
        if (chartToUpdate) {

          chartData.labels = testData.labels;//["Red", "Blue", "Yellow", "Green", "Purple", "Orange"];
          chartData.datasets[0].data = testData.data;//[12, 19, 3, 5, 2, 3];

          APP.chart.createChart(chartToUpdate, chartData, chartOptions);
          console.log("in testChartCB: " + chartToUpdate.id + ", " + chartToUpdate.chartRef);
        } else {
          console.log("data: Not yet!");
        }
      },
      //test chart function. Mimics function in html
      testChart = function (chartObject) {

        console.log("testChart");
        var passthru = [chartObject];
        testChartCB(testData, passthru);
      };

  //To re-size the chart according to screen size of windows
  Chart.defaults.global.responsive = true;
  Chart.defaults.global.maintainAspectRatio = false;

  instance = {
    initCharts: initCharts,
    findChart: findChart,
    createChart: createChart,
    testChart: testChart,
    updateChart: updateChart,
    //chartDataArray: chartDataArray,
    getChartDataArray: getChartDataArray,
    getChartObject: getChartObject,
    updateAllCharts: updateAllCharts,
    //customTooltip: customTooltip
  };
  APP.chart = instance;
  //APP.chart.charDataArray = chartDataArray;

}(APP));
