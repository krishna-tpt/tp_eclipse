<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!-- After apache taglibs recognize URI -->
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>ExpenseIQ – Backup & Restore</title>
<link
	href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@300;400;500&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
.backup-hero {
	background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
	border-radius: 20px;
	padding: 36px 40px;
	margin-bottom: 28px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 24px;
	position: relative;
	overflow: hidden
}

.backup-hero::before {
	content: '';
	position: absolute;
	width: 300px;
	height: 300px;
	background: radial-gradient(circle, rgba(99, 102, 241, .25) 0%,
		transparent 70%);
	top: -80px;
	right: -60px;
	border-radius: 50%
}

.hero-text h2 {
	font-family: 'Syne', sans-serif;
	font-size: 1.8rem;
	font-weight: 800;
	color: #fff;
	margin: 0 0 6px
}

.hero-text p {
	color: rgba(255, 255, 255, .65);
	font-size: .93rem;
	margin: 0
}

.hero-actions {
	display: flex;
	gap: 12px;
	flex-shrink: 0
}

.stats-row {
	display: grid;
	grid-template-columns: repeat(4, 1fr);
	gap: 16px;
	margin-bottom: 28px
}

.stat-card {
	background: var(--card-bg);
	border: 1px solid var(--border);
	border-radius: 14px;
	padding: 20px 22px;
	display: flex;
	align-items: center;
	gap: 14px
}

.stat-icon {
	width: 44px;
	height: 44px;
	border-radius: 12px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 1.2rem;
	flex-shrink: 0
}

.stat-icon.blue {
	background: rgba(99, 102, 241, .15);
	color: #6366f1
}

.stat-icon.green {
	background: rgba(16, 185, 129, .15);
	color: #10b981
}

.stat-icon.yellow {
	background: rgba(245, 158, 11, .15);
	color: #f59e0b
}

.stat-icon.red {
	background: rgba(239, 68, 68, .15);
	color: #ef4444
}

.stat-info label {
	font-size: .75rem;
	color: var(--text-muted);
	text-transform: uppercase;
	letter-spacing: .05em
}

.stat-info span {
	display: block;
	font-size: 1.35rem;
	font-weight: 700;
	color: var(--text-primary);
	font-family: 'Syne', sans-serif
}

.backup-table {
	width: 100%;
	border-collapse: collapse
}

.backup-table th {
	padding: 10px 16px;
	text-align: left;
	font-size: .72rem;
	text-transform: uppercase;
	letter-spacing: .07em;
	color: var(--text-muted);
	background: var(--surface);
	border-bottom: 1px solid var(--border)
}

.backup-table td {
	padding: 14px 16px;
	border-bottom: 1px solid var(--border);
	font-size: .88rem;
	color: var(--text-primary);
	vertical-align: middle
}

.backup-table tr:last-child td {
	border-bottom: none
}

.backup-table tr:hover td {
	background: rgba(99, 102, 241, .04)
}

.badge {
	display: inline-flex;
	align-items: center;
	gap: 5px;
	padding: 3px 10px;
	border-radius: 20px;
	font-size: .73rem;
	font-weight: 600
}

.badge-success {
	background: rgba(16, 185, 129, .15);
	color: #10b981
}

.badge-danger {
	background: rgba(239, 68, 68, .15);
	color: #ef4444
}

.badge-warning {
	background: rgba(245, 158, 11, .15);
	color: #f59e0b
}

.badge-info {
	background: rgba(99, 102, 241, .15);
	color: #6366f1
}

.badge-secondary {
	background: rgba(148, 163, 184, .12);
	color: #94a3b8
}

.type-pill {
	display: inline-block;
	padding: 2px 9px;
	border-radius: 6px;
	font-size: .7rem;
	font-weight: 600;
	text-transform: uppercase;
	letter-spacing: .06em
}

.type-manual {
	background: rgba(99, 102, 241, .12);
	color: #818cf8
}

.type-scheduled {
	background: rgba(16, 185, 129, .12);
	color: #34d399
}

.type-auto {
	background: rgba(245, 158, 11, .12);
	color: #fbbf24
}

.btn-icon {
	width: 32px;
	height: 32px;
	border-radius: 8px;
	border: none;
	cursor: pointer;
	display: inline-flex;
	align-items: center;
	justify-content: center;
	font-size: .85rem;
	transition: .15s
}

.btn-icon:hover {
	transform: translateY(-1px)
}

.btn-restore {
	background: rgba(16, 185, 129, .15);
	color: #10b981
}

.btn-restore:hover {
	background: rgba(16, 185, 129, .3)
}

