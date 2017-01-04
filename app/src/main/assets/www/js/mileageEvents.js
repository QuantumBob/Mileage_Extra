/*global $:false, APP:true */
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
