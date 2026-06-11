<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>ExpenseIQ – ${type == 'income' ? 'Income' : 'Expenses'}</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link
	href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
.amt-cell {
	font-family: 'Syne', sans-serif;
	font-weight: 700;
}

.amt-income {
	color: #10b981;
}

.amt-expense {
	color: #ef4444;
}

.action-btns {
	display: flex;
	gap: 6px;
}

.btn-tbl {
	width: 30px;
	height: 30px;
	border-radius: 8px;
	border: none;
	cursor: pointer;
	display: inline-flex;
	align-items: center;
	justify-content: center;
	font-size: .8rem;
	transition: .15s;
}

.btn-edit {
	background: rgba(99, 102, 241, .15);
	color: #818cf8;
}

.btn-edit:hover {
	background: rgba(99, 102, 241, .3);
}

.btn-del {
	background: rgba(239, 68, 68, .12);
	color: #f87171;
}

.btn-del:hover {
	background: rgba(239, 68, 68, .3);
}

/* column manager */
.col-mgr {
	background: var(--card-bg);
	border: 1px solid var(--border);
	border-radius: 14px;
	padding: 20px 24px;
	margin-bottom: 20px;
}

.col-mgr h4 {
	font-family: 'Syne', sans-serif;
	font-size: .9rem;
	font-weight: 700;
	margin-bottom: 14px;
	display: flex;
	align-items: center;
	gap: 8px;
}

.col-chips {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 14px;
}

.col-chip {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	background: rgba(99, 102, 241, .1);
	border: 1px solid rgba(99, 102, 241, .25);
	color: #818cf8;
	border-radius: 20px;
	padding: 4px 12px;
	font-size: .78rem;
}

.col-chip-del {
	background: none;
	border: none;
	cursor: pointer;
	color: #f87171;
	font-size: .7rem;
	padding: 0;
	line-height: 1;
}

.add-col-form {
	display: flex;
	gap: 10px;
	flex-wrap: wrap;
	align-items: flex-end;
}

.add-col-form .form-group {
	margin-bottom: 0;
	flex: 1;
	min-width: 140px;
}

/* modal */
.modal-overlay {
	display: none;
	position: fixed;
	inset: 0;
	z-index: 1000;
	background: rgba(0, 0, 0, .65);
	backdrop-filter: blur(4px);
	align-items: center;
	justify-content: center;
}

.modal-overlay.open {
	display: flex;
}

.modal-box {
	background: var(--card-bg);
	border: 1px solid var(--border);
	border-radius: 18px;
	width: 100%;
	max-width: 420px;
	padding: 28px;
	animation: slideUp .22s ease;
}

@
keyframes slideUp {
	from {transform: translateY(16px);
	opacity: 0
}

to {
	transform: translateY(0);
	opacity: 1
}

}
.modal-box h3 {
	font-family: 'Syne', sans-serif;
	font-size: 1.1rem;
	font-weight: 700;
	margin-bottom: 8px;
}

.modal-box p {
	color: var(--text-muted);
	font-size: .85rem;
	margin-bottom: 20px;
}

.modal-actions {
	display: flex;
	gap: 10px;
	justify-content: flex-end;
	margin-top: 20px;
}

.empty-state {
	text-align: center;
	padding: 52px 20px;
	color: var(--text-muted);
}

