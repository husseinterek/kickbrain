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

<body class="vertical-layout vertical-menu-modern 1-column blank-page" data-menu="vertical-menu-modern" data-col="1-column" onLoad="document.getElementById('username').focus();">
    <!-- BEGIN: Content-->
	<div class="app-content content" id='homepage'>
		<div class="content-overlay"></div>
		<div class="content-wrapper">
			<div class="content-body" style='background-color: #282828'>
				<div class='row header pt-1'>
					<div class='col-2 pl-0 pr-0'>
						<a class="brand-logo" href="/?lang=${language}"><img class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/images/kickbrain-logo.png"/>">
	                    </a>
					</div>
					<div class='col-8 text-center pt-2'>
						<h4 style='color: #F0F0F0'><spring:message code="home.title"/></h4>
						<p class='pt-1' style='color: #F8F8F8; font-size: 8px'><spring:message code="home.subtitle"/></p>
					</div>
					<div class='col-2 pl-0 pr-0'>
						<a href="/?lang=en"><img style='width:20px; height: 20px' class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/flags/4x3/us.svg"/>"></a>
						<a href="/?lang=ar"><img style='width:20px; height: 20px;' class="img-fluid ar-flag" src="<c:url value="${pageContext.request.contextPath}/static/flags/4x3/sa.svg"/>"></a>
					</div>
				</div>
				<div class='row h-100 m-1' id='game-rules'>
					<div class='col-12'>
								<div id="carouselExampleSlidesOnly" class="carousel slide" data-ride="carousel">
									<div class="carousel-inner">
										<div class='card-group'>
										<div class='carousel-item active card game-rules-card rounded game-rules-card-selected' id='whatDoYouKnow' style='background-color: #EDEDED;'>
											<div class="d-block w-100 card-content">
												<div class="card-body" style='color: #000000;'>
													<div class="bs-callout-warning">
														<p style='font-size: 11px;'><strong><spring:message code="home.challenge1Title"/></strong></p>
														<p id='challengeRulesTitle' style='font-size: 9px;'><span style='text-decoration:underline'><spring:message code="home.challenge1RulesTitle"/></span></p>
														<ol id='challengeRules' style='font-size: 9px;'>
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
										<div class='carousel-item card game-rules-card rounded game-rules-card-selected' id='auction' style='background-color: #EDEDED; cursor: default !important'>
											<div class="d-block w-100 card-content">
												<div class="card-body" style='color: #000000;'>
													<div class="bs-callout-warning">
														<p style='font-size: 11px;'><strong><spring:message code="home.challenge2Title"/></strong></p>
														<p id='challengeRulesTitle' style='font-size: 9px;'><span style='text-decoration:underline'><spring:message code="home.challenge2RulesTitle"/></span></p>
														<ol id='challengeRules' style='font-size: 9px;'>
															<li><spring:message code="home.challenge2Rule1"/></li>
															<li><spring:message code="home.challenge2Rule2"/></li>
															<li><spring:message code="home.challenge2Rule3"/></li>
															<li><spring:message code="home.challenge2Rule4"/></li>
														</ol>
													</div>
												</div>
											</div>
										</div>
										<div class='carousel-item card game-rules-card rounded game-rules-card-selected' id='auction' style='background-color: #EDEDED; cursor: default !important'>
											<div class="d-block w-100 card-content">
												<div class="card-body" style='color: #000000;'>
													<div class="bs-callout-warning">
														<p style='font-size: 11px;'><strong><spring:message code="home.challenge3Title"/></strong></p>
														<p id='challengeRulesTitle' style='font-size: 9px;'><span style='text-decoration:underline'><spring:message code="home.challenge3RulesTitle"/></span></p>
														<ol id='challengeRules' style='font-size: 9px;'>
															<li><spring:message code="home.challenge3Rule1"/></li>
															<li><spring:message code="home.challenge3Rule2"/></li>
															<li><spring:message code="home.challenge3Rule3"/></li>
															<li><spring:message code="home.challenge3Rule4"/></li>
														</ol>
													</div>
												</div>
											</div>
										</div>
									</div>
							</div>
							<div class='row mt-2'>
								<div class='col-12'>
									<div>
										<span style='color: #F8F8F8; font-size: 16px; font-style: italic'><spring:message code="home.reflectingUpdates"/></span>
									</div>
									<div class='mt-1'>
										<span style='color: #ffa500; font-size: 16px;'><spring:message code="home.redirectToMobile"/></span>
									</div>
									<div class='mt-1 text-center'>
										<a class="img-fluid" href='https://apps.apple.com/sa/app/kickbrain/id6464127361' target='_blank'>
											<img style='width: 150px' src="<c:url value="${pageContext.request.contextPath}/static/images/download-app-store.png"/>">
										</a>
										<a class="img-fluid" href='https://play.google.com/store/apps/details?id=com.app.kickbrain' target='_blank'>
											<img style='width: 150px' src="<c:url value="${pageContext.request.contextPath}/static/images/download-google-play.png"/>">
										</a>
									</div>
								</div>
							</div>
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
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/home.js"/>"></script>
    <!-- END: Page JS-->
    
</body>
<!-- END: Body-->

</html>