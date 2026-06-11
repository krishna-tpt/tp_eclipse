<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>ExpenseIQ – ${pageTitle != null ? pageTitle : 'Dashboard'}</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link
	href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<script
	src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

	<nav class="sidebar">
		<div class="sidebar-brand">
			<span class="brand-icon">💰</span> <span class="brand-name">ExpenseIQ</span>
		</div>
		<ul class="nav-links">
			<li><a href="${pageContext.request.contextPath}/dashboard"
				class="${activePage == 'dashboard' ? 'active' : ''}"> <i
					class="fas fa-chart-pie"></i><span>Dashboard</span></a></li>
			<li><a href="${pageContext.request.contextPath}/income/list"
				class="${activePage == 'income' ? 'active' : ''}"> <i
					class="fas fa-arrow-trend-up"></i><span>Income</span></a></li>
			<li><a href="${pageContext.request.contextPath}/expense/list"
				class="${activePage == 'expense' ? 'active' : ''}"> <i
					class="fas fa-arrow-trend-down"></i><span>Expenses</span></a></li>
			<li><a href="${pageContext.request.contextPath}/reports"
				class="${activePage == 'reports' ? 'active' : ''}"> <i
					class="fas fa-chart-bar"></i><span>Reports</span></a></li>
		</ul>
		<div class="sidebar-footer">
			<span>v1.0.0</span>
		</div>
	</nav>

	<main class="main-content">
		<div class="topbar">
			<div class="page-title">${pageTitle != null ? pageTitle : 'Dashboard'}</div>
			<div class="topbar-actions">
				<a href="${pageContext.request.contextPath}/income/add"
					class="btn btn-success btn-sm"> <i class="fas fa-plus"></i> Add
					Income
				</a> <a href="${pageContext.request.contextPath}/expense/add"
					class="btn btn-danger btn-sm"> <i class="fas fa-plus"></i> Add
					Expense
				</a>
			</div>
		</div>

		<c:if test="${not empty sessionScope.successMsg}">
			<div class="alert alert-success">
				<i class="fas fa-check-circle"></i> ${sessionScope.successMsg}
			</div>
			<c:remove var="successMsg" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.errorMsg}">
			<div class="alert alert-danger">
				<i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMsg}
			</div>
			<c:remove var="errorMsg" scope="session" />
		</c:if>
		<c:if test="${not empty error}">
			<div class="alert alert-danger">
				<i class="fas fa-exclamation-circle"></i> ${error}
			</div>
		</c:if>

		<div class="page-body">