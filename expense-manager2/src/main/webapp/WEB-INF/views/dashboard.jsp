<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>ExpenseIQ – Dashboard</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link
	href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
.recent-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 20px;
	margin-top: 24px;
}

.recent-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 12px 16px;
	border-radius: 10px;
	border: 1px solid var(--border);
	transition: background .15s;
}

.recent-item:hover {
	background: rgba(99, 102, 241, .05);
}

.recent-item+.recent-item {
	margin-top: 8px;
}

.ri-left {
	display: flex;
	align-items: center;
	gap: 12px;
}

.ri-icon {
	width: 36px;
	height: 36px;
	border-radius: 10px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: .9rem;
}

.ri-icon.inc {
	background: rgba(16, 185, 129, .12);
	color: #10b981;
}

.ri-icon.exp {
	background: rgba(239, 68, 68, .12);
	color: #ef4444;
}

.ri-cat {
	font-size: .88rem;
	font-weight: 500;
}

.ri-date {
	font-size: .75rem;
	color: var(--text-muted);
	margin-top: 2px;
}

.ri-amt {
	font-family: 'Syne', sans-serif;
	font-size: .95rem;
	font-weight: 700;
}

.ri-amt.inc {
	color: #10b981;
}

.ri-amt.exp {
	color: #ef4444;
}

.balance-positive {
	color: #10b981 !important;
}

.balance-negative {
	color: #ef4444 !important;
}

.quick-actions {
	display: flex;
	gap: 12px;
	flex-wrap: wrap;
	margin-top: 24px;
}

.qa-card {
	flex: 1;
	min-width: 160px;
	background: var(--card-bg);
	border: 1px solid var(--border);
	border-radius: 14px;
	padding: 18px 20px;
	cursor: pointer;
	transition: .2s;
	text-align: center;
	text-decoration: none;
	color: inherit;
}

.qa-card:hover {
	border-color: var(--accent);
	transform: translateY(-2px);
}

.qa-card i {
	font-size: 1.5rem;
	margin-bottom: 8px;
	display: block;
}