.btn-download {
	background: rgba(99, 102, 241, .15);
	color: #818cf8
}

.btn-download:hover {
	background: rgba(99, 102, 241, .3)
}

.btn-del {
	background: rgba(239, 68, 68, .1);
	color: #f87171
}

.btn-del:hover {
	background: rgba(239, 68, 68, .25)
}

.modal-overlay {
	display: none;
	position: fixed;
	inset: 0;
	z-index: 1000;
	background: rgba(0, 0, 0, .65);
	backdrop-filter: blur(4px);
	align-items: center;
	justify-content: center
}

.modal-overlay.open {
	display: flex
}

.modal-box {
	background: var(--card-bg);
	border: 1px solid var(--border);
	border-radius: 20px;
	width: 100%;
	max-width: 480px;
	padding: 32px;
	animation: slideUp .25s ease
}

@
keyframes slideUp {
	from {transform: translateY(20px);
	opacity: 0
}

to {
	transform: translateY(0);
	opacity: 1
}

}
.modal-box h3 {
	font-family: 'Syne', sans-serif;
	font-size: 1.2rem;
	font-weight: 700;
	margin: 0 0 6px
}

.modal-box p {
	color: var(--text-muted);
	font-size: .88rem;
	margin: 0 0 22px;
	line-height: 1.6
}

.modal-actions {
	display: flex;
	gap: 10px;
	justify-content: flex-end;
	margin-top: 24px
}

.restore-warning {
	background: rgba(245, 158, 11, .08);
	border: 1px solid rgba(245, 158, 11, .3);
	border-radius: 10px;
	padding: 14px 16px;
	margin-bottom: 16px;
	font-size: .83rem;
	color: #fbbf24;
	line-height: 1.6
}

.upload-zone {
	border: 2px dashed var(--border);
	border-radius: 12px;
	padding: 28px;
	text-align: center;
	cursor: pointer;
	transition: .2s
}

.upload-zone:hover, .upload-zone.drag {
	border-color: #6366f1;
	background: rgba(99, 102, 241, .05)
}

.upload-zone i {
	font-size: 2rem;
	color: var(--text-muted);
	margin-bottom: 10px;
	display: block
}

.upload-zone p {
	color: var(--text-muted);
	font-size: .85rem;
	margin: 0
}

.upload-zone input[type=file] {
	display: none
}

.schedule-box {
	background: rgba(99, 102, 241, .06);
	border: 1px solid rgba(99, 102, 241, .2);
	border-radius: 12px;
	padding: 16px 20px;
	display: flex;
	align-items: center;
	gap: 14px;
	margin: 0 24px 20px
}

.schedule-box i {
	color: #818cf8;
	font-size: 1.3rem;
	flex-shrink: 0
}

.schedule-box div {
	font-size: .85rem;
	color: var(--text-secondary);
	line-height: 1.6
}

.schedule-box strong {
	color: var(--text-primary)
}

