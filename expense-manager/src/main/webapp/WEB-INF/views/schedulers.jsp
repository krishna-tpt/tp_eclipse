<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="pageTitle" value="Schedulers" scope="request" />
<c:set var="activePage" value="schedulers" scope="request" />
<%@ include file="header.jsp"%>

<style>
/* ── Scheduler cards ── */
.sched-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
	gap: 1.1rem;
	margin-bottom: 1.5rem;
}

.sched-card {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	overflow: hidden;
}

.sched-header {
	display: flex;
	align-items: center;
	gap: .6rem;
	padding: .75rem 1rem;
	background: #f8fafc;
	border-bottom: 1px solid var(--border);
}

.sched-icon {
	font-size: 1.3rem;
}

.sched-title {
	font-size: .9rem;
	font-weight: 700;
	flex: 1;
}

.sched-body {
	padding: 1rem;
}

/* Status badge */
.status-badge {
	padding: .15rem .55rem;
	border-radius: 20px;
	font-size: .68rem;
	font-weight: 700;
	text-transform: uppercase;
}

.status-badge.SUCCESS {
	background: #dcfce7;
	color: #166534;
}

.status-badge.FAILED {
	background: #fee2e2;
	color: #991b1b;
}

.status-badge.RUNNING {
	background: #dbeafe;
	color: #1e40af;
}

.status-badge.NEVER {
	background: #f1f5f9;
	color: #64748b;
}

/* Toggle switch */
.toggle-wrap {
	display: flex;
	align-items: center;
	gap: .5rem;
}

.toggle {
	position: relative;
	display: inline-block;
	width: 40px;
	height: 22px;
}

.toggle input {
	opacity: 0;
	width: 0;
	height: 0;
}

.slider {
	position: absolute;
	inset: 0;
	background: #cbd5e1;
	border-radius: 22px;
	cursor: pointer;
	transition: .3s;
}

.slider:before {
	content: "";
	position: absolute;
	height: 16px;
	width: 16px;
	left: 3px;
	bottom: 3px;
	background: #fff;
	border-radius: 50%;
	transition: .3s;
}

.toggle input:checked+.slider {
	background: var(--primary);
}

.toggle input:checked+.slider:before {
	transform: translateX(18px);
}

/* Config form in card */
.sched-form .form-row {
	display: flex;
	gap: .5rem;
	align-items: center;
	flex-wrap: wrap;
	margin: .4rem 0;
}

.sched-form label.sm {
	font-size: .78rem;
	color: var(--text-2);
	font-weight: 600;
	min-width: 70px;
}

.sched-form select, .sched-form input[type=number] {
	padding: .3rem .55rem;
	border: 1px solid var(--border);
	border-radius: 6px;
	font-size: .82rem;
}

.day-btns {
	display: flex;
	gap: .25rem;
	flex-wrap: wrap;
}

.day-btn {
	width: 34px;
	height: 28px;
	border: 1px solid var(--border);
	border-radius: 5px;
	font-size: .72rem;
	font-weight: 700;
	cursor: pointer;
	background: #f8fafc;
	display: flex;
	align-items: center;
	justify-content: center;
	user-select: none;
}

.day-btn.selected {
	background: var(--primary);
	color: #fff;
	border-color: var(--primary);
}

/* Month calendar picker */
.month-cal {
	display: grid;
	grid-template-columns: repeat(7, 1fr);
	gap: 3px;
	max-width: 240px;
}

.cal-day {
	height: 26px;
	border: 1px solid var(--border);
	border-radius: 4px;
	font-size: .72rem;
	font-weight: 600;
	cursor: pointer;
	background: #f8fafc;
	display: flex;
	align-items: center;
	justify-content: center;
}

.cal-day.selected {
	background: var(--primary);
	color: #fff;
	border-color: var(--primary);
}

/* Stats row */
.sched-stats {
	display: flex;
	gap: 1rem;
	margin-bottom: .75rem;
	flex-wrap: wrap;
}

.stat-pill {
	font-size: .74rem;
	color: var(--text-2);
}

