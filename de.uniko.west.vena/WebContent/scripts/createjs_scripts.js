var stage,content,canvas,bg;
var update = true;
var rasterSize = 20;

var distX = 180;
var distY = 120;
var entityWidth = 140;
var entityHeight = 70;
var familyWidth = 20;
var familyHeight = 20;

var bobbleList = new Array(0);
var lineList = new Array(0);

var draggingLine = false, dragLine = null;
var dragLineSrc, dragLineSrcShape;

var centerPosX,centerPosY;

var handlePositionChange = false;

var selShad,hoverShad,unselShad;
var viewSelected = null;


function initCanvas() {
	console.log("init canvas");
	canvas = document.getElementById("graphCanvas");
    stage = new createjs.Stage(canvas);
    stage.name = "VenaStage";
    centerPosX = stage.canvas.width/2;
    centerPosY = stage.canvas.height/2;
	
    content = new createjs.Container();
    
    stage.enableMouseOver(10);
    createjs.Ticker.addEventListener("tick", stage);
    stage.mouseEnabled = true;
    stage.addEventListener("click", easlHandleSelect);
    initShadows();
    
	stage.addEventListener("pressmove", easlHandleBubbleDrag);
//	stage.addEventListener("pressup", easlHandleBubbleDroppedPosAbort);
    
    easlCreateBackground();
    stage.addChild(content);
	
	stage.update();
}


function initShadows(){
//	unselShad = new createjs.Shadow("#888888", 1, 1, 3);
//	hoverShad = new createjs.Shadow("#505050", 1, 1, 3);
//	selShad = new createjs.Shadow("#303030", 2, 2, 4);
	unselShad = new createjs.Shadow("#404040", 2, 2, 3);
	hoverShad = new createjs.Shadow("#101010", 2, 2, 4);
	selShad = new createjs.Shadow("#303099", 2, 2, 4);
}

function easlCreateBackground() {
	var bgColor = "#ffffef";
	var rasterColor = "#dddddd";
	
	bg = new createjs.Shape();
	bg.graphics.beginFill( bgColor ).drawRect(0,0,stage.canvas.width,stage.canvas.height);
	
	var stepy = stage.canvas.height / rasterSize;
	var stepx = stage.canvas.width / rasterSize;
	
	for (var i = 0; i < 20; i++){
		bg.graphics.beginFill(rasterColor).drawRect(0, 
				stepy*i, stage.canvas.width, 1 );
	}
	for (var i = 0; i < 20; i++){
		bg.graphics.beginFill(rasterColor).drawRect(stepx*i, 
				0, 1, stage.canvas.height );
	}
	stage.addChild(bg);
}

function easlDrawConnection(fromXY, toXY){
	var lineColor = "#999999";
	
	var top = null;
	var bot = null;
	
//	draw line from upper components bottom to lower component top side 
	if (fromXY[1] > toXY[1]){
		top = fromXY; bot = toXY;
	}else{
		top = toXY; bot = fromXY;
	}
	
	var linestartx = top[0];
	var linestarty = top[1] - ( entityHeight / 2 );
	var lineendx = bot[0];
	var lineendy = bot[1] + ( entityHeight / 2 );
	
//	draw line
	var g = new createjs.Graphics();
	g.setStrokeStyle(2);
	g.beginStroke(lineColor);
	g.moveTo(linestartx,linestarty);
	g.lineTo(linestartx,linestarty+(lineendy-linestarty)/2);
	g.lineTo(lineendx, 	linestarty+(lineendy-linestarty)/2);
	g.lineTo(lineendx,lineendy);
	
	
	var line = new createjs.Shape( g );
	line.shadow = new createjs.Shadow("#bbbbbb", 2, 2, 3);
	content.addChild(line);
	stage.update();
}


