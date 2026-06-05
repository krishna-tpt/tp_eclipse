// ═══════════════════════════════════════
// ExpenseOS — app.js
// ═══════════════════════════════════════

// ── Sync ──────────────────────────────
function syncNow() {
  const icon = document.getElementById('syncIcon');
  icon.classList.add('spinning');
  showToast('Syncing from WorkDrive...', 'info');

  fetch(getContextPath() + '/sync')
    .then(r => r.json())
    .then(data => {
      icon.classList.remove('spinning');
      if (data.status === 'ok') {
        showToast(`✓ Synced — ${data.count} transactions loaded`, 'success');
        setTimeout(() => location.reload(), 1200);
      } else {
        showToast('✗ Sync failed: ' + data.message, 'error');
      }
    })
    .catch(err => {
      icon.classList.remove('spinning');
      showToast('✗ Network error during sync', 'error');
    });
}

// ── Toast ─────────────────────────────
function showToast(msg, type = 'info') {
  const toast = document.getElementById('sync-toast');
  if (!toast) return;
  toast.textContent = msg;
  toast.className = 'toast show';
  toast.style.borderColor = type === 'success' ? 'var(--green)'
                          : type === 'error'   ? 'var(--red)'
                          : 'var(--border-bright)';
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => toast.className = 'toast hidden', 3500);
}

// ── Modal ─────────────────────────────
function openModal(id) {
  document.getElementById(id).classList.add('open');
}
function closeModal(id) {
  document.getElementById(id).classList.remove('open');
}
// Close on backdrop click
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) {
    e.target.classList.remove('open');
  }
});

// ── Tab switching ─────────────────────
function switchTab(tabId, btnEl) {
  document.querySelectorAll('.tab-panel').forEach(p => p.style.display = 'none');
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  const panel = document.getElementById(tabId);
  if (panel) panel.style.display = 'block';
  if (btnEl) btnEl.classList.add('active');
}

// ── Amount formatter ──────────────────
function fmtCurrency(n) {
  return '₹' + Number(n).toLocaleString('en-IN', { maximumFractionDigits: 2 });
}

// ── Context path helper ───────────────
function getContextPath() {
  const base = document.querySelector('base');
  if (base) return base.href.replace(/\/$/, '');
  return window.location.pathname.split('/').slice(0, 2).join('/');
}

// ── Auto-dismiss alerts ───────────────
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.alert').forEach(a => {
    setTimeout(() => a.style.opacity = '0', 4000);
    setTimeout(() => a.remove(), 4500);
  });
});

// ── Dynamic extra field rows ──────────
function addExtraFieldRow(containerId) {
  const container = document.getElementById(containerId);
  const row = document.createElement('div');
  row.className = 'form-grid mt-1';
  row.innerHTML = `
    <div class="form-group">
      <label>Field Name</label>
      <input type="text" name="extraFieldKey" placeholder="e.g. Invoice No" />
    </div>
    <div class="form-group">
      <label>Value</label>
      <input type="text" name="extraFieldVal" placeholder="Value" />
    </div>
    <div class="form-group" style="justify-content:flex-end;padding-top:1.5rem">
      <button type="button" class="btn btn-danger" onclick="this.closest('.form-grid').remove()">Remove</button>
    </div>`;
  container.appendChild(row);
}

// Gather extra fields before form submit
function gatherExtraFields(formId) {
  const form = document.getElementById(formId);
  const keys = form.querySelectorAll('[name="extraFieldKey"]');
  const vals = form.querySelectorAll('[name="extraFieldVal"]');
  keys.forEach((keyEl, i) => {
    const k = keyEl.value.trim();
    const v = vals[i] ? vals[i].value.trim() : '';
    if (k) {
      const inp = document.createElement('input');
      inp.type = 'hidden';
      inp.name = 'extra_' + k;
      inp.value = v;
      form.appendChild(inp);
    }
  });
  return true;
}
