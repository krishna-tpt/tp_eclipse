<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="pageTitle" value="Transactions" scope="request" />
<c:set var="activePage" value="txn" scope="request" />
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>"
	scope="request" />
<%@ include file="header.jsp"%>

<style>
/* Layout: list + detail side-by-side */
.txn-layout {
	display: grid;
	grid-template-columns: 1fr;
	gap: 1.25rem;
	transition: grid-template-columns .3s;
}

.txn-layout.detail-open {
	grid-template-columns: 1fr 420px;
}

.detail-panel {
	display: none;
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	overflow: hidden;
	position: sticky;
	top: 70px;
	max-height: calc(100vh - 90px);
	overflow-y: auto;
}

.txn-layout.detail-open .detail-panel {
	display: block;
}

/* Filter panel */
.filter-panel {
	background: #f8fafc;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: .85rem 1.1rem;
	margin-bottom: 1.1rem;
}

.filter-head {
	display: flex;
	align-items: center;
	gap: .6rem;
	cursor: pointer;
	user-select: none;
}

.filter-head h4 {
	font-size: .875rem;
	font-weight: 600;
	margin: 0;
}

.filter-body {
	margin-top: .85rem;
}

.filter-panel.collapsed .filter-body {
	display: none;
}

.filter-badge {
	background: #dbeafe;
	color: var(--primary);
	padding: .1rem .5rem;
	border-radius: 20px;
	font-size: .68rem;
	font-weight: 700;
}

/* Multi-select styled */
.multi-select-wrap {
	position: relative;
}

.ms-trigger {
	display: flex;
	align-items: center;
	justify-content: space-between;
	background: #fff;
	border: 1px solid var(--border);
	border-radius: 7px;
	padding: .55rem .75rem;
	cursor: pointer;
	font-size: .875rem;
	user-select: none;
	transition: border-color .15s;
}

.ms-trigger:hover {
	border-color: var(--primary);
}

.ms-trigger.open {
	border-color: var(--primary);
	box-shadow: 0 0 0 3px rgba(37, 99, 235, .1);
}

.ms-dropdown {
	position: absolute;
	top: calc(100% + 4px);
	left: 0;
	right: 0;
	background: #fff;
	border: 1px solid var(--border);
	border-radius: 8px;
	box-shadow: 0 8px 24px rgba(0, 0, 0, .1);
	z-index: 50;
	max-height: 220px;
	overflow-y: auto;
	display: none;
}

.ms-dropdown.show {
	display: block;
}

.ms-option {
	display: flex;
	align-items: center;
	gap: .5rem;
	padding: .5rem .75rem;
	cursor: pointer;
	font-size: .875rem;
	transition: background .1s;
}

.ms-option:hover {
	background: #f0f7ff;
}

.ms-option input[type=checkbox] {
	accent-color: var(--primary);
	width: 15px;
	height: 15px;
	flex-shrink: 0;
}

.ms-selected-tags {
	display: flex;
	flex-wrap: wrap;
	gap: .3rem;
	margin-top: .3rem;
}

.ms-tag {
	background: #dbeafe;
	color: var(--primary);
	padding: .1rem .45rem;
	border-radius: 20px;
	font-size: .7rem;
	font-weight: 500;
	display: flex;
	align-items: center;
	gap: .2rem;
}

.ms-tag button {
	background: none;
	border: none;
	cursor: pointer;
	color: inherit;
	font-size: .75rem;
	padding: 0;
	line-height: 1;
}

/* Rows */
tbody tr.clickable {
	cursor: pointer;
}

tbody tr.clickable:hover {
	background: #f0f7ff !important;
}

tbody tr.selected {
	background: #eff6ff !important;
	outline: 2px solid var(--primary);
	outline-offset: -2px;
}

/* â”€â”€ Amount filter â”€â”€ */
.amt-row {
	display: flex;
	gap: .4rem;
	align-items: center;
	flex-wrap: wrap;
}

