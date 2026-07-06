<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="pageTitle" value="Bulk Add Transactions" scope="request" />
<c:set var="activePage" value="txn" scope="request" />
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>"
	scope="request" />
<%@ include file="header.jsp"%>

<style>
.bulk-table {
	width: 100%;
	border-collapse: collapse;
	font-size: .82rem;
}

.bulk-table th {
	text-align: left;
	padding: .4rem .5rem;
	font-weight: 600;
	color: var(--text-2);
	border-bottom: 2px solid var(--border);
	white-space: nowrap;
}

.bulk-table td {
	padding: .3rem .35rem;
	vertical-align: middle;
}

.bulk-table tr:nth-child(even) td {
	background: #fafafa;
}

.bulk-table input, .bulk-table select {
	width: 100%;
	padding: .35rem .45rem;
	border: 1px solid var(--border);
	border-radius: 6px;
	font-size: .82rem;
	font-family: inherit;
	background: #fff;
	box-sizing: border-box;
}

.bulk-table input:focus, .bulk-table select:focus {
	outline: none;
	border-color: var(--primary);
	box-shadow: 0 0 0 3px rgba(37, 99, 235, .08);
}

.col-row {
	width: 32px;
	text-align: center;
	color: var(--text-2);
	font-size: .75rem;
}

.col-type {
	width: 100px;
}

.col-date {
	width: 155px;
}

.col-amt {
	width: 110px;
}

.col-cat {
	width: 150px;
}

.col-sub {
	width: 140px;
}

.col-del {
	width: 30px;
	text-align: center;
}

.bulk-del-btn {
	background: none;
	border: none;
	cursor: pointer;
	color: #cbd5e1;
	font-size: 1rem;
	padding: 0 4px;
	line-height: 1;
	transition: color .15s;
}

