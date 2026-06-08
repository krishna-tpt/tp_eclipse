<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle"  value="Transactions" scope="request"/>
<c:set var="activePage" value="txn"          scope="request"/>
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>" scope="request"/>
<%@ include file="header.jsp" %>

<style>
/* Clickable rows */
tbody tr.clickable { cursor:pointer; transition:background .1s; }
tbody tr.clickable:hover { background:#f0f7ff !important; }
tbody tr.clickable:hover td { color:var(--primary); }
tbody tr.clickable:hover .badge,
tbody tr.clickable:hover .chip,
tbody tr.clickable:hover .amount-pos,
tbody tr.clickable:hover .amount-neg { filter:brightness(.9); }
.row-hint { font-size:.68rem; color:var(--text-3); margin-top:.15rem; }
</style>

<div class="page-header flex">
  <div>
    <h1>Transactions</h1>
    <p>All records for <strong>${sessionScope.activeBookName}</strong> (${total} total)
       &nbsp;<span class="row-hint">&#128071; Click any row to view/edit</span></p>
  </div>
  <div class="flex gap-1 ml-auto">
    <button class="btn btn-success btn-sm" onclick="openModal('incomeModal')">+ Income</button>
    <button class="btn btn-danger btn-sm"  onclick="openModal('expenseModal')">+ Expense</button>
   <!-- <button class="btn btn-outline btn-sm" onclick="openModal('catModal')">+ Category</button>
    <button class="btn btn-outline btn-sm" onclick="openModal('colModal')">+ Column</button> -->
  </div>
</div>

<c:if test="${not empty param.success}">
  <div class="alert alert-success">
    <c:choose>
      <c:when test="${param.success=='1'}">&#10003; Transaction saved!</c:when>
      <c:when test="${param.success=='deleted'}">&#10003; Transaction deleted.</c:when>
      <c:when test="${param.success=='cat'}">&#10003; Category added!</c:when>
      <c:when test="${param.success=='col'}">&#10003; Custom column added!</c:when>
    </c:choose>
  </div>
</c:if>
<c:if test="${not empty param.error}">
  <div class="alert alert-error">&#10007; Error: ${param.error}</div>
</c:if>
<c:if test="${not empty dbError}">
  <div class="alert alert-error">&#10007; DB: ${dbError}</div>
</c:if>

<!-- Filter tabs -->
<div class="tabs">
  <a href="${pageContext.request.contextPath}/transactions"
     class="tab ${empty param.filter ? 'active' : ''}">All</a>
  <a href="${pageContext.request.contextPath}/transactions?filter=INCOME"
     class="tab income ${param.filter == 'INCOME' ? 'active' : ''}">Income</a>
  <a href="${pageContext.request.contextPath}/transactions?filter=EXPENSE"
     class="tab expense ${param.filter == 'EXPENSE' ? 'active' : ''}">Expenses</a>
</div>

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
        <th style="width:60px"></th>
        <c:choose>
          <c:when test="${param.filter == 'INCOME'}">
            <c:forEach var="col" items="${incomeColumns}"><th>${col.colName}</th></c:forEach>
          </c:when>
          <c:when test="${param.filter == 'EXPENSE'}">
            <c:forEach var="col" items="${expenseColumns}"><th>${col.colName}</th></c:forEach>
          </c:when>
        </c:choose>
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
          <td class="text-muted"
              style="max-width:160px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">
            ${t.note}
          </td>
          <td>
            <a href="${pageContext.request.contextPath}/transaction?id=${t.id}"
               class="btn btn-outline btn-sm"
               onclick="event.stopPropagation()"
               title="Edit / View history">&#9998;</a>
          </td>
          <c:choose>
            <c:when test="${param.filter == 'INCOME'}">
              <c:forEach var="col" items="${incomeColumns}">
                <td class="text-muted">${t.customValues[col.colKey]}</td>
              </c:forEach>
            </c:when>
            <c:when test="${param.filter == 'EXPENSE'}">
              <c:forEach var="col" items="${expenseColumns}">
                <td class="text-muted">${t.customValues[col.colKey]}</td>
              </c:forEach>
            </c:when>
          </c:choose>
        </tr>
      </c:forEach>
      <c:if test="${empty transactions}">
        <tr><td colspan="9" class="empty-state">No transactions found.</td></tr>
      </c:if>
    </tbody>
  </table>
</div>

<!-- Pagination -->
<c:if test="${totalPages > 1}">
  <div class="pagination mt-2">
    <c:forEach begin="1" end="${totalPages}" var="p">
      <c:choose>
        <c:when test="${not empty param.filter}">
          <a href="${pageContext.request.contextPath}/transactions?page=${p}&amp;filter=${param.filter}"
             class="page-btn ${p == page ? 'active' : ''}">${p}</a>
        </c:when>
        <c:otherwise>
          <a href="${pageContext.request.contextPath}/transactions?page=${p}"
             class="page-btn ${p == page ? 'active' : ''}">${p}</a>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </div>
</c:if>

<%@ include file="txn_modals.jsp" %>
<%@ include file="footer.jsp" %>