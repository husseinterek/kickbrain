<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@page contentType="text/html; charset=UTF-8" %>

<c:choose>
	<c:when test="${empty param.lang || param.lang == 'english' || param.lang == 'en'}">
		<c:set var="dir" value="ltr" />
		<c:set var="language" value="en" />
	</c:when>
	<c:otherwise>
		<c:set var="dir" value="rtl" />
		<c:set var="language" value="ar" />
	</c:otherwise>
</c:choose>

<html class="loading" lang="${language}" data-textdirection="${dir}">
<!-- BEGIN: Head-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimal-ui">
    <meta name="description" content="analytics,dashboard &amp; data dashboard.">
    <meta name="keywords" content="">
    <meta name="author" content="">
    <title>Kick Brain</title>


    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="${pageContext.request.contextPath}/static/images/favicon.ico"/>"/>
    <link rel="stylesheet" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/fonts.css"/>"/>

    <!-- BEGIN: Vendor CSS-->
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/vendors.min.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/toastr.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/sweetalert2.min.css"/>"/>
    <!-- END: Vendor CSS-->

    <!-- BEGIN: Theme CSS-->
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/bootstrap.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/bootstrap-extended.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/colors.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/components.css"/>"/>
    <!-- END: Theme CSS-->

    <!-- BEGIN: Page CSS - libraries CSS should be put here -->
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/vertical-menu-modern.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/palette-gradient.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/loaders.min.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/line-awesome.min.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/${dir}/toastr.css"/>"/>
    <!-- END: Page CSS-->

    <!-- BEGIN: Custom CSS - Custom CSS should be put here -->
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/style-${dir}.css"/>"/>
    <!-- END: Custom CSS-->
    
    <script>
    	var contextPath='<%=request.getContextPath()%>';
    	var language='<c:out value='${language}'/>';
    	
    	var roomId = '<c:out value='${roomId}'/>';
    	var player1= {};
    	player1.username = '<c:out value='${player1.username}'/>';
    	player1.playerId = '<c:out value='${player1.playerId}'/>';
    	
    	var player2= {};
    	player2.username = '<c:out value='${player2.username}'/>';
    	player2.playerId = '<c:out value='${player2.playerId}'/>';
    	
    	var currentTurn = '<c:out value='${currentTurn}'/>';
    	var isMobile = '<c:out value='${isMobile}'/>';
    		
    	var questionsLst = [];
    	'<c:forEach items='${result.questions}' var='questionItem'>'
    		var question = {};
    		question.id = '<c:out value='${questionItem.id}'/>';
    		question.questionEn = '<c:out value='${questionItem.questionEn}'/>';
    		question.questionAr = '<c:out value='${questionItem.questionAr}'/>';
    		
    		questionsLst.push(question);
		'</c:forEach>'
    </script>
    
    <style>
    	.blink_text {
		  animation: blinker 2s linear infinite;
		}
		
		@keyframes blinker {
		  50% {
		    opacity: 0.2;
		  }
		}
		
		.rounded-bell-box:hover {
			 -webkit-transform: scale(0.95);
			 -moz-transform:    scale(0.95);
			 -o-transform:      scale(0.95);
			 -ms-transform:     scale(0.95);
		}
    </style>

</head>
<!-- END: Head-->

