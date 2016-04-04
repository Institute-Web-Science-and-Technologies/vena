var URI_PREFIX = "http://west.uni-koblenz.de/vena/";

var user = {
	name : "",
	password : ""
}

var probFolder="";

var graphNodes = [];


function mdlInit(accUser){
	user.name = accUser.name;
}

function mdlGetNode(id){
	var result = null;
	for (var i = 0; i < graphNodes.length; i++) {
		if (graphNodes[i].id == id){
			result = graphNodes[i];
			i = graphNodes.length;
		}
	}
	return result;
}

function mdlContainsNode(id){
	var result = false;
	for (var i = 0; i < graphNodes.length; i++) {
		if (graphNodes[i].id == id){
			result = true;
			i = graphNodes.length;
		}
	}
	return result;
}


function mdlAddNode(node){
	graphNodes.push(node);
}


function mdlRemoveNode(node){
	var index = graphNodes.indexOf(node);
	
	if(index>-1){
		graphNodes.splice(index,1);
	}
}

function mdlCreateVenaID(){
	var uid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
					    var t = Math.random()*16|0, v = c == 'x' ? t : (t&0x3|0x8);
					    return v.toString(16);
					});
	
	return probFolder + uid;
}

function mdlCreateRandomFamilyName(){
	return "family" + Math.floor((Math.random()*1000000)).toString();
}

// generate string with @places leading zeroes from integer @num
function mdlZeroPad(num, places){
	var zero = places - num.toString().length + 1;
	return Array(+(zero > 0 && zero )).join("0")+num;
}

