/*jslint browser:true, devel:true, white:true, vars:true */

var chartStore = { id1:25,id2:30 };
var myID = "id3";

if (myID in chartStore)
	console.log("key found");
else
	console.log("No key");
chartStore[myID] = 450;
console.log("now using a for loop");
for (var key in chartStore) {
  console.log("key " + key + " has value " + chartStore[key]);
}
if (myID in chartStore)
	console.log("Now key found");
else
	console.log("Still no key");

data: data,
	options: {
		scales: {
				xAxes: [{
						stacked: true
				}],
				yAxes: [{
						stacked: true
				}]
			}
		}
	}
});

