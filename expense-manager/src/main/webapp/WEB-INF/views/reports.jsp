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

<style>
.chart-grid-2 {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 1.1rem;
	margin-bottom: 1.1rem;
}

.chart-grid-3 {
	display: grid;
	grid-template-columns: 1fr 1fr 1fr;
	gap: 1.1rem;
	margin-bottom: 1.1rem;
}

.chart-box {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: 1.1rem 1.25rem;
}

.chart-box h3 {
	font-size: .82rem;
	font-weight: 700;
	color: var(--text-2);
	text-transform: uppercase;
	letter-spacing: .05em;
	margin: 0 0 .85rem;
}

.chart-box canvas {
	width: 100% !important;
}

/* Month selector */
.month-bar {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: .65rem 1rem;
	margin-bottom: 1.1rem;
	display: flex;
	align-items: center;
	gap: .6rem;
	flex-wrap: wrap;
}

.month-bar label {
	font-size: .82rem;
	font-weight: 600;
	color: var(--text-2);
}

.month-bar select {
	font-size: .82rem;
	padding: .3rem .6rem;
	border: 1px solid var(--border);
	border-radius: 6px;
}

.month-nav-btn {
	padding: .3rem .7rem;
	border: 1px solid var(--border);
	border-radius: 6px;
	background: #fff;
	cursor: pointer;
	font-size: .82rem;
	font-weight: 600;
	transition: background .15s;
}

.month-nav-btn:hover {
	background: #f0f7ff;
	border-color: var(--primary);
}

/* Month summary strip */
.month-summary {
	display: grid;
	grid-template-columns: repeat(5, 1fr);
	gap: .65rem;
	margin-bottom: 1.1rem;
}

.ms-card {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	padding: .65rem .85rem;
	text-align: center;
}

.ms-lbl {
	font-size: .68rem;
	font-weight: 700;
	text-transform: uppercase;
	letter-spacing: .05em;
	color: var(--text-2);
}

.ms-val {
	font-size: 1.15rem;
	font-weight: 800;
	margin-top: .15rem;
}

