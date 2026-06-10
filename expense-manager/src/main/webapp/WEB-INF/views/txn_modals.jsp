<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- Load data if not already set (called from dashboard) --%>
<%
if (request.getAttribute("incomeCategories") == null) {
	try {
		com.expensemanager.dao.CategoryDAO cDao = new com.expensemanager.dao.CategoryDAO();
		com.expensemanager.dao.ColumnDefinitionDAO colDao = new com.expensemanager.dao.ColumnDefinitionDAO();
		com.expensemanager.dao.SubCategoryDAO scDao = new com.expensemanager.dao.SubCategoryDAO();
		request.setAttribute("incomeCategories", cDao.findByType("INCOME"));
		request.setAttribute("expenseCategories", cDao.findByType("EXPENSE"));
		request.setAttribute("incomeColumns", colDao.findByType("INCOME"));
		request.setAttribute("expenseColumns", colDao.findByType("EXPENSE"));
		request.setAttribute("subCategories", scDao.findAll());
	} catch (Exception ignored) {
	}
}
%>

<style>
/* Receipts */
.receipt-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
	gap: .75rem;
	margin-top: .75rem;
}

.receipt-card {
	background: #f8fafc;
	border: 1px solid var(--border);
	border-radius: 8px;
	overflow: hidden;
	position: relative;
}

.receipt-img {
	width: 100%;
	height: 100px;
	object-fit: cover;
	display: block;
	cursor: pointer;
}

.receipt-info {
	padding: .4rem .5rem;
	font-size: .72rem;
	color: var(--text-2);
}

.receipt-del {
	position: absolute;
	top: .3rem;
	right: .3rem;
	background: rgba(255, 255, 255, .9);
	border: none;
	border-radius: 50%;
	width: 22px;
	height: 22px;
	cursor: pointer;
	font-size: .75rem;
	display: flex;
	align-items: center;
	justify-content: center;
}

.receipt-del:hover {
	background: #fee2e2;
	color: var(--red);
}

.receipt-file {
	padding: .75rem;
	text-align: center;
	font-size: .75rem;
	color: var(--primary);
}
</style>