function easlCreateFamilyNode(posXY, name, id){
	var frameBgColor = "#d9d9f2";
	var btnAddBgColor = "#CBE6CB", btnDelBgColor = "#ee6666";
	
	var entity = new createjs.Container();
	var dragger = new createjs.Container();
	
	var frame = new createjs.Shape();
	frame.name = id;
	frame.cursor = "pointer";
	frame.mouseEnabled = true;
	frame.addEventListener("click", easlHandleSelect);
	frame.addEventListener("mouseover", easlHandleHover);
	frame.addEventListener("mouseout", easlHandleOut);
	
	frame.graphics.setStrokeStyle(0.5);
	frame.graphics.beginStroke("#888888");

	frame.graphics.beginFill(frameBgColor).drawRoundRect(0, 0, familyWidth, familyHeight, 5);
	
	entity.x = posXY[0];
	entity.y = posXY[1];
	entity.vid = id;
	entity.setBounds( posXY[0], posXY[1], familyWidth, familyHeight );
	
	var btns = [ easlCreateAddButton(entity, familyWidth/2-5, familyHeight, btnAddBgColor, id, 	ACTION_ADD_B),
	             easlCreateAddButton(entity, -10, familyHeight/2-5, btnAddBgColor, id, 			ACTION_ADD_L),
	             easlCreateAddButton(entity, familyWidth, familyHeight/2-5, btnAddBgColor, id, 	ACTION_ADD_R),
	             easlCreateDeleteButton(entity, familyWidth, -10, btnDelBgColor, id, 			ACTION_DELETE)
	            ]; 
	
	frame.shadow = unselShad;
	

//	entity.addEventListener("pressmove", easlHandleNodeDragNDrop);
	
	dragger.entity = entity;
	dragger.addEventListener("pressmove", easlHandleNodeDragNDrop);
	dragger.addEventListener("pressup", easlHandleDroppedPos);
	
	for (var i = 0; i < btns.length; i++) {
		entity.addChild(btns[i]);
	}
	
	dragger.addChild(frame);
	entity.addChild(dragger);
	
	return entity;
	
}


//posXY defines center of Entity to draw
function easlCreateEntity(posXY, name, info, id) {
	var frameBgColor = "#d9d9f2";
	var btnAddBgColor = "#CBE6CB", btnDelBgColor = "#ee6666"; 
	
    var textOffset = 5;
	var infoOffset = 20;
	
	var entity = new createjs.Container();
	
	var dragger = new createjs.Container();
	
	var frame = new createjs.Shape();
	frame.name = id;
	frame.cursor = "pointer";
	frame.mouseEnabled = true;
	frame.addEventListener("click", easlHandleSelect);
	frame.addEventListener("mouseover", easlHandleHover);
	frame.addEventListener("mouseout", easlHandleOut);
	
	
	frame.graphics.setStrokeStyle(0.5);
	frame.graphics.beginStroke("#888888");
	
//	Draw add-bubblesframe
//	frame.graphics.beginFill(frameBgColor).drawRoundRect(5, -10, 11, 11, 1);
//	frame.graphics.beginFill(frameBgColor).drawRoundRect(105, -10, 11, 11, 1);
	
	frame.graphics.beginFill(frameBgColor).drawRoundRect(0, 0, entityWidth, entityHeight, 5);
	frame.graphics.beginFill("888888").drawRect(5, infoOffset, entityWidth-10, 1);
	
	entity.x = posXY[0];
	entity.y = posXY[1];
	entity.vid = id;
	entity.setBounds( posXY[0], posXY[1], entityWidth, entityHeight );
	
	var btns = [ easlCreateAddButton(entity, entityWidth/2-5, -10, btnAddBgColor, id, 			ACTION_ADD_T),
	             easlCreateAddButton(entity, entityWidth/2-5, entityHeight, btnAddBgColor, id, 	ACTION_ADD_B),
	             easlCreateAddButton(entity, -10, entityHeight/2-5, btnAddBgColor, id, 			ACTION_ADD_L),
	             easlCreateAddButton(entity, entityWidth, entityHeight/2-5, btnAddBgColor, id, 	ACTION_ADD_R),
	             easlCreateDeleteButton(entity, entityWidth, -10, btnDelBgColor, id, 			ACTION_DELETE)
	            ]; 
	
	frame.shadow = unselShad;
			
	var nameText = new createjs.Text( name, "bold 12px Arial", "#222222");
	nameText.x = textOffset;
	nameText.y = textOffset;
	
	var infoText = new createjs.Text( info, "bold 12px Arial", "#222222");
	infoText.x = textOffset;
	infoText.y = textOffset + infoOffset;


//	entity.addEventListener("pressmove", easlHandleNodeDragNDrop);
	
	dragger.entity = entity;
	dragger.addEventListener("pressmove", easlHandleNodeDragNDrop);
	dragger.addEventListener("pressup", easlHandleDroppedPos);
	
	var btnl = (info =="ego" ? btns.length-1 : btns.length ); //dont delete ego
	for (var i = 0; i < btnl; i++) {
		entity.addChild(btns[i]);
	}
	
	dragger.addChild(frame);
	entity.addChild(dragger);
	entity.addChild(nameText);
	entity.addChild(infoText);
	
	return entity;
}

