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
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/select2.min.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="${pageContext.request.contextPath}/static/css/plugins/datatables.min.css"/>"/>
    
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

<body class="vertical-layout vertical-menu-modern 1-column blank-page" data-menu="vertical-menu-modern" data-col="1-column" style='background-color: #282828'>
    <!-- BEGIN: Content-->
	<div class="app-content content">
		<div class="content-overlay"></div>
		<div class="content-wrapper" style='background-color: #282828'>
			<div class="content-body">
				<!-- users list start -->
                <section class="questions-list-wrapper">
                    <!-- <div class="users-list-filter px-1">
                    </div> -->
                    <div class='row mt-2'>
                    	<div class='col-10 offset-1 my-auto'>
                    		<div class='row'>
                    			<div class='col-6'>
                    				<input type="button" id='addnewQuestionBtn' class="btn btn-info" value="Add New Question">
                    				<a href="/admin-answers" target="_blank" class="btn btn-success" role="button" style='margin-left: 10px'><span>Answers Management</span></a>
                    			</div>
                    			<div class='col-6' style='color: white'>
                    				<span>Questions Management</span>
                    			</div>
                    		</div>
                    	</div>
					</div>
					<div class='row mt-1'>
							<div class='col-10 offset-1 my-auto'>
								<div class="questions-list-table">
			                        <div class="card">
			                            <div class="card-content">
			                                <div class="card-body">
													<div class="table-responsive">
				                                        <table id="questions-list-datatable" class="table">
				                                            <thead>
				                                                <tr>
				                                                    <th style="width: 30%">Description Ar</th>
				                                                    <th style="width: 30%">Description En</th>
				                                                    <th style="width: 30%">Answers En</th>
				                                                    <th style="width: 5%">Category</th>
				                                                    <th style="width: 5%">Edit</th>
				                                                </tr>
				                                            </thead>
				                                            <tbody id='question-table'>
				                                            </tbody>
				                                        </table>
				                                    </div>
												</div>
			                                    <!-- datatable ends -->
			                                </div>
			                            </div>
			                        </div>
							</div>
                    </div>
                </section>
			</div>
		</div>
	</div>
	
	<div class="modal fade text-left" id="questionModal" tabindex="-1" role="dialog" aria-labelledby="questionModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" style='overflow-y: initial !important' role="document">
		     <div class="modal-content">
		         <div class="modal-header border-bottom-blue">
		             <h3 class="modal-title">Question</h3>
		             <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		                 <span aria-hidden="true">&times;</span>
		             </button>
		         </div>
		         <form novalidate id='newDashboardForm'>
		             <div class="modal-body">
		             	<div class="form-group">
		                 	<div class="row">
		                 		<input type="hidden" class="form-control" id="question-id">
		                 		<div class='col-12'>
		                 			<input type="text" placeholder="Description Ar" class="form-control mt-1" id="question-desc-ar">
		                 		</div>
		                 		<div class='col-12'>
		                 			<input type="text" placeholder="Description En" class="form-control mt-1" id="question-desc-en">
		                 		</div>
		                 		<div class='col-12 mt-1'>
		                 			<select multiple='multiple' id="question-answers-en" >
									</select>
		                 		</div>
		                 		<div class='col-12 mt-1'>
		                 			<select id="question-category">
										<option value='-1' selected>Select Option</option>
										<option value='1'>What do you know</option>
										<option value='2'>Auction</option>
										<option value='3'>Bell</option>
										<option value='4'>Who am I</option>
									</select>
		                 		</div>
		                 		<div class='col-12 mt-1'>
		                 			<select id="question-tag">
										<option value='1'>La Liga</option>
										<option value='2'>Premier League</option>
										<option value='3'>Champions League</option>
										<option value='5' selected>Others</option>
									</select>
		                 		</div>
		                 	</div>
		                </div>
		             </div>
		             <div class="modal-footer">
		             	 <input type="button" id='saveQuestionBtn' class="btn btn-info" value="Save">
		                 <input type="button" class="btn btn-secondary" data-dismiss="modal" value="Cancel">
		             </div>
	         	</form>
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
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/select2.full.min.js"/>"></script>
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/datatables.min.js"/>"></script>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <!-- END: Page Vendor JS-->
    
    <!-- BEGIN: Theme JS-->
    <!-- END: Theme JS-->
    
    <!-- BEGIN: Page JS - Custom libraries should be put here-->
    <script src="<c:url value="${pageContext.request.contextPath}/static/js/admin-questions.js"/>"></script>
    <!-- END: Page JS-->
    
</body>
<!-- END: Body-->

</html>