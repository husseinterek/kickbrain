var roomId = '';
var stompClient = null;
$(document).ready(function() {
	
	connectToWebSocket();
	
	window.onbeforeunload = function() {
		 return 'Are you sure you want to leave this page? The game will get disconnected';
	}
	
	$("#cancelGameBtn").click(function(e) {
		cancelGame();
	});
});

//Connect to the WebSocket endpoint
function connectToWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        subscribeToTopics();
        startGame();
    });
}

function subscribeToTopics()
{
	stompClient.subscribe('/topic/game/wait/' + username, function(message) {
		
		var response = $.parseJSON(message.body);
		$('#roomId').html(response.roomId);
		roomId = response.roomId;
    	// Send a heartbeat ping every 10 seconds
        setInterval(sendPing, 5000);
    });
	
	stompClient.subscribe('/topic/game/start/' + username, function(message) {
		window.onbeforeunload = null;
		handleRoomId($.parseJSON(message.body));
    });
}

//Function to create a new game room or join an existing room
function startGame() {
    var player = {
        username: username
    };

    // Send the player's information to the backend via WebSocket
    stompClient.send('/app/startGame', {}, JSON.stringify(player));
}

//Function to handle roomId received from the backend
function handleRoomId(gameStartEvent) {
    // Handle the roomId as required (e.g., store it in a variable)
    document.cookie = "playerId=" + gameStartEvent.playerId;
    
    // Redirect the player to the game HTML page with the roomId as a parameter
    document.location = contextPath + "/generateNewGame?lang="+language+"&roomId=" + gameStartEvent.roomId;
}

function sendPing() {
	stompClient.send('/app/waitPing', {}, '');
}

function cancelGame()
{
	Swal.fire({
		  title: language == 'ar' ? 'هل انت متأكد انك تريد انهاء اللعبة؟' : 'Are you sure you want to cancel the game?',
		  type: 'warning',
	      showCancelButton: true,
	      confirmButtonColor: '#3085d6',
	      cancelButtonColor: '#d33',
	      confirmButtonText: language == 'ar' ?  'نعم' : 'Yes',
	      cancelButtonText: language == 'ar' ? 'لا' : 'Cancel',
	      confirmButtonClass: 'btn btn-info',
	      cancelButtonClass: 'btn btn-danger ml-1',
	      buttonsStyling: false,
		}).then(function(result){
		  if (result.value) {
			  Swal.showLoading();
			  $.ajax({
				  url:contextPath + "/game/cancelGame?roomId=" + roomId,
				  type:"POST",
				  contentType:"application/json; charset=utf-8",
				  dataType:"json",
				  async: true,
				  success: function(response){
					  Swal.hideLoading();
					  if(response.status == 1)
					  {
						  window.onbeforeunload = null;
						  document.location = contextPath + "/?lang="+language;
					  }
					  else
					  {
						  toastr.error(language == 'en' ? "An error occured while cancelling the game!" : 'حدث خطأ أثناء انهاء اللعبة!', language == 'en' ? 'Internal Error!' : 'حدث خطأ!');
					  }
				  }
				});
		  }
	});
}