function easlCreateAddButton(parent, posX, posY, bgColor, id, action){
	var btn = new createjs.Shape();
	btn.graphics.beginFill(bgColor).drawRoundRect(posX,posY,11,11,1);
	btn.shadow = unselShad;
	btn.vid = id;
	btn.action = action;
	btn.setBounds(parent.getBounds().x + posX, parent.getBounds().y + posY, 11, 11);
	btn.parent = parent;
	console.log(btn.getBounds());
	btn.addEventListener("mouseover", easlHandleHover);
	btn.addEventListener("mouseout", easlHandleOut);
//	btn.addEventListener("click", easlHandleAddClick);
	

	btn.addEventListener("mousedown", easlHandleBubbleDragStart);
	stage.addEventListener("pressup", easlHandleBubbleDroppedPos);
	
	bobbleList.push(btn);
	return btn;
}



function easlCreateDeleteButton(parent, posX, posY, bgColor, id, action){
	var btn = new createjs.Shape();
	btn.graphics.beginFill(bgColor).drawRoundRect(posX,posY,11,11,1);
	btn.shadow = unselShad;
	btn.vid = id;
	btn.action = action;

	btn.setBounds(parent.getBounds().x + posX, parent.getBounds().y + posY, 11, 11);
	btn.addEventListener("mouseover", easlHandleHover);
	btn.addEventListener("mouseout", easlHandleOut);
	btn.addEventListener("click", easlHandleDeleteClick);
	return btn;
}

function easlRedrawAll(){
	easlClear(true);
	console.log("drawing " + graphNodes.length + " entities");
	for (var i = 0; i < graphNodes.length; i++) {
		easlDrawEntity( graphNodes[i] );
	}
	
	stage.update();
}

function easlDrawEntity(node){
	var posXY;
	if (node.posX < 0 || node.posY < 0){
		posXY = [centerPosX, centerPosY];
	}else{
		posXY = [node.posX, node.posY];	
	}
	
	 
	var nodeShape;
	
	if (node.type == MDL_NODETYPE_PERSON){
		nodeShape = easlCreateEntity( posXY, node.name , node.info, node.id );
	}else if (node.type == MDL_NODETYPE_FAMILY){
		nodeShape = easlCreateFamilyNode(posXY, node.name, node.id);
	}else{
		console.log("Warning: nodetype not set");
	}
	
	content.addChild(nodeShape);
}

function easlClear(doUpdate){
	content.removeAllChildren();
	bobbleList = new Array();
	if (doUpdate){
		stage.update();
	}
}



function easlForceUnselect(){
	if (viewSelected != null){
		viewSelected.shadow = unselShad;
		viewSelected = null;
		stage.update();
	}
}

