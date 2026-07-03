<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="pageTitle" value="Transaction Detail" scope="request" />
<c:set var="activePage" value="txn" scope="request" />
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>"
	scope="request" />
<%@ include file="header.jsp"%>

<style>
/* Audit timeline */
.timeline {
	position: relative;
	padding-left: 2rem;
}

.timeline::before {
	content: '';
	position: absolute;
	left: .6rem;
	top: 0;
	bottom: 0;
	width: 2px;
	background: var(--border);
}

.tl-item {
	position: relative;
	margin-bottom: 1.1rem;
}

.tl-item::before {
	content: '';
	position: absolute;
	left: -1.55rem;
	top: .35rem;
	width: 12px;
	height: 12px;
	border-radius: 50%;
	border: 2px solid #fff;
}

.tl-item.create::before {
	background: var(--green);
	box-shadow: 0 0 0 2px var(--green);
}

.tl-item.update::before {
	background: var(--primary);
	box-shadow: 0 0 0 2px var(--primary);
}

.tl-item.delete::before {
	background: var(--red);
	box-shadow: 0 0 0 2px var(--red);
}

.tl-time {
	font-size: .7rem;
	color: var(--text-2);
	margin-bottom: .2rem;
}

.tl-card {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: 8px;
	padding: .65rem .9rem;
	box-shadow: 0 1px 2px rgba(0, 0, 0, .04);
}

.tl-action {
	display: inline-flex;
	padding: .1rem .5rem;
	border-radius: 20px;
	font-size: .65rem;
	font-weight: 700;
	text-transform: uppercase;
	margin-bottom: .3rem;
}

.act-create {
	background: #dcfce7;
	color: #15803d;
}

.act-update {
	background: #dbeafe;
	color: #1d4ed8;
}

.act-delete {
	background: #fee2e2;
	color: #b91c1c;
}

.diff-row {
	display: flex;
	align-items: center;
	gap: .4rem;
	flex-wrap: wrap;
	font-size: .8rem;
	margin-top: .2rem;
}

.diff-field {
	font-weight: 600;
	color: var(--text-2);
	min-width: 80px;
}

.diff-old {
	color: #b91c1c;
	text-decoration: line-through;
	background: #fee2e2;
	padding: .08rem .35rem;
	border-radius: 4px;
}

.diff-new {
	color: #15803d;
	background: #dcfce7;
	padding: .08rem .35rem;
	border-radius: 4px;
}

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

/* ── Prev / Next detail navigation ── */
.txn-nav-btn {
	display: inline-flex;
	align-items: center;
	gap: .3rem;
	padding: .5rem 1rem;
	border-radius: 20px;
	border: 1px solid var(--border);
	background: #fff;
	color: var(--text-1);
	font-size: .85rem;
	font-weight: 600;
	text-decoration: none;
	transition: background .12s, border-color .12s;
}

.txn-nav-btn:hover {
	background: #f0f7ff;
	border-color: var(--primary);
}

.txn-nav-btn.disabled {
	opacity: .4;
	pointer-events: none;
	cursor: default;
}

/* ── iOS-style Copy Modal — theme colors ── */
.ios-overlay {
	position: fixed;
	inset: 0;
	background: rgba(0, 0, 0, .45);
	z-index: 1000;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 1rem;
	animation: overlayIn .2s ease;
}

@
keyframes overlayIn {from { opacity:0
	
}

to {
	opacity: 1
}

}
.ios-sheet {
	background: #fff;
	border-radius: 16px;
	width: 100%;
	max-width: 400px;
	overflow: hidden;
	animation: sheetUp .25s cubic-bezier(.32, 1, .23, 1);
	box-shadow: 0 20px 60px rgba(0, 0, 0, .25);
}

@
keyframes sheetUp {from { opacity:0;
	transform: scale(.95)
}

to {
	opacity: 1;
	transform: scale(1)
}

}
.ios-sheet-title {
	font-size: 1rem;
	font-weight: 700;
	text-align: center;
	padding: 1.1rem 1rem .75rem;
	border-bottom: 1px solid #e5e7eb;
	color: #111;
}

.ios-option {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: .95rem 1.25rem;
	border-bottom: 1px solid #f1f5f9;
	cursor: pointer;
	transition: background .12s;
	font-size: 1rem;
	color: var(--text);
}

