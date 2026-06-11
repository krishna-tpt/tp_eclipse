<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>ExpenseIQ – ${not empty transaction ? 'Edit' : 'Add'}
	${type == 'income' ? 'Income' : 'Expense'}</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link
	href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
.form-card {
	max-width: 760px;
}

.form-header {
	display: flex;
	align-items: center;
	gap: 14px;
	margin-bottom: 28px;
	padding-bottom: 18px;
	border-bottom: 1px solid var(--border);
}

.form-header-icon {
	width: 46px;
	height: 46px;
	border-radius: 13px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 1.3rem;
}

.icon-income {
	background: rgba(16, 185, 129, .15);
	color: #10b981;
}

.icon-expense {
	background: rgba(239, 68, 68, .15);
	color: #ef4444;
}

.form-header h2 {
	font-family: 'Syne', sans-serif;
	font-size: 1.25rem;
	font-weight: 800;
	margin: 0;
}

.form-header p {
	color: var(--text-muted);
	font-size: .82rem;
	margin: 2px 0 0;
}

.divider {
	height: 1px;
	background: var(--border);
	margin: 22px 0;
}

.custom-section h4 {
	font-family: 'Syne', sans-serif;
	font-size: .88rem;
	font-weight: 700;
	color: var(--text-secondary);
	margin-bottom: 16px;
	display: flex;
	align-items: center;
	gap: 8px;
}

.required {
	color: #f87171;
	margin-left: 2px;
}

.add-cat-link {
	font-size: .75rem;
	color: var(--accent);
	cursor: pointer;
	display: inline-flex;
	align-items: center;
	gap: 4px;
	margin-top: 5px;
}

.add-cat-link:hover {
	color: var(--accent-hover);
}

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
	max-width: 380px;
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
	font-size: 1.05rem;
	font-weight: 700;
	margin-bottom: 16px;
}