function easlHandleSelect(event){
	var id = event.target.name;
	if (viewSelected != null){ // unselect
		viewSelected.shadow = unselShad;
	}
	if (event.target.name == null){ // no target with id selected
		viewSelected = null;
		ctrEntityUnselected();
	}else{ // new selection
		viewSelected = event.target;
		event.target.shadow = selShad;
		ctrEntitySelected(id);
		stage.update();
	}
//	if (viewSelected!= stage) { 
//		var tween = createjs.Tween.get(event.target, {loop: false})
//			.to({shadow: selShad},800, createjs.Ease.bounceOut);
//	}
	
}

function easlHandleHover(event){
	if (viewSelected != event.target){
		event.target.shadow = hoverShad;
		stage.update();
	}
	
}

function easlHandleBubbleDragStart(event){
	draggingLine = true;
	if (dragLine == null){
		dragLine = new createjs.Shape();
		stage.addChild(dragLine);
		dragLineSrcShape = event.currentTarget;
	}
	var bnds=event.currentTarget.getBounds();
	dragLineSrc = new createjs.Point( bnds.x + bnds.width/2, bnds.y+bnds.height/2 );
	
}

function easlHandleBubbleDrag(event){
	if (draggingLine){
		dragLine.graphics.clear().setStrokeStyle(2.4).beginStroke("#888899").moveTo(
				dragLineSrc.x, dragLineSrc.y).lineTo(stage.mouseX, stage.mouseY);
		stage.update();
	}
	

}

function easlHandleBubbleDroppedPos(event){
	if (draggingLine){
		draggingLine = false;
		console.log("dragStop!");
		console.log("FROM: " + dragLineSrcShape.vid + " TO " + event.currentTarget.vid);
		var b;
		var hit = false;
		for (var i = 0; i < bobbleList.length; i++) {
			b = bobbleList[i].getBounds();
			if (stage.mouseX > b.x && stage.mouseX < b.x + b.width && stage.mouseY > b.y && stage.mouseY < b.y + b.height){
				
				console.log("FROM: " + dragLineSrcShape.vid + " TO " + bobbleList[i].vid);
				hit = true;
				i = bobbleList.length;
			}
		} 
		if (!hit) stage.removeChild(dragLine);
		dragLine = null;
		stage.update();
	}
}

function checkValidLine(from, to){
	
	
}

function easlHandleBubbleDroppedPosAbort(event){
	if (draggingLine){
		draggingLine = false;
		console.log("dragStop BANG DinG OW!");
		stage.removeChild(dragLine);
		dragLine = null;
		stage.update();
	}
}

function easlHandleDroppedPos(event){
	var id = event.currentTarget.entity.vid;
	console.log("saving new node-position of " + id)
	if (handlePositionChange){
		var oldNode = mdlGetNode(id);
		// jQuery object clone method!
		var newNode = jQuery.extend(true, {}, oldNode);
		setPosXY(newNode, [event.currentTarget.entity.x,event.currentTarget.entity.y]);
		
		ctrModifyNodeON(oldNode, newNode, false);
		handlePositionChange = false;
	}
}

function easlHandleOut(event){
	if (viewSelected != event.target){
		event.target.shadow = unselShad;
		stage.update();
	}
}

function easlHandleNodeDragNDrop(event){
	event.currentTarget.entity.x = event.stageX - event.currentTarget.entity.getBounds().width/2;
	event.currentTarget.entity.y = event.stageY - event.currentTarget.entity.getBounds().height/2;
	handlePositionChange = true;
	stage.update();
}

function easlHandleAddClick(event){
	console.log(event.currentTarget.vid + " wants to add: " + event.currentTarget.action);
	ctrAddNodeFromUI(event.currentTarget.vid, event.currentTarget.action);
}

function easlHandleDeleteClick(event){
	console.log(event.currentTarget.vid + " wants to be deleted");
	ctrDelNodeFromUI(event.currentTarget.vid);
}