.stat-pill strong {
	color: var(--text-1);
}

/* Log table */
.log-section h3 {
	font-size: .9rem;
	font-weight: 700;
	margin: 0 0 .75rem;
}

.log-row {
	display: grid;
	grid-template-columns: 1fr 90px 70px 80px;
	gap: .5rem;
	font-size: .76rem;
	padding: .3rem 0;
	border-bottom: 1px solid var(--border);
	align-items: center;
}

.log-row.header {
	font-weight: 700;
	color: var(--text-2);
	font-size: .68rem;
	text-transform: uppercase;
}

/* Config overview box */
.config-overview {
	background: #eff6ff;
	border: 1px solid #bfdbfe;
	border-radius: 8px;
	padding: .6rem .85rem;
	font-size: .78rem;
	color: #1e40af;
	margin-top: .6rem;
}
</style>

<%-- ═══ PAGE HEADER ═══ --%>
<div class="page-header flex">
	<div>
		<h1>&#9201; Schedulers</h1>
		<p>Automated tasks — configure, monitor &amp; run manually</p>
	</div>
</div>

<c:if test="${not empty param.success}">
	<div class="alert alert-success">&#10003; Saved!</div>
</c:if>
<c:if test="${not empty param.msg}">
	<div class="alert alert-success">
		&#9654; Scheduler <strong>${param.name}</strong> started!
	</div>
</c:if>
<c:if test="${not empty param.error}">
	<div class="alert alert-error">&#10007; ${param.error}</div>
</c:if>
<c:if test="${not empty dbError}">
	<div class="alert alert-error">&#9888; ${dbError}</div>
</c:if>

