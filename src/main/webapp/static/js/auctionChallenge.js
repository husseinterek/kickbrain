var questionsMap={};
var currentQuestionIdx=0;
var totalScore = 0;
var globalBidIntervalId;
var globalAnswerIntervalId;
var nbFailedAttempts = 0;
var nbCorrectAnswers = 0;
var globalBidNumber = 0;
var botAnswers = [];
var playerAnswers = [];
$(document).ready(function() {
	
	openNewQuestion();
	
	var vertical_scroll = new PerfectScrollbar(".question", {
	    wheelPropagation: true
	});
	
	$('#game').on('keyup', '#bidNumber', function(e) {
		if(e.keyCode == 13)
		{
			var question = questionsLst[currentQuestionIdx-1];
			var id = question.id;
			recordAnswers(id);
		}
	});
	
	$('#game').on('click', '#skipQuestionBtn', function(e) {
		openNewQuestion();
	});
	
	$('#game .question').on('keyup', '.answerValue', function(e) {
		if(e.keyCode == 13)
		{
			var capturedAnswer = $(this).val();
			if(capturedAnswer)
			{
				$(this).closest('li').find('.answerByMicrophone').replaceWith("<div class='ball-clip-rotate loader-primary float-right loader'><div></div></div>");
				$(this).attr("disabled", "disabled");
				
				var answerElement = $(this);
				var request = {};
				request.questionId = $(this).data('questionid');
				request.capturedAnswer = capturedAnswer;
				
				if(botAnswers.length > 0)
				{
					request.botAnswers = botAnswers;
				}
				if(playerAnswers.length > 0)
				{
					request.playerAnswers = playerAnswers;
				}
				
				$.ajax({
					  url:contextPath + "/game/validateAnswer",
					  type:"POST",
					  data:JSON.stringify(request),
					  contentType:"application/json; charset=utf-8",
					  dataType:"json",
					  async: true,
					  success: function(response){
						  $(answerElement).closest('li').find('.loader').remove();
						  if(response.status == 1)
						  {
							  if(response.correct == true)
							  {
								  playerAnswers.push(response.matchingAnswer);
								  $(answerElement).data('passed', '1');
								  $(answerElement).data('answer', response.matchingAnswer);
								  $(answerElement).val(response.matchingAnswer);
								  $(answerElement).closest('li').find('.answer-icon-div').append("<div class='badge border-success success round badge-border float-right mr-1'><i class='la la-check'></i></div>");
								  
								  nbCorrectAnswers++;
								  $('#answerSection #correctAnswersLabel').html(nbCorrectAnswers);
								  
								  if(nbCorrectAnswers == globalBidNumber)
								  {
									  toastr.success("Congratulations, you have successfully provided "+globalBidNumber+" correct answers!", 'Question Passed!');
									  
									  // set the passed flag in the report
									  var questionItem = questionsMap[request.questionId];
									  questionItem.passed = 1;
									  questionsMap[request.questionId] = questionItem;
									  
									  calculateScore(request.questionId);
									  openNewQuestion();
								  }
								  else
								  {
									  switchTurn('bot', request.questionId);
								  }
							  }
							  else
						      {
								  $(answerElement).removeAttr("disabled");
								  $(answerElement).val('');
								  $(answerElement).focus();
								  
								  strike(request.questionId);
								  $(answerElement).closest('li').find('.answer-icon-div').append("<button type='button' class='btn btn-icon btn-warning float-right answerByMicrophone'><i class='la la-microphone'></i></button>");
						      }
						  }
						  else
						  {
							  clearInterval(globalAnswerIntervalId);
							  $(answerElement).removeAttr("disabled");
							  $(answerElement).val('');
							  $(answerElement).focus();
							  
							  toastr.error(response.errorMessage, 'Error!');
							  $(answerElement).closest('li').find('.answer-icon-div').append("<button type='button' class='btn btn-icon btn-warning float-right answerByMicrophone'><i class='la la-microphone'></i></button>");
						  }
					  }
				});
			}
			return;
		}
	});
	
	$('#resultReportModal').on('hidden.bs.modal', function () {
		document.location = contextPath + "/";
	});
	
});

