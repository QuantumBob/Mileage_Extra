/*global $:false, localforage:false, APP, Promise, moment, rwkDevtools*/
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
              key = parseInt(key, 10);
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
            console.log('addData: ' + value.mileage);
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

          db.removeItem(inKey).then(()=>{
            console.log("item deleted");
            resolve(true);
          }).catch((err)=>{
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
                if (results[key][where] === value) {
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
      // read the database for mileage records
      readDb = function (table, callback, passthru) {

        var keyArray = [];

        return new Promise (function(resolve, reject){
          db.startsWith(table.toLowerCase()).then(function(results){
            keyArray = Object.keys(results);

            if (keyArray.length > 0) {
              db.getItems(keyArray).then(function(results){
                //callback(results, passthru);
                resolve(results);
              }).catch(function(err){
                console.log("readDb-getItems: " + err);
              });
            }
          }).catch(function(err){
            console.log("readDb-startsWith: " + err);
          });
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
    getNextKey: getNextKey
  };
  APP.db = instance;

}(APP));


