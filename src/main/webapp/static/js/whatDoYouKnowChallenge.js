var currentQuestionIdx=1;
var globalAnswerP1IntervalId;
var globalAnswerP2IntervalId;
var currentPlayer;
var opponentPlayer;
var stompClient = null;
var questionsMap = {};
$(document).ready(function() {
	
	var playerIdSession = getCookie('playerId');
	if(player1.playerId == playerIdSession)
	{
		currentPlayer = player1;
		opponentPlayer = player2;
	}
	else
	{
		opponentPlayer = player1;
		currentPlayer = player2;
	}
	
	var vertical_scroll = new PerfectScrollbar(".question", {
	    wheelPropagation: true
	});
	
	connectToWebSocket();
	openNewQuestion();
	
	$('#game').on('click', '#skipQuestionBtn', function(e) {
		
		var question = questionsLst[currentQuestionIdx-1];
		var questionId = question.id;
		
		stompClient.send('/app/game/skipQuestion', {}, JSON.stringify({currentQuestionIndex: currentQuestionIdx, roomId: roomId, submittedPlayerId: currentPlayer.playerId, questionId: questionId}));
	});
	
	$('#game .question').on('keyup', '.playerAnswerValue', function(e) {
		if(e.keyCode == 13)
		{
			validateAnswer(this);
		}
	});
	
	$('#resultReportModal').on('hidden.bs.modal', function () {
		document.location = contextPath + "/?lang="+language;
	});
	
	window.onbeforeunload = function() {
		 return 'Are you sure you want to leave this page? The game will get disconnected';
	}
	
	$('#game').on('click', '.answerByMicrophone', function(e) {
		var answerElement = $(this).closest('.list-group-item').find('.playerAnswerValue');
		validateAnswer(answerElement);
	});
	
	// Send a heartbeat ping every 10 seconds
    setInterval(sendPing, 5000);
});

function validateAnswer(element)
{
	var capturedAnswer = $(element).val();
	if(capturedAnswer)
	{
		$(element).closest('li').find('.answerByMicrophone').replaceWith("<div class='ball-clip-rotate loader-primary float-right loader'><div></div></div>");
		$(element).attr("readonly", "readonly");
		
		var answerElement = $(element);
		var request = {};
		request.questionId = $(element).data('questionid');
		request.capturedAnswer = capturedAnswer;
		request.roomId = roomId;
		request.submittedPlayerId = currentPlayer.playerId;
		request.opponentPlayerId = opponentPlayer.playerId;
		request.currentQuestionIdx = currentQuestionIdx;
		request.answerElementId = $(element).attr('id');
		
		$.ajax({
			  url:contextPath + "/game/validateAnswer",
			  type:"POST",
			  data:JSON.stringify(request),
			  contentType:"application/json; charset=utf-8",
			  dataType:"json",
			  async: true,
			  success: function(response){
				  $(answerElement).closest('li').find('.loader').replaceWith("<button type='button' class='btn btn-icon btn-warning float-right answerByMicrophone'><i class='la la-play'></i></button>");
			  }
		});
	}
	return;
}

