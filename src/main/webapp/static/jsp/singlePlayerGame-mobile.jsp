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
    	
    	var currentTurn = '<c:out value='${player1.playerId}'/>';
    	var isMobile = '<c:out value='${isMobile}'/>';
    		
    	var questionsLst = [];
    	'<c:forEach items='${result.questions}' var='questionItem'>'
    		var question = {};
    		question.id = '<c:out value='${questionItem.id}'/>';
    		question.questionEn = '<c:out value='${questionItem.questionEn}'/>';
    		question.questionAr = '<c:out value='${questionItem.questionAr}'/>';
    		question.possibleAnswers = '<c:out value='${questionItem.possibleAnswers}'/>';
    		
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
    </style>

</head>
<!-- END: Head-->

<body class="vertical-layout vertical-menu-modern 1-column blank-page" data-menu="vertical-menu-modern" data-col="1-column">
    <!-- BEGIN: Content-->
	<div class="app-content content" id='whatdoyouknow'>
		<div class="content-overlay"></div>
		<div class="content-wrapper" style='height: 100%'>
			<div class="content-body" style='background-color: #282828; height: 100%'>
				<section id='hero-section'>
					<div class='row pt-2'>
						<div class='col-3 header-logo'>
							<a class="brand-logo" href="/?lang=${language}"><img class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/images/kickbrain-logo.png"/>">
		                    </a>
						</div>
					</div>
					<div class='row'>
						<div class='col-8 text-center pt-2' style='margin: 0 auto; float:none;'>
							<h1 style='color: #F0F0F0'><spring:message code="wdk.title"/></h1>
							<span style='font-size: 9px; color: #FFA500'><spring:message code="home.betaVersion"/></span>
						</div>
					</div>
				</section>
				<section class="row mt-3" id='game'>
					<div class="col-12">
						<div class="col-12">
							<div class="card" style='color: #000000; background-color: #EDEDED;'>
								<div class="card-content">
									<div class="card-header" style='background-color: #EDEDED;'>
										<div class='row'>
											<div class='col-6'>
												<div class='text-left'>
													<span class='question-number'></span>
												</div>
											</div>
											<div class='col-6' style='font-size: 12px'>
												<div class='text-right'>
													<span>${player1.username}: </span>
													<span class='player-score' style='font-weight: bold'>0 pts</span>
												</div>
											</div>
										</div>
									</div>
									<div class="card-body question" style='position: relative; padding: 10px;'>
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
				</section>
				<div class='row'>
					<div class='col-12 text-center'>
						<span style='font-size: 10px; color: #fbc02d; font-style: italic'><spring:message code="home.mobileVersionLabel"/></span>
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
		         			<span class='game-winner-label'><spring:message code="gameReport.singlePlayerWinnerTitle"/><span id='player1-score'></span></span>
		         		</div>
		         	</div>
		         	<div class='row mt-2'>
		         		<div class='col-12 table-responsive' style='max-height : 400px; overflow-y: auto;'>
		         			<table class="table game-score-table">
				         		<thead class="bg-yellow bg-lighten-4">
									<tr>
										<th width="80%"><spring:message code="wdk.question"/></th>
										<th width="20%" class='text-center'>
											<span>${player1.username}</span>
											<div>
												<span class='player1-score'></span>
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
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/singlePlayerGame.js"/>"></script>
    <!-- END: Page JS-->
    
</body>
<!-- END: Body-->

</html>