<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- <%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> --%>

<!-- After ✅ apache taglibs recognize URI -->
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0">
    <title>ExpenseIQ – Reports</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .report-summary { display:grid; grid-template-columns:repeat(3,1fr); gap:18px; margin-bottom:26px; }
        .cat-table { width:100%; border-collapse:collapse; }
        .cat-table td, .cat-table th {
            padding:10px 14px; font-size:.85rem;
            border-bottom:1px solid var(--border);
        }
        .cat-table th { font-size:.72rem; text-transform:uppercase; letter-spacing:.06em; color:var(--text-muted); }
        .cat-table tr:last-child td { border-bottom:none; }
        .progress-bar-wrap { background:rgba(255,255,255,.06); border-radius:4px; height:6px; min-width:80px; }
        .progress-bar-fill { height:6px; border-radius:4px; }
    </style>
</head>
<body>

<nav class="sidebar">
    <div class="sidebar-brand"><span class="brand-icon">💰</span><span class="brand-name">ExpenseIQ</span></div>
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-chart-pie"></i><span>Dashboard</span></a></li>
        <li><a href="${pageContext.request.contextPath}/income/list"><i class="fas fa-arrow-trend-up"></i><span>Income</span></a></li>
        <li><a href="${pageContext.request.contextPath}/expense/list"><i class="fas fa-arrow-trend-down"></i><span>Expenses</span></a></li>
        <li><a href="${pageContext.request.contextPath}/reports" class="active"><i class="fas fa-chart-bar"></i><span>Reports</span></a></li>
        <li><a href="${pageContext.request.contextPath}/backup/list"><i class="fas fa-database"></i><span>Backup</span></a></li>
    </ul>
    <div class="sidebar-footer"><span>v1.0.0</span></div>
</nav>