.amt-row select {
	width: 75px;
	flex-shrink: 0;
	font-size: .85rem;
}

.amt-row input {
	width: 110px;
}

.amt-and {
	font-size: .75rem;
	font-weight: 700;
	color: var(--text-2);
}

/*  Export bar */
.export-bar {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: .6rem 1rem;
	margin-bottom: 1rem;
	display: flex;
	align-items: center;
	gap: .5rem;
	flex-wrap: wrap;
}

@media ( max-width :900px) {
	.txn-layout.detail-open {
		grid-template-columns: 1fr;
	}
	.detail-panel {
		position: fixed;
		inset: 0;
		z-index: 300;
		max-height: 100vh;
		border-radius: 0;
	}
}
</style>

<div class="page-header flex">
	<div>
		<h1>Transactions</h1>
		<p>
			<strong>${sessionScope.activeBookName}</strong> ${total} records
			<c:if test="${filter.filtered}">&nbsp;<span
					class="filter-badge">&#128269; Filtered</span>
			</c:if>
		</p>
	</div>
	<div class="flex gap-1 ml-auto">
		<a href="${pageContext.request.contextPath}/calendar"
			class="btn btn-outline btn-sm">&#128197; Calendar</a> <a
			href="${pageContext.request.contextPath}/audit"
			class="btn btn-outline btn-sm">&#128203; Audit Log</a>
		<button class="btn btn-success btn-sm"
			onclick="openModal('incomeModal')">+ Income</button>
		<button class="btn btn-danger btn-sm"
			onclick="openModal('expenseModal')">+ Expense</button>
		<button class="btn btn-outline btn-sm" onclick="openModal('catModal')">+
			Category</button>
		<button class="btn btn-outline btn-sm" onclick="openModal('colModal')">+
			Column</button>
	</div>
</div>

<%-- Alerts --%>
<c:if test="${not empty param.success}">
	<div class="alert alert-success">
		&#10003;
		<c:choose>
			<c:when test="${param.success=='1'}">Transaction saved!</c:when>
			<c:when test="${param.success=='deleted'}">Transaction deleted.</c:when>
		</c:choose>
	</div>
</c:if>

<%-- Export bar --%>
<div class="export-bar">
	<span style="font-size: .8rem; font-weight: 600; color: var(--text-2)">
		&#128216; ${sessionScope.activeBookName} <c:if
			test="${filter.filtered}"> &bull; Filtered (${total} records)</c:if>
		&nbsp;&#8250;
	</span>
	<%-- Build filter query string for filtered export --%>
	<c:set var="fqs" value="" />
	<c:if test="${filter.filtered}">
		<c:set var="fqs" value="&filtered=1" />
		<c:if test="${filter.dateFrom != null}">
			<c:set var="fqs" value="${fqs}&dateFrom=${filter.dateFrom}" />
		</c:if>
		<c:if test="${filter.dateTo   != null}">
			<c:set var="fqs" value="${fqs}&dateTo=${filter.dateTo}" />
		</c:if>
		<c:if test="${not empty filter.type}">
			<c:set var="fqs" value="${fqs}&filter=${filter.type}" />
		</c:if>
		<c:if test="${not empty filter.noteSearch}">
			<c:set var="fqs" value="${fqs}&search=${filter.noteSearch}" />
		</c:if>
	</c:if>
	<a href="${pageContext.request.contextPath}/export?type=pdf${fqs}"
		class="btn btn-outline btn-sm">&#128196; PDF</a> <a
		href="${pageContext.request.contextPath}/export?type=excel${fqs}"
		class="btn btn-outline btn-sm">&#128202; Excel</a>
	<button class="btn btn-primary btn-sm"
		onclick="openModal('emailModal')">&#9993; Email</button>
</div>

