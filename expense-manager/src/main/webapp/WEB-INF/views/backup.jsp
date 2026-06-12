<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="pageTitle" value="Backup &amp; Restore" scope="request" />
<c:set var="activePage" value="backup" scope="request" />
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>"
	scope="request" />
<%@ include file="header.jsp"%>

<style>
.backup-hero {
	background: linear-gradient(135deg, #1e40af 0%, #1d4ed8 60%, #2563eb 100%);
	border-radius: var(--radius);
	padding: 2rem 2.25rem;
	margin-bottom: 1.5rem;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 1.5rem;
	position: relative;
	overflow: hidden;
}

.backup-hero::before {
	content: '';
	position: absolute;
	width: 260px;
	height: 260px;
	background: radial-gradient(circle, rgba(255, 255, 255, .12) 0%,
		transparent 70%);
	top: -70px;
	right: -50px;
	border-radius: 50%;
}

.hero-text h2 {
	font-size: 1.35rem;
	font-weight: 700;
	color: #fff;
	margin: 0 0 5px;
}

.hero-text p {
	color: rgba(255, 255, 255, .75);
	font-size: .875rem;
	margin: 0;
}

.hero-actions {
	display: flex;
	gap: .75rem;
	flex-shrink: 0;
}

.schedule-info {
	background: #eff6ff;
	border: 1px solid #bfdbfe;
	border-radius: var(--radius);
	padding: .85rem 1.1rem;
	display: flex;
	align-items: center;
	gap: .75rem;
	margin-bottom: 1.5rem;
	font-size: .85rem;
	color: #1e40af;
}

.schedule-info .icon {
	font-size: 1.1rem;
	flex-shrink: 0;
}

.type-pill {
	display: inline-block;
	padding: .18rem .55rem;
	border-radius: 5px;
	font-size: .68rem;
	font-weight: 700;
	text-transform: uppercase;
	letter-spacing: .05em;
}

.type-manual {
	background: #eff6ff;
	color: #2563eb;
}

.type-scheduled {
	background: #dcfce7;
	color: #15803d;
}

.type-auto {
	background: #fef9c3;
	color: #92400e;
}

.status-badge {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	padding: .18rem .55rem;
	border-radius: 20px;
	font-size: .7rem;
	font-weight: 600;
	text-transform: uppercase;
}

.status-success {
	background: #dcfce7;
	color: #15803d;
}

.status-restored {
	background: #dbeafe;
	color: #1d4ed8;
}

.status-restoring {
	background: #fef9c3;
	color: #92400e;
}

.status-failed {
	background: #fee2e2;
	color: #b91c1c;
}

.status-pending {
	background: #f1f5f9;
	color: #64748b;
}

.btn-icon {
	width: 30px;
	height: 30px;
	border-radius: 7px;
	border: 1px solid var(--border);
	background: #fff;
	cursor: pointer;
	display: inline-flex;
	align-items: center;
	justify-content: center;
	font-size: .82rem;
	transition: all .15s;
	text-decoration: none;
}

.btn-icon:hover {
	transform: translateY(-1px);
	box-shadow: 0 2px 6px rgba(0, 0, 0, .1);
}

.btn-icon.restore {
	color: #15803d;
	border-color: #bbf7d0;
}

.btn-icon.restore:hover {
	background: #dcfce7;
}

.btn-icon.download {
	color: #2563eb;
	border-color: #bfdbfe;
}

.btn-icon.download:hover {
	background: #eff6ff;
}

.btn-icon.delete {
	color: #b91c1c;
	border-color: #fecaca;
}

.btn-icon.delete:hover {
	background: #fee2e2;
}

.upload-zone {
	border: 2px dashed var(--border);
	border-radius: var(--radius);
	padding: 2rem;
	text-align: center;
	cursor: pointer;
	transition: all .2s;
	color: var(--text-2);
}

.upload-zone:hover, .upload-zone.drag {
	border-color: var(--primary);
	background: #eff6ff;
}

.upload-zone .icon {
	font-size: 2rem;
	margin-bottom: .5rem;
}

.upload-zone p {
	font-size: .85rem;
	margin: 0;
}

.upload-zone input[type=file] {
	display: none;
}

.restore-warning {
	background: #fefce8;
	border: 1px solid #fde68a;
	border-radius: 8px;
	padding: .85rem 1rem;
	margin-bottom: 1rem;
	font-size: .83rem;
	color: #92400e;
	line-height: 1.6;
}
</style>

<div class="page-header flex">
	<div>
		<h1>&#128274; Backup &amp; Restore</h1>
		<p>Protect your data — create, download, and restore backups</p>
	</div>
</div>

<%-- Flash messages --%>
<c:if test="${not empty sessionScope.successMsg}">
	<div class="alert alert-success">&#10003;
		${sessionScope.successMsg}</div>
	<c:remove var="successMsg" scope="session" />
</c:if>
<c:if test="${not empty sessionScope.errorMsg}">
	<div class="alert alert-error">&#10007; ${sessionScope.errorMsg}</div>
	<c:remove var="errorMsg" scope="session" />
</c:if>

<%-- Hero --%>
<div class="backup-hero">
	<div class="hero-text">
		<h2>&#128737; Your Data is Safe</h2>
		<p>
			Create manual backups, schedule auto-backups, download ZIP files,<br>
			and restore from any previous backup — all in one place.
		</p>
	</div>
	<div class="hero-actions">
		<button class="btn btn-outline"
			style="background: #fff; color: var(--primary)"
			onclick="openModal('createModal')">&#43; Create Backup</button>
		<button class="btn btn-outline"
			style="background: rgba(255, 255, 255, .15); color: #fff; border-color: rgba(255, 255, 255, .3)"
			onclick="openModal('uploadModal')">&#8679; Upload Backup</button>
	</div>
</div>

<%-- Stats --%>
<c:set var="sCount" value="0" />
<c:set var="schCount" value="0" />
<c:set var="fCount" value="0" />
<c:forEach var="b" items="${backups}">
	<c:if
		test="${b.status.name()=='SUCCESS' or b.status.name()=='RESTORED'}">
		<c:set var="sCount" value="${sCount+1}" />
	</c:if>
	<c:if test="${b.backupType.name()=='SCHEDULED'}">
		<c:set var="schCount" value="${schCount+1}" />
	</c:if>
	<c:if test="${b.status.name()=='FAILED'}">
		<c:set var="fCount" value="${fCount+1}" />
	</c:if>
</c:forEach>

<div class="stats-grid" style="margin-bottom: 1.5rem">
	<div class="stat-card">
		<div class="stat-label">Total Backups</div>
		<div class="stat-value" style="color: var(--primary)">${backups.size()}</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Successful</div>
		<div class="stat-value income">${sCount}</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Scheduled</div>
		<div class="stat-value" style="color: var(--amber)">${schCount}</div>
	</div>
	<div class="stat-card">
		<div class="stat-label">Failed</div>
		<div class="stat-value expense">${fCount}</div>
	</div>
</div>

<%-- Schedule info --%>
<div class="schedule-info">
	<span class="icon">&#128197;</span> <span><strong>Daily
			Auto-Backup Active</strong> — Runs every day at <strong>midnight
			(00:00)</strong>. Configure via <code>web.xml</code> params <code>backup.schedule.hour</code>
		/ <code>backup.schedule.minute</code>. A safety backup is always
		created before any restore.</span>
</div>

<%-- Backup table --%>
<div class="table-wrap">
	<table>
		<thead>
			<tr>
				<th>#</th>
				<th>File Name</th>
				<th>Type</th>
				<th>Status</th>
				<th>Records</th>
				<th>Size</th>
				<th>Description</th>
				<th>Created At</th>
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${empty backups}">
					<tr>
						<td colspan="9" class="empty-state">No backups yet.
							<button class="btn btn-primary btn-sm"
								style="margin-left: .75rem" onclick="openModal('createModal')">&#43;
								Create First Backup</button>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach var="b" items="${backups}" varStatus="st">
						<tr>
							<td style="color: var(--text-2); font-size: .8rem">${st.index+1}</td>
							<td style="font-family: monospace; font-size: .82rem">
								&#128230; ${b.fileName}</td>
							<td><c:choose>
									<c:when test="${b.backupType.name()=='MANUAL'}">
										<span class="type-pill type-manual">Manual</span>
									</c:when>
									<c:when test="${b.backupType.name()=='SCHEDULED'}">
										<span class="type-pill type-scheduled">Scheduled</span>
									</c:when>
									<c:otherwise>
										<span class="type-pill type-auto">Auto</span>
									</c:otherwise>
								</c:choose></td>
							<td><c:choose>
									<c:when test="${b.status.name()=='SUCCESS'}">
										<span class="status-badge status-success">&#10003;
											Success</span>
									</c:when>
									<c:when test="${b.status.name()=='RESTORED'}">
										<span class="status-badge status-restored">&#8635;
											Restored</span>
									</c:when>
									<c:when test="${b.status.name()=='RESTORING'}">
										<span class="status-badge status-restoring">&#8987;
											Restoring</span>
									</c:when>
									<c:when test="${b.status.name()=='FAILED'}">
										<span class="status-badge status-failed">&#10007;
											Failed</span>
									</c:when>
									<c:otherwise>
										<span class="status-badge status-pending">Pending</span>
									</c:otherwise>
								</c:choose></td>
							<td><span class="amount-pos">&#9650; ${b.incomeCount}</span>
								&nbsp; <span class="amount-neg">&#9660; ${b.expenseCount}</span>
							</td>
							<td style="color: var(--text-2); font-size: .82rem">${b.fileSizeFormatted}</td>
							<td
								style="color: var(--text-2); font-size: .82rem; max-width: 160px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap"
								title="${b.description}">${not empty b.description ? b.description : '—'}
							</td>
							<td
								style="font-size: .82rem; color: var(--text-2); white-space: nowrap">
								<c:if test="${not empty b.createdAt}">
                                    ${b.createdAt.toLocalDate()}<br>
									<span style="color: var(--text-3)">${b.createdAt.toLocalTime().toString().substring(0,8)}</span>
								</c:if>
							</td>
							<td>
								<div class="flex gap-1">
									<c:if test="${b.restorable}">
										<button class="btn-icon restore" title="Restore"
											onclick="confirmRestore(${b.id},'${b.fileName}')">&#8635;</button>
										<a
											href="${pageContext.request.contextPath}/backup/download?id=${b.id}"
											class="btn-icon download" title="Download ZIP">&#8681;</a>
									</c:if>
									<button class="btn-icon delete" title="Delete"
										onclick="confirmDelete(${b.id},'${b.fileName}')">&#128465;</button>
								</div>
							</td>
						</tr>
						<c:if test="${not empty b.errorMessage}">
							<tr>
								<td colspan="9" style="padding: .25rem 1rem .75rem 2.5rem">
									<span style="font-size: .78rem; color: #b91c1c">&#9888;
										${b.errorMessage}</span>
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</div>

<%-- ── Create Modal ── --%>
<div id="createModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3>&#43; Create New Backup</h3>
			<button class="modal-close" onclick="closeModal('createModal')">&#x2715;</button>
		</div>
		<p
			style="font-size: .875rem; color: var(--text-2); margin-bottom: 1rem">
			A ZIP file will be created with all your data: income, expense,
			categories, and custom columns.</p>
		<form method="post"
			action="${pageContext.request.contextPath}/backup/create">
			<div class="form-group">
				<label>Description (optional)</label> <input type="text"
					name="description" placeholder="e.g. Before year-end audit…">
			</div>
			<div class="flex gap-1 mt-2" style="justify-content: flex-end">
				<button type="button" class="btn btn-outline"
					onclick="closeModal('createModal')">Cancel</button>
				<button type="submit" class="btn btn-primary">&#128190;
					Create Backup</button>
			</div>
		</form>
	</div>
</div>

<%-- ── Upload Modal ── --%>
<div id="uploadModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3>&#8679; Upload Backup File</h3>
			<button class="modal-close" onclick="closeModal('uploadModal')">&#x2715;</button>
		</div>
		<p
			style="font-size: .875rem; color: var(--text-2); margin-bottom: 1rem">
			Upload a previously downloaded
			<code>.zip</code>
			backup to register it for restore.
		</p>
		<form method="post"
			action="${pageContext.request.contextPath}/backup/upload"
			enctype="multipart/form-data">
			<div class="upload-zone" id="uploadZone"
				onclick="document.getElementById('fileInput').click()">
				<div class="icon">&#128228;</div>
				<p id="uploadLabel">
					Click or drag &amp; drop a <strong>.zip</strong> backup file
				</p>
				<input type="file" id="fileInput" name="backupFile" accept=".zip"
					onchange="fileSelected(this)">
			</div>
			<div class="form-group mt-2">
				<label>Description (optional)</label> <input type="text"
					name="description" placeholder="Restored from old server…">
			</div>
			<div class="flex gap-1 mt-2" style="justify-content: flex-end">
				<button type="button" class="btn btn-outline"
					onclick="closeModal('uploadModal')">Cancel</button>
				<button type="submit" class="btn btn-primary">&#8679;
					Upload &amp; Register</button>
			</div>
		</form>
	</div>
</div>

<%-- ── Restore Confirm Modal ── --%>
<div id="restoreModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3 style="color: var(--amber)">&#9888; Confirm Restore</h3>
			<button class="modal-close" onclick="closeModal('restoreModal')">&#x2715;</button>
		</div>
		<div class="restore-warning">
			&#128737; <strong>Safety auto-backup will be created first.</strong><br>
			All current data will be <strong>replaced</strong> with the selected
			backup. The auto-backup lets you recover if needed.
		</div>
		<p style="font-size: .875rem; margin-bottom: 1.25rem">
			Restore from: <strong id="restoreFileName" style="color: var(--text)"></strong>
		</p>
		<form method="post"
			action="${pageContext.request.contextPath}/backup/restore">
			<input type="hidden" name="backupId" id="restoreBackupId">
			<div class="flex gap-1" style="justify-content: flex-end">
				<button type="button" class="btn btn-outline"
					onclick="closeModal('restoreModal')">Cancel</button>
				<button type="submit" class="btn btn-danger">&#8635; Yes,
					Restore Now</button>
			</div>
		</form>
	</div>
</div>

<%-- ── Delete Confirm Modal ── --%>
<div id="deleteModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3 style="color: var(--red)">&#128465; Delete Backup</h3>
			<button class="modal-close" onclick="closeModal('deleteModal')">&#x2715;</button>
		</div>
		<p style="font-size: .875rem; margin-bottom: 1.25rem">
			This permanently deletes the record <strong>and</strong> the ZIP file
			from disk.<br> File: <strong id="deleteFileName"
				style="color: var(--text)"></strong>
		</p>
		<form method="post"
			action="${pageContext.request.contextPath}/backup/delete">
			<input type="hidden" name="backupId" id="deleteBackupId">
			<div class="flex gap-1" style="justify-content: flex-end">
				<button type="button" class="btn btn-outline"
					onclick="closeModal('deleteModal')">Cancel</button>
				<button type="submit" class="btn btn-danger">&#128465;
					Delete</button>
			</div>
		</form>
	</div>
</div>

<script>
function confirmRestore(id, name) {
    document.getElementById('restoreBackupId').value = id;
    document.getElementById('restoreFileName').textContent = name;
    openModal('restoreModal');
}
function confirmDelete(id, name) {
    document.getElementById('deleteBackupId').value = id;
    document.getElementById('deleteFileName').textContent = name;
    openModal('deleteModal');
}
function fileSelected(input) {
    if (input.files && input.files[0]) {
        var f = input.files[0];
        var s = f.size < 1048576
            ? (f.size / 1024).toFixed(1) + ' KB'
            : (f.size / 1048576).toFixed(2) + ' MB';
        document.getElementById('uploadLabel').innerHTML = '<strong>' + f.name + '</strong> (' + s + ')';
        document.getElementById('uploadZone').style.borderColor = 'var(--primary)';
    }
}
var zone = document.getElementById('uploadZone');
if (zone) {
    zone.addEventListener('dragover', function(e) { e.preventDefault(); zone.classList.add('drag'); });
    zone.addEventListener('dragleave', function() { zone.classList.remove('drag'); });
    zone.addEventListener('drop', function(e) {
        e.preventDefault(); zone.classList.remove('drag');
        var fi = document.getElementById('fileInput');
        fi.files = e.dataTransfer.files;
        fileSelected(fi);
    });
}
</script>

<%@ include file="footer.jsp"%>