.ios-option:hover {
	background: #eff6ff;
}

.ios-option.selected {
	color: var(--primary);
	font-weight: 600;
}

.ios-option .check {
	color: var(--primary);
	font-size: 1.1rem;
}

.ios-ok {
	width: 100%;
	padding: 1rem;
	background: #fff;
	border: none;
	border-top: 1px solid #e5e7eb;
	font-size: 1rem;
	font-weight: 600;
	color: var(--primary);
	cursor: pointer;
	letter-spacing: .02em;
	transition: background .12s;
}

.ios-ok:hover {
	background: #eff6ff;
}

/* ── Copy Preview Card — theme colors ── */
#copyPreview {
	border: 2px solid #bfdbfe;
	border-radius: 12px;
	background: #f0f7ff;
	padding: 1rem 1.1rem;
	margin-top: .85rem;
	animation: fadeIn .2s ease;
}

@
keyframes fadeIn {from { opacity:0;
	transform: translateY(4px)
}

to {
	opacity: 1;
	transform: translateY(0)
}

}
.preview-row {
	display: flex;
	gap: .5rem;
	font-size: .83rem;
	padding: .2rem 0;
}

.preview-label {
	color: var(--text-2);
	font-weight: 600;
	min-width: 80px;
}

.preview-val {
	color: var(--text-1);
}

.preview-changed {
	color: var(--primary);
	font-weight: 700;
}

#moveBookRow select {
	margin-top: .3rem;
}
</style>

<div class="page-header flex">
	<div>
		<a href="${pageContext.request.contextPath}/home"
			style="display: inline-block; padding: 10px 18px; background: linear-gradient(135deg, #43B6F0, #7B7EF0, #D91EF0); color: white; text-decoration: none; border-radius: 25px; font-size: .9rem; font-weight: 600; box-shadow: 0 4px 10px rgba(0, 0, 0, .15);">
			&#8592; Back </a>
		<h1 style="margin-top: 1rem">Transaction Detail</h1>
	</div>
	<%-- ═══ Prev / Next — step through records without going back
	     to the list. Same order the list uses (most recent first),
	     scoped to the current cash book. ═══ --%>
	<div class="flex gap-1 ml-auto"
		style="align-items: flex-start; margin-top: .15rem">
		<c:choose>
			<c:when test="${not empty prevTxnId}">
				<a id="navPrevBtn"
					href="${pageContext.request.contextPath}/transaction?id=${prevTxnId}"
					class="txn-nav-btn" title="Newer transaction">&#8249; Prev</a>
			</c:when>
			<c:otherwise>
				<span class="txn-nav-btn disabled">&#8249; Prev</span>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${not empty nextTxnId}">
				<a id="navNextBtn"
					href="${pageContext.request.contextPath}/transaction?id=${nextTxnId}"
					class="txn-nav-btn" title="Older transaction">Next &#8250;</a>
			</c:when>
			<c:otherwise>
				<span class="txn-nav-btn disabled">Next &#8250;</span>
			</c:otherwise>
		</c:choose>
	</div>
</div>

<c:if test="${not empty param.success}">
	<div class="alert alert-success">
		&#10003;
		<c:choose>
			<c:when test="${param.success=='updated'}">Transaction updated!</c:when>
			<c:when test="${param.success=='duplicated'}">&#128260; Transaction duplicated &amp; saved!</c:when>
			<c:when test="${param.success=='receipt_uploaded'}">Receipt uploaded!</c:when>
			<c:when test="${param.success=='receipt_deleted'}">Receipt removed.</c:when>
		</c:choose>
	</div>
</c:if>
<c:if test="${not empty param.error}">
	<div class="alert alert-error">&#10007; ${param.error}</div>
</c:if>

