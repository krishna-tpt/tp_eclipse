<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="pageTitle" value="Application Log" scope="request" />
<c:set var="activePage" value="log" scope="request" />
<%@ include file="header.jsp"%>

<style>
.log-toolbar {
	display: flex;
	align-items: center;
	gap: .6rem;
	flex-wrap: wrap;
	margin-bottom: .75rem;
}

.log-toolbar h1 {
	font-size: 1rem;
	font-weight: 700;
	margin: 0;
}

.log-wrap {
	background: #0f172a;
	border-radius: var(--radius);
	padding: 1rem;
	height: calc(100vh - 200px);
	overflow-y: auto;
	font-family: 'Cascadia Code', 'Fira Code', 'Consolas', monospace;
	font-size: .78rem;
	line-height: 1.6;
	position: relative;
}

.log-line {
	display: block;
	white-space: pre-wrap;
	word-break: break-all;
	padding: .05rem 0;
	border-bottom: 1px solid rgba(255, 255, 255, .03);
}

.log-line.ERROR {
	color: #f87171;
}

.log-line.WARN {
	color: #fbbf24;
}

.log-line.INFO {
	color: #86efac;
}

.log-line.DEBUG {
	color: #93c5fd;
}

.log-line.TRACE {
	color: #94a3b8;
}

.log-line.SEVERE {
	color: #f87171;
}

.log-line:hover {
	background: rgba(255, 255, 255, .04);
}

.log-line.hidden {
	display: none;
}

.hl {
	background: #fef08a;
	color: #0f172a;
	border-radius: 2px;
	padding: 0 1px;
}

.status-dot {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background: #94a3b8;
	flex-shrink: 0;
	transition: background .3s;
}

.status-dot.connected {
	background: #4ade80;
	box-shadow: 0 0 6px #4ade80;
}

.status-dot.error {
	background: #f87171;
}

.log-count {
	font-size: .75rem;
	color: var(--text-2);
}

.level-btns {
	display: flex;
	gap: .3rem;
}

.lbtn {
	padding: .2rem .55rem;
	border-radius: 4px;
	font-size: .7rem;
	font-weight: 700;
	cursor: pointer;
	border: 1px solid transparent;
	transition: opacity .15s;
}

.lbtn.active {
	opacity: 1;
}

.lbtn.inactive {
	opacity: .35;
}

.lbtn.ERROR {
	background: #fee2e2;
	color: #991b1b;
	border-color: #fca5a5;
}

.lbtn.WARN {
	background: #fef9c3;
	color: #92400e;
	border-color: #fde047;
}

.lbtn.INFO {
	background: #dcfce7;
	color: #166534;
	border-color: #86efac;
}

.lbtn.DEBUG {
	background: #dbeafe;
	color: #1e40af;
	border-color: #93c5fd;
}

.lbtn.TRACE {
	background: #f1f5f9;
	color: #475569;
	border-color: #cbd5e1;
}

#searchBox {
	padding: .3rem .65rem;
	border-radius: 6px;
	border: 1px solid var(--border);
	font-size: .8rem;
	width: 180px;
}

.auto-scroll-btn {
	font-size: .75rem;
	padding: .28rem .65rem;
}
</style>

<div class="log-toolbar">
	<div class="status-dot" id="statusDot" title="SSE connection status"></div>
	<h1>&#128203; Application Log</h1>
	<span class="log-count" id="lineCount">0 lines</span>

	<div class="level-btns">
		<button class="lbtn ERROR active" onclick="toggleLevel('ERROR',this)">ERROR</button>
		<button class="lbtn WARN  active" onclick="toggleLevel('WARN', this)">WARN</button>
		<button class="lbtn INFO  active" onclick="toggleLevel('INFO', this)">INFO</button>
		<button class="lbtn DEBUG active" onclick="toggleLevel('DEBUG',this)">DEBUG</button>
		<button class="lbtn TRACE active" onclick="toggleLevel('TRACE',this)">TRACE</button>
	</div>

	<input id="searchBox" type="text"
		placeholder="&#128269; Filter text&#8230;"
		oninput="applySearch(this.value)">

	<button class="btn btn-outline btn-sm auto-scroll-btn"
		id="autoScrollBtn" onclick="toggleAutoScroll()">&#8595;
		Auto-scroll ON</button>

	<%-- isELIgnored இல்லாம் normal EL use பண்றோம் --%>
	<form method="post"
		action="${pageContext.request.contextPath}/log/clear"
		style="margin-left: auto">
		<button type="submit" class="btn btn-outline btn-sm"
			style="color: var(--red)"
			onclick="return confirm('Clear log buffer?')">&#128465;
			Clear</button>
	</form>
</div>

<div class="log-wrap" id="logWrap">
	<span style="color: #475569; font-size: .75rem">Connecting to
		log stream&#8230;</span>
