<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@page contentType="text/html; charset=UTF-8" %>

<script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/vendors.min.js"/>"></script>
<script src="<c:url value="${pageContext.request.contextPath}/static/js/plugins/jquery-ui.min.js"/>"></script>

<script>
    $(document).ready(function (){
        if(navigator.userAgent.toLowerCase().indexOf("android") > -1){
            window.location.href = 'https://play.google.com/store/apps/details?id=com.app.kickbrain';
        }
        if(navigator.userAgent.toLowerCase().indexOf("iphone") > -1){
            window.location.href = 'https://apps.apple.com/sa/app/kickbrain/id6464127361';
        }
    });
</script>