<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Transactions" scope="request"/>
<c:set var="activePage" value="txn" scope="request"/>
<%@ include file="header.jsp" %>

<div class="page-header flex">
  <div>
    <h1>Transactions</h1>
    <p>All income &amp; expense records</p>
  </div>
  <div class="flex gap-2 ml-auto">
    <button class="btn btn-primary" onclick="openModal('incomeModal')">+ Income</button>
    <button class="btn btn-danger"  onclick="openModal('expenseModal')">+ Expense</button>
    <button class="btn btn-amber"   onclick="openModal('categoryModal')">⚙ Config</button>
  </div>
</div>

<!-- Alerts -->
<c:if test="${not empty param.success}">
  <div class="alert alert-success">✓ Transaction saved &amp; syncing to WorkDrive in background.</div>
</c:if>
<c:if test="${not empty param.error}">
  <div class="alert alert-error">✗ Error: ${param.error}</div>
</c:if>

<!-- Filter Tabs -->
<div class="tab-bar">
  <button class="tab-btn ${empty param.filter ? 'active' : ''}"
    onclick="location.href='${pageContext.request.contextPath}/transactions'">All</button>
  <button class="tab-btn income ${param.filter == 'INCOME' ? 'active' : ''}"
    onclick="location.href='${pageContext.request.contextPath}/transactions?filter=INCOME'">Income</button>
  <button class="tab-btn expense ${param.filter == 'EXPENSE' ? 'active' : ''}"
    onclick="location.href='${pageContext.request.contextPath}/transactions?filter=EXPENSE'">Expenses</button>
</div>

<div class="txn-table-wrap">
  <table>
    <thead>
      <tr>
        <th>#</th>
        <th>Date &amp; Time</th>
        <th>Type</th>
        <th>Category</th>
        <th>Amount</th>
        <th>Payment</th>
        <th>Note</th>
      </tr>
    </thead>
    <tbody>
      <c:set var="rowNum" value="0"/>
      <c:forEach var="t" items="${transactions}" varStatus="st">
        <c:set var="rowNum" value="${transactions.size() - st.index}"/>
        <tr>
          <td style="color:var(--text-muted);font-size:0.8rem">${rowNum}</td>
          <td class="text-mono" style="font-size:0.8rem;white-space:nowrap">
            <c:if test="${t.dateTime != null}">
              <fmt:formatDate value="${t.dateTime}" pattern="dd MMM yyyy HH:mm"/>
            </c:if>
          </td>
          <td><span class="badge ${t.type == 'INCOME' ? 'income' : 'expense'}">${t.type}</span></td>
          <td><span class="category-chip">${t.category}</span></td>
          <td class="amount-cell ${t.type == 'INCOME' ? 'income' : 'expense'} text-mono">
            ${t.type == 'INCOME' ? '+' : '−'}₹<fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/>
          </td>
          <td>
            <c:if test="${not empty t.payment}">
              <span class="payment-chip">${t.payment}</span>
            </c:if>
          </td>
          <td style="color:var(--text-secondary);max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">
            ${t.note}
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty transactions}">
        <tr>
          <td colspan="7">
            <div class="empty-state">
              <div class="icon">📊</div>
              <div>No transactions found. Add one using the buttons above.</div>
            </div>
          </td>
        </tr>
      </c:if>
    </tbody>
  </table>
</div>

<%@ include file="txn_modals.jsp" %>
<%@ include file="footer.jsp" %>