function openNewQuestion()
{
	if(currentQuestionIdx == 10)
	{
		completeGame();
	}
	else
	{
		currentQuestionIdx++;
		
		// hide the skip button if it's the last question
		if(currentQuestionIdx == 10)
		{
			$("#skipQuestionBtn").html('Complete Game');
			$('#game').on('click', '#skipQuestionBtn', function(e) {
				completeGame();
			});
		}
		
		// reset the flags when showing new question
		nbFailedAttempts = 0;
		globalBidNumber = 0;
		nbCorrectAnswers = 0;
		$('#game .card-header #attempt1').css('color', '#b7b4b4');
	  	$('#game .card-header #attempt2').css('color', '#b7b4b4');
	  	$('#game .card-header #attempt3').css('color', '#b7b4b4');
	  	
	  	// clear the intervals if any
	  	clearIntervals();
	  	
		$('.question-number').html("Question (" + (currentQuestionIdx) + "/10)");
		$('.score-value').html(totalScore + " pts");
		$('.question').empty();
		
		var question = questionsLst[currentQuestionIdx-1];
		var id = question.id;
		
		var questionMapItem = {};
		questionMapItem.id = id;
		questionMapItem.description = question.questionEn;
		questionMapItem.score = 0;
		questionMapItem.passed = 0;
		questionsMap[id] = questionMapItem;
		
		var questionElement = "<div id='"+id+"' class='mr-1 ml-1'></div>";
		$('.question').append(questionElement);
		
		var bidPeriod = 30;
		var questionPromptElement =	"<div class='row'>"
										+ "<div class='col-11'>"
											+ "<p style='font-size: 22px; font-weight: bold;'>"+question.questionEn+"</p>"
										+ "</div>"
										+ "<div class='col-1'>"
										+	"<div class='badge badge-glow badge-pill badge-warning bidCountdown' style='font-size: 22px;'>"+bidPeriod+"</div>"
										+ "</div>"
									+ "</div>";
		$(".question #"+ id + "").append(questionPromptElement);
		
		var questionBidElement = "<div class='row mt-1' id='bidSection'>"
										+ "<div class='col-5'>"
											+ "<input type='number' id='bidNumber' class='form-control' placeholder='How many answers can you give?'/>"
										+ "</div>"
										+ "<div class='col-3'>"
											+ "<button id='answerBtn' type='button' class='btn' style='background-color:#FFA500; border-color: #FFA500; color: #F0F0F0' onclick='recordAnswers("+id+")'>Start Answering</button>"
										+ "</div>"
									+ "</div>";
		$(".question #"+ id + "").append(questionBidElement);
		
		$(".question #"+ id + "").find('#bidSection').find('#bidNumber').focus();
		
		var blankAnswerElement = "<div class='row mt-1' id='answerSection'>"
									+ "<div class='col-12 text-center'>"
										+ "<p class='mt-5'>Answers will be displayed here when recording.</p>"
									+ "</div>"
								+ "</div>";
		$(".question #"+ id + "").append(blankAnswerElement);
		
		let bidIntervalId = setInterval(function () {
			globalBidIntervalId = bidIntervalId;
			bidPeriod--;
			if (bidPeriod < 0) {
				var bidNumber = $(".question #"+ id + "").find('#bidNumber').val();
				if(!bidNumber)
				{
					toastr.warning("Question is skipped due to no answer", 'Question Timeout!');
					openNewQuestion();
				}
				else
				{
					recordAnswers(id);
				}
				clearInterval(bidIntervalId);
			}
			else
			{
				$(".question #"+ id + "").find('.bidCountdown').html(bidPeriod);
			}
		}, 1000);
	}
}

function recordAnswers(id)
{
	var bidNumber = $(".question #"+ id + "").find('#bidNumber').val();
	if(bidNumber)
	{
		clearInterval(globalBidIntervalId);
		globalBidNumber = bidNumber;
		$(".question #"+ id + "").find('.bidCountdown').remove();
		
		$(".question #"+ id + "").find('#bidSection').remove();
		$(".question #"+ id + "").find('#answerSection').remove();
		
		var answerPeriod = 15;
		var answerSectionElement = "<div class='row mt-1' id='answerSection'>"
										+ "<div class='col-6'>"
											+ "<div class='border-light playerAnswerSection rounded answer-border-thickness'>"
												+ "<div class='row p-1' style='height: 30px'>"
													+ "<div class='col-6'>"
														+ "<p class='blink_text' style='color: red'>Recording your answers</p>"
													+ "</div>"
													+ "<div class='col-1'>"
														+ "<div class='badge badge-glow badge-pill badge-warning answerCountdown'>"+answerPeriod+"</div>"
													+ "</div>"
													+ "<div class='col-5 text-right'>"
														+ "<span>Correct Answers: (</span><span id='correctAnswersLabel'>0</span><span>/"+bidNumber+")</span>"
													+ "</div>"
												+ "</div>"
											+ "</div>"
										+ "</div>"
										+ "<div class='col-6'>"
											+ "<div class='border-light botAnswerSection rounded answer-border-thickness'>"
												+ "<div class='row text-center p-1' style='height: 30px'>"
													+ "<span class='col-12'>Bot Answers</span>"
												+ "</div>"
											+ "</div>"
										+ "</div>"
									+ "</div>";
		$(".question #"+ id + "").append(answerSectionElement);
		
		var playerAnswersList = "<div class='row playerAnswersList mt-1 p-1'><div class='col-12'><ul class='list-group'></ul></div></div>";
		$(".question #"+ id + "").find('.playerAnswerSection').append(playerAnswersList);
		
		var botAnswersList = "<div class='row botAnswersList mt-1 p-1'><div class='col-12'><ul class='list-group'></ul></div></div>";
		$(".question #"+ id + "").find('.botAnswerSection').append(botAnswersList);
		
		populatePlayerAnswerElement(id);
		populateBotAnswerElement(id);
		
		switchTurn('player', id);
	}
	else
	{
		toastr.warning("Please fill how many answers you can claim for this question", 'Invalid Input!');
		return;
	}
}

