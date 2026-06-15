<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>ExpenseIQ – ${not empty budget ? 'Edit' : 'Add'} Budget</title>
<link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.preview-bar-wrap{background:rgba(255,255,255,.06);border-radius:8px;height:12px;overflow:hidden;margin:10px 0 6px}
.preview-bar-fill{height:12px;border-radius:8px;transition:width .4s,background .3s}
.slider-wrap{display:flex;align-items:center;gap:12px}
input[type=range]{flex:1;accent-color:#6366f1;height:4px}
.slider-val{font-family:'Syne',sans-serif;font-weight:700;font-size:1rem;color:#818cf8;min-width:40px;text-align:right}
.preview-card{background:rgba(99,102,241,.06);border:1px solid rgba(99,102,241,.2);border-radius:12px;padding:16px 20px;margin-top:20px}
.preview-card h4{font-family:'Syne',sans-serif;font-size:.88rem;font-weight:700;margin-bottom:12px;color:var(--text-secondary)}
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
  <div class="page-title">${not empty budget ? 'Edit' : 'Add'} Budget</div>
  <a href="${pageContext.request.contextPath}/budget/list" class="btn btn-secondary btn-sm"><i class="fas fa-arrow-left"></i> Back</a>
</div>
<div class="page-body">
<div class="form-card" style="max-width:640px">
  <div style="display:flex;align-items:center;gap:14px;padding-bottom:20px;border-bottom:1px solid var(--border);margin-bottom:24px">
    <div style="width:46px;height:46px;border-radius:13px;background:rgba(16,185,129,.15);color:#10b981;display:flex;align-items:center;justify-content:center;font-size:1.3rem">
      <i class="fas fa-wallet"></i>
    </div>
    <div>
      <h2 style="font-family:'Syne',sans-serif;font-size:1.2rem;font-weight:800;margin:0">${not empty budget ? 'Edit' : 'New'} Budget</h2>
      <p style="color:var(--text-muted);font-size:.82rem;margin:2px 0 0">Set spending limit with real-time SSE alerts</p>
    </div>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/budget/save">
    <c:if test="${not empty budget}">
      <input type="hidden" name="id" value="${budget.id}">
    </c:if>

    <div class="form-grid">
      <div class="form-group">
        <label>Category <span style="color:#f87171">*</span></label>
        <select name="category" class="form-control" id="catSel" required>
          <c:set var="cats" value="Food &amp; Dining,Transport,Shopping,Entertainment,Health,Education,Utilities,Rent,Insurance,Travel,Personal Care,Loans,Other"/>
          <c:forTokens items="${cats}" delims="," var="cat">
            <option value="${cat}" ${not empty budget && budget.category == cat ? 'selected':''}>${cat}</option>
          </c:forTokens>
        </select>
      </div>
      <div class="form-group">
        <label>Budget Amount (₹) <span style="color:#f87171">*</span></label>
        <input type="number" name="amount" class="form-control" id="amtInput"
               required step="100" min="100" placeholder="5000"
               value="${not empty budget ? budget.amount : ''}">
      </div>
      <div class="form-group">
        <label>Period</label>
        <select name="period" class="form-control" id="periodSel">
          <c:forEach var="p" items="${periods}">
            <option value="${p}" ${not empty budget && budget.period == p ? 'selected' : p.name() == 'MONTHLY' ? 'selected' : ''}>${p}</option>
          </c:forEach>
        </select>
      </div>
      <div class="form-group">
        <label>Year</label>
        <select name="year" class="form-control">
          <c:forEach begin="2024" end="2027" var="y">
            <option value="${y}" ${not empty budget && budget.year == y ? 'selected' : y == currentYear ? 'selected' : ''}>${y}</option>
          </c:forEach>
        </select>
      </div>
      <div class="form-group" id="monthGroup">
        <label>Month</label>
        <select name="month" class="form-control">
          <c:set var="mnames" value="January,February,March,April,May,June,July,August,September,October,November,December"/>
          <c:forTokens items="${mnames}" delims="," var="mn" varStatus="ms">
            <option value="${ms.index+1}" ${not empty budget && budget.month == ms.index+1 ? 'selected' : ms.index+1 == currentMonth ? 'selected' : ''}>${mn}</option>
          </c:forTokens>
        </select>
      </div>
    </div>

    <!-- Alert threshold slider -->
    <div class="form-group">
      <label>Alert Threshold — notify when <span id="sliderLabel">${not empty budget ? budget.alertAtPct : 80}</span>% is spent</label>
      <div class="slider-wrap">
        <span style="font-size:.78rem;color:var(--text-muted)">50%</span>
        <input type="range" name="alertAtPct" id="alertSlider" min="50" max="100" step="5"
               value="${not empty budget ? budget.alertAtPct : 80}"
               oninput="updateSlider(this.value)">
        <span class="slider-val" id="sliderVal">${not empty budget ? budget.alertAtPct : 80}%</span>
      </div>

      <!-- Live preview bar -->
      <div style="margin-top:14px;padding:14px 16px;background:rgba(255,255,255,.03);border-radius:10px;border:1px solid var(--border)">
        <div style="display:flex;justify-content:space-between;font-size:.78rem;color:var(--text-muted);margin-bottom:6px">
          <span>₹0</span>
          <span id="prevAlertLbl" style="color:#f59e0b">Alert at <span id="prevAlertPct">80</span>%</span>
          <span id="prevBudgetAmt">₹5,000</span>
        </div>
        <div class="preview-bar-wrap">
          <div class="preview-bar-fill" id="previewBar" style="width:0%;background:#10b981"></div>
        </div>
        <div style="font-size:.75rem;color:var(--text-muted);margin-top:4px" id="previewNote">
          SSE alert fires when spending crosses <strong id="previewAlertAmt">₹4,000</strong>
        </div>
      </div>
    </div>

    <div style="display:flex;gap:12px;margin-top:4px">
      <button type="submit" class="btn btn-primary">
        <i class="fas fa-wallet"></i> ${not empty budget ? 'Update' : 'Create'} Budget
      </button>
      <a href="${pageContext.request.contextPath}/budget/list" class="btn btn-secondary">
        <i class="fas fa-xmark"></i> Cancel
      </a>
    </div>
  </form>
</div>
</div>
</main>

<script>
function updateSlider(val) {
  document.getElementById('sliderLabel').textContent = val;
  document.getElementById('sliderVal').textContent   = val + '%';
  document.getElementById('prevAlertPct').textContent = val;
  updatePreview();
}

function updatePreview() {
  const amt   = parseFloat(document.getElementById('amtInput').value) || 5000;
  const pct   = parseInt(document.getElementById('alertSlider').value) || 80;
  const alert = amt * pct / 100;

  document.getElementById('prevBudgetAmt').textContent  = '₹' + amt.toLocaleString('en-IN');
  document.getElementById('previewAlertAmt').textContent = '₹' + alert.toLocaleString('en-IN',{minimumFractionDigits:0});

  // Animate bar to alert position
  const bar = document.getElementById('previewBar');
  bar.style.width      = pct + '%';
  bar.style.background = pct >= 90 ? '#ef4444' : pct >= 80 ? '#f97316' : '#f59e0b';
}

document.getElementById('amtInput').addEventListener('input', updatePreview);

// Show/hide month based on period
document.getElementById('periodSel').addEventListener('change', function() {
  document.getElementById('monthGroup').style.display = this.value === 'YEARLY' ? 'none' : '';
});

window.addEventListener('DOMContentLoaded', function() {
  updatePreview();
  if (document.getElementById('periodSel').value === 'YEARLY') {
    document.getElementById('monthGroup').style.display = 'none';
  }
});
</script>
</body>
</html>