.ms-val.inc {
	color: var(--green, #16a34a);
}

.ms-val.exp {
	color: var(--red, #dc2626);
}

.ms-val.net {
	color: var(--primary, #2563eb);
}

.ms-val.cnt {
	color: #7c3aed;
}

.ms-val.sav {
	color: #0891b2;
}

/* Stats table */
.stat-tbl {
	width: 100%;
	border-collapse: collapse;
	font-size: .8rem;
}

.stat-tbl th {
	text-align: left;
	padding: .35rem .5rem;
	font-weight: 700;
	color: var(--text-2);
	border-bottom: 2px solid var(--border);
	font-size: .72rem;
	text-transform: uppercase;
}

.stat-tbl td {
	padding: .35rem .5rem;
	border-bottom: 1px solid var(--border);
}

.stat-tbl tr:last-child td {
	border-bottom: none;
}

.stat-tbl .bar-cell {
	width: 120px;
}

.mini-bar {
	background: #e2e8f0;
	border-radius: 4px;
	height: 8px;
}

.mini-bar-fill {
	height: 8px;
	border-radius: 4px;
	transition: width .4s;
}

.fill-exp {
	background: #ef4444;
}

.fill-inc {
	background: #16a34a;
}

.pct-badge {
	font-size: .7rem;
	font-weight: 700;
	color: var(--text-2);
}

/* Section header */
.section-hdr {
	display: flex;
	align-items: center;
	gap: .5rem;
	font-size: .9rem;
	font-weight: 700;
	margin: .75rem 0 .65rem;
	padding-bottom: .4rem;
	border-bottom: 2px solid var(--border);
}

@media ( max-width :900px) {
	.chart-grid-2, .chart-grid-3 {
		grid-template-columns: 1fr;
	}
	.month-summary {
		grid-template-columns: repeat(3, 1fr);
	}
}
</style>

<%-- ═══ PAGE HEADER ═══ --%>
<div class="page-header flex">
	<div>
		<h1>&#128200; Reports &amp; Analytics</h1>
		<p>
			<strong>${sessionScope.activeBookName}</strong>
		</p>
	</div>
	<div class="flex gap-1 ml-auto">
		<a
			href="${pageContext.request.contextPath}/export?type=reports-pdf&amp;year=${selYear}&amp;month=${selMonth}"
			class="btn btn-outline btn-sm">&#128196; Export PDF</a>
		<button class="btn btn-primary btn-sm"
			onclick="openModal('reportEmailModal')">&#9993; Email Report</button>
	</div>
</div>

<c:if test="${not empty dbError}">
	<div class="alert alert-error">&#10007; ${dbError}</div>
</c:if>

<%-- ═══ MONTH SELECTOR ═══ --%>
<div class="month-bar">
	<label>&#128197; Month:</label>
	<form method="get" action="${pageContext.request.contextPath}/reports"
		id="monthForm" style="display: flex; gap: .5rem; align-items: center">
		<select name="month"
			onchange="document.getElementById('monthForm').submit()">
			<c:forEach begin="1" end="12" var="m">
				<c:set var="mNames"
					value="Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec" />
				<option value="${m}" ${m==selMonth?'selected':''}>
					<c:forEach var="mn" items="${mNames}" varStatus="st">
						<c:if test="${st.index+1 == m}">${mn}</c:if>
					</c:forEach>
				</option>
			</c:forEach>
		</select> <select name="year"
			onchange="document.getElementById('monthForm').submit()">
			<c:forEach begin="2023" end="2027" var="y">
				<option value="${y}" ${y==selYear?'selected':''}>${y}</option>
			</c:forEach>
		</select>
	</form>
	<%-- Prev / Next month --%>
	<c:set var="prevM" value="${selMonth == 1 ? 12 : selMonth - 1}" />
	<c:set var="prevY" value="${selMonth == 1 ? selYear - 1 : selYear}" />
	<c:set var="nextM" value="${selMonth == 12 ? 1 : selMonth + 1}" />
	<c:set var="nextY" value="${selMonth == 12 ? selYear + 1 : selYear}" />
	<a
		href="${pageContext.request.contextPath}/reports?year=${prevY}&month=${prevM}"
		class="month-nav-btn">&#8592;</a> <a
		href="${pageContext.request.contextPath}/reports?year=${nextY}&month=${nextM}"
		class="month-nav-btn">&#8594;</a> <a
		href="${pageContext.request.contextPath}/reports"
		class="btn btn-outline btn-sm">Current</a>
</div>

<%-- ═══ ALL-TIME SUMMARY ═══ --%>
<div class="section-hdr">&#127759; All-Time Overview</div>
<div class="stats-grid" style="margin-bottom: 1.1rem">
	<div class="stat-card">
		<div class="stat-label">Total Income</div>
		<div class="stat-value income">
			&#8377;
			<fmt:formatNumber value="${totalIncome}" pattern="#,##0.00" />
		</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Total Expenses</div>
		<div class="stat-value expense">
			&#8377;
			<fmt:formatNumber value="${totalExpense}" pattern="#,##0.00" />
		</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Net Balance</div>
		<div class="stat-value balance">
			&#8377;
			<fmt:formatNumber value="${balance}" pattern="#,##0.00" />
		</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Savings Rate</div>
		<div class="stat-value balance">
			<c:choose>
				<c:when test="${totalIncome > 0}">
					<fmt:formatNumber value="${(balance/totalIncome)*100}"
						pattern="#0.1" />%
        </c:when>
				<c:otherwise>—</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>

<%-- ═══ SELECTED MONTH SUMMARY ═══ --%>
<c:set var="ms" value="${monthSummary}" />
<div class="section-hdr">&#128197; Selected Month —
	${selMonth}/${selYear}</div>
<div class="month-summary">
	<div class="ms-card">
		<div class="ms-lbl">Income</div>
		<div class="ms-val inc">
			&#8377;
			<fmt:formatNumber value="${ms.income}" pattern="#,##0" />
		</div>
	</div>
	<div class="ms-card">
		<div class="ms-lbl">Expense</div>
		<div class="ms-val exp">
			&#8377;
			<fmt:formatNumber value="${ms.expense}" pattern="#,##0" />
		</div>
	</div>
	<div class="ms-card">
		<div class="ms-lbl">Net</div>
		<div class="ms-val net">
			&#8377;
			<fmt:formatNumber value="${ms.net}" pattern="#,##0" />
		</div>
	</div>
	<div class="ms-card">
		<div class="ms-lbl">Transactions</div>
		<div class="ms-val cnt">${ms.txnCount}</div>
	</div>
	<div class="ms-card">
		<div class="ms-lbl">Savings Rate</div>
		<div class="ms-val sav">
			<fmt:formatNumber value="${ms.savingsRate}" pattern="#0.1" />
			%
		</div>
	</div>
</div>

<%-- ═══ MONTHLY TREND (12 months) ═══ --%>
<div class="section-hdr">&#128200; Monthly Trend (Last 12 Months)</div>
<div class="chart-box" style="margin-bottom: 1.1rem">
	<h3>Income vs Expense</h3>
	<div style="height: 240px; position: relative">
		<canvas id="monthlyChart"></canvas>
	</div>
</div>

<%-- ═══ SELECTED MONTH: DAILY + WEEKLY + DOW ═══ --%>
<div class="section-hdr">&#128197; ${selMonth}/${selYear} — Time
	Breakdown</div>
<div class="chart-grid-3">
	<div class="chart-box">
		<h3>Daily (Day-by-Day)</h3>
		<div style="height: 200px; position: relative">
			<canvas id="dailyChart"></canvas>
		</div>
	</div>
	<div class="chart-box">
		<h3>Weekly Totals</h3>
		<div style="height: 200px; position: relative">
			<canvas id="weeklyChart"></canvas>
		</div>
	</div>
	<div class="chart-box">
		<h3>Day of Week Pattern</h3>
		<div style="height: 200px; position: relative">
			<canvas id="dowChart"></canvas>
		</div>
	</div>
</div>

<%-- ═══ CATEGORY CHARTS ═══ --%>
<div class="section-hdr">&#127914; Category Breakdown —
	${selMonth}/${selYear}</div>
<div class="chart-grid-2">
	<div class="chart-box">
		<h3>Expense by Category</h3>
		<div style="height: 220px; position: relative">
			<canvas id="expCatMonthChart"></canvas>
		</div>
	</div>
	<div class="chart-box">
		<h3>Income by Category</h3>
		<div style="height: 220px; position: relative">
			<canvas id="incCatMonthChart"></canvas>
		</div>
	</div>
</div>

<%-- Category tables --%>
<div class="chart-grid-2">
	<%-- Expense category table --%>
	<div class="chart-box">
		<h3>Expense Category Details</h3>
		<table class="stat-tbl" id="expCatTable">
			<thead>
				<tr>
					<th>Category</th>
					<th>Amount</th>
					<th>Txns</th>
					<th class="bar-cell">% Share</th>
				</tr>
			</thead>
			<tbody id="expCatTbody"></tbody>
		</table>
	</div>
	<%-- Income category table --%>
	<div class="chart-box">
		<h3>Income Category Details</h3>
		<table class="stat-tbl">
			<thead>
				<tr>
					<th>Category</th>
					<th>Amount</th>
					<th>Txns</th>
					<th class="bar-cell">% Share</th>
				</tr>
			</thead>
			<tbody id="incCatTbody"></tbody>
		</table>
	</div>
</div>

<%-- ═══ SUB-CATEGORY BREAKDOWN ═══ --%>
<div class="section-hdr">&#128203; Sub-Category Breakdown —
	${selMonth}/${selYear}</div>
<div class="chart-grid-2">
	<div class="chart-box">
		<h3>Expense Sub-Categories</h3>
		<div style="height: 220px; position: relative">
			<canvas id="expSubChart"></canvas>
		</div>
	</div>
	<div class="chart-box">
		<h3>Income Sub-Categories</h3>
		<div style="height: 220px; position: relative">
			<canvas id="incSubChart"></canvas>
		</div>
	</div>
</div>

<%-- Sub-cat tables --%>
<div class="chart-grid-2" style="margin-bottom: 1.5rem">
	<div class="chart-box">
		<h3>Expense Sub-Category Details</h3>
		<table class="stat-tbl">
			<thead>
				<tr>
					<th>Category</th>
					<th>Sub-Category</th>
					<th>Amount</th>
					<th>Txns</th>
				</tr>
			</thead>
			<tbody id="expSubTbody"></tbody>
		</table>
	</div>
	<div class="chart-box">
		<h3>Income Sub-Category Details</h3>
		<table class="stat-tbl">
			<thead>
				<tr>
					<th>Category</th>
					<th>Sub-Category</th>
					<th>Amount</th>
					<th>Txns</th>
				</tr>
			</thead>
			<tbody id="incSubTbody"></tbody>
		</table>
	</div>
</div>

<%-- ═══ ALL-TIME CATEGORY CHARTS ═══ --%>
<div class="section-hdr">&#127758; All-Time Category Split</div>
<div class="chart-grid-2" style="margin-bottom: 1.5rem">
	<div class="chart-box">
		<h3>All-Time Expense by Category</h3>
		<div style="height: 220px; position: relative">
			<canvas id="expCatChart"></canvas>
		</div>
	</div>
	<div class="chart-box">
		<h3>All-Time Income by Category</h3>
		<div style="height: 220px; position: relative">
			<canvas id="incCatChart"></canvas>
		</div>
	</div>
</div>

<%-- ═══ DATA ═══ --%>
<script type="application/json" id="d-monthly"><%=request.getAttribute("monthlyJson")%></script>
<script type="application/json" id="d-daily"><%=request.getAttribute("dailyJson")%></script>
<script type="application/json" id="d-weekly"><%=request.getAttribute("weeklyJson")%></script>
<script type="application/json" id="d-dow"><%=request.getAttribute("dowJson")%></script>
<script type="application/json" id="d-expCatM"><%=request.getAttribute("expCatMonthJson")%></script>
<script type="application/json" id="d-incCatM"><%=request.getAttribute("incCatMonthJson")%></script>
<script type="application/json" id="d-expSub"><%=request.getAttribute("expSubCatJson")%></script>
<script type="application/json" id="d-incSub"><%=request.getAttribute("incSubCatJson")%></script>
<script type="application/json" id="d-expCat"><%=request.getAttribute("expCatJson")%></script>
<script type="application/json" id="d-incCat"><%=request.getAttribute("incCatJson")%></script>

<script>
	function jd(id) {
		return JSON.parse(document.getElementById(id).textContent || '[]');
	}
	function jdo(id) {
		return JSON.parse(document.getElementById(id).textContent || '{}');
	}
	function fmt(v) {
		return '\u20B9' + Number(v || 0).toLocaleString('en-IN', {
			minimumFractionDigits : 0,
			maximumFractionDigits : 0
		});
	}

	Chart.defaults.font.family = "'Inter','Segoe UI',sans-serif";
	Chart.defaults.color = '#64748b';
	Chart.defaults.borderColor = '#e2e8f0';

	var PAL = [ '#2563eb', '#16a34a', '#dc2626', '#d97706', '#7c3aed',
			'#0891b2', '#be185d', '#059669', '#ea580c', '#6366f1' ];
	var PAL2 = [ '#3b82f6', '#22c55e', '#ef4444', '#f59e0b', '#8b5cf6',
			'#06b6d4', '#ec4899', '#10b981', '#f97316', '#818cf8' ];

	function mkBar(canvasId, labels, datasets, yFmt) {
		var el = document.getElementById(canvasId);
		if (!el)
			return;
		new Chart(el, {
			type : 'bar',
			data : {
				labels : labels,
				datasets : datasets
			},
			options : {
				responsive : true,
				maintainAspectRatio : false,
				plugins : {
					legend : {
						position : 'top'
					}
				},
				scales : {
					x : {
						grid : {
							display : false
						}
					},
					y : {
						ticks : {
							callback : yFmt || function(v) {
								return fmt(v);
							}
						}
					}
				}
			}
		});
	}

	function mkDoughnut(canvasId, labels, data, palette) {
		var el = document.getElementById(canvasId);
		if (!el || !data.length)
			return;
		new Chart(el, {
			type : 'doughnut',
			data : {
				labels : labels,
				datasets : [ {
					data : data,
					backgroundColor : palette || PAL,
					borderWidth : 2,
					borderColor : '#fff'
				} ]
			},
			options : {
				responsive : true,
				maintainAspectRatio : false,
				plugins : {
					legend : {
						position : 'right',
						labels : {
							font : {
								size : 10
							},
							boxWidth : 12
						}
					}
				}
			}
		});
	}

	function buildCatTable(tbodyId, data, fillClass) {
		var tbody = document.getElementById(tbodyId);
		if (!tbody)
			return;
		var total = data.reduce(function(s, r) {
			return s + Number(r.total || 0);
		}, 0);
		tbody.innerHTML = '';
		data
				.forEach(function(r) {
					var pct = total > 0 ? (Number(r.total || 0) / total * 100)
							.toFixed(1) : 0;
					var tr = document.createElement('tr');
					tr.innerHTML = '<td style="font-weight:500">'
							+ (r.category || r.name)
							+ '</td>'
							+ '<td style="font-weight:700">'
							+ fmt(r.total)
							+ '</td>'
							+ '<td class="text-muted">'
							+ (r.txnCount || 0)
							+ '</td>'
							+ '<td class="bar-cell"><div class="mini-bar"><div class="mini-bar-fill '
							+ fillClass + '" style="width:' + pct
							+ '%"></div></div>' + '<span class="pct-badge"> '
							+ pct + '%</span></td>';
					tbody.appendChild(tr);
				});
	}

	function buildSubTable(tbodyId, data) {
		var tbody = document.getElementById(tbodyId);
		if (!tbody)
			return;
		tbody.innerHTML = '';
		data.forEach(function(r) {
			var tr = document.createElement('tr');
			tr.innerHTML = '<td class="text-muted" style="font-size:.78rem">'
					+ (r.category || '') + '</td>'
					+ '<td style="font-weight:500">' + (r.subcategory || '')
					+ '</td>' + '<td style="font-weight:700">' + fmt(r.total)
					+ '</td>' + '<td class="text-muted">' + (r.txnCount || 0)
					+ '</td>';
			tbody.appendChild(tr);
		});
	}

	// ── Monthly trend ──────────────────────────────────────
	var monthly = jd('d-monthly');
	if (monthly.length)
		mkBar('monthlyChart', monthly.map(function(m) {
			return m.month;
		}), [ {
			label : 'Income',
			data : monthly.map(function(m) {
				return Number(m.income || 0);
			}),
			backgroundColor : '#dcfce7',
			borderColor : '#16a34a',
			borderWidth : 2,
			borderRadius : 4
		}, {
			label : 'Expense',
			data : monthly.map(function(m) {
				return Number(m.expense || 0);
			}),
			backgroundColor : '#fee2e2',
			borderColor : '#dc2626',
			borderWidth : 2,
			borderRadius : 4
		} ]);

	// ── Daily ──────────────────────────────────────────────
	var daily = jd('d-daily');
	if (daily.length)
		mkBar('dailyChart', daily.map(function(d) {
			return d.day ? d.day.substring(8) : '';
		}), [ {
			label : 'Income',
			data : daily.map(function(d) {
				return Number(d.income || 0);
			}),
			backgroundColor : '#bbf7d0',
			borderColor : '#16a34a',
			borderWidth : 1,
			borderRadius : 3
		}, {
			label : 'Expense',
			data : daily.map(function(d) {
				return Number(d.expense || 0);
			}),
			backgroundColor : '#fecaca',
			borderColor : '#dc2626',
			borderWidth : 1,
			borderRadius : 3
		} ]);

	// ── Weekly ─────────────────────────────────────────────
	var weekly = jd('d-weekly');
	if (weekly.length)
		mkBar('weeklyChart', weekly.map(function(w) {
			return w.week;
		}), [ {
			label : 'Income',
			data : weekly.map(function(w) {
				return Number(w.income || 0);
			}),
			backgroundColor : '#93c5fd',
			borderColor : '#2563eb',
			borderWidth : 1,
			borderRadius : 3
		}, {
			label : 'Expense',
			data : weekly.map(function(w) {
				return Number(w.expense || 0);
			}),
			backgroundColor : '#fca5a5',
			borderColor : '#dc2626',
			borderWidth : 1,
			borderRadius : 3
		} ]);

	// ── Day of week ────────────────────────────────────────
	var dow = jd('d-dow');
	if (dow.length)
		mkBar('dowChart', dow.map(function(d) {
			return d.label;
		}), [ {
			label : 'Income',
			data : dow.map(function(d) {
				return Number(d.income || 0);
			}),
			backgroundColor : '#a7f3d0',
			borderColor : '#059669',
			borderWidth : 1,
			borderRadius : 3
		}, {
			label : 'Expense',
			data : dow.map(function(d) {
				return Number(d.expense || 0);
			}),
			backgroundColor : '#fda4af',
			borderColor : '#e11d48',
			borderWidth : 1,
			borderRadius : 3
		} ]);

	// ── Category charts (month) ────────────────────────────
	var expCatM = jd('d-expCatM');
	var incCatM = jd('d-incCatM');
	mkDoughnut('expCatMonthChart', expCatM.map(function(r) {
		return r.category;
	}), expCatM.map(function(r) {
		return Number(r.total || 0);
	}), PAL);
	mkDoughnut('incCatMonthChart', incCatM.map(function(r) {
		return r.category;
	}), incCatM.map(function(r) {
		return Number(r.total || 0);
	}), PAL2);
	buildCatTable('expCatTbody', expCatM, 'fill-exp');
	buildCatTable('incCatTbody', incCatM, 'fill-inc');

	// ── Sub-category charts ────────────────────────────────
	var expSub = jd('d-expSub');
	var incSub = jd('d-incSub');
	mkDoughnut('expSubChart', expSub.map(function(r) {
		return r.subcategory;
	}), expSub.map(function(r) {
		return Number(r.total || 0);
	}), PAL);
	mkDoughnut('incSubChart', incSub.map(function(r) {
		return r.subcategory;
	}), incSub.map(function(r) {
		return Number(r.total || 0);
	}), PAL2);
	buildSubTable('expSubTbody', expSub);
	buildSubTable('incSubTbody', incSub);

	// ── All-time doughnut ──────────────────────────────────
	var expCat = jd('d-expCat');
	var incCat = jd('d-incCat');
	mkDoughnut('expCatChart', expCat.map(function(r) {
		return r.name;
	}), expCat.map(function(r) {
		return Number(r.total || 0);
	}), PAL);
	mkDoughnut('incCatChart', incCat.map(function(r) {
		return r.name;
	}), incCat.map(function(r) {
		return Number(r.total || 0);
	}), PAL2);
</script>

<c:if test="${not empty param.emailSent}">
	<div class="alert alert-success">&#10003; Full report sent to
		your email successfully!</div>
</c:if>
<c:if test="${not empty param.exportError}">
	<div class="alert alert-error">&#10007; Export error:
		${param.exportError}</div>
</c:if>

<%-- Email Report Modal --%>
<div id="reportEmailModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3>&#9993; Email Full Report</h3>
			<button class="modal-close" onclick="closeModal('reportEmailModal')">&#x2715;</button>
		</div>
		<form action="${pageContext.request.contextPath}/export" method="post">
			<input type="hidden" name="reportEmail" value="1"> <input
				type="hidden" name="format" value="pdf">
			<div class="form-group mb-2">
				<label>Recipient Email *</label> <input type="email" name="email"
					placeholder="yourname@gmail.com" required autofocus>
			</div>
			<p class="text-muted"
				style="font-size: .78rem; margin-bottom: .75rem">This sends the
				complete report PDF — all-time overview, ${selMonth}/${selYear}
				monthly breakdown, daily/weekly/day-of-week stats, category &amp;
				sub-category details — exactly as shown on this page.</p>
			<div class="flex gap-1 mt-2">
				<button type="button" class="btn btn-outline"
					onclick="closeModal('reportEmailModal')">Cancel</button>
				<button type="submit" class="btn btn-primary ml-auto">Send
					Email</button>
			</div>
		</form>
	</div>
</div>

<%@ include file="footer.jsp"%>