<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="pageTitle" value="Cash Books" scope="request" />
<c:set var="activePage" value="books" scope="request" />
<c:set var="currentYear" value="<%=java.time.Year.now().getValue()%>"
	scope="request" />
<%@ include file="header.jsp"%>

<c:if test="${not empty param.msg}">
	<div class="alert alert-success">&#10003; ${param.msg == 'deleted' ? 'Book deleted.' : 'Saved!'}</div>
</c:if>
<c:if test="${not empty dbError}">
	<div class="alert alert-error">&#10007; ${dbError}</div>
</c:if>

<c:if test="${param.msg == 'inactive'}">
	<div class="alert alert-error">&#9888; That book is inactive.
		Activate it from Edit before opening.</div>
</c:if>

<c:if test="${empty sessionScope.activeBookId}">
	<div class="alert alert-error" style="margin-bottom: 1.25rem">
		&#9888; No book selected. Click <strong>Open Book</strong> on any book
		below to start.
	</div>
</c:if>

<div class="page-header flex">
	<div>
		<h1>&#128216; Cash Books</h1>
		<p>Select a book to start tracking — each book is an independent
			ledger</p>
	</div>

	<div class="flex gap-1 ml-auto" style="align-items: center">
		<form action="${pageContext.request.contextPath}/books" method="get"
			class="flex gap-1">
			<c:if test="${not empty sort}">
				<input type="hidden" name="sort" value="${sort}">
			</c:if>
			<input type="text" name="search" id="bookSearchInput"
				value="${search}" placeholder="Search book name...">
			<button type="submit" class="btn btn-outline btn-sm">&#128269;
			</button>
			<c:if test="${not empty search}">
				<a
					href="${pageContext.request.contextPath}/books${not empty sort ? '?sort='.concat(sort) : ''}"
					class="btn btn-outline btn-sm">Clear</a>
			</c:if>
		</form>

		<button type="button" class="btn btn-outline btn-sm"
			onclick="openModal('sortModal')">
			&#8645;
			<c:choose>
				<c:when test="${sort eq 'name_asc'}">Name (A-Z)</c:when>
				<c:when test="${sort eq 'balance_desc'}">Balance (High-Low)</c:when>
				<c:when test="${sort eq 'balance_asc'}">Balance (Low-High)</c:when>
				<c:when test="${sort eq 'created'}">Last Created</c:when>
				<c:otherwise>Last Updated</c:otherwise>
			</c:choose>
		</button>

		<button class="btn btn-primary" onclick="openModal('newBookModal')">+
			New Book</button>
	</div>
</div>