<%-- ═══ FILTER PANEL ═══ --%>
<div class="filter-panel ${filter.filtered ? '' : 'collapsed'}"
	id="filterPanel">
	<div class="filter-head" onclick="toggleFilter()">
		<span>&#128269;</span>
		<h4>Search &amp; Filter</h4>
		<c:if test="${filter.filtered}">
			<span class="filter-badge">Active</span>
		</c:if>
		<a href="${pageContext.request.contextPath}/home"
			class="btn btn-outline btn-sm ml-auto" style="font-size: .72rem"
			onclick="event.stopPropagation()">&#10005; Clear</a> <span
			id="fArrow" style="font-size: .7rem; transition: transform .2s">&#9660;</span>
	</div>

	<div class="filter-body">
		<form id="filterForm" method="get"
			action="${pageContext.request.contextPath}/home">
			<c:if test="${not empty param.filter}">
				<input type="hidden" name="filter" value="${param.filter}">
			</c:if>

			<div class="form-grid"
				style="grid-template-columns: repeat(auto-fit, minmax(165px, 1fr))">
				<div class="form-group">
					<label>Date From</label> <input type="date" name="dateFrom"
						value="${empty filter.dateFrom ? todayStr : filter.dateFrom}">
				</div>
				<div class="form-group">
					<label>Date To</label> <input type="date" name="dateTo"
						value="${empty filter.dateTo ? todayStr : filter.dateTo}">
				</div>

				<%-- Multi-select: Category --%>
				<div class="form-group" style="grid-column: span 2">
					<label>Category (multi-select)</label>
					<div class="multi-select-wrap" id="msCatWrap">
						<div class="ms-trigger" onclick="toggleMS('msCat')"
							id="msCatTrigger">
							<span id="msCatLabel">All Categories</span> <span
								style="font-size: .65rem; opacity: .6">&#9660;</span>
						</div>
						<div class="ms-dropdown" id="msCat">
							<div class="ms-search-row">
								<input type="text" class="ms-search" placeholder="Search&#8230;"
									autocomplete="off" onclick="event.stopPropagation()"
									onkeydown="if(event.key==='Enter'){event.preventDefault();event.stopPropagation();}"
									oninput="filterMSOptions('msCat', this.value)">
							</div>
							<label class="ms-option ms-select-all"> <input
								type="checkbox" class="ms-select-all-cb"
								onchange="toggleSelectAllMS('msCat','msCatLabel','msCatTags','categoryId', this.checked)">
								<strong>(Select All)</strong>
							</label>
							<c:if test="${empty param.filter or param.filter == 'INCOME'}">
								<div class="ms-group-header">Income</div>
								<c:forEach var="cat" items="${incomeCategories}">
									<label class="ms-option"> <input type="checkbox"
										name="categoryId" value="${cat.id}"
										${filter.categoryIds != null && filter.categoryIds.contains(cat.id) ? 'checked' : ''}
										onchange="updateMSTags('msCat','msCatLabel','msCatTags','categoryId')">
										${cat.name}
									</label>
								</c:forEach>
							</c:if>
							<c:if test="${empty param.filter or param.filter == 'EXPENSE'}">
								<div class="ms-group-header">Expense</div>
								<c:forEach var="cat" items="${expenseCategories}">
									<label class="ms-option"> <input type="checkbox"
										name="categoryId" value="${cat.id}"
										${filter.categoryIds != null && filter.categoryIds.contains(cat.id) ? 'checked' : ''}
										onchange="updateMSTags('msCat','msCatLabel','msCatTags','categoryId')">
										${cat.name}
									</label>
								</c:forEach>
							</c:if>
							<div class="ms-clear-row">
								<button type="button" class="ms-clear-btn"
									onclick="clearMSOptions('msCat','msCatLabel','msCatTags','categoryId')">&#10005;
									Clear</button>
							</div>
						</div>
						<div class="ms-selected-tags" id="msCatTags"></div>
					</div>
				</div>

				<%-- Multi-select: Sub Category --%>
				<div class="form-group" style="grid-column: span 2">
					<label>Sub Category (multi-select)</label>
					<div class="multi-select-wrap" id="msSubWrap">
						<div class="ms-trigger" onclick="toggleMS('msSub')"
							id="msSubTrigger">
							<span id="msSubLabel">All Sub Categories</span> <span
								style="font-size: .65rem; opacity: .6">&#9660;</span>
						</div>
						<div class="ms-dropdown" id="msSub">
							<div class="ms-search-row">
								<input type="text" class="ms-search" placeholder="Search&#8230;"
									autocomplete="off" onclick="event.stopPropagation()"
									onkeydown="if(event.key==='Enter'){event.preventDefault();event.stopPropagation();}"
									oninput="filterMSOptions('msSub', this.value)">
							</div>
							<label class="ms-option ms-select-all"> <input
								type="checkbox" class="ms-select-all-cb"
								onchange="toggleSelectAllMS('msSub','msSubLabel','msSubTags','subCategoryId', this.checked)">
								<strong>(Select All)</strong>
							</label>
							<c:forEach var="sc" items="${subCategories}">
								<label class="ms-option"> <input type="checkbox"
									name="subCategoryId" value="${sc.id}"
									${filter.subCategoryIds != null && filter.subCategoryIds.contains(sc.id) ? 'checked' : ''}
									onchange="updateMSTags('msSub','msSubLabel','msSubTags','subCategoryId')">
									${sc.name}
								</label>
							</c:forEach>
							<div class="ms-clear-row">
								<button type="button" class="ms-clear-btn"
									onclick="clearMSOptions('msSub','msSubLabel','msSubTags','subCategoryId')">&#10005;
									Clear</button>
							</div>
						</div>
						<div class="ms-selected-tags" id="msSubTags"></div>
					</div>
				</div>

				<%-- Note search --%>
				<div class="form-group" style="grid-column: 1/-1">
					<label>Search (Note &amp; Custom Fields)</label> <input type="text"
						name="search" placeholder="e.g. Dinner; tea"
						value="${filter.noteSearch}">
					<div
						style="font-size: .7rem; color: var(--text-2); margin-top: .25rem">
						Use
						<code>;</code>
						to search multiple keywords &mdash; e.g.
						<code>Dinner; tea</code>
						matches notes/custom fields containing &ldquo;Dinner&rdquo; OR
						&ldquo;tea&rdquo;.
					</div>
				</div>
			</div>

			<%-- Amount filter --%>
			<div class="form-group mt-2">
				<label>Amount Filter</label>
				<div class="amt-row mt-1">
					<select name="amountOp1">
						<option value=""
							<c:if test="${empty filter.amountOp1}">selected</c:if>>Any</option>
						<option value="="
							<c:if test="${filter.amountOp1 == '='}">selected</c:if>>=</option>
						<option value=">"
							<c:if test="${filter.amountOp1 == '>'}">selected</c:if>>&#62;</option>
						<option value=">="
							<c:if test="${filter.amountOp1 == '>='}">selected</c:if>>&#62;=</option>
						<option value="&lt;"
							<c:if test="${filter.amountOp1 == '<'}">selected</c:if>>&#60;</option>
						<option value="&lt;="
							<c:if test="${filter.amountOp1 == '<='}">selected</c:if>>&#60;=</option>
					</select> <input type="number" name="amount1" step="0.01" min="0"
						placeholder="Amount" value="${filter.amount1}"> <span
						class="amt-and">AND</span> <select name="amountOp2">
						<option value=""
							<c:if test="${empty filter.amountOp2}">selected</c:if>>(none)</option>
						<option value=">="
							<c:if test="${filter.amountOp2 == '>='}">selected</c:if>>&#62;=</option>
						<option value="&lt;="
							<c:if test="${filter.amountOp2 == '<='}">selected</c:if>>&#60;=</option>
						<option value=">"
							<c:if test="${filter.amountOp2 == '>'}">selected</c:if>>&#62;</option>
						<option value="&lt;"
							<c:if test="${filter.amountOp2 == '<'}">selected</c:if>>&#60;</option>
					</select> <input type="number" name="amount2" step="0.01" min="0"
						placeholder="Range end" value="${filter.amount2}">
				</div>
				<div
					style="font-size: .7rem; color: var(--text-2); margin-top: .25rem">
					Example: &gt;=10 AND &lt;=50 &rarr; amounts between 10 and 50</div>
			</div>

			<div class="flex gap-1 mt-2">
				<button type="submit" class="btn btn-primary btn-sm">&#128269;
					Apply</button>
				<button type="button" class="btn btn-outline btn-sm"
					onclick="location.href='${pageContext.request.contextPath}/home'">Reset</button>
			</div>
		</form>
	</div>