.qa-card span {
	font-size: .83rem;
	color: var(--text-secondary);
}
</style>
</head>
<body>

	<nav class="sidebar">
		<div class="sidebar-brand">
			<span class="brand-icon">💰</span> <span class="brand-name">ExpenseIQ</span>
		</div>
		<ul class="nav-links">
			<li><a href="${pageContext.request.contextPath}/dashboard"
				class="active"><i class="fas fa-chart-pie"></i><span>Dashboard</span></a></li>
			<li><a href="${pageContext.request.contextPath}/income/list"><i
					class="fas fa-arrow-trend-up"></i><span>Income</span></a></li>
			<li><a href="${pageContext.request.contextPath}/expense/list"><i
					class="fas fa-arrow-trend-down"></i><span>Expenses</span></a></li>
			<li><a href="${pageContext.request.contextPath}/reports"><i
					class="fas fa-chart-bar"></i><span>Reports</span></a></li>
			<li><a href="${pageContext.request.contextPath}/backup/list"><i
					class="fas fa-database"></i><span>Backup</span></a></li>
		</ul>
		<div class="sidebar-footer">
			<span>v1.0.0</span>
		</div>
	</nav>

	<main class="main-content">
		<div class="topbar">
			<div class="page-title">Dashboard</div>
			<div class="topbar-actions">
				<a href="${pageContext.request.contextPath}/income/add"
					class="btn btn-success btn-sm"><i class="fas fa-plus"></i> Add
					Income</a> <a href="${pageContext.request.contextPath}/expense/add"
					class="btn btn-danger  btn-sm"><i class="fas fa-plus"></i> Add
					Expense</a>
			</div>
		</div>

		<c:if test="${not empty sessionScope.successMsg}">
			<div class="alert alert-success" style="margin: 16px 28px 0">
				<i class="fas fa-check-circle"></i> ${sessionScope.successMsg}
			</div>
			<c:remove var="successMsg" scope="session" />
		</c:if>

		<div class="page-body">

			<!-- Summary Cards -->
			<div class="summary-cards">
				<div class="summary-card card-income">
					<div class="card-label">This Month – Income</div>
					<div class="card-amount">
						₹
						<fmt:formatNumber value="${totalIncome}" pattern="#,##0.00" />
					</div>
					<i class="fas fa-arrow-trend-up card-icon" style="color: #10b981"></i>
				</div>
				<div class="summary-card card-expense">
					<div class="card-label">This Month – Expense</div>
					<div class="card-amount">
						₹
						<fmt:formatNumber value="${totalExpense}" pattern="#,##0.00" />
					</div>
					<i class="fas fa-arrow-trend-down card-icon" style="color: #ef4444"></i>
				</div>
				<div class="summary-card card-balance">
					<div class="card-label">Net Balance</div>
					<div
						class="card-amount ${balance >= 0 ? 'balance-positive' : 'balance-negative'}">
						${balance >= 0 ? '+' : ''}₹
						<fmt:formatNumber value="${balance}" pattern="#,##0.00" />
					</div>
					<i class="fas fa-scale-balanced card-icon" style="color: #818cf8"></i>
				</div>
			</div>

			<!-- Quick Actions -->
			<div class="quick-actions">
				<a href="${pageContext.request.contextPath}/income/add"
					class="qa-card"> <i class="fas fa-plus-circle"
					style="color: #10b981"></i> <span>Add Income</span>
				</a> <a href="${pageContext.request.contextPath}/expense/add"
					class="qa-card"> <i class="fas fa-minus-circle"
					style="color: #ef4444"></i> <span>Add Expense</span>
				</a> <a href="${pageContext.request.contextPath}/reports"
					class="qa-card"> <i class="fas fa-chart-bar"
					style="color: #818cf8"></i> <span>View Reports</span>
				</a> <a href="${pageContext.request.contextPath}/backup/list"
					class="qa-card"> <i class="fas fa-database"
					style="color: #f59e0b"></i> <span>Backup Data</span>
				</a>
			</div>

			<!-- Recent Transactions -->
			<div class="recent-grid">
				<!-- Recent Income -->
				<div class="section-card">
					<div class="section-head">
						<h3>
							<i class="fas fa-arrow-trend-up" style="color: #10b981"></i>
							Recent Income
						</h3>
						<a href="${pageContext.request.contextPath}/income/list"
							style="font-size: .78rem; color: var(--accent)">View all →</a>
					</div>
					<div style="padding: 12px 16px">
						<c:choose>
							<c:when test="${empty recentIncome}">
								<p
									style="color: var(--text-muted); font-size: .85rem; text-align: center; padding: 20px 0">No
									income records yet.</p>
							</c:when>
							<c:otherwise>
								<c:forEach var="t" items="${recentIncome}">
									<div class="recent-item">
										<div class="ri-left">
											<div class="ri-icon inc">
												<i class="fas fa-arrow-up"></i>
											</div>
											<div>
												<div class="ri-cat">${t.category}</div>
												<div class="ri-date">
													<fmt:formatDate value="${t.transactionDate}"
														pattern="dd MMM yyyy" />
													<c:if test="${not empty t.note}"> · ${t.note}</c:if>
												</div>
											</div>
										</div>
										<div class="ri-amt inc">
											+₹
											<fmt:formatNumber value="${t.amount}" pattern="#,##0.00" />
										</div>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</div>
				</div>

				<!-- Recent Expense -->
				<div class="section-card">
					<div class="section-head">
						<h3>
							<i class="fas fa-arrow-trend-down" style="color: #ef4444"></i>
							Recent Expenses
						</h3>
						<a href="${pageContext.request.contextPath}/expense/list"
							style="font-size: .78rem; color: var(--accent)">View all →</a>
					</div>
					<div style="padding: 12px 16px">
						<c:choose>
							<c:when test="${empty recentExpense}">
								<p
									style="color: var(--text-muted); font-size: .85rem; text-align: center; padding: 20px 0">No
									expense records yet.</p>
							</c:when>
							<c:otherwise>
								<c:forEach var="t" items="${recentExpense}">
									<div class="recent-item">
										<div class="ri-left">
											<div class="ri-icon exp">
												<i class="fas fa-arrow-down"></i>
											</div>
											<div>
												<div class="ri-cat">${t.category}</div>
												<div class="ri-date">
													<fmt:formatDate value="${t.transactionDate}"
														pattern="dd MMM yyyy" />
													<c:if test="${not empty t.note}"> · ${t.note}</c:if>
												</div>
											</div>
										</div>
										<div class="ri-amt exp">
											-₹
											<fmt:formatNumber value="${t.amount}" pattern="#,##0.00" />
										</div>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
			<!-- /recent-grid -->

		</div>
		<!-- /page-body -->
	</main>
</body>
</html>