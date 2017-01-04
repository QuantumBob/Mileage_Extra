/*jslint browser:true, devel:true, unparam:true*/
/*global $:false, intel:false, APP*/
// ##############################
// rwkDB
// ##############################
"use strict";
(function (APP) {


    var db,
        instance,
        //window.internalData = {"labels": [], "datasets": [{"label": null, "backgroundColor": null, "data": []}], "options": {}};

        //initialize the database.
        // Can we use db = $(document).SQLitePlugin.openDatabase...?
        initDB = function () {

            db = null;
            db = window.sqlitePlugin.openDatabase({name: 'mileage.db', location: 'default'},
                                                  function (db) {
                    console.log('Db opened okay');
                },
                                                  function (err) {
                    console.log('Open database ERROR: ' + JSON.stringify(err));
                });
            if (!db) {
                console.log("db=" + db);
            }
        },
        // write to console to check everything worked
        writeRecordCount = function (table) {

            db.transaction(function (tx) {
                tx.executeSql('SELECT count(*) AS mycount FROM ' + table, [], function (tx, rs) {
                    console.log('Record count : ' + rs.rows.item(0).mycount);
                }, function (tx, error) {
                    console.log('SELECT error: ' + error.message);
                });
            });
        },
        //populate database with test values
        addTestData = function (callback, passthru) {

            db.transaction(function (tx) {
                tx.executeSql('DROP TABLE IF EXISTS tblMileage', [], function (tx, resultSet) {
                    console.log("Table dropped");
                });
                tx.executeSql('CREATE TABLE IF NOT EXISTS tblMileage (date, startMiles, endMiles, fuelBought, priceUnit, mileage)', [], function (tx, resultSet) {
                    console.log("Table created");
                });
                tx.executeSql('INSERT INTO tblMileage VALUES (?,?,?,?,?,?)', ["2016-01-01", 200, 300, 38, 1.2, 3.16], function (tx, resultSet) {
                    console.log("Data inserted");
                    if (typeof callback === 'function') {
                        callback(passthru);
                    }
                });
            }, function (error) {
                console.log('Transaction ERROR: ' + error.message);
            }, function () {
                console.log('Test data added OK');
                writeRecordCount();
            });
        },
        //add current mileage values to table:tblMileage
        addDataToTable = function (table, data) {

            var sqlStr,
                dataStr;

            sqlStr = 'CREATE TABLE IF NOT EXISTS ' + table + ' (' + Object.keys(data) + ')';
            console.log("sqlStr : " + sqlStr);

            db.transaction(function (tx) {
                tx.executeSql(sqlStr); //'CREATE TABLE IF NOT EXISTS tblMileage (date, start_miles, end_miles, fuel_bought, price_unit, mileage)'
                //tx.executeSql('INSERT INTO tblMileage VALUES (?,?,?,?,?,?)', [inDate, inStart_Miles, inEndMiles, inFuel_Bought, inPrice_Unit, inMileage]);
                sqlStr = 'INSERT INTO tblMileage VALUES (?,?,?,?,?,?)';
                dataStr = '[data.date, data.startMiles, data.endMiles, data.fuelBought, data.priceUnit, data.mileage]';
                console.log("dataStr: " + dataStr);
                tx.executeSql(sqlStr, dataStr); //'INSERT INTO tblMileage VALUES (?,?,?,?,?,?)', [inDate, inStart_Miles, inEndMiles, inFuel_Bought, inPrice_Unit, inMileage]
            }, function (error) {
                console.log('Transaction ERROR: ' + error.message);
            }, function () {
                console.log('Populated database OK');
                writeRecordCount();
            });
        },
        depopulateCB = function () {
            console.log("in depopulateCB");
            writeRecordCount();
        },
        // delete all entries in the database
        depopulateTable = function (callback) {

            db.transaction(function (tx) {

                tx.executeSql('DROP TABLE IF EXISTS tblMileage', [], function (tx, resultSet) {
                    if (typeof callback === 'function') {
                        callback();
                    }
                });
            }, function (error) {
                console.log('Transaction ERROR: ' + error.message);
            }, function () {
                console.log('Depopulated table OK');
            });
        },
        // read the database for mileage reacords
        readDb = function (query, callback, passthru) {

            db.transaction(function (tx) {

                tx.executeSql(query, [], function (tx, resultSet) {

                    if (resultSet.rows && resultSet.rows.length) {
                        if (typeof callback === 'function') {
                            callback(resultSet, passthru);
                        }
                    }

                }, function (tx, error) {
                    console.log('SELECT error: ' + error.message);
                });
            });
        };

    instance = {
        readDb: readDb,
        depopulateTable: depopulateTable,
        addDataToTable: addDataToTable,
        addTestData: addTestData,
        writeRecordCount: writeRecordCount,
        initDB: initDB,
        depopulateCB: depopulateCB
    };
    APP.db = instance;

}(APP));