</div>

<%-- Type tabs --%>
<div class="tabs">
	<a href="${pageContext.request.contextPath}/transactions"
		class="tab ${empty param.filter?'active':''}">All</a> <a
		href="${pageContext.request.contextPath}/transactions?filter=INCOME"
		class="tab income  ${param.filter=='INCOME'?'active':''}">Income</a> <a
		href="${pageContext.request.contextPath}/transactions?filter=EXPENSE"
		class="tab expense ${param.filter=='EXPENSE'?'active':''}">Expenses</a>
</div>

<%--  MAIN LAYOUT: table + detail panel  --%>
<div class="txn-layout" id="txnLayout">
	<div>
		<div class="table-wrap">
			<table>
				<thead>
					<tr>
						<th>#</th>
						<th>Date &amp; Time</th>
						<th>Type</th>
						<th>Category</th>
						<th>Sub Cat</th>
						<th>Amount</th>
						<th>Note</th>
						<th style="width: 42px"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="t" items="${transactions}" varStatus="st">
						<tr class="clickable" id="row-${t.id}"
							<%-- onclick="loadDetail(${t.id}, this)"> --%>
							onclick="window.location='${pageContext.request.contextPath}/transaction?id=${t.id}'">
							<td class="text-muted" style="font-size: .78rem">${total-((page-1)*15)-st.index}</td>
							<td style="font-size: .82rem; white-space: nowrap">${t.formattedDateTime}</td>
							<td><c:choose>
									<c:when test="${t.type=='INCOME'}">
										<span class="badge income">INCOME</span>
									</c:when>
									<c:otherwise>
										<span class="badge expense">EXPENSE</span>
									</c:otherwise>
								</c:choose></td>
							<td><span class="chip">${t.categoryName}</span></td>
							<td><c:if test="${not empty t.subCategoryName}">
									<span class="chip chip-amber">${t.subCategoryName}</span>
								</c:if></td>
							<td><c:choose>
									<c:when test="${t.type=='INCOME'}">
										<span class="amount-pos">+&#8377;${t.amount}</span>
									</c:when>
									<c:otherwise>
										<span class="amount-neg">-&#8377;${t.amount}</span>
									</c:otherwise>
								</c:choose></td>
							<td class="text-muted"
								style="max-width: 140px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">${t.note}</td>
							<td><a
								href="${pageContext.request.contextPath}/transaction?id=${t.id}"
								class="btn btn-outline btn-sm" onclick="event.stopPropagation()"
								title="Full page edit">&#8599;</a></td>
						</tr>
					</c:forEach>
					<c:if test="${empty transactions}">
						<tr>
							<td colspan="8" class="empty-state">No transactions match
								your filter.</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>

		<%-- Pagination --%>
		<c:if test="${totalPages > 1}">
			<div class="pagination mt-2">
				<c:forEach begin="1" end="${totalPages}" var="p">
					<a
						href="${pageContext.request.contextPath}/transactions?page=${p}<c:if test="${not empty param.filter}">&amp;filter=${param.filter}</c:if>"
						class="page-btn ${p==page?'active':''}">${p}</a>
				</c:forEach>
			</div>
		</c:if>
	</div>

	<%-- INLINE DETAIL PANEL  --%>
	<div class="detail-panel" id="detailPanel">
		<div
			style="padding: 1rem; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: .5rem; background: #f8fafc">
			<span style="font-weight: 700; font-size: .9rem" id="dpTitle">Transaction</span>
			<a id="dpFullLink" href="#" class="btn btn-outline btn-sm"
				style="font-size: .72rem">Full Page &#8599;</a>
			<button onclick="closeDetail()"
				class="btn btn-outline btn-sm ml-auto" style="font-size: .75rem">&#x2715;
				Close</button>
		</div>
		<div id="dpBody" style="padding: 1rem">
			<div
				style="text-align: center; color: var(--text-2); padding: 2rem; font-size: .9rem">
				Click a transaction row to view details</div>
		</div>
	</div>