<body class="vertical-layout vertical-menu-modern 1-column blank-page" data-menu="vertical-menu-modern" data-col="1-column" style='background-color: #282828;'>
    <!-- BEGIN: Content-->
	<div class="app-content content" id='whatdoyouknow'>
		<div class="content-overlay"></div>
		<div class="content-wrapper">
			<div class="content-body">
				<div id='hero-section' class='row pt-2'>
					<div class='col-2 header-logo'>
						<a class="brand-logo" href="/?lang=${language}"><img class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/images/kickbrain-logo.png"/>">
	                    </a>
					</div>
					<div class='col-8 text-center pt-2'>
						<h1 style='color: #F0F0F0'><spring:message code="wdk.title"/> <%-- <span style='font-size: 12px; color: #FFA500'><spring:message code="home.betaVersion"/></span> --%></h1>
					</div>
					<div class='col-1 text-right pt-2 header-flags pl-0 pr-0'>
						<a class="img-fluid" href='https://apps.apple.com/sa/app/kickbrain/id6464127361' target='_blank'>
							<img style='width: 150px' src="<c:url value="${pageContext.request.contextPath}/static/images/download-app-store.png"/>">
						</a>
					</div>
				</div>
				<div class="row" id='game' style='margin-top: 20px'>
					<div class="col-7" style='float: none;margin: 0 auto;'>
						<div class="col-12">
							<div class="card" style='color: #000000; background-color: #EDEDED;'>
								<div class="card-content">
									<div class="card-header" style='background-color: #EDEDED;'>
										<div class='row'>
											<div class='col-2'>
												<div class='text-left'>
													<span class='question-number'></span>
												</div>
											</div>
											<div class='col-10' style='font-size: 12px'>
												<div class='text-right'>
													<%-- <span><img class='img-fluid' style='width: 20px; height: 20px' src="<c:url value="${pageContext.request.contextPath}/static/images/points.png"/>"> ${player1.username}: </span> --%>
													<span>${player1.username}: </span>
													<span class='player-score' data-player='${player1.playerId}' style='font-weight: bold'>0 pts</span>
												</div>
												<div class='text-right'>
													<span>${player2.username}: </span>
													<span class='player-score' data-player='${player2.playerId}' style='font-weight: bold'>0 pts</span>
												</div>
											</div>
										</div>
									</div>
									<div class="card-body question" style='position: relative; padding: 10px;' >
										<div id="1" class='mr-1 ml-1'>
											<div class='row'>
												<div class='col-12'>
													<p class='question-description' style='font-size: 20px; font-weight: bold;'></p>
												</div>
											</div>
											<div class='row mt-2 mb-2' id='ring-section'>
												<div class='col-4 offset-4 text-center mb-1'>
													<div class='badge badge-glow badge-pill badge-warning player1AnswerCountDown'>20</div>
												</div>
												<div class='col-4 offset-4 text-center'>
													<div class='rounded-bell-box' style='cursor: pointer; border-radius: 25px; padding: 20px; background: radial-gradient(534px at 7.8% 17.6%, #f8f0d9 1.7%, #FFEAA8 91.8%); border-color: #BF9000; border-style: dashed;border-width: 1px'>
														<img class='img-fluid' src="<c:url value="${pageContext.request.contextPath}/static/images/ring-bell.svg"/>">
													</div>
												</div>
												<div class='col-4 offset-4 text-center mt-1'>
													<span id='bell-label' style='color: #282E6A; font-weight: bold;font-style: italic '>Ring the bell to get turn</span>
												</div>
											</div>
										</div>
									</div>
									<div class="card-footer pt-0 pb-0" style='background-color: #EDEDED'>
										<div class='skipQuestionDiv'>
											<button id='skipQuestionBtn' type="button" class="btn btn-secondary mt-1 mb-1"><spring:message code="wdk.skipQuestion"/></button>
										</div>
									</div>
								</div>
							</div> 
						</div>
					</div>
				</div>
				<div class='row'>
					<div class="col-6" style="float: none; margin: 0 auto;">
						<div style="float: none; margin: 0 auto; padding-bottom: 15px;" class='text-center'>
							<span style='color: #F8F8F8; font-size: 12px; font-weight: bold'><spring:message code="home.mobileAppDownload"/></span> 
								<a class="img-fluid" href='https://apps.apple.com/sa/app/kickbrain/id6464127361' target='_blank'>
									<img style='width: 90px' src="<c:url value="${pageContext.request.contextPath}/static/images/download-app-store.png"/>">
								</a>					
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade text-left" id="resultReportModal" tabindex="-1" role="dialog" aria-labelledby="resultReportModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" style='overflow-y: initial !important' role="document">
		     <div class="modal-content">
		         <div class="modal-header border-bottom-blue">
		             <h3 class="modal-title"><spring:message code="wdk.gameReport"/></h3>
		             <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		                 <span aria-hidden="true">&times;</span>
		             </button>
		         </div>
		         <div class="modal-body">
		         	<div class='row'>
		         		<div class='col-12 text-center' id='game-winner-section'>
		         			<i class="la la-trophy pl-1 pr-1" style="font-size: 45px; vertical-align: middle;"></i>
		         			<span class='game-winner-label'><spring:message code="gameReport.winnerTitle"/><span id='game-winner'></span></span>
		         		</div>
		         		<div class='col-12 text center' id='game-draw-section' style='display:none;'>
		         			<span class='game-draw-label'><spring:message code="gameReport.drawTitle"/></span>
		         		</div>
		         	</div>
		         	<div class='row mt-2'>
		         		<div class='col-12 table-responsive' style='max-height : 400px; overflow-y: auto;'>
		         			<table class="table game-score-table">
				         		<thead class="bg-yellow bg-lighten-4">
									<tr>
										<th width="70%"><spring:message code="wdk.question"/></th>
										<th width="15%" class='text-center'>
											<span>${player1.username}</span>
											<div>
												<span class='player1-score'></span>
											</div>
										</th>
										<th width="15%" class='text-center'>
											<span>${player2.username}</span>
											<div>
												<span class='player2-score'></span>
											</div>
										</th>
									</tr>
								</thead>
				         		<tbody class='reportResult-table'>
				         		</tbody>
				         	</table>
		         		</div>
		         	</div>
		         </div>
		     </div>
		 </div>
	</div>

	<!-- BEGIN: Vendor JS-->
	<script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/vendors.min.js"/>"></script>
    <!-- END Vendor JS-->
    
    <!-- BEGIN: Page Vendor JS - All libraries imports should be put here-->
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/jquery-ui.min.js"/>"></script>
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/jqBootstrapValidation.js"/>"></script>
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/toastr.min.js"/>"></script>
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/sweetalert2.all.min.js"/>"></script>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    
    <!-- END: Page Vendor JS-->
    
    <!-- BEGIN: Theme JS-->
    <!-- END: Theme JS-->
    
    <!-- BEGIN: Page JS - Custom libraries should be put here-->
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/bellChallenge.js"/>"></script>
    <!-- END: Page JS-->
    
</body>
<!-- END: Body-->

</html>