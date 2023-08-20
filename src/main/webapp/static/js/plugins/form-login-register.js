$(document).ready(function(){
	'use strict';
	//Login Register Validation
	if($("form.form-horizontal").attr("novalidate")!=undefined){
		$("input,select,textarea").not("[type=submit]").jqBootstrapValidation();
	}
	
	$("#register").submit(function(e) {

	    e.preventDefault(); // avoid to execute the actual submit of the form.

	    var form = $(this);
	    var url = form.attr('action');
	    
	    if($("#firstName").val() == '' || $("#lastName").val() == '' || $("#country").val() == '-1' || $("#company").val() == '' || $("#email").val() == '' || $("#password").val() == '')
	    {
	    	Swal.fire({
		          type: "error",
		          title: "Please fill all the mandatory fields!",
		          confirmButtonClass: 'btn btn-danger',
  			});
	    	return;
	    }
	    
	    var request = {};
	    request.firstName = $("#firstName").val();
	    request.lastName = $("#lastName").val();
	    request.country = $("#country").val();
	    request.company = $("#company").val();
	    request.email = $("#email").val();
	    request.password = $("#password").val();
	    
	    $.ajax({
	    	type: "POST",
	    	url: url,
	    	data:JSON.stringify(request),
			contentType:"application/json; charset=utf-8",
			dataType:"json",
	    	success: function(data)
	    	{
	    		if(data.status == 1)
	    		{
	    			Swal.fire({
				          type: "success",
				          title: "Your account has been created successfully",
				          confirmButtonClass: 'btn btn-success',
				    });
	    			
	    			setTimeout(() => {
	    				document.location = contextPath + '/login';
					}, 2000);
	    		}
	    		else
	    		{
	    			if(data.errorCode = 'error.USER_ALREADY_EXIST')
	    			{
	    				Swal.fire({
	    			          type: "error",
	    			          title: "User already registered!",
	    			          confirmButtonClass: 'btn btn-danger',
	    	  			});
	    			}
	    			else
	    			{
	    				Swal.fire({
	    			          type: "error",
	    			          title: "A general error has occurred, please try again later.",
	    			          confirmButtonClass: 'btn btn-danger',
	    	  			});
	    			}
	    		}
	    	}
         });
	});
	
	$("#reset-password").submit(function(e) {

	    e.preventDefault(); // avoid to execute the actual submit of the form.

	    var form = $(this);
	    var url = form.attr('action');
	    
	    var email = $("#email").val();
	    
	    Swal.showLoading();
	    $.ajax({
	    	type: "POST",
	    	url: url,
	    	data:email,
			contentType:"application/json; charset=utf-8",
			dataType:"json",
	    	success: function(data)
	    	{
	    		if(data.status == 1)
	    		{
	    			Swal.close();
	    			$("#success-alert").show();
	    		}
	    	}
         });
	});
	
	$("#change-password").submit(function(e) {

	    e.preventDefault(); // avoid to execute the actual submit of the form.

	    var form = $(this);
	    var url = form.attr('action');
	    
	    Swal.showLoading();
	    
	    var password = $("#password").val();
	    var reenterPassword = $("#reenterPassword").val();
	    
	    if(password == reenterPassword)
	    {
	    	var request = {};
	    	request.token = token;
	    	request.password = password;
	    	$.ajax({
		    	type: "POST",
		    	url: url,
		    	data:JSON.stringify(request),
				contentType:"application/json; charset=utf-8",
				dataType:"json",
		    	success: function(data)
		    	{
		    		if(data.status == 1)
		    		{
		    			setTimeout(() => {
		    				document.location = contextPath + '/login';
						}, 2000);
		    		}
		    		else
		    		{
		    			Swal.fire({
		  		          type: "error",
		  		          title: "An error occured while changing your password.",
		  		          confirmButtonClass: 'btn btn-danger',
		    			});
		    		}
		    	}
	         });
	    }
	    else
	    {
	    	Swal.fire({
		          type: "error",
		          title: "Passwords don't match",
		          confirmButtonClass: 'btn btn-danger',
		    });
	    	
	    	return false;
	    }
	});
	
	if($('#user-name'))
	{
		$('#user-name').focus();
	}
});