<%-- ═══ SCHEDULER CARDS ═══ --%>
<div class="sched-grid">
	<c:forEach var="s" items="${schedulers}">
		<div class="sched-card" id="card-${s.id}">

			<%-- Header --%>
			<div class="sched-header">
				<span class="sched-icon"> <c:choose>
						<c:when test="${s.name=='BACKUP'}">&#128230;</c:when>
						<c:when test="${s.name=='CASHBOOK'}">&#128218;</c:when>
						<c:when test="${s.name=='BUDGET'}">&#127811;</c:when>
						<c:when test="${s.name=='NEON_SYNC'}">&#9729;&#65039;</c:when>
						<c:otherwise>&#9201;</c:otherwise>
					</c:choose>
				</span> <span class="sched-title">${s.displayName}</span>
				<%-- Status badge --%>
				<c:choose>
					<c:when test="${empty s.lastRunStatus}">
						<span class="status-badge NEVER">Never run</span>
					</c:when>
					<c:otherwise>
						<span class="status-badge ${s.lastRunStatus}">${s.lastRunStatus}</span>
					</c:otherwise>
				</c:choose>
			</div>

			<%-- Body --%>
			<div class="sched-body">

				<%-- Stats --%>
				<div class="sched-stats">
					<div class="stat-pill">
						Last: <strong>${s.lastRunDisplay}</strong>
					</div>
					<div class="stat-pill">
						Next: <strong>${s.nextRunDisplay}</strong>
					</div>
				</div>
				<c:if test="${not empty s.lastRunMsg}">
					<div
						style="font-size: .73rem; color: var(--text-2); margin-bottom: .5rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap"
						title="${s.lastRunMsg}">${s.lastRunMsg}</div>
				</c:if>

				<%-- Config form --%>
				<form method="post"
					action="${pageContext.request.contextPath}/schedulers"
					class="sched-form" id="form-${s.id}">
					<input type="hidden" name="action" value="save"> <input
						type="hidden" name="id" value="${s.id}">

					<%-- Enable toggle --%>
					<div class="form-row" style="margin-bottom: .6rem">
						<label class="sm">Enable</label> <label class="toggle"> <input
							type="checkbox" name="enabled" value="on"
							${s.enabled?'checked':''} onchange="this.form.submit()">
							<span class="slider"></span>
						</label> <span style="font-size: .75rem; color: var(--text-2)">${s.enabled?'Enabled':'Disabled'}</span>
					</div>

					<%-- Repeat type --%>
					<div class="form-row">
						<label class="sm">Repeat</label> <select name="repeatType"
							id="rt-${s.id}" onchange="onRepeatChange(${s.id})">
							<option value="DAILY" ${s.repeatType=='DAILY'  ?'selected':''}>Every
								Day</option>
							<option value="WEEKLY" ${s.repeatType=='WEEKLY' ?'selected':''}>Weekly
								Once</option>
							<option value="MONTHLY" ${s.repeatType=='MONTHLY'?'selected':''}>Monthly
								Once</option>
						</select>
					</div>

					<%-- WEEKLY: day picker --%>
					<div id="weekly-${s.id}" class="form-row"
						style="${s.repeatType=='WEEKLY'?'':'display:none'}">
						<label class="sm">Days</label>
						<div class="day-btns" id="daybtn-${s.id}">
							<c:forEach var="d" items="${'SUN,MON,TUE,WED,THU,FRI,SAT'.split(',')}">
								<c:set var="sel"
									value="${not empty s.repeatDays and s.repeatDays.contains(d)}" />
								<div class="day-btn ${sel?'selected':''}"
									onclick="toggleDay(${s.id},'${d}',this)">${d}</div>
							</c:forEach>
						</div>
						<input type="hidden" name="weekDays" id="weekdays-${s.id}"
							value="${not empty s.repeatDays?s.repeatDays:''}">
					</div>

					<%-- MONTHLY: calendar picker --%>
					<div id="monthly-${s.id}" class="form-row"
						style="${s.repeatType=='MONTHLY'?'':'display:none'}">
						<label class="sm">Day</label>
						<div class="month-cal" id="monthcal-${s.id}">
							<c:forEach begin="1" end="31" var="dd">
								<c:set var="selDay" value="${s.repeatDays == dd.toString()}" />
								<div class="cal-day ${selDay?'selected':''}"
									onclick="selectMonthDay(${s.id},${dd},this)">${dd}</div>
							</c:forEach>
						</div>
						<input type="hidden" name="monthDay" id="monthday-${s.id}"
							value="${not empty s.repeatDays?s.repeatDays:'1'}">
					</div>

					<%-- Time --%>
					<div class="form-row">
						<label class="sm">Time</label> <select name="runHour"
							id="rh-${s.id}">
							<c:forEach begin="0" end="23" var="h">
								<option value="${h}" ${h==s.runHour?'selected':''}>${h<10?'0':''}${h}</option>
							</c:forEach>
						</select> <span style="font-size: .78rem; font-weight: 700">Hrs</span> <select
							name="runMinute" id="rm-${s.id}">
							<c:forEach begin="0" end="59" var="m">
								<option value="${m}" ${m==s.runMinute?'selected':''}>${m<10?'0':''}${m}</option>
							</c:forEach>
						</select> <span style="font-size: .78rem; font-weight: 700">Mins</span>
					</div>

					<%-- Config overview --%>
					<div class="config-overview" id="overview-${s.id}">&#128203;
						${s.repeatDescription}</div>

					<%-- Action buttons --%>
					<div class="flex gap-1 mt-2">
						<button type="submit" class="btn btn-primary btn-sm">&#128190;
							Save</button>
						<button type="button" class="btn btn-outline btn-sm"
							onclick="runNow('${s.name}','${s.displayName}')">
							&#9654; Run Now</button>
					</div>
				</form>
			</div>
		</div>
	</c:forEach>
</div>

