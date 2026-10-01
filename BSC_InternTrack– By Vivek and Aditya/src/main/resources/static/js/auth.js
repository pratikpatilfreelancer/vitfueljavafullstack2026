const AUTH_KEY = 'interntrack_user';

function getCurrentUser() {
    const user = localStorage.getItem(AUTH_KEY);
    return user ? JSON.parse(user) : null;
}

function setCurrentUser(user) {
    localStorage.setItem(AUTH_KEY, JSON.stringify(user));
}

function logout() {
    localStorage.removeItem(AUTH_KEY);
    window.location.href = '/login.html';
}

function requireAuth() {
    const user = getCurrentUser();
    if (!user) {
        window.location.href = '/login.html';
        return null;
    }
    return user;
}

function showAlert(message, type = 'success') {
    const container = document.getElementById('alertContainer');
    if (!container) return;
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    container.appendChild(alert);
    setTimeout(() => alert.remove(), 3000);
}

document.addEventListener('DOMContentLoaded', () => {
    const user = getCurrentUser();
    if (user) {
        updateNavForUser(user);
    }
});

function updateNavForUser(user) {
    const nav = document.querySelector('nav ul');
    if (!nav) return;
    const existingAuth = document.getElementById('authNav');
    if (existingAuth) existingAuth.remove();
    const authLi = document.createElement('li');
    authLi.id = 'authNav';
    authLi.innerHTML = `<span style="color:white;font-size:0.9rem;">${user.email} (${user.role})</span> <a href="#" onclick="logout()" style="margin-left:10px;">Logout</a>`;
    nav.appendChild(authLi);
}

document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    try {
        const response = await apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        setCurrentUser(response);
        updateNavForUser(response);
        showAlert('Login successful!');
        setTimeout(() => {
            window.location.href = '/dashboard.html';
        }, 500);
    } catch (error) {
        showAlert(error.message || 'Login failed', 'error');
    }
});

document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const role = document.getElementById('regRole').value;
    const refId = document.getElementById('regRefId').value.trim();
    try {
        const response = await apiRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ email, password, role, refId })
        });
        showAlert('Registration successful! Please login.');
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 500);
    } catch (error) {
        showAlert(error.message || 'Registration failed', 'error');
    }
});
