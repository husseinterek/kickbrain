var searchFilter = "";
$(document).ready(function() {
	
	// datatable initialization
	if ($("#questions-list-datatable").length > 0) {
	    $("#questions-list-datatable").DataTable({
	        "processing": true,
	        'serverSide': true,
	    	"ajax": {
                url: contextPath + "/admin/questions?offset=0&max=10",
                type: "GET",
                dataType: "json",
                dataSrc: function (d) {
                    return d
                }
            },
            columns: [
                { data: 'descriptionAr' },
                { data: 'descriptionEn' },
                { data: 'answersEn' },
                { data: 'category' },
                {
                    data: null,
                    className: 'dt-center editor-edit',
                    defaultContent: '<i class="ft-edit-1"/>',
                    orderable: false
                }
            ],
	    'columnDefs': [{
	        "orderable": false
	      }]
	    });
	    
	    $(".dataTables_filter input[type=search]").focus();
	};
	
	$(".dataTables_filter input[type=search]").unbind();
	$(".dataTables_filter input[type=search]").on('keyup', function(e) {
	    if (e.keyCode === 13) {
	    	reloadTableWithSearch($(".dataTables_filter input[type=search]").val());
	    }
	});
	
	$("#questions-list-datatable").on('click', 'td.editor-edit', function (e) {
		var rowData = $("#questions-list-datatable").DataTable().row($(this).closest('tr')).data();
		
		$('#questionModal').find('#question-id').val(rowData.id);
		$('#questionModal').find('#question-desc-ar').val(rowData.descriptionAr);
		$('#questionModal').find('#question-desc-en').val(rowData.descriptionEn);
		
		// populate answers in select2 component
		initSelect2Answers();
		
		$("#question-answers-en").empty();
		
		for(var i in rowData.answers)
		{
			var answer = rowData.answers[i];
			var answerOptionEn = "<option value="+answer.answerId+" selected><span>"+answer.answerEn+"</span></option>";
			$("#question-answers-en").append(answerOptionEn);
		}
		
		// populate category and tag in static select component
		$('#questionModal').find('#question-category').val(rowData.category);
		$('#questionModal').find('#question-tag').val(rowData.tag);
		
		$("#question-category").select2();
		$("#question-tag").select2();
		
		$('#questionModal').modal('show');
	});
	
	$("#addnewQuestionBtn").click(function() {
		$('#questionModal').modal({
		    backdrop: 'static',
		    keyboard: false
		});
		
		resetModal();
	});
	
	$('#questionModal').on('shown.bs.modal', function () {
	    $('#question-desc-ar').focus();
	});
	
	$('#saveQuestionBtn').click(function() {
		
		var questionId = $('#questionModal').find('#question-id').val();
		var questionDescAr = $('#questionModal').find('#question-desc-ar').val();
		var questionDescEn = $('#questionModal').find('#question-desc-en').val();
		var questionCategory = $('#questionModal').find('#question-category').val();
		var questionTag = $('#questionModal').find('#question-tag').val();
		var questionAnswers = $('#questionModal').find('#question-answers-en').val();
		
		var request = {};
		request.descriptionAr = questionDescAr;
		request.descriptionEn = questionDescEn;
		request.category = questionCategory;
		request.tag = questionTag;
		request.answers = questionAnswers;
		
		if(questionId)
		{
			request.id = questionId;
		}
		
		// update
		Swal.showLoading();
		$.ajax({
			  url:contextPath + "/admin/saveQuestion",
			  type:"POST",
			  data:JSON.stringify(request),
			  contentType:"application/json; charset=utf-8",
			  dataType:"json",
			  async: false,
			  success: function(response){
				  Swal.close();
				  if(response.status == 1)
				  {
					  toastr.success("Save Successful!",'Success!');
					  
					  if(!questionId)
					  {
						  resetModal();
					  }
					  reloadTableWithSearch($(".dataTables_filter input[type=search]").val());
				  }
				  else
				  {
					  toastr.error("Save Failed", 'Error!');
				  }
			  }
		  });
	});
	
	
});

function reloadTableWithSearch(searchFilter)
{
	$("#questions-list-datatable").DataTable().destroy();
	$("#questions-list-datatable").DataTable({
        "processing": true,
        'serverSide': true,
    	"ajax": {
            url: contextPath + "/admin/questions?offset=0&max=20&search=" + searchFilter,
            type: "GET",
            dataType: "json",
            dataSrc: function (d) {
                return d
            }
        },
        columns: [
            { data: 'descriptionAr' },
            { data: 'descriptionEn' },
            { data: 'answersEn' },
            { data: 'category' },
            {
                data: null,
                className: 'dt-center editor-edit',
                defaultContent: '<i class="ft-edit-1"/>',
                orderable: false
            }
        ],
    'columnDefs': [{
        "orderable": false
      }]
    });
	
	$(".dataTables_filter input[type=search]").val(searchFilter);
	$(".dataTables_filter input[type=search]").focus();
	
	$(".dataTables_filter input[type=search]").unbind();
	$(".dataTables_filter input[type=search]").on('keyup', function(e) {
	    if (e.keyCode === 13) {
	    	reloadTableWithSearch($(".dataTables_filter input[type=search]").val());
	    }
	});
}

function initSelect2Answers()
{
	$("#question-answers-en").select2({
		ajax: {
			url: contextPath + "/admin/answers",
			dataType: 'json',
			delay: 250,
			data: function (params) {
				return {
					search: params.term, // search term
					page: params.page
				};
			},
			processResults: function (data, params) {
				// parse the results into the format expected by Select2
				// since we are using custom formatting functions we do not need to
				// alter the remote JSON data, except to indicate that infinite
				// scrolling can be used
				params.page = params.page || 1;
				
				var response = [];
				for(var i in data)
				{
					var item = data[i];
					var entity = {};
					entity.id = item.id;
					entity.text = item.nameEn;
					
					response.push(entity);
				}
				
				return {
					results: response,
					pagination: {
						more: (params.page * 30) < data.total_count
					}
				};
			},
			cache: true
		},
		placeholder: 'Search here ...',
		escapeMarkup: function (markup) { return markup; }, // let our custom formatter work
		minimumInputLength: 3
	});
}

function resetModal()
{
	$('#questionModal').find('#question-id').val('');
	$('#questionModal').find('#question-desc-ar').val('');
	  $('#questionModal').find('#question-desc-en').val('');
	  $('#questionModal').find('#question-category').val('-1');
	  $('#questionModal').find('#question-tag').val('5');
	  
	  $('#questionModal').find('#question-desc-ar').focus();
	  
	  $("#question-answers-en").empty();
	  initSelect2Answers();
		$("#question-category").select2();
		$("#question-tag").select2();
}