function calculateScore(questionId)
{
	var nbAnswers = globalBidNumber;
	var questionGrade = 1;
	var score = nbAnswers * questionGrade;
	totalScore += score;
	
	$("#game .score-value").html(totalScore);
	
	// set the question's score in the report
	var questionItem = questionsMap[questionId];
	questionItem.score = score;
	questionsMap[questionId] = questionItem;
}

function completeGame()
{
	// clear the intervals if any
  	clearIntervals();
  	
	toastr.info("You have successfully completed the game, please check the game report and share with your friends.", 'Game Complete');
	$('#resultReportModal').find('.username').html(username);
	$('#resultReportModal').find('.totalScore').html(totalScore + " points");
	
	$('#resultReportModal').find('.reportResult-table').empty();
	for(var i in questionsMap)
	{
		var question = questionsMap[i];
		var row = "<tr><td>"+question.description+"</td>";
		
		if(question.passed == 1)
		{
			row += "<td><div class='badge border-success success round badge-border float-right mr-1'><i class='la la-check'></i></div></td>";
		}
		else
		{
			row += "<td><div class='badge border-danger danger round badge-border float-right mr-1'><i class='la la-close'></i></div></td>";
		}
		
		row += "<td class='text-center'>"+question.score+"</td></tr>";
		$('#resultReportModal').find('.reportResult-table').append(row);
	}
	
	$('#resultReportModal').modal('show');
}

function populatePlayerAnswerElement(questionId)
{
	var answerElement = "<li class='list-group-item' style='padding: 12px'>"
							+ "<div class='row'>"
								+ "<div class='col-10'>"
									+ "<input type='text' data-questionid='"+questionId+"' class='form-control answerValue' data-passed='0' placeholder='Write/Record your answer'/>"
								+ "</div>"
								+ "<div class='col-2 answer-icon-div'>"
									+ "<button type='button' class='btn btn-icon btn-warning float-right answerByMicrophone'><i class='la la-microphone'></i></button>"
								+ "</div>"
							+ "</div>"
						+ "</li>";
	$(".question #"+ questionId + "").find('.playerAnswersList').find("ul").append(answerElement);
	$(".question #"+ questionId + "").find('.playerAnswersList').find('.answerValue').focus();
}

function populateBotAnswerElement(questionId)
{
	var answerElement = "<li class='list-group-item' style='padding: 12px'>"
							+ "<div class='row'>"
								+ "<div class='col-10'>"
									+ "<input type='text' data-questionid='"+questionId+"' class='form-control botAnswerValue' data-answered='0' placeholder='Waiting my turn...' disabled/>"
								+ "</div>"
								+ "<div class='col-2 answer-icon-div'>"
									+ "<button type='button' class='btn btn-icon btn-secondary float-right botAnswerIcon'><i class='la la-user-secret'></i></button>"
								+ "</div>"
							+ "</div>"
						+ "</li>";
	$(".question #"+ questionId + "").find('.botAnswersList').find("ul").append(answerElement);
	$(".question #"+ questionId + "").find('.botAnswersList').find('.answerValue').focus();
}

function clearIntervals()
{
	if(globalBidIntervalId)
  	{
  		clearInterval(globalBidIntervalId);
  	}
  	if(globalAnswerIntervalId)
  	{
  		clearInterval(globalAnswerIntervalId);
  	}
}