<main class="main-content">
    <div class="topbar">
        <div class="page-title">Reports & Analytics</div>
    </div>
    <div class="page-body">

        <!-- Filter -->
        <form method="get" action="${pageContext.request.contextPath}/reports" class="filter-bar" style="margin-bottom:22px">
            <div class="filter-group">
                <label>From Date</label>
                <input type="date" name="fromDate" class="form-control" value="${fromDate}">
            </div>
            <div class="filter-group">
                <label>To Date</label>
                <input type="date" name="toDate" class="form-control" value="${toDate}">
            </div>
            <div class="filter-group">
                <label>Year (trend)</label>
                <select name="year" class="form-control">
                    <c:forEach begin="2020" end="2030" var="y">
                        <option value="${y}" ${selectedYear == y ? 'selected':''}>${y}</option>
                    </c:forEach>
                </select>
            </div>
            <button type="submit" class="btn btn-primary btn-sm" style="align-self:flex-end">
                <i class="fas fa-filter"></i> Apply
            </button>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary btn-sm" style="align-self:flex-end">
                <i class="fas fa-xmark"></i> Clear
            </a>
        </form>

        <!-- Summary Cards -->
        <div class="report-summary">
            <div class="summary-card card-income">
                <div class="card-label">Total Income</div>
                <div class="card-amount">₹<fmt:formatNumber value="${totalIncome}" pattern="#,##0.00"/></div>
                <i class="fas fa-arrow-trend-up card-icon" style="color:#10b981"></i>
            </div>
            <div class="summary-card card-expense">
                <div class="card-label">Total Expense</div>
                <div class="card-amount">₹<fmt:formatNumber value="${totalExpense}" pattern="#,##0.00"/></div>
                <i class="fas fa-arrow-trend-down card-icon" style="color:#ef4444"></i>
            </div>
            <div class="summary-card card-balance">
                <div class="card-label">Net Savings</div>
                <div class="card-amount ${balance >= 0 ? 'balance-positive' : 'balance-negative'}"
                     style="${balance >= 0 ? 'color:#10b981' : 'color:#ef4444'}">
                    ${balance >= 0 ? '+' : ''}₹<fmt:formatNumber value="${balance}" pattern="#,##0.00"/>
                </div>
                <i class="fas fa-piggy-bank card-icon" style="color:#818cf8"></i>
            </div>
        </div>

        <!-- Charts -->
        <div class="charts-grid">
            <div class="chart-card">
                <h4><i class="fas fa-chart-line" style="color:#818cf8"></i> Monthly Trend – ${selectedYear}</h4>
                <div class="chart-container"><canvas id="trendChart"></canvas></div>
            </div>
            <div class="chart-card">
                <h4><i class="fas fa-chart-pie" style="color:#f59e0b"></i> Expense by Category</h4>
                <div class="chart-container"><canvas id="expPieChart"></canvas></div>
            </div>
            <div class="chart-card">
                <h4><i class="fas fa-chart-bar" style="color:#10b981"></i> Income by Category</h4>
                <div class="chart-container"><canvas id="incBarChart"></canvas></div>
            </div>
            <div class="chart-card">
                <h4><i class="fas fa-scale-balanced" style="color:#6366f1"></i> Income vs Expense</h4>
                <div class="chart-container"><canvas id="compareChart"></canvas></div>
            </div>
        </div>

        <!-- Category breakdowns -->
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;">

            <!-- Income by category -->
            <div class="section-card">
                <div class="section-head"><h3><i class="fas fa-arrow-trend-up" style="color:#10b981"></i> Income Breakdown</h3></div>
                <div style="padding:4px 0">
                <c:choose>
                    <c:when test="${empty incomeByCategory}">
                        <p style="padding:20px;color:var(--text-muted);font-size:.85rem">No data.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="cat-table">
                            <thead><tr><th>Category</th><th>Count</th><th>Amount</th><th>Share</th></tr></thead>
                            <tbody>
                            <c:forEach var="row" items="${incomeByCategory}">
                                <tr>
                                    <td>${row.category}</td>
                                    <td style="color:var(--text-muted)">${row.count}</td>
                                    <td style="color:#10b981;font-weight:600">₹<fmt:formatNumber value="${row.total}" pattern="#,##0.00"/></td>
                                    <td>
                                        <c:if test="${totalIncome > 0}">
                                            <div class="progress-bar-wrap">
                                                <div class="progress-bar-fill" style="background:#10b981;width:${(row.total/totalIncome)*100}%"></div>
                                            </div>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
                </div>
            </div>

            <!-- Expense by category -->
            <div class="section-card">
                <div class="section-head"><h3><i class="fas fa-arrow-trend-down" style="color:#ef4444"></i> Expense Breakdown</h3></div>
                <div style="padding:4px 0">
                <c:choose>
                    <c:when test="${empty expenseByCategory}">
                        <p style="padding:20px;color:var(--text-muted);font-size:.85rem">No data.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="cat-table">
                            <thead><tr><th>Category</th><th>Count</th><th>Amount</th><th>Share</th></tr></thead>
                            <tbody>
                            <c:forEach var="row" items="${expenseByCategory}">
                                <tr>
                                    <td>${row.category}</td>
                                    <td style="color:var(--text-muted)">${row.count}</td>
                                    <td style="color:#ef4444;font-weight:600">₹<fmt:formatNumber value="${row.total}" pattern="#,##0.00"/></td>
                                    <td>
                                        <c:if test="${totalExpense > 0}">
                                            <div class="progress-bar-wrap">
                                                <div class="progress-bar-fill" style="background:#ef4444;width:${(row.total/totalExpense)*100}%"></div>
                                            </div>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
                </div>
            </div>
        </div>

    </div>
</main>

<script>
/* ── Build chart data from JSTL ── */
const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];

// Monthly trend data
const incomeByMonth  = new Array(12).fill(0);
const expenseByMonth = new Array(12).fill(0);
<c:forEach var="row" items="${monthlyTrend}">
    const mi = ${row.month} - 1;
    <c:choose>
        <c:when test="${row.type == 'income'}">incomeByMonth[mi]  = ${row.total};</c:when>
        <c:otherwise>                          expenseByMonth[mi] = ${row.total};</c:otherwise>
    </c:choose>
