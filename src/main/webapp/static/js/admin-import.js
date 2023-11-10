function submitExcel(event)
{
	event.preventDefault(); 
	
	var file = document.querySelector("#excelFile").files[0];
	if(!file)
	{
		toastr.warning(language == 'ar' ? 'الرجاء ملأ الحقول الاجبارية': 'Please fill all the mandatory fields!', language == 'ar' ? 'تنبيه' : 'Warning!');
	}
	else
	{
		var reader = new FileReader();
		reader.readAsDataURL(file);
		reader.onload = function () {
			var base64File = reader.result;
			
			$('#importBtn').append("<i class='la la-spinner spinner'></i>");
			$('#importBtn').prop("disabled", true);
			
			$.ajax({
					url:contextPath + "/admin/importQuestions",
				  type:"POST",
				  data:base64File,
				  contentType:"application/json; charset=utf-8",
				  dataType:"json",
				  async: false,
				  success: function(response){
					  $('#importBtn').prop("disabled", false);
					  $('#importBtn').find('i').remove();
					  if(response.status == 1)
					  {
						  toastr.success("Questions are added successfully", 'Success!');
					  }
					  else
					  {
						  toastr.error("Error occurred while importing the questions, please try again later", 'Error!');
					  }
				  }
			  });
		};
	}
}