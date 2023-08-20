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

<body class="vertical-layout vertical-menu-modern 1-column blank-page" data-menu="vertical-menu-modern" data-col="1-column" onLoad="document.getElementById('questionEn').focus();" style='background-color: #282828'>
    <!-- BEGIN: Content-->
	<div class="app-content content" id='homepage'>
		<div class="content-overlay"></div>
		<div class="content-wrapper" style='background-color: #282828'>
			<div class="content-body">
				<div id='hero-section' class='row pt-2 header'>
					<div class='col-2 header-logo'>
						<a class="brand-logo" href="/?lang=${language}"><img class="img-fluid" src="<c:url value="${pageContext.request.contextPath}/static/images/kickbrain-logo.png"/>">
	                    </a>
	                    <div class='mt-1'>
							<span style='font-size: 10px; color: #eacc9a; font-style: italic'><spring:message code="home.mobileVersionLabel"/></span>			                    	
	                    </div>
					</div>
					<div class='col-8 text-center pt-2'>
						<h1 style='color: #F0F0F0'><spring:message code="home.title"/> <span style='font-size: 12px; color: #FFA500'><spring:message code="home.betaVersion"/></span></h1>
						<p class='pt-1' style='color: #F8F8F8; width: 70%; margin: 0 auto; font-size: 12px;'><spring:message code="home.subtitle"/></p>
					</div>
				</div>
				<div id='game-rules' class='row mt-5'>
					<div class='col-6 offset-3 my-auto'>
						<div class='row'>
							<div class='col-12'>
								<div class='card-group'>
									<div class='card game-rules-card rounded game-rules-card-selected' id='whatDoYouKnow' style='background-color: #EDEDED;'>
										<div class="card-content">
											<div class="card-body" style='color: #000000;'>
												<form class="form-horizontal form-simple">
							                         <fieldset class="form-group position-relative mb-1">
							                             <input type="text" class="form-control form-control-lg input-lg" id="questionEn" placeholder="English Question">
							                         </fieldset>
							                         <fieldset class="form-group position-relative mb-1">
							                             <input type="text" class="form-control form-control-lg input-lg" id="questionAr" placeholder="Arabic Question">
							                         </fieldset>
							                         <fieldset class="form-group position-relative mb-1">
							                             <textarea rows="5" class="form-control form-control-lg input-lg" id="answers" placeholder="Place Answers Here"></textarea>
							                         </fieldset>
							                         <button id='submitQuestion' type="button" class="btn btn-block" style='background-color: #FFA500 !important; border-color: #FFA500 !important; color: #F0F0F0'>Submit Question</button>
							                     </form>
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
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <!-- END: Page Vendor JS-->
    
    <!-- BEGIN: Theme JS-->
    <!-- END: Theme JS-->
    
    <!-- BEGIN: Page JS - Custom libraries should be put here-->
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/admin.js"/>"></script>
    <!-- END: Page JS-->
    
</body>
<!-- END: Body-->

</html>