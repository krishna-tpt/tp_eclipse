<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
</div>
<!-- .page -->
<footer class="footer">
	ExpenseOS &copy;
	<%=java.time.Year.now().getValue()%>
	— PostgreSQL + Jakarta Servlet
</footer>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>