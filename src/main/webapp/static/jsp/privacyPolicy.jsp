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
						<h1 style='color: #F0F0F0'>Privacy Policy</h1>
					</div>
					<div class='col-1 text-right pt-2 header-flags pl-0 pr-0'>
						<a href="https://www.instagram.com/kickbrainchallenge/" target="_blank" style='color: #ffffff !important;' class='insta-icon'><i class='la la-instagram' style='font-size: 28px; vertical-align: middle;'></i></a>
					</div>
				</div>
				<div id='policy' class='row mt-5'>
					<div class='col-8 offset-2 my-auto'>
						<div class='row'>
							<div class='col-12'>
								<div class='card-group'>
									<div class='card game-rules-card rounded game-rules-card-selected' id='whatDoYouKnow' style='background-color: #EDEDED;'>
										<div class="card-content">
											<div class="card-body" style='color: #000000;'>
												<div class="bs-callout-warning">
													<p>By using <span style='font-weight: bold'>KickBrain</span>, you are consenting to our policies regarding the collection, use and disclosure of personal information set out in this privacy policy.</p>
													<p style='font-weight: bold'>1. Collection of Personal Information</p>
													<span>We do not collect, store, use or share any information, personal or otherwise.</span>
													<p class='mt-2' style='font-weight: bold'>2. Email</p>
													<span>If you email the developer for support or other feedback, your email address will be retained for quality assurance purposes. The email addresses will be used only to reply to the concerns or suggestions raised and will never be used for any marketing purpose.</span>
													<p class='mt-2' style='font-weight: bold'>3. Disclosure of Personal Information</p>
													<span>We will not disclose your information to any third party except if you expressly consent or where required by law.</span>
													<p class='mt-2' style='font-weight: bold'>4. Contacting Us</p>
													<span>If you have any questions regarding this privacy policy, you can email <span style='font-weight:bold'>terek.houssein@gmail.com</span></span>
												</div>
											</div>
										</div>
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
    
    <!-- END: Page Vendor JS-->
    
    <!-- BEGIN: Theme JS-->
    <!-- END: Theme JS-->
    
    <!-- BEGIN: Page JS - Custom libraries should be put here-->
    <!-- END: Page JS-->
    
</body>
<!-- END: Body-->

</html>