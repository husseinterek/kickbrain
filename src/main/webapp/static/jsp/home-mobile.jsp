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
						<span style='font-size: 9px; color: #FFA500'><spring:message code="home.betaVersion"/></span>
						<p class='pt-1' style='color: #F8F8F8; font-size: 8px'><spring:message code="home.subtitle"/></p>
					</div>
					<div class='col-2 pl-0 pr-0'>
						<a href="/?lang=en"><img style='width:20px; height: 20px' class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/flags/4x3/us.svg"/>"></a>
						<a href="/?lang=ar"><img style='width:20px; height: 20px;' class="img-fluid ar-flag" src="<c:url value="${pageContext.request.contextPath}/static/flags/4x3/sa.svg"/>"></a>
					</div>
				</div>
				<div class='row h-100 m-1' id='game-rules'>
					<div class='col-12'>
						<div class='row'>
							<div class='col-12'>
								<div class='card-group'>
									<div class='card game-rules-card rounded game-rules-card-selected' id='whatDoYouKnow' style='background-color: #EDEDED;'>
										<div class="card-content">
											<div class="card-body" style='color: #000000;'>
												<div class="bs-callout-warning">
													<p style='font-size: 10px;'><strong><spring:message code="home.challenge1Title"/></strong></p>
													<p id='challengeRulesTitle' style='font-size: 8px;'><span style='text-decoration:underline'><spring:message code="home.challenge1RulesTitle"/></span></p>
													<ol id='challengeRules' style='font-size: 8px;'>
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
									<div class='card game-rules-card rounded' id='auction' style='background-color: #c5c5c5; cursor: default !important; margin-top: 5px'>
										<div class="card-content position-relative">
											<div class='text-right'>
												<span class="badge badge-pill badge-border badge-square position-absolute coming-soon-badge" style='width: 70px; font-size: 10px;height: 20px;'><spring:message code="home.comingSoon"/></span>	
											</div>
											<div class="card-body" style='color: #000000;'>
												<div class="bs-callout-warning">
													<p style='font-size: 10px;'><strong><spring:message code="home.challenge2Title"/></strong></p>
													<p id='challengeRulesTitle' style='font-size: 8px;'><span style='text-decoration:underline'><spring:message code="home.challenge2RulesTitle"/></span></p>
													<ol id='challengeRules' style='font-size: 8px;'>
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
							<div class='col-12'>
								<div>
									<spring:message code="home.usernamePlaceholder" var="titleLabel" />
									<input id='username' type="text" class="form-control" placeholder="${titleLabel}" style='background-color: #EDEDED; color: #000000'>
								</div>
								<div class='mt-1'>
									<button id='newOnlineGameBtn' type="button" class="spinner-button btn" style='background-color: #FFA500 !important; border-color: #FFA500 !important; color: #F0F0F0'>
										<i class='la la-globe'></i>
										<spring:message code="home.startOnlineGameBtn"/>
									</button>
									<button id='newSingleGameBtn' type="button" class="btn btn-secondary ml-1" style='font-size: 10px'>
										<i class='la la-user'></i>
										<spring:message code="home.startSingleGameBtn"/>
									</button>
								</div>
								<div style='padding-top: 5px;'>
									<label style='color: #F8F8F8; font-size: 8px;'><spring:message code="home.gameBtnAlert"/></label>
								</div>
							</div>
							<div class='col-12 text-center'>
								<span style='font-size: 10px; color: #fbc02d; font-style: italic'><spring:message code="home.mobileVersionLabel"/></span>
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