@media ( max-width :900px) {
	.stats-row {
		grid-template-columns: repeat(2, 1fr)
	}
	.backup-hero {
		flex-direction: column;
		align-items: flex-start
	}
}
</style>
</head>
<body>
	<nav class="sidebar">
		<div class="sidebar-brand">
			<span class="brand-icon">💰</span><span class="brand-name">ExpenseIQ</span>
		</div>
		<ul class="nav-links">
			<li><a href="${pageContext.request.contextPath}/dashboard"><i
					class="fas fa-chart-pie"></i><span>Dashboard</span></a></li>
			<li><a href="${pageContext.request.contextPath}/income/list"><i
					class="fas fa-arrow-trend-up"></i><span>Income</span></a></li>
			<li><a href="${pageContext.request.contextPath}/expense/list"><i
					class="fas fa-arrow-trend-down"></i><span>Expenses</span></a></li>
			<li><a href="${pageContext.request.contextPath}/reports"><i
					class="fas fa-chart-bar"></i><span>Reports</span></a></li>
			<li><a href="${pageContext.request.contextPath}/backup/list"
				class="active"><i class="fas fa-database"></i><span>Backup</span></a></li>
		</ul>
		<div class="sidebar-footer">
			<span>v1.0.0</span>
		</div>
	</nav>
	<main class="main-content">
		<div class="topbar">
			<div class="page-title">Backup &amp; Restore</div>
		</div>
		<c:if test="${not empty sessionScope.successMsg}">
			<div class="alert alert-success" style="margin: 14px 28px 0">
				<i class="fas fa-check-circle"></i> ${sessionScope.successMsg}
			</div>
			<c:remove var="successMsg" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.errorMsg}">
			<div class="alert alert-danger" style="margin: 14px 28px 0">
				<i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMsg}
			</div>
			<c:remove var="errorMsg" scope="session" />
		</c:if>
		<div class="page-body">
			<div class="backup-hero">
				<div class="hero-text">
					<h2>
						<i class="fas fa-shield-halved" style="color: #818cf8"></i>
						&nbsp;Your Data is Safe
					</h2>
					<p>
						Create manual backups, schedule daily auto-backups, download ZIP
						files,<br>upload &amp; restore from any previous backup — all
						in one place.
					</p>
				</div>
				<div class="hero-actions">
					<button class="btn btn-primary" onclick="openModal('createModal')">
						<i class="fas fa-plus"></i> Create Backup
					</button>
					<button class="btn btn-secondary"
						onclick="openModal('uploadModal')">
						<i class="fas fa-upload"></i> Upload Backup
					</button>
				</div>
			</div>

			<%-- Stats --%>
			<c:set var="sCount" value="0" />
			<c:set var="schCount" value="0" />
			<c:set var="fCount" value="0" />
			<c:forEach var="b" items="${backups}">
				<c:if
					test="${b.status.name()=='SUCCESS'||b.status.name()=='RESTORED'}">
					<c:set var="sCount" value="${sCount+1}" />
				</c:if>
				<c:if test="${b.backupType.name()=='SCHEDULED'}">
					<c:set var="schCount" value="${schCount+1}" />
				</c:if>
				<c:if test="${b.status.name()=='FAILED'}">
					<c:set var="fCount" value="${fCount+1}" />
				</c:if>
			</c:forEach>
			<div class="stats-row">
				<div class="stat-card">
					<div class="stat-icon blue">
						<i class="fas fa-database"></i>
					</div>
					<div class="stat-info">
						<label>Total Backups</label><span>${backups.size()}</span>
					</div>
				</div>
				<div class="stat-card">
					<div class="stat-icon green">
						<i class="fas fa-circle-check"></i>
					</div>
					<div class="stat-info">
						<label>Successful</label><span>${sCount}</span>
					</div>
				</div>
				<div class="stat-card">
					<div class="stat-icon yellow">
						<i class="fas fa-clock"></i>
					</div>
					<div class="stat-info">
						<label>Scheduled</label><span>${schCount}</span>
					</div>
				</div>
				<div class="stat-card">
					<div class="stat-icon red">
						<i class="fas fa-circle-xmark"></i>
					</div>
					<div class="stat-info">
						<label>Failed</label><span>${fCount}</span>
					</div>
				</div>
			</div>

			<div class="section-card">
				<div class="schedule-box">
					<i class="fas fa-calendar-check"></i>
					<div>
						<strong>Daily Auto-Backup Active</strong> — Runs every day at <strong>midnight
							(00:00)</strong>. Configure via
						<code>web.xml</code>
						params
						<code>backup.schedule.hour</code>
						/
						<code>backup.schedule.minute</code>
						. A safety backup is always created automatically before any
						restore.
					</div>
				</div>
			</div>

			<div class="section-card">
				<div class="section-head">
					<h3>
						<i class="fas fa-history" style="color: #6366f1"></i> Backup
						History
					</h3>
					<span style="font-size: .8rem; color: var(--text-muted)">${backups.size()}
						records</span>
				</div>
				<c:choose>
					<c:when test="${empty backups}">
						<div class="empty-state">
							<i class="fas fa-box-open"></i>
							<p>
								No backups yet.<br>
								<button class="btn btn-primary"
									onclick="openModal('createModal')" style="margin-top: 12px">
									<i class="fas fa-plus"></i> Create First Backup
								</button>
							</p>
						</div>
					</c:when>
					<c:otherwise>
						<div style="overflow-x: auto">
							<table class="backup-table">
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
									<c:forEach var="b" items="${backups}" varStatus="st">
										<tr>
											<td style="color: var(--text-muted); font-size: .8rem">${st.index+1}</td>
											<td><div
													style="display: flex; align-items: center; gap: 8px">
													<i class="fas fa-file-zipper"
														style="color: #6366f1; font-size: .9rem"></i><span
														style="font-family: monospace; font-size: .82rem">${b.fileName}</span>
												</div></td>
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
														<span class="badge badge-success"><i
															class="fas fa-circle" style="font-size: .5rem"></i>
															Success</span>
													</c:when>
													<c:when test="${b.status.name()=='RESTORED'}">
														<span class="badge badge-info"><i
															class="fas fa-rotate-left" style="font-size: .7rem"></i>
															Restored</span>
													</c:when>
													<c:when test="${b.status.name()=='RESTORING'}">
														<span class="badge badge-warning"><i
															class="fas fa-spinner fa-spin" style="font-size: .7rem"></i>
															Restoring…</span>
													</c:when>
													<c:when test="${b.status.name()=='FAILED'}">
														<span class="badge badge-danger"><i
															class="fas fa-circle" style="font-size: .5rem"></i>
															Failed</span>
													</c:when>
													<c:otherwise>
														<span class="badge badge-secondary">Pending</span>
													</c:otherwise>
												</c:choose></td>
											<td><span style="color: #10b981; font-size: .82rem">▲
													${b.incomeCount}</span> &nbsp;<span
												style="color: #ef4444; font-size: .82rem">▼
													${b.expenseCount}</span></td>
											<td style="font-size: .82rem; color: var(--text-secondary)">${b.fileSizeFormatted}</td>
											<td
												style="font-size: .82rem; color: var(--text-muted); max-width: 160px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap"
												title="${b.description}">${not empty b.description ? b.description : '—'}</td>
											<td
												style="font-size: .82rem; color: var(--text-secondary); white-space: nowrap">
												<c:if test="${not empty b.createdAt}">${b.createdAt.toLocalDate()} <br>
													<span style="color: var(--text-muted)">${b.createdAt.toLocalTime().toString().substring(0,8)}</span>
												</c:if>
											</td>
											<td><div
													style="display: flex; gap: 6px; align-items: center">
													<c:if test="${b.restorable}">
														<button class="btn-icon btn-restore" title="Restore"
															onclick="confirmRestore(${b.id},'${b.fileName}')">
															<i class="fas fa-rotate-left"></i>
														</button>
														<a
															href="${pageContext.request.contextPath}/backup/download?id=${b.id}"
															class="btn-icon btn-download" title="Download ZIP"><i
															class="fas fa-download"></i></a>
													</c:if>
													<button class="btn-icon btn-del" title="Delete"
														onclick="confirmDelete(${b.id},'${b.fileName}')">
														<i class="fas fa-trash"></i>
													</button>
												</div></td>
										</tr>
										<c:if test="${not empty b.errorMessage}">
											<tr>
												<td colspan="9" style="padding: 4px 16px 12px 48px"><span
													style="font-size: .78rem; color: #f87171"><i
														class="fas fa-circle-exclamation"></i> ${b.errorMessage}</span></td>
											</tr>
										</c:if>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</main>

	<!-- Create Modal -->
	<div class="modal-overlay" id="createModal">
		<div class="modal-box">
			<h3>
				<i class="fas fa-plus-circle" style="color: #6366f1"></i>
				&nbsp;Create New Backup
			</h3>
			<p>A ZIP file will be created with all your data: income,
				expense, categories, and custom columns.</p>
			<form method="post"
				action="${pageContext.request.contextPath}/backup/create">
				<div style="margin-bottom: 16px">
					<label
						style="display: block; font-size: .8rem; font-weight: 600; color: var(--text-secondary); margin-bottom: 6px; text-transform: uppercase">Description
						(optional)</label> <input type="text" name="description"
						style="width: 100%; padding: 10px 14px; border-radius: 10px; border: 1.5px solid var(--border); background: var(--surface); color: var(--text-primary); font-size: .9rem; font-family: inherit"
						placeholder="e.g. Before year-end audit…">
				</div>
				<div class="modal-actions">
					<button type="button" class="btn btn-secondary"
						onclick="closeModal('createModal')">Cancel</button>
					<button type="submit" class="btn btn-primary">
						<i class="fas fa-database"></i> Create Backup
					</button>
				</div>
			</form>
		</div>
	</div>

	<!-- Upload Modal -->
	<div class="modal-overlay" id="uploadModal">
		<div class="modal-box">
			<h3>
				<i class="fas fa-upload" style="color: #6366f1"></i> &nbsp;Upload
				Backup File
			</h3>
			<p>
				Upload a previously downloaded
				<code>.zip</code>
				backup to register it for restore.
			</p>
			<form method="post"
				action="${pageContext.request.contextPath}/backup/upload"
				enctype="multipart/form-data">
				<div style="margin-bottom: 16px">
					<div class="upload-zone" id="uploadZone"
						onclick="document.getElementById('fileInput').click()">
						<i class="fas fa-cloud-upload-alt"></i>
						<p id="uploadLabel">
							Click or drag &amp; drop a <strong>.zip</strong> backup file
						</p>
						<input type="file" id="fileInput" name="backupFile" accept=".zip"
							onchange="fileSelected(this)">
					</div>
				</div>
				<div style="margin-bottom: 16px">
					<label
						style="display: block; font-size: .8rem; font-weight: 600; color: var(--text-secondary); margin-bottom: 6px; text-transform: uppercase">Description
						(optional)</label> <input type="text" name="description"
						style="width: 100%; padding: 10px 14px; border-radius: 10px; border: 1.5px solid var(--border); background: var(--surface); color: var(--text-primary); font-size: .9rem; font-family: inherit"
						placeholder="Restored from old server…">
				</div>
				<div class="modal-actions">
					<button type="button" class="btn btn-secondary"
						onclick="closeModal('uploadModal')">Cancel</button>
					<button type="submit" class="btn btn-primary">
						<i class="fas fa-upload"></i> Upload &amp; Register
					</button>
				</div>
			</form>
		</div>
	</div>

	<!-- Restore Confirm Modal -->
	<div class="modal-overlay" id="restoreModal">
		<div class="modal-box">
			<h3 style="color: #f59e0b">
				<i class="fas fa-triangle-exclamation"></i> &nbsp;Confirm Restore
			</h3>
			<div class="restore-warning">
				<i class="fas fa-shield-halved"></i> <strong>Safety
					auto-backup will be created first.</strong><br>All current data will be
				<strong>replaced</strong> with the selected backup. The auto-backup
				lets you recover if needed.
			</div>
			<p>
				Restore from: <strong id="restoreFileName"
					style="color: var(--text-primary)"></strong>
			</p>
			<form method="post"
				action="${pageContext.request.contextPath}/backup/restore">
				<input type="hidden" name="backupId" id="restoreBackupId">
				<div class="modal-actions">
					<button type="button" class="btn btn-secondary"
						onclick="closeModal('restoreModal')">Cancel</button>
					<button type="submit" class="btn btn-danger">
						<i class="fas fa-rotate-left"></i> Yes, Restore Now
					</button>
				</div>
			</form>
		</div>
	</div>

	<!-- Delete Confirm Modal -->
	<div class="modal-overlay" id="deleteModal">
		<div class="modal-box">
			<h3 style="color: #ef4444">
				<i class="fas fa-trash"></i> &nbsp;Delete Backup
			</h3>
			<p>
				This permanently deletes the record <strong>and</strong> the ZIP
				file from disk.<br>File: <strong id="deleteFileName"
					style="color: var(--text-primary)"></strong>
			</p>
			<form method="post"
				action="${pageContext.request.contextPath}/backup/delete">
				<input type="hidden" name="backupId" id="deleteBackupId">
				<div class="modal-actions">
					<button type="button" class="btn btn-secondary"
						onclick="closeModal('deleteModal')">Cancel</button>
					<button type="submit" class="btn btn-danger">
						<i class="fas fa-trash"></i> Delete
					</button>
				</div>
			</form>
		</div>
	</div>

	<script>
