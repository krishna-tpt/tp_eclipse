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

<c:if test="${not empty param.msg}">
	<div class="alert alert-success">&#10003; ${param.msg == 'saved' ? 'Transaction Saved!' : 'not saved'}</div>
</c:if>

<c:if test="${not empty dbError}">
	<div class="alert alert-error">&#10007; ${dbError}</div>
</c:if>

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

/* Txn type tabs inside shared modal */
.txn-tab-row {
	display: flex;
	gap: .5rem;
	margin-bottom: 1.1rem;
}

.txn-tab-btn {
	flex: 1;
	padding: .6rem 1rem;
	border-radius: 8px;
	border: 1.5px solid var(--border);
	background: #fff;
	font-family: inherit;
	font-size: .875rem;
	font-weight: 600;
	cursor: pointer;
	transition: all .15s;
	color: var(--text-2);
}

.txn-tab-btn.income.active {
	background: #dcfce7;
	border-color: var(--green);
	color: #15803d;
}

.txn-tab-btn.expense.active {
	background: #fee2e2;
	border-color: var(--red);
	color: #b91c1c;
}
</style>

<!-- ══ SHARED INCOME / EXPENSE MODAL (tab switch) ══ -->
<div id="incomeModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3 id="txnModalTitle" style="color: var(--green)">+ Add
				Transaction</h3>
			<button class="modal-close" onclick="closeModal('incomeModal')">&#x2715;</button>
		</div>

		<%-- Tab switch: Income / Expense --%>
		<div class="txn-tab-row">
			<button type="button" id="txnTabIncomeBtn"
				class="txn-tab-btn income active" onclick="switchTxnTab('INCOME')">&#43;
				Income</button>
			<button type="button" id="txnTabExpenseBtn"
				class="txn-tab-btn expense" onclick="switchTxnTab('EXPENSE')">&#43;
				Expense</button>
		</div>

		<form id="incomeForm"
			action="${pageContext.request.contextPath}/transactions"
			method="post" enctype="multipart/form-data"
			onsubmit="return prepareSubmit('incomeForm')">
			<input type="hidden" name="type" id="txnTypeField" value="INCOME">
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
							<option value="${cat.id}" class="cat-opt-INCOME">${cat.name}</option>
						</c:forEach>
						<c:forEach var="cat" items="${expenseCategories}">
							<option value="${cat.id}" class="cat-opt-EXPENSE"
								style="display: none">${cat.name}</option>
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

			<%-- Income custom fields --%>
			<c:if test="${not empty incomeColumns}">
				<div id="incomeCustomFields"
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

			<%-- Expense custom fields (hidden by default) --%>
			<c:if test="${not empty expenseColumns}">
				<div id="expenseCustomFields"
					style="display: none; margin-top: .75rem; border-top: 1px solid var(--border); padding-top: .75rem">
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
					style="font-size: .82rem; flex: 1"> <small id="fileError"
					style="color: red; display: none;"> File size should not
					exceed 5 MB. </small>
			</div>

			<div id="incExtras"></div>
			<div class="flex gap-1 mt-2">
				<button type="submit" id="txnSubmitBtn"
					class="btn btn-success ml-auto">Save Income</button>
			</div>
		</form>
	</div>
</div>

<script>
	// ── Tab switch: Income <-> Expense (shared modal) ───────
	function switchTxnTab(type) {
		var isIncome = (type === 'INCOME');

		document.getElementById('txnTypeField').value = type;

		// Tab buttons
		document.getElementById('txnTabIncomeBtn').classList.toggle('active', isIncome);
		document.getElementById('txnTabExpenseBtn').classList.toggle('active', !isIncome);

		// Title + submit button styling
		var title = document.getElementById('txnModalTitle');
		var submitBtn = document.getElementById('txnSubmitBtn');
		if (isIncome) {
			title.textContent = '+ Add Income';
			title.style.color = 'var(--green)';
			submitBtn.textContent = 'Save Income';
			submitBtn.classList.remove('btn-danger');
			submitBtn.classList.add('btn-success');
		} else {
			title.textContent = '+ Add Expense';
			title.style.color = 'var(--red)';
			submitBtn.textContent = 'Save Expense';
			submitBtn.classList.remove('btn-success');
			submitBtn.classList.add('btn-danger');
		}

		// Category dropdown — show only matching type options
		document.querySelectorAll('#incCategorySelect option.cat-opt-INCOME')
			.forEach(function(o) { o.style.display = isIncome ? '' : 'none'; });
		document.querySelectorAll('#incCategorySelect option.cat-opt-EXPENSE')
			.forEach(function(o) { o.style.display = isIncome ? 'none' : ''; });
		document.getElementById('incCategorySelect').value = '';

		// Reset sub-category
		var subSel = document.getElementById('incSubCatSelect');
		subSel.value = '';
		subSel.disabled = true;
		subSel.querySelectorAll('option[data-cat]').forEach(function(o) { o.style.display = 'none'; });

		// Custom fields — show only matching type's block
		var incCustom = document.getElementById('incomeCustomFields');
		var expCustom = document.getElementById('expenseCustomFields');
		if (incCustom) incCustom.style.display = isIncome ? '' : 'none';
		if (expCustom) expCustom.style.display = isIncome ? 'none' : '';
	}

	// Open modal in a specific tab — call this from the Income/Expense buttons
	function openTxnModal(type) {
		switchTxnTab(type);
		openModal('incomeModal');
	}

	// ── Enter key → submit active form ──────────────────────
	document.addEventListener('keydown', function(e) {
		if (e.key !== 'Enter')
			return;
		if (e.target.tagName === 'TEXTAREA' || e.target.tagName === 'BUTTON')
			return;

		var openModalEl = document.querySelector('.modal-overlay.open');
		if (!openModalEl)
			return;

		var form = openModalEl.querySelector('form');
		if (form) {
			e.preventDefault();
			form.requestSubmit();
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

	async function submitForm(e, formId) {
		e.preventDefault();
		prepareSubmit(formId);

		const form = document.getElementById(formId);
		const formData = new FormData(form);

		try {
			const resp = await fetch(form.action, {
				method : 'POST',
				body : formData
			});

			showInlineMsg(formId, '✓ Transaction Saved!', 'success');
			form.reset();

			const now = new Date();
			const local = new Date(now - now.getTimezoneOffset() * 60000)
					.toISOString().slice(0, 16);
			form.querySelector('input[type="datetime-local"]').value = local;

			loadSummary();

		} catch (err) {
			showInlineMsg(formId, '✗ Save failed: ' + err.message, 'error');
		}
		return false;
	}

	function showInlineMsg(formId, msg, type) {
		const form = document.getElementById(formId);
		let msgDiv = form.querySelector('.inline-msg');
		if (!msgDiv) {
			msgDiv = document.createElement('div');
			msgDiv.className = 'inline-msg';
			form.prepend(msgDiv);
		}
		msgDiv.textContent = msg;

		// plain if/else for colors
		const bg = (type === 'success') ? '#d1fae5' : '#fee2e2';
		const color = (type === 'success') ? '#065f46' : '#991b1b';

		msgDiv.style.cssText = 'padding:.5rem .75rem;border-radius:6px;'
				+ 'margin-bottom:.75rem;font-size:.85rem;'
				+ 'background:' + bg + ';color:' + color + ';';

		setTimeout(() => msgDiv.remove(), 3000);
	}
</script>