<!-- Books Grid -->
<div
	style="display: grid; grid-template-columns: repeat(auto-fill, minmax(290px, 1fr)); gap: 1rem">
	<c:forEach var="book" items="${books}">
		<c:set var="sum" value="${summaries[book.id]}" />
		<c:set var="isActive" value="${sessionScope.activeBookId == book.id}" />
		<c:set var="isEnabled" value="${book.active}" />

		<c:choose>
			<c:when test="${!isEnabled}">
				<c:set var="cardOnclick" value="" />
			</c:when>
			<c:when test="${isActive}">
				<c:set var="cardOnclick"
					value="window.location='${pageContext.request.contextPath}/home'" />
			</c:when>
			<c:otherwise>
				<c:set var="cardOnclick"
					value="window.location='${pageContext.request.contextPath}/books?select=${book.id}'" />
			</c:otherwise>
		</c:choose>

		<div class="card"
			style="border:2px solid ${isActive ? 'var(--primary)' : 'var(--border)'}; position:relative; ${isEnabled ? 'cursor:pointer' : ''}; opacity:${isEnabled ? '1' : '.55'}"
			onclick="${cardOnclick}">

			<c:if test="${isActive}">
				<span
					style="position: absolute; top: .7rem; right: .7rem; background: var(--primary); color: #fff; padding: .12rem .55rem; border-radius: 20px; font-size: .68rem; font-weight: 700">&#10003;
					ACTIVE</span>
			</c:if>
			<c:if test="${!isEnabled}">
				<span
					style="position: absolute; top: .7rem; right: .7rem; background: #9ca3af; color: #fff; padding: .12rem .55rem; border-radius: 20px; font-size: .68rem; font-weight: 700">INACTIVE</span>
			</c:if>

			<div
				style="font-size: 1.05rem; font-weight: 700; margin-bottom: .2rem; padding-right: 5rem">${book.name}</div>
			<div class="text-muted"
				style="font-size: .8rem; margin-bottom: .75rem; min-height: 1.2rem">${book.description}&nbsp;</div>

			<div
				style="display: grid; grid-template-columns: 1fr 1fr; gap: .5rem; margin-bottom: .75rem">
				<div
					style="background: #dcfce7; border-radius: 7px; padding: .5rem .75rem">
					<div
						style="font-size: .65rem; color: #15803d; font-weight: 700; text-transform: uppercase">Income</div>
					<div style="font-size: .95rem; font-weight: 700; color: #15803d">
						&#8377;
						<fmt:formatNumber value="${sum.income}" pattern="#,##0.00" />
					</div>
				</div>
				<div
					style="background: #fee2e2; border-radius: 7px; padding: .5rem .75rem">
					<div
						style="font-size: .65rem; color: #b91c1c; font-weight: 700; text-transform: uppercase">Expense</div>
					<div style="font-size: .95rem; font-weight: 700; color: #b91c1c">
						&#8377;
						<fmt:formatNumber value="${sum.expense}" pattern="#,##0.00" />
					</div>
				</div>
			</div>

			<div class="text-muted"
				style="font-size: .73rem; margin-bottom: .75rem">Created:
				${book.formattedDate}</div>

			<div class="flex gap-1">
				<c:choose>
					<c:when test="${isActive}">
						<a href="${pageContext.request.contextPath}/home"
							class="btn btn-primary btn-sm" onclick="event.stopPropagation()">Go
							to Dashboard</a>
					</c:when>
					<c:when test="${isEnabled}">
						<a
							href="${pageContext.request.contextPath}/books?select=${book.id}"
							class="btn btn-primary btn-sm" onclick="event.stopPropagation()">Open
							Book</a>
					</c:when>
					<c:otherwise>
						<button class="btn btn-primary btn-sm" disabled
							style="opacity: .6; cursor: not-allowed">Open Book</button>
					</c:otherwise>
				</c:choose>
				<button class="btn btn-outline btn-sm"
					onclick="event.stopPropagation(); openEditModal(${book.id},'${book.name}','${book.description}', ${book.active})">Edit</button>
			</div>
		</div>
	</c:forEach>

	<c:if test="${empty books}">
		<div class="empty-state" style="grid-column: 1/-1">
			<div style="font-size: 2rem; margin-bottom: .5rem">&#128216;</div>
			No cash books yet. Create one to get started!
		</div>
	</c:if>
</div>

<!-- Sort Modal -->
<div id="sortModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3>Sort Books By</h3>
			<button class="modal-close" onclick="closeModal('sortModal')">&#x2715;</button>
		</div>
		<form action="${pageContext.request.contextPath}/books" method="get">
			<c:if test="${not empty search}">
				<input type="hidden" name="search" value="${search}">
			</c:if>

			<div class="form-group mb-2">
				<label class="flex"
					style="align-items: center; gap: .5rem; font-weight: 400">
					<input type="radio" name="sort" value="updated"
					${empty sort || sort eq 'updated' ? 'checked' : ''}> Last
					Updated
				</label>
			</div>
			<div class="form-group mb-2">
				<label class="flex"
					style="align-items: center; gap: .5rem; font-weight: 400">
					<input type="radio" name="sort" value="name_asc"
					${sort eq 'name_asc' ? 'checked' : ''}> Name (A to Z)
				</label>
			</div>
			<div class="form-group mb-2">
				<label class="flex"
					style="align-items: center; gap: .5rem; font-weight: 400">
					<input type="radio" name="sort" value="balance_desc"
					${sort eq 'balance_desc' ? 'checked' : ''}> Net Balance
					(High to Low)
				</label>
			</div>
			<div class="form-group mb-2">
				<label class="flex"
					style="align-items: center; gap: .5rem; font-weight: 400">
					<input type="radio" name="sort" value="balance_asc"
					${sort eq 'balance_asc' ? 'checked' : ''}> Net Balance (Low
					to High)
				</label>
			</div>
			<div class="form-group mb-2">
				<label class="flex"
					style="align-items: center; gap: .5rem; font-weight: 400">
					<input type="radio" name="sort" value="created"
					${sort eq 'created' ? 'checked' : ''}> Last Created
				</label>
			</div>

			<div class="flex mt-2">
				<button type="submit" class="btn btn-primary" style="width: 100%">Apply</button>
			</div>
		</form>
	</div>
