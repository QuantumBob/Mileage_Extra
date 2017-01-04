/*global MILEAGE, miles:true, db:true, device:false, Chart, moment, localforage, rwkDevtools*/

/*************************
 * Mileage (version: 0.0.1)
 * Mileage calculator for drivers
 * http://smalldoor.org/
 *
 * Copyright 2016 Rob Kirk
 * Released under the MIT license
 * https://github.com/QuantumBob/mileage_html5_designer/blob/master/LICENSE.md
 *************************/

//#########################
// entry.js
//#########################
"use strict";
var APP;
(function () {


    // Set to "true" if you want the console.log messages to appear.
  var instance,
      initDebug = function () {

        console.log("in APP.initDebug");

        if (window.device ) {
          console.log("device.version: " + device.version);
          console.log("device.model: " + device.model);
          console.log("device.platform: " + device.platform);
          console.log("device.version: " + device.version);
        }
      },
        // Using a splash screen is optional. This function will not fail if none is present.
        // This is also a simple study in the art of multi-platform device API detection.
      hideSplashScreen = function () {

            // see https://github.com/01org/appframework/blob/master/documentation/detail/%24.ui.launch.md
            // Do the following if you disabled App Framework autolaunch (in index.html, for example)
            // $.ui.launch() ;

        if (navigator.splashscreen && navigator.splashscreen.hide) {   // Cordova API detected
          navigator.splashscreen.hide();
        }
      };
  instance = {
    initDebug: initDebug,
    hideSplashScreen: hideSplashScreen
  };
  APP = instance;

}());

window.MILEAGE = APP || {};

/* Constructor
(function (APP) {
    "use strict";
    var MILEAGE = {};
    APP.MILEAGE = MILEAGE;

}(this));*/


//##########################
// rwkHelpers.js
//##########################
"use strict";
(function (APP) {


  var instance,
        // quick test function
      test = function (inText) {

        $('#test').text(inText);
      },
        // converts string to funciton call
      executeFunctionByName = function (functionName, context, args) {
        var namespaces = functionName.split("."),
            func = namespaces.pop(),
            i;
            // get the args list from executeFunctionByName
        args = [].slice.call(arguments).splice(2);

        for (i = 0; i < namespaces.length; i = i + 1) {
          context = context[namespaces[i]];
        }
        return context[func].apply(context, args);
      },
        //calculate todays date and return it
      /*todaysDate = function () {

        var date = new Date(), day = date.getDate(), month = date.getMonth() + 1,
            year = date.getFullYear(), today;

        if (month < 10) {
          month = "0" + month;
        }
        if (day < 10) {
          day = "0" + day;
        }

        today = year + "-" + month + "-" + day;
        return today;
      },*/
        //formats date to DD MM YYYY
      formatDate = function (inDate) {

        return moment(inDate, ["MM-DD-YYYY", "DD-MM-YYYY", "YYYY-MM-DD", "YY-MM-DD", "DD-MM-YY"]).format("DD-MM-YYYY");

        /*if (inDate.length === 10) {
          var temp = inDate.split('-');
          temp.reverse();
          inDate = temp.join('-');
        }
        return inDate;*/
      };

  instance = {
    test: test,
    executeFunctionByName: executeFunctionByName,
    //todaysDate: todaysDate,
    formatDate: formatDate
  };
  APP.helpers = instance;
}(APP));



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

          var chartTest = chartDataArray.some(function(chartObject){
            if (chartObject.id === canvasId){
              chart = chartObject;//.chartRef;
            }
          });

//          chart = chartDataArray.find(function (chartObject) {
//            if (chartObject.id === canvasId) {
//              return chartObject.chartRef;
//            }
//          });
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


