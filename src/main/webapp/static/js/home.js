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
	
	$('#joinExistingGameModal').on('click', '#joinGameBtn', function(e) {
		joinExistingGame($(this).data('roomid'));
	});
	
	$('#joinExistingGameModal').on('shown.bs.modal', function (e) {
		$('#waiting-room-list').find('ul').append("<div class='ball-clip-rotate loader-primary float-right loader text-center'><div></div></div>");
		
		$.ajax({
			  url:contextPath + "/game/waitingRooms",
			  type:"GET",
			  contentType:"application/json; charset=utf-8",
			  dataType:"json",
			  success: function(response){
				  
				  $('#waiting-room-list').find('ul').empty();
				  
				  var waitingRooms = response.waitingRooms;
				  if(waitingRooms.length > 0)
				  {
					  $('#waiting-room-list').find('#joinExistingGame-username').show();
					  for(var i in waitingRooms)
					  {
						  var room = waitingRooms[i];
						  
						  var roomElement = "<li class='list-group-item'>"
                        		+ "<div class='row'>"
                        			+ "<div class='col-10'>"
                        				+ "<h4 class='hostName'>"+room.hostingPlayer.username+"</h4>"
                        				+ "<p class='roomId'>" + (language == 'en' ? "Room Id: " : 'رقم التحدي: ') +room.roomId+"</p>"
                        			+ "</div>"
                        			+ "<div class='col-2'>"
                        				+ "<a href='#' id='joinGameBtn' data-roomid='"+room.roomId+"' class='btn btn-float btn-round btn-warning'><i class='la la-gamepad'></i></a>"
                        			+ "</div>"
                        		+ "</div>"
                        	+ "</li>";
						  
						  $('#waiting-room-list').find('ul').append(roomElement);
					  }
				  }
				  else
				  {
					  $('#waiting-room-list').find('#joinExistingGame-username').hide();
					  $('#waiting-room-list').find('ul').append("<label>"+(language =='en' ? "No waiting games have been found at the moment, please create a new game and invite other players to join!" : "لم يتم العثور على تحديات قيد الانتظار, بادر الى انشاء لعبة جديدة وأدعو أصدقائك للمشاركة!")+ "</label>");
				  }
			  }
		});
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

function joinExistingGame(roomId)
{
	var username = $('#joinExistingGame-username').val().trim();
	
	if(!username)
	{
		toastr.warning(language == 'en' ? "Please fill your name before starting the game!" : "من فضلك قم بإدخال اسمك قبل بدء اللعبة!", language == 'en' ? 'Mandatory fields!': 'حقول اجبارية!');
		return;
	}
	else
	{
		/*$('#joinExistingGameBtn').append("<i class='la la-spinner spinner'></i>");
		$('#joinExistingGameBtn').prop("disabled", true);*/
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