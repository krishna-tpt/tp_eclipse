// ═══════════════════════════════════════
// ExpenseOS — app.js
// ═══════════════════════════════════════

function openModal(id) {
  document.getElementById(id).classList.add('open');
}
function closeModal(id) {
  document.getElementById(id).classList.remove('open');
}
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) e.target.classList.remove('open');
});

// Auto-fill current datetime
document.addEventListener('DOMContentLoaded', () => {
  const now = new Date();
  const local = new Date(now - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
  document.querySelectorAll('input[type="datetime-local"]').forEach(el => {
    if (!el.value) el.value = local;
  });

  // Auto-dismiss alerts
  document.querySelectorAll('.alert').forEach(a => {
    setTimeout(() => { a.style.transition='opacity .4s'; a.style.opacity='0'; }, 3500);
    setTimeout(() => a.remove(), 4000);
  });
});

// Dynamic custom field rows
function addCustomField(containerId) {
  const c = document.getElementById(containerId);
  const div = document.createElement('div');
  div.className = 'form-grid mt-1';
  div.innerHTML = `
    <div class="form-group">
      <label>Field Name</label>
      <input type="text" name="_cfk" placeholder="e.g. Invoice No" />
    </div>
    <div class="form-group">
      <label>Value</label>
      <input type="text" name="_cfv" placeholder="Value" />
    </div>
    <div class="form-group" style="justify-content:flex-end;padding-top:1.4rem">
      <button type="button" class="btn btn-danger btn-sm" onclick="this.closest('.form-grid').remove()">Remove</button>
    </div>`;
  c.appendChild(div);
}

// Gather dynamic custom fields → hidden inputs before submit
function submitForm(formId) {
  const form = document.getElementById(formId);
  const keys = form.querySelectorAll('[name="_cfk"]');
  const vals = form.querySelectorAll('[name="_cfv"]');
  keys.forEach((k, i) => {
    if (k.value.trim()) {
      const key = k.value.trim().toLowerCase().replace(/[^a-z0-9]+/g, '_');
      const inp = document.createElement('input');
      inp.type = 'hidden';
      inp.name = 'custom_' + key;
      inp.value = vals[i] ? vals[i].value : '';
      form.appendChild(inp);
    }
  });
  form.submit();
}

function fmtINR(n) {
  return '₹' + Number(n).toLocaleString('en-IN', { maximumFractionDigits: 2 });
}
