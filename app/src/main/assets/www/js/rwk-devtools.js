/*global $, APP, Promise*/
"use strict";

var rwkDevtools = (function () {

    var watchRow1Div = document.createElement("div"),
        watchRow2Div = document.createElement("div"),

        addDevToolEvents = function () {

        $(document).on("click", "#btnFirst", function () {

            APP.db.depopulateTable ('Mileage').then(function(value){

                APP.chart.updateChart('Mileage');
                APP.db.writeRecordCount();

            }).catch(function(err){
              console.log("devtools one error");
            });
        });

        $(document).on("click", "#btnSecond", function () {

            APP.db.addTestData().then(function(value){

                APP.chart.updateChart('Mileage');
                APP.db.writeRecordCount();

            }).catch(function(err){
              console.log("devtools two error");
            });

        });

        $(document).on("click", "#btnThird", function () {
            APP.chart.updateAllCharts();
            console.log("dev Update all charts");
        });

        $(document).on("click", "#btnForth", function () {
                    APP.db.prepareBackup().then(function(results) {
                        var jsonResults = JSON.stringify(results);
                        Android.makeBackup(jsonResults);
                    }).catch(function(err){
                        console.log("prepareBackup: " + err);
                    });
         });

        $(document).on("click", "#btnFifth", function () {
            APP.db.depopulateDb();
            APP.chart.updateAllCharts();
            console.log("dev Clear Db");
        });
    },
        setWatchOneByValue = function (watchText, value) {
            watchRow1Div.textContent = watchText + " :" + value;
        },
        setWatchOneByFunction = function () {
            console.log("setWatch");
        },
        setWatchOneById = function () {
            console.log("setWatch");
        },
        setWatchTwoByValue = function () {
            console.log("setWatch");
        },
        setWatchTwoByFunction = function () {
            console.log("setWatch");
        },
        setWatchTwoById = function () {
            console.log("setWatch");
        },
        injectCSS = function  () {

        var file = location.pathname.split( "/" ).pop(),
            link = document.createElement( "link" );

        link.href = file.substr( 0, file.lastIndexOf( "." ) ) + ".css";
        link.rel = "stylesheet";

        document.getElementsByTagName( "head" )[0].appendChild( link );
    },
        initDevTools = function () {
        var wrapperDiv = document.createElement("div"),
            columnLeftDiv = document.createElement("div"),
            columnRightDiv = document.createElement("div"),
            outerRowDiv = document.createElement("div"),
            //watchRow1Div = document.createElement("div"),
            //watchRow2Div = document.createElement("div"),
            firstButton = document.createElement("button"),
            secondButton = document.createElement("button"),
            thirdButton = document.createElement("button"),
            forthButton = document.createElement("button"),
            fifthButton = document.createElement("button"),
            devToolsTitle = document.createTextNode("RWK Devtools"),

            firstSpan = document.createElement("span"),
            secondSpan = document.createElement("span"),
            thirdSpan = document.createElement("span"),
            forthSpan = document.createElement("span"),
            fifthSpan = document.createElement("span");


        wrapperDiv.id = "rwk-devtools-wrapper";
        wrapperDiv.className += "container-fluid";

        outerRowDiv.id = "rwk-devtools-row1";
        outerRowDiv.className += "row";

        columnLeftDiv.id = "rwk-devtools-col1";
        columnLeftDiv.className += "col-xs-6";

        columnRightDiv.id = "rwk-devtools-col2";
        columnRightDiv.className += "col-xs-6";
        columnRightDiv.className += " text-left";

        watchRow1Div.id = "rwk-devtools-row2";
        watchRow1Div.textContent = "watch 1...";

        watchRow2Div.id = "rwk-devtools-row3";
        watchRow2Div.textContent = "watch 2...";

        firstSpan.id = "rwk-devtools-span1";
        firstSpan.className += "btn-image";

        secondSpan.id = "rwk-devtools-span2";
        secondSpan.className += "btn-image";

        thirdSpan.id = "rwk-devtools-span3";
        thirdSpan.className += "btn-image";

        forthSpan.id = "rwk-devtools-span4";
        forthSpan.className += "btn-image";

        fifthSpan.id = "rwk-devtools-span5";
        fifthSpan.className += "btn-image";

        firstButton.id = "btnFirst";
        firstButton.className += " btn";
        firstButton.className += " btn-default";
        firstButton.alt = 'Trash can';
        firstButton.title = 'Clear table data';

        secondButton.id = "btnSecond";
        secondButton.className += " btn";
        secondButton.className += " btn-primary";
        secondButton.alt = 'Plus symbol';
        secondButton.title = 'Add test data';

        thirdButton.id = "btnThird";
        thirdButton.className += " btn";
        thirdButton.className += " btn-success";
        thirdButton.alt = 'Refresh symbol';
        thirdButton.title = 'Refresh Chart';

        forthButton.id = "btnForth";
        forthButton.className += " btn";
        forthButton.className += " btn-info";
        forthButton.alt = 'Update symbol';
        forthButton.title = 'Backup Database';

        fifthButton.id = "btnFifth";
        fifthButton.className += " btn";
        fifthButton.className += " btn-danger";
        fifthButton.alt = 'Change symbol';
        fifthButton.title = 'Clear Db';

        document.body.insertBefore(wrapperDiv, document.body.firstChild);

        wrapperDiv.appendChild(outerRowDiv);

        outerRowDiv.appendChild(columnLeftDiv);
        outerRowDiv.appendChild(columnRightDiv);

        columnLeftDiv.appendChild(devToolsTitle);
        columnLeftDiv.appendChild(watchRow1Div);
        columnLeftDiv.appendChild(watchRow2Div);

        columnRightDiv.appendChild(firstButton);
        columnRightDiv.appendChild(secondButton);
        columnRightDiv.appendChild(thirdButton);
        columnRightDiv.appendChild(forthButton);
        columnRightDiv.appendChild(fifthButton);

        firstButton.appendChild(firstSpan);
        secondButton.appendChild(secondSpan);
        thirdButton.appendChild(thirdSpan);
        forthButton.appendChild(forthSpan);
        fifthButton.appendChild(fifthSpan);

        addDevToolEvents();
    };

    return {
        initDevTools: initDevTools,
        setWatchOneByValue: setWatchOneByValue
    };
}());

document.addEventListener("deviceready", rwkDevtools.initDevTools, false);
document.addEventListener("DOMContentLoaded", rwkDevtools.initDevTools, false);