<c:if test="${not empty txn}">
	<div
		style="display: grid; grid-template-columns: 1fr 1.2fr; gap: 1.5rem; align-items: start">

		<!-- LEFT -->
		<div>
			<!-- Edit form -->
			<div class="card">
				<div class="flex mb-2">
					<span class="card-title" style="margin-bottom: 0">Edit
						Transaction</span>
					<c:choose>
						<c:when test="${txn.type=='INCOME'}">
							<span class="badge income ml-auto">INCOME</span>
						</c:when>
						<c:otherwise>
							<span class="badge expense ml-auto">EXPENSE</span>
						</c:otherwise>
					</c:choose>
				</div>
				<form id="editForm"
					action="${pageContext.request.contextPath}/transaction"
					method="post">
					<input type="hidden" name="action" value="update"> <input
						type="hidden" name="id" value="${txn.id}">
					<div class="form-group mb-2">
						<label>Date &amp; Time *</label> <input type="datetime-local"
							name="dateTime" id="dtInput" required>
					</div>
					<div class="form-group mb-2">
						<label>Amount (&#8377;) *</label> <input type="number"
							name="amount" step="0.01" min="0.01" value="${txn.amount}"
							required>
					</div>
					<div class="form-group mb-2">
						<label>Category *</label> <select name="categoryId" id="editCat"
							required onchange="filterEditSub()">
							<option value="">Select&#8230;</option>
							<c:choose>
								<c:when test="${txn.type=='INCOME'}">
									<c:forEach var="cat" items="${incomeCategories}">
										<option value="${cat.id}"
											${cat.id==txn.categoryId?'selected':''}>${cat.name}</option>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<c:forEach var="cat" items="${expenseCategories}">
										<option value="${cat.id}"
											${cat.id==txn.categoryId?'selected':''}>${cat.name}</option>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</select>
					</div>
					<div class="form-group mb-2">
						<label>Sub Category</label> <select name="subcategoryId"
							id="editSub">
							<option value="">None</option>
							<c:forEach var="sc" items="${subCategories}">
								<option value="${sc.id}" data-cat="${sc.category_id}"
									${sc.id==txn.subcategoryid?'selected':''}>${sc.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group mb-2">
						<label>Note</label> <input type="text" name="note"
							value="${txn.note}">
					</div>

					<div class="flex gap-1 mt-2" style="flex-wrap: wrap">
						<button type="submit" class="btn btn-primary">&#10003;
							Save</button>
						<button type="button" class="btn btn-outline"
							onclick="openCopyModal()">&#128203; Copy</button>
						<button type="button" class="btn btn-outline"
							onclick="toggleMoveBook()">&#128230; Move</button>
						<button type="button" class="btn btn-danger ml-auto"
							onclick="if(confirm('Delete this transaction permanently?'))document.getElementById('delForm').submit()">
							&#x1F5D1; Delete</button>
					</div>

					<%-- ── Move to another Cash Book (toggle) ── --%>
					<div id="moveBookRow"
						style="display: none; margin-top: .85rem; padding-top: .85rem; border-top: 1px solid var(--border)">
						<label style="font-size: .72rem">Move to Cash Book</label> <select
							name="newbookid" id="moveBookSelect" form="editForm">
							<c:forEach var="bk" items="${cashbooks}">
								<option value="${bk.id}" ${bk.id==txn.bookId?'selected':''}>${bk.name}</option>
							</c:forEach>
						</select>
						<div style="font-size: .72rem; color: #94a3b8; margin-top: .4rem">
							&#8505; Select the target book, then click <strong>Save</strong>
							above to move this transaction.
						</div>
					</div>
				</form>

				<form id="delForm"
					action="${pageContext.request.contextPath}/transaction"
					method="post" style="display: none">
					<input type="hidden" name="action" value="delete"> <input
						type="hidden" name="id" value="${txn.id}">
				</form>

				<!-- ── Copy Preview Card (shown after option selected) ── -->
				<div id="copyPreview" style="display: none">
					<div
						style="font-size: .82rem; font-weight: 700; color: var(--primary); margin-bottom: .6rem">
						&#128203; Copy Preview — edit if needed, then save</div>

					<form method="post"
						action="${pageContext.request.contextPath}/transaction"
						id="dupForm">
						<input type="hidden" name="action" value="duplicate"> <input
							type="hidden" name="id" value="${txn.id}">

						<div class="form-group mb-2">
							<label style="font-size: .72rem">Date &amp; Time</label> <input
								type="datetime-local" name="dupDateTime" id="dupDateTime"
								required>
						</div>
						<div class="form-group mb-2">
							<label style="font-size: .72rem">Amount (&#8377;)</label> <input
								type="number" name="dupAmount" id="dupAmount" step="0.01"
								min="0.01" value="${txn.amount}" required>
						</div>
						<div class="form-group mb-2">
							<label style="font-size: .72rem">Category</label> <select
								name="dupCategoryId" id="dupCategorySelect" required>
								<c:choose>
									<c:when test="${txn.type=='INCOME'}">
										<c:forEach var="cat" items="${incomeCategories}">
											<option value="${cat.id}"
												${cat.id==txn.categoryId?'selected':''}>${cat.name}</option>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<c:forEach var="cat" items="${expenseCategories}">
											<option value="${cat.id}"
												${cat.id==txn.categoryId?'selected':''}>${cat.name}</option>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</select>
						</div>
						<div class="form-group mb-2">
							<label style="font-size: .72rem">Sub Category</label> <select
								name="dupSubCategoryId" id="dupSubCategorySelect">
								<option value="">None</option>
								<c:forEach var="sc" items="${subCategories}">
									<option value="${sc.id}" data-cat="${sc.category_id}"
										${sc.id==txn.subcategoryid?'selected':''}>${sc.name}</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group mb-2">
							<label style="font-size: .72rem">Note</label> <input type="text"
								name="dupNote" id="dupNote" value="${txn.note}">
						</div>

						<div
							style="font-size: .72rem; color: #94a3b8; margin: .25rem 0 .6rem">
							&#8505; Receipts &amp; attachments will not be copied.</div>

						<div class="flex gap-1 mt-1">
							<button type="submit" class="btn btn-primary btn-sm">&#10003;
								Save Copy</button>
							<button type="button" class="btn btn-outline btn-sm"
								onclick="cancelCopy()" style="color: var(--text-2)">
								&#10005; Cancel</button>
						</div>
					</form>
				</div>
			</div>

			<!-- Receipts -->
			<div class="card mt-2">
				<div class="flex mb-2">
					<span class="card-title" style="margin-bottom: 0">&#128248;
						Receipts &amp; Attachments</span> <span class="text-muted"
						style="font-size: .75rem; margin-left: .5rem">(max 5 MB
						each)</span>
				</div>
				<form action="${pageContext.request.contextPath}/receipt"
					method="post" enctype="multipart/form-data" class="flex gap-1">
					<input type="hidden" name="transactionId" value="${txn.id}">
					<input type="file" name="receipt" accept="image/*,application/pdf"
						style="font-size: .82rem; flex: 1" required>
					<button type="submit" class="btn btn-primary btn-sm">Upload</button>
				</form>
				<div class="receipt-grid">
					<c:forEach var="r" items="${receipts}">
						<div class="receipt-card">
							<c:choose>
								<c:when test="${r.image}">
									<img
										src="${pageContext.request.contextPath}/receipt?view=${r.id}"
										class="receipt-img"
										onclick="window.open('${pageContext.request.contextPath}/receipt?view=${r.id}','_blank')"
										alt="${r.fileName}">
								</c:when>
								<c:otherwise>
									<a
										href="${pageContext.request.contextPath}/receipt?view=${r.id}"
										target="_blank" class="receipt-file">&#128196;
										${r.fileName}</a>
								</c:otherwise>
							</c:choose>
							<div class="receipt-info">${r.fileSizeDisplay}<br>${r.formattedUploadedAt}</div>
							<form action="${pageContext.request.contextPath}/receipt"
								method="post" style="display: inline">
								<input type="hidden" name="action" value="delete"> <input
									type="hidden" name="receiptId" value="${r.id}"> <input
									type="hidden" name="transactionId" value="${txn.id}">
								<button class="receipt-del" title="Remove"
									onclick="return confirm('Remove receipt?')">&#x2715;</button>
							</form>
						</div>
					</c:forEach>
					<c:if test="${empty receipts}">
						<div class="text-muted"
							style="font-size: .82rem; padding: .5rem 0">No receipts
							attached.</div>
					</c:if>
				</div>
			</div>
		</div>

		<!-- RIGHT: Audit log -->
		<div class="card" style="min-height: 400px">
			<div class="card-title">
				&#128203; Change History <span
					style="font-weight: 400; color: var(--text-3)">(${auditLogs.size()}
					events)</span>
			</div>
			<c:if test="${empty auditLogs}">
				<div class="empty-state">No history yet.</div>
			</c:if>
			<div class="timeline">
				<c:forEach var="log" items="${auditLogs}">
					<div
						class="tl-item ${log.action=='CREATE'?'create':log.action=='UPDATE'?'update':'delete'}">
						<div class="tl-time">${log.formattedChangedAt}</div>
						<div class="tl-card">
							<c:choose>
								<c:when test="${log.action=='CREATE'}">
									<span class="tl-action act-create">&#10010; Created</span>
									<div style="font-size: .8rem; color: var(--text-2)">Transaction
										created</div>
								</c:when>
								<c:when test="${log.action=='DELETE'}">
									<span class="tl-action act-delete">&#x1F5D1; Deleted</span>
								</c:when>
								<c:when test="${log.action=='RECEIPT_ADD'}">
									<span class="tl-action act-create">&#10010; Receipt
										uploaded</span>
									<c:if test="${not empty log.fieldDisplay}">
										<div class="diff-row">
											<span class="diff-field">${log.fieldDisplay}</span> <span
												class="diff-new">${log.newValue}</span>
										</div>
									</c:if>
								</c:when>
								<c:when test="${log.action=='RECEIPT_DEL'}">
									<span class="tl-action act-delete">&#x1F5D1; Receipt
										deleted</span>
									<c:if test="${not empty log.fieldDisplay}">
										<div class="diff-row">
											<span class="diff-field">${log.fieldDisplay}</span> <span
												class="diff-old">${log.oldValue}</span>
										</div>
									</c:if>
								</c:when>
								<c:otherwise>
									<span class="tl-action act-update">&#9998; Updated</span>
									<c:if test="${not empty log.fieldDisplay}">
										<div class="diff-row">
											<span class="diff-field">${log.fieldDisplay}</span>
											<c:if test="${not empty log.oldValue}">
												<span class="diff-old">${log.oldValue}</span>
												<span style="color: var(--text-3)">&#8594;</span>
											</c:if>
											<span class="diff-new">${log.newValue}</span>
										</div>
									</c:if>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
</c:if>

<!-- ════ iOS-style Copy Modal ════ -->
<div id="copyModal" class="ios-overlay" style="display: none"
	onclick="handleOverlayClick(event)">
	<div class="ios-sheet" onclick="event.stopPropagation()">
		<div class="ios-sheet-title">Copy</div>

		<div class="ios-option selected" id="opt-today"
			onclick="selectCopyOption('today')">
			<span>Copy with today&#39;s date</span> <span class="check"
				id="chk-today">&#10003;</span>
		</div>
		<div class="ios-option" id="opt-original"
			onclick="selectCopyOption('original')">
			<span>Copy with date of entry</span> <span class="check"
				id="chk-original" style="display: none">&#10003;</span>
		</div>

		<button type="button" class="ios-ok" onclick="confirmCopyModal()">OK</button>
	</div>
</div>

<script>
	// ── Txn data for preview ───────────────────────────────
	var TXN_DATETIME_RAW = '${txn.dateTime}'; // e.g. 2026-06-19T09:52 or with seconds
	var TODAY_DATETIME_LOCAL = '';
	(function() {
		var now = new Date();
		var local = new Date(now - now.getTimezoneOffset() * 60000);
		TODAY_DATETIME_LOCAL = local.toISOString().slice(0, 16);
	})();

	var selectedCopyMode = 'today';

	// ── Open modal ─────────────────────────────────────────
	function openCopyModal() {
		// Reset to default — hide any leftover preview from earlier
		document.getElementById('copyPreview').style.display = 'none';
		selectCopyOption('today');
		document.getElementById('copyModal').style.display = 'flex';
		document.body.style.overflow = 'hidden';
	}

	// ── Option select ──────────────────────────────────────
	function selectCopyOption(mode) {
		selectedCopyMode = mode;
		[ 'today', 'original' ].forEach(function(m) {
			var opt = document.getElementById('opt-' + m);
			var chk = document.getElementById('chk-' + m);
			if (m === mode) {
				opt.classList.add('selected');
				chk.style.display = '';
			} else {
				opt.classList.remove('selected');
				chk.style.display = 'none';
			}
		});
	}

	// ── Overlay click to dismiss ───────────────────────────
	function handleOverlayClick(e) {
		if (e.target === document.getElementById('copyModal'))
			closeCopyOverlay();
	}

	function closeCopyOverlay() {
		document.getElementById('copyModal').style.display = 'none';
		document.body.style.overflow = '';
	}

	// ── OK clicked → close modal, show EDITABLE preview form ──
	function confirmCopyModal() {
		closeCopyOverlay();

		// Pre-fill datetime based on chosen mode
		var dtVal = (selectedCopyMode === 'today') ? TODAY_DATETIME_LOCAL
				: (TXN_DATETIME_RAW.length > 16 ? TXN_DATETIME_RAW.substring(0,
						16) : TXN_DATETIME_RAW);
		document.getElementById('dupDateTime').value = dtVal;

		// Re-apply sub-category visibility for the duplicate form's current category
		filterDupSub();

		// Show editable preview form
		document.getElementById('copyPreview').style.display = 'block';
		document.getElementById('copyPreview').scrollIntoView({
			behavior : 'smooth',
			block : 'nearest'
		});
	}

	// ── Cancel preview ─────────────────────────────────────
	function cancelCopy() {
		document.getElementById('copyPreview').style.display = 'none';
	}

	// ── Move to another cash book — toggle field visibility ──
	function toggleMoveBook() {
		var row = document.getElementById('moveBookRow');
		row.style.display = (row.style.display === 'none') ? 'block' : 'none';
	}

	// ── Sub-cat filter for duplicate popup ─────────────────
	function filterDupSub() {
		var catSel = document.getElementById('dupCategorySelect');
		var subSel = document.getElementById('dupSubCategorySelect');
		if (!catSel || !subSel)
			return;
		var selCat = catSel.value;
		var hasOpt = false;
		subSel.querySelectorAll('option[data-cat]').forEach(function(o) {
			var show = o.getAttribute('data-cat') === selCat;
			o.style.display = show ? '' : 'none';
			if (show)
				hasOpt = true;
		});
		var sel = subSel.options[subSel.selectedIndex];
		if (sel && sel.style.display === 'none')
			subSel.value = '';
	}
	document.addEventListener('DOMContentLoaded', function() {
		var dupCatSel = document.getElementById('dupCategorySelect');
		if (dupCatSel)
			dupCatSel.addEventListener('change', filterDupSub);
	});

	// ── DateTime fill ──────────────────────────────────────
	(function() {
		var el = document.getElementById('dtInput');
		var raw = '${txn.dateTime}';
		if (el && raw)
			el.value = raw.length > 16 ? raw.substring(0, 16) : raw;
	})();

	// ── Sub-cat filter ─────────────────────────────────────
	function filterEditSub() {
		var catSel = document.getElementById('editCat');
		var subSel = document.getElementById('editSub');
		var selCat = catSel.value;
		var hasOpt = false;
		subSel.querySelectorAll('option[data-cat]').forEach(function(o) {
			var show = o.getAttribute('data-cat') === selCat;
			o.style.display = show ? '' : 'none';
			if (show)
				hasOpt = true;
		});
		var sel = subSel.options[subSel.selectedIndex];
		if (sel && sel.style.display === 'none')
			subSel.value = '';
		subSel.disabled = !hasOpt;
	}
	document.addEventListener('DOMContentLoaded', function() {
		filterEditSub();
	});

	// ── Enter → submit edit form ───────────────────────────
	document.addEventListener('keydown', function(e) {
		if (e.key === 'Enter' && e.target.tagName !== 'BUTTON'
				&& e.target.tagName !== 'SELECT') {
			if (document.getElementById('copyModal').style.display !== 'none')
				return;
			e.preventDefault();
			document.getElementById('editForm').requestSubmit();
		}
	});

	// ── Alt+Left / Alt+Right → Prev / Next transaction ─────
	// (Alt-gated so it doesn't fire while typing in the note/amount fields.)
	document.addEventListener('keydown', function(e) {
		if (!e.altKey)
			return;
		if (e.key === 'ArrowLeft') {
			var el = document.getElementById('navPrevBtn');
			if (el) {
				e.preventDefault();
				el.click();
			}
		} else if (e.key === 'ArrowRight') {
			var el = document.getElementById('navNextBtn');
			if (el) {
				e.preventDefault();
				el.click();
			}
		}
	});
</script>

<%@ include file="footer.jsp"%>