<!-- ══ ADD INCOME ══ -->
<div id="incomeModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3 style="color: var(--green)">+ Add Income</h3>
			<button class="modal-close" onclick="closeModal('incomeModal')">&#x2715;</button>
		</div>
		<form id="incomeForm"
			action="${pageContext.request.contextPath}/transactions"
			method="post" enctype="multipart/form-data"
			onsubmit="return prepareSubmit('incomeForm')">
			<input type="hidden" name="type" value="INCOME">
			<div class="form-grid">
				<div class="form-group">
					<label>Date &amp; Time *</label> <input type="datetime-local"
						name="dateTime" required>
				</div>
				<div class="form-group">
					<label>Amount (&#8377;) *</label> <input type="number"
						name="amount" min="0.01" step="0.01" placeholder="0.00" required
						autofocus>
				</div>
				<div class="form-group">
					<label>Category *</label> <select name="categoryid"
						id="incCategorySelect" required onchange="filterSubCat('inc')">
						<option value="">Select&#8230;</option>
						<c:forEach var="cat" items="${incomeCategories}">
							<option value="${cat.id}">${cat.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label>Sub Category</label> <select name="subcategory_id"
						id="incSubCatSelect" disabled>
						<option value="">Select&#8230;</option>
						<c:forEach var="sc" items="${subCategories}">
							<option value="${sc.id}" data-cat="${sc.category_id}">
								${sc.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group" style="grid-column: 1/-1">
					<label>Note</label> <input type="text" name="note"
						placeholder="Optional">
				</div>
			</div>

			<c:if test="${not empty incomeColumns}">
				<div
					style="margin-top: .75rem; border-top: 1px solid var(--border); padding-top: .75rem">
					<div class="card-title">Custom Fields</div>
					<div class="form-grid">
						<c:forEach var="col" items="${incomeColumns}">
							<div class="form-group">
								<label>${col.colName}</label> <input type="text"
									name="custom_${col.colKey}" placeholder="${col.colName}">
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
			<!-- Receipts -->
			<div class="card mt-2">
				<div class="flex mb-2">
					<span class="card-title" style="margin-bottom: 0">&#128248;
						Receipts &amp; Attachments</span> <span class="text-muted"
						style="font-size: .75rem; margin-left: .5rem">(max 5 MB
						each)</span>
				</div>


				<input type="file" id="receiptFile" name="receipt"
					accept="image/*,application/pdf" onchange="validateFileSize(this)"
					style="font-size: .82rem; flex: 1" required> <small
					id="fileError" style="color: red; display: none;"> File
					size should not exceed 5 MB. </small>
			</div>

			<div id="incExtras"></div>
			<div class="flex gap-1 mt-2">
				<!-- <button type="button" class="btn btn-outline btn-sm"
                onclick="addCustomField('incExtras')">+ Ad-hoc Field</button> -->
				<button type="submit" class="btn btn-success ml-auto">Save
					Income</button>
			</div>
		</form>
	</div>
</div>

<!-- ══ ADD EXPENSE ══ -->
<div id="expenseModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3 style="color: var(--red)">+ Add Expense</h3>
			<button class="modal-close" onclick="closeModal('expenseModal')">&#x2715;</button>
		</div>
		<form id="expenseForm"
			action="${pageContext.request.contextPath}/transactions"
			method="post" onsubmit="return prepareSubmit('expenseForm')">
			<input type="hidden" name="type" value="EXPENSE">
			<div class="form-grid">
				<div class="form-group">
					<label>Date &amp; Time *</label> <input type="datetime-local"
						name="dateTime" required>
				</div>
				<div class="form-group">
					<label>Amount (&#8377;) *</label> <input type="number"
						name="amount" min="0.01" step="0.01" placeholder="0.00" required>
				</div>
				<div class="form-group">
					<label>Category *</label> <select name="categoryId"
						id="expCategorySelect" required onchange="filterSubCat('exp')">
						<option value="">Select&#8230;</option>
						<c:forEach var="cat" items="${expenseCategories}">
							<option value="${cat.id}">${cat.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label>Sub Category</label> <select name="subcategoryId"
						id="expSubCatSelect" disabled>
						<option value="">Select&#8230;</option>
						<c:forEach var="sc" items="${subCategories}">
							<option value="${sc.id}" data-cat="${sc.category_id}">
								${sc.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group" style="grid-column: 1/-1">
					<label>Note</label> <input type="text" name="note"
						placeholder="Optional">
				</div>
			</div>

			<c:if test="${not empty expenseColumns}">
				<div
					style="margin-top: .75rem; border-top: 1px solid var(--border); padding-top: .75rem">
					<div class="card-title">Custom Fields</div>
					<div class="form-grid">
						<c:forEach var="col" items="${expenseColumns}">
							<div class="form-group">
								<label>${col.colName}</label> <input type="text"
									name="custom_${col.colKey}" placeholder="${col.colName}">
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>

			<div id="expExtras"></div>
			<div class="flex gap-1 mt-2">
				<!-- <button type="button" class="btn btn-outline btn-sm"
                onclick="addCustomField('expExtras')">+ Ad-hoc Field</button> -->
				<button type="submit" class="btn btn-danger ml-auto">Save
					Expense</button>
			</div>
		</form>
	</div>
</div>

<script>
	// ── Enter key → submit active form ──────────────────────
	document.addEventListener('keydown', function(e) {
		if (e.key !== 'Enter')
			return;
		// Don't intercept textarea or button
		if (e.target.tagName === 'TEXTAREA' || e.target.tagName === 'BUTTON')
			return;

		var openModal = document.querySelector('.modal-overlay.open');
		if (!openModal)
			return;

		var form = openModal.querySelector('form');
		if (form) {
			e.preventDefault();
			form.requestSubmit(); // triggers onsubmit + HTML5 validation
		}
	});

	// ── Sub-category filter ─────────────────────────────────
	function filterSubCat(prefix) {
		var catSel = document.getElementById(prefix + 'CategorySelect');
		var subSel = document.getElementById(prefix + 'SubCatSelect');
		var selCat = catSel.value;

		subSel.value = '';
		var opts = subSel.querySelectorAll('option[data-cat]');
		var has = false;
		opts.forEach(function(o) {
			var show = o.getAttribute('data-cat') === selCat;
			o.style.display = show ? '' : 'none';
			if (show)
				has = true;
		});
		subSel.disabled = !has;
	}

	// ── Gather ad-hoc custom fields before submit ───────────
	function prepareSubmit(formId) {
		var form = document.getElementById(formId);
		form.querySelectorAll('[name="_cfk"]').forEach(function(kEl, i) {
			var k = kEl.value.trim();
			if (!k)
				return;
			var vEl = form.querySelectorAll('[name="_cfv"]')[i];
			var inp = document.createElement('input');
			inp.type = 'hidden';
			inp.name = 'custom_' + k.toLowerCase().replace(/[^a-z0-9]+/g, '_');
			inp.value = vEl ? vEl.value : '';
			form.appendChild(inp);
		});
		return true;
	}

	// ── Auto-fill datetime on open ──────────────────────────
	document.addEventListener('DOMContentLoaded', function() {
		var now = new Date();
		var local = new Date(now - now.getTimezoneOffset() * 60000)
				.toISOString().slice(0, 16);
		document.querySelectorAll('input[type="datetime-local"]').forEach(
				function(el) {
					if (!el.value)
						el.value = local;
				});
	});

	const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

	function validateFileSize(input) {
		const error = document.getElementById("fileError");

		if (input.files.length > 0) {
			const file = input.files[0];

			if (file.size > MAX_FILE_SIZE) {
				error.style.display = "block";
				alert("Selected file exceeds 5 MB limit.");

				input.value = "";
				return false;
			}
		}

		error.style.display = "none";
		return true;
	}

	function validateForm() {

		const fileInput = document.getElementById("receiptFile");

		if (fileInput.files.length > 0) {

			if (fileInput.files[0].size > MAX_FILE_SIZE) {
				alert("File size should not exceed 5 MB.");
				return false;
			}
		}

		// Existing submit logic
		return prepareSubmit('incomeForm');
	}
</script>
