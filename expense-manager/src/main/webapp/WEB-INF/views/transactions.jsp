<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="pageTitle" value="Transactions" scope="request" />
<c:set var="activePage" value="txn" scope="request" />
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>"
	scope="request" />
<%@ include file="header.jsp"%>

<style>
.filter-panel {
	background: #f8fafc;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: 1rem 1.25rem;
	margin-bottom: 1.25rem;
}

.filter-panel.collapsed .filter-body {
	display: none;
}

.filter-header {
	display: flex;
	align-items: center;
	gap: .5rem;
	cursor: pointer;
	user-select: none;
}

.filter-header h4 {
	font-size: .875rem;
	font-weight: 600;
	margin: 0;
}

.filter-tag {
	background: #dbeafe;
	color: var(--primary);
	padding: .1rem .5rem;
	border-radius: 20px;
	font-size: .7rem;
	font-weight: 600;
}

.filter-body {
	margin-top: .85rem;
}

.amount-row {
	display: flex;
	gap: .5rem;
	align-items: center;
	flex-wrap: wrap;
}

.amount-row select {
	width: 80px;
	flex-shrink: 0;
}

.amount-row input {
	width: 120px;
}

.amount-row .and {
	font-size: .8rem;
	color: var(--text-2);
	font-weight: 600;
	padding: 0 .25rem;
}

tbody tr.clickable {
	cursor: pointer;
}

tbody tr.clickable:hover {
	background: #eff6ff !important;
}
</style>

<div class="page-header flex">
	<div>
		<h1>Transactions</h1>
		<p>
			<strong>${sessionScope.activeBookName}</strong> — ${total} records
			found
			<c:if test="${filter.filtered}">
        &nbsp;<span class="filter-tag">&#128269; Filtered</span>
			</c:if>
		</p>
	</div>
	<div class="flex gap-1 ml-auto">
		<a href="${pageContext.request.contextPath}/calendar"
			class="btn btn-outline btn-sm">&#128197; Calendar</a>
		<button class="btn btn-success btn-sm"
			onclick="openModal('incomeModal')">+ Income</button>
		<button class="btn btn-danger btn-sm"
			onclick="openModal('expenseModal')">+ Expense</button>
		<!-- <button class="btn btn-outline btn-sm" onclick="openModal('catModal')">+
			Category</button>
		<button class="btn btn-outline btn-sm" onclick="openModal('colModal')">+
			Column</button> -->
	</div>
</div>

<c:if test="${not empty param.success}">
	<div class="alert alert-success">
		&#10003;
		<c:choose>
			<c:when test="${param.success=='saved'}">Transaction saved!</c:when>
			<c:when test="${param.success=='deleted'}">Transaction deleted.</c:when>
		</c:choose>
	</div>
</c:if>

