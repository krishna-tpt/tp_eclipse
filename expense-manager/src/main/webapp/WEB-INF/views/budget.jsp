<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="pageTitle" value="Budget & Trends" scope="request" />
<c:set var="activePage" value="budget" scope="request" />
<%@ include file="header.jsp"%>

<style>
/* ── Tabs ── */
.btab-bar {
	display: flex;
	gap: .5rem;
	margin-bottom: 1.25rem;
	border-bottom: 2px solid var(--border);
}

.btab {
	padding: .55rem 1.1rem;
	font-size: .875rem;
	font-weight: 600;
	cursor: pointer;
	border: none;
	background: none;
	color: var(--text-2);
	border-bottom: 2px solid transparent;
	margin-bottom: -2px;
	transition: color .15s, border-color .15s;
}

.btab.active {
	color: var(--primary);
	border-bottom-color: var(--primary);
}

/* ── Progress bar ── */
.prog-wrap {
	background: #e2e8f0;
	border-radius: 99px;
	height: 10px;
	overflow: hidden;
	margin: .4rem 0;
}

.prog-bar {
	height: 100%;
	border-radius: 99px;
	transition: width .4s;
}

.prog-bar.ok {
	background: #4ade80;
}

.prog-bar.warning {
	background: #fbbf24;
}

.prog-bar.danger {
	background: #f87171;
}

/* ── Budget card ── */
.budget-card {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: 1.1rem 1.25rem;
	margin-bottom: 1rem;
}

.budget-card h3 {
	font-size: .95rem;
	font-weight: 700;
	margin: 0 0 .75rem;
}

/* ── Category budget row ── */
.cat-budget-row {
	display: grid;
	grid-template-columns: 1fr 110px 80px 80px;
	gap: .5rem;
	align-items: center;
	padding: .4rem 0;
	border-bottom: 1px solid var(--border);
}

.cat-budget-row:last-child {
	border-bottom: none;
}

.cat-budget-row label {
	font-size: .82rem;
	font-weight: 500;
}

/* ── Alert popup ── */
.budget-alert-toast {
	position: fixed;
	bottom: 1.5rem;
	right: 1.5rem;
	z-index: 999;
	display: flex;
	flex-direction: column;
	gap: .5rem;
	max-width: 340px;
}

.toast-item {
	background: #fff;
	border-radius: 10px;
	padding: .75rem 1rem;
	box-shadow: 0 4px 20px rgba(0, 0, 0, .15);
	border-left: 4px solid #fbbf24;
	font-size: .82rem;
	animation: slideIn .3s ease;
	display: flex;
	align-items: flex-start;
	gap: .6rem;
}

.toast-item.exceeded {
	border-left-color: #f87171;
}

.toast-item .toast-icon {
	font-size: 1.1rem;
	flex-shrink: 0;
}

.toast-item .toast-close {
	margin-left: auto;
	background: none;
	border: none;
	cursor: pointer;
	color: var(--text-2);
	font-size: .9rem;
	padding: 0;
	flex-shrink: 0;
}

@
keyframes slideIn {
	from {transform: translateX(100%);
	opacity: 0
}

to {
	transform: translateX(0);
	opacity: 1
}

}

/* ── Trend charts ── */
.chart-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 1.25rem;
	margin-bottom: 1.25rem;
}

.chart-card {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: 1rem;
}

.chart-card h3 {
	font-size: .875rem;
	font-weight: 700;
	margin: 0 0 .75rem;
	color: var(--text-1);
}

.chart-card canvas {
	width: 100% !important;
}

@media ( max-width :800px) {
	.chart-grid {
		grid-template-columns: 1fr;
	}
}

/* ── Month selector ── */
.month-nav {
	display: flex;
	align-items: center;
	gap: .6rem;
	margin-bottom: 1rem;
}

.month-nav select {
	font-size: .85rem;
	padding: .3rem .6rem;
}

/* ── Summary strip ── */
.bsummary {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: .75rem;
	margin-bottom: 1rem;
}

.bsum-card {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: .75rem 1rem;
	text-align: center;
}

.bsum-label {
	font-size: .7rem;
	font-weight: 600;
	text-transform: uppercase;
	letter-spacing: .5px;
	color: var(--text-2);
}

.bsum-val {
	font-size: 1.3rem;
	font-weight: 700;
	margin-top: .15rem;
}
</style>

