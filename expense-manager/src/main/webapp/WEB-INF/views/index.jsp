<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle"  value="Dashboard" scope="request"/>
<c:set var="activePage" value="home"       scope="request"/>
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>" scope="request"/>
<%@ include file="header.jsp" %>

<div class="page-header flex">
  <div>
    <h1>Dashboard</h1>
    <p>
      <c:choose>
        <c:when test="${not empty sessionScope.activeBookName}">
          ${sessionScope.activeBookName} — financial overview
        </c:when>
        <c:otherwise>Your financial overview at a glance</c:otherwise>
      </c:choose>
    </p>
  </div>
  <div class="flex gap-1 ml-auto">
    <button class="btn btn-success" onclick="openModal('incomeModal')">+ Income</button>
    <button class="btn btn-danger"  onclick="openModal('expenseModal')">+ Expense</button>
  </div>
</div>

<c:if test="${not empty dbError}">
  <div class="alert alert-error">&#9888; DB Error: ${dbError}</div>
</c:if>

<%-- Export bar (PDF / Excel / Email) --%>
<%@ include file="export_bar.jsp" %>

<!-- Stats -->
<div class="stats-grid">
  <div class="stat-card">
    <div class="stat-label">Total Income</div>
    <div class="stat-value income">&#8377;<fmt:formatNumber value="${totalIncome}"  pattern="#,##0.00"/></div>
  </div>
  <div class="stat-card">
    <div class="stat-label">Total Expenses</div>
    <div class="stat-value expense">&#8377;<fmt:formatNumber value="${totalExpense}" pattern="#,##0.00"/></div>
  </div>
  <div class="stat-card">
    <div class="stat-label">Net Balance</div>
    <div class="stat-value balance">&#8377;<fmt:formatNumber value="${balance}" pattern="#,##0.00"/></div>
  </div>
</div>

<!-- Recent Transactions -->
<div class="card">
  <div class="flex mb-2">
    <span class="card-title" style="margin-bottom:0">Recent Transactions</span>
    <a href="${pageContext.request.contextPath}/transactions" class="btn btn-outline btn-sm ml-auto">View All &#8594;</a>
  </div>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>Date</th><th>Type</th><th>Category</th>
          <th>Sub Cat</th><th>Amount</th><th>Note</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="t" items="${recentTxns}">
          <tr>
            <td class="text-muted">${t.formattedDateTime}</td>
            <td>
              <c:choose>
                <c:when test="${t.type == 'INCOME'}">
                  <span class="badge income">INCOME</span>
                </c:when>
                <c:otherwise>
                  <span class="badge expense">EXPENSE</span>
                </c:otherwise>
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
            <td class="text-muted">${t.note}</td>
          </tr>
        </c:forEach>
        <c:if test="${empty recentTxns}">
          <tr><td colspan="6" class="empty-state">No transactions yet. Add one above!</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<div class="flex gap-2 mt-3">
  <a href="${pageContext.request.contextPath}/transactions?filter=INCOME"  class="btn btn-outline">View Income &#8594;</a>
  <a href="${pageContext.request.contextPath}/transactions?filter=EXPENSE" class="btn btn-outline">View Expenses &#8594;</a>
  <a href="${pageContext.request.contextPath}/reports" class="btn btn-primary ml-auto">Analytics &#8594;</a>
</div>

<%@ include file="txn_modals.jsp" %>
<%@ include file="footer.jsp" %>