</div>

<%-- Email modal --%>
<div id="emailModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3>&#9993; Send Report via Email</h3>
			<button class="modal-close" onclick="closeModal('emailModal')">&#x2715;</button>
		</div>
		<form action="${pageContext.request.contextPath}/export" method="post">
			<div class="form-group mb-2">
				<label>Recipient Email *</label> <input type="email" name="email"
					placeholder="yourname@gmail.com" required autofocus>
			</div>
			<div class="form-group mb-2">
				<label>Format</label>
				<div class="flex gap-2" style="margin-top: .3rem">
					<label class="flex gap-1" style="cursor: pointer"><input
						type="radio" name="format" value="pdf" checked> PDF</label> <label
						class="flex gap-1" style="cursor: pointer"><input
						type="radio" name="format" value="excel"> Excel</label>
				</div>
			</div>
			<div class="flex mt-2">
				<button type="button" class="btn btn-outline"
					onclick="closeModal('emailModal')">Cancel</button>
				<button type="submit" class="btn btn-primary ml-auto">Send</button>
			</div>
		</form>
	</div>
</div>

<script>
const CTX = '${pageContext.request.contextPath}';

function closeDetail() {
  document.getElementById('txnLayout').classList.remove('detail-open');
  document.querySelectorAll('tbody tr.selected').forEach(r => r.classList.remove('selected'));
}