<%-- ═══ PAGE HEADER ═══ --%>
<div class="page-header flex">
	<div>
		<h1>&#127811; Budget &amp; Trends</h1>
		<p>
			<strong>${sessionScope.activeBookName}</strong>
		</p>
	</div>
</div>

<%-- Alerts --%>
<c:if test="${not empty param.success}">
	<div class="alert alert-success">&#10003; Budget saved!</div>
</c:if>
<c:if test="${not empty param.error}">
	<div class="alert alert-error">&#10007; ${param.error}</div>
</c:if>
<c:if test="${not empty dbError}">
	<div class="alert alert-error">&#9888; ${dbError}</div>
</c:if>

<%-- ═══ TAB BAR ═══ --%>
<div class="btab-bar">
	<button class="btab ${tab=='budget'?'active':''}"
		onclick="switchTab('budget')">&#127811; Budget</button>
	<button class="btab ${tab=='trend' ?'active':''}"
		onclick="switchTab('trend')">&#128200; Trend Analysis</button>
</div>

<%-- ════════════════════════════════════════════════
     TAB 1 : BUDGET
     ════════════════════════════════════════════════ --%>
<div id="tabBudget" class="${tab=='budget'?'':'hidden'}">

	<%-- Month selector --%>
	<form method="get" action="${pageContext.request.contextPath}/budget"
		class="month-nav" id="monthForm">
		<input type="hidden" name="tab" value="budget"> <label
			style="font-size: .82rem; font-weight: 600">Month:</label> <select
			name="month" onchange="document.getElementById('monthForm').submit()">
			<c:forEach begin="1" end="12" var="m">
				<option value="${m}" ${m==selMonth?'selected':''}>${m}</option>
			</c:forEach>
		</select> <select name="year"
			onchange="document.getElementById('monthForm').submit()">
			<c:forEach begin="2023" end="2027" var="y">
				<option value="${y}" ${y==selYear?'selected':''}>${y}</option>
			</c:forEach>
		</select>
	</form>

	<%-- Summary strip --%>
	<c:choose>
		<c:when test="${not empty budget}">
			<div class="bsummary">
				<div class="bsum-card">
					<div class="bsum-label">Monthly Limit</div>
					<div class="bsum-val" style="color: var(--primary)">
						&#8377;
						<fmt:formatNumber value="${budget.overallLimit}"
							pattern="#,##0.00" />
					</div>
				</div>
				<div class="bsum-card">
					<div class="bsum-label">Spent</div>
					<div class="bsum-val" style="color: var(--red)">
						&#8377;
						<fmt:formatNumber value="${budget.totalSpent}" pattern="#,##0.00" />
					</div>
				</div>
				<div class="bsum-card">
					<div class="bsum-label">Remaining</div>
					<div class="bsum-val"
						style="color:${budget.remainingPositive?'var(--green)':'var(--red)'}">
						&#8377;
						<fmt:formatNumber value="${budget.remainingAmount}"
							pattern="#,##0.00" />
					</div>
				</div>
			</div>

			<%-- Overall progress bar --%>
			<div class="budget-card">
				<h3>Overall Budget — ${budget.monthName} ${budget.year}</h3>
				<div
					style="display: flex; justify-content: space-between; font-size: .78rem; color: var(--text-2)">
					<span>&#8377;<fmt:formatNumber value="${budget.totalSpent}"
							pattern="#,##0" /> spent
					</span> <span>${budget.usedPct}% used</span> <span>&#8377;<fmt:formatNumber
							value="${budget.overallLimit}" pattern="#,##0" /> limit
					</span>
				</div>
				<div class="prog-wrap">
					<div
						class="prog-bar ${budget.usedPct>=100?'danger':budget.usedPct>=80?'warning':'ok'}"
						style="width:${budget.usedPct}%"></div>
				</div>

				<%-- Category breakdown --%>
				<c:if test="${not empty budget.categories}">
					<div style="margin-top: 1rem">
						<div class="cat-budget-row"
							style="font-size: .72rem; font-weight: 700; color: var(--text-2); text-transform: uppercase">
							<span>Category</span><span>Limit</span><span>Spent</span><span>Used</span>
						</div>
						<c:forEach var="bc" items="${budget.categories}">
							<div class="cat-budget-row">
								<div>
									<div style="font-size: .82rem; font-weight: 600">${bc.categoryName}</div>
									<div class="prog-wrap" style="margin: .3rem 0 0">
										<div
											class="prog-bar ${bc.usedPct>=100?'danger':bc.usedPct>=bc.alertPct?'warning':'ok'}"
											style="width:${bc.usedPct}%"></div>
									</div>
								</div>
								<span style="font-size: .82rem">&#8377;<fmt:formatNumber
										value="${bc.catLimit}" pattern="#,##0" /></span> <span
									style="font-size: .82rem; color: var(--red)">&#8377;<fmt:formatNumber
										value="${bc.spentSafe}" pattern="#,##0" /></span> <span
									style="font-size:.82rem;font-weight:700;color:${bc.usedPct>=100?'var(--red)':bc.alertPct<=bc.usedPct?'#d97706':'var(--green)'}">
									${bc.usedPct}% </span>
							</div>
						</c:forEach>
					</div>
				</c:if>
			</div>
		</c:when>
		<c:otherwise>
			<div class="alert"
				style="background: #f0f9ff; border: 1px solid #bae6fd; color: #0c4a6e; border-radius: 8px; padding: .75rem 1rem">
				&#128203; No budget set for this month. Create one below.</div>
		</c:otherwise>
	</c:choose>

	<%-- ── Budget FORM ── --%>
	<div class="budget-card" style="margin-top: 1rem">
		<h3>&#9999;&#65039; Set Budget — ${selMonth}/${selYear}</h3>
		<form method="post" action="${pageContext.request.contextPath}/budget"
			id="budgetForm">
			<input type="hidden" name="year" value="${selYear}"> <input
				type="hidden" name="month" value="${selMonth}">

			<div class="form-group" style="max-width: 280px">
				<label>Monthly Overall Limit (&#8377;)</label> <input type="number"
					name="overallLimit" step="0.01" min="0" required
					value="${not empty budget ? budget.overallLimit : ''}"
					placeholder="e.g. 50000">
			</div>

			<div style="margin-top: 1rem">
				<div
					style="font-size: .82rem; font-weight: 700; margin-bottom: .5rem">
					Category-wise Limits <span
						style="font-weight: 400; color: var(--text-2)">(optional)</span>
				</div>
				<div class="cat-budget-row"
					style="font-size: .72rem; font-weight: 700; color: var(--text-2); text-transform: uppercase">
					<span>Category</span><span>Limit (&#8377;)</span><span>Alert
						at %</span><span></span>
				</div>
				<c:forEach var="cat" items="${expenseCategories}">
					<%-- find existing budget category if any --%>
					<c:set var="existBC" value="${null}" />
					<c:forEach var="bc" items="${budget.categories}">
						<c:if test="${bc.categoryId == cat.id}">
							<c:set var="existBC" value="${bc}" />
						</c:if>
					</c:forEach>
					<div class="cat-budget-row">
						<label>${cat.name}<input type="hidden" name="catId"
							value="${cat.id}"></label> <input type="number" name="catLimit"
							step="0.01" min="0" placeholder="0"
							value="${not empty existBC ? existBC.catLimit : ''}"
							style="width: 100%; font-size: .82rem"> <input
							type="number" name="alertPct" min="1" max="100" placeholder="80"
							value="${not empty existBC ? existBC.alertPct : 80}"
							style="width: 100%; font-size: .82rem"> <span></span>
					</div>
				</c:forEach>
			</div>

			<div class="flex gap-1 mt-2">
				<button type="submit" class="btn btn-primary btn-sm">&#128190;
					Save Budget</button>
			</div>
		</form>
	</div>

	<%-- Past budgets list --%>
	<c:if test="${not empty allBudgets}">
		<div class="budget-card">
			<h3>&#128203; All Budgets</h3>
			<div class="table-wrap">
				<table>
					<thead>
						<tr>
							<th>Month</th>
							<th>Limit</th>
							<th>Spent</th>
							<th>Used</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="b" items="${allBudgets}">
							<tr>
								<td>${b.monthName}${b.year}</td>
								<td>&#8377;<fmt:formatNumber value="${b.overallLimit}"
										pattern="#,##0.00" /></td>
								<td>&#8377;<fmt:formatNumber
										value="${empty b.totalSpent ? 0 : b.totalSpent}"
										pattern="#,##0.00" /></td>
								<td>
									<div class="prog-wrap"
										style="width: 80px; display: inline-block">
										<div
											class="prog-bar ${b.usedPct>=100?'danger':b.usedPct>=80?'warning':'ok'}"
											style="width:${b.usedPct}%"></div>
									</div> <span style="font-size: .75rem; margin-left: .3rem">${b.usedPct}%</span>
								</td>
								<td><a
									href="${pageContext.request.contextPath}/budget?year=${b.year}&month=${b.month}&tab=budget"
									class="btn btn-outline btn-sm">&#9998;</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
</div>

<%-- ════════════════════════════════════════════════
     TAB 2 : TREND ANALYSIS
     ════════════════════════════════════════════════ --%>
<div id="tabTrend" class="${tab=='trend'?'':'hidden'}">

	<%-- Period selector --%>
	<form method="get" action="${pageContext.request.contextPath}/budget"
		class="month-nav" id="trendForm">
		<input type="hidden" name="tab" value="trend"> <label
			style="font-size: .82rem; font-weight: 600">Period:</label> <select
			name="trendMonths"
			onchange="document.getElementById('trendForm').submit()">
			<option value="6" ${trendMonths==6 ?'selected':''}>Last 6
				months</option>
			<option value="12" ${trendMonths==12?'selected':''}>Last 12
				months</option>
			<option value="24" ${trendMonths==24?'selected':''}>Last 24
				months</option>
		</select>
	</form>

	<div class="chart-grid">
		<%-- Chart 1: Monthly Income vs Expense --%>
		<div class="chart-card" style="grid-column: 1/-1">
			<h3>&#128200; Monthly Income vs Expense</h3>
			<canvas id="chartMonthly" height="90"></canvas>
		</div>

		<%-- Chart 2: Category-wise Monthly Expense --%>
		<div class="chart-card" style="grid-column: 1/-1">
			<h3>&#127914; Category-wise Monthly Expense</h3>
			<canvas id="chartCategory" height="90"></canvas>
		</div>

		<%-- Chart 3: Year-over-Year --%>
		<div class="chart-card" style="grid-column: 1/-1">
			<h3>&#128197; Year-over-Year Comparison</h3>
			<canvas id="chartYoY" height="90"></canvas>
		</div>
	</div>
</div>

<%-- ═══ ALERT TOASTS (budget warnings) ═══ --%>
<div class="budget-alert-toast" id="toastContainer"></div>

<%-- ═══ HIDDEN JSON DATA ═══ --%>
<script type="application/json" id="monthlyData"><%=request.getAttribute("monthlyTrendJson")%></script>
<script type="application/json" id="catData"><%=request.getAttribute("catTrendJson")%></script>
<script type="application/json" id="yoyData"><%=request.getAttribute("yoyJson")%></script>

<%-- ═══ Chart.js CDN ═══ --%>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>

<script>
// ── Tab switch ─────────────────────────────────────────
function switchTab(tab) {
  document.getElementById('tabBudget').classList.toggle('hidden', tab !== 'budget');
  document.getElementById('tabTrend') .classList.toggle('hidden', tab !== 'trend');
  document.querySelectorAll('.btab').forEach(function(b,i){
    b.classList.toggle('active', (i===0 && tab==='budget')||(i===1 && tab==='trend'));
  });
}

// ── Toast alerts ───────────────────────────────────────
function showToast(msg, exceeded) {
  var container = document.getElementById('toastContainer');
  var div = document.createElement('div');
  div.className = 'toast-item' + (exceeded ? ' exceeded' : '');
  div.innerHTML =
    '<span class="toast-icon">' + (exceeded ? '\u26A0\uFE0F' : '\uD83D\uDD14') + '</span>' +
    '<span>' + msg + '</span>' +
    '<button class="toast-close" onclick="this.parentElement.remove()">&#x2715;</button>';
  container.appendChild(div);
  setTimeout(function(){ if(div.parentElement) div.remove(); }, 8000);
}

// ── Fire alerts for categories near/over limit ─────────
(function fireAlerts(){
  var alerts = [];
  <c:if test="${not empty budget}">
    <c:forEach var="bc" items="${budget.categories}">
      <c:if test="${bc.alertTriggered}">
        alerts.push({
          msg: '${bc.categoryName}: ${bc.usedPct}% used (&#8377;${bc.spentSafe} / &#8377;${bc.catLimit})',
          exceeded: ${bc.exceeded}
        });
      </c:if>
    </c:forEach>
  </c:if>
  setTimeout(function(){
    alerts.forEach(function(a){ showToast(a.msg, a.exceeded); });
  }, 800);
})();

// ── Charts ─────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', function() {
  var monthly = JSON.parse(document.getElementById('monthlyData').textContent || '[]');
  var catData = JSON.parse(document.getElementById('catData').textContent     || '[]');
  var yoyData = JSON.parse(document.getElementById('yoyData').textContent     || '[]');

  Chart.defaults.font.family = "'Inter','Segoe UI',sans-serif";
  Chart.defaults.font.size   = 12;

  // ── Chart 1: Monthly Income vs Expense bar chart ──────
  if (monthly.length) {
    var labels   = monthly.map(function(r){ return r.label; });
    var incomes  = monthly.map(function(r){ return Number(r.income)  || 0; });
    var expenses = monthly.map(function(r){ return Number(r.expense) || 0; });
    var nets     = monthly.map(function(r){ return Number(r.net)     || 0; });

    new Chart(document.getElementById('chartMonthly'), {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          { label:'Income',  data:incomes,  backgroundColor:'rgba(74,222,128,.7)',  borderColor:'#16a34a', borderWidth:1 },
          { label:'Expense', data:expenses, backgroundColor:'rgba(248,113,113,.7)', borderColor:'#dc2626', borderWidth:1 },
          { label:'Net',     data:nets,     type:'line',
            borderColor:'#2563eb', backgroundColor:'rgba(37,99,235,.1)',
            borderWidth:2, pointRadius:4, fill:true, tension:.3 }
        ]
      },
      options: {
        responsive:true,
        plugins:{ legend:{ position:'top' }, tooltip:{ mode:'index', intersect:false } },
        scales:{
          y:{ ticks:{ callback:function(v){ return '\u20B9'+v.toLocaleString('en-IN'); } } }
        }
      }
    });
  }

  // ── Chart 2: Category stacked bar ────────────────────
  if (catData.length) {
    // Build unique labels and categories
    var labelSet = [], catSet = [];
    catData.forEach(function(r){
      if (labelSet.indexOf(r.label) < 0) labelSet.push(r.label);
      if (catSet.indexOf(r.category) < 0) catSet.push(r.category);
    });

    var palette = ['#6366f1','#f59e0b','#10b981','#ef4444','#8b5cf6',
                   '#06b6d4','#f97316','#84cc16','#ec4899','#14b8a6'];

    var datasets = catSet.map(function(cat, ci) {
      var dataArr = labelSet.map(function(lbl) {
        var found = catData.find(function(r){ return r.label===lbl && r.category===cat; });
        return found ? Number(found.total) : 0;
      });
      return {
        label: cat,
        data: dataArr,
        backgroundColor: palette[ci % palette.length]
      };
    });

    new Chart(document.getElementById('chartCategory'), {
      type:'bar',
      data:{ labels:labelSet, datasets:datasets },
      options:{
        responsive:true,
        plugins:{ legend:{ position:'top' } },
        scales:{
          x:{ stacked:true },
          y:{ stacked:true,
              ticks:{ callback:function(v){ return '\u20B9'+v.toLocaleString('en-IN'); } } }
        }
      }
    });
  }

  // ── Chart 3: Year-over-Year ───────────────────────────
  if (yoyData.length) {
    // Group by year
    var years = [];
    yoyData.forEach(function(r){ if(years.indexOf(r.yr)<0) years.push(r.yr); });
    years.sort();

    var monthNames = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    var colors = ['#2563eb','#dc2626','#16a34a','#d97706','#7c3aed'];

    var datasets = [];
    years.forEach(function(yr, yi) {
      var expArr = Array(12).fill(0);
      yoyData.filter(function(r){ return r.yr===yr; })
             .forEach(function(r){ expArr[r.mo-1] = Number(r.expense)||0; });
      datasets.push({
        label: yr + ' Expense',
        data: expArr,
        borderColor: colors[yi % colors.length],
        backgroundColor: 'transparent',
        borderWidth: 2, pointRadius: 4, tension: .3
      });
    });

    new Chart(document.getElementById('chartYoY'), {
      type:'line',
      data:{ labels:monthNames, datasets:datasets },
      options:{
        responsive:true,
        plugins:{ legend:{ position:'top' },
                  tooltip:{ mode:'index', intersect:false } },
        scales:{
          y:{ ticks:{ callback:function(v){ return '\u20B9'+v.toLocaleString('en-IN'); } } }
        }
      }
    });
  }
});
</script>

<%@ include file="txn_modals.jsp"%>
<%@ include file="footer.jsp"%>