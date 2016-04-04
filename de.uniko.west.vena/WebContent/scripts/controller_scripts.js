var ctrSelected = null;

var ACTION_ADD_T	= "add_top",
	ACTION_ADD_L 	= "add_left",
	ACTION_ADD_R 	= "add_right",
	ACTION_ADD_B 	= "add_bot",
	ACTION_DELETE 	= "delete";


function ctrGetGraphForDataset(dsName,isNew){
	$.event.trigger( "loadingStart" );


	function callback(success, nodeList){
		if (success){
			
			probFolder = URI_PREFIX + "prob/" + dsName.substring(0, dsName.lastIndexOf("#")) + "/";
			console.log("new probFolder: " + probFolder);
			for (var i = 0; i < nodeList.length; i++) {
				mdlAddNode(nodeList[i]);
			}
			
			ctrDrawGraph();
			
			$.event.trigger( "loadingStop" );
		}else{
			alert("Getting graph from server was unsuccessful..");
		}
	}
	
	srvGetGraph(dsName, isNew, callback);

}


function ctrDrawGraph(){
	console.log("starting to draw");
	easlRedrawAll();
}


function ctrEntitySelected(id){
	ctrSelected = mdlGetNode(id);
//	$("#editPane").find("input[type='button']").each( function(){ $(this).prop('disabled',false); } );
	$("#edModifyBtn").prop('disabled',false);
	$("#edName").prop('readonly', false).val(ctrSelected.name);
	
	var info = ctrSelected.info;
	if (info != "ego"){
		$("#edInfo").prop('readonly', false);
	}
	$("#edInfo").val(info);
	
	$("#edID").val(ctrSelected.id);
	
	$("#edGender").val(ctrSelected.gender).selectmenu("refresh");
}

function ctrEntityUnselected(){
	ctrSelected = null;
//	$("#editPane").find("input[type='button']").each( function(){ $(this).prop('disabled',true); } );
	
	$("#edModifyBtn").prop('disabled',true);
	
	$("#editPane :input[type='text']").prop('readonly', true).val("");
	
	$("#edGender").val(MDL_GENDER_U).selectmenu("refresh");
	
}

function callback(success, data){
	if(success){
		ctrSelected.connected.push(newNode.id);
		rasterPositions[newPos[0]][newPos[1]] = newNode.id;
		mdlAddNode(newNode);
		
		ctrDrawGraph();
		
		easlForceUnselect();
		ctrEntityUnselected();
		
	}else{
		
	}
	
};

function ctrDelNodeFromUI(id){
	
	var delNode = mdlGetNode(id);
	
	
	console.log("deleting: " + delNode.name);
	
	function callback(success, data){
		if(success){

//			unselect in view
			easlForceUnselect();
			ctrEntityUnselected();
			
//			remove connections TODO
//			for (var i = 0; i < delNode.connected.length; i++) {
//				var otherNode = mdlGetNode(delNode.connected[i]);
//				var index = otherNode.connected.indexOf(delNode.id);
//				if (index > -1){
//					otherNode.connected.splice(index,1);
//				}
//			}

			console.log("removing from graphnodes...");
			mdlRemoveNode(delNode);
			console.log("... done");
			
			ctrDrawGraph();
			
			console.log("deleted node");
		}else{
			console.log("deleting node was unsuccessful... nothing changed." );
		}
		
	};
	srvDeleteGraphNode([delNode], callback);
}

function ctrModifyNodeFromUI(){
	var oldNode = ctrSelected;
	//awesomesauce jquery objectclone
	var newNode = jQuery.extend(true, {}, oldNode);
	newNode.name = 	$("#edName").val();
	newNode.info = 	$("#edInfo").val();
	newNode.gender = $("#edGender").val();
	ctrModifyNodeON(oldNode, newNode, true);
}

function ctrModifyNodeON(oldNode, newNode, redraw){
	
	function callback(success, data){
		if(success){

			mdlRemoveNode(oldNode)
			mdlAddNode(newNode);
			
			if (redraw){
				ctrDrawGraph();
			}
			
			easlForceUnselect();
			ctrEntityUnselected();
			
		}else{
			
		}
		
	};
	
	srvModifyGraphNode(oldNode, newNode, callback);
}

function ctrCalcNewPos(relate, addAction){
	// drawlogic here?
	var offset = 50;
	var newPosXY = new Array(2);
	var relXY = getPosXY(relate);
	console.log("HAXXX");
	console.log(relate);
	console.log(relXY);
	
	switch(addAction){
	case ACTION_ADD_T:
		newPosXY = [relXY[0], relXY[1]-offset];
		break;

	case ACTION_ADD_L:
		newPosXY = [relXY[0]-offset, relXY[1]];
		break;

	case ACTION_ADD_R:
		newPosXY = [relXY[0]+offset, relXY[1]];
		break;

	case ACTION_ADD_B:
		newPosXY = [relXY[0], relXY[1]+offset];
		break;


	default:
		newPosXY = [relXY[0] + 10, relXY[1] +10];
		break;
	}
	
	return newPosXY;
	
}

function ctrUpdateDatasetList(data){
	
	var datasetSelect = $('#datasets');
	
	for (var i = 0; i < data.length; i++) {
		datasetSelect.append(
		        $('<option></option>').val(data[i]).html(data[i])
		    );
	}
	
}



function ctrAddNodeFromUI(type) { //id, addAction){
//	var currentNode = mdlGetNode(id);
//	var newCurrentNode = jQuery.extend(true, {}, currentNode);
	
	var newNode;
	
//	if ( addAction == ACTION_ADD_T ) {
//		newNode = new VFamily();
//		newNode.name = mdlCreateRandomFamilyName();
//		newNode.children.push(id);
//		newCurrentNode.topFamilies.push[newNode.id];
//	}
//	if ( addAction == ACTION_ADD_B ) {
//		newNode = new VFamily();
//		newNode.name = mdlCreateRandomFamilyName();
//		if (newCurrentNode.gender == MDL_GENDER_M){
//			newNode.mother = id;
//		}else{
//			newNode.father = id;
//		}
//		newCurrentNode.botFamilies.push[newNode.id];
//	}
//	if ( addAction == ACTION_ADD_L || addAction == ACTION_ADD_R) {
//		newNode = new VPerson();
//		newNode.name = "";
//		newNode.friends.push(id);
//		newCurrentNode.friends.push(newNode.id);
//	}
	
	if (type == MDL_NODETYPE_FAMILY){
		newNode = new VFamily();
		newNode.name = mdlCreateRandomFamilyName();
	}else if (type == MDL_NODETYPE_PERSON ){
		newNode = new VPerson();
		newNode.name = "";
	}
	
		
//	var newPos = ctrCalcNewPos( currentNode, ACTION_ADD_T );
	
	setPosXY(newNode, [450,450]);
	

	
	function callback(success, data){
		if(success){
			console.log("server responded success in adding: " + data);
			
//			ctrModifyNodeON(currentNode, newCurrentNode, true);
			mdlAddNode(newNode);
			ctrDrawGraph();
			
		}else{
		}
	};
	srvAddGraphNode([newNode], callback);
	
}

