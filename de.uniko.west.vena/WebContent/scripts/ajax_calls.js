var ajJsonReplacerNode = ['id','name','type','posX','posY','info','gender']; //TODO

function srvGetUser(currentUser, callback){
  	console.log("checking account data");
			
	$.ajax({
		url: "LoginServlet",
		type: "POST",
		contentType: "application/json;charset=utf-8",
		dataType: "json",
		data: JSON.stringify(currentUser),
		success: function(data){
			console.log("got correct data for: " + name);
			callback(true, data);
		},
		error: function(data, textStatus, errorThrown) {
			console.log("failed account check... " + data + " - " + textStatus);
			callback(false, null);
		}
	});
}


function srvGetGraph(dsName, isNew, callback){
 	console.log("fetching "+ (isNew?"new":"existing") + " graph data for dataset: " + dsName);
 	
 	var sendData = "{\"isNew\": "+ (isNew?"\"true\"":"\"false\"") + ", \"dsName\": \"" + dsName +"\"}";
 	console.log(sendData);
 	
	$.ajax({
		url: "GraphGetServlet",
		type: "POST",
		contentType: "application/json;charset=utf-8",
		dataType: "json",
		data: sendData,
		success: function(data){
			callback(true, data);
		},
		error: function(data, textStatus, errorThrown) {
			callback(false, null);
		}
	});
}

function srvAddGraphNode(newNode, callback){
	//actually multiple nodes here... newNode is an array of nodes
	var sendData = "{ \"action\": \"add\", \"nodes\": " + JSON.stringify(newNode, ajJsonReplacerNode) + " }";
	console.log("add Data: " + sendData);
	$.ajax({
		url: "GraphUpdateServlet",
		type: "POST",
		contentType: "application/json;charset=utf-8",
		dataType: "json",
		data: sendData,
		success: function(data){
			callback(true, data);
		},
		error: function(data, textStatus, errorThrown) {
			callback(false, null);
		}
	});
}	

function srvDeleteGraphNode(delNodes, callback){
	//actually multiple nodes here... delNode is an array of nodes
	var sendData = "{ \"action\": \"delete\", \"nodes\": " + JSON.stringify(delNodes, ajJsonReplacerNode) + " }";
	console.log("del Data: " + sendData);
	$.ajax({
		url: "GraphUpdateServlet",
		type: "POST",
		contentType: "application/json;charset=utf-8",
		dataType: "json",
		data: sendData,
		success: function(data){
			callback(true, data);
		},
		error: function(data, textStatus, errorThrown) {
			callback(false, null);
		}
	});
}
	
function srvModifyGraphNode(oldNode, newNode, callback){
	var sendData = "{ \"action\": \"modify\", \"newNode\": " + 
						JSON.stringify(newNode, ajJsonReplacerNode) + ", " +
						"\"oldNode\": " +
						JSON.stringify(oldNode, ajJsonReplacerNode) + " }";
	console.log("mod Data: " + sendData);
	$.ajax({
		url: "GraphUpdateServlet",
		type: "POST",
		contentType: "application/json;charset=utf-8",
		dataType: "json",
			data: sendData,
			success: function(data){
				callback(true, data);
			},
			error: function(data, textStatus, errorThrown) {
				callback(false, null);
			}
		});
}

