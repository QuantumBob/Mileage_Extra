/*global APP, moment*/
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
