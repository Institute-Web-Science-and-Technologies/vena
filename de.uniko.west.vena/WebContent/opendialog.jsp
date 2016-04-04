<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script>
$(function() {
    var oDialog;
        
    oDialog = $( "#open_dialog" ).dialog({
      position:	{
  		  my: "left top",
   		  at: "left bottom",
  		  of: "#open_btn",
  		  collision: "none"},
      autoOpen: false,
      height: 350,
      width: 260,
      resizable:false,
      draggable:false,
      modal: true,
      open: function(event, ui) { 
    	  	$(".ui-dialog-titlebar", ui.dialog | ui).hide();
      		$('.ui-widget-overlay').bind('click', function(){ $("#open_dialog").dialog('close'); }); },
      show: {
        effect: "blind",
        duration: 200
      },
      hide: {
        effect: "blind",
        duration: 200
      },
      buttons: {
          "öffnen": openDataset,
          "abbrechen": function() {
        	  oDialog.dialog( "close" );
          }
        }
    });
    
    
    
    $( "#open_btn" ).button().on( "click", function() {
    	oDialog.dialog( "open" );
    });
    
    function openDataset(){
       	var dsName = $('#datasets').val();
       	console.log(dsName);
       	if (dsName=="" || dsName =="-1" || dsName == null){
       		console.log("invalid dataset selected...");	
       	}else{
       		ctrGetGraphForDataset(dsName,false);
       	}
       	oDialog.dialog( "close" );
    }
    
});

</script>
<div id="open_dialog" title="Öffnen" class="dropdown_menu">
	  	<table id="openDialogTable" width="100%" cellspacing="10">
	        <tbody>
	            <tr>
	                <td>Datensatz auswählen</td>
	            </tr>
	            <tr>
	                <td><select name="datasets" id="datasets">
					      <option value="-1" disabled selected>verfügbare Datensätze...</option>
<!-- 					      <option>data_5.2.2016</option> -->
<!-- 					      <option>data_12.2.2016</option> -->
<!-- 					      <option>data_14.2.2016</option> -->
<!-- 					      <option>data_15.2.2016</option> -->
    					</select>
    				</td>
	            </tr>
	     	</tbody>
	     	
	  	</table>
	  	<input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
    
</div>