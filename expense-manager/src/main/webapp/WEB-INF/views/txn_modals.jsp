<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- ── Add Income Modal ── -->
<div id="incomeModal" class="modal-overlay">
  <div class="modal">
    <div class="modal-header">
      <h3 style="color:var(--green)">＋ Add Income</h3>
      <button class="modal-close" onclick="closeModal('incomeModal')">✕</button>
    </div>
    <form id="incomeForm" action="${pageContext.request.contextPath}/transactions" method="post" onsubmit="return gatherExtraFields('incomeForm')">
      <input type="hidden" name="type" value="income">
      <div class="form-grid">
        <div class="form-group">
          <label>Date & Time *</label>
          <input type="datetime-local" name="dateTime" required>
        </div>
        <div class="form-group">
          <label>Amount (₹) *</label>
          <input type="number" name="amount" min="0.01" step="0.01" placeholder="0.00" required>
        </div>
        <div class="form-group">
          <label>Category *</label>
          <select name="category" required>
            <option value="">Select category</option>
            <c:forEach var="cat" items="${config.incomeCategories}">
              <option value="${cat}">${cat}</option>
            </c:forEach>
          </select>
        </div>
        <div class="form-group">
          <label>Payment Mode</label>
          <select name="payment">
            <option value="">— optional —</option>
            <c:forEach var="pm" items="${config.incomePaymentModes}">
              <option value="${pm}">${pm}</option>
            </c:forEach>
          </select>
        </div>
        <div class="form-group" style="grid-column:1/-1">
          <label>Note</label>
          <input type="text" name="note" placeholder="Optional note">
        </div>
      </div>
      <!-- Extra fields container -->
      <div id="incomeExtras"></div>
      <div class="flex gap-2 mt-2">
        <button type="button" class="btn btn-secondary" onclick="addExtraFieldRow('incomeExtras')">+ Custom Field</button>
        <button type="submit" class="btn btn-primary ml-auto">Save Income</button>
      </div>
    </form>
  </div>
</div>

<!-- ── Add Expense Modal ── -->
<div id="expenseModal" class="modal-overlay">
  <div class="modal">
    <div class="modal-header">
      <h3 style="color:var(--red)">＋ Add Expense</h3>
      <button class="modal-close" onclick="closeModal('expenseModal')">✕</button>
    </div>
    <form id="expenseForm" action="${pageContext.request.contextPath}/transactions" method="post" onsubmit="return gatherExtraFields('expenseForm')">
      <input type="hidden" name="type" value="expense">
      <div class="form-grid">
        <div class="form-group">
          <label>Date & Time *</label>
          <input type="datetime-local" name="dateTime" required>
        </div>
        <div class="form-group">
          <label>Amount (₹) *</label>
          <input type="number" name="amount" min="0.01" step="0.01" placeholder="0.00" required>
        </div>
        <div class="form-group">
          <label>Category *</label>
          <select name="category" required>
            <option value="">Select category</option>
            <c:forEach var="cat" items="${config.expenseCategories}">
              <option value="${cat}">${cat}</option>
            </c:forEach>
          </select>
        </div>
        <div class="form-group">
          <label>Payment Mode</label>
          <select name="payment">
            <option value="">— optional —</option>
            <c:forEach var="pm" items="${config.expensePaymentModes}">
              <option value="${pm}">${pm}</option>
            </c:forEach>
          </select>
        </div>
        <div class="form-group" style="grid-column:1/-1">
          <label>Note</label>
          <input type="text" name="note" placeholder="Optional note">
        </div>
      </div>
      <div id="expenseExtras"></div>
      <div class="flex gap-2 mt-2">
        <button type="button" class="btn btn-secondary" onclick="addExtraFieldRow('expenseExtras')">+ Custom Field</button>
        <button type="submit" class="btn btn-danger ml-auto">Save Expense</button>
      </div>
    </form>
  </div>
</div>

<!-- ── Add Category Modal ── -->
<div id="categoryModal" class="modal-overlay">
  <div class="modal">
    <div class="modal-header">
      <h3>Add Category / Column</h3>
      <button class="modal-close" onclick="closeModal('categoryModal')">✕</button>
    </div>
    <form action="${pageContext.request.contextPath}/categories" method="post">
      <input type="hidden" name="action" value="category" id="configAction">
      <div class="form-grid">
        <div class="form-group" style="grid-column:1/-1">
          <label>Name *</label>
          <input type="text" name="name" placeholder="Category or column name" required>
        </div>
        <div class="form-group" style="grid-column:1/-1">
          <label>Type</label>
          <select name="type">
            <option value="BOTH">Both Income &amp; Expense</option>
            <option value="I">Income only</option>
            <option value="E">Expense only</option>
          </select>
        </div>
      </div>
      <div class="flex gap-2 mt-2">
        <button type="button" class="btn btn-secondary"
          onclick="document.getElementById('configAction').value='column'">As Custom Column</button>
        <button type="submit" class="btn btn-primary ml-auto">Save</button>
      </div>
      <p style="color:var(--text-muted);font-size:0.75rem;margin-top:0.75rem">
        "As Custom Column" adds a new data column to all transactions. "Save" adds a category to the dropdown.
      </p>
    </form>
  </div>
</div>

<script>
// Pre-fill today's datetime on modal open
document.addEventListener('DOMContentLoaded', () => {
  const now = new Date();
  const local = new Date(now - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
  document.querySelectorAll('input[type="datetime-local"]').forEach(el => el.value = local);
});
</script>
