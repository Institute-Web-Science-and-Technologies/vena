<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script>
$(function() {
    var nDialog;
        
    nDialog = $( "#new_dialog" ).dialog({
      position:	{
  		  my: "left top",
   		  at: "left bottom",
  		  of: "#new_btn",
  		  collision: "none"},
      autoOpen: false,
      height: 350,
      width: 260,
      resizable:false,
      draggable:false,
      modal: true,
      open: function(event, ui) { 
    	  	$(".ui-dialog-titlebar", ui.dialog | ui).hide();
      		$('.ui-widget-overlay').bind('click', function(){ $("#new_dialog").dialog('close'); }); },
      show: {
        effect: "blind",
        duration: 200
      },
      hide: {
        effect: "blind",
        duration: 200
      },
      buttons: {
          "anlegen": createNewDataset,
          "abbrechen": function() {
        	nDialog.dialog( "close" );
          }
        }
    });
    
    
    
    $( "#new_btn" ).button().on( "click", function() {
    	$('#create_ego_name').val("");
    	$('#create_ds_name').val("");
    	nDialog.dialog( "open" );
    });
    
    function createNewDataset(){
    	var dsName = $('#create_ds_name').val();
    	if (dsName=="" || dsName == null){
       		console.log("invalid dataset name...");	
       	}else{
    		ctrGetGraphForDataset(dsName,true);
       	}
    	nDialog.dialog( "close" );
    }
    
    $('#create_ego_name').on('input',function(e){
        var inputText = $('#create_ego_name').val();
        var d = new Date();

        var month = d.getMonth()+1;
        var day = d.getDate();

        var timestamp = (day<10 ? '0' : '') + day + '_' + (month<10 ? '0' : '') + month + '_' + d.getFullYear();
        
        $('#create_ds_name').val(inputText + "#" + timestamp); 
    });
    
    $('#create_ego_name').bind('keypress', function (event) {
        var regex = new RegExp("^[a-zA-Z0-9._\b]+$");
        var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
        if (!regex.test(key)) {
           event.preventDefault();
           return false;
        }
    });
    
});

</script>
<div id="new_dialog" title="Neu" class="dropdown_menu">
	  	<table id="newDialogTable" width="100%" cellspacing="10">
	        <tbody>
	            <tr>
	                <td>Name des Egos/Datensatzes</td>
	            </tr>
	            <tr>
	                <td><input type="text" name="create_ego_name" id="create_ego_name" value="" class="text ui-widget-content ui-corner-all"></td>
	            </tr>
		        <tr>
	                <td>erzeugter Datensatz:</td>
	            </tr>
	            <tr>
	                <td><input type="text" name="create_ds_name" id="create_ds_name" value="" readOnly class="text ui-widget-content ui-corner-all"></td>
	            </tr>
	     	</tbody>
	     	
	  	</table>
	  	<input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
    
</div>