.bulk-del-btn:hover {
	color: var(--red, #e53e3e);
}

.type-badge {
	display: inline-block;
	padding: .25rem .5rem;
	border-radius: 20px;
	font-size: .75rem;
	font-weight: 600;
	cursor: pointer;
	user-select: none;
	border: 1.5px solid transparent;
	width: 100%;
	text-align: center;
	box-sizing: border-box;
	transition: all .15s;
}

.type-badge.expense {
	background: #fee2e2;
	border-color: #fca5a5;
	color: #b91c1c;
}

.type-badge.income {
	background: #dcfce7;
	border-color: #86efac;
	color: #15803d;
}

/* Summary bar */
.bulk-summary-bar {
	display: flex;
	gap: 1.5rem;
	padding: .55rem .85rem;
	background: #f8fafc;
	border-radius: 8px;
	border: 1px solid var(--border);
	margin-bottom: .85rem;
	font-size: .82rem;
	flex-wrap: wrap;
}

.s-label {
	color: var(--text-2);
}

.s-val {
	font-weight: 700;
	margin-left: .25rem;
}

.s-income {
	color: #15803d;
}

.s-expense {
	color: #b91c1c;
}

.s-net {
	color: var(--primary, #4f46e5);
}

.bulk-footer {
	display: flex;
	align-items: center;
	gap: .75rem;
	margin-top: 1rem;
	flex-wrap: wrap;
}

.bulk-result {
	font-size: .82rem;
	margin-left: auto;
	font-weight: 600;
}
</style>

<%-- ═══ PAGE HEADER ═══ --%>
<div class="page-header flex">
	<div>
		<h1>&#9776; Bulk Add Transactions</h1>
		<p>
			Add multiple transactions at once — <strong>${sessionScope.activeBookName}</strong>
		</p>
	</div>
	<div class="flex gap-1 ml-auto">
		<label class="btn btn-outline btn-sm" style="cursor: pointer"
			title="Import CSV or Excel"> &#128196; Import File <input
			type="file" id="importFileInput" accept=".csv,.xlsx,.xls"
			style="display: none" onchange="importFile(this)">
		</label>
		<button class="btn btn-outline btn-sm" onclick="bulkAddRow()">&#43;
			Add Row</button>
		<button class="btn btn-outline btn-sm" onclick="bulkAddRows(5)">&#43;
			5 Rows</button>
		<button class="btn btn-outline btn-sm" onclick="bulkClearAll()"
			style="color: var(--red)">&#10005; Clear All</button>
		<button type="button" class="btn btn-success" id="bulkSaveBtn"
			onclick="bulkSubmit()">&#10003; Save All</button>
	</div>

</div>

<div style="font-size:.75rem;color:var(--text-2);margin-bottom:.75rem;
            background:#eff6ff;border:1px solid #bfdbfe;border-radius:8px;padding:.5rem .85rem">
    &#128196; <strong>Import format (CSV/Excel):</strong>
    Columns in order:
    <code>type</code>,
    <code>date_time</code>,
    <code>amount</code>,
    <code>category</code>,
    <code>sub_category</code>,
    <code>note</code>
    &nbsp;|&nbsp;
    type = <code>INCOME</code> or <code>EXPENSE</code> &nbsp;|&nbsp;
    date_time = <code>2026-06-01 10:30</code>
    &nbsp;|&nbsp; First row = header (skipped)
</div>

<%-- Success / Error alerts --%>
<div id="bulkAlertWrap"></div>

<%-- Live summary bar --%>
<div class="bulk-summary-bar">
	<div>
		<span class="s-label">Rows:</span> <span class="s-val" id="bSumRows">0</span>
	</div>
	<div>
		<span class="s-label">Income:</span> <span class="s-val s-income"
			id="bSumIncome">&#8377;0</span>
	</div>
	<div>
		<span class="s-label">Expense:</span><span class="s-val s-expense"
			id="bSumExpense">&#8377;0</span>
	</div>
	<div>
		<span class="s-label">Net:</span> <span class="s-val s-net"
			id="bSumNet">&#8377;0</span>
	</div>
	<div class="ml-auto" style="font-size: .75rem; color: var(--text-2)">
		Click the type badge to toggle Income / Expense</div>
</div>

<%-- Table --%>
<div class="card" style="padding: 0; overflow: hidden">
	<div style="overflow-x: auto">
		<table class="bulk-table" id="bulkTable">
			<thead>
				<tr>
					<th class="col-row">#</th>
					<th class="col-type">Type</th>
					<th class="col-date">Date &amp; Time *</th>
					<th class="col-amt">Amount (&#8377;) *</th>
					<th class="col-cat">Category *</th>
					<th class="col-sub">Sub Category</th>
					<th class="col-note">Note</th>
					<th class="col-del"></th>
				</tr>
			</thead>
			<tbody id="bulkBody"></tbody>
		</table>
	</div>

	<%-- Footer inside card --%>
	<div class="bulk-footer"
		style="padding: .75rem 1rem; border-top: 1px solid var(--border)">
		<button type="button" class="btn btn-outline btn-sm"
			onclick="bulkAddRow()">&#43; Row</button>
		<button type="button" class="btn btn-outline btn-sm"
			onclick="bulkAddRows(5)">&#43; 5 Rows</button>
		<button type="button" class="btn btn-outline btn-sm"
			onclick="bulkClearAll()" style="color: var(--red)">&#10005;
			Clear</button>
		<span class="bulk-result" id="bulkResult"></span>
		<button type="button" class="btn btn-success" id="bulkSaveBtn"
			onclick="bulkSubmit()">&#10003; Save All</button>
	</div>
</div>

<%-- ═══ Raw category data for JS ═══ --%>
<script>
(function() {

    var CAT_INCOME  = [];
    var CAT_EXPENSE = [];
    var SUBCATS     = [];

    <c:forEach var="c" items="${incomeCategories}">
    CAT_INCOME.push({ id: '${c.id}', name: '${c.name}' });
    </c:forEach>
    <c:forEach var="c" items="${expenseCategories}">
    CAT_EXPENSE.push({ id: '${c.id}', name: '${c.name}' });
    </c:forEach>
    <c:forEach var="s" items="${subCategories}">
    SUBCATS.push({ id: '${s.id}', name: '${s.name}', catId: '${s.category_id}' });
    </c:forEach>

    var rowCount = 0;

    /* ── Helpers ─────────────────────────────────────────── */
    function nowLocal() {
        var now = new Date();
        return new Date(now - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
    }

    function buildCatOptions(type, selectedId) {
        var list = (type === 'INCOME') ? CAT_INCOME : CAT_EXPENSE;
        var html = '<option value="">Select\u2026</option>';
        list.forEach(function(c) {
            html += '<option value="' + c.id + '"' + (c.id === selectedId ? ' selected' : '') + '>' + c.name + '</option>';
        });
        return html;
    }

    function buildSubOptions(catId, selectedId) {
        var html = '<option value="">-</option>';
        SUBCATS.forEach(function(s) {
            if (s.catId === catId) {
                html += '<option value="' + s.id + '"' + (s.id === selectedId ? ' selected' : '') + '>' + s.name + '</option>';
            }
        });
        return html;
    }

    /* ── Add a single row ────────────────────────────────── */
    function bulkAddRow(opts) {
        opts = opts || {};
        var type = opts.type || 'EXPENSE';
        rowCount++;
        var id = 'br' + rowCount;
        var dt = opts.date || nowLocal();

        var tr = document.createElement('tr');
        tr.id = id;
        tr.innerHTML =
            '<td class="col-row">' + rowCount + '</td>'
          + '<td class="col-type">'
          +   '<span class="type-badge ' + type.toLowerCase() + '" onclick="bulkToggleType(\'' + id + '\')">'
          +     type.charAt(0) + type.slice(1).toLowerCase()
          +   '</span>'
          +   '<input type="hidden" class="b-type" value="' + type + '">'
          + '</td>'
          + '<td class="col-date"><input type="datetime-local" class="b-date" value="' + dt + '"></td>'
          + '<td class="col-amt"><input type="number" class="b-amt" min="0.01" step="0.01" placeholder="0.00" oninput="bulkUpdateSummary()"></td>'
          + '<td class="col-cat"><select class="b-cat" onchange="bulkCatChange(\'' + id + '\')">'
          +     buildCatOptions(type, opts.catId || '')
          + '</select></td>'
          + '<td class="col-sub"><select class="b-sub" disabled><option value="">-</option></select></td>'
          + '<td class="col-note"><input type="text" class="b-note" placeholder="Optional"></td>'
          + '<td class="col-del"><button class="bulk-del-btn" onclick="bulkDelRow(\'' + id + '\')" title="Remove row">&#10005;</button></td>';

        document.getElementById('bulkBody').appendChild(tr);

        if (opts.catId) {
            var subSel = tr.querySelector('.b-sub');
            subSel.innerHTML = buildSubOptions(opts.catId, opts.subId || '');
            subSel.disabled  = (SUBCATS.filter(function(s){ return s.catId === opts.catId; }).length === 0);
        }
        bulkUpdateSummary();
    }
    window.bulkAddRow = bulkAddRow;

    window.bulkAddRows = function(n) { for (var i = 0; i < n; i++) bulkAddRow(); };

    /* ── Toggle type ─────────────────────────────────────── */
    window.bulkToggleType = function(rowId) {
        var tr     = document.getElementById(rowId);
        var badge  = tr.querySelector('.type-badge');
        var hidden = tr.querySelector('.b-type');
        var catSel = tr.querySelector('.b-cat');
        var subSel = tr.querySelector('.b-sub');
        var newType = hidden.value === 'EXPENSE' ? 'INCOME' : 'EXPENSE';
        hidden.value     = newType;
        badge.textContent= newType.charAt(0) + newType.slice(1).toLowerCase();
        badge.className  = 'type-badge ' + newType.toLowerCase();
        catSel.innerHTML = buildCatOptions(newType, '');
        subSel.innerHTML = '<option value="">-</option>';
        subSel.disabled  = true;
        bulkUpdateSummary();
    };

    /* ── Category change → subcategory ──────────────────── */
    window.bulkCatChange = function(rowId) {
        var tr    = document.getElementById(rowId);
        var catId = tr.querySelector('.b-cat').value;
        var subSel= tr.querySelector('.b-sub');
        var subs  = SUBCATS.filter(function(s){ return s.catId === catId; });
        subSel.innerHTML = buildSubOptions(catId, '');
        subSel.disabled  = (subs.length === 0);
    };

    /* ── Delete row ──────────────────────────────────────── */
    window.bulkDelRow = function(rowId) {
        var tr = document.getElementById(rowId);
        if (tr) tr.remove();
        bulkRenumber();
        bulkUpdateSummary();
    };

    function bulkRenumber() {
        document.querySelectorAll('#bulkBody tr').forEach(function(tr, i) {
            tr.querySelector('.col-row').textContent = i + 1;
        });
    }

    /* ── Live summary ────────────────────────────────────── */
    function bulkUpdateSummary() {
        var income = 0, expense = 0;
        document.querySelectorAll('#bulkBody tr').forEach(function(tr) {
            var amt  = parseFloat(tr.querySelector('.b-amt').value) || 0;
            var type = tr.querySelector('.b-type').value;
            if (type === 'INCOME') income += amt; else expense += amt;
        });
        var rows = document.querySelectorAll('#bulkBody tr').length;
        var net  = income - expense;
        document.getElementById('bSumRows').textContent    = rows;
        document.getElementById('bSumIncome').textContent  = '\u20b9' + income.toFixed(2);
        document.getElementById('bSumExpense').textContent = '\u20b9' + expense.toFixed(2);
        document.getElementById('bSumNet').textContent     = '\u20b9' + net.toFixed(2);
        document.getElementById('bSumNet').style.color     = net >= 0 ? '#15803d' : '#b91c1c';
    }
    window.bulkUpdateSummary = bulkUpdateSummary;

    /* ── Clear all ───────────────────────────────────────── */
    window.bulkClearAll = function() {
        if (!confirm('Clear all rows?')) return;
        document.getElementById('bulkBody').innerHTML = '';
        rowCount = 0;
        bulkUpdateSummary();
    };
    
    /* ── Import File (CSV / Excel) ───────────────────────── */
    window.importFile = function(input) {
        var file = input.files[0];
        if (!file) return;
        input.value = ''; // reset so same file can be re-imported

        var reader = new FileReader();
        var isExcel = file.name.match(/\.(xlsx|xls)$/i);

        reader.onload = function(e) {
            var rows = [];

            if (isExcel) {
                // Excel — use SheetJS
                var wb = XLSX.read(e.target.result, { type: 'array', cellDates: true });
                var ws = wb.Sheets[wb.SheetNames[0]];
                var data = XLSX.utils.sheet_to_json(ws, { header: 1, raw: false, dateNF: 'yyyy-mm-dd hh:mm' });
                rows = data;
            } else {
                // CSV
                var text = e.target.result;
                rows = text.split('\n').map(function(line) {
                    // Handle quoted fields
                    var result = [], cur = '', inQ = false;
                    for (var i = 0; i < line.length; i++) {
                        var c = line[i];
                        if (c === '"') { inQ = !inQ; }
                        else if (c === ',' && !inQ) { result.push(cur.trim()); cur = ''; }
                        else { cur += c; }
                    }
                    result.push(cur.trim());
                    return result;
                });
            }

            // Skip header row
            if (rows.length <= 1) {
                showAlert('File is empty or has only headers.', 'error');
                return;
            }

            var imported = 0, skipped = 0;
            // Clear existing rows before import
            if (document.querySelectorAll('#bulkBody tr').length > 0) {
                if (!confirm('Clear existing rows and load from file?')) return;
                document.getElementById('bulkBody').innerHTML = '';
                rowCount = 0;
            }

            for (var i = 1; i < rows.length; i++) {
                var r = rows[i];
                // Skip empty rows
                if (!r || r.length < 3 || (r[0] === '' && r[2] === '')) {
                    skipped++;
                    continue;
                }

                var rawType = (r[0] || '').toString().trim().toUpperCase();
                var type    = (rawType === 'INCOME') ? 'INCOME' : 'EXPENSE';
                var rawDate = (r[1] || '').toString().trim();
                var amount  = (r[2] || '').toString().trim();
                var rawCat  = (r[3] || '').toString().trim();
                var rawSub  = (r[4] || '').toString().trim();
                var note    = (r[5] || '').toString().trim();

                // Normalize date → datetime-local format (YYYY-MM-DDTHH:mm)
                var dt = normalizeDate(rawDate);

                // Find category id by name (case-insensitive)
                var catList = (type === 'INCOME') ? CAT_INCOME : CAT_EXPENSE;
                var catMatch = catList.filter(function(c) {
                    return c.name.toLowerCase() === rawCat.toLowerCase();
                })[0];
                var catId = catMatch ? catMatch.id : '';

                // Find sub-category id by name
                var subMatch = SUBCATS.filter(function(s) {
                    return s.name.toLowerCase() === rawSub.toLowerCase()
                        && (!catId || s.catId === catId);
                })[0];
                var subId = subMatch ? subMatch.id : '';

                // Add row to form
                bulkAddRow({ type: type, date: dt, catId: catId, subId: subId });

                // Set values that bulkAddRow doesn't set directly
                var allRows = document.querySelectorAll('#bulkBody tr');
                var tr = allRows[allRows.length - 1];
                tr.querySelector('.b-amt').value  = amount;
                tr.querySelector('.b-note').value = note;

                // Highlight unmatched category in red
                if (!catId && rawCat) {
                    tr.querySelector('.b-cat').style.borderColor = '#f59e0b';
                    tr.querySelector('.b-cat').title = 'Category "' + rawCat + '" not found — please select manually';
                }
                if (!subId && rawSub) {
                    tr.querySelector('.b-sub').style.borderColor = '#f59e0b';
                    tr.querySelector('.b-sub').title = 'Sub category "' + rawSub + '" not found';
                }

                imported++;
            }

            bulkUpdateSummary();
            showAlert(imported + ' rows imported' + (skipped > 0 ? ', ' + skipped + ' skipped' : '') + '. Review and save.', 'success');
        };

        if (isExcel) {
            reader.readAsArrayBuffer(file);
        } else {
            reader.readAsText(file);
        }
    };

    /* ── Date normalizer ─────────────────────────────────── */
    function normalizeDate(raw) {
        if (!raw) return nowLocal();
        // Already ISO format: 2026-06-01T10:30 or 2026-06-01 10:30
        var s = raw.replace(' ', 'T');
        if (s.match(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}/)) {
            return s.substring(0, 16);
        }
        // DD/MM/YYYY HH:MM or DD-MM-YYYY HH:MM
        var m = raw.match(/^(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{4})[\s,T]?(\d{2}:\d{2})?/);
        if (m) {
            var dd = m[1].padStart(2,'0'), mo = m[2].padStart(2,'0'), yy = m[3];
            var tm = m[4] || '00:00';
            return yy + '-' + mo + '-' + dd + 'T' + tm;
        }
        return nowLocal();
    }

    /* ── Submit ──────────────────────────────────────────── */
    window.bulkSubmit = function() {
        var rows = document.querySelectorAll('#bulkBody tr');
        if (rows.length === 0) { showAlert('No rows to save.', 'error'); return; }

        var entries = [];
        var valid   = true;
        rows.forEach(function(tr) {
            var amt = tr.querySelector('.b-amt').value.trim();
            var cat = tr.querySelector('.b-cat').value;
            var amtEl = tr.querySelector('.b-amt');
            var catEl = tr.querySelector('.b-cat');
            if (!amt || !cat) {
                valid = false;
                amtEl.style.borderColor = !amt ? '#ef4444' : '';
                catEl.style.borderColor = !cat ? '#ef4444' : '';
            } else {
                amtEl.style.borderColor = '';
                catEl.style.borderColor = '';
            }
            entries.push({
                type         : tr.querySelector('.b-type').value,
                dateTime     : tr.querySelector('.b-date').value,
                amount       : amt,
                categoryid   : cat,
                subcategoryId: tr.querySelector('.b-sub').value,
                note         : tr.querySelector('.b-note').value
            });
        });

        if (!valid) { showAlert('Fill required fields marked in red.', 'error'); return; }

        var btn = document.getElementById('bulkSaveBtn');
        btn.disabled    = true;
        btn.textContent = 'Saving\u2026';
        document.getElementById('bulkResult').textContent = '';

        var CTX   = '<%=request.getContextPath()%>';
        var saved = 0, failed = 0, total = entries.length;

        function saveNext(i) {
            if (i >= total) {
                btn.disabled    = false;
                btn.textContent = '\u2713 Save All';
                var msg  = saved + ' of ' + total + ' saved';
                if (failed) msg += ' (' + failed + ' failed)';
                showAlert(msg, failed === 0 ? 'success' : 'error');
                document.getElementById('bulkResult').textContent = msg;
                document.getElementById('bulkResult').style.color = failed === 0 ? '#15803d' : '#b91c1c';
                if (failed === 0) {
                    document.getElementById('bulkBody').innerHTML = '';
                    rowCount = 0;
                    for (var r = 0; r < 3; r++) bulkAddRow();  /* reset to 3 rows */
                    bulkUpdateSummary();
                }
                return;
            }
            var e  = entries[i];
            var fd = new FormData();
            fd.append('type',          e.type);
            fd.append('dateTime',      e.dateTime);
            fd.append('amount',        e.amount);
            fd.append('categoryid',    e.categoryid);
            fd.append('subcategoryId', e.subcategoryId);
            fd.append('note',          e.note);
            fetch(CTX + '/transactions', { method:'POST', body:fd })
                .then(function(r){ if (r.ok) saved++; else failed++; saveNext(i+1); })
                .catch(function(){  failed++; saveNext(i+1); });
        }
        saveNext(0);
    };

    /* ── Alert helper ────────────────────────────────────── */
    function showAlert(msg, type) {
        var wrap = document.getElementById('bulkAlertWrap');
        wrap.innerHTML = '<div class="alert alert-' + (type === 'success' ? 'success' : 'error') + '">'
            + (type === 'success' ? '&#10003; ' : '&#10007; ') + msg + '</div>';
        setTimeout(function(){ wrap.innerHTML = ''; }, 4000);
    }

    /* ── Init: 3 default rows ────────────────────────────── */
    document.addEventListener('DOMContentLoaded', function() {
        for (var i = 0; i < 3; i++) bulkAddRow();
    });

})();




</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
<%@ include file="footer.jsp"%>