<!-- ═══ ADVANCED FILTER PANEL ═══ -->
<div class="filter-panel ${filter.filtered ? '' : 'collapsed'}"
	id="filterPanel">
	<div class="filter-header" onclick="toggleFilter()">
		<span style="font-size: 1rem">&#128269;</span>
		<h4>Search &amp; Filter</h4>
		<c:if test="${filter.filtered}">
			<span class="filter-tag">Active</span>
		</c:if>
		<a
			href="${pageContext.request.contextPath}/transactions${not empty param.filter ? '?filter='.concat(param.filter) : ''}"
			class="btn btn-outline btn-sm ml-auto" style="font-size: .75rem"
			onclick="event.stopPropagation()">&#10005; Clear All</a> <span
			id="filterArrow" style="transition: transform .2s; font-size: .75rem">&#9660;</span>
	</div>

	<div class="filter-body">
		<form method="get"
			action="${pageContext.request.contextPath}/transactions"
			id="filterForm">
			<%-- Preserve type tab --%>
			<c:if test="${not empty param.filter}">
				<input type="hidden" name="filter" value="${param.filter}">
			</c:if>

			<div class="form-grid"
				style="grid-template-columns: repeat(auto-fit, minmax(180px, 1fr))">

				<!-- Date From -->
				<div class="form-group">
					<label>Date From</label> <input type="date" name="dateFrom"
						value="${filter.dateFrom}">
				</div>
				<!-- Date To -->
				<div class="form-group">
					<label>Date To</label> <input type="date" name="dateTo"
						value="${filter.dateTo}">
				</div>

				<!-- Category -->
				<div class="form-group">
					<label>Category</label> <select name="categoryId"
						onchange="filterSubCatSearch()">
						<option value="">All Categories</option>
						<c:if test="${empty param.filter or param.filter == 'INCOME'}">
							<optgroup label="Income">
								<c:forEach var="cat" items="${incomeCategories}">
									<option value="${cat.id}"
										${filter.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
								</c:forEach>
							</optgroup>
						</c:if>
						<c:if test="${empty param.filter or param.filter == 'EXPENSE'}">
							<optgroup label="Expense">
								<c:forEach var="cat" items="${expenseCategories}">
									<option value="${cat.id}"
										${filter.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
								</c:forEach>
							</optgroup>
						</c:if>
					</select>
				</div>

				<!-- Sub Category -->
				<div class="form-group">
					<label>Sub Category</label> <select name="subCategoryId"
						id="searchSubCat">
						<option value="">All</option>
						<c:forEach var="sc" items="${subCategories}">
							<option value="${sc.id}" data-cat="${sc.category_id}"
								${filter.subCategoryId == sc.id ? 'selected' : ''}>${sc.name}</option>
						</c:forEach>
					</select>
				</div>

				<!-- Note / Custom search -->
				<div class="form-group" style="grid-column: span 2">
					<label>Search (Note &amp; Custom Fields)</label> <input type="text"
						name="search" placeholder="Search in note, custom fields&#8230;"
						value="${filter.noteSearch}">
				</div>
			</div>

			<!-- Amount filter -->
			<div class="form-group mt-2">
				<label>Amount Filter</label>
				<div class="amount-row mt-1">
					<select name="amountOp1">
						<option value="" ${empty filter.amountOp1 ? 'selected' : ''}>Any</option>
						<option value="=" ${filter.amountOp1 == '=' ? 'selected' : ''}>=</option>
						<option value=">" ${filter.amountOp1 == '>' ? 'selected' : ''}>&#62;</option>
						<option value=">=" ${filter.amountOp1 == '>=' ? 'selected' : ''}>&#62;=</option>
						<option value="<" ${filter.amountOp1 == '<' ? 'selected' : ''}>&#60;</option>
            <option value="<=" ${filter.amountOp1 == '<=' ? 'selected' : ''}>&#60;=</option>
          </select>
          <input type="number" name="amount1" step="0.01" min="0"
                 placeholder="Amount" value="${filter.amount1}">

          <span class="and">AND</span>

          <select name="amountOp2">
            <option value=""  ${empty filter.amountOp2 ? 'selected' : ''}>(none)</option>
            <option value=">=" ${filter.amountOp2 == '>=' ? 'selected' : ''}>&#62;=</option>
            <option value="<=" ${filter.amountOp2 == '<=' ? 'selected' : ''}>&#60;=</option>
            <option value=">" ${filter.amountOp2 == '>' ? 'selected' : ''}>&#62;</option>
            <option value="<" ${filter.amountOp2 == '<' ? 'selected' : ''}>&#60;</option>
          </select>
          <input type="number" name="amount2" step="0.01" min="0"
                 placeholder="Amount (range)" value="${filter.amount2}">
        </div>
        <div style="font-size:.73rem;color:var(--text-2);margin-top:.3rem">
          Example: &gt;= 10 AND &lt;= 50 &rarr; amounts between 10 and 50
        </div>
      </div>

      <div class="flex gap-1 mt-2">
        <button type="submit" class="btn btn-primary btn-sm">&#128269; Apply Filter</button>
        <button type="button" class="btn btn-outline btn-sm"
          onclick="event.stopPropagation()">Reset</button> 
      </div>
    </form>
  </div>
</div>

<!-- Type tabs -->
<div class="tabs">
  <a href="${pageContext.request.contextPath}/transactions"
   class="tab ${empty param.filter ? 'active' : ''}">All</a>
<a href="${pageContext.request.contextPath}/transactions?filter=INCOME"
   class="tab income ${param.filter == 'INCOME' ? 'active' : ''}">Income</a>
<a href="${pageContext.request.contextPath}/transactions?filter=EXPENSE"
   class="tab expense ${param.filter == 'EXPENSE' ? 'active' : ''}">Expenses</a>
</div>

<%-- Helper function to preserve filter params when switching tabs --%>
<%!
  private String filterQS(String type) {
    // tabs just pass type; other filters reset on tab switch for simplicity
    return type.isEmpty() ? "" : "?filter=" + type;
  }
%>

<div class="table-wrap">
  <table>
    <thead>
      <tr>
        <th>#</th><th>Date &amp; Time</th><th>Type</th>
        <th>Category</th><th>Sub Cat</th><th>Amount</th><th>Note</th>
        <th style="width:50px"></th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="t" items="${transactions}" varStatus="st">
        <tr class="clickable"
            onclick="window.location='${pageContext.request.contextPath}/transaction?id=${t.id}'">
          <td class="text-muted" style="font-size:.78rem">${total - ((page-1)*15) - st.index}</td>
          <td class="text-muted" style="font-size:.82rem;white-space:nowrap">${t.formattedDateTime}</td>
          <td>
            <c:choose>
              <c:when test="${t.type == 'INCOME'}"><span class="badge income">INCOME</span></c:when>
              <c:otherwise><span class="badge expense">EXPENSE</span></c:otherwise>
            </c:choose>
          </td>
          <td><span class="chip">${t.categoryName}</span></td>
          <td>
            <c:if test="${not empty t.subCategoryName}">
              <span class="chip chip-amber">${t.subCategoryName}</span>
            </c:if>
          </td>
          <td>
            <c:choose>
              <c:when test="${t.type == 'INCOME'}">
                <span class="amount-pos">+&#8377;${t.amount}</span>
              </c:when>
              <c:otherwise>
                <span class="amount-neg">-&#8377;${t.amount}</span>
              </c:otherwise>
            </c:choose>
          </td>
          <td class="text-muted" style="max-width:160px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">${t.note}</td>
          <td>
            <a href="${pageContext.request.contextPath}/transaction?id=${t.id}"
               class="btn btn-outline btn-sm" onclick="event.stopPropagation()" title="Edit">&#9998;</a>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty transactions}">
        <tr><td colspan="8" class="empty-state">No transactions match your filter.</td></tr>
      </c:if>
    </tbody>
  </table>
</div>

<!-- Pagination -->
<c:if test="${totalPages > 1}">
  <div class="pagination mt-2">
    <c:forEach begin="1" end="${totalPages}" var="p">
      <a href="${pageContext.request.contextPath}/transactions?page=${p}<c:if test="${not empty param.filter}">&amp;filter=${param.filter}</c:if>"
         class="page-btn ${p == page ? 'active' : ''}">${p}</a>
    </c:forEach>
  </div>
</c:if>

<script>
// Filter panel toggle
function toggleFilter() {
  var p = document.getElementById('filterPanel');
  var a = document.getElementById('filterArrow');
  p.classList.toggle('collapsed');
  a.style.transform = p.classList.contains('collapsed') ? 'rotate(-90deg)' : 'rotate(0)';
}

// Sub-cat filter in search form
function filterSubCatSearch() {
  var catSel = document.querySelector('[name="categoryId"]');
  var subSel = document.getElementById('searchSubCat');
  var selCat = catSel.value;
  subSel.querySelectorAll('option[data-cat]').forEach(function(o) {
    o.style.display = (!selCat || o.getAttribute('data-cat') === selCat) ? '' : 'none';
  });
  subSel.value = '';
}

// Enter → submit filter form (when filter panel open)
document.addEventListener('keydown', function(e) {
  if (e.key !== 'Enter') return;
  var openModal = document.querySelector('.modal-overlay.open');
  if (openModal) return; // let modal handle it
  var panel = document.getElementById('filterPanel');
  if (!panel.classList.contains('collapsed')) {
    e.preventDefault();
    document.getElementById('filterForm').submit();
  }
});

// Init sub-cat visibility
filterSubCatSearch();
</script>

<%@ include file="txn_modals.jsp" %>
<%@ include file="footer.jsp" %>