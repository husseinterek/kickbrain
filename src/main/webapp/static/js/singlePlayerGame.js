var currentQuestionIdx=0;
var globalAnswerP1IntervalId;
var questionsMap = {};
var nbStrikes=0;
var playerAnswers=[];
var totalScore = 0;
var questionsResult = {};
var globalAnswerPeriod = 20;
$(document).ready(function() {
	
	openNewQuestion();
	
	$('#game').on('click', '#skipQuestionBtn', function(e) {
		var question = questionsLst[currentQuestionIdx-1];
		var questionId = question.id;
		questionsResult[questionId] = false;
		Proceed();
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
});

function validateAnswer(element)
{
	var capturedAnswer = $(element).val().trim();
	if(capturedAnswer)
	{
		$(element).closest('li').find('.answerByMicrophone').replaceWith("<div class='ball-clip-rotate loader-primary float-right loader'><div></div></div>");
		$(element).attr("disabled", "disabled");
		
		var answerElement = $(element);
		var request = {};
		request.questionId = $(element).data('questionid');
		request.capturedAnswer = capturedAnswer;
		request.currentQuestionIdx = currentQuestionIdx;
		request.answerElementId = $(element).attr('id');
		request.submittedPlayerAnswers = playerAnswers;
		
		var questionId = $(element).data('questionid');
		$.ajax({
			  url:contextPath + "/game/validateSinglePlayerAnswer",
			  type:"POST",
			  data:JSON.stringify(request),
			  contentType:"application/json; charset=utf-8",
			  dataType:"json",
			  async: true,
			  success: function(response){
				  var isCorrect = response.correct;
				  var answerElementId = response.answerElementId;
				  
				  if(isCorrect)
				  {
					  playerAnswers.push(response.matchingAnswer);
					  
					  $(".player1AnswerSection #"+answerElementId+".player1AnswerValue").val(response.matchingAnswer);
					  $(".player1AnswerSection #"+answerElementId+".player1AnswerValue").closest('li').find('.answer-icon-div').empty();
					  $(".player1AnswerSection #"+answerElementId+".player1AnswerValue").closest('li').find('.answer-icon-div').append("<div class='badge border-success success round badge-border float-right mr-1'><i class='la la-check'></i></div>");
					  populatePlayer1AnswerElement(questionId);
						
					  var allAnswersProvided = response.allAnswersProvided;
					  if(allAnswersProvided)
					  {
						  // All possible answers are submitted. Give the point of the question to the player who gave more answers. In case of tie, both players get the point
						  totalScore++;
						  $(".player-score").animate({'opacity': 0}, 400, function(){
						        $(this).html(totalScore + ' pts').animate({'opacity': 1}, 400);    
						    });
						  questionsResult[questionId] = true;
						  toastr.success(language == 'en' ? "Good job! you have earned the point of the question!" : "تهانينا! لقد كسبت نقاط السؤال!", language == 'en' ? 'Question Passed!' : 'تمت الاجابة بنجاح!');
						  Proceed();
					  }
					  else
					  {
						  var question = questionsLst[currentQuestionIdx-1];
							
						  var remainingAnswers = question.possibleAnswers - playerAnswers.length;
						  $('#possibleAnswersLabel').html((language == 'en' ? "("+remainingAnswers +" remaining answers)" : "(باقي "+remainingAnswers+" اجابات)"))
					  }
				  }
				  else
				  {
					  $(".player1AnswerSection #"+answerElementId+".player1AnswerValue").closest('li').find('.loader').replaceWith("<button type='button' class='btn btn-icon btn-warning float-right answerByMicrophone'><i class='la la-play'></i></button>");
					  $(".player1AnswerSection #"+answerElementId+".player1AnswerValue").removeAttr('disabled');
					  $(".player1AnswerSection #"+answerElementId+".player1AnswerValue").focus();
					  
					  if(nbStrikes < 2)
					  {
						  strike();
					  }
					  else
					  {
						  toastr.error(language == 'en' ? "Sorry, you have reached the maximum failure attempts. Moving on to the next question!" : "لقد تم استنفاد جميع الأخطاء المتاحة!", language == 'en' ? 'Wrong Answer!' : 'جواب خاطىء!');
						  questionsResult[questionId] = false;
						  Proceed();
					  }
				  }
				  clearIntervals();
			  }
		});
	}
	else
	{
		toastr.warning(language == 'en' ? "Please type an answer!" : "من فضلك اكتب اجابتك!", language == 'en' ? 'Missing Answer!': 'اكتب اجابتك!');
		return;
	}
}

function openNewQuestion()
{
	// reset the flags when showing new question
	$('.player1AnswerSection #attempt1').replaceWith("<img class='img-fluid' id='attempt1' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	$('.player1AnswerSection #attempt2').replaceWith("<img class='img-fluid' id='attempt2' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	$('.player1AnswerSection #attempt3').replaceWith("<img class='img-fluid' id='attempt3' src='"+contextPath+"/static/images/empty-strike.png'></img>");
  	nbStrikes = 0;
  	playerAnswers = [];
  	
  	currentQuestionIdx++;
  	
  	clearIntervals();
  	
  	// hide the skip button if it's the last question
	if(currentQuestionIdx == 10)
	{
		$("#skipQuestionBtn").html(language == 'en' ? 'Complete Game' : 'انهاء اللعبة');
	}
  	
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
	
	recordAnswers(id, question.possibleAnswers);
}

function recordAnswers(id, possibleAnswers)
{
	var answerPeriod = 20;
	var answerSectionElement = "<div class='row mt-1' id='answerSection'>"
									+ "<div class='"+(isMobile == "true" ? "col-12" : "col-8")+"'>"
										+ "<div class='border-light player1AnswerSection rounded answer-border-thickness'>"
											+ "<div class='row p-1' style='height: 30px'>"
												+ "<div class='"+(isMobile == "true" ? "col-6" : "col-8")+"' style='font-size: 12px'>"
													+ "<p style='color: red' id='player1AnswerSectionLabel'><span class='blink_text'>" + (language == 'en' ? 'Recording your answers!' : 'جاري تسجيل أجوبتك!')+"</span><span id='possibleAnswersLabel'>"+ (language == 'en' ? "("+possibleAnswers+" remaining answers)" : "(باقي "+possibleAnswers+" اجابات)")+"</span></p>"
												+ "</div>"
												+ "<div class='col-1'>"
													+ "<div class='badge badge-glow badge-pill badge-warning player1AnswerCountDown'>"+answerPeriod+"</div>"
												+ "</div>"
												+ "<div class='"+(isMobile == "true" ? "col-4 text-right" : "col-3 text-right")+"'>"
													+ "<img class='img-fluid' id='attempt1' src='"+contextPath+"/static/images/empty-strike.png'></img>"
													+ "<img class='img-fluid' id='attempt2' src='"+contextPath+"/static/images/empty-strike.png'></img>"
													+ "<img class='img-fluid' id='attempt3' src='"+contextPath+"/static/images/empty-strike.png'></img>"
												+ "</div>"
											+ "</div>"
										+ "</div>"
									+ "</div>"
								+ "</div>";
	
	$(".question #"+ id + "").append(answerSectionElement);
	
	var player1AnswersList = "<div class='row player1AnswersList mt-1 p-1' style='position: relative; height: 200px'><div class='col-12'><ul class='list-group'></ul></div></div>";
	$(".question #"+ id + "").find('.player1AnswerSection').append(player1AnswersList);
	
	var vertical_scroll = new PerfectScrollbar(".player1AnswersList", {
	    wheelPropagation: true
	});
	
	populatePlayer1AnswerElement(id);
	
	if(!globalAnswerP1IntervalId)
	{
		answerIntervalFunc();
	}
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
									+ "<input type='text' data-questionid='"+questionId+"' class='form-control player1AnswerValue playerAnswerValue' style='font-size: 13px' id='"+(existingAnswers+1)+"' placeholder='"+(language == 'en' ? "Write your answer here!" : " اكتب الاجابة هنا! ")+"'/>"
								+ "</div>"
								+ "<div class='col-2 answer-icon-div'>"
									+ "<button type='button' class='btn btn-icon btn-warning float-right answerByMicrophone'><i class='la la-play'></i></button>"
								+ "</div>"
							+ "</div>"
						+ "</li>";
	$(".question #"+ questionId + "").find('.player1AnswersList').find("ul").append(answerElement);
	$(".question #"+ questionId + "").find('.player1AnswersList').find('.player1AnswerValue').focus();
}

function clearIntervals()
{
	globalAnswerPeriod = 20;
}

function strike()
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
	
	var question = questionsLst[currentQuestionIdx-1];
	var questionId = question.id;
	
	nbStrikes++;
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

function answerIntervalFunc()
{
	let answersIntervalId = setInterval(function () {
		globalAnswerP1IntervalId = answersIntervalId;
		globalAnswerPeriod--;
		if (globalAnswerPeriod < 0) {
			if(nbStrikes < 2)
			{
				strike();
			}
			else
			{
				toastr.error(language == 'en' ? "Sorry, you have reached the maximum attempts of failure answers. Moving to the next question!" : "لقد تم استنفاد جميع الأخطاء المتاحة!", language == 'en' ? 'Wrong Answer!' : 'جواب خاطىء!');
				Proceed();
			}
			clearIntervals();
		}
		else
		{
			$('#answerSection').find('.player1AnswerCountdown').html(globalAnswerPeriod);
		}
	}, 1000);
}

function completeGame()
{
	// clear the intervals if any
	clearInterval(globalAnswerP1IntervalId);
  	window.onbeforeunload = null;
  	
	toastr.info(language == 'en' ? "Game is completed, please check the game report and share with your friends." : "تم إكمال اللعبة، يرجى التحقق من تقرير اللعبة ومشاركته مع أصدقائك.", language == 'en' ? 'Game Complete' : 'انتهاء اللعبة!');
	
	$('#resultReportModal').find('.reportResult-table').empty();
	
	for(var i in questionsResult)
	{
		var question = questionsMap[i];
		var row = "<tr><td>"+(language == 'en' ? question.questionEn : question.questionAr)+"</td>";
		
		var result = questionsResult[i];
		if(result)
		{
			row += "<td class='text-center'><div class='badge border-success success round badge-border'><i class='la la-check'></i></div></td>";
		}
		else
		{
			row += "<td class='text-center'><div class='badge border-danger danger round badge-border'><i class='la la-close'></i></div></td>";
		}
		
		$('#resultReportModal').find('.reportResult-table').append(row);
	}
	
	$('#resultReportModal').find('#player1-score').html("( " + totalScore + " "+(language == 'en' ? "pts" : "نقطة")+" )");
	
	$('#resultReportModal').modal('show');
	
	// persist game report
	var request = {};
	
	var playerTmp = player1;
	playerTmp.playerId = null;
	
	request.player = playerTmp;
	request.totalScore = totalScore;
	
	var questionsResultLst = [];
	for(var i in questionsResult)
	{
		var questionResult = questionsResult[i];
		var questionResultObj = {};
		questionResultObj.questionId = i;
		questionResultObj.isPassed = questionResult;
		
		questionsResultLst.push(questionResultObj);
	}
	request.questionsResult = questionsResultLst;
	
	$.ajax({
		  url:contextPath + "/game/singleGameReport",
		  type:"POST",
		  data:JSON.stringify(request),
		  contentType:"application/json; charset=utf-8",
		  dataType:"json",
		  async: true,
		  success: function(response){
		  }
	});
}

function Proceed()
{
	// open new question or complete game
	if(currentQuestionIdx == 10)
	{
		// complete the game
		completeGame();
	}
	else
	{
		// open new question
		openNewQuestion();
	}
}