function openNewQuestion()
{
	// reset the flags when showing new question
	$('.player1AnswerSection #attempt1').replaceWith("<img class='img-fluid' id='attempt1' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	$('.player1AnswerSection #attempt2').replaceWith("<img class='img-fluid' id='attempt2' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	$('.player1AnswerSection #attempt3').replaceWith("<img class='img-fluid' id='attempt3' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	$('.player2AnswerSection #attempt1').replaceWith("<img class='img-fluid' id='attempt1' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	$('.player2AnswerSection #attempt2').replaceWith("<img class='img-fluid' id='attempt2' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	$('.player2AnswerSection #attempt3').replaceWith("<img class='img-fluid' id='attempt3' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	
  	// clear the intervals if any
  	clearIntervals();
  	
	$('.question-number').html(language == 'en' ? "Question (" + (currentQuestionIdx) + "/10)" : "سؤال (" + (currentQuestionIdx) + "/10)");
	$('.question').empty();
	
	var question = questionsLst[currentQuestionIdx-1];
	var id = question.id;
	
	questionsMap[id] = question;
	
	var questionElement = "<div id='"+id+"' class='mr-1 ml-1'></div>";
	$('.question').append(questionElement);
	
	var questionPromptElement =	"<div class='row'>"
									+ "<div class='col-12'>"
										+ "<p style='font-size: 20px; font-weight: bold;'>"+(language == 'en' ? question.questionEn : question.questionAr)+"</p>"
									+ "</div>"
								+ "</div>";
	$(".question #"+ id + "").append(questionPromptElement);
	
	recordAnswers(id);
}

function recordAnswers(id)
{
	var answerPeriod = 20;
	var answerSectionElement = "<div class='row mt-1' id='answerSection'>"
									+ "<div class='"+(isMobile == "true" ? 'col-12' : 'col-6')+"'>"
										+ "<div class='border-light player1AnswerSection rounded answer-border-thickness'>"
											+ "<div class='row p-1' style='height: 30px'>"
												+ "<div class='"+(isMobile == "true" ? 'col-6' : 'col-6')+"' style='font-size: 12px'>"
													+ "<p style='color: red' id='player1AnswerSectionLabel'>"+currentPlayer.username+" "+(language == 'en' ? 'answers' : 'أجوبة')+"</p>"
												+ "</div>"
												+ "<div class='col-1'>"
													+ "<div class='badge badge-glow badge-pill badge-warning player1AnswerCountDown'>"+answerPeriod+"</div>"
												+ "</div>"
												+ "<div class='"+(isMobile == "true" ? 'col-4' : 'col-4 text-right')+"'>"
													+ "<img class='img-fluid' id='attempt1' src='"+contextPath+"/static/images/empty-strike.png'></img>"
													+ "<img class='img-fluid' id='attempt2' src='"+contextPath+"/static/images/empty-strike.png'></img>"
													+ "<img class='img-fluid' id='attempt3' src='"+contextPath+"/static/images/empty-strike.png'></img>"
												+ "</div>"
											+ "</div>"
										+ "</div>"
									+ "</div>"
									+ "<div class='"+(isMobile == "true" ? 'col-12 mt-1' : 'col-6')+"'>"
										+ "<div class='border-light player2AnswerSection rounded answer-border-thickness'>"
											+ "<div class='row p-1' style='height: 30px'>"
												+ "<div class='"+(isMobile == "true" ? 'col-6' : 'col-6')+"' style='font-size: 12px'>"
													+ "<p style='color: red' id='player2AnswerSectionLabel'>"+opponentPlayer.username+" "+(language == 'en' ? 'answers' : 'أجوبة')+"</p>"
												+ "</div>"
												+ "<div class='col-1'>"
													+ "<div class='badge badge-glow badge-pill badge-warning player2AnswerCountDown'>"+answerPeriod+"</div>"
												+ "</div>"
												+ "<div class='"+(isMobile == "true" ? 'col-4' : 'col-4 text-right')+"'>"
													+ "<img class='img-fluid' id='attempt1' src='"+contextPath+"/static/images/empty-strike.png'></img>"
													+ "<img class='img-fluid' id='attempt2' src='"+contextPath+"/static/images/empty-strike.png'></img>"
													+ "<img class='img-fluid' id='attempt3' src='"+contextPath+"/static/images/empty-strike.png'></img>"
												+ "</div>"
											+ "</div>"
										+ "</div>"
									+ "</div>"
								+ "</div>";
	
	$(".question #"+ id + "").append(answerSectionElement);
	
	var player1AnswersList = "<div class='row player1AnswersList mt-1 p-1' style='position: relative; height: 250px'><div class='col-12'><ul class='list-group'></ul></div></div>";
	$(".question #"+ id + "").find('.player1AnswerSection').append(player1AnswersList);
	
	var player2AnswersList = "<div class='row player2AnswersList mt-1 p-1' style='position: relative; height: 250px'><div class='col-12'><ul class='list-group'></ul></div></div>";
	$(".question #"+ id + "").find('.player2AnswerSection').append(player2AnswersList);
	
	var vertical_scroll = new PerfectScrollbar(".player1AnswersList", {
	    wheelPropagation: true
	});
	
	var vertical_scroll = new PerfectScrollbar(".player2AnswersList", {
	    wheelPropagation: true
	});
	
	populatePlayer1AnswerElement(id);
	populatePlayer2AnswerElement(id);
	
	switchTurn();
}

function populatePlayer1AnswerElement(questionId)
{
	var existingAnswers = 0;
	$('.player1AnswerValue').each(function(i) {
		existingAnswers++;
	});
	
	var answerElement = "<li class='list-group-item' style='padding: 12px'>"
							+ "<div class='row'>"
								+ "<div class='col-10'>"
									+ "<input type='text' data-questionid='"+questionId+"' class='form-control player1AnswerValue playerAnswerValue' style='font-size: 13px' id='"+(existingAnswers+1)+"' placeholder='"+(language == 'en' ? "Write/Record "+currentPlayer.username+" answer" : " تسجيل أجوبة  "+currentPlayer.username+"")+"' readonly/>"
								+ "</div>"
								+ "<div class='col-2 answer-icon-div'>"
									+ "<button type='button' class='btn btn-icon btn-warning float-right answerByMicrophone'><i class='la la-play'></i></button>"
								+ "</div>"
							+ "</div>"
						+ "</li>";
	$(".question #"+ questionId + "").find('.player1AnswersList').find("ul").append(answerElement);
	$(".question #"+ questionId + "").find('.player1AnswersList').find('.player1AnswerValue').focus();
}

function populatePlayer2AnswerElement(questionId)
{
	var existingAnswers = 0;
	$('.player2AnswerValue').each(function(i) {
		existingAnswers++;
	});
	
	var answerElement = "<li class='list-group-item' style='padding: 12px'>"
							+ "<div class='row'>"
								+ "<div class='col-10'>"
									+ "<input type='text' data-questionid='"+questionId+"' class='form-control player2AnswerValue playerAnswerValue' style='font-size: 13px' id='"+(existingAnswers+1)+"' placeholder='"+(language == 'en' ? "Write/Record "+opponentPlayer.username+" answer" : " تسجيل أجوبة  "+opponentPlayer.username+"")+"' readonly/>"
								+ "</div>"
								+ "<div class='col-2 answer-icon-div'>"
								+ "<button type='button' class='btn btn-icon btn-warning float-right answerByMicrophone'><i class='la la-play'></i></button>"
								+ "</div>"
							+ "</div>"
						+ "</li>";
	$(".question #"+ questionId + "").find('.player2AnswersList').find("ul").append(answerElement);
	$(".question #"+ questionId + "").find('.player2AnswersList').find('.player2AnswerValue').focus();
}

function clearIntervals()
{
  	if(globalAnswerP1IntervalId)
  	{
  		clearInterval(globalAnswerP1IntervalId);
  	}
  	
  	if(globalAnswerP2IntervalId)
  	{
  		clearInterval(globalAnswerP2IntervalId);
  	}
}

function strike(nbStrikes)
{
	var question = questionsLst[currentQuestionIdx-1];
	var questionId = question.id;
	
	if(currentTurn == currentPlayer.playerId)
	{
		switch(nbStrikes)
		{
			  case 1:
			  {
				  	toastr.error(language == 'en' ? "Your response is incorrect. " + (3 - nbStrikes) + " more failure chances are remaining" : "جوابك خاطىء. " + (3 - nbStrikes) + " أخطاء متبقية في رصيدك", language == 'en' ? 'Wrong Answer!' : 'جواب خاطىء!');
				  	
				  	$('.player1AnswerSection #attempt1').replaceWith("<img class='img-fluid' id='attempt1' src='"+contextPath+"/static/images/full-strike.png'></img>");
				  	break;
			  }
			  case 2: 
			  {
				  	toastr.error(language == 'en' ? "Your response is incorrect. " + (3 - nbStrikes) + " more failure chances are remaining" : "جوابك خاطىء. " + (3 - nbStrikes) + " أخطاء متبقية في رصيدك", language == 'en' ? 'Wrong Answer!' : 'جواب خاطىء!');
				  	$('.player1AnswerSection #attempt2').replaceWith("<img class='img-fluid' id='attempt2' src='"+contextPath+"/static/images/full-strike.png'></img>");
				  	break;
			  }
		}
	}
	else
	{
		switch(nbStrikes)
		{
			  case 1:
			  {
				  	$('.player2AnswerSection #attempt1').replaceWith("<img class='img-fluid' id='attempt1' src='"+contextPath+"/static/images/full-strike.png'></img>");
				  	break;
			  }
			  case 2: 
			  {
				  	$('.player2AnswerSection #attempt2').replaceWith("<img class='img-fluid' id='attempt2' src='"+contextPath+"/static/images/full-strike.png'></img>");
				  	break;
			  }
		}
	}
}

function answerIntervalFunc(questionId)
{
	if(currentTurn == currentPlayer.playerId)
	{
		var answerPeriod = 20;
		$(".question #"+ questionId + "").find('#answerSection').find('.player1AnswerCountdown').html(answerPeriod);
		let answersIntervalId = setInterval(function () {
			globalAnswerP1IntervalId = answersIntervalId;
			answerPeriod--;
			if (answerPeriod < 0) {
				strikePost();
			}
			else
			{
				$(".question #"+ questionId + "").find('#answerSection').find('.player1AnswerCountdown').html(answerPeriod);
			}
		}, 1000);
	}
	else
	{
		var answerPeriod = 20;
		$(".question #"+ questionId + "").find('#answerSection').find('.player2AnswerCountdown').html(answerPeriod);
		let answersIntervalId = setInterval(function () {
			globalAnswerP2IntervalId = answersIntervalId;
			answerPeriod--;
			if (answerPeriod < 0) {
			}
			else
			{
				$(".question #"+ questionId + "").find('#answerSection').find('.player2AnswerCountdown').html(answerPeriod);
			}
		}, 1000);
	}
}

function switchTurn()
{
	clearIntervals();
	
	var question = questionsLst[currentQuestionIdx-1];
	var questionId = question.id;
	
	if(currentTurn == currentPlayer.playerId)
	{
		$('.player2AnswerSection').removeClass('border-warning');
		$('.player2AnswerSection').removeClass('answer-border-thickness');
		$('.player2AnswerSection').addClass('border-light');
		
		$('.player1AnswerSection').removeClass('border-light');
		$('.player1AnswerSection').addClass('border-warning');
		$('.player1AnswerSection').addClass('answer-border-thickness');
		
		var maxId = 0;
		$(".player1AnswerSection .player1AnswerValue").each(function(i) {
			var id = $(this).attr('id');
			if(maxId < id)
			{
				maxId = id;
			}
		});
		
		//$(".player2AnswerSection .player2AnswerValue[data-passed='0']").attr('disabled', 'disabled');
		$(".question #"+ questionId + "").find('#answerSection').find('.player2AnswerCountdown').hide();
		
		$(".player1AnswerSection #"+maxId+".player1AnswerValue").removeAttr('readonly');
		$(".player1AnswerSection #"+maxId+".player1AnswerValue").focus();
		$('.player1AnswerSection').find('.player1AnswersList').find('.answerByMicrophone').show();
		
		$('.player1AnswerSection').find('#player1AnswerSectionLabel').html(language == 'en' ? 'Recording ' + currentPlayer.username + ' Answers' : "جاري تسجيل أجوبة "+currentPlayer.username+"");
		$('.player1AnswerSection').find('#player1AnswerSectionLabel').addClass('blink_text');
		
		$('.player2AnswerSection').find('#player2AnswerSectionLabel').html(language == 'en' ? opponentPlayer.username + ' Answers' : 'أجوبة ' + opponentPlayer.username);
		$('.player2AnswerSection').find('#player2AnswerSectionLabel').removeClass('blink_text');
		$('.player2AnswerSection').find('.player2AnswersList').find('.answerByMicrophone').hide();
		
		$(".question #"+ questionId + "").find('#answerSection').find('.player1AnswerCountdown').show();
		answerIntervalFunc(questionId);
	}
	else
	{
		$('.player1AnswerSection').removeClass('border-warning');
		$('.player1AnswerSection').removeClass('answer-border-thickness');
		$('.player1AnswerSection').addClass('border-light');
		
		$('.player2AnswerSection').removeClass('border-light');
		$('.player2AnswerSection').addClass('border-warning');
		$('.player2AnswerSection').addClass('answer-border-thickness');
		
		var maxId = 0;
		$(".player1AnswerSection .player1AnswerValue").each(function(i) {
			var id = $(this).attr('id');
			if(maxId < id)
			{
				maxId = id;
			}
		});
		
		$(".player1AnswerSection #"+maxId+".player1AnswerValue").attr('readonly', 'readonly');
		$(".question #"+ questionId + "").find('#answerSection').find('.player1AnswerCountdown').hide();
		
		$('.player1AnswerSection').find('#player1AnswerSectionLabel').html(language == 'en' ? currentPlayer.username + ' Answers' : 'أجوبة ' + currentPlayer.username);
		$('.player1AnswerSection').find('#player1AnswerSectionLabel').removeClass('blink_text');
		$('.player1AnswerSection').find('.player1AnswersList').find('.answerByMicrophone').hide();
		
		$('.player2AnswerSection').find('#player2AnswerSectionLabel').html(language == 'en' ? 'Recording ' + opponentPlayer.username + ' Answers' : "جاري تسجيل أجوبة "+opponentPlayer.username+"");
		$('.player2AnswerSection').find('#player2AnswerSectionLabel').addClass('blink_text');
		$('.player2AnswerSection').find('.player2AnswersList').find('.answerByMicrophone').hide();
		
		$(".question #"+ questionId + "").find('#answerSection').find('.player2AnswerCountdown').show();
		answerIntervalFunc(questionId);
	}
}

function disconnect() {
    if (stompClient !== null) {
      stompClient.disconnect();
    }
}

//Connect to the WebSocket endpoint
function connectToWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        var playerId = currentPlayer.playerId;
        stompClient.send('/app/connection', {}, playerId);
        subscribeToTopics();
    });
}

