var MDL_NODETYPE_PERSON = "person",
	MDL_NODETYPE_FAMILY = "family";

var MDL_GENDER_M = "männlich",
	MDL_GENDER_F = "weiblich",
	MDL_GENDER_U = "unbekannt";

//------- VNode --------
function VNode() {
	this.id =			mdlCreateVenaID();
	this.name = 		"";
	this.type =			"";
	this.posX = 		"0";
	this.posY =			"0";
}

// FUUUUUUU doesnt work at all cause nodes come from backend(json) and are ducktyped instead of created... no functions for them :(
//VNode.prototype.connected = function(){
//	return [];
//}
//VNode.prototype.getPosXY = function(){
//	return [parseInt(this.posX), parseInt(this.posY)];
//}
//VNode.prototype.setPosXY = function(posXY){
//	this.posX = posXY[0].toString();
//	this.posY = posXY[1].toString();
//}
//------- VPerson --------

function VPerson(){
	VNode.call(this);
	this.type = MDL_NODETYPE_PERSON;
	this.info = "";
	this.gender = MDL_GENDER_U;
	this.topFamilies = [];
	this.botFamilies = [];
	this.friends = [];
}

VPerson.prototype = new VNode();
VPerson.prototype.constructor=VPerson;
VPerson.prototype.connected = function(){
	var result = [];
	result = result.concat(this.topFamilies, this.botFamilies, this.friends);
	result = result.filter(function(n){ return n != undefined });
	return result;
}

//------- VFamily --------
function VFamily(){
	VNode.call(this);
	this.type = MDL_NODETYPE_FAMILY;
	this.mother = null;
	this.father = null;
	this.children = [];
}

VFamily.prototype = new VNode();
VFamily.prototype.constructor=VFamily;
VFamily.prototype.connected = function(){
	var result = [];
	result = result.concat([this.mother],[this.father],this.children);
	result = result.filter(function(n){ return n != undefined });
	return result;
}



function getPosXY(node){
	return [ parseInt(node.posX), parseInt(node.posY) ];
}

function setPosXY(node, posXY){
	node.posX = posXY[0].toString();
	node.posY = posXY[1].toString();
}

function getFamilyOfParent(parent){
	var family = null
	var tmp;
	for (var i = 0; i < parent.connected.length; i++) {
		if (parent.connected[i].type == MDL_NODETYPE_FAMILY){
			tmp = mdlGetNode(parent.connected[i]);
			if (isParentInFamily(parent, tmp)){
				family = tmp;
			}
		}
	}
	return family;
}


function createTopFamily(child){
	var family = new VFamily();
	family.name = mdlCreateRandomFamilyName();
	family.id = mdlCreateVenaID();
	family.children.push(child.id);
	child.topFamily.push(family.id);
	return family;
}

function createBotFamily(parent){
	var family = new VFamily();
	family.name = mdlCreateRandomFamilyName();
	family.id = mdlCreateVenaID();
	if (parent.gender == MDL_GENDER_M){
		family.father = parent.id;
	} else { //gender = undefined -> female...?!?!?! rejecting would be troublesome here... so yes for now
		family.mother = parent.id;
	}
	parent.botFamily.push(family.id);
	return family;
}

function isParentInFamily(parent, family){
	return family.father == parent.id || family.mother == parent.id ;
}

function isChildInFamily(child, family){
	console.log("checking child in family: " + child.name + "  "+ family.name);
	return $.inArray(child.id, family.children) < 0 ? false : true;
}