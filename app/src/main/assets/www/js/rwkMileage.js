/*global moment, APP*/
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

        var data = {
              date: document.getElementById("date").value,
              startMiles: document.getElementById("startMiles").value,
              endMiles: document.getElementById("endMiles").value,
              fuelBought: document.getElementById("fuelBought").value,
              priceUnit: document.getElementById("priceUnit").value
            },
            i;

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
            label = document.querySelector('label[for="price_unit"]'),
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
          toSort.sort ( (a, b) => {
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
        var outArray = $.map (data, (value, index) => {
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

          resultsArray.forEach( (row) => {
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
            query;

        APP.db.readDb(table, mileageDataCB, passthru).then(function(data){
          //APP.db.readDbTable(table, query).then(function(data){
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
