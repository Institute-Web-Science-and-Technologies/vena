<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script>
$(function() {
	
    var dialog, form,
 
      name = $( "#name" ),
      password = $( "#password" ),
      allFields = $( [] ).add( name ).add( password ),
      tips = $( ".validateTips" );
 
    function updateTips( t ) {
      tips
        .text( t )
        .addClass( "ui-state-highlight" );
      setTimeout(function() {
        tips.removeClass( "ui-state-highlight", 1500 );
      }, 500 );
    }
 
    function checkLength( o, n, min, max ) {
      if ( o.val().length > max || o.val().length < min ) {
        o.addClass( "ui-state-error" );
        updateTips( "Length of " + n + " must be between " +
          min + " and " + max + "." );
        return false;
      } else {
        return true;
      }
    }
 
    function checkRegexp( o, regexp, n ) {
      if ( !( regexp.test( o.val() ) ) ) {
        o.addClass( "ui-state-error" );
        updateTips( n );
        return false;
      } else {
        return true;
      }
    }
    
 	function respondWrongPassword(){
 		password.addClass( "ui-state-error" );
 		updateTips("Passwort passt nicht zum Nutzername");
 	} 
    
    
   function addUser() {
      var valid = true;
      allFields.removeClass( "ui-state-error" );
 
      valid = valid && checkLength( name, "username", 3, 16 );
      valid = valid && checkLength( password, "password", 5, 16 );
 
      valid = valid && checkRegexp( name, /^[a-z]([0-9a-z\s])+$/i, "Username may consist of a-z, 0-9, underscores and must begin with a letter." );
      valid = valid && checkRegexp( password, /^([0-9a-zA-Z])+$/, "Password field only allow : a-z 0-9" );
  	  
      $.event.trigger( "loadingStart" );
      
      var currentUser = Object.create(user);
  	  currentUser.name = name.val();
  	  currentUser.password = password.val();
	  
  	  if ( valid ) {
    	function callback(success, data){
    		if ( success ){
    			mdlInit(currentUser);
    			
     			alert( "Willkommen: "+ name.val() );
     			$(".login_vis").show();
     			$(".logout_vis").hide();
     			valid = true;
    		}else{
    			respondWrongPassword();
    			valid = false;
    		}
    		if ( valid ){
    			
    			
    			console.log("got datasets from server: " + data);
    	      	dialog.dialog( "close" );
    	      	
    	      	ctrUpdateDatasetList(data);
    	      	
    	    }
    		$.event.trigger( "loadingStop" );
    	}
    	
    	srvGetUser(currentUser, callback);
      }
      return valid;
    }
 
    dialog = $( "#dialog-form" ).dialog({
      autoOpen: false,
      height: 340,
      width: 380,
      resizable:false,
      draggable:false,
      modal: true,
      buttons: {
        "Login / Create account": addUser,
        Cancel: function() {
          dialog.dialog( "close" );
        }
      },
      close: function() {
        form[ 0 ].reset();
        allFields.removeClass( "ui-state-error" );
      }
    });
 
    form = dialog.find( "form" ).on( "submit", function( event ) {
      event.preventDefault();
      addUser();
    });
 
    $( "#login_dlg_button" ).button().on( "click", function() {
      dialog.dialog( "open" );
    });
  });
</script>

<div id="dialog-form" title="Login">
  <p class="validateTips">Hello.</p>
 
  <form>
    <fieldset>
      <label class="lrdialog" for="name">Name</label>
      <input class="lrdialog" type="text" name="name" id="name" value="admin" class="text ui-widget-content ui-corner-all">
      <label class="lrdialog" for="password">Passwort</label>
      <input class="lrdialog" type="password" name="password" id="password" value="dasDongo2000" class="text ui-widget-content ui-corner-all">
 
      <!-- Allow form submission with keyboard without duplicating the dialog button -->
      <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
    </fieldset>
  </form>
</div>