</c:forEach>

// Income category data
const incCats   = [<c:forEach var="r" items="${incomeByCategory}">'${r.category}',</c:forEach>];
const incTotals = [<c:forEach var="r" items="${incomeByCategory}">${r.total},</c:forEach>];

// Expense category data
const expCats   = [<c:forEach var="r" items="${expenseByCategory}">'${r.category}',</c:forEach>];
const expTotals = [<c:forEach var="r" items="${expenseByCategory}">${r.total},</c:forEach>];

Chart.defaults.color = '#5a5e78';
Chart.defaults.font.family = "'DM Sans', sans-serif";

const gridColor = 'rgba(255,255,255,.06)';

// 1. Monthly trend line chart
new Chart(document.getElementById('trendChart'), {
    type: 'line',
    data: {
        labels: months,
        datasets: [
            { label:'Income',  data:incomeByMonth,  borderColor:'#10b981', backgroundColor:'rgba(16,185,129,.1)',
              tension:.4, fill:true, pointRadius:4, pointHoverRadius:6 },
            { label:'Expense', data:expenseByMonth, borderColor:'#ef4444', backgroundColor:'rgba(239,68,68,.1)',
              tension:.4, fill:true, pointRadius:4, pointHoverRadius:6 }
        ]
    },
    options: { responsive:true, maintainAspectRatio:false,
        plugins:{ legend:{ labels:{ boxWidth:12, padding:16 }}},
        scales:{ x:{ grid:{ color:gridColor }}, y:{ grid:{ color:gridColor }, ticks:{ callback:v=>'₹'+v.toLocaleString() }}}
    }
});

// 2. Expense pie chart
const pieColors = ['#ef4444','#f97316','#f59e0b','#84cc16','#06b6d4','#6366f1','#8b5cf6','#ec4899','#14b8a6','#64748b'];
new Chart(document.getElementById('expPieChart'), {
    type: 'doughnut',
    data: {
        labels: expCats,
        datasets: [{ data: expTotals, backgroundColor: pieColors, borderWidth:0, hoverOffset:8 }]
    },
    options: { responsive:true, maintainAspectRatio:false, cutout:'65%',
        plugins:{ legend:{ position:'bottom', labels:{ boxWidth:10, padding:12, font:{size:11} }}}
    }
});

// 3. Income bar chart
new Chart(document.getElementById('incBarChart'), {
    type: 'bar',
    data: {
        labels: incCats,
        datasets: [{ label:'Income', data: incTotals,
            backgroundColor:'rgba(16,185,129,.7)', borderRadius:6, borderSkipped:false }]
    },
    options: { responsive:true, maintainAspectRatio:false, indexAxis:'y',
        plugins:{ legend:{ display:false }},
        scales:{ x:{ grid:{ color:gridColor }, ticks:{ callback:v=>'₹'+v.toLocaleString() }},
                 y:{ grid:{ color:gridColor }}}
    }
});

// 4. Compare bar chart
new Chart(document.getElementById('compareChart'), {
    type: 'bar',
    data: {
        labels: months,
        datasets: [
            { label:'Income',  data:incomeByMonth,  backgroundColor:'rgba(16,185,129,.7)', borderRadius:5 },
            { label:'Expense', data:expenseByMonth, backgroundColor:'rgba(239,68,68,.7)',   borderRadius:5 }
        ]
    },
    options: { responsive:true, maintainAspectRatio:false,
        plugins:{ legend:{ labels:{ boxWidth:12, padding:16 }}},
        scales:{ x:{ grid:{ color:gridColor }, stacked:false },
                 y:{ grid:{ color:gridColor }, ticks:{ callback:v=>'₹'+v.toLocaleString() }}}
    }
});
</script>
</body>
</html>
