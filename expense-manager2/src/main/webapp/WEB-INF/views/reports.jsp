<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="pageTitle" value="Reports" scope="request" />
<c:set var="activePage" value="reports" scope="request" />
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>"
	scope="request" />
<%@ include file="header.jsp"%>

<script
	src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<div class="page-header">
	<h1>Reports &amp; Analytics</h1>
	<p>Last 6 months financial overview</p>
</div>

<c:if test="${not empty dbError}">
	<div class="alert alert-error">✗ ${dbError}</div>
</c:if>

<!-- Summary -->
<div class="stats-grid">
	<div class="stat-card">
		<div class="stat-label">Total Income</div>
		<div class="stat-value income">
			₹
			<fmt:formatNumber value="${totalIncome}" pattern="#,##0.00" />
		</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Total Expenses</div>
		<div class="stat-value expense">
			₹
			<fmt:formatNumber value="${totalExpense}" pattern="#,##0.00" />
		</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Net Balance</div>
		<div class="stat-value balance">
			₹
			<fmt:formatNumber value="${balance}" pattern="#,##0.00" />
		</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Savings Rate</div>
		<div class="stat-value balance">
			<c:choose>
				<c:when test="${totalIncome > 0}">
					<fmt:formatNumber value="${(balance/totalIncome)*100}"
						pattern="#0.0" />%
        </c:when>
				<c:otherwise>—</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>

<!-- Monthly trend (full width) -->
<div class="chart-card mb-2">
	<div class="chart-title">Monthly Income vs Expense (Last 6
		Months)</div>
	<div class="chart-wrap" style="height: 260px">
		<canvas id="monthlyChart"></canvas>
	</div>
</div>

<!-- Category charts -->
<div class="charts-grid">
	<div class="chart-card">
		<div class="chart-title">Expense by Category</div>
		<div class="chart-wrap">
			<canvas id="expCatChart"></canvas>
		</div>
	</div>
	<div class="chart-card">
		<div class="chart-title">Income by Category</div>
		<div class="chart-wrap">
			<canvas id="incCatChart"></canvas>
		</div>
	</div>
</div>

<script>
const monthly  = ${monthlyJson};
const expCat   = ${expCatJson};
const incCat   = ${incCatJson};

Chart.defaults.font.family = "'Inter', system-ui, sans-serif";
Chart.defaults.color = '#64748b';
Chart.defaults.borderColor = '#e2e8f0';

const palette = ['#2563eb','#16a34a','#dc2626','#d97706','#7c3aed','#0891b2','#be185d','#059669'];

// Monthly bar chart
new Chart(document.getElementById('monthlyChart'), {
  type: 'bar',
  data: {
    labels: monthly.map(m => m.month),
    datasets: [
      { label: 'Income',  data: monthly.map(m => m.income),  backgroundColor: '#dcfce7', borderColor: '#16a34a', borderWidth: 2, borderRadius: 4 },
      { label: 'Expense', data: monthly.map(m => m.expense), backgroundColor: '#fee2e2', borderColor: '#dc2626', borderWidth: 2, borderRadius: 4 }
    ]
  },
  options: {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { position: 'top' } },
    scales: {
      x: { grid: { display: false } },
      y: { ticks: { callback: v => '₹' + Number(v).toLocaleString('en-IN') } }
    }
  }
});

// Expense donut
if (expCat.length > 0) {
  new Chart(document.getElementById('expCatChart'), {
    type: 'doughnut',
    data: {
      labels: expCat.map(e => e.name),
      datasets: [{ data: expCat.map(e => e.total), backgroundColor: palette, borderWidth: 2, borderColor: '#fff' }]
    },
    options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right', labels: { font: { size: 11 } } } } }
  });
}

// Income donut
if (incCat.length > 0) {
  new Chart(document.getElementById('incCatChart'), {
    type: 'doughnut',
    data: {
      labels: incCat.map(e => e.name),
      datasets: [{ data: incCat.map(e => e.total), backgroundColor: palette.slice(3), borderWidth: 2, borderColor: '#fff' }]
    },
    options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right', labels: { font: { size: 11 } } } } }
  });
}
</script>

<%@ include file="footer.jsp"%>
