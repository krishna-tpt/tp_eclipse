<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>ExpenseIQ – Budget Management</title>
<link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.budget-hero{background:linear-gradient(135deg,#0f2027,#203a43,#2c5364);border-radius:20px;padding:32px 40px;margin-bottom:24px;display:flex;align-items:center;justify-content:space-between;gap:20px;position:relative;overflow:hidden}
.budget-hero::before{content:'';position:absolute;width:300px;height:300px;background:radial-gradient(circle,rgba(16,185,129,.2),transparent 70%);top:-80px;right:-40px;border-radius:50%}
.hero-text h2{font-family:'Syne',sans-serif;font-size:1.7rem;font-weight:800;color:#fff;margin:0 0 5px}
.hero-text p{color:rgba(255,255,255,.6);font-size:.9rem;margin:0}
.stats-row{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-bottom:24px}
.stat-card{background:var(--card-bg);border:1px solid var(--border);border-radius:14px;padding:18px 20px;display:flex;align-items:center;gap:12px}
.stat-icon{width:42px;height:42px;border-radius:11px;display:flex;align-items:center;justify-content:center;font-size:1.15rem;flex-shrink:0}
.si-blue{background:rgba(99,102,241,.15);color:#818cf8}
.si-red{background:rgba(239,68,68,.15);color:#ef4444}
.si-amber{background:rgba(245,158,11,.15);color:#f59e0b}
.si-green{background:rgba(16,185,129,.15);color:#10b981}
.stat-info label{font-size:.72rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.05em}
.stat-info span{display:block;font-size:1.3rem;font-weight:700;font-family:'Syne',sans-serif;color:var(--text-primary)}

/* Budget cards */
.budgets-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(320px,1fr));gap:16px;margin-bottom:28px}
.budget-card{background:var(--card-bg);border:1px solid var(--border);border-radius:16px;padding:22px;transition:.2s;position:relative}
.budget-card:hover{transform:translateY(-2px)}
.budget-card.budget-over{border-color:rgba(239,68,68,.4);box-shadow:0 0 20px rgba(239,68,68,.1)}
.budget-card.budget-alert{border-color:rgba(245,158,11,.4);box-shadow:0 0 20px rgba(245,158,11,.1)}
.bc-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px}
.bc-cat{font-family:'Syne',sans-serif;font-size:.95rem;font-weight:700;display:flex;align-items:center;gap:8px}
.bc-actions{display:flex;gap:6px}
.bca{width:26px;height:26px;border-radius:7px;border:none;cursor:pointer;display:flex;align-items:center;justify-content:center;font-size:.7rem;transition:.15s;text-decoration:none}
.bca:hover{transform:translateY(-1px)}
.bca-edit{background:rgba(99,102,241,.12);color:#818cf8}
.bca-del{background:rgba(239,68,68,.1);color:#f87171}

.bc-amounts{display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:10px}
.bc-spent{font-family:'Syne',sans-serif;font-size:1.4rem;font-weight:800}
.bc-limit{font-size:.82rem;color:var(--text-muted)}
.bc-remain{font-size:.8rem;font-weight:600}

/* Progress bar */
.progress-wrap{background:rgba(255,255,255,.06);border-radius:6px;height:8px;margin-bottom:10px;overflow:hidden}
.progress-fill{height:8px;border-radius:6px;transition:width .6s cubic-bezier(.34,1.56,.64,1)}
.progress-safe  .progress-fill{background:linear-gradient(90deg,#10b981,#34d399)}
.progress-warn  .progress-fill{background:linear-gradient(90deg,#f59e0b,#fbbf24)}
.progress-alert .progress-fill{background:linear-gradient(90deg,#f97316,#fb923c);animation:pulse-bar 1.5s ease-in-out infinite}
.progress-over  .progress-fill{background:linear-gradient(90deg,#ef4444,#f87171);animation:pulse-bar 1s ease-in-out infinite}
@keyframes pulse-bar{0%,100%{opacity:1}50%{opacity:.6}}

.bc-footer{display:flex;align-items:center;justify-content:space-between}
.status-chip{display:inline-flex;align-items:center;gap:5px;padding:3px 9px;border-radius:20px;font-size:.72rem;font-weight:600}
.budget-safe  .status-chip{background:rgba(16,185,129,.12);color:#10b981}
.budget-warn  .status-chip{background:rgba(245,158,11,.1);color:#fbbf24}
.budget-alert .status-chip{background:rgba(249,115,22,.15);color:#fb923c}
.budget-over  .status-chip{background:rgba(239,68,68,.15);color:#ef4444}
.alert-pct{font-size:.72rem;color:var(--text-muted)}

/* Month filter */
.month-filter{background:var(--card-bg);border:1px solid var(--border);border-radius:12px;padding:14px 20px;display:flex;gap:12px;align-items:center;margin-bottom:20px;flex-wrap:wrap}

/* Toast */
.notif-tray{position:fixed;top:72px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:10px;max-width:360px;pointer-events:none}
.toast{background:#1c1c2e;border:1px solid var(--border);border-radius:14px;overflow:hidden;box-shadow:0 12px 40px rgba(0,0,0,.5);animation:toastIn .35s cubic-bezier(.34,1.56,.64,1);pointer-events:all;display:flex;min-width:300px}
.toast-accent{width:4px;flex-shrink:0}
.toast-accent.budget_alert{background:#f97316}
.toast-accent.budget_over{background:#ef4444;animation:flash-acc .8s ease-in-out infinite}
@keyframes flash-acc{0%,100%{opacity:1}50%{opacity:.2}}
.toast-body{padding:12px 14px 12px 12px;flex:1}
.toast-head{display:flex;align-items:center;justify-content:space-between;margin-bottom:3px}
.toast-title{font-family:'Syne',sans-serif;font-size:.88rem;font-weight:700;color:#f0f0fa}
.toast-close{background:none;border:none;cursor:pointer;color:#5a5e78;font-size:.8rem}
.toast-msg{font-size:.78rem;color:#a9adc7;line-height:1.5}
@keyframes toastIn{from{transform:translateX(110%) scale(.9);opacity:0}to{transform:translateX(0) scale(1);opacity:1}}

.modal-overlay{display:none;position:fixed;inset:0;z-index:1000;background:rgba(0,0,0,.65);backdrop-filter:blur(4px);align-items:center;justify-content:center}
.modal-overlay.open{display:flex}
.modal-box{background:var(--card-bg);border:1px solid var(--border);border-radius:18px;width:100%;max-width:400px;padding:28px;animation:slideUp .22s ease}
@keyframes slideUp{from{transform:translateY(16px);opacity:0}to{transform:translateY(0);opacity:1}}
.modal-box h3{font-family:'Syne',sans-serif;font-size:1.1rem;font-weight:700;margin-bottom:8px}
.modal-actions{display:flex;gap:10px;justify-content:flex-end;margin-top:18px}
@media(max-width:900px){.stats-row{grid-template-columns:repeat(2,1fr)}.budgets-grid{grid-template-columns:1fr}}
</style>
</head>
<body>
<nav class="sidebar">
<div class="sidebar-brand"><span class="brand-icon">💰</span><span class="brand-name">ExpenseIQ</span></div>
<ul class="nav-links">
  <li><a href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-chart-pie"></i><span>Dashboard</span></a></li>
  <li><a href="${pageContext.request.contextPath}/income/list"><i class="fas fa-arrow-trend-up"></i><span>Income</span></a></li>
  <li><a href="${pageContext.request.contextPath}/expense/list"><i class="fas fa-arrow-trend-down"></i><span>Expenses</span></a></li>
  <li><a href="${pageContext.request.contextPath}/budget/list" class="active"><i class="fas fa-wallet"></i><span>Budget</span></a></li>
  <li><a href="${pageContext.request.contextPath}/budget/trends"><i class="fas fa-chart-line"></i><span>Trends</span></a></li>
  <li><a href="${pageContext.request.contextPath}/reports"><i class="fas fa-chart-bar"></i><span>Reports</span></a></li>
  <li><a href="${pageContext.request.contextPath}/reminders/list"><i class="fas fa-bell"></i><span>Reminders</span></a></li>
  <li><a href="${pageContext.request.contextPath}/backup/list"><i class="fas fa-database"></i><span>Backup</span></a></li>
</ul>
<div class="sidebar-footer"><span>v1.0.0</span></div>
</nav>
<main class="main-content">
<div class="topbar">
  <div class="page-title">Budget Management</div>
  <div class="topbar-actions">
    <a href="${pageContext.request.contextPath}/budget/trends" class="btn btn-secondary btn-sm"><i class="fas fa-chart-line"></i> Trend Analysis</a>
    <a href="${pageContext.request.contextPath}/budget/add"    class="btn btn-primary btn-sm"><i class="fas fa-plus"></i> Add Budget</a>
  </div>
</div>
<c:if test="${not empty sessionScope.successMsg}">
  <div class="alert alert-success" style="margin:14px 28px 0"><i class="fas fa-check-circle"></i> ${sessionScope.successMsg}</div>
  <c:remove var="successMsg" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.errorMsg}">
  <div class="alert alert-danger" style="margin:14px 28px 0"><i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMsg}</div>
  <c:remove var="errorMsg" scope="session"/>
</c:if>
<div class="page-body">

  <!-- Hero -->
  <div class="budget-hero">
    <div class="hero-text">
      <h2><i class="fas fa-wallet" style="color:#10b981"></i> &nbsp;Smart Budget Control</h2>
      <p>Category-wise budgets with real-time SSE alerts.<br>Know exactly where your money goes before it's gone.</p>
    </div>
    <a href="${pageContext.request.contextPath}/budget/add" class="btn btn-primary"><i class="fas fa-plus"></i> Set Budget</a>
  </div>

  <!-- Month filter -->
  <form method="get" action="${pageContext.request.contextPath}/budget/list" class="month-filter">
    <i class="fas fa-calendar" style="color:var(--text-muted)"></i>
    <div class="filter-group">
      <label>Year</label>
      <select name="year" class="form-control" style="min-width:90px">
        <c:forEach begin="2023" end="2027" var="y">
          <option value="${y}" ${selectedYear == y ? 'selected':''}>${y}</option>
        </c:forEach>
      </select>
    </div>
    <div class="filter-group">
      <label>Month</label>
      <select name="month" class="form-control" style="min-width:110px">
        <c:set var="mnames" value="January,February,March,April,May,June,July,August,September,October,November,December"/>
        <c:forTokens items="${mnames}" delims="," var="mn" varStatus="ms">
          <option value="${ms.index+1}" ${selectedMonth == ms.index+1 ? 'selected':''}>${mn}</option>
        </c:forTokens>
      </select>
    </div>
    <button type="submit" class="btn btn-primary btn-sm" style="align-self:flex-end"><i class="fas fa-filter"></i> View</button>
  </form>

  <!-- Stats -->
  <div class="stats-row">
    <div class="stat-card">
      <div class="stat-icon si-blue"><i class="fas fa-wallet"></i></div>
      <div class="stat-info"><label>Total Budget</label><span>₹<fmt:formatNumber value="${totalBudget}" pattern="#,##0"/></span></div>
    </div>
    <div class="stat-card">
      <div class="stat-icon si-amber"><i class="fas fa-receipt"></i></div>
      <div class="stat-info"><label>Total Spent</label><span>₹<fmt:formatNumber value="${totalSpent}" pattern="#,##0"/></span></div>
    </div>
    <div class="stat-card">
      <div class="stat-icon si-red"><i class="fas fa-triangle-exclamation"></i></div>
      <div class="stat-info"><label>Over Budget</label><span>${overCount}</span></div>
    </div>
    <div class="stat-card">
      <div class="stat-icon si-amber"><i class="fas fa-bell"></i></div>
      <div class="stat-info"><label>Near Limit</label><span>${alertCount}</span></div>
    </div>
  </div>

  <!-- Budget Cards -->
  <c:choose>
    <c:when test="${empty budgets}">
      <div class="empty-state">
        <i class="fas fa-wallet"></i>
        <p>No budgets for this period. <a href="${pageContext.request.contextPath}/budget/add" style="color:var(--accent)">Set your first budget →</a></p>
      </div>
    </c:when>
    <c:otherwise>
      <div class="budgets-grid">
        <c:forEach var="b" items="${budgets}">
        <c:set var="pct" value="${b.spentPct}"/>
        <div class="budget-card ${b.statusClass}">
          <div class="bc-header">
            <div class="bc-cat">
              <c:choose>
                <c:when test="${b.category == 'Food & Dining'}"><i class="fas fa-utensils" style="color:#f59e0b"></i></c:when>
                <c:when test="${b.category == 'Transport'}">    <i class="fas fa-car"      style="color:#818cf8"></i></c:when>
                <c:when test="${b.category == 'Health'}">       <i class="fas fa-heart-pulse" style="color:#ef4444"></i></c:when>
                <c:when test="${b.category == 'Entertainment'}"><i class="fas fa-tv"       style="color:#06b6d4"></i></c:when>
                <c:when test="${b.category == 'Shopping'}">     <i class="fas fa-bag-shopping" style="color:#a78bfa"></i></c:when>
                <c:when test="${b.category == 'Education'}">    <i class="fas fa-graduation-cap" style="color:#10b981"></i></c:when>
                <c:otherwise>                                   <i class="fas fa-tag"      style="color:#94a3b8"></i></c:otherwise>
              </c:choose>
              ${b.category}
            </div>
            <div class="bc-actions">
              <a href="${pageContext.request.contextPath}/budget/edit?id=${b.id}" class="bca bca-edit" title="Edit"><i class="fas fa-pen"></i></a>
              <button class="bca bca-del" title="Delete" onclick="confirmDelete(${b.id},'${b.category}')"><i class="fas fa-trash"></i></button>
            </div>
          </div>

          <div class="bc-amounts">
            <div>
              <div class="bc-spent ${b.overBudget ? 'text-danger' : ''}" style="color:${b.overBudget ? '#ef4444' : b.atAlert ? '#f97316' : '#f0f0fa'}">
                ₹<fmt:formatNumber value="${b.spent != null ? b.spent : 0}" pattern="#,##0.00"/>
              </div>
              <div class="bc-limit">of ₹<fmt:formatNumber value="${b.amount}" pattern="#,##0.00"/> budget</div>
            </div>
            <div class="bc-remain" style="color:${b.overBudget ? '#ef4444' : '#10b981'}">
              <c:choose>
                <c:when test="${b.overBudget}">
                  <i class="fas fa-circle-exclamation"></i> Over by<br>
                  ₹<fmt:formatNumber value="${(b.spent != null ? b.spent : 0) - b.amount}" pattern="#,##0"/>
                </c:when>
                <c:otherwise>
                  ₹<fmt:formatNumber value="${b.remaining}" pattern="#,##0"/><br>
                  <span style="font-size:.72rem;color:var(--text-muted)">remaining</span>
                </c:otherwise>
              </c:choose>
            </div>
          </div>

          <!-- Progress bar -->
          <div class="progress-wrap progress-${b.statusClass}">
            <div class="progress-fill" style="width:${pct > 100 ? 100 : pct}%"></div>
          </div>

          <div class="bc-footer">
            <span class="status-chip">
              <i class="fas ${b.overBudget ? 'fa-circle-exclamation' : b.atAlert ? 'fa-bell' : 'fa-circle-check'}" style="font-size:.6rem"></i>
              ${b.statusLabel}
            </span>
            <span class="alert-pct">Alert at ${b.alertAtPct}%</span>
          </div>
        </div>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>

</div><!-- /page-body -->
</main>

<!-- Toast tray -->
<div class="notif-tray" id="notifTray"></div>

<!-- Delete Modal -->
<div class="modal-overlay" id="deleteModal">
  <div class="modal-box">
    <h3 style="color:#ef4444"><i class="fas fa-trash"></i> Delete Budget</h3>
    <p>Delete budget for <strong id="delCat" style="color:var(--text-primary)"></strong>?</p>
    <form method="get" id="deleteForm">
      <div class="modal-actions">
        <button type="button" class="btn btn-secondary" onclick="document.getElementById('deleteModal').classList.remove('open')">Cancel</button>
        <button type="submit" class="btn btn-danger"><i class="fas fa-trash"></i> Delete</button>
      </div>
    </form>
  </div>
</div>

<script>
const CTX = '${pageContext.request.contextPath}';

function confirmDelete(id, cat) {
  document.getElementById('delCat').textContent = cat;
  document.getElementById('deleteForm').action = CTX + '/budget/delete?id=' + id;
  document.getElementById('deleteModal').classList.add('open');
}
document.getElementById('deleteModal').addEventListener('click', function(e){
  if(e.target===this) this.classList.remove('open');
});

/* SSE for real-time budget alerts */
function connectSSE() {
  const es = new EventSource(CTX + '/sse/stream');

  es.addEventListener('budget_alert', function(e) {
    const d = JSON.parse(e.data);
    showBudgetToast('budget_alert',
      'Budget Alert: ' + d.category,
      d.pct + '% spent — ₹' + parseFloat(d.spent).toLocaleString('en-IN') +
      ' of ₹' + parseFloat(d.limit).toLocaleString('en-IN') +
      ' | ₹' + parseFloat(d.remaining).toLocaleString('en-IN') + ' remaining');
  });

  es.addEventListener('budget_over', function(e) {
    const d = JSON.parse(e.data);
    showBudgetToast('budget_over',
      'OVER BUDGET: ' + d.category,
      'Spent ₹' + parseFloat(d.spent).toLocaleString('en-IN') +
      ' — exceeded by ₹' + (parseFloat(d.spent)-parseFloat(d.limit)).toLocaleString('en-IN'));
  });

  es.onerror = function() { setTimeout(connectSSE, 5000); };
}

function showBudgetToast(type, title, msg) {
  const tray  = document.getElementById('notifTray');
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.innerHTML =
    '<div class="toast-accent ' + type + '"></div>' +
    '<div class="toast-body">' +
      '<div class="toast-head">' +
        '<span class="toast-title">' + (type==='budget_over' ? '🚨 ' : '⚠️ ') + title + '</span>' +
        '<button class="toast-close" onclick="this.closest(\'.toast\').remove()"><i class="fas fa-xmark"></i></button>' +
      '</div>' +
      '<div class="toast-msg">' + msg + '</div>' +
    '</div>';
  tray.appendChild(toast);
  if (type !== 'budget_over') setTimeout(function(){ if(toast.parentNode) toast.remove(); }, 8000);

  // Native notification
  if (Notification.permission === 'granted') {
    new Notification((type==='budget_over'?'🚨 ':'⚠️ ') + title, { body: msg });
  }
}

if ('Notification' in window && Notification.permission === 'default') Notification.requestPermission();
connectSSE();

setTimeout(function(){
  document.querySelectorAll('.alert').forEach(function(a){
    a.style.transition='opacity .5s'; a.style.opacity='0';
    setTimeout(function(){ a.remove(); }, 500);
  });
}, 3500);
</script>
</body>
</html>