// ##############################
// rwkDB
// ##############################
"use strict";
(function (APP) {

  var db = localforage,
      instance,

      //no need to initialize the database when using localforage
      initDB = function () {

      },
      prepareBackup = function () {
        return new Promise (function (resolve, reject){
          readDb().then(function(results){
            if (Object.keys(results).length > 0){
              console.log("backup prepared");
              resolve (results);
            } else {
              console.log("no backup");
              reject (false);
            }
          }).catch(function(err){
            console.log("prepareBackup: " + err);
          });
        });
      },
      // returns an array of all the keys that start with 'table'
      getTableKeys = function (table) {

        db.keys().then(function(keys){
          var filtered = keys.filter(function(value){
            return value.includes(table.toLowerCase());
          });
          return filtered;
        }).catch(function(err){
          console.log('error: ' + err);
          return false;
        });
        return false;
      },
      // returns true if the table is empty
      isTableEmpty = function (table) {

        var tableKeys;
        db.keys().then(function(keys){
          tableKeys = keys.filter(function(value){
            return value.includes(table.toLowerCase());
          });
          if (tableKeys.length === 0) {
            return true;
          } else {
            return false;
          }
        }).catch(function(err){
          console.log('error: ' + err);
        });
      },
      // returns true if the table has data
      isTablePopulated = function (table) {
        var tableKeys = getTableKeys(table);

        if (tableKeys.length === 0) {
          return false;
        } else {
          return true;
        }
      },
      // write to console to check everything worked
      writeRecordCount = function (preKey) {

        return new Promise (function (resolve, reject){
          db.length().then(function(numberOfKeys){
            //console.log('Record count : ' + numberOfKeys);
            rwkDevtools.setWatchOneByValue('Record count', numberOfKeys);
            resolve(numberOfKeys);
          }).catch(function(err){
            console.log('error: ' + err) ;
            reject("writeRecordCount: " + err);
          });
        });
        return false;
      },
      //populate database with test values
      addTestData = function () {

        //date, startMiles, endMiles, fuelBought, priceUnit, mileage
        var keyArray = ['1', '2', '3', '4'],
            key = '1',
            data = [{date:'2015-02-01', startMiles:201, endMiles:301, fuelBought:38, priceUnit:1.1, mileage:3.15},
                    {date:'2015-02-03', startMiles:202, endMiles:302, fuelBought:39, priceUnit:1.2, mileage:3.16},
                    {date:'2015-02-04', startMiles:203, endMiles:303, fuelBought:37, priceUnit:1.3, mileage:3.17},
                    {date:'2015-02-02', startMiles:204, endMiles:304, fuelBought:36, priceUnit:1.4, mileage:3.18}];

        return new Promise (function(resolve, reject){
          for (var i=0; i < keyArray.length; i++){
            addDataToTable('Mileage', keyArray[i], data[i]);
          }
          if (i === keyArray.length) {
            resolve(true);
          } else {
            reject (false);
          }
        });
      },
      // gets the next key for a given table
      getNextKey = function (table) {

        var nextKey = 1,
            key;// = table + '_' + nextKey;

        return new Promise (function(resolve, reject){
          //db.startsWith(key).then(function(results){
          db.startsWith(table.toLowerCase()).then(function(results){
            for(key in results){
              key = key.split('_');
              key = parseInt(key[1], 10);
              if (key >= nextKey) {
                nextKey = key + 1;
              }
            }
            resolve (nextKey);
          }).catch(function(err){
            console.log("getNextKey: " + err);
            resolve(nextKey);
          });
          return false;
        });
        return false;
      },
      // split and return the index part of the table key
      getIndexFromKey = function (key) {
        var keyIndex = key.split('_');
        keyIndex = parseInt(keyIndex[keyIndex.length - 1], 10);
        if (typeof keyIndex === "number") {
          return keyIndex;
        } else {
          return null;
        }
      },
      //add current mileage values to table:tblMileage
      addDataToTable = function (table, key, data) {

        var tableKey = table.toLowerCase() + '_' + key;
        return new Promise (function(resolve, reject){

          // add key to data
          data.key = tableKey;
          db.setItem(tableKey, data).then(function (value) {
            //console.log('addData: ' + value.mileage);
            resolve();
          }).catch(function(err){
            console.log('addDataToTable error: ' + err);
            reject();
          });
        });
        return false;
      },
      // delete all entries in the sent table
      depopulateTable = function (table, callback) {

        return new Promise (function(resolve, reject){

          db.startsWith(table.toLowerCase()).then(function(results){

            db.removeItems(Object.keys(results)).then(function(){
              console.log("items removed");
              resolve (true);
            }).catch(function(err){
              console.log("removeItem error: " + err);
              reject ( false);
            });
          }).catch(function(err){
            console.log('depopulateTable error: ' + err);
            return false;
          });
        });
        return false;
      },
      // delete all entries in the database
      depopulateDb = function () {

        return new Promise (function(resolve, reject){
          db.clear().then(function() {
            console.log('Depopulated table OK');
            resolve(true);
          }).catch(function(err){
            console.log('error: ' + err);
            reject(false);
          });
        });
      },
      // checks and removes data from given table
      deleteDataFromTable = function (table, inKey, data) {

        return new Promise (function(resolve, reject){

          db.removeItem(inKey).then(function(){
            console.log("item deleted");
            resolve(true);
          }).catch(function(err){
            console.log("deleteDataFromTable-removeItem: " + err);
            reject(false);
          });
        });
        return false;
      },
      // read a specified table of the database.
      // query is an object specifying the type of data to return and the value or *
      readDbTable = function (table, query) {

        var key,
            rows = [],
            what = query.what,
            where = query.where,
            value = query.value;

        if (where === "date") {
          value = moment (value, "DD-MM-YYYY").format("YYYY-MM-DD");
        }

        return new Promise (function(resolve, reject){
          db.startsWith(table.toLowerCase()).then(function(results){

            if (what === "all"){
              for (key in results){
                console.log("results[key][where]: " + results[key][where]);

                if (results[key][where] === value || where === "all") {
                  console.log("value = " + results[key][where]);
                  console.log("results[key] = " + results[key]);
                  rows.push(results[key]);
                }
              }
              resolve(rows);
            } else {
              for (key in results){
                if (results[key][where] === value) {
                  console.log("value = " + results[key][where]);
                  console.log("results[key][what] = " + results[key][what]);
                }
              }
              reject(false);
            }
          }).catch(function(err){
            console.log("readDbTable-startsWith: " + err);
          });
        });
        return false;
      },
      // read the entire database for backing up records
      readDb = function (){//(query, passthru) {

        var keyArray = [];

        return new Promise (function(resolve, reject){

          db.getItems().then(function(results){
            resolve(results);
          }).catch(function(err){
            console.log("readDb-getItems: " + err);
            reject(false);
          });

          /*db.startsWith(table.toLowerCase()).then(function(results){
            keyArray = Object.keys(results);

            if (keyArray.length > 0) {
              db.getItems(keyArray).then(function(results){
                resolve(results);
              }).catch(function(err){
                console.log("readDb-getItems: " + err);
              });
            }
          }).catch(function(err){
            console.log("readDb-startsWith: " + err);
          });*/
        });
        return false;
      };

  instance = {
    readDb: readDb,
    readDbTable: readDbTable,
    depopulateTable: depopulateTable,
    addDataToTable: addDataToTable,
    isTableEmpty: isTableEmpty,
    isTablePopulated: isTablePopulated,
    addTestData: addTestData,
    writeRecordCount: writeRecordCount,
    initDB: initDB,
    depopulateDb: depopulateDb,
    deleteDataFromTable: deleteDataFromTable,
    getIndexFromKey: getIndexFromKey,
    getNextKey: getNextKey,
    prepareBackup: prepareBackup
  };
  APP.db = instance;

}(APP));




