<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Export bar + Email modal — include this in index.jsp and reports.jsp --%>

<c:if test="${not empty param.emailSent}">
  <div class="alert alert-success">&#10003; Report sent to your email successfully!</div>
</c:if>
<c:if test="${not empty param.exportError}">
  <div class="alert alert-error">&#10007; Export error: ${param.exportError}</div>
</c:if>

<!-- Export action bar -->
<div class="card" style="padding:.75rem 1.25rem;margin-bottom:1.25rem">
  <div class="flex gap-1" style="flex-wrap:wrap">
    <span style="font-size:.8rem;font-weight:600;color:var(--text-2);align-self:center;margin-right:.25rem">
      &#128216; ${sessionScope.activeBookName} &nbsp;&#8250;
    </span>
    <a href="${pageContext.request.contextPath}/export?type=pdf"
       class="btn btn-outline btn-sm">
      &#128196; Download PDF
    </a>
    <a href="${pageContext.request.contextPath}/export?type=excel"
       class="btn btn-outline btn-sm">
      &#128202; Download Excel
    </a>
    <button class="btn btn-primary btn-sm" onclick="openModal('emailModal')">
      &#9993; Send via Email
    </button>
  </div>
</div>

<!-- Email Modal -->
<div id="emailModal" class="modal-overlay">
  <div class="modal">
    <div class="modal-header">
      <h3>&#9993; Send Report via Email</h3>
      <button class="modal-close" onclick="closeModal('emailModal')">&#x2715;</button>
    </div>
    <form action="${pageContext.request.contextPath}/export" method="post">
      <div class="form-group mb-2">
        <label>Recipient Email *</label>
        <input type="email" name="email" placeholder="yourname@gmail.com" required autofocus>
      </div>
      <div class="form-group mb-2">
        <label>Format</label>
        <div class="flex gap-2" style="margin-top:.3rem">
          <label class="flex gap-1" style="cursor:pointer">
            <input type="radio" name="format" value="pdf" checked> PDF Report
          </label>
          <label class="flex gap-1" style="cursor:pointer">
            <input type="radio" name="format" value="excel"> Excel Spreadsheet
          </label>
        </div>
      </div>
      <p class="text-muted" style="font-size:.78rem;margin-bottom:.75rem">
        All ${sessionScope.activeBookName} transactions will be attached.
      </p>
      <div class="flex gap-1 mt-2">
        <button type="button" class="btn btn-outline" onclick="closeModal('emailModal')">Cancel</button>
        <button type="submit" class="btn btn-primary ml-auto">Send Email</button>
      </div>
    </form>
  </div>
</div>