</div>

<script>
// ── CTX: scriptlet use பண்றோம் — EL/template literal conflict இல்லை ──
var CTX      = '<%=request.getContextPath()%>';
var logWrap  = document.getElementById('logWrap');
var statusDot= document.getElementById('statusDot');
var countEl  = document.getElementById('lineCount');

var autoScroll   = true;
var totalLines   = 0;
var hiddenLevels = new Set();
var searchTerm   = '';
var es           = null; // ← single variable, everywhere same name

// ── SSE connection ─────────────────────────────────────
function connect() {
  es = new EventSource(CTX + '/log/stream'); // ✅ CTX, es — same variable

  es.onopen = function() {
    statusDot.className = 'status-dot connected';
    statusDot.title     = 'Connected';
  };

  es.onmessage = function(e) {
    appendLine(e.data);
  };

  es.onerror = function() {
    statusDot.className = 'status-dot error';
    statusDot.title     = 'Disconnected — reconnecting...';
    es.close();
    setTimeout(connect, 3000);
  };
}

// ── Append a log line to the UI ────────────────────────
function appendLine(text) {
  var level = 'INFO';
  var m = text.match(/^\d{2}:\d{2}:\d{2} (ERROR|SEVERE|WARN |WARN|INFO |INFO|DEBUG|TRACE)/);
  if (m) level = m[1].trim();
  if (level === 'SEVERE') level = 'ERROR'; // Tomcat SEVERE → ERROR color

  var span = document.createElement('span');
  span.className     = 'log-line ' + level;
  span.dataset.level = level;
  span.dataset.raw   = text;
  span.innerHTML     = highlight(escapeHtml(text), searchTerm);

  if (hiddenLevels.has(level)) span.classList.add('hidden');

  if (totalLines === 0) logWrap.innerHTML = '';

  logWrap.appendChild(span);
  totalLines++;
  countEl.textContent = totalLines + ' lines';

  var lines = logWrap.querySelectorAll('.log-line');
  if (lines.length > 500) lines[0].remove();

  if (autoScroll) logWrap.scrollTop = logWrap.scrollHeight;
}

function escapeHtml(s) {
  return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}

function highlight(html, term) {
  if (!term) return html;
  // escapeRegex — no template literals, plain string concat
  var escaped = term
    .replace(/\\/g, '\\\\')
    .replace(/\./g, '\\.')
    .replace(/\*/g, '\\*')
    .replace(/\+/g, '\\+')
    .replace(/\?/g, '\\?')
    .replace(/\^/g, '\\^')
    .replace(/\$/g, '\\$')
    .replace(/\{/g, '\\{')
    .replace(/\}/g, '\\}')
    .replace(/\(/g, '\\(')
    .replace(/\)/g, '\\)')
    .replace(/\|/g, '\\|')
    .replace(/\[/g, '\\[')
    .replace(/\]/g, '\\]');
  var re = new RegExp('(' + escaped + ')', 'gi');
  return html.replace(re, '<span class="hl">$1</span>');
}

function toggleLevel(level, btn) {
  if (hiddenLevels.has(level)) {
    hiddenLevels.delete(level);
    btn.classList.replace('inactive', 'active');
  } else {
    hiddenLevels.add(level);
    btn.classList.replace('active', 'inactive');
  }
  logWrap.querySelectorAll('.log-line[data-level="' + level + '"]').forEach(function(el) {
    el.classList.toggle('hidden', hiddenLevels.has(level));
  });
}

function applySearch(val) {
  searchTerm = val.trim();
  logWrap.querySelectorAll('.log-line').forEach(function(el) {
    var raw = el.dataset.raw  || '';
    var lv  = el.dataset.level|| 'INFO';
    var matchesLevel  = !hiddenLevels.has(lv);
    var matchesSearch = !searchTerm || raw.toLowerCase().indexOf(searchTerm.toLowerCase()) >= 0;
    el.classList.toggle('hidden', !(matchesLevel && matchesSearch));
    if (matchesLevel && matchesSearch) {
      el.innerHTML = highlight(escapeHtml(raw), searchTerm);
    }
  });
}

function toggleAutoScroll() {
  autoScroll = !autoScroll;
  document.getElementById('autoScrollBtn').textContent =
    (autoScroll ? '\u2193 Auto-scroll ON' : '\u2193 Auto-scroll OFF');
}

logWrap.addEventListener('scroll', function() {
  var atBottom = logWrap.scrollHeight - logWrap.scrollTop - logWrap.clientHeight < 40;
  if (!atBottom && autoScroll) {
    autoScroll = false;
    document.getElementById('autoScrollBtn').textContent = '\u2193 Auto-scroll OFF';
  }
});

connect();
</script>

<%@ include file="footer.jsp"%>