.modal-actions {
	display: flex;
	gap: 10px;
	justify-content: flex-end;
	margin-top: 18px;
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
			<div class="page-title">${not empty transaction ? 'Edit' : 'Add'}
				${type == 'income' ? 'Income' : 'Expense'}</div>
			<div class="topbar-actions">
				<a href="${pageContext.request.contextPath}/${type}/list"
					class="btn btn-secondary btn-sm"> <i class="fas fa-arrow-left"></i>
					Back
				</a>
			</div>
		</div>

		<c:if test="${not empty error}">
			<div class="alert alert-danger" style="margin: 14px 28px 0">
				<i class="fas fa-exclamation-circle"></i> ${error}
			</div>
		</c:if>

		<div class="page-body">
			<div class="form-card">

				<!-- Form Header -->
				<div class="form-header">
					<div
						class="form-header-icon ${type=='income' ? 'icon-income' : 'icon-expense'}">
						<i
							class="fas ${type=='income' ? 'fa-arrow-trend-up' : 'fa-arrow-trend-down'}"></i>
					</div>
					<div>
						<h2>${not empty transaction ? 'Edit' : 'New'}${type == 'income' ? 'Income' : 'Expense'}
							Record</h2>
						<p>${not empty transaction ? 'Update the details below' : 'Fill in the details to record a new ' += type}</p>
					</div>
				</div>

				<form method="post"
					action="${pageContext.request.contextPath}/${type}/${not empty transaction ? 'edit' : 'add'}">
					<c:if test="${not empty transaction}">
						<input type="hidden" name="id" value="${transaction.id}">
					</c:if>

					<!-- Core Fields -->
					<div class="form-grid">
						<div class="form-group">
							<label>Date <span class="required">*</span></label> <input
								type="date" name="transactionDate" class="form-control" required
								value="${not empty transaction ? transaction.transactionDate : ''}">
						</div>
						<div class="form-group">
							<label>Time <span class="required">*</span></label> <input
								type="time" name="transactionTime" class="form-control" required
								value="${not empty transaction ? transaction.transactionTime : ''}">
						</div>
						<div class="form-group">
							<label>Amount (₹) <span class="required">*</span></label> <input
								type="number" name="amount" class="form-control" required
								step="0.01" min="0.01" placeholder="0.00"
								value="${not empty transaction ? transaction.amount : ''}">
						</div>
						<div class="form-group">
							<label>Category <span class="required">*</span></label> <select
								name="category" class="form-control" required>
								<option value="">-- Select Category --</option>
								<c:forEach var="cat" items="${categories}">
									<option value="${cat}"
										${not empty transaction && transaction.category == cat ? 'selected' : ''}>${cat}</option>
								</c:forEach>
							</select> <span class="add-cat-link"
								onclick="document.getElementById('catModal').classList.add('open')">
								<i class="fas fa-plus"></i> Add new category
							</span>
						</div>
					</div>

					<div class="form-group">
						<label>Note</label>
						<textarea name="note" class="form-control" rows="3"
							placeholder="Optional description…">${not empty transaction ? transaction.note : ''}</textarea>
					</div>

					<!-- Custom Columns -->
					<c:if test="${not empty customColumns}">
						<div class="divider"></div>
						<div class="custom-section">
							<h4>
								<i class="fas fa-sliders" style="color: var(--accent)"></i>
								Custom Fields
							</h4>
							<div class="form-grid">
								<c:forEach var="col" items="${customColumns}">
									<div class="form-group">
										<label>${col.columnLabel}</label>
										<c:choose>
											<c:when test="${col.columnType == 'DATE'}">
												<input type="date" name="custom_${col.columnName}"
													class="form-control"
													value="${not empty transaction ? transaction.customValues[col.columnName] : ''}">
											</c:when>
											<c:when test="${col.columnType == 'NUMBER'}">
												<input type="number" name="custom_${col.columnName}"
													class="form-control" step="any" placeholder="0"
													value="${not empty transaction ? transaction.customValues[col.columnName] : ''}">
											</c:when>
											<c:otherwise>
												<input type="text" name="custom_${col.columnName}"
													class="form-control" placeholder="${col.columnLabel}"
													value="${not empty transaction ? transaction.customValues[col.columnName] : ''}">
											</c:otherwise>
										</c:choose>
									</div>
								</c:forEach>
							</div>
						</div>
					</c:if>

					<div class="divider"></div>
					<div style="display: flex; gap: 12px;">
						<button type="submit"
							class="btn ${type=='income' ? 'btn-success' : 'btn-danger'}">
							<i class="fas fa-save"></i> ${not empty transaction ? 'Update' : 'Save'}
							${type == 'income' ? 'Income' : 'Expense'}
						</button>
						<a href="${pageContext.request.contextPath}/${type}/list"
							class="btn btn-secondary"> <i class="fas fa-xmark"></i>
							Cancel
						</a>
					</div>
				</form>
			</div>
		</div>
	</main>

	<!-- Add Category Modal -->
	<div class="modal-overlay" id="catModal">
		<div class="modal-box">
			<h3>
				<i class="fas fa-tag" style="color: var(--accent)"></i> Add New
				Category
			</h3>
			<form method="post"
				action="${pageContext.request.contextPath}/${type}/add">
				<input type="hidden" name="action" value="addCategory">
				<div class="form-group">
					<label>Category Name</label> <input type="text" name="categoryName"
						class="form-control" placeholder="e.g. Groceries, Side Hustle…"
						required>
				</div>
				<div class="modal-actions">
					<button type="button" class="btn btn-secondary"
						onclick="document.getElementById('catModal').classList.remove('open')">Cancel</button>
					<button type="submit" class="btn btn-primary">
						<i class="fas fa-plus"></i> Add
					</button>
				</div>
			</form>
		</div>
	</div>

	<script>
// Set today's date and current time as defaults for new records
window.addEventListener('DOMContentLoaded', () => {
    const dateInput = document.querySelector('input[name="transactionDate"]');
    const timeInput = document.querySelector('input[name="transactionTime"]');
    if (dateInput && !dateInput.value) {
        dateInput.value = new Date().toISOString().split('T')[0];
    }
    if (timeInput && !timeInput.value) {
        const now = new Date();
        timeInput.value = String(now.getHours()).padStart(2,'0') + ':' + String(now.getMinutes()).padStart(2,'0');
    }
});
document.getElementById('catModal').addEventListener('click', function(e) {
    if (e.target === this) this.classList.remove('open');
});
</script>
</body>
</html>