.empty-state i {
	font-size: 2.8rem;
	opacity: .25;
	display: block;
	margin-bottom: 14px;
}
</style>
</head>
<body>

	<nav class="sidebar">
		<div class="sidebar-brand">
			<span class="brand-icon">💰</span><span class="brand-name">ExpenseIQ</span>
		</div>
		<ul class="nav-links">
			<li><a href="${pageContext.request.contextPath}/dashboard"><i
					class="fas fa-chart-pie"></i><span>Dashboard</span></a></li>
			<li><a href="${pageContext.request.contextPath}/income/list"
				class="${type=='income'  ? 'active':''}"><i
					class="fas fa-arrow-trend-up"></i><span>Income</span></a></li>
			<li><a href="${pageContext.request.contextPath}/expense/list"
				class="${type=='expense' ? 'active':''}"><i
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
			<div class="page-title">${type == 'income' ? 'Income' : 'Expenses'}</div>
			<div class="topbar-actions">
				<a href="${pageContext.request.contextPath}/${type}/add"
					class="btn ${type=='income' ? 'btn-success' : 'btn-danger'} btn-sm">
					<i class="fas fa-plus"></i> Add ${type == 'income' ? 'Income' : 'Expense'}
				</a>
			</div>
		</div>

		<c:if test="${not empty sessionScope.successMsg}">
			<div class="alert alert-success" style="margin: 14px 28px 0">
				<i class="fas fa-check-circle"></i> ${sessionScope.successMsg}
			</div>
			<c:remove var="successMsg" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.errorMsg}">
			<div class="alert alert-danger" style="margin: 14px 28px 0">
				<i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMsg}
			</div>
			<c:remove var="errorMsg" scope="session" />
		</c:if>

		<div class="page-body">

			<!-- ── Custom Column Manager ── -->
			<div class="col-mgr">
				<h4>
					<i class="fas fa-table-columns" style="color: var(--accent)"></i>
					Custom Columns <span
						style="font-size: .75rem; font-weight: 400; color: var(--text-muted)">(add
						extra fields to every record)</span>
				</h4>

				<div class="col-chips">
					<c:choose>
						<c:when test="${empty customColumns}">
							<span style="font-size: .8rem; color: var(--text-muted)">No
								custom columns yet.</span>
						</c:when>
						<c:otherwise>
							<c:forEach var="col" items="${customColumns}">
								<span class="col-chip"> <i class="fas fa-tag"
									style="font-size: .65rem"></i> ${col.columnLabel}
									<form method="post"
										action="${pageContext.request.contextPath}/${type}/list"
										style="margin: 0">
										<input type="hidden" name="action" value="deleteColumn">
										<input type="hidden" name="columnId" value="${col.id}">
										<button type="submit" class="col-chip-del"
											title="Remove column"
											onclick="return confirm('Delete column \'${col.columnLabel}\'? All stored values will be lost.')">
											✕</button>
									</form>
								</span>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</div>

				<form method="post"
					action="${pageContext.request.contextPath}/${type}/list"
					class="add-col-form">
					<input type="hidden" name="action" value="addColumn">
					<div class="form-group">
						<label>Column Label</label> <input type="text" name="columnLabel"
							class="form-control" placeholder="e.g. Invoice No, Tag, Ref"
							required>
					</div>
					<div class="form-group">
						<label>Type</label> <select name="columnType" class="form-control">
							<option value="TEXT">Text</option>
							<option value="NUMBER">Number</option>
							<option value="DATE">Date</option>
						</select>
					</div>
					<button type="submit" class="btn btn-primary btn-sm"
						style="align-self: flex-end">
						<i class="fas fa-plus"></i> Add Column
					</button>
				</form>
			</div>

			<!-- ── Filter Bar ── -->
			<form method="get"
				action="${pageContext.request.contextPath}/${type}/list"
				class="filter-bar">
				<div class="filter-group">
					<label>From Date</label> <input type="date" name="fromDate"
						class="form-control" value="${param.fromDate}">
				</div>
				<div class="filter-group">
					<label>To Date</label> <input type="date" name="toDate"
						class="form-control" value="${param.toDate}">
				</div>
				<div class="filter-group">
					<label>Category</label> <select name="category"
						class="form-control">
						<option value="">All Categories</option>
						<c:forEach var="cat" items="${categories}">
							<option value="${cat}" ${param.category == cat ? 'selected':''}>${cat}</option>
						</c:forEach>
					</select>
				</div>
				<div class="filter-group">
					<label>Sort By</label> <select name="sortBy" class="form-control">
						<option value="transaction_date"
							${param.sortBy=='transaction_date'||empty param.sortBy ? 'selected':''}>Date</option>
						<option value="amount" ${param.sortBy=='amount'   ? 'selected':''}>Amount</option>
						<option value="category"
							${param.sortBy=='category' ? 'selected':''}>Category</option>
					</select>
				</div>
				<div class="filter-group">
					<label>Order</label> <select name="sortDir" class="form-control">
						<option value="DESC"
							${param.sortDir=='DESC'||empty param.sortDir ? 'selected':''}>Newest
							First</option>
						<option value="ASC" ${param.sortDir=='ASC'  ? 'selected':''}>Oldest
							First</option>
					</select>
				</div>
				<button type="submit" class="btn btn-primary btn-sm"
					style="align-self: flex-end">
					<i class="fas fa-filter"></i> Filter
				</button>
				<a href="${pageContext.request.contextPath}/${type}/list"
					class="btn btn-secondary btn-sm" style="align-self: flex-end">
					<i class="fas fa-xmark"></i> Clear
				</a>
			</form>

			<!-- ── Total chip ── -->
			<div class="totals-bar">
				<div
					class="total-chip ${type=='income' ? 'income-chip' : 'expense-chip'}">
					<i class="fas fa-sigma"></i> Total ${type == 'income' ? 'Income' : 'Expense'}:
					<strong>₹<fmt:formatNumber value="${totalAmount}"
							pattern="#,##0.00" /></strong>
				</div>
				<div class="total-chip" style="color: var(--text-muted)">
					<i class="fas fa-list"></i> ${transactions.size()} records
				</div>
			</div>

			<!-- ── Table ── -->
			<div class="section-card">
				<c:choose>
					<c:when test="${empty transactions}">
						<div class="empty-state">
							<i class="fas fa-inbox"></i>
							<p>
								No ${type} records found. <a
									href="${pageContext.request.contextPath}/${type}/add"
									style="color: var(--accent)">Add one now →</a>
							</p>
						</div>
					</c:when>
					<c:otherwise>
						<div class="table-wrapper">
							<table class="data-table">
								<thead>
									<tr>
										<th>#</th>
										<th>Date</th>
										<th>Time</th>
										<th>Category</th>
										<th>Amount</th>
										<th>Note</th>
										<c:forEach var="col" items="${customColumns}">
											<th>${col.columnLabel}</th>
										</c:forEach>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="t" items="${transactions}" varStatus="st">
										<tr>
											<td style="color: var(--text-muted); font-size: .8rem">${st.index+1}</td>
											<td><fmt:formatDate value="${t.transactionDate}"
													pattern="dd MMM yyyy" /></td>
											<td style="color: var(--text-muted)">${t.transactionTime}</td>
											<td><span
												style="background: rgba(99, 102, 241, .1); color: #818cf8; padding: 3px 9px; border-radius: 6px; font-size: .78rem">
													${t.category} </span></td>
											<td
												class="amt-cell ${type=='income' ? 'amt-income' : 'amt-expense'}">
												${type=='income' ? '+' : '-'}₹<fmt:formatNumber
													value="${t.amount}" pattern="#,##0.00" />
											</td>
											<td style="color: var(--text-muted); font-size: .85rem">${not empty t.note ? t.note : '—'}</td>
											<c:forEach var="col" items="${customColumns}">
												<td style="font-size: .85rem; color: var(--text-secondary)">
													${not empty t.customValues[col.columnName] ? t.customValues[col.columnName] : '—'}
												</td>
											</c:forEach>
											<td>
												<div class="action-btns">
													<a
														href="${pageContext.request.contextPath}/${type}/edit?id=${t.id}"
														class="btn-tbl btn-edit" title="Edit"><i
														class="fas fa-pen"></i></a>
													<button class="btn-tbl btn-del" title="Delete"
														onclick="confirmDelete(${t.id})">
														<i class="fas fa-trash"></i>
													</button>
												</div>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</c:otherwise>
				</c:choose>
			</div>

		</div>
		<!-- /page-body -->
	</main>

	<!-- Delete confirm modal -->
	<div class="modal-overlay" id="deleteModal">
		<div class="modal-box">
			<h3 style="color: #ef4444">
				<i class="fas fa-trash"></i> Delete Record
			</h3>
			<p>Are you sure you want to delete this ${type} record? This
				action cannot be undone.</p>
			<form method="get" id="deleteForm" action="">
				<div class="modal-actions">
					<button type="button" class="btn btn-secondary"
						onclick="document.getElementById('deleteModal').classList.remove('open')">Cancel</button>
					<button type="submit" class="btn btn-danger">
						<i class="fas fa-trash"></i> Delete
					</button>
				</div>
			</form>
		</div>
	</div>

	<script>
function confirmDelete(id) {
    document.getElementById('deleteForm').action =
        '${pageContext.request.contextPath}/${type}/delete?id=' + id;
    document.getElementById('deleteModal').classList.add('open');
}
document.getElementById('deleteModal').addEventListener('click', function(e) {
    if (e.target === this) this.classList.remove('open');
});
setTimeout(() => {
    document.querySelectorAll('.alert').forEach(a => {
        a.style.transition = 'opacity .5s'; a.style.opacity = '0';
        setTimeout(() => a.remove(), 500);
    });
}, 3500);
</script>
</body>
</html>