function subscribeToTopics()
{
	stompClient.subscribe('/topic/game/'+roomId+'/newQuestion', function(message) {
		currentQuestionIdx = message.body;
		openNewQuestion();
    });
	
	stompClient.subscribe('/topic/game/'+roomId+'/strike', function(message) {
		
		var response = $.parseJSON(message.body);
		var nbStrikes = response.nbStrikes;
		var submittedPlayer = response.submittedPlayer;
		
		if(submittedPlayer == currentPlayer.playerId)
		{
			var maxId = 0;
			$(".player1AnswerSection .player1AnswerValue").each(function(i) {
				var id = $(this).attr('id');
				if(maxId < id)
				{
					maxId = id;
				}
			});
			
			$(".player1AnswerSection .player1AnswerValue#"+maxId).val('');
		}
		else
		{
			var maxId = 0;
			$(".player2AnswerSection .player2AnswerValue").each(function(i) {
				var id = $(this).attr('id');
				if(maxId < id)
				{
					maxId = id;
				}
			});
			
			$(".player2AnswerSection .player2AnswerValue#"+maxId).val('');
		}
		
		strike(nbStrikes);
		currentTurn = response.currentTurn;
		switchTurn();
    });
	
	stompClient.subscribe('/topic/game/'+roomId+'/complete', function(message) {
		
		var response = $.parseJSON(message.body);
		var player1Score = response.player1Score;
		var player2Score = response.player2Score;
		var questionsResult = response.questionsResult;
		
		window.onbeforeunload = null;
		completeGame(player1Score, player2Score, questionsResult);
    });
	
	stompClient.subscribe('/topic/game/'+roomId+'/updateScore', function(message) {
		var response = $.parseJSON(message.body);
		
		var currentPlayerScore = response[currentPlayer.playerId];
		$(".player-score[data-player='"+currentPlayer.playerId+"']").animate({'opacity': 0}, 400, function(){
			$(".player-score[data-player='"+currentPlayer.playerId+"']").html(currentPlayerScore + ' pts').animate({'opacity': 1}, 400);    
	    });
		
		var opponentPlayerScore = response[opponentPlayer.playerId];
		$(".player-score[data-player='"+opponentPlayer.playerId+"']").animate({'opacity': 0}, 400, function(){
			$(".player-score[data-player='"+opponentPlayer.playerId+"']").html(opponentPlayerScore + ' pts').animate({'opacity': 1}, 400);    
	    });
    });
	
	stompClient.subscribe('/topic/game/'+roomId+'/answer', function(message) {
		
		var response = $.parseJSON(message.body);
		var submittedPlayer = response.submittedPlayer;
		var question = questionsLst[currentQuestionIdx-1];
		var questionId = question.id;
		
		var correct = response.correct;
		if(correct == true)
		{
			  if(submittedPlayer == currentPlayer.playerId)
			  {
				  var maxId = 0;
					$(".player1AnswerSection .player1AnswerValue").each(function(i) {
						var id = $(this).attr('id');
						if(maxId < id)
						{
							maxId = id;
						}
					});
					
					$(".player1AnswerSection #"+maxId+".player1AnswerValue").val(response.matchingAnswer);
					$(".player1AnswerSection #"+maxId+".player1AnswerValue").closest('li').find('.answer-icon-div').empty();
					$(".player1AnswerSection #"+maxId+".player1AnswerValue").closest('li').find('.answer-icon-div').append("<div class='badge border-success success round badge-border float-right mr-1'><i class='la la-check'></i></div>");
					populatePlayer1AnswerElement(questionId);
			  }
			  else
			  {
				  var maxId = 0;
					$(".player2AnswerSection .player2AnswerValue").each(function(i) {
						var id = $(this).attr('id');
						if(maxId < id)
						{
							maxId = id;
						}
					});
					
					$(".player2AnswerSection #"+maxId+".player2AnswerValue").val(response.matchingAnswer);
					$(".player2AnswerSection #"+maxId+".player2AnswerValue").closest('li').find('.answer-icon-div').empty();
					$(".player2AnswerSection #"+maxId+".player2AnswerValue").closest('li').find('.answer-icon-div').append("<div class='badge border-success success round badge-border float-right mr-1'><i class='la la-check'></i></div>");
					populatePlayer2AnswerElement(questionId);
			  }
		}
		
		var allAnswersProvided = response.allAnswersProvided;
		if(allAnswersProvided)
		{
			toastr.success(language == 'en' ? "All answers related to this question are successfully provided. The question will be passed and the player who gave more answers will take the point. In case of tie, both players take one point." : "تم تقديم جميع الإجابات المتعلقة بهذا السؤال. سيتم تمرير السؤال وسيحصل اللاعب الذي قدم العدد الأكبر من الاجابات الصحيحة على نقطة السؤال!", language == 'en' ? 'All Answers Provided!' : "تم تقديم جميع الاجابات!");
		}
		else
		{
			currentTurn = response.currentTurn;
			switchTurn();
		}
    });
	
	stompClient.subscribe('/topic/game/'+roomId+'/end', function(message) {
		toastr.error(language == 'en' ? "Game is disconnected, you will redirected to the home page" : "حدث خطأ في الشبكة، سيتم توجيهك إلى الصفحة الرئيسية", language == 'en' ? 'Game Disconnected!' : "خطأ في الشبكة!");
		window.onbeforeunload = null;
		document.location = contextPath + "/?lang="+language;
    });
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

function strikePost()
{
	var request = {};
	request.roomId = roomId;
		
	var question = questionsLst[currentQuestionIdx-1];
	var questionId = question.id;
	request.questionId = questionId;
	request.submittedPlayer = currentPlayer.playerId;
	request.currentQuestionIdx = currentQuestionIdx;
	
	$.ajax({
		  url:contextPath + "/game/strike",
		  type:"POST",
		  data:JSON.stringify(request),
		  contentType:"application/json; charset=utf-8",
		  dataType:"json",
		  async: false,
		  success: function(response){
		  }
	});
}

function completeGame(player1Score, player2Score, questionsResult)
{
	// clear the intervals if any
  	clearIntervals();
  	
	toastr.info(language == 'en' ? "Game is completed, please check the game report and share with your friends." : "تم إكمال اللعبة، يرجى التحقق من تقرير اللعبة ومشاركته مع أصدقائك.", language == 'en' ? 'Game Complete' : 'انتهاء اللعبة!');
	
	$('#resultReportModal').find('.reportResult-table').empty();
	
	for(var i in questionsResult)
	{
		var question = questionsMap[i];
		var row = "<tr><td>"+(language == 'en' ? question.questionEn : question.questionAr)+"</td>";
		
		var passedPlayer = questionsResult[i];
		if(passedPlayer == player1.playerId)
		{
			row += "<td class='text-center'><div class='badge border-success success round badge-border'><i class='la la-check'></i></div></td>";
			row += "<td class='text-center'><div class='badge border-danger danger round badge-border'><i class='la la-close'></i></div></td>";
		}
		else
		{
			row += "<td class='text-center'><div class='badge border-danger danger round badge-border'><i class='la la-close'></i></div></td>";
			row += "<td class='text-center'><div class='badge border-success success round badge-border'><i class='la la-check'></i></div></td>";
		}
		
		$('#resultReportModal').find('.reportResult-table').append(row);
	}
	
	$('#resultReportModal').find('.player1-score').html("( " + player1Score + " "+(language == 'en' ? "pts" : "نقطة")+" )");
	$('#resultReportModal').find('.player2-score').html("( " + player2Score + " "+(language == 'en' ? "pts" : "نقطة")+" )");
	
	if(player1Score != player2Score)
	{
		$('#game-winner-section').find('#game-winner').html(player1Score > player2Score ? player1.username : player2.username);
	}
	else
	{
		$('#game-winner-section').hide();
		$('#game-draw-section').show();
	}
	
	$('#resultReportModal').modal('show');
}

function sendPing() {
	stompClient.send('/app/ping', {}, '');
}