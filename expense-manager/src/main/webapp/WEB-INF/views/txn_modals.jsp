<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Load data if not already set (called from dashboard) --%>
<%
  if (request.getAttribute("incomeCategories") == null) {
    try {
      com.expensemanager.dao.CategoryDAO cDao         = new com.expensemanager.dao.CategoryDAO();
      com.expensemanager.dao.ColumnDefinitionDAO colDao = new com.expensemanager.dao.ColumnDefinitionDAO();
      com.expensemanager.dao.SubCategoryDAO scDao       = new com.expensemanager.dao.SubCategoryDAO();
      request.setAttribute("incomeCategories",  cDao.findByType("INCOME"));
      request.setAttribute("expenseCategories", cDao.findByType("EXPENSE"));
      request.setAttribute("incomeColumns",     colDao.findByType("INCOME"));
      request.setAttribute("expenseColumns",    colDao.findByType("EXPENSE"));
      request.setAttribute("subCategories",     scDao.findAll());
    } catch (Exception ignored) {}
  }
%>

<!-- ══ ADD INCOME ══ -->
<div id="incomeModal" class="modal-overlay">
  <div class="modal">
    <div class="modal-header">
      <h3 style="color:var(--green)">+ Add Income</h3>
      <button class="modal-close" onclick="closeModal('incomeModal')">&#x2715;</button>
    </div>
    <form id="incomeForm" action="${pageContext.request.contextPath}/transactions" method="post"
          onsubmit="return prepareSubmit('incomeForm')">
      <input type="hidden" name="type" value="INCOME">
      <div class="form-grid">
        <div class="form-group">
          <label>Date &amp; Time *</label>
          <input type="datetime-local" name="dateTime" required>
        </div>
        <div class="form-group">
          <label>Amount (&#8377;) *</label>
          <input type="number" name="amount" min="0.01" step="0.01"
                 placeholder="0.00" required autofocus>
        </div>
        <div class="form-group">
          <label>Category *</label>
          <select name="categoryid" id="incCategorySelect" required onchange="filterSubCat('inc')">
            <option value="">Select&#8230;</option>
            <c:forEach var="cat" items="${incomeCategories}">
              <option value="${cat.id}">${cat.name}</option>
            </c:forEach>
          </select>
        </div>
        <div class="form-group">
          <label>Sub Category</label>
          <select name="subcategory_id" id="incSubCatSelect" disabled>
            <option value="">Select&#8230;</option>
            <c:forEach var="sc" items="${subCategories}">
              <option value="${sc.id}" data-cat="${sc.category_id}">
                ${sc.name}
              </option>
            </c:forEach>
          </select>
        </div>
        <div class="form-group" style="grid-column:1/-1">
          <label>Note</label>
          <input type="text" name="note" placeholder="Optional">
        </div>
      </div>

      <c:if test="${not empty incomeColumns}">
        <div style="margin-top:.75rem;border-top:1px solid var(--border);padding-top:.75rem">
          <div class="card-title">Custom Fields</div>
          <div class="form-grid">
            <c:forEach var="col" items="${incomeColumns}">
              <div class="form-group">
                <label>${col.colName}</label>
                <input type="text" name="custom_${col.colKey}" placeholder="${col.colName}">
              </div>
            </c:forEach>
          </div>
        </div>
      </c:if>

 <div id="incExtras"></div>
      <div class="flex gap-1 mt-2">
       <!-- <button type="button" class="btn btn-outline btn-sm"
                onclick="addCustomField('incExtras')">+ Ad-hoc Field</button> -->
        <button type="submit" class="btn btn-success ml-auto">Save Income</button>
      </div> 
    </form>
  </div>
</div>

<!-- ══ ADD EXPENSE ══ -->
<div id="expenseModal" class="modal-overlay">
  <div class="modal">
    <div class="modal-header">
      <h3 style="color:var(--red)">+ Add Expense</h3>
      <button class="modal-close" onclick="closeModal('expenseModal')">&#x2715;</button>
    </div>
    <form id="expenseForm" action="${pageContext.request.contextPath}/transactions" method="post"
          onsubmit="return prepareSubmit('expenseForm')">
      <input type="hidden" name="type" value="EXPENSE">
      <div class="form-grid">
        <div class="form-group">
          <label>Date &amp; Time *</label>
          <input type="datetime-local" name="dateTime" required>
        </div>
        <div class="form-group">
          <label>Amount (&#8377;) *</label>
          <input type="number" name="amount" min="0.01" step="0.01" placeholder="0.00" required>
        </div>
        <div class="form-group">
          <label>Category *</label>
          <select name="categoryId" id="expCategorySelect" required onchange="filterSubCat('exp')">
            <option value="">Select&#8230;</option>
            <c:forEach var="cat" items="${expenseCategories}">
              <option value="${cat.id}">${cat.name}</option>
            </c:forEach>
          </select>
        </div>
        <div class="form-group">
          <label>Sub Category</label>
          <select name="subcategoryId" id="expSubCatSelect" disabled>
            <option value="">Select&#8230;</option>
            <c:forEach var="sc" items="${subCategories}">
              <option value="${sc.id}" data-cat="${sc.category_id}">
                ${sc.name}
              </option>
            </c:forEach>
          </select>
        </div>
        <div class="form-group" style="grid-column:1/-1">
          <label>Note</label>
          <input type="text" name="note" placeholder="Optional">
        </div>
      </div>

      <c:if test="${not empty expenseColumns}">
        <div style="margin-top:.75rem;border-top:1px solid var(--border);padding-top:.75rem">
          <div class="card-title">Custom Fields</div>
          <div class="form-grid">
            <c:forEach var="col" items="${expenseColumns}">
              <div class="form-group">
                <label>${col.colName}</label>
                <input type="text" name="custom_${col.colKey}" placeholder="${col.colName}">
              </div>
            </c:forEach>
          </div>
        </div>
      </c:if>

     <div id="expExtras"></div>
      <div class="flex gap-1 mt-2">
        <!-- <button type="button" class="btn btn-outline btn-sm"
                onclick="addCustomField('expExtras')">+ Ad-hoc Field</button> -->
        <button type="submit" class="btn btn-danger ml-auto">Save Expense</button>
      </div>
    </form>
  </div>
</div>

<script>
// ── Enter key → submit active form ──────────────────────
document.addEventListener('keydown', function(e) {
  if (e.key !== 'Enter') return;
  // Don't intercept textarea or button
  if (e.target.tagName === 'TEXTAREA' || e.target.tagName === 'BUTTON') return;

  var openModal = document.querySelector('.modal-overlay.open');
  if (!openModal) return;

  var form = openModal.querySelector('form');
  if (form) {
    e.preventDefault();
    form.requestSubmit();   // triggers onsubmit + HTML5 validation
  }
});

// ── Sub-category filter ─────────────────────────────────
function filterSubCat(prefix) {
  var catSel = document.getElementById(prefix + 'CategorySelect');
  var subSel = document.getElementById(prefix + 'SubCatSelect');
  var selCat = catSel.value;

  subSel.value = '';
  var opts = subSel.querySelectorAll('option[data-cat]');
  var has  = false;
  opts.forEach(function(o) {
    var show = o.getAttribute('data-cat') === selCat;
    o.style.display = show ? '' : 'none';
    if (show) has = true;
  });
  subSel.disabled = !has;
}

// ── Gather ad-hoc custom fields before submit ───────────
function prepareSubmit(formId) {
  var form = document.getElementById(formId);
  form.querySelectorAll('[name="_cfk"]').forEach(function(kEl, i) {
    var k = kEl.value.trim();
    if (!k) return;
    var vEl = form.querySelectorAll('[name="_cfv"]')[i];
    var inp = document.createElement('input');
    inp.type  = 'hidden';
    inp.name  = 'custom_' + k.toLowerCase().replace(/[^a-z0-9]+/g,'_');
    inp.value = vEl ? vEl.value : '';
    form.appendChild(inp);
  });
  return true;
}

// ── Auto-fill datetime on open ──────────────────────────
document.addEventListener('DOMContentLoaded', function() {
  var now   = new Date();
  var local = new Date(now - now.getTimezoneOffset() * 60000).toISOString().slice(0,16);
  document.querySelectorAll('input[type="datetime-local"]').forEach(function(el) {
    if (!el.value) el.value = local;
  });
});
</script>
