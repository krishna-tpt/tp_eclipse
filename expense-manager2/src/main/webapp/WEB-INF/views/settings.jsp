<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle"  value="Settings"  scope="request"/>
<c:set var="activePage" value="settings"  scope="request"/>
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>" scope="request"/>
<%@ include file="header.jsp" %>

<div class="page-header">
  <h1>&#9881; Settings</h1>
  <p>Manage categories, sub-categories, and custom columns</p>
</div>

<c:if test="${not empty param.msg}">
  <div class="alert alert-success">&#10003; Saved!</div>
</c:if>
<c:if test="${not empty dbError}">
  <div class="alert alert-error">&#10007; ${dbError}</div>
</c:if>

<div class="tabs">
  <button class="tab active" onclick="switchTab('catTab', this)">Categories</button>
  <button class="tab"        onclick="switchTab('subcatTab', this)">Sub Categories</button>
  <button class="tab"        onclick="switchTab('colTab', this)">Custom Columns</button>
</div>

<!-- ═══ CATEGORIES ═══ -->
<div id="catTab" class="tab-panel">
  <div style="display:grid;grid-template-columns:1fr 1fr;gap:1.5rem">

    <div class="card">
      <div class="card-title">Income Categories</div>
      <form action="${pageContext.request.contextPath}/settings" method="post"
            class="flex gap-1 mb-2" onsubmit="return this.name.value.trim()!==''">
        <input type="hidden" name="action" value="addCategory">
        <input type="hidden" name="type"   value="INCOME">
        <input type="text"   name="name"   placeholder="New income category"
               class="full-w" required
               onkeydown="if(event.key==='Enter'){event.preventDefault();this.closest('form').submit();}">
        <button type="submit" class="btn btn-success btn-sm" style="white-space:nowrap">+ Add</button>
      </form>
      <div class="table-wrap">
        <table><tbody>
          <c:forEach var="cat" items="${incomeCategories}">
            <tr>
              <td>${cat.name}</td>
              <td class="text-right">
                <form action="${pageContext.request.contextPath}/settings" method="post" style="display:inline">
                  <input type="hidden" name="action" value="deleteCategory">
                  <input type="hidden" name="id"     value="${cat.id}">
                  <button class="btn btn-danger btn-sm"
                          onclick="return confirm('Delete \'${cat.name}\'?')">&#x2715;</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty incomeCategories}">
            <tr><td class="empty-state">No income categories</td></tr>
          </c:if>
        </tbody></table>
      </div>
    </div>

    <div class="card">
      <div class="card-title">Expense Categories</div>
      <form action="${pageContext.request.contextPath}/settings" method="post"
            class="flex gap-1 mb-2">
        <input type="hidden" name="action" value="addCategory">
        <input type="hidden" name="type"   value="EXPENSE">
        <input type="text"   name="name"   placeholder="New expense category"
               class="full-w" required
               onkeydown="if(event.key==='Enter'){event.preventDefault();this.closest('form').submit();}">
        <button type="submit" class="btn btn-success btn-sm" style="white-space:nowrap">+ Add</button>
      </form>
      <div class="table-wrap">
        <table><tbody>
          <c:forEach var="cat" items="${expenseCategories}">
            <tr>
              <td>${cat.name}</td>
              <td class="text-right">
                <form action="${pageContext.request.contextPath}/settings" method="post" style="display:inline">
                  <input type="hidden" name="action" value="deleteCategory">
                  <input type="hidden" name="id"     value="${cat.id}">
                  <button class="btn btn-danger btn-sm"
                          onclick="return confirm('Delete \'${cat.name}\'?')">&#x2715;</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty expenseCategories}">
            <tr><td class="empty-state">No expense categories</td></tr>
          </c:if>
        </tbody></table>
      </div>
    </div>
  </div>
</div>

