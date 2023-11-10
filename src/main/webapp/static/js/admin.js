$(document).ready(function() {
	
	$("#submitQuestion").click(function(e) {
		submitQuestion();
	});
});

function submitQuestion()
{
	var questionEn = $('#questionEn').val().trim();
	var questionAr = $('#questionAr').val().trim();
	var answersEn = $('#english-answers').val().trim();
	var answersAr = $('#arabic-answers').val().trim();
	
	if(!questionEn || !questionAr || !answersEn || !answersAr)
	{
		toastr.warning("Please fill the mandatory fields!", 'Mandatory fields!');
		return;
	}
	else
	{
		var request = {};
		request.questionEn = questionEn;
		request.questionAr = questionAr;
		request.answersEn = answersEn;
		request.answersAr = answersAr;
		
		$('#submitQuestion').append("<i class='la la-spinner spinner'></i>");
		$('#submitQuestion').prop("disabled", true);
		$.ajax({
			  url:contextPath + "/admin/submitQuestion",
			  type:"POST",
			  data:JSON.stringify(request),
			  contentType:"application/json; charset=utf-8",
			  dataType:"json",
			  async: true,
			  success: function(response){
				  $('#submitQuestion').prop("disabled", false);
				  $('#submitQuestion').find('i').remove();
				  
				  if(response.status == 1)
				  {
					  $('#questionEn').val('');
					  $('#questionAr').val('');
					  $('#english-answers').val('');
					  $('#arabic-answers').val('');
					  toastr.success("Question is added successfully", 'Success!');
				  }
				  else
				  {
					  toastr.error("Error occurred while adding the question, please try again later", 'Error!');
				  }
			  }
		});
	}
}