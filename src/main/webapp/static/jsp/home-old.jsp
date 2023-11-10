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
    </script>

</head>
<!-- END: Head-->

<body class="vertical-layout vertical-menu-modern 1-column blank-page" data-menu="vertical-menu-modern" data-col="1-column" onLoad="document.getElementById('username').focus();" style='background-color: #282828'>
    <!-- BEGIN: Content-->
	<div class="app-content content" id='homepage'>
		<div class="content-overlay"></div>
		<div class="content-wrapper" style='background-color: #282828'>
			<div class="content-body">
				<div id='hero-section' class='row pt-2 header'>
					<div class='col-2 header-logo'>
						<a class="brand-logo" href="/?lang=${language}"><img class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/images/kickbrain-logo.png"/>">
	                    </a>
					</div>
					<div class='col-8 text-center pt-2'>
						<h1 style='color: #F0F0F0'><spring:message code="home.title"/> <%-- <span style='font-size: 12px; color: #FFA500'><spring:message code="home.betaVersion"/></span> --%></h1>
						<p class='pt-1' style='color: #F8F8F8; width: 70%; margin: 0 auto; font-size: 12px;'><spring:message code="home.subtitle"/></p>
					</div>
					<div class='col-1 text-right pt-2 header-flags pl-0 pr-0'>
						<a href="/?lang=en"><img style='width:25px; height: 25px' class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/flags/4x3/us.svg"/>"></a>
						<a href="/?lang=ar"><img style='width:33px; height: 33px;' class="img-fluid ar-flag" src="<c:url value="${pageContext.request.contextPath}/static/flags/4x3/sa.svg"/>"></a>
						<%-- <a href="#" data-toggle="modal" data-backdrop="static" data-keyboard="false" data-target="#contactusModal"><img style='height: 25px;' class="img-fluid contactus-icon" src="<c:url value="${pageContext.request.contextPath}/static/images/contactus.png"/>"></a> --%>
						<a href="https://www.instagram.com/kickbrainchallenge/" target="_blank" style='color: #ffffff !important;' class='insta-icon'><i class='la la-instagram' style='font-size: 28px; vertical-align: middle;'></i></a>
					</div>
				</div>
				<div id='game-rules' class='row mt-5'>
					<div class='col-8 offset-2 my-auto'>
						<div class='row'>
							<div class='col-12'>
								<div class='card-group'>
									<div class='card game-rules-card rounded game-rules-card-selected' id='whatDoYouKnow' style='background-color: #EDEDED;'>
										<div class="card-content">
											<div class="card-body" style='color: #000000;'>
												<div class="bs-callout-warning">
													<p><strong><spring:message code="home.challenge1Title"/></strong></p>
													<p id='challengeRulesTitle'><span style='text-decoration:underline'><spring:message code="home.challenge1RulesTitle"/></span></p>
													<ol id='challengeRules'>
														<li><spring:message code="home.challenge1Rule1"/></li>
														<li><spring:message code="home.challenge1Rule2"/></li>
														<li><spring:message code="home.challenge1Rule3"/></li>
														<li><spring:message code="home.challenge1Rule4"/></li>
														<li><spring:message code="home.challenge1Rule5"/></li>
													</ol>
												</div>
											</div>
										</div>
									</div>
									<div class='card ml-3 game-rules-card rounded' id='auction' style='background-color: #c5c5c5; cursor: default !important'>
										<div class="card-content position-relative">
											<div class='text-right'>
												<span class="badge badge-pill badge-border badge-square position-absolute coming-soon-badge"><spring:message code="home.comingSoon"/></span>	
											</div>
											<div class="card-body" style='color: #000000;'>
												<div class="bs-callout-warning">
													<p><strong><spring:message code="home.challenge2Title"/></strong></p>
													<p id='challengeRulesTitle'><span style='text-decoration:underline'><spring:message code="home.challenge2RulesTitle"/></span></p>
													<ol id='challengeRules'>
														<li><spring:message code="home.challenge2Rule1"/></li>
														<li><spring:message code="home.challenge2Rule2"/></li>
														<li><spring:message code="home.challenge2Rule3"/></li>
														<li><spring:message code="home.challenge2Rule4"/></li>
														<li><spring:message code="home.challenge2Rule5"/></li>
													</ol>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class='row'>
							<div class='col-7' style='float: none;margin: 0 auto;'>
								<div class="input-group mt-2">
									<spring:message code="home.usernamePlaceholder" var="titleLabel" />
									<input id='username' type="text" class="form-control" placeholder="${titleLabel}" style='background-color: #EDEDED; color: #000000'>
									<button id='newOnlineGameBtn' type="button" class="spinner-button btn ml-1" style='background-color: #FFA500 !important; border-color: #FFA500 !important; color: #F0F0F0'>
										<i class='la la-globe'></i>
										<spring:message code="home.startOnlineGameBtn"/>
									</button>
									<button id='newSingleGameBtn' type="button" class="btn btn-secondary ml-1" style='font-size: 12px'>
										<i class='la la-user'></i>
										<spring:message code="home.startSingleGameBtn"/>
									</button>
									<div class='pt-1'>
										<label style='color: #F8F8F8; font-size: 9px;'><spring:message code="home.gameBtnAlert"/></label>
									</div>
								</div>
							</div>
						</div>
						<div class='row'>
							<div class="col-5" style="float: none; margin: 0 auto; padding-top: 10px;">
								<h6 class="line-on-side text-muted text-center font-small-3">
									<span style="background: #282828; color: #FFA500; font-weight: bold;"> <spring:message code="home.existingGameSeparator"/> </span>
								</h6>
								<div style="float: none; margin: 0 auto; padding-bottom: 15px;" class='text-center'>
									<a href="#" data-toggle="modal" data-backdrop="static" data-keyboard="false" data-target="#joinExistingGameModal"><span style='color: #eacc9a; font-size: 12px;'><spring:message code="home.existingGameLabel"/></span></a>							
								</div>
							</div>
						</div>
						<div class='row'>
							<div class="col-6" style="float: none; margin: 0 auto;">
								<div style="float: none; margin: 0 auto; padding-bottom: 15px;" class='text-center'>
									<span style='color: #F8F8F8; font-size: 10px; font-weight: bold'><spring:message code="home.mobileAppDownload"/></span> 
										<a class="img-fluid" href='https://apps.apple.com/sa/app/kickbrain/id6464127361' target='_blank'>
											<img style='width: 90px' src="<c:url value="${pageContext.request.contextPath}/static/images/download-app-store.png"/>">
										</a>					
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%-- <div class="modal fade text-left" id="joinExistingGameModal" tabindex="-1" role="dialog" aria-labelledby="joinExistingGameModalLabel" aria-hidden="true">
		<div class="modal-dialog" style='overflow-y: initial !important' role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title"><spring:message code="home.joinExistingGame.title"/></h3>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form class="form-horizontal form-simple">
                         <fieldset class="form-group position-relative has-icon-left mb-1">
                         	<spring:message code="home.joinExistingGame.username" var="existingGameUsernameLabel"/>
                             <input type="text" class="form-control form-control-lg input-lg" id="joinExistingGame-username" placeholder="${existingGameUsernameLabel}">
                             <div class="form-control-position">
                                 <i class="la la-user"></i>
                             </div>
                         </fieldset>
                         <fieldset class="form-group position-relative has-icon-left mb-1">
                         	  <spring:message code="home.joinExistingGame.roomId" var="existingGameRoomIdLabel"/>
                             <input type="text" class="form-control form-control-lg input-lg" id="joinExistingGame-roomId" placeholder="${existingGameRoomIdLabel}">
                             <div class="form-control-position">
                                 <i class="la la-gamepad"></i>
                             </div>
                         </fieldset>
                         <button id='joinExistingGameBtn' type="button" class="btn btn-block" style='background-color: #FFA500 !important; border-color: #FFA500 !important; color: #F0F0F0'><spring:message code="home.joinExistingGame.submit"/></button>
                     </form>
				</div>
			</div>
		</div>
	</div> --%>
	<div class="modal fade text-left" id="joinExistingGameModal" tabindex="-1" role="dialog" aria-labelledby="joinExistingGameModalLabel" aria-hidden="true">
		<div class="modal-dialog" style='overflow-y: initial !important' role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title"><spring:message code="home.joinExistingGame.title"/></h3>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div id="waiting-room-list">
						<spring:message code="home.joinExistingGame.username" var="existingGameUsernameLabel"/>
                        <input type="text" class="form-control mb-1" id="joinExistingGame-username" placeholder="${existingGameUsernameLabel} *">
                        
                        <ul class="list-group overflow-auto list">
                        </ul>
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
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/home.js"/>"></script>
    <!-- END: Page JS-->
    
</body>
<!-- END: Body-->

</html>