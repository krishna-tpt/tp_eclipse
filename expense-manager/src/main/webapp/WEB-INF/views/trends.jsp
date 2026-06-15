<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>ExpenseIQ – Trend Analysis</title>
<link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.trend-hero{background:linear-gradient(135deg,#0f0c29,#302b63,#24243e);border-radius:20px;padding:32px 40px;margin-bottom:24px;display:flex;align-items:center;justify-content:space-between;gap:20px;position:relative;overflow:hidden}
.trend-hero::before{content:'';position:absolute;width:300px;height:300px;background:radial-gradient(circle,rgba(99,102,241,.25),transparent 70%);top:-80px;right:-40px;border-radius:50%}
.hero-text h2{font-family:'Syne',sans-serif;font-size:1.7rem;font-weight:800;color:#fff;margin:0 0 5px}
.hero-text p{color:rgba(255,255,255,.6);font-size:.9rem;margin:0}
.filter-bar{background:var(--card-bg);border:1px solid var(--border);border-radius:12px;padding:14px 20px;display:flex;gap:14px;align-items:flex-end;flex-wrap:wrap;margin-bottom:22px}
.charts-2col{display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-bottom:22px}
.chart-card{background:var(--card-bg);border:1px solid var(--border);border-radius:16px;padding:22px}
.chart-card h4{font-family:'Syne',sans-serif;font-size:.92rem;font-weight:700;margin-bottom:16px;display:flex;align-items:center;gap:8px}
.chart-container{position:relative;height:260px}
.chart-full{background:var(--card-bg);border:1px solid var(--border);border-radius:16px;padding:22px;margin-bottom:22px}
.chart-full h4{font-family:'Syne',sans-serif;font-size:.92rem;font-weight:700;margin-bottom:16px;display:flex;align-items:center;gap:8px}
.chart-container-lg{position:relative;height:300px}

/* Growth table */
.growth-table{width:100%;border-collapse:collapse}
.growth-table th{padding:9px 14px;text-align:left;font-size:.7rem;text-transform:uppercase;letter-spacing:.07em;color:var(--text-muted);border-bottom:1px solid var(--border)}
.growth-table td{padding:12px 14px;border-bottom:1px solid var(--border);font-size:.86rem}
.growth-table tr:last-child td{border-bottom:none}
.growth-table tr:hover td{background:rgba(99,102,241,.04)}
.pct-chip{display:inline-flex;align-items:center;gap:4px;padding:2px 8px;border-radius:6px;font-size:.74rem;font-weight:600}
.pct-up{background:rgba(239,68,68,.12);color:#ef4444}
.pct-down{background:rgba(16,185,129,.12);color:#10b981}
.pct-flat{background:rgba(148,163,184,.1);color:#94a3b8}

/* Summary KPI row */
.kpi-row{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-bottom:22px}
.kpi-card{background:var(--card-bg);border:1px solid var(--border);border-radius:14px;padding:16px 18px;text-align:center}
.kpi-val{font-family:'Syne',sans-serif;font-size:1.4rem;font-weight:800;color:var(--text-primary);margin-bottom:2px}
.kpi-label{font-size:.72rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:.05em}

.spinner{display:flex;align-items:center;justify-content:center;height:260px;color:var(--text-muted);font-size:.85rem;gap:10px}
@media(max-width:900px){.charts-2col{grid-template-columns:1fr}.kpi-row{grid-template-columns:repeat(2,1fr)}}
</style>
</head>
<body>
<nav class="sidebar">
<div class="sidebar-brand"><span class="brand-icon">💰</span><span class="brand-name">ExpenseIQ</span></div>
<ul class="nav-links">
  <li><a href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-chart-pie"></i><span>Dashboard</span></a></li>
  <li><a href="${pageContext.request.contextPath}/income/list"><i class="fas fa-arrow-trend-up"></i><span>Income</span></a></li>
  <li><a href="${pageContext.request.contextPath}/expense/list"><i class="fas fa-arrow-trend-down"></i><span>Expenses</span></a></li>
  <li><a href="${pageContext.request.contextPath}/budget/list"><i class="fas fa-wallet"></i><span>Budget</span></a></li>
  <li><a href="${pageContext.request.contextPath}/budget/trends" class="active"><i class="fas fa-chart-line"></i><span>Trends</span></a></li>
  <li><a href="${pageContext.request.contextPath}/reports"><i class="fas fa-chart-bar"></i><span>Reports</span></a></li>
  <li><a href="${pageContext.request.contextPath}/reminders/list"><i class="fas fa-bell"></i><span>Reminders</span></a></li>
  <li><a href="${pageContext.request.contextPath}/backup/list"><i class="fas fa-database"></i><span>Backup</span></a></li>
</ul>
<div class="sidebar-footer"><span>v1.0.0</span></div>
</nav>
<main class="main-content">
<div class="topbar">
  <div class="page-title">Trend Analysis</div>
  <a href="${pageContext.request.contextPath}/budget/list" class="btn btn-secondary btn-sm"><i class="fas fa-wallet"></i> Budgets</a>
</div>
<div class="page-body">

  <!-- Hero -->
  <div class="trend-hero">
    <div class="hero-text">
      <h2><i class="fas fa-chart-line" style="color:#818cf8"></i> &nbsp;Historical Trend Analysis</h2>
      <p>Monthly comparisons, year-over-year, category growth, daily patterns.<br>Understand your spending habits over time.</p>
    </div>
    <div style="display:flex;gap:10px;flex-shrink:0">
      <button class="btn btn-secondary btn-sm" onclick="loadData(6)">6M</button>
      <button class="btn btn-primary btn-sm"   onclick="loadData(12)" id="btn12">12M</button>
      <button class="btn btn-secondary btn-sm" onclick="loadData(24)">24M</button>
    </div>
  </div>

  <!-- Filters -->
  <div class="filter-bar">
    <div class="filter-group">
      <label>Months to show</label>
      <select id="monthsSel" class="form-control" onchange="loadData(this.value)" style="min-width:120px">
        <option value="3">Last 3 months</option>
        <option value="6">Last 6 months</option>
        <option value="12" selected>Last 12 months</option>
        <option value="24">Last 24 months</option>
      </select>
    </div>
    <div class="filter-group">
      <label>Year-over-Year</label>
      <select id="yoySel" class="form-control" onchange="loadYoY(this.value)" style="min-width:100px">
        <c:forEach begin="2023" end="2027" var="y">
          <option value="${y}" ${selectedYear == y ? 'selected':''}>${y}</option>
        </c:forEach>
      </select>
    </div>
  </div>

  <!-- KPI Cards (populated by JS) -->
  <div class="kpi-row" id="kpiRow">
    <div class="kpi-card"><div class="kpi-val" id="kpiAvgInc">–</div><div class="kpi-label">Avg Monthly Income</div></div>
    <div class="kpi-card"><div class="kpi-val" id="kpiAvgExp">–</div><div class="kpi-label">Avg Monthly Expense</div></div>
    <div class="kpi-card"><div class="kpi-val" id="kpiSavings">–</div><div class="kpi-label">Total Savings</div></div>
    <div class="kpi-card"><div class="kpi-val" id="kpiSavRate">–</div><div class="kpi-label">Savings Rate</div></div>
  </div>

  <!-- Row 1: Monthly trend + YoY -->
  <div class="charts-2col">
    <div class="chart-card">
      <h4><i class="fas fa-chart-area" style="color:#818cf8"></i> Income vs Expense — Monthly</h4>
      <div class="chart-container"><div class="spinner" id="trendSpinner"><i class="fas fa-spinner fa-spin"></i> Loading…</div><canvas id="trendChart" style="display:none"></canvas></div>
    </div>
    <div class="chart-card">
      <h4><i class="fas fa-calendar-check" style="color:#10b981"></i> Year-over-Year Comparison</h4>
      <div class="chart-container"><div class="spinner" id="yoySpinner"><i class="fas fa-spinner fa-spin"></i> Loading…</div><canvas id="yoyChart" style="display:none"></canvas></div>
    </div>
  </div>

  <!-- Row 2: Savings trend + Daily spending -->
  <div class="charts-2col">
    <div class="chart-card">
      <h4><i class="fas fa-piggy-bank" style="color:#f59e0b"></i> Monthly Savings Trend</h4>
      <div class="chart-container"><canvas id="savingsChart"></canvas></div>
    </div>
    <div class="chart-card">
      <h4><i class="fas fa-calendar-days" style="color:#06b6d4"></i> Daily Spending — This Month</h4>
      <div class="chart-container"><canvas id="dailyChart"></canvas></div>
    </div>
  </div>

  <!-- Row 3: Category growth table -->
  <div class="chart-full">
    <h4><i class="fas fa-arrow-trend-up" style="color:#f97316"></i> Category Growth vs Previous Month</h4>
    <div id="growthTable"><div class="spinner"><i class="fas fa-spinner fa-spin"></i> Loading…</div></div>
  </div>

  <!-- Row 4: Category stacked bar -->
  <div class="chart-full">
    <h4><i class="fas fa-layer-group" style="color:#a78bfa"></i> Category Spending Over Time</h4>
    <div class="chart-container-lg"><canvas id="catChart"></canvas></div>
  </div>

</div>
</main>

<script>
const CTX   = '${pageContext.request.contextPath}';
const GRID  = 'rgba(255,255,255,.05)';
const COLORS = ['#6366f1','#10b981','#f59e0b','#ef4444','#06b6d4','#a78bfa','#f97316','#84cc16','#ec4899','#14b8a6'];

Chart.defaults.color     = '#5a5e78';
Chart.defaults.font.family = "'DM Sans', sans-serif";

let trendChartInst, yoyChartInst, savingsChartInst, dailyChartInst, catChartInst;

function rupee(v) { return '₹' + parseFloat(v||0).toLocaleString('en-IN',{minimumFractionDigits:0,maximumFractionDigits:0}); }
function destroyChart(c) { if(c) c.destroy(); }

/* ── Monthly Trend + Savings ── */
function loadData(months) {
  document.getElementById('monthsSel').value = months;
  document.getElementById('trendSpinner').style.display = 'flex';
  document.getElementById('trendChart').style.display   = 'none';

  fetch(CTX + '/budget/api/trend?months=' + months)
    .then(function(r){ return r.json(); })
    .then(function(data) {
      const labels   = data.map(function(d){ return d.label; });
      const incomes  = data.map(function(d){ return parseFloat(d.income||0); });
      const expenses = data.map(function(d){ return parseFloat(d.expense||0); });
      const savings  = data.map(function(d){ return parseFloat(d.savings||0); });

      // KPIs
      const avgInc = incomes.reduce(function(a,b){return a+b;},0)/incomes.length;
      const avgExp = expenses.reduce(function(a,b){return a+b;},0)/expenses.length;
      const totSav = savings.reduce(function(a,b){return a+b;},0);
      const totInc = incomes.reduce(function(a,b){return a+b;},0);
      document.getElementById('kpiAvgInc').textContent  = rupee(avgInc);
      document.getElementById('kpiAvgExp').textContent  = rupee(avgExp);
      document.getElementById('kpiSavings').textContent = rupee(totSav);
      document.getElementById('kpiSavRate').textContent = totInc>0 ? Math.round(totSav/totInc*100)+'%' : '–';

      // Trend chart
      document.getElementById('trendSpinner').style.display = 'none';
      document.getElementById('trendChart').style.display   = '';
      destroyChart(trendChartInst);
      trendChartInst = new Chart(document.getElementById('trendChart'), {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            { label:'Income',  data:incomes,  backgroundColor:'rgba(16,185,129,.7)', borderRadius:4 },
            { label:'Expense', data:expenses, backgroundColor:'rgba(239,68,68,.7)',  borderRadius:4 }
          ]
        },
        options: { responsive:true, maintainAspectRatio:false,
          plugins:{ legend:{ labels:{ boxWidth:12, padding:14 }}},
          scales:{ x:{ grid:{ color:GRID }}, y:{ grid:{ color:GRID }, ticks:{ callback:function(v){ return rupee(v); }}}}
        }
      });

      // Savings line
      destroyChart(savingsChartInst);
      savingsChartInst = new Chart(document.getElementById('savingsChart'), {
        type: 'line',
        data: {
          labels: labels,
          datasets:[{
            label:'Monthly Savings', data:savings,
            borderColor:'#f59e0b', backgroundColor:'rgba(245,158,11,.1)',
            tension:.4, fill:true, pointRadius:4,
            pointBackgroundColor: savings.map(function(v){ return v>=0?'#f59e0b':'#ef4444'; })
          }]
        },
        options: { responsive:true, maintainAspectRatio:false,
          plugins:{ legend:{ display:false }},
          scales:{ x:{ grid:{ color:GRID }},
                   y:{ grid:{ color:GRID }, ticks:{ callback:function(v){ return rupee(v); }}}}
        }
      });
    });

  loadDaily();
  loadCategoryTrend(months);
  loadGrowth();
}

/* ── Year-over-Year ── */
function loadYoY(year) {
  document.getElementById('yoySpinner').style.display = 'flex';
  document.getElementById('yoyChart').style.display   = 'none';
  fetch(CTX + '/budget/api/yoy?year=' + year)
    .then(function(r){ return r.json(); })
    .then(function(data) {
      document.getElementById('yoySpinner').style.display = 'none';
      document.getElementById('yoyChart').style.display   = '';
      destroyChart(yoyChartInst);
      yoyChartInst = new Chart(document.getElementById('yoyChart'), {
        type: 'line',
        data: {
          labels: data.map(function(d){ return d.label; }),
          datasets:[
            { label: year + ' Expense',     data:data.map(function(d){ return parseFloat(d.thisYear||0); }),
              borderColor:'#6366f1', backgroundColor:'rgba(99,102,241,.1)', tension:.4, fill:true, pointRadius:4 },
            { label: (year-1) + ' Expense', data:data.map(function(d){ return parseFloat(d.lastYear||0); }),
              borderColor:'#94a3b8', backgroundColor:'rgba(148,163,184,.05)', tension:.4, fill:true, pointRadius:4,
              borderDash:[5,4] }
          ]
        },
        options:{ responsive:true, maintainAspectRatio:false,
          plugins:{ legend:{ labels:{ boxWidth:12, padding:14 }}},
          scales:{ x:{ grid:{ color:GRID }}, y:{ grid:{ color:GRID }, ticks:{ callback:function(v){ return rupee(v); }}}}
        }
      });
    });
}

/* ── Daily spending sparkline ── */
function loadDaily() {
  fetch(CTX + '/budget/api/daily')
    .then(function(r){ return r.json(); })
    .then(function(data) {
      destroyChart(dailyChartInst);
      dailyChartInst = new Chart(document.getElementById('dailyChart'), {
        type:'bar',
        data:{
          labels: data.map(function(d){ return 'Day '+d.day; }),
          datasets:[{ label:'Spending', data:data.map(function(d){ return parseFloat(d.total||0); }),
            backgroundColor:'rgba(6,182,212,.6)', borderRadius:4, borderSkipped:false }]
        },
        options:{ responsive:true, maintainAspectRatio:false,
          plugins:{ legend:{ display:false }},
          scales:{ x:{ grid:{ color:GRID }, ticks:{ maxRotation:0, font:{ size:10 }}},
                   y:{ grid:{ color:GRID }, ticks:{ callback:function(v){ return rupee(v); }}}}
        }
      });
    });
}

/* ── Category stacked bar ── */
function loadCategoryTrend(months) {
  fetch(CTX + '/budget/api/category?months=' + months)
    .then(function(r){ return r.json(); })
    .then(function(data) {
      // Build {category: {label: total}}
      var catMap = {}, labels = [];
      data.forEach(function(d){
        if(!catMap[d.category]) catMap[d.category] = {};
        catMap[d.category][d.label] = parseFloat(d.total||0);
        if(labels.indexOf(d.label) === -1) labels.push(d.label);
      });
      var cats = Object.keys(catMap);
      var datasets = cats.map(function(cat, i){
        return {
          label: cat,
          data: labels.map(function(l){ return catMap[cat][l]||0; }),
          backgroundColor: COLORS[i % COLORS.length],
          borderRadius: 3
        };
      });
      destroyChart(catChartInst);
      catChartInst = new Chart(document.getElementById('catChart'), {
        type:'bar',
        data:{ labels:labels, datasets:datasets },
        options:{ responsive:true, maintainAspectRatio:false,
          plugins:{ legend:{ position:'bottom', labels:{ boxWidth:10, padding:12, font:{size:11}}}},
          scales:{ x:{ stacked:true, grid:{ color:GRID }}, y:{ stacked:true, grid:{ color:GRID }, ticks:{ callback:function(v){ return rupee(v); }}}}
        }
      });
    });
}

/* ── Growth table ── */
function loadGrowth() {
  fetch(CTX + '/budget/api/growth')
    .then(function(r){ return r.json(); })
    .then(function(data) {
      if(!data.length){ document.getElementById('growthTable').innerHTML='<p style="color:var(--text-muted);padding:20px;font-size:.85rem">No data available.</p>'; return; }
      var html = '<div style="overflow-x:auto"><table class="growth-table">' +
        '<thead><tr><th>Category</th><th>This Month</th><th>Last Month</th><th>Change</th></tr></thead><tbody>';
      data.forEach(function(d){
        var pct    = d.pctChange;
        var chip   = '';
        if(pct === null || pct === undefined) { chip = '<span class="pct-chip pct-flat">New</span>'; }
        else if(pct > 0) { chip = '<span class="pct-chip pct-up"><i class="fas fa-arrow-up" style="font-size:.6rem"></i> ' + pct + '%</span>'; }
        else if(pct < 0) { chip = '<span class="pct-chip pct-down"><i class="fas fa-arrow-down" style="font-size:.6rem"></i> ' + Math.abs(pct) + '%</span>'; }
        else              { chip = '<span class="pct-chip pct-flat">No change</span>'; }
        html += '<tr><td><strong>' + (d.category||'–') + '</strong></td>' +
          '<td style="color:var(--text-primary);font-weight:600">' + rupee(d.currTotal) + '</td>' +
          '<td style="color:var(--text-muted)">' + rupee(d.prevTotal) + '</td>' +
          '<td>' + chip + '</td></tr>';
      });
      html += '</tbody></table></div>';
      document.getElementById('growthTable').innerHTML = html;
    });
}

// Initial load
loadData(12);
loadYoY(${selectedYear});
</script>
</body>
</html>
