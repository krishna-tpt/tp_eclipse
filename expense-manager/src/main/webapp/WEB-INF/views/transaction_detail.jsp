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

<div class="page-header flex">
	<div>
		<a href="${pageContext.request.contextPath}/transactions"
			style="color: var(--text-2); text-decoration: none; font-size: .85rem">&#8592;
			Back</a>
		<h1 style="margin-top: .2rem">Transaction Detail</h1>
	</div>
</div>

<c:if test="${not empty param.success}">
	<div class="alert alert-success">
		&#10003;
		<c:choose>
			<c:when test="${param.success=='updated'}">Transaction updated!</c:when>
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
						<c:when test="${txn.type == 'INCOME'}">
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
								<c:when test="${txn.type == 'INCOME'}">
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
					<div class="flex gap-1 mt-2">
						<button type="submit" class="btn btn-primary">&#10003;
							Save</button>
						<button type="button" class="btn btn-danger ml-auto"
							onclick="if(confirm('Delete this transaction permanently?'))document.getElementById('delForm').submit()">
							&#x1F5D1; Delete</button>
					</div>
				</form>
				<form id="delForm"
					action="${pageContext.request.contextPath}/transaction"
					method="post" style="display: none">
					<input type="hidden" name="action" value="delete"> <input
						type="hidden" name="id" value="${txn.id}">
				</form>
			</div>

			<!-- Receipts -->
			<div class="card mt-2">
				<div class="flex mb-2">
					<span class="card-title" style="margin-bottom: 0">&#128248;
						Receipts &amp; Attachments</span> <span class="text-muted"
						style="font-size: .75rem; margin-left: .5rem">(max 5 MB
						each)</span>
				</div>

				<%-- Upload form --%>
				<form action="${pageContext.request.contextPath}/receipt"
					method="post" enctype="multipart/form-data" class="flex gap-1">
					<input type="hidden" name="transactionId" value="${txn.id}">
					<input type="file" name="receipt" accept="image/*,application/pdf"
						style="font-size: .82rem; flex: 1" required>
					<button type="submit" class="btn btn-primary btn-sm">Upload</button>
				</form>

				<%-- Receipt grid --%>
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
						class="tl-item ${log.action=='CREATE'?'create':log.action=='UPDATE'?'update':log.action=='RECEIPT_ADD'?'Receipt uploaded':log.action=='RECEIPT_DEL'?'Receipt deleted':'delete'}">
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
									<span class="tl-action act-create">&#10010; Receipt uploaded</span>
									<div style="font-size: .8rem; color: var(--text-2)">Receipt uploaded</div>
								</c:when>
								<c:when test="${log.action=='RECEIPT_DEL'}">
									<span class="tl-action act-delete">&#x1F5D1; Receipt deleted</span>
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

<script>
// Set datetime-local value from LocalDateTime string
(function() {
  var el  = document.getElementById('dtInput');
  var raw = '${txn.dateTime}';           // e.g. 2026-06-08T10:30:00
  if (el && raw) el.value = raw.length > 16 ? raw.substring(0,16) : raw;
})();

// Sub-cat filter for edit form
function filterEditSub() {
  var catSel = document.getElementById('editCat');
  var subSel = document.getElementById('editSub');
  var selCat = catSel.value;
  var hasOpt = false;
  subSel.querySelectorAll('option[data-cat]').forEach(function(o) {
    var show = o.getAttribute('data-cat') === selCat;
    o.style.display = show ? '' : 'none';
    if (show) hasOpt = true;
  });
  var sel = subSel.options[subSel.selectedIndex];
  if (sel && sel.style.display === 'none') subSel.value = '';
  subSel.disabled = !hasOpt;
}

document.addEventListener('DOMContentLoaded', function() {
  filterEditSub();
});

// Enter → submit edit form
document.addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && e.target.tagName !== 'BUTTON' && e.target.tagName !== 'SELECT') {
    e.preventDefault();
    document.getElementById('editForm').requestSubmit();
  }
});
</script>

<%@ include file="footer.jsp"%>