function strike(questionId)
{
	nbFailedAttempts++;
	if(nbFailedAttempts == 3)
	{
		toastr.error("Sorry, you have reached the maximum attempts of failure answers!", 'Question Failed!');
		openNewQuestion();
	}
	else
	{
		switch(nbFailedAttempts)
		{
			  case 1:
			  {
				  	toastr.error("Your response is incorrect. " + (3 - nbFailedAttempts) + " more failure chances are remaining", 'Wrong Answer!');
				  	$('#game .card-header #attempt1').css('color', 'red');
				  	break;
			  }
			  case 2: 
			  {
				  	toastr.error("Your response is incorrect. " + (3 - nbFailedAttempts) + " more failure chances are remaining", 'Wrong Answer!');
				  	$('#game .card-header #attempt2').css('color', 'red');
				  	break;
			  }
		}
		switchTurn("bot", questionId);
	}
}

function answerIntervalFunc(questionId)
{
	answerPeriod = 15;
	$(".question #"+ questionId + "").find('#answerSection').find('.answerCountdown').html(answerPeriod);
	let answersIntervalId = setInterval(function () {
		globalAnswerIntervalId = answersIntervalId;
		answerPeriod--;
		if (answerPeriod < 0) {
			strike(questionId);
		}
		else
		{
			$(".question #"+ questionId + "").find('#answerSection').find('.answerCountdown').html(answerPeriod);
		}
	}, 1000);
}

function recordBotAnswer(questionId)
{
	var answerElement;
	$('.botAnswerValue').each(function(index) {
		  if($(this).data('answered') == '0')
		  {
			  answerElement = $(this);
		  }
	});
	
	var request = {};
	request.questionId = questionId;
	
	if(botAnswers.length > 0)
	{
		request.botAnswers = botAnswers;
	}
	if(playerAnswers.length > 0)
	{
		request.playerAnswers = playerAnswers;
	}
	
	$(answerElement).closest('li').find('.answer-icon-div').find('.botAnswerIcon').replaceWith("<div class='ball-clip-rotate loader-primary float-right loader'><div></div></div>");
	$.ajax({
		  url:contextPath + "/game/generateBotAnswer",
		  type:"POST",
		  data:JSON.stringify(request),
		  contentType:"application/json; charset=utf-8",
		  dataType:"json",
		  async: false,
		  success: function(response){
			  
			  $(answerElement).closest('li').find('.loader').remove();
			  if(response.status == 1)
			  {
				  $(answerElement).data('answered', '1');
				  botAnswers.push(response.answer);
				  $(answerElement).closest('li').find('.answer-icon-div').append("<div class='badge border-success success round badge-border float-right mr-1'><i class='la la-check'></i></div>");
				  $(answerElement).closest('li').find('.botAnswerValue').val(response.answer);
				  
				  switchTurn('player', questionId);
			  }
			  else
			  {
				  toastr.error(response.errorMessage, 'Error!');
				  $(answerElement).closest('li').find('.answer-icon-div').append("<button type='button' class='btn btn-icon btn-secondary float-right botAnswerIcon'><i class='la la-user-secret'></i></button>");
			  }
		  }
	});
}

function switchTurn(playerOrBot, questionId)
{
	if(playerOrBot == 'player')
	{
		$('.botAnswerSection').removeClass('border-warning');
		$('.botAnswerSection').removeClass('answer-border-thickness');
		$('.botAnswerSection').addClass('border-light');
		
		$('.playerAnswerSection').removeClass('border-light');
		$('.playerAnswerSection').addClass('border-warning');
		$('.playerAnswerSection').addClass('answer-border-thickness');
		
		// check if there is a blank answer box for the player
		var playerAnswerBoxExists = false;
		$('.playerAnswersList .answerValue').each(function(index) {
		  if($(this).data('passed') == '0')
		  {
			  playerAnswerBoxExists = true;
		  }
		});
	  
		if(!playerAnswerBoxExists)
		{
			populatePlayerAnswerElement(questionId);
	  	}
		answerIntervalFunc(questionId);
	}
	else
	{
		$('.playerAnswerSection').removeClass('border-warning');
		$('.playerAnswerSection').removeClass('answer-border-thickness');
		$('.playerAnswerSection').addClass('border-light');
		
		$('.botAnswerSection').removeClass('border-light');
		$('.botAnswerSection').addClass('border-warning');
		$('.botAnswerSection').addClass('answer-border-thickness');
		
		clearInterval(globalAnswerIntervalId);
		recordBotAnswer(questionId);
		populateBotAnswerElement(questionId);
	}
}