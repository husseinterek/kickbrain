var selectedChallenge = 'whatDoYouKnow';
var stompClient = null;
var roomId = null;
$(document).ready(function() {
	
	$("#newOnlineGameBtn").click(function(e) {
		  startOnlineGame();
	});
	
	$("#newSingleGameBtn").click(function(e) {
		startSingleGame();
	});
	
	$('#joinExistingGameBtn').click(function(e) {
		joinExistingGame();
	});
});

function startOnlineGame()
{
	if(!selectedChallenge)
	{
		toastr.warning(language == 'en' ? "Please select a challenge before proceeding!" : "من فضلك اختر تحديًا قبل المتابعة!", language == 'en' ? 'Select Challenge!': 'اختر تحديًا');
		return;
	}
	
	var username = $("#username").val().trim();
	if(!username)
	{
		toastr.warning(language == 'en' ? "Please fill your name before starting the game!" : "من فضلك قم بإدخال اسمك قبل بدء اللعبة!", language == 'en' ? 'Missing Information!' : 'معلومات ناقصة!');
		$('#username').focus();
		return;
	}
	else
	{
		document.location = contextPath + "/waitingRoom?lang="+language+"&username=" + username;
	}
}

function startSingleGame()
{
	if(!selectedChallenge)
	{
		toastr.warning(language == 'en' ? "Please select a challenge before proceeding!" : "من فضلك اختر تحديًا قبل المتابعة!", language == 'en' ? 'Select Challenge!': 'اختر تحديًا');
		return;
	}
	
	var username = $("#username").val().trim();
	if(!username)
	{
		toastr.warning(language == 'en' ? "Please fill your name before starting the game!" : "من فضلك قم بإدخال اسمك قبل بدء اللعبة!", language == 'en' ? 'Missing Information!' : 'معلومات ناقصة!');
		$('#username').focus();
		return;
	}
	
	// Redirect the player to the game HTML page with the roomId as a parameter
    document.location = contextPath + "/generateSingleGame?lang="+language+"&mode="+selectedChallenge+"&username=" + username;
}

function joinExistingGame()
{
	var username = $('#joinExistingGame-username').val().trim();
	var roomId = $('#joinExistingGame-roomId').val().trim();
	
	if(!username || !roomId)
	{
		toastr.warning(language == 'en' ? "Please fill the mandatory fields!" : "الرجاء تعبئة كافة الحقول!", language == 'en' ? 'Mandatory fields!': 'حقول اجبارية!');
		return;
	}
	else
	{
		$('#joinExistingGameBtn').append("<i class='la la-spinner spinner'></i>");
		$('#joinExistingGameBtn').prop("disabled", true);
		$.ajax({
			  url:contextPath + "/game/"+roomId+"/join?username=" + username,
			  type:"POST",
			  contentType:"application/json; charset=utf-8",
			  dataType:"json",
			  async: true,
			  success: function(response){
				  $('#joinExistingGameBtn').prop("disabled", false);
				  $('#joinExistingGameBtn').find('i').remove();
				  
				  if(response.status == 1)
				  {
					  // Redirect the player to the game HTML page with the roomId as a parameter
					  document.cookie = "playerId=" + response.playerId;
					  document.location = contextPath + "/generateNewGame?lang="+language+"&roomId=" + roomId;
				  }
				  else
				  {
					  toastr.error(response.errorMessage, language == 'en' ? 'Error!': 'حدث خطأ!');
				  }
			  }
		});
	}
}