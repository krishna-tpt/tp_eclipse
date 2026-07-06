<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- Load data if not already set (called from dashboard) --%>
<%
if (request.getAttribute("incomeCategories") == null) {
	try {
		Integer bookId = (Integer) request.getSession().getAttribute("activeBookId");

		com.expensemanager.dao.CategoryDAO cDao = new com.expensemanager.dao.CategoryDAO();
		com.expensemanager.dao.ColumnDefinitionDAO colDao = new com.expensemanager.dao.ColumnDefinitionDAO();
		com.expensemanager.dao.SubCategoryDAO scDao = new com.expensemanager.dao.SubCategoryDAO();
		request.setAttribute("incomeCategories", cDao.findByType("INCOME"));
		request.setAttribute("expenseCategories", cDao.findByType("EXPENSE"));
		request.setAttribute("incomeCategories", cDao.findByType("INCOME", bookId));
		request.setAttribute("expenseCategories", cDao.findByType("EXPENSE", bookId));
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

.clock-modal-overlay {
	display: none;
	position: absolute;
	top: 100%;
	left: 0;
	z-index: 500;
	background: #fff;
	border: 1px solid var(--border);
	border-radius: 12px;
	padding: 1rem;
	box-shadow: 0 8px 24px rgba(0, 0, 0, .12);
	width: 260px;
}

.clock-modal-overlay.open {
	display: block;
}

.clock-trigger {
	display: flex;
	align-items: center;
	gap: .5rem;
	padding: .5rem .75rem;
	border: 1px solid var(--border);
	border-radius: 7px;
	cursor: pointer;
	font-size: .9rem;
	background: #fff;
	color: var(--text);
	width: 100%;
	margin-top: .3rem;
}

.clock-trigger:hover {
	border-color: var(--primary);
}

.clock-face {
	position: relative;
}

.ampm-row {
	display: flex;
	gap: 6px;
	justify-content: center;
	margin-bottom: 8px;
}

.ampm-btn {
	padding: 4px 16px;
	border-radius: 16px;
	border: 1px solid var(--border);
	background: #fff;
	cursor: pointer;
	font-size: .8rem;
	font-weight: 600;
	color: var(--text-2);
}

.ampm-btn.active {
	background: var(--primary);
	color: #fff;
	border-color: var(--primary);
}

.clock-mode-row {
	display: flex;
	gap: 6px;
	justify-content: center;
	margin-top: 8px;
}

.clock-mode-btn {
	padding: 3px 12px;
	border-radius: 6px;
	border: 1px solid var(--border);
	background: #fff;
	cursor: pointer;
	font-size: .75rem;
	color: var(--text-2);
}

.clock-mode-btn.active {
	background: #f1f5f9;
	color: var(--text);
}

.clock-time-display {
	text-align: center;
	font-size: 1.6rem;
	font-weight: 700;
	color: var(--primary);
	margin-bottom: 8px;
	letter-spacing: 2px;
}

.clock-done {
	width: 100%;
	margin-top: 10px;
	padding: .4rem;
	border: none;
	background: var(--primary);
	color: #fff;
	border-radius: 7px;
	cursor: pointer;
	font-size: .875rem;
	font-weight: 600;
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
			method="post" enctype="multipart/form-data">
			<!-- onsubmit="return prepareSubmit('incomeForm')"> -->
			<input type="hidden" name="type" id="txnTypeField" value="INCOME">
			<div class="form-grid">

				<!-- <div class="form-group">
					<label>Date &amp; Time *</label> <input type="datetime-local"
						name="dateTime" required>
				</div>  -->

				<div class="form-group" style="position: relative">
					<label>Date &amp; Time *</label> <input type="date" id="txnDate"
						required style="margin-bottom: .3rem">
					<button type="button" class="clock-trigger"
						onclick="openClockPicker()">
						&#128336; <span id="clockDisplayTrigger">Select time</span>
					</button>
					<input type="hidden" name="dateTime" id="txnDateTimeHidden">

					<div class="clock-modal-overlay" id="clockPickerModal">
						<div class="ampm-row">
							<button type="button" class="ampm-btn active" id="cpAM"
								onclick="cpSetAmPm('AM')">AM</button>
							<button type="button" class="ampm-btn" id="cpPM"
								onclick="cpSetAmPm('PM')">PM</button>
						</div>
						<div class="clock-time-display" id="cpDisplay">10:30</div>
						<div class="clock-mode-row">
							<button type="button" class="clock-mode-btn active" id="cpModeHr"
								onclick="cpSetMode('hour')">Hour</button>
							<button type="button" class="clock-mode-btn" id="cpModeMin"
								onclick="cpSetMode('minute')">Minute</button>
						</div>
						<svg id="cpSvg" viewBox="0 0 220 220"
							style="width: 100%; cursor: pointer; user-select: none"
							onmousedown="cpStartDrag(event)"
							ontouchstart="cpStartDrag(event)">
            <circle cx="110" cy="110" r="108" fill="#fff"
								stroke="#e2e8f0" stroke-width="1" />
            <g id="cpMarkers"></g>
            <line id="cpHandHour" x1="110" y1="110" x2="110" y2="58"
								stroke="#2563eb" stroke-width="4" stroke-linecap="round" />
            <line id="cpHandMinute" x1="110" y1="110" x2="110" y2="40"
								stroke="#94a3b8" stroke-width="2.5" stroke-linecap="round" />
            <circle cx="110" cy="110" r="5" fill="#2563eb" />
            <circle id="cpSelDot" cx="110" cy="58" r="10" fill="#2563eb"
								opacity="0.18" />
        </svg>
						<button type="button" class="clock-done" onclick="cpDone()">Done</button>
					</div>
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
			<!-- <div class="flex gap-1 mt-2">
				<button type="submit" id="txnSubmitBtn"
					class="btn btn-success ml-auto">Save Income</button>
			</div> -->
			<div class="flex gap-1 mt-2">
				<button type="button" id="txnContinueBtn" class="btn btn-outline"
					onclick="submitTxn('continue')">Continue</button>
				<button type="button" id="txnSubmitBtn"
					class="btn btn-success ml-auto" onclick="submitTxn('save')">Save
					Income</button>
			</div>
		</form>
	</div>
</div>

<!-- <script>
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
</script> -->

<script>
	// ── Tab switch ──────────────────────────────────────────
	function switchTxnTab(type) {
		var isIncome = (type === 'INCOME');
		document.getElementById('txnTypeField').value = type;

		document.getElementById('txnTabIncomeBtn').classList.toggle('active',
				isIncome);
		document.getElementById('txnTabExpenseBtn').classList.toggle('active',
				!isIncome);

		var title = document.getElementById('txnModalTitle');
		var submitBtn = document.getElementById('txnSubmitBtn');
		var continueBtn = document.getElementById('txnContinueBtn');

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

		document.querySelectorAll('#incCategorySelect option.cat-opt-INCOME')
	    .forEach(function(o) {
	        o.style.display = isIncome ? '' : 'none';
	        o.disabled = !isIncome;
	        o.hidden   = !isIncome;
	    });
	document.querySelectorAll('#incCategorySelect option.cat-opt-EXPENSE')
	    .forEach(function(o) {
	        o.style.display = isIncome ? 'none' : '';
	        o.disabled = isIncome;
	        o.hidden   = isIncome;
	    });
	document.getElementById('incCategorySelect').value = '';

		var subSel = document.getElementById('incSubCatSelect');
		subSel.value = '';
		subSel.disabled = true;
		subSel.querySelectorAll('option[data-cat]').forEach(function(o) {
			o.style.display = 'none';
		});

		var incCustom = document.getElementById('incomeCustomFields');
		var expCustom = document.getElementById('expenseCustomFields');
		if (incCustom)
			incCustom.style.display = isIncome ? '' : 'none';
		if (expCustom)
			expCustom.style.display = isIncome ? 'none' : '';
	}

	function openTxnModal(type) {
		switchTxnTab(type || 'INCOME');
		openModal('incomeModal');
	}

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
        o.disabled = !show;   // ← add
        o.hidden   = !show;   // ← add (extra safety)
        if (show) has = true;
    });
    subSel.disabled = !has;
}

	// ── Gather custom_ fields before submit ─────────────────
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
	}

	// ── Core save via fetch (no redirect) ───────────────────
	function submitTxn(mode) {
    var form = document.getElementById('incomeForm');

    var amount = form.querySelector('[name="amount"]').value;
    var catId  = form.querySelector('[name="categoryid"]').value;
    if (!amount || !catId) {
        showTxnMsg('Amount and Category are required.', 'error');
        return;
    }

    prepareSubmit('incomeForm');

    var snapshot = {
        type      : document.getElementById('txnTypeField').value,
        categoryid: form.querySelector('[name="categoryid"]').value,
        subcatId  : form.querySelector('[name="subcategoryId"]')
                        ? form.querySelector('[name="subcategoryId"]').value : '',
        dateTime  : form.querySelector('[name="dateTime"]').value
    };

    var formData = new FormData(form);

    fetch(form.action, { method: 'POST', body: formData })
        .then(function(resp) {
            if (!resp.ok) throw new Error('Server error ' + resp.status);

            showTxnMsg('Transaction saved!', 'success');

            refreshBackgroundData();

            if (mode === 'continue') {
                form.reset();

                // Restore the date & time (both the hidden combined field
                // AND the visible date input, which form.reset() clears
                // back to its empty "dd/mm/yyyy" placeholder state)
                form.querySelector('[name="dateTime"]').value = snapshot.dateTime;
                if (snapshot.dateTime) {
                    document.getElementById('txnDate').value = snapshot.dateTime.split('T')[0];
                }

                // Reset subcategory dropdown
                var subSel = document.getElementById('incSubCatSelect');
                subSel.value = '';
                subSel.disabled = true;
                subSel.querySelectorAll('option[data-cat]')
                    .forEach(function(o) { o.style.display = 'none'; });

                // Clear injected hidden fields
                form.querySelectorAll('input[type="hidden"][name^="custom_"]')
                    .forEach(function(el) { el.remove(); });

                form.querySelector('[name="amount"]').focus();

            } else {
                form.reset();

                var now = new Date();
                var local = new Date(now - now.getTimezoneOffset() * 60000)
                                .toISOString().slice(0, 16);
                form.querySelector('[name="dateTime"]').value = local;

                // form.reset() also blanks the visible date input back to
                // its "dd/mm/yyyy" placeholder — restore it to today
                document.getElementById('txnDate').value = local.split('T')[0];

                // Reset the clock-picker trigger label + internal state to "now"
                var h24 = now.getHours();
                cpAmPm = h24 >= 12 ? 'PM' : 'AM';
                cpHour = h24 % 12 || 12;
                cpMin  = Math.round(now.getMinutes() / 5) * 5;
                if (cpMin === 60) cpMin = 55;
                document.getElementById('cpAM').classList.toggle('active', cpAmPm === 'AM');
                document.getElementById('cpPM').classList.toggle('active', cpAmPm === 'PM');
                var hh = cpHour < 10 ? '0' + cpHour : cpHour;
                var mm = cpMin  < 10 ? '0' + cpMin  : cpMin;
                document.getElementById('clockDisplayTrigger').textContent = hh + ':' + mm + ' ' + cpAmPm;

                var subSel = document.getElementById('incSubCatSelect');
                subSel.value = '';
                subSel.disabled = true;
                subSel.querySelectorAll('option[data-cat]')
                    .forEach(function(o) { o.style.display = 'none'; });

                form.querySelectorAll('input[type="hidden"][name^="custom_"]')
                    .forEach(function(el) { el.remove(); });
            }
        })
        .catch(function(err) {
            showTxnMsg('Save failed: ' + err.message, 'error');
        });
}

	// ── Refresh dashboard/list data behind the modal ────────
	// Re-fetches the current page in the background and swaps in the
	// updated stats cards + transaction table, without closing the
	// modal or doing a disruptive full page reload.
	function refreshBackgroundData() {
		fetch(window.location.href, { credentials: 'same-origin' })
			.then(function(resp) { return resp.text(); })
			.then(function(html) {
				var doc = new DOMParser().parseFromString(html, 'text/html');
				['.stats-grid', '.table-wrap', '.pagination'].forEach(function(sel) {
					var oldEl = document.querySelector(sel);
					var newEl = doc.querySelector(sel);
					if (oldEl && newEl) {
						oldEl.outerHTML = newEl.outerHTML;
					} else if (oldEl && !newEl) {
						// e.g. pagination disappeared because only 1 page now
						oldEl.remove();
					}
				});
			})
			.catch(function(err) {
				console.error('Background refresh failed:', err);
			});
	}

	// ── Inline status message inside modal ──────────────────
	function showTxnMsg(msg, type) {
		var form = document.getElementById('incomeForm');
		var msgDiv = form.querySelector('.txn-inline-msg');
		if (!msgDiv) {
			msgDiv = document.createElement('div');
			msgDiv.className = 'txn-inline-msg';
			form.prepend(msgDiv);
		}
		msgDiv.textContent = msg;
		var bg = (type === 'success') ? '#d1fae5' : '#fee2e2';
		var color = (type === 'success') ? '#065f46' : '#991b1b';
		msgDiv.style.cssText = 'padding:.5rem .75rem;border-radius:6px;'
				+ 'margin-bottom:.75rem;font-size:.85rem;' + 'background:' + bg
				+ ';color:' + color + ';';
		setTimeout(function() {
			msgDiv.remove();
		}, 3000);
	}

	// ── Enter key → submit ───────────────────────────────────
	document.addEventListener('keydown', function(e) {
    if (e.key !== 'Enter') return;
    if (e.target.tagName === 'TEXTAREA' || e.target.tagName === 'BUTTON') return;
    var openModalEl = document.querySelector('.modal-overlay.open');
    if (!openModalEl) return;
    e.preventDefault();
    submitTxn(e.shiftKey ? 'continue' : 'save');
});

	// ── Auto-fill datetime on load ───────────────────────────
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

	// ── File size validation ─────────────────────────────────
	const MAX_FILE_SIZE = 5 * 1024 * 1024;
	function validateFileSize(input) {
		var error = document.getElementById('fileError');
		if (input.files.length > 0 && input.files[0].size > MAX_FILE_SIZE) {
			error.style.display = 'block';
			alert('Selected file exceeds 5 MB limit.');
			input.value = '';
			return false;
		}
		error.style.display = 'none';
		return true;
	}
	var cpHour=10, cpMin=30, cpAmPm='AM', cpMode='hour', cpDragging=false;

	function openClockPicker(){
	    document.getElementById('clockPickerModal').classList.toggle('open');
	    cpBuildMarkers(); cpUpdateHands();
	}

	function cpSetAmPm(v){
	    cpAmPm=v;
	    document.getElementById('cpAM').classList.toggle('active',v==='AM');
	    document.getElementById('cpPM').classList.toggle('active',v==='PM');
	    cpBuildDateTime();
	}

	function cpSetMode(m){
	    cpMode=m;
	    document.getElementById('cpModeHr').classList.toggle('active',m==='hour');
	    document.getElementById('cpModeMin').classList.toggle('active',m==='minute');
	    document.getElementById('cpHandHour').setAttribute('stroke',m==='hour'?'#2563eb':'#94a3b8');
	    document.getElementById('cpHandMinute').setAttribute('stroke',m==='minute'?'#2563eb':'#94a3b8');
	    cpBuildMarkers(); cpUpdateHands();
	}

	function cpBuildMarkers(){
	    var g=document.getElementById('cpMarkers');
	    g.innerHTML='';
	    var cx=110,cy=110,r=90;
	    for(var i=0;i<12;i++){
	        var ang=(i/12)*Math.PI*2-Math.PI/2;
	        var x=cx+Math.cos(ang)*r, y=cy+Math.sin(ang)*r;
	        var lbl=cpMode==='hour'?(i===0?12:i):(i*5<10?'0'+i*5:i*5);
	        if(cpMode==='minute'&&i%3!==0&&i!==0){
	            var dot=document.createElementNS('http://www.w3.org/2000/svg','circle');
	            dot.setAttribute('cx',x);dot.setAttribute('cy',y);
	            dot.setAttribute('r',2);dot.setAttribute('fill','#cbd5e1');
	            g.appendChild(dot);
	        } else {
	            var t=document.createElementNS('http://www.w3.org/2000/svg','text');
	            t.setAttribute('x',x);t.setAttribute('y',y);
	            t.setAttribute('text-anchor','middle');
	            t.setAttribute('dominant-baseline','central');
	            t.setAttribute('font-size','13');
	            t.setAttribute('font-weight','500');
	            t.setAttribute('fill','#64748b');
	            t.textContent=lbl;
	            g.appendChild(t);
	        }
	    }
	}

	function cpAngleFromEvent(e){
	    var svg=document.getElementById('cpSvg');
	    var rect=svg.getBoundingClientRect();
	    var cx=rect.left+rect.width/2, cy=rect.top+rect.height/2;
	    var ex,ey;
	    if(e.touches){ex=e.touches[0].clientX;ey=e.touches[0].clientY;}
	    else{ex=e.clientX;ey=e.clientY;}
	    return Math.atan2(ey-cy,ex-cx);
	}

	function cpApplyAngle(ang){
	    var raw=((ang+Math.PI/2)/(Math.PI*2)+1)%1;
	    if(cpMode==='hour'){cpHour=Math.round(raw*12)%12||12;}
	    else{cpMin=Math.round(raw*60)%60;}
	    cpUpdateHands();
	}

	function cpUpdateHands(){
	    var cx=110,cy=110;
	    var hAng=((cpHour%12)/12)*Math.PI*2+(cpMin/60)*(Math.PI*2/12)-Math.PI/2;
	    var hR=50;
	    document.getElementById('cpHandHour').setAttribute('x2',cx+Math.cos(hAng)*hR);
	    document.getElementById('cpHandHour').setAttribute('y2',cy+Math.sin(hAng)*hR);
	    var mAng=(cpMin/60)*Math.PI*2-Math.PI/2;
	    var mR=68;
	    document.getElementById('cpHandMinute').setAttribute('x2',cx+Math.cos(mAng)*mR);
	    document.getElementById('cpHandMinute').setAttribute('y2',cy+Math.sin(mAng)*mR);
	    var sa=cpMode==='hour'?hAng:mAng, sr=cpMode==='hour'?hR:mR;
	    document.getElementById('cpSelDot').setAttribute('cx',cx+Math.cos(sa)*sr);
	    document.getElementById('cpSelDot').setAttribute('cy',cy+Math.sin(sa)*sr);
	    var h=cpHour<10?'0'+cpHour:cpHour;
	    var m=cpMin<10?'0'+cpMin:cpMin;
	    document.getElementById('cpDisplay').textContent=h+':'+m;
	}

	function cpStartDrag(e){
	    cpDragging=true; cpApplyAngle(cpAngleFromEvent(e)); e.preventDefault();
	}

	document.addEventListener('mousemove',function(e){if(cpDragging)cpApplyAngle(cpAngleFromEvent(e));});
	document.addEventListener('touchmove',function(e){if(cpDragging)cpApplyAngle(cpAngleFromEvent(e));},{passive:false});
	document.addEventListener('mouseup',function(){
	    if(cpDragging&&cpMode==='hour'){cpDragging=false;cpSetMode('minute');}
	    else cpDragging=false;
	});
	document.addEventListener('touchend',function(){cpDragging=false;});

	function cpBuildDateTime(){
	    var dateVal=document.getElementById('txnDate').value;
	    if(!dateVal) return;
	    var h=cpHour;
	    if(cpAmPm==='AM'){if(h===12)h=0;}
	    else{if(h!==12)h+=12;}
	    var hh=h<10?'0'+h:''+h;
	    var mm=cpMin<10?'0'+cpMin:''+cpMin;
	    document.getElementById('txnDateTimeHidden').value=dateVal+'T'+hh+':'+mm;
	}

	function cpDone(){
	    cpBuildDateTime();
	    var h=cpHour<10?'0'+cpHour:cpHour;
	    var m=cpMin<10?'0'+cpMin:cpMin;
	    document.getElementById('clockDisplayTrigger').textContent=h+':'+m+' '+cpAmPm;
	    document.getElementById('clockPickerModal').classList.remove('open');
	}

	// Auto-fill on page load
	document.addEventListener('DOMContentLoaded',function(){
	    var now=new Date();
	    var yyyy=now.getFullYear(),mo=now.getMonth()+1,dd=now.getDate();
	    document.getElementById('txnDate').value=yyyy+'-'+(mo<10?'0'+mo:mo)+'-'+(dd<10?'0'+dd:dd);
	    var h24=now.getHours();
	    cpAmPm=h24>=12?'PM':'AM';
	    cpHour=h24%12||12;
	    cpMin=Math.round(now.getMinutes()/5)*5;
	    if(cpMin===60){cpMin=55;}
	    document.getElementById('cpAM').classList.toggle('active',cpAmPm==='AM');
	    document.getElementById('cpPM').classList.toggle('active',cpAmPm==='PM');
	    cpBuildDateTime();
	    var h=cpHour<10?'0'+cpHour:cpHour;
	    var m=cpMin<10?'0'+cpMin:cpMin;
	    document.getElementById('clockDisplayTrigger').textContent=h+':'+m+' '+cpAmPm;
	});

	// Close picker on outside click
	document.addEventListener('click',function(e){
	    var modal=document.getElementById('clockPickerModal');
	    var trigger=document.querySelector('.clock-trigger');
	    if(modal&&!modal.contains(e.target)&&trigger&&!trigger.contains(e.target)){
	        modal.classList.remove('open');
	    }
	});	

</script>