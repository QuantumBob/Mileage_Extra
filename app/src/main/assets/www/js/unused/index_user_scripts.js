/*jshint browser:true */
/*global $ */
/*
function todaysDate() {
    "use strict";

    var date = new Date(), day = date.getDate(), month = date.getMonth() + 1,
        year = date.getFullYear(), today;

    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }

    today = year + "-" + month + "-" + day;
    //$("#theDate").attr("value", today);
    return today;
}

// Set the date field to today
function setDate(inDate){

    if(inDate == "today"){
        var myDate = todaysDate();

        $("#miles_date").attr("value", myDate);
        //document.getElementById('#miles_date').value = today.getDate();
        //$('#miles_date').val(new Date());//.toDateInputValue());
    }
}

function addEntry(){
    "use strict";

    //var db = $(document).SQLitePlugin.openDatabase({name:'mileage.db', location:'default'});
    //var db = window.sqlitePlugin.openDatabase({name:'mileage.db', location:'default'});

    $(document).sqlitePlugin.openDatabase({name: 'mileage.db', location: 'default'}, function(db) {
        db.transaction(function(tx) {$(document).alert("Done!");},
            function(err) {$(document).console.log('Open database ERROR: ' + JSON.stringify(err));});
    });
}

// Auto run this function at startup when script loads
(function(){
"use strict";

    // quick test function
    function test(inText){
        $('#test').text(inText);
    }

    // hook up event handlers
    function register_event_handlers(){

        // button  #commit
        $(document).on("click", "#commit", function(evt){
            // your code goes here
            addEntry();
            return false;
        });
        // back button
        $(document).on("backbutton", function(evt){
            // handle the back button
            navigator.app.exitApp();
        });
    }

    // initialize the app
    function initApp(){
        register_event_handlers();

        setDate("today");
    }

    // Call this automatically when the script loads at startup
    document.addEventListener("deviceready", initApp, false);
    //document.addEventListener("backbutton", onBackKeyDown, false);

})();
*/