<%-- ═══ RUN HISTORY LOG ═══ --%>
<div class="log-section"
	style="background: #fff; border: 1px solid var(--border); border-radius: var(--radius); padding: 1rem">
	<h3>&#128203; Recent Run History</h3>
	<div class="log-row header">
		<span>Scheduler</span><span>Started</span><span>Status</span><span>Duration</span>
	</div>
	<c:forEach var="lg" items="${recentLogs}">
		<div class="log-row">
			<span style="font-weight: 500">${lg.schedulerName} <c:if
					test="${not empty lg.message}">
					<span title="${lg.message}"
						style="cursor: help; color: var(--text-2)">&#8505;</span>
				</c:if>
			</span> <span class="text-muted" style="font-size: .72rem">${lg.startedAt}</span>
			<span><span class="status-badge ${lg.status}">${lg.status}</span></span>
			<span class="text-muted">${lg.durationDisplay}</span>
		</div>
	</c:forEach>
	<c:if test="${empty recentLogs}">
		<div class="empty-state" style="padding: 1.5rem">No run history
			yet.</div>
	</c:if>
</div>

<%-- Run Now confirm form (hidden) --%>
<form method="post"
	action="${pageContext.request.contextPath}/schedulers" id="runNowForm">
	<input type="hidden" name="action" value="runNow"> <input
		type="hidden" name="name" id="runNowName">
</form>

<script>
// ── Repeat type toggle ─────────────────────────────────
function onRepeatChange(id) {
  var rt = document.getElementById('rt-' + id).value;
  document.getElementById('weekly-'  + id).style.display = rt === 'WEEKLY'  ? '' : 'none';
  document.getElementById('monthly-' + id).style.display = rt === 'MONTHLY' ? '' : 'none';
  updateOverview(id);
}

// ── Day button toggle ──────────────────────────────────
function toggleDay(id, day, el) {
  el.classList.toggle('selected');
  var selected = Array.from(
    document.querySelectorAll('#daybtn-' + id + ' .day-btn.selected')
  ).map(function(b){ return b.textContent.trim(); });
  document.getElementById('weekdays-' + id).value = selected.join(',');
  updateOverview(id);
}

// ── Month day select ───────────────────────────────────
function selectMonthDay(id, day, el) {
  document.querySelectorAll('#monthcal-' + id + ' .cal-day')
    .forEach(function(d){ d.classList.remove('selected'); });
  el.classList.add('selected');
  document.getElementById('monthday-' + id).value = day;
  updateOverview(id);
}

// ── Config overview text ───────────────────────────────
function updateOverview(id) {
  var rt  = document.getElementById('rt-' + id).value;
  var h   = document.getElementById('rh-' + id).value;
  var m   = document.getElementById('rm-' + id).value;
  var hh  = String(h).padStart(2,'0');
  var mm  = String(m).padStart(2,'0');
  var msg = '';

  if (rt === 'DAILY') {
    msg = 'The task will run every day at ' + hh + ':' + mm + ' Hrs.';
  } else if (rt === 'WEEKLY') {
    var days = document.getElementById('weekdays-' + id).value || 'no day selected';
    msg = 'The task will run weekly on ' + days + ' at ' + hh + ':' + mm + ' Hrs.';
  } else if (rt === 'MONTHLY') {
    var day = document.getElementById('monthday-' + id).value || '1';
    msg = 'The task will run monthly on day ' + day + ' at ' + hh + ':' + mm + ' Hrs.';
  }

  document.getElementById('overview-' + id).innerHTML = '\uD83D\uDCCB ' + msg;
}

// ── Run Now ───────────────────────────────────────────
function runNow(name, displayName) {
  if (!confirm('Run "' + displayName + '" now?')) return;
  document.getElementById('runNowName').value = name;
  document.getElementById('runNowForm').submit();
}

// ── Auto-refresh every 15s if any RUNNING ─────────────
(function autoRefresh() {
  var hasRunning = document.querySelector('.status-badge.RUNNING');
  if (hasRunning) setTimeout(function(){ location.reload(); }, 15000);
})();

// Init hour/min selects → update overview
document.addEventListener('DOMContentLoaded', function() {
  <c:forEach var="s" items="${schedulers}">
    document.getElementById('rh-${s.id}').addEventListener('change', function(){ updateOverview(${s.id}); });
    document.getElementById('rm-${s.id}').addEventListener('change', function(){ updateOverview(${s.id}); });
  </c:forEach>
});
</script>

<%@ include file="footer.jsp"%>