</div>

<!-- New Book Modal -->
<div id="newBookModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3>New Cash Book</h3>
			<button class="modal-close" onclick="closeModal('newBookModal')">&#x2715;</button>
		</div>
		<form action="${pageContext.request.contextPath}/books" method="post"
			onsubmit="return document.getElementById('newBookName').value.trim()!==''">
			<input type="hidden" name="action" value="create">
			<div class="form-group mb-2">
				<label>Book Name *</label> <input type="text" name="name"
					id="newBookName" placeholder="e.g. JUN 2026, New Home, Personal"
					required autofocus>
			</div>
			<div class="form-group mb-2">
				<label>Description</label> <input type="text" name="description"
					placeholder="Optional description">
			</div>
			<div class="flex mt-2">
				<button type="submit" class="btn btn-primary ml-auto">Create
					&amp; Open</button>
			</div>
		</form>
	</div>
</div>

<!-- Edit Book Modal -->
<div id="editBookModal" class="modal-overlay">
	<div class="modal">
		<div class="modal-header">
			<h3>Edit Cash Book</h3>
			<button class="modal-close" onclick="closeModal('editBookModal')">&#x2715;</button>
		</div>
		<form action="${pageContext.request.contextPath}/books" method="post">
			<input type="hidden" name="action" value="update"> <input
				type="hidden" name="id" id="editBookId">
			<div class="form-group mb-2">
				<label>Book Name *</label> <input type="text" name="name"
					id="editBookName" required>
			</div>
			<div class="form-group mb-2">
				<label>Description</label> <input type="text" name="description"
					id="editBookDesc">
			</div>
			<div class="form-group mb-2">
				<label
					style="display: flex; justify-content: flex-start; align-items: center; gap: .5rem; font-weight: 400; text-transform: none">
					<input type="checkbox" name="active" id="editBookActive"
					value="true" style="width: auto; margin: 0"> Active
				</label>
			</div>
			<div class="flex mt-2">
				<button type="submit" class="btn btn-primary ml-auto">Save
					Changes</button>
			</div>
		</form>
	</div>
</div>

<script>
function openEditModal(id, name, desc, isActive) {
	  document.getElementById('editBookId').value   = id;
	  document.getElementById('editBookName').value = name;
	  document.getElementById('editBookDesc').value = desc;
	  document.getElementById('editBookActive').checked = (isActive === true || isActive === 'true');
	  openModal('editBookModal');
	}
// Enter → submit in modals
document.addEventListener('keydown', function(e) {
  if (e.key !== 'Enter') return;
  var m = document.querySelector('.modal-overlay.open');
  if (m) { e.preventDefault(); m.querySelector('form').requestSubmit(); }
});

//'/' → focus search box (unless already typing in an input/textarea)
document.addEventListener('keydown', function(e) {
  if (e.key === '/' && document.activeElement.tagName !== 'INPUT' && document.activeElement.tagName !== 'TEXTAREA') {
    e.preventDefault();
    var searchBox = document.getElementById('bookSearchInput');
    if (searchBox) searchBox.focus();
  }
});
</script>

<%@ include file="footer.jsp"%>