function toggleFilter() {
  var p = document.getElementById('filterPanel');
  var a = document.getElementById('fArrow');
  p.classList.toggle('collapsed');
  a.style.transform = p.classList.contains('collapsed') ? 'rotate(-90deg)' : '';
}

function toggleMS(id) {
  var dd = document.getElementById(id);
  dd.classList.toggle('show');
  var trigger = dd.previousElementSibling;
  trigger.classList.toggle('open', dd.classList.contains('show'));
}

document.addEventListener('click', function(e) {
  document.querySelectorAll('.ms-dropdown.show').forEach(function(dd) {
    var wrap = dd.closest('.multi-select-wrap');
    if (!wrap.contains(e.target)) {
      dd.classList.remove('show');
      dd.previousElementSibling.classList.remove('open');
    }
  });
});

// ── Filter visible options as user types, WITHOUT touching checked
//    state — so a category picked before a search stays selected
//    even while it's scrolled out of view / hidden by the search. ──
function filterMSOptions(ddId, query) {
  var dd = document.getElementById(ddId);
  var q = query.trim().toLowerCase();

  dd.querySelectorAll('.ms-option:not(.ms-select-all)').forEach(function(opt) {
    var text = opt.textContent.trim().toLowerCase();
    opt.style.display = (q === '' || text.indexOf(q) !== -1) ? '' : 'none';
  });

  // Hide a group header (e.g. "Income" / "Expense") if every option
  // under it is currently filtered out.
  dd.querySelectorAll('.ms-group-header').forEach(function(hdr) {
    var el = hdr.nextElementSibling;
    var anyVisible = false;
    while (el && el.classList.contains('ms-option')) {
      if (el.style.display !== 'none') anyVisible = true;
      el = el.nextElementSibling;
    }
    hdr.style.display = anyVisible ? '' : 'none';
  });
}


