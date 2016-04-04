<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Vena</title>
<link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css">
<link rel="stylesheet" href="vena.css">
<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/json3/3.3.2/json3.min.js"></script> -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
<script src="https://code.createjs.com/easeljs-0.8.2.min.js"></script>
<script src="https://code.createjs.com/tweenjs-0.6.2.min.js"></script>
<script src="scripts/createjs_scripts.js"></script>
<!-- <script src="scripts/ajax_calls.js"></script> -->
<!-- <script src="scripts/loginform_script.js"></script> -->

<script type="text/javascript">

	$body = $("body");
	$(document).on({
		loadingStart: function(){ $body.addClass("loading");    },
		loadingStop:  function(){ $body.removeClass("loading"); }
	});
	
	
// 	$.holdReady(true);
	$.event.trigger( "loadingStart" );
	$.when(
			$.getScript("scripts/model.js"),
			$.getScript("scripts/node_ops.js"),
			$.getScript("scripts/ajax_calls.js"),
			$.getScript("scripts/controller_scripts.js"),
			$.Deferred(function( deferred ){
				$( deferred.resolve );
			})
		).done(function(){
// 			holdReady(false);
			
			
			
			$.event.trigger( "loadingStop" );
			$(".login_vis").hide();
		});
	
	
	$(document).ready(function(){
		$(".login_vis").hide();
		var canvas = $('#graphCanvas');
		var ctx = canvas[0].getContext("2d");
		initCanvas();
		
		$("button, input:submit, input:button").button();
		
		$(function(){
			$("#login_dlg_button").load("loginform.jsp");
		});
		
	    $(function(){
			$("#sidebar").load("sidebar.jsp");
		});
	    
	    $(function(){
			$("#open_btn").load("opendialog.jsp");
		})
	   	$(function(){
			$("#new_btn").load("newdialog.jsp");
		})
		$( "#logout_button" ).button().on( "click", function() {
			location.reload(); 
    	});
	    
	   	
	    
// 		$("#canvasContainer").scrollTop(500);
// 		$("#canvasContainer").scrollLeft(500);

	});
		
</script>


</head>

<body>

<div id="header">
	<h1>Vena</h1>
</div>
<div id="nav">
	<div id="nav_left">
		<input type="button" class="login_vis" id="new_btn" value="neu"/>
		<input type="button" class="login_vis" id="open_btn" value="öffnen"/>
	</div>
	<div id="nav_right">
  		
    		<input type="button" class="logout_vis" id="login_dlg_button" value="Login"/>
    		<input type="button" class="login_vis"  id="logout_button"    value="Logout"/>
<!--     		<div id="welcometext"></div> -->
  		
	</div>
</div>

<div id="center">

<div id="canvasContainer">
	<canvas id="graphCanvas" height= "1000" width="1000"></canvas>
</div>

<div id="right">
	<div id="sidebar"></div>
</div>

</div>

<div class="modal"></div>
</body>
</html>