function openModal(id){document.getElementById(id).classList.add('open')}
function closeModal(id){document.getElementById(id).classList.remove('open')}
document.querySelectorAll('.modal-overlay').forEach(el=>el.addEventListener('click',e=>{if(e.target===el)el.classList.remove('open')}));
function confirmRestore(id,name){document.getElementById('restoreBackupId').value=id;document.getElementById('restoreFileName').textContent=name;openModal('restoreModal')}
function confirmDelete(id,name){document.getElementById('deleteBackupId').value=id;document.getElementById('deleteFileName').textContent=name;openModal('deleteModal')}
function fileSelected(input){if(input.files&&input.files[0]){const f=input.files[0];const s=f.size<1048576?(f.size/1024).toFixed(1)+' KB':(f.size/1048576).toFixed(2)+' MB';document.getElementById('uploadLabel').innerHTML='<strong>'+f.name+'</strong> ('+s+')';document.getElementById('uploadZone').style.borderColor='#6366f1'}}
const zone=document.getElementById('uploadZone');
if(zone){zone.addEventListener('dragover',e=>{e.preventDefault();zone.classList.add('drag')});zone.addEventListener('dragleave',()=>zone.classList.remove('drag'));zone.addEventListener('drop',e=>{e.preventDefault();zone.classList.remove('drag');const fi=document.getElementById('fileInput');fi.files=e.dataTransfer.files;fileSelected(fi)})}
setTimeout(()=>{document.querySelectorAll('.alert').forEach(a=>{a.style.transition='opacity .5s';a.style.opacity='0';setTimeout(()=>a.remove(),500)})},4000);
</script>
</body>
</html>
