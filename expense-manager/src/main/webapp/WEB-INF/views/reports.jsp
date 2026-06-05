<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Reports & Analytics" scope="request"/>
<c:set var="activePage" value="reports" scope="request"/>
<%@ include file="header.jsp" %>

<!-- Chart.js CDN -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<div class="page-header">
  <h1>Reports &amp; Analytics</h1>
  <p>Visualize your spending and income patterns</p>
</div>

<c:if test="${not empty error}">
  <div class="not-loaded-banner">
    <h3>⚠ Workbook not loaded</h3>
    <p>Please sync from WorkDrive first.</p>
    <button class="btn btn-amber" onclick="syncNow()">⟳ Sync</button>
  </div>
</c:if>

<c:if test="${empty error}">
  <!-- Summary Row -->
  <div class="stats-grid">
    <div class="stat-card income">
      <div class="stat-label">Total Income</div>
      <div class="stat-value income">₹<fmt:formatNumber value="${totalIncome}" pattern="#,##0.00"/></div>
    </div>
    <div class="stat-card expense">
      <div class="stat-label">Total Expenses</div>
      <div class="stat-value expense">₹<fmt:formatNumber value="${totalExpense}" pattern="#,##0.00"/></div>
    </div>
    <div class="stat-card balance">
      <div class="stat-label">Net Balance</div>
      <div class="stat-value balance">₹<fmt:formatNumber value="${balance}" pattern="#,##0.00"/></div>
    </div>
  </div>

  <!-- Charts Row 1 -->
  <div class="charts-grid">
    <!-- Monthly Trend -->
    <div class="chart-card" style="grid-column: 1 / -1">
      <div class="chart-title">Monthly Income vs Expense</div>
      <div class="chart-container" style="height:260px">
        <canvas id="monthlyChart"></canvas>
      </div>
    </div>
  </div>

  <!-- Charts Row 2 -->
  <div class="charts-grid">
    <div class="chart-card">
      <div class="chart-title">Expense by Category</div>
      <div class="chart-container">
        <canvas id="expCategoryChart"></canvas>
      </div>
    </div>
    <div class="chart-card">
      <div class="chart-title">Income by Category</div>
      <div class="chart-container">
        <canvas id="incCategoryChart"></canvas>
      </div>
    </div>
    <div class="chart-card">
      <div class="chart-title">Payment Mode Breakdown</div>
      <div class="chart-container">
        <canvas id="paymentChart"></canvas>
      </div>
    </div>
  </div>
</c:if>

<script>
// Data from server
const monthLabels  = ${monthLabelsJson};
const monthIncome  = ${monthIncomeJson};
const monthExpense = ${monthExpenseJson};
const expByCategory = ${expByCategoryJson};
const incByCategory = ${incByCategoryJson};
const payByMode     = ${payByModeJson};

// Chart defaults
Chart.defaults.color = '#7a8aab';
Chart.defaults.borderColor = '#1e2a3d';
Chart.defaults.font.family = "'Space Grotesk', sans-serif";

const palette = ['#00c9a7','#4d9cff','#f6a623','#ff5c72','#9b59b6','#00d68f','#e74c3c','#3498db'];

// Monthly Bar Chart
new Chart(document.getElementById('monthlyChart'), {
  type: 'bar',
  data: {
    labels: monthLabels,
    datasets: [
      { label: 'Income',  data: monthIncome,  backgroundColor: 'rgba(0,214,143,0.7)', borderRadius: 5 },
      { label: 'Expense', data: monthExpense, backgroundColor: 'rgba(255,92,114,0.7)', borderRadius: 5 }
    ]
  },
  options: {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { position: 'top' } },
    scales: {
      x: { grid: { color: '#1e2a3d' } },
      y: { grid: { color: '#1e2a3d' }, ticks: { callback: v => '₹' + v.toLocaleString('en-IN') } }
    }
  }
});

// Expense by Category - Doughnut
new Chart(document.getElementById('expCategoryChart'), {
  type: 'doughnut',
  data: {
    labels: Object.keys(expByCategory),
    datasets: [{ data: Object.values(expByCategory), backgroundColor: palette, borderWidth: 1, borderColor: '#111622' }]
  },
  options: {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { position: 'right', labels: { font: { size: 11 } } } }
  }
});

// Income by Category - Doughnut
new Chart(document.getElementById('incCategoryChart'), {
  type: 'doughnut',
  data: {
    labels: Object.keys(incByCategory),
    datasets: [{ data: Object.values(incByCategory), backgroundColor: palette.slice(3), borderWidth: 1, borderColor: '#111622' }]
  },
  options: {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { position: 'right', labels: { font: { size: 11 } } } }
  }
});

// Payment Mode - Polar Area
new Chart(document.getElementById('paymentChart'), {
  type: 'polarArea',
  data: {
    labels: Object.keys(payByMode),
    datasets: [{ data: Object.values(payByMode), backgroundColor: palette.map(c => c + 'bb'), borderColor: '#1e2a3d', borderWidth: 1 }]
  },
  options: {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { position: 'right', labels: { font: { size: 11 } } } },
    scales: { r: { grid: { color: '#1e2a3d' }, ticks: { display: false } } }
  }
});
</script>

<%@ include file="footer.jsp" %>
