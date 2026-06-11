<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>ExpenseIQ – Error</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@700;800&family=DM+Sans:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        *{box-sizing:border-box;margin:0;padding:0}
        body{font-family:'DM Sans',sans-serif;background:#0d0d14;color:#f0f0fa;min-height:100vh;display:flex;align-items:center;justify-content:center}
        .err-box{text-align:center;max-width:480px;padding:40px 30px}
        .err-code{font-family:'Syne',sans-serif;font-size:7rem;font-weight:800;
                  background:linear-gradient(135deg,#6366f1,#818cf8);
                  -webkit-background-clip:text;-webkit-text-fill-color:transparent;line-height:1}
        .err-title{font-family:'Syne',sans-serif;font-size:1.5rem;font-weight:700;margin:16px 0 10px}
        .err-desc{color:#5a5e78;font-size:.9rem;line-height:1.6;margin-bottom:28px}
        .btn-home{display:inline-flex;align-items:center;gap:8px;padding:11px 24px;border-radius:12px;
                  background:#6366f1;color:#fff;text-decoration:none;font-size:.9rem;font-weight:500;transition:.15s}
        .btn-home:hover{background:#818cf8;transform:translateY(-1px)}
    </style>
</head>
<body>
    <div class="err-box">
        <div class="err-code">${pageContext.errorData.statusCode > 0 ? pageContext.errorData.statusCode : '500'}</div>
        <div class="err-title">
            ${pageContext.errorData.statusCode == 404 ? 'Page Not Found' : 'Something Went Wrong'}
        </div>
        <p class="err-desc">
            ${pageContext.errorData.statusCode == 404
              ? 'The page you are looking for does not exist or has been moved.'
              : 'An unexpected error occurred. Please try again or contact support.'}
        </p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn-home">
            <i class="fas fa-house"></i> Go to Dashboard
        </a>
    </div>
</body>
</html>
