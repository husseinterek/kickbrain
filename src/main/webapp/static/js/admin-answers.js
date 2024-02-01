var searchFilter = "";
$(document).ready(function() {
	
	// datatable initialization
	if ($("#answers-list-datatable").length > 0) {
	    $("#answers-list-datatable").DataTable({
	        "processing": true,
	        'serverSide': true,
	    	"ajax": {
                url: contextPath + "/admin/answers?offset=0&max=10",
                type: "GET",
                dataType: "json",
                dataSrc: function (d) {
                    return d
                }
            },
            columns: [
            	{ data: 'nameAr' },
                { data: 'nameEn' },
                { data: 'type' },
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
	
	$("#answers-list-datatable").on('click', 'td.editor-edit', function (e) {
		var rowData = $("#answers-list-datatable").DataTable().row($(this).closest('tr')).data();
		
		$('#answerModal').find('#answer-id').val(rowData.id);
		$('#answerModal').find('#answer-desc-ar').val(rowData.nameAr);
		$('#answerModal').find('#answer-desc-en').val(rowData.nameEn);
		
		// populate category and tag in static select component
		$('#answerModal').find('#answer-type').val(rowData.type);
		
		$("#answer-type").select2();
		
		$('#answerModal').modal('show');
	});
	
	$('#answerModal').on('shown.bs.modal', function () {
	    $('#answer-desc-ar').focus();
	});
	
	$("#addnewAnswerBtn").click(function() {
		$("#answer-type").select2();
		
		$('#answerModal').modal({
		    backdrop: 'static',
		    keyboard: false
		});
		
		resetModal();
	});
	
	$('#saveAnswerBtn').click(function() {
		
		var answerId = $('#answerModal').find('#answer-id').val();
		var answerDescAr = $('#answerModal').find('#answer-desc-ar').val();
		var answerDescEn = $('#answerModal').find('#answer-desc-en').val();
		var answerType = $('#answerModal').find('#answer-type').val();
		
		var request = {};
		request.nameAr = answerDescAr;
		request.nameEn = answerDescEn;
		request.type = answerType;
		
		if(answerId)
		{
			request.id = answerId;
		}
		
		// update
		Swal.showLoading();
		$.ajax({
			  url:contextPath + "/admin/saveAnswer",
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
					  
					  if(!answerId)
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
	$("#answers-list-datatable").DataTable().destroy();
	$("#answers-list-datatable").DataTable({
        "processing": true,
        'serverSide': true,
    	"ajax": {
            url: contextPath + "/admin/answers?offset=0&max=10&search=" + searchFilter,
            type: "GET",
            dataType: "json",
            dataSrc: function (d) {
                return d
            }
        },
        columns: [
            { data: 'nameAr' },
            { data: 'nameEn' },
            { data: 'type' },
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

function resetModal()
{
	$('#answerModal').find('#answer-id').val('');
	$('#answerModal').find('#answer-desc-ar').val('');
	  $('#answerModal').find('#answer-desc-en').val('');
	  $('#answerModal').find('#answer-type').val('1');
	  $('#answerModal').find('#answer-desc-ar').focus();
}