<!-- ═══ SUB CATEGORIES ═══ -->
<div id="subcatTab" class="tab-panel" style="display:none">
  <div class="card">
    <div class="card-title">Add Sub Category</div>
    <form action="${pageContext.request.contextPath}/settings" method="post"
          class="form-grid mb-2">
      <input type="hidden" name="action" value="addSubCategory">
      <div class="form-group">
        <label>Parent Category *</label>
        <select name="categoryId" required>
          <option value="">Select&#8230;</option>
          <optgroup label="Income">
            <c:forEach var="cat" items="${incomeCategories}">
              <option value="${cat.id}">${cat.name}</option>
            </c:forEach>
          </optgroup>
          <optgroup label="Expense">
            <c:forEach var="cat" items="${expenseCategories}">
              <option value="${cat.id}">${cat.name}</option>
            </c:forEach>
          </optgroup>
        </select>
      </div>
      <div class="form-group">
        <label>Sub Category Name *</label>
        <input type="text" name="name" placeholder="e.g. Dinner" required
               onkeydown="if(event.key==='Enter'){event.preventDefault();this.closest('form').submit();}">
      </div>
      <div class="form-group" style="justify-content:flex-end;padding-top:1.4rem">
        <button type="submit" class="btn btn-primary">+ Add</button>
      </div>
    </form>

    <div class="table-wrap">
      <table>
        <thead><tr><th>Sub Category</th><th>Parent Category</th><th></th></tr></thead>
        <tbody>
          <c:forEach var="sc" items="${allSubCategories}">
            <tr>
              <td>${sc.name}</td>
              <td>
                <%-- Find parent name from combined list --%>
                <span class="chip">
                  <c:forEach var="cat" items="${incomeCategories}">
                    <c:if test="${cat.id == sc.category_id}">${cat.name} (I)</c:if>
                  </c:forEach>
                  <c:forEach var="cat" items="${expenseCategories}">
                    <c:if test="${cat.id == sc.category_id}">${cat.name} (E)</c:if>
                  </c:forEach>
                </span>
              </td>
              <td class="text-right">
                <form action="${pageContext.request.contextPath}/settings" method="post" style="display:inline">
                  <input type="hidden" name="action" value="deleteSubCategory">
                  <input type="hidden" name="id"     value="${sc.id}">
                  <button class="btn btn-danger btn-sm"
                          onclick="return confirm('Delete \'${sc.name}\'?')">&#x2715;</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty allSubCategories}">
            <tr><td colspan="3" class="empty-state">No sub-categories yet</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- ═══ CUSTOM COLUMNS ═══ -->
<div id="colTab" class="tab-panel" style="display:none">
  <div style="display:grid;grid-template-columns:1fr 1fr;gap:1.5rem">

    <div class="card">
      <div class="card-title">Income Custom Columns</div>
      <form action="${pageContext.request.contextPath}/settings" method="post" class="flex gap-1 mb-2">
        <input type="hidden" name="action" value="addColumn">
        <input type="hidden" name="type"   value="INCOME">
        <input type="text"   name="colName" placeholder="Column name" class="full-w" required
               onkeydown="if(event.key==='Enter'){event.preventDefault();this.closest('form').submit();}">
        <button type="submit" class="btn btn-success btn-sm" style="white-space:nowrap">+ Add</button>
      </form>
      <div class="table-wrap"><table><tbody>
        <c:forEach var="col" items="${incomeColumns}">
          <tr>
            <td>${col.colName}</td>
            <td class="text-muted" style="font-size:.75rem">${col.colKey}</td>
            <td class="text-right">
              <form action="${pageContext.request.contextPath}/settings" method="post" style="display:inline">
                <input type="hidden" name="action" value="deleteColumn">
                <input type="hidden" name="id"     value="${col.id}">
                <button class="btn btn-danger btn-sm"
                        onclick="return confirm('Delete column \'${col.colName}\'? All values will be lost.')">&#x2715;</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty incomeColumns}">
          <tr><td colspan="3" class="empty-state">No custom columns</td></tr>
        </c:if>
      </tbody></table></div>
    </div>

    <div class="card">
      <div class="card-title">Expense Custom Columns</div>
      <form action="${pageContext.request.contextPath}/settings" method="post" class="flex gap-1 mb-2">
        <input type="hidden" name="action" value="addColumn">
        <input type="hidden" name="type"   value="EXPENSE">
        <input type="text"   name="colName" placeholder="Column name" class="full-w" required
               onkeydown="if(event.key==='Enter'){event.preventDefault();this.closest('form').submit();}">
        <button type="submit" class="btn btn-success btn-sm" style="white-space:nowrap">+ Add</button>
      </form>
      <div class="table-wrap"><table><tbody>
        <c:forEach var="col" items="${expenseColumns}">
          <tr>
            <td>${col.colName}</td>
            <td class="text-muted" style="font-size:.75rem">${col.colKey}</td>
            <td class="text-right">
              <form action="${pageContext.request.contextPath}/settings" method="post" style="display:inline">
                <input type="hidden" name="action" value="deleteColumn">
                <input type="hidden" name="id"     value="${col.id}">
                <button class="btn btn-danger btn-sm"
                        onclick="return confirm('Delete column \'${col.colName}\'?')">&#x2715;</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty expenseColumns}">
          <tr><td colspan="3" class="empty-state">No custom columns</td></tr>
        </c:if>
      </tbody></table></div>
    </div>
  </div>
</div>

<script>
function switchTab(tabId, btn) {
  document.querySelectorAll('.tab-panel').forEach(p => p.style.display = 'none');
  document.querySelectorAll('.tab').forEach(b => b.classList.remove('active'));
  document.getElementById(tabId).style.display = 'block';
  btn.classList.add('active');
}
</script>

<%@ include file="footer.jsp" %>
