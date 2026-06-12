<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle"  value="Audit Log"  scope="request"/>
<c:set var="activePage" value="audit"      scope="request"/>
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>" scope="request"/>
<%@ include file="header.jsp" %>

<style>
.action-badge {
  display:inline-flex; align-items:center; padding:.15rem .55rem;
  border-radius:20px; font-size:.68rem; font-weight:700;
  text-transform:uppercase; letter-spacing:.4px; white-space:nowrap;
}
.act-create { background:#dcfce7; color:#15803d; }
.act-update { background:#dbeafe; color:#1d4ed8; }
.act-delete { background:#fee2e2; color:#b91c1c; }

.diff-inline { display:flex; align-items:center; gap:.35rem; flex-wrap:wrap; font-size:.82rem; }
.diff-old { color:#b91c1c; text-decoration:line-through; background:#fee2e2; padding:.05rem .35rem; border-radius:4px; max-width:180px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.diff-new { color:#15803d; background:#dcfce7; padding:.05rem .35rem; border-radius:4px; max-width:180px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.diff-field { font-weight:600; color:var(--text-2); font-size:.75rem; min-width:75px; }
.txn-link { color:var(--primary); text-decoration:none; font-weight:600; font-size:.82rem; }
.txn-link:hover { text-decoration:underline; }
</style>

<div class="page-header flex">
  <div>
    <h1>&#128203; Audit Log</h1>
    <p>All changes for <strong>${sessionScope.activeBookName}</strong> — ${total} events</p>
  </div>
  <a href="${pageContext.request.contextPath}/transactions" class="btn btn-outline btn-sm ml-auto">
    &#8592; Back to Transactions
  </a>
</div>

<c:if test="${not empty dbError}">
  <div class="alert alert-error">&#10007; ${dbError}</div>
</c:if>

<div class="table-wrap">
  <table>
    <thead>
      <tr>
        <th>Time</th>
        <th>Action</th>
        <th>Transaction</th>
        <th>Field Changed</th>
        <th>Old Value</th>
        <th></th>
        <th>New Value</th>
    <!--     <th>By</th> -->
      </tr>
    </thead>
    <tbody>
      <c:forEach var="log" items="${auditLogs}">
        <tr>
          <td class="text-muted" style="font-size:.78rem;white-space:nowrap">${log.formattedChangedAt}</td>
          <td>
            <span class="action-badge ${log.action=='CREATE'?'act-create':log.action=='UPDATE'?'act-update':'act-delete'}">
              <c:choose>
                <c:when test="${log.action=='CREATE'}">&#10010; Created</c:when>
                <c:when test="${log.action=='UPDATE'}">&#9998; Updated</c:when>
                <c:otherwise>&#x1F5D1; Deleted</c:otherwise>
              </c:choose>
            </span>
          </td>
          <td>
            <a href="${pageContext.request.contextPath}/transaction?id=${log.transactionId}"
               class="txn-link">#${log.transactionId}</a>
            <c:if test="${not empty log.txnDate}">
              <div style="font-size:.72rem;color:var(--text-2)">${log.txnDate}</div>
            </c:if>
            <c:if test="${not empty log.txnCategoryName}">
              <span class="chip" style="font-size:.68rem">${log.txnCategoryName}</span>
            </c:if>
          </td>
          <td>
            <c:if test="${not empty log.fieldDisplay}">
              <span class="diff-field">${log.fieldDisplay}</span>
            </c:if>
          </td>
          <td>
            <c:if test="${not empty log.oldValue}">
              <span class="diff-old" title="${log.oldValue}">${log.oldValue}</span>
            </c:if>
          </td>
          <td style="color:var(--text-3);font-size:.8rem">
            <c:if test="${not empty log.oldValue or not empty log.newValue}">&#8594;</c:if>
          </td>
          <td>
            <c:if test="${not empty log.newValue}">
              <span class="diff-new" title="${log.newValue}">${log.newValue}</span>
            </c:if>
            <c:if test="${log.action=='CREATE'}">
              <span style="font-size:.78rem;color:var(--text-2)">Transaction created</span>
            </c:if>
            <c:if test="${log.action=='DELETE'}">
              <span style="font-size:.78rem;color:var(--red)">Transaction deleted</span>
            </c:if>
          </td>
          <%-- <td class="text-muted" style="font-size:.78rem">${log.changedBy}</td> --%>
        </tr>
      </c:forEach>
      <c:if test="${empty auditLogs}">
        <tr>
          <td colspan="8" class="empty-state">
            <div class="icon">&#128203;</div>
            No audit events yet. Changes to transactions will appear here.
          </td>
        </tr>
      </c:if>
    </tbody>
  </table>
</div>

<%-- Pagination --%>
<c:if test="${totalPages > 1}">
  <div class="pagination mt-2">
    <c:forEach begin="1" end="${totalPages}" var="p">
      <a href="${pageContext.request.contextPath}/audit?page=${p}"
         class="page-btn ${p==page?'active':''}">${p}</a>
    </c:forEach>
  </div>
</c:if>

<%@ include file="footer.jsp" %>