//── "(Select All)" — applies to whatever options are currently
// visible (i.e. respects an active search filter). ──────────
function toggleSelectAllMS(ddId, labelId, tagsId, inputName, checked) {
var dd = document.getElementById(ddId);
dd.querySelectorAll('.ms-option:not(.ms-select-all)').forEach(function(opt) {
 if (opt.style.display !== 'none') {
   var cb = opt.querySelector('input[type="checkbox"]');
   if (cb) cb.checked = checked;
 }
});
updateMSTags(ddId, labelId, tagsId, inputName);
}


//── Clear button inside the dropdown — unchecks everything
// regardless of search filter, and resets the search box. ──
function clearMSOptions(ddId, labelId, tagsId, inputName) {
var dd = document.getElementById(ddId);
dd.querySelectorAll('input[type="checkbox"]').forEach(function(cb) { cb.checked = false; });

var search = dd.querySelector('.ms-search');
if (search) {
 search.value = '';
 filterMSOptions(ddId, '');
}
updateMSTags(ddId, labelId, tagsId, inputName);
}

document.addEventListener('click', function(e) {
document.querySelectorAll('.ms-dropdown.show').forEach(function(dd) {
 var wrap = dd.closest('.multi-select-wrap');
 if (!wrap.contains(e.target)) {
   dd.classList.remove('show');
   dd.previousElementSibling.classList.remove('open');
 }
});
});


function updateMSTags(ddId, labelId, tagsId, inputName) {
	  var dd     = document.getElementById(ddId);
	  var label  = document.getElementById(labelId);
	  var tagsEl = document.getElementById(tagsId);
	  var checked = dd.querySelectorAll('input[name="' + inputName + '"]:checked');
	  if (checked.length === 0) {
	    label.textContent = 'All'; tagsEl.innerHTML = '';
	  } else {
	    label.textContent = checked.length + ' selected';
	    tagsEl.innerHTML  = '';
	    checked.forEach(function(cb) {
	      var tag = document.createElement('span');
	      tag.className = 'ms-tag';
	      tag.innerHTML = cb.closest('.ms-option').textContent.trim()
	        + '<button type="button" onclick="uncheckTag(this,\'' + ddId + '\',\'' + labelId + '\',\'' + tagsId + '\',\'' + inputName + '\')">&#x2715;</button>';
	      tagsEl.appendChild(tag);
	    });
	  }

	  // Keep the "(Select All)" checkbox in sync: checked only when every
	  // currently visible option is checked.
	  var selAllCb = dd.querySelector('.ms-select-all-cb');
	  if (selAllCb) {
	    var visibleOpts = Array.prototype.filter.call(
	      dd.querySelectorAll('.ms-option:not(.ms-select-all)'),
	      function(o) { return o.style.display !== 'none'; });
	    var visibleChecked = visibleOpts.filter(function(o) {
	      return o.querySelector('input[type="checkbox"]').checked;
	    });
	    selAllCb.checked = visibleOpts.length > 0 && visibleOpts.length === visibleChecked.length;
	  }
	}

function uncheckTag(btn, ddId, labelId, tagsId, inputName) {
  var tagText = btn.previousSibling.textContent.trim();
  var dd = document.getElementById(ddId);
  dd.querySelectorAll('input:checked').forEach(function(cb) {
    if (cb.closest('.ms-option').textContent.trim() === tagText) cb.checked = false;
  });
  updateMSTags(ddId, labelId, tagsId, inputName);
}

document.addEventListener('keydown', function(e) {
  if (e.key !== 'Enter') return;
  if (document.querySelector('.modal-overlay.open')) return;
  var fp = document.getElementById('filterPanel');
  if (!fp.classList.contains('collapsed')) {
    e.preventDefault();
    document.getElementById('filterForm').submit();
  }
});

document.addEventListener('DOMContentLoaded', function() {
  updateMSTags('msCat', 'msCatLabel', 'msCatTags', 'categoryId');
  updateMSTags('msSub', 'msSubLabel', 'msSubTags', 'subCategoryId');
});
</script>

<%@ include file="txn_modals.jsp"%>
<%@ include file="footer.jsp"%>