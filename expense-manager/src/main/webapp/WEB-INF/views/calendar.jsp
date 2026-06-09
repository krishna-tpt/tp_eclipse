<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle"  value="Calendar"  scope="request"/>
<c:set var="activePage" value="calendar"  scope="request"/>
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>" scope="request"/>
<%@ include file="header.jsp" %>

<style>
.cal-nav { display:flex; align-items:center; gap:1rem; margin-bottom:1.25rem; }
.cal-nav h2 { font-size:1.2rem; font-weight:700; min-width:180px; text-align:center; }
.cal-grid { display:grid; grid-template-columns:repeat(7,1fr); gap:2px; background:var(--border); border:1px solid var(--border); border-radius:var(--radius); overflow:hidden; }
.cal-day-header { background:#f8fafc; text-align:center; padding:.5rem .25rem; font-size:.72rem; font-weight:700; text-transform:uppercase; color:var(--text-2); letter-spacing:.5px; }
.cal-cell { background:#fff; min-height:90px; padding:.4rem .5rem; cursor:pointer; transition:background .12s; position:relative; }
.cal-cell:hover { background:#f0f7ff; }
.cal-cell.empty { background:#fafafa; cursor:default; }
.cal-cell.today { background:#eff6ff; }
.cal-cell.today .cal-date { color:var(--primary); font-weight:700; }
.cal-cell.selected { outline:2px solid var(--primary); outline-offset:-2px; background:#dbeafe; }
.cal-date { font-size:.8rem; font-weight:600; color:var(--text); margin-bottom:.25rem; }
.cal-income  { font-size:.7rem; color:#15803d; font-weight:600; line-height:1.4; }
.cal-expense { font-size:.7rem; color:#b91c1c; font-weight:600; line-height:1.4; }
.cal-bar { position:absolute; bottom:0; left:0; right:0; height:3px; }
.view-toggle { display:flex; gap:.25rem; background:var(--bg); border:1px solid var(--border); border-radius:8px; padding:.2rem; }
.view-btn { padding:.3rem .75rem; border:none; background:transparent; border-radius:6px; font-size:.8rem; font-weight:500; cursor:pointer; transition:all .15s; font-family:inherit; color:var(--text-2); }
.view-btn.active { background:#fff; color:var(--text); box-shadow:0 1px 3px rgba(0,0,0,.08); }
.day-panel { background:#fff; border:1px solid var(--border); border-radius:var(--radius); padding:1.25rem; margin-top:1.25rem; display:none; }
.day-panel.show { display:block; }
.week-grid { display:grid; grid-template-columns:repeat(7,1fr); gap:.5rem; }
.week-cell { background:#fff; border:1px solid var(--border); border-radius:8px; padding:.75rem .5rem; min-height:120px; cursor:pointer; transition:background .1s; }
.week-cell:hover { background:#f0f7ff; }
.week-cell.today { border-color:var(--primary); border-width:2px; }
.week-cell-label { font-size:.72rem; font-weight:700; margin-bottom:.4rem; color:var(--text-2); }
.cal-summary { display:flex; gap:1rem; flex-wrap:wrap; margin-bottom:1.25rem; background:#fff; border:1px solid var(--border); border-radius:var(--radius); padding:.75rem 1rem; align-items:center; }
.sum-item .sum-label { font-size:.65rem; font-weight:600; text-transform:uppercase; color:var(--text-2); }
.sum-item .sum-val { font-size:1.05rem; font-weight:700; }
</style>

<div class="page-header flex">
  <div>
    <h1>&#128197; Calendar</h1>
    <p>${sessionScope.activeBookName} — income &amp; expense overview</p>
  </div>
  <div class="view-toggle ml-auto">
    <button class="view-btn active" id="btnMonth" onclick="switchView('month')">Monthly</button>
    <button class="view-btn"        id="btnWeek"  onclick="switchView('week')">Weekly</button>
    <button class="view-btn"        id="btnDay"   onclick="switchView('day')">Daily</button>
  </div>
</div>

<%-- DB error --%>
<c:if test="${not empty dbError}">
  <div class="alert alert-error">&#10007; ${dbError}</div>
</c:if>

<%-- Month nav --%>
<div class="cal-nav">
  <a href="${pageContext.request.contextPath}/calendar?year=${month==1?year-1:year}&month=${month==1?12:month-1}"
     class="btn btn-outline btn-sm">&#8592; Prev</a>
  <h2 id="calTitle">Loading&#8230;</h2>
  <a href="${pageContext.request.contextPath}/calendar?year=${month==12?year+1:year}&month=${month==12?1:month+1}"
     class="btn btn-outline btn-sm">Next &#8594;</a>
  <a href="${pageContext.request.contextPath}/calendar" class="btn btn-outline btn-sm">Today</a>
</div>

<%-- Summary row --%>
<div class="cal-summary">
  <div class="sum-item">
    <div class="sum-label">Month Income</div>
    <div class="sum-val" id="sumIncome" style="color:var(--green)">&#8377;0</div>
  </div>
  <div class="sum-item">
    <div class="sum-label">Month Expense</div>
    <div class="sum-val" id="sumExpense" style="color:var(--red)">&#8377;0</div>
  </div>
  <div class="sum-item">
    <div class="sum-label">Net Balance</div>
    <div class="sum-val" id="sumNet" style="color:var(--primary)">&#8377;0</div>
  </div>
  <div class="sum-item" style="margin-left:auto">
    <div class="sum-label">Active Days</div>
    <div class="sum-val" id="sumDays">0</div>
  </div>
</div>

<%-- Month view --%>
<div id="viewMonth">
  <div class="cal-grid">
    <div class="cal-day-header">Sun</div>
    <div class="cal-day-header">Mon</div>
    <div class="cal-day-header">Tue</div>
    <div class="cal-day-header">Wed</div>
    <div class="cal-day-header">Thu</div>
    <div class="cal-day-header">Fri</div>
    <div class="cal-day-header">Sat</div>
    <div id="calBody" style="display:contents"></div>
  </div>
</div>

<%-- Week view --%>
<div id="viewWeek" style="display:none">
  <div class="week-grid" id="weekBody"></div>
</div>

<%-- Day view --%>
<div id="viewDay" style="display:none">
  <div class="card" id="dayBody"></div>
</div>

<%-- Day detail slide-in --%>
<div class="day-panel" id="dayPanel">
  <div class="flex mb-2">
    <h3 id="dayPanelTitle" style="font-size:1rem;font-weight:700"></h3>
    <button class="btn btn-outline btn-sm ml-auto"
            onclick="document.getElementById('dayPanel').classList.remove('show')">&#x2715;</button>
  </div>
  <div id="dayPanelBody"></div>
  <div class="flex gap-1 mt-2" id="dayPanelActions"></div>
</div>

<%-- Extract Java values into page-scope vars to avoid ${} inside <script> --%>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" />

<script>
<%-- Use scriptlet to emit Java values safely --%>
var dailyData = ${dailyJson};
var CAL_YEAR  = ${year};
var CAL_MONTH = ${month};
var TODAY     = '<c:out value="${today}"/>';
var CTX       = '<c:out value="${ctxPath}"/>';
var MONTH_NAMES = ['','January','February','March','April','May','June',
                   'July','August','September','October','November','December'];
var DAY_NAMES   = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];

// Build lookup
var dataMap = {};
var totIncome = 0, totExpense = 0;
dailyData.forEach(function(d) {
  dataMap[d.day] = d;
  totIncome  += +d.income  || 0;
  totExpense += +d.expense || 0;
});

// Summary
document.getElementById('calTitle').textContent  = MONTH_NAMES[CAL_MONTH] + ' ' + CAL_YEAR;
document.getElementById('sumIncome').textContent  = '₹' + fmt(totIncome);
document.getElementById('sumExpense').textContent = '₹' + fmt(totExpense);
document.getElementById('sumNet').textContent     = '₹' + fmt(totIncome - totExpense);
document.getElementById('sumDays').textContent    = dailyData.length;

// ── Month View ────────────────────────────────────────
function buildMonth() {
  var body     = document.getElementById('calBody');
  body.innerHTML = '';
  var firstDow = new Date(CAL_YEAR, CAL_MONTH - 1, 1).getDay();
  var daysInMo = new Date(CAL_YEAR, CAL_MONTH, 0).getDate();

  // Empty prefix cells
  for (var i = 0; i < firstDow; i++) {
    var e = document.createElement('div');
    e.className = 'cal-cell empty';
    body.appendChild(e);
  }

  for (var d = 1; d <= daysInMo; d++) {
    var ds   = pad(CAL_YEAR) + '-' + pad(CAL_MONTH) + '-' + pad(d);
    var data = dataMap[ds];
    var cell = document.createElement('div');
    cell.className = 'cal-cell' + (ds === TODAY ? ' today' : '');
    cell.dataset.date = ds;
    (function(ds, data){ cell.onclick = function(){ showPanel(ds, data); }; })(ds, data);

    var h = '<div class="cal-date">' + d + '</div>';
    if (data) {
      if (+data.income  > 0) h += '<div class="cal-income">+₹' + fmt(data.income)  + '</div>';
      if (+data.expense > 0) h += '<div class="cal-expense">-₹' + fmt(data.expense) + '</div>';
      var color = +data.income > +data.expense ? '#dcfce7' : '#fee2e2';
      h += '<div class="cal-bar" style="background:' + color + '"></div>';
    }
    cell.innerHTML = h;
    body.appendChild(cell);
  }
}

// ── Week View ─────────────────────────────────────────
function buildWeek() {
  var body = document.getElementById('weekBody');
  body.innerHTML = '';
  var anchorStr  = TODAY >= pad(CAL_YEAR)+'-'+pad(CAL_MONTH) ? TODAY
                   : pad(CAL_YEAR)+'-'+pad(CAL_MONTH)+'-01';
  var anchor     = new Date(anchorStr);
  var dow        = anchor.getDay();
  var weekStart  = new Date(anchor);
  weekStart.setDate(anchor.getDate() - dow);

  for (var i = 0; i < 7; i++) {
    var d    = new Date(weekStart); d.setDate(weekStart.getDate() + i);
    var ds   = d.toISOString().split('T')[0];
    var data = dataMap[ds];
    var cell = document.createElement('div');
    cell.className = 'week-cell' + (ds === TODAY ? ' today' : '');
    (function(ds, data){ cell.onclick = function(){ showPanel(ds, data); }; })(ds, data);

    var h = '<div class="week-cell-label">' + DAY_NAMES[d.getDay()] + ' ' + d.getDate() + '</div>';
    if (data) {
      if (+data.income  > 0) h += '<div class="cal-income">+₹' + fmt(data.income)  + '</div>';
      if (+data.expense > 0) h += '<div class="cal-expense">-₹' + fmt(data.expense) + '</div>';
    } else {
      h += '<div style="font-size:.72rem;color:var(--text-3);margin-top:.25rem">No activity</div>';
    }
    cell.innerHTML = h;
    body.appendChild(cell);
  }
}

// ── Day View ──────────────────────────────────────────
function buildDay() {
  var ds   = TODAY.startsWith(pad(CAL_YEAR)+'-'+pad(CAL_MONTH)) ? TODAY
             : pad(CAL_YEAR)+'-'+pad(CAL_MONTH)+'-01';
  var data = dataMap[ds];
  var d    = new Date(ds+'T00:00:00');
  document.getElementById('dayBody').innerHTML =
    '<div class="card-title">' + d.toLocaleDateString('en-IN',{weekday:'long',year:'numeric',month:'long',day:'numeric'}) + '</div>'
    + summaryCards(data)
    + '<div class="flex gap-1 mt-2">'
    +   '<a href="' + CTX + '/transactions?dateFrom=' + ds + '&dateTo=' + ds + '" class="btn btn-outline btn-sm">View Transactions</a>'
    +   '<a href="' + CTX + '/home" class="btn btn-primary btn-sm ml-auto">+ Add Entry</a>'
    + '</div>';
}

// ── Panel ─────────────────────────────────────────────
function showPanel(ds, data) {
  document.querySelectorAll('.cal-cell.selected,.week-cell.selected')
    .forEach(function(el){ el.classList.remove('selected'); });
  var el = document.querySelector('[data-date="' + ds + '"]');
  if (el) el.classList.add('selected');

  var d = new Date(ds + 'T00:00:00');
  document.getElementById('dayPanelTitle').textContent =
    d.toLocaleDateString('en-IN', {weekday:'long', year:'numeric', month:'long', day:'numeric'});
  document.getElementById('dayPanelBody').innerHTML = summaryCards(data);
  document.getElementById('dayPanelActions').innerHTML =
    '<a href="' + CTX + '/transactions?dateFrom=' + ds + '&dateTo=' + ds + '" class="btn btn-outline btn-sm">View Transactions &#8594;</a>'
    + '<a href="' + CTX + '/home" class="btn btn-success btn-sm ml-auto">+ Add Entry</a>';
  document.getElementById('dayPanel').classList.add('show');
}

function summaryCards(data) {
  if (!data) return '<div class="text-muted" style="font-size:.875rem;padding:.5rem 0">No transactions on this day.</div>';
  var net = +data.income - +data.expense;
  return '<div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:.6rem;margin-top:.5rem">'
    + '<div style="background:#dcfce7;border-radius:8px;padding:.6rem .75rem">'
    +   '<div style="font-size:.65rem;color:#15803d;font-weight:700;text-transform:uppercase">Income</div>'
    +   '<div style="font-size:1rem;font-weight:700;color:#15803d">₹' + fmt(data.income) + '</div>'
    + '</div>'
    + '<div style="background:#fee2e2;border-radius:8px;padding:.6rem .75rem">'
    +   '<div style="font-size:.65rem;color:#b91c1c;font-weight:700;text-transform:uppercase">Expense</div>'
    +   '<div style="font-size:1rem;font-weight:700;color:#b91c1c">₹' + fmt(data.expense) + '</div>'
    + '</div>'
    + '<div style="background:#eff6ff;border-radius:8px;padding:.6rem .75rem">'
    +   '<div style="font-size:.65rem;color:var(--primary);font-weight:700;text-transform:uppercase">Net</div>'
    +   '<div style="font-size:1rem;font-weight:700;color:' + (net>=0?'#15803d':'#b91c1c') + '">₹' + fmt(net) + '</div>'
    + '</div>'
    + '</div>';
}

function switchView(v) {
  ['Month','Week','Day'].forEach(function(x){
    document.getElementById('view'+x).style.display = x.toLowerCase()===v ? '' : 'none';
    document.getElementById('btn'+x).classList.toggle('active', x.toLowerCase()===v);
  });
  document.getElementById('dayPanel').classList.remove('show');
  if (v === 'week') buildWeek();
  if (v === 'day')  buildDay();
}

function fmt(n)  { return parseFloat(n||0).toLocaleString('en-IN',{minimumFractionDigits:2,maximumFractionDigits:2}); }
function pad(n)  { return String(n).padStart(2,'0'); }

buildMonth();
</script>

<%@ include file="footer.jsp" %>