"use strict";
// ##############################
// rwkMileage
// ##############################
(function (APP) {

  var instance,
      currentData = {},
      helpers  = APP.helpers,
      // Set the date field
      setDate = function (inDate) {

        if (inDate === "today") {

          $("#date").val(moment().format("YYYY-MM-DD"));
        } else {
          //$("#date").val(helpers.formatDate(inDate));
          $("#date").val(moment(inDate, ["MM-DD-YYYY", "DD-MM-YYYY", "YYYY-MM-DD", "YY-MM-DD", "DD-MM-YY"]).format("DD-MM-YYYY"));
        }
      },
      // gets the data from the form fields
      getInputData = function () {

        console.log("getInputData");
        var i,
            data = {
              date: document.getElementById("date").value,
              startMiles: document.getElementById("startMiles").value,
              endMiles: document.getElementById("endMiles").value,
              fuelBought: document.getElementById("fuelBought").value,
              priceUnit: document.getElementById("priceUnit").value
            };

        console.log("date: " + data.date);
        if (moment(data.date, "YYYY-MM-DD", true).isValid()) {
          for (i in data) {
            if (data.hasOwnProperty(i)) {
              if (i !== 'date' && i !== 'endMiles') {
                if (data[i] === "" || isNaN(data[i])) {
                  if (document.getElementById("result").value !== "") {
                    document.getElementById("result").textContent = "";
                  }
                  return false;
                }
              }
            }
          }
        }
        return data;
      },
      // calculate the mileage on the fly as each value is inputed
      calcMileage = function (inputData) {

        var convertion = 1.00,
            mileage;

        if (document.getElementById("units").value !== 'litres') {
          convertion = 0.219;
        }

        if (inputData.endMiles === "") {
          //mileage = s_miles.value / (fuel_bought.value / (price_unit.value/convertion));
          mileage = (inputData.startMiles * inputData.priceUnit) / (inputData.fuelBought * convertion);
        } else {
          //mileage = (e_miles.value - s_miles.value) / (fuel_bought.value / (price_unit.value/convertion));
          mileage = ((inputData.endMiles - inputData.startMiles) * inputData.priceUnit) / (inputData.fuelBought * convertion);
        }
        if (isNaN(mileage)) {
          return false;
        }
        return mileage.toFixed(2);
      },
      // updates the result label with the current mileage
      showMileage = function (mileage) {
        document.getElementById("result").textContent = mileage.toFixed(2);
      },
      // change the units from gallons to litres or back
      changeUnits = function (inElement) {

        var toggle = document.querySelector('#mpu'),
            label = document.querySelector('label[for="priceUnit"]'),
            data;

        if (inElement.value === 'litres') {
          label.textContent = 'Price per gallon';
          toggle.textContent = "mpg";
          inElement.value = 'gallons';
        } else {
          label.textContent = 'Price per litre';
          toggle.textContent = "mpl";
          inElement.value = 'litres';
        }

        if (document.getElementById("result").textContent !== "") {

          data = getInputData();
          if (data) {
            document.getElementById("result").textContent = APP.miles.calcMileage(data);
          }
        }
      },
      // takes an object and in
      populateInputFields = function (inputs) {

        console.log("empty" + inputs);
      },
      // sort the data by date before adding it to Chart
      orderByDate = function (toSort) {

        if (Array.isArray(toSort)) {
          toSort.sort ( function(a, b) {
            return moment(a.date).isAfter(b.date, "day");
          });
          return toSort;
        } else {
          console.log("not an array");
          return false;
        }

      },
      // convert the data object to an array
      dataObjectToArray = function (data) {
        var outArray = $.map (data, function(value, index) {
          return [value];
        });
        return outArray;
      },
      // mileage datum callback
      mileageDatumCB = function (rows) {//, passthru){

        var i,
            j,
            key;

        /*if (rows.length === 1) {
          console.log("there is " + rows.length + " row. Row date: " + rows[0].date);
        } else if (rows.length === 0) {
          console.log("there are " + rows.length + " rows.");
          return false;
        } else {
          console.log("there are " + rows.length + " rows. First row date: " + rows[0].date);
        }*/

        for (let row of rows) {
          for (key in row) {
            currentData[key] = row[key];
            //console.log("row[" + key + "] = " + row[key]);
            $("#" + key).val(row[key]);
          }
        }
        $("#result").text(APP.miles.calcMileage(rows[0]));
      },
      // get one record from mileage table
      mileageDatum = function (key) {

        var passthru = [key],
            table = "Mileage",
            query = {
              what: "all",
              where: "key",
              value: key
            };

        //APP.db.readDb(table, mileageDatumCB, passthru).then(function(data){
        APP.db.readDbTable(table, query).then(function(data){
          mileageDatumCB(data);
        });
      },
      // what to do when a chart is clicked
      onChartClicked = function (evt) {
        console.log("onChartClicked");

        var chartObject = APP.chart.findChart($(evt.target)),
            activePoints = chartObject.chartRef.getElementsAtEvent(evt),
            clickedElementIndex,
            key,
            data;

        if (activePoints.length > 0) {
          $("#btn-grp-Edit").slideDown();
          $("#btn-grp-Commit").slideUp();
          //get the internal index of slice in pie chart
          clickedElementIndex = activePoints[0]._index;
          //get specific key by index
          key = chartObject.chartRef.data.datasets[0].keys[clickedElementIndex];
          console.log("keys: " + key);
          // get data for this label
          data = chartObject.chartRef.data.datasets[0];

          mileageDatum(key);

          //get value by index
          //var value = chartObject.chartRef.data.datasets[0].data[clickedElementIndex];

          /* other stuff that requires slice's label and value */
        }
      },
      // clears all input fields
      clearInputs = function () {

        $("#startMiles").val("");
        $("#endMiles").val("");
        $("#fuelBought").val("");
        $("#priceUnit").val("");

        $("#result").text("");
      },
      // returns the current data in the form or null
      getCurrentData = function () {
        if ($.isEmptyObject(currentData)) {
          return null;
        } else {
          return currentData;
        }
      },
      //mileage data callback function
      mileageDataCB = function (resultSet, chartObject) {

        var i = 0,
            row,
            chartToUpdate,
            chartData = {labels: [], datasets: [{label: null, fill: null, backgroundColor: null, data: [], keys: []}]},
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
              }/*,
              tooltips: {
                custom: APP.chart.customTooltip
              }*/
            };

        chartData.datasets[0].label = "Mileage";
        chartData.datasets[0].fill = false;
        chartData.datasets[0].backgroundColor = "rgba(91, 206, 30, 0.4)";
        chartOptions.scales.yAxes[0].ticks.min = 0;
        chartOptions.scales.xAxes[0].categoryPercentage = 0.9;
        chartOptions.scales.xAxes[0].barPercentage = 0.2;
        //chartOptions.scales.yAxes[0].ticks.stepSize = 1;
        //chartOptions.scales.yAxes[0].scaleLabel.display = true;
        //chartOptions.scales.yAxes[0].gridLines.color = "rgba(255, 0, 0, 1)";

        chartToUpdate = chartObject;

        if (chartToUpdate) {

          var resultsArray = dataObjectToArray(resultSet);//orderByDate(resultSet);
          resultsArray = orderByDate(resultsArray);

          resultsArray.forEach( function(row) {
            //console.log("resultSet[row]: " + resultSet[row]);
            chartData.labels.push(APP.helpers.formatDate(row.date));
            chartData.datasets[0].data.push(row.mileage);
            chartData.datasets[0].keys.push(row.key);
          });
          /*for (var key in resultSet) {
            if (resultSet.hasOwnProperty(key)) {

              row = resultSet[key];
              chartData.labels.push([key, APP.helpers.formatDate(row.date)]);
              chartData.datasets[0].keys.push(key);
              chartData.datasets[0].data.push(row.mileage);
            }
          }*/
          //chartData.datasets[0].data = orderByDate(chartData.datasets[0].data);
          APP.chart.createChart(chartToUpdate, chartData, chartOptions);
        } else {
          console.log("data: Not yet!");
        }
      },
      //mileage data retreival
      mileageData = function (chartObject) {

        var passthru = [chartObject],
            table = "Mileage",
            query =  {
              what: "all",
              where: "all",
              value: "all"
            };

        APP.db.readDbTable(table, query).then(function(data){
          mileageDataCB(data, chartObject);
        });
      };

  instance = {
    //register_event_handlers: register_event_handlers,
    setDate: setDate,
    populateFields: populateInputFields,
    onChartClicked: onChartClicked,
    mileageData: mileageData,
    changeUnits: changeUnits,
    clearInputs: clearInputs,
    calcMileage: calcMileage,
    showMileage: showMileage,
    getInputData: getInputData,
    getCurrentData: getCurrentData
  };
  APP.miles = instance;

}(APP));


//#########################
// mileageEvents.js
//#########################
"use strict";
(function (APP) {

  var instance,
        //helpers = APP.helpers,
      initApp = function () {

        var Mileage = APP,
            mod,
            propKey;


        $("#btn-grp-Edit").hide();
        Mileage.miles.setDate("today");
            //Mileage.db.initDB();
        Mileage.db.writeRecordCount('Mileage');
        if (Mileage.db.isTableEmpty('Mileage')) {
          console.log("No data yet");
        } else {
          Mileage.chart.initCharts();
        }

            //call each modules event handler routine
        for (mod in Mileage) {
          if (Mileage.hasOwnProperty(mod)) {
            for (propKey in Mileage[mod]) {
              if (Mileage[mod].hasOwnProperty(propKey)) {
                if (typeof Mileage[mod][propKey] === 'function' && propKey === 'registerEventHandlers') {
                  Mileage[mod][propKey]();
                }
              }
            }
          }
        }

        APP.hideSplashScreen();    // after init is good time to remove splash screen; using a splash screen is optional
      },
        // create click event handler for chart
      registerCanvasEventHandlers = function (canvas) {

        var i,
            chartsArray = APP.chart.getChartDataArray();
        if (canvas === undefined) {
                // process all canvases
          for (i = 0; i < chartsArray.length; i = i + 1) {

            canvas = document.getElementById('canvas-' + chartsArray[i].id);
            if (canvas.nodeName.toLowerCase() === 'canvas') {

              $(canvas).on("click", APP.miles.onChartClicked);
            }
          }
        } else {
          if (canvas.nodeName.toLowerCase() === 'canvas') {

            $(canvas).on("click", APP.miles.onChartClicked);
          }
        }
      },
      registerEventHandlers = function () {

            // update button
        $(document).on("click", "#btnUpdate", function () {

          var chartObject,
              data = APP.miles.getInputData(),
              keyIndex = APP.db.getIndexFromKey(APP.miles.getCurrentData().key);

          if (data && keyIndex) {
            data.mileage = APP.miles.calcMileage(data);
            if (data.mileage) {
              APP.db.addDataToTable('Mileage', keyIndex, data).then(function(){

                chartObject = APP.chart.getChartObject('mileage');
                console.log("chartObject[0].id : " + chartObject.id + ', ' + chartObject.chartRef);
                APP.chart.updateChart(chartObject);//'chartObject.id === mileage-chart';
                APP.miles.clearInputs();
                APP.miles.setDate("today");
                APP.db.writeRecordCount();
                return true;
              }).catch(function(err){
                console.log("Commit Button-addDataToTable:" + err);
              });
            }
            $("#btn-grp-Edit").slideUp();
            $("#btn-grp-Commit").slideDown();
            APP.miles.clearInputs();
            APP.miles.setDate("today");
          }
        });
            // cancel button
        $(document).on("click", "#btnCancel", function () {
          $("#btn-grp-Edit").slideUp();
          $("#btn-grp-Commit").slideDown();
          APP.miles.clearInputs();
          APP.miles.setDate("today");
        });
            // delete button
        $(document).on("click", "#btnDelete", function () {

          var chartObject,
              data = APP.miles.getInputData(),
              key = APP.miles.getCurrentData().key;

          if (data && key) {

            APP.db.deleteDataFromTable('Mileage', key, data).then(function(){

              chartObject = APP.chart.getChartObject('mileage');
              console.log("chartObject[0].id : " + chartObject.id + ', ' + chartObject.chartRef);
              APP.chart.updateChart(chartObject);//'chartObject.id === mileage-chart';
              APP.miles.clearInputs();
              APP.miles.setDate("today");
              APP.db.writeRecordCount();
              return true;
            }).catch(function(err){
              console.log("Commit Button-addDataToTable:" + err);
            });

            $("#btn-grp-Edit").slideUp();
            $("#btn-grp-Commit").slideDown();
            APP.miles.clearInputs();
            APP.miles.setDate("today");
          }
        });
            // commit button
        $(document).on("click", "#btnCommit", function () {

          var chartObject,
              data = APP.miles.getInputData();

          if (data) {
            data.mileage = APP.miles.calcMileage(data);
            if (data.mileage) {

              APP.db.getNextKey('Mileage').then(function(key){
                APP.db.addDataToTable('Mileage', key, data).then(function(){

                  chartObject = APP.chart.getChartObject('mileage');
                  console.log("chartObject[0].id : " + chartObject.id + ', ' + chartObject.chartRef);
                  APP.chart.updateChart(chartObject);//'chartObject.id === mileage-chart';
                  APP.miles.clearInputs();
                  APP.miles.setDate("today");
                  APP.db.writeRecordCount();
                  return true;
                }).catch(function(err){
                  console.log("Commit Button-addDataToTable:" + err);
                });
              }).catch(function(err){
                console.log("Commit Button-getNextKey:" + err);
              });
            }
          }
          return false;
        });
            // clear button
        $(document).on("click", "#btnClear", function () {

          APP.miles.clearInputs();
          APP.miles.setDate("today");
        });
            // units toggle
        $(document).on("change", "#units", function () {

          APP.miles.changeUnits(this);

        });
            // text input change
        $(document).on("input", ".calc-miles", function () {

          var data = APP.miles.getInputData();

          if (data) {
            document.getElementById("result").textContent = APP.miles.calcMileage(data);
          }
        });
            // back button
        $(document).on("backbutton", function () {

          navigator.APP.exitApp();
        });
            // all canvases
        registerCanvasEventHandlers();
        return false;
      };

  instance = {
    initApp: initApp,
    registerEventHandlers: registerEventHandlers
  };
  APP.events = instance;

}(APP));


//##########################
// exit.js
//##########################
"use strict";

//document.addEventListener("DOMContentLoaded", APP.initApp, false);
//document.addEventListener("deviceready", APP.initApp, false);
document.addEventListener("deviceready", APP.events.initApp, false);
document.addEventListener("DOMContentLoaded", APP.events.initApp, false);
