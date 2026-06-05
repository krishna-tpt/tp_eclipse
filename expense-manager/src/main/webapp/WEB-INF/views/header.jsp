<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<base href="${pageContext.request.contextPath}/">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} — Expense Manager</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">
        <span class="nav-icon">◈</span>
        <span class="nav-title">ExpenseOS</span>
    </div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/" class="${activePage == 'home' ? 'active' : ''}">Dashboard</a>
        <a href="${pageContext.request.contextPath}/transactions" class="${activePage == 'txn' ? 'active' : ''}">Transactions</a>
        <a href="${pageContext.request.contextPath}/reports" class="${activePage == 'reports' ? 'active' : ''}">Reports</a>
    </div>
    <div class="nav-actions">
        <button class="sync-btn" onclick="syncNow()" title="Sync from WorkDrive">
            <span id="syncIcon">⟳</span> Sync
        </button>
    </div>
</nav>
<div id="sync-toast" class="toast hidden"></div>
<main class="page-content">
