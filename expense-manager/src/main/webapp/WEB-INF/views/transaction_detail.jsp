<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle"  value="Transaction Detail" scope="request"/>
<c:set var="activePage" value="txn"               scope="request"/>
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>" scope="request"/>
<%@ include file="header.jsp" %>

<style>
/* ── Audit Timeline ── */
.timeline { position:relative; padding-left:2rem; }
.timeline::before {
  content:''; position:absolute; left:.6rem; top:0; bottom:0;
  width:2px; background:var(--border);
}
.timeline-item {
  position:relative; margin-bottom:1.25rem;
}
.timeline-item::before {
  content:''; position:absolute; left:-1.55rem; top:.35rem;
  width:12px; height:12px; border-radius:50%;
  border:2px solid #fff; box-shadow:0 0 0 2px var(--border);
}
.timeline-item.create::before { background:var(--green); box-shadow:0 0 0 2px var(--green); }
.timeline-item.update::before { background:var(--primary); box-shadow:0 0 0 2px var(--primary); }
.timeline-item.delete::before { background:var(--red); box-shadow:0 0 0 2px var(--red); }

.timeline-time { font-size:.72rem; color:var(--text-2); margin-bottom:.2rem; }
.timeline-card {
  background:#fff; border:1px solid var(--border);
  border-radius:8px; padding:.75rem 1rem;
  box-shadow:0 1px 3px rgba(0,0,0,.05);
}
.timeline-action {
  display:inline-flex; align-items:center; gap:.3rem;
  padding:.12rem .55rem; border-radius:20px;
  font-size:.68rem; font-weight:700; text-transform:uppercase;
  letter-spacing:.4px; margin-bottom:.4rem;
}
.action-create { background:#dcfce7; color:#15803d; }
.action-update { background:#dbeafe; color:#1d4ed8; }
.action-delete { background:#fee2e2; color:#b91c1c; }

.diff-row {
  display:flex; align-items:baseline; gap:.5rem; flex-wrap:wrap;
  font-size:.82rem; margin-top:.25rem;
}
.diff-field { font-weight:600; color:var(--text-2); min-width:90px; }
.diff-old { color:var(--red); text-decoration:line-through; padding:.1rem .4rem; background:#fee2e2; border-radius:4px; }
.diff-arrow { color:var(--text-3); }
.diff-new { color:var(--green); padding:.1rem .4rem; background:#dcfce7; border-radius:4px; }

/* ── Field group in edit form ── */
.edit-section { margin-top:1.5rem; border-top:1px solid var(--border); padding-top:1.25rem; }
</style>

<div class="page-header flex">
  <div>
    <a href="${pageContext.request.contextPath}/transactions"
       style="color:var(--text-2);text-decoration:none;font-size:.85rem">
      &#8592; Back to Transactions
    </a>
    <h1 style="margin-top:.25rem">Transaction Detail</h1>
  </div>
</div>

<c:if test="${not empty param.success}">
  <div class="alert alert-success">&#10003; Transaction updated successfully!</div>
</c:if>
<c:if test="${not empty param.error}">
  <div class="alert alert-error">&#10007; Error: ${param.error}</div>
</c:if>
<c:if test="${not empty dbError}">
  <div class="alert alert-error">&#10007; ${dbError}</div>
</c:if>

<c:if test="${not empty txn}">
<div style="display:grid;grid-template-columns:1fr 1.2fr;gap:1.5rem;align-items:start">

  <!-- ═══ LEFT: Edit Form ═══ -->
  <div>
    <div class="card">
      <div class="flex mb-2">
        <span class="card-title" style="margin-bottom:0">Edit Transaction</span>
        <c:choose>
          <c:when test="${txn.type == 'INCOME'}">
            <span class="badge income ml-auto">INCOME</span>
          </c:when>
          <c:otherwise>
            <span class="badge expense ml-auto">EXPENSE</span>
          </c:otherwise>
        </c:choose>
      </div>

      <form action="${pageContext.request.contextPath}/transaction" method="post"
            id="editForm">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id"     value="${txn.id}">

        <div class="form-group mb-2">
          <label>Date &amp; Time *</label>
          <input type="datetime-local" name="dateTime"
                 value="${txn.dateTime}" required>
        </div>

        <div class="form-group mb-2">
          <label>Amount (&#8377;) *</label>
          <input type="number" name="amount" step="0.01" min="0.01"
                 value="${txn.amount}" required>
        </div>

        <div class="form-group mb-2">
          <label>Category *</label>
          <select name="categoryId" id="editCatSelect" required
                  onchange="filterEditSubCat()">
            <option value="">Select&#8230;</option>
            <c:choose>
              <c:when test="${txn.type == 'INCOME'}">
                <c:forEach var="cat" items="${incomeCategories}">
                  <option value="${cat.id}"
                          ${cat.id == txn.categoryId ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <c:forEach var="cat" items="${expenseCategories}">
                  <option value="${cat.id}"
                          ${cat.id == txn.categoryId ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </select>
        </div>

        <div class="form-group mb-2">
          <label>Sub Category</label>
          <select name="subcategoryId" id="editSubCatSelect">
            <option value="">None</option>
            <c:forEach var="sc" items="${subCategories}">
              <option value="${sc.id}"
                      data-cat="${sc.category_id}"
                      ${sc.id == txn.subcategoryid ? 'selected' : ''}
                      style="display:none">${sc.name}</option>
            </c:forEach>
          </select>
        </div>

        <div class="form-group mb-2">
          <label>Note</label>
          <input type="text" name="note" value="${txn.note}" placeholder="Optional">
        </div>

        <div class="flex gap-1 mt-3">
          <button type="submit" class="btn btn-primary">&#10003; Save Changes</button>
          <button type="button" class="btn btn-danger ml-auto"
                  onclick="if(confirm('Delete this transaction?')) {
                    document.getElementById('deleteForm').submit();
                  }">&#x1F5D1; Delete</button>
        </div>
      </form>

      <form id="deleteForm" action="${pageContext.request.contextPath}/transaction"
            method="post" style="display:none">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="id"     value="${txn.id}">
      </form>
    </div>

    <!-- Current values summary -->
    <div class="card mt-2">
      <div class="card-title">Current Values</div>
      <table style="font-size:.875rem;width:100%">
        <tr><td class="text-muted" style="padding:.35rem 0;width:120px">Transaction ID</td>
            <td style="font-weight:600">#${txn.id}</td></tr>
        <tr><td class="text-muted" style="padding:.35rem 0">Date &amp; Time</td>
            <td>${txn.formattedDateTime}</td></tr>
        <tr><td class="text-muted" style="padding:.35rem 0">Amount</td>
            <td class="${txn.type=='INCOME'?'amount-pos':'amount-neg'}" style="font-weight:700">
              ${txn.type=='INCOME'?'+':'-'}&#8377;${txn.amount}</td></tr>
        <tr><td class="text-muted" style="padding:.35rem 0">Category</td>
            <td><span class="chip">${txn.categoryName}</span></td></tr>
        <tr><td class="text-muted" style="padding:.35rem 0">Sub Category</td>
            <td>
              <c:choose>
                <c:when test="${not empty txn.subCategoryName}">
                  <span class="chip chip-amber">${txn.subCategoryName}</span>
                </c:when>
                <c:otherwise><span class="text-muted">—</span></c:otherwise>
              </c:choose>
            </td></tr>
        <tr><td class="text-muted" style="padding:.35rem 0">Note</td>
            <td>${not empty txn.note ? txn.note : '—'}</td></tr>
      </table>
    </div>
  </div>

  <!-- ═══ RIGHT: Audit Timeline ═══ -->
  <div class="card" style="min-height:400px">
    <div class="card-title">
      &#128203; Change History
      <span style="color:var(--text-3);font-weight:400;margin-left:.5rem">
        (${auditLogs.size()} events)
      </span>
    </div>

    <c:if test="${empty auditLogs}">
      <div class="empty-state">No history yet.</div>
    </c:if>

    <div class="timeline">
      <c:forEach var="log" items="${auditLogs}">
        <div class="timeline-item ${log.action == 'CREATE' ? 'create' : log.action == 'UPDATE' ? 'update' : 'delete'}">
          <div class="timeline-time">${log.formattedChangedAt}</div>
          <div class="timeline-card">
            <c:choose>
              <c:when test="${log.action == 'CREATE'}">
                <span class="timeline-action action-create">&#10010; Created</span>
                <div style="font-size:.82rem;color:var(--text-2)">Transaction created</div>
              </c:when>
              <c:when test="${log.action == 'DELETE'}">
                <span class="timeline-action action-delete">&#x1F5D1; Deleted</span>
                <div style="font-size:.82rem;color:var(--text-2)">Transaction deleted</div>
              </c:when>
              <c:otherwise>
                <span class="timeline-action action-update">&#9998; Updated</span>
                <c:if test="${not empty log.fieldDisplay}">
                  <div class="diff-row">
                    <span class="diff-field">${log.fieldDisplay}</span>
                    <c:if test="${not empty log.oldValue}">
                      <span class="diff-old">${log.oldValue}</span>
                      <span class="diff-arrow">&#8594;</span>
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
// Initialize sub-category filter for edit form
document.addEventListener('DOMContentLoaded', function() {
  filterEditSubCat();
});

function filterEditSubCat() {
  var catSel  = document.getElementById('editCatSelect');
  var subSel  = document.getElementById('editSubCatSelect');
  var selCat  = catSel.value;
  var opts    = subSel.querySelectorAll('option[data-cat]');
  var hasOpts = false;

  opts.forEach(function(o) {
    var show = o.getAttribute('data-cat') === selCat;
    o.style.display = show ? '' : 'none';
    if (show) hasOpts = true;
  });

  // If current selection is hidden, reset
  var selected = subSel.options[subSel.selectedIndex];
  if (selected && selected.style.display === 'none') subSel.value = '';
  subSel.disabled = !hasOpts;
}

// datetime-local needs value in "YYYY-MM-DDTHH:MM" format
// Set from JSP value
(function() {
  var dtInput = document.querySelector('input[name="dateTime"]');
  if (!dtInput) return;
  // JSP gives LocalDateTime toString: "2026-06-08T10:30:00" → slice to 16 chars
  var val = dtInput.value;
  if (val && val.length > 16) dtInput.value = val.substring(0, 16);
})();

// Enter key → submit edit form
document.addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && e.target.tagName !== 'BUTTON') {
    e.preventDefault();
    document.getElementById('editForm').requestSubmit();
  }
});
</script>

<%@ include file="footer.jsp" %>
