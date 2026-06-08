<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Load all cashbooks for dropdown --%>
<%
  if (request.getAttribute("_allBooks") == null) {
    try {
      java.util.List<com.expensemanager.model.CashBook> allBooks =
          new com.expensemanager.dao.CashBookDAO().findAll();
      request.setAttribute("_allBooks", allBooks);
    } catch (Exception ignored) {}
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${pageTitle} — ExpenseOS</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
  <style>
    /* ── Book Switcher Dropdown ── */
    .book-switcher { position:relative; }
    .book-badge {
      display:flex; align-items:center; gap:.4rem;
      background:#eff6ff; border:1px solid #bfdbfe;
      color:var(--primary); padding:.32rem .8rem;
      border-radius:20px; font-size:.8rem; font-weight:600;
      cursor:pointer; white-space:nowrap; user-select:none;
      transition:all .15s;
    }
    .book-badge:hover { background:#dbeafe; border-color:#93c5fd; }
    .book-badge .dot  { width:8px;height:8px;border-radius:50%;background:var(--primary);flex-shrink:0; }
    .book-badge .arr  { font-size:.6rem; opacity:.6; transition:transform .2s; }
    .book-switcher.open .arr { transform:rotate(180deg); }

    .book-dropdown {
      position:absolute; top:calc(100% + 6px); left:0;
      background:#fff; border:1px solid var(--border);
      border-radius:10px; box-shadow:0 8px 24px rgba(0,0,0,.12);
      min-width:220px; z-index:500;
      opacity:0; pointer-events:none; transform:translateY(-6px);
      transition:opacity .15s, transform .15s;
    }
    .book-switcher.open .book-dropdown { opacity:1; pointer-events:all; transform:translateY(0); }

    .book-dropdown-header {
      padding:.6rem 1rem .4rem;
      font-size:.68rem; font-weight:700; text-transform:uppercase;
      letter-spacing:.8px; color:var(--text-2);
      border-bottom:1px solid var(--border);
    }
    .book-dropdown-item {
      display:flex; align-items:center; gap:.6rem;
      padding:.6rem 1rem; cursor:pointer;
      font-size:.875rem; font-weight:500;
      text-decoration:none; color:var(--text);
      transition:background .1s;
    }
    .book-dropdown-item:hover { background:#f8fafc; }
    .book-dropdown-item.active { color:var(--primary); background:#eff6ff; }
    .book-dropdown-item .bk-dot {
      width:8px;height:8px;border-radius:50%;
      background:var(--border); flex-shrink:0;
    }
    .book-dropdown-item.active .bk-dot { background:var(--primary); }
    .book-dropdown-divider { border:none; border-top:1px solid var(--border); margin:.25rem 0; }
    .book-dropdown-footer {
      padding:.5rem 1rem .6rem;
    }
  </style>
</head>
<body>

<nav class="navbar">
  <a class="nav-brand" href="${pageContext.request.contextPath}/books">
    <span>&#128200;</span> ExpenseOS
  </a>

  <%-- CashBook Switcher Dropdown --%>
  <div class="book-switcher" id="bookSwitcher">
    <div class="book-badge" onclick="toggleBookDropdown()">
      <span class="dot"></span>
      <span>${not empty sessionScope.activeBookName ? sessionScope.activeBookName : 'Select Book'}</span>
      <span class="arr">&#9660;</span>
    </div>
    <div class="book-dropdown" id="bookDropdown">
      <div class="book-dropdown-header">Switch Cash Book</div>
      <c:forEach var="bk" items="${_allBooks}">
        <a href="${pageContext.request.contextPath}/books?select=${bk.id}"
           class="book-dropdown-item ${sessionScope.activeBookId == bk.id ? 'active' : ''}">
          <span class="bk-dot"></span>
          ${bk.name}
          <c:if test="${sessionScope.activeBookId == bk.id}">
            <span style="margin-left:auto;font-size:.7rem;color:var(--primary)">&#10003;</span>
          </c:if>
        </a>
      </c:forEach>
      <hr class="book-dropdown-divider">
      <div class="book-dropdown-footer">
        <a href="${pageContext.request.contextPath}/books" class="btn btn-outline btn-sm full-w"
           style="justify-content:center">+ Manage Books</a>
      </div>
    </div>
  </div>

  <div class="nav-links">
    <a href="${pageContext.request.contextPath}/home"
       class="${activePage=='home'?'active':''}">Dashboard</a>
    <a href="${pageContext.request.contextPath}/transactions"
       class="${activePage=='txn'?'active':''}">Transactions</a>
    <a href="${pageContext.request.contextPath}/reports"
       class="${activePage=='reports'?'active':''}">Reports</a>
    <a href="${pageContext.request.contextPath}/books"
       class="${activePage=='books'?'active':''}">Books</a>
  </div>

  <a href="${pageContext.request.contextPath}/settings"
     class="btn btn-outline btn-sm">&#9881; Settings</a>
</nav>

<script>
function toggleBookDropdown() {
  document.getElementById('bookSwitcher').classList.toggle('open');
}
// Close on outside click
document.addEventListener('click', function(e) {
  var sw = document.getElementById('bookSwitcher');
  if (sw && !sw.contains(e.target)) sw.classList.remove('open');
});
</script>

<div class="page">
