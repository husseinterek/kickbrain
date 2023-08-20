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
    	var username='<c:out value='${username}'/>';
    	
    	var questionsLst = [];
    	'<c:forEach items='${result.questions}' var='questionItem'>'
    		var question = {};
    		question.id = '<c:out value='${questionItem.id}'/>';
    		question.questionEn = '<c:out value='${questionItem.questionEn}'/>';
    		question.questionAr = '<c:out value='${questionItem.questionEn}'/>';
    		
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
	<div class="app-content content">
		<div class="content-overlay"></div>
		<div class="content-wrapper" style='height: 100%'>
			<div class="content-body" style='background-color: #282828; height: 100%'>
				<section id='hero-section' style='height: 10%'>
					<div class='row pt-2'>
						<div class='col-2' style='margin-left: 20px'>
							<a class="brand-logo" href="/?lang=${param.language}"><img class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/images/kickbrain-logo.png"/>">
		                    </a>
						</div>
						<div class='col-8 text-center pt-2'>
							<h1 style='color: #F0F0F0'>Bot Player Challenge</h1>
						</div>
					</div>
				</section>
				<section class="row mt-3" id='game' style='height: 80%'>
					<div class="col-8 offset-2" style='height: 100%'>
						<div class="col-12" style='height: 85%'>
							<div class="card" style='color: #000000; background-color: #EDEDED;height: 100%'>
								<div class="card-content" style='height: 100%'>
									<div class="card-header" style='background-color: #EDEDED; height: 15%'>
										<div class='row'>
											<div class='col-10'>
												<div class='text-left'>
													<span class='question-number'></span>
												</div>
												<div class='text-left' style='margin-top: 5px'>
													<i id='attempt1' class='la la-circle' style='color: #b7b4b4'></i>
													<i id='attempt2' class='la la-circle' style='margin-left: 5px; color: #b7b4b4'></i>
													<i id='attempt3' class='la la-circle' style='margin-left: 5px; color: #b7b4b4'></i>
												</div>
											</div>
											<div class='col-2'>
												<div class='text-right'>
													<span class='username-label'>${username}</span>
												</div>
												<div class='text-right'>
													<span class='score-label' style='font-weight: bold; color: #00bc00'>Total Score: </span><span class='score-value'></span>
												</div>
											</div>
										</div>
									</div>
									<div class="card-body question" style='position: relative; padding: 10px; height: 80%' >
									</div>
									<div class="card-footer pt-0 pb-0" style='background-color: #EDEDED'>
										<div class='text-right'>
											<button id='skipQuestionBtn' type="button" class="btn btn-secondary mt-1 mb-1">Skip Question</button>
										</div>
									</div>
								</div>
							</div> 
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
	
	<div class="modal fade text-left" id="resultReportModal" tabindex="-1" role="dialog" aria-labelledby="resultReportModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" style='overflow-y: initial !important' role="document">
		     <div class="modal-content">
		         <div class="modal-header border-bottom-blue">
		             <h3 class="modal-title">Game Report</h3>
		             <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		                 <span aria-hidden="true">&times;</span>
		             </button>
		         </div>
		         <div class="modal-body">
		         	<div class='row'>
		         		<div class='col-12'>
		         			<span>Username: </span><span class='username'></span>
		         		</div>
		         	</div>
		         	<div class='row mb-1'>
		         		<div class='col-12'>
		         			<span>Total Score: </span><span class='totalScore'></span>
		         		</div>
		         	</div>
		         	<div class='row'>
		         		<div class='col-12 table-responsive' style='max-height : 400px; overflow-y: auto;'>
		         			<table class="table table-bordered table-striped table-middle">
				         		<thead class="bg-info bg-lighten-2 white">
									<tr>
										<th width="70%">Question</th>
										<th width="15%">Status</th>
										<th width="15%">Points</th>
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
    <!-- END: Page Vendor JS-->
    
    <!-- BEGIN: Theme JS-->
    <!-- END: Theme JS-->
    
    <!-- BEGIN: Page JS - Custom libraries should be put here-->
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/auctionChallenge.js"/>"></script>
    <!-- END: Page JS-->
    
</body>
<!-- END: Body-->

</html>