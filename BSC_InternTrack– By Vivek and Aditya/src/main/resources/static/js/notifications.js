document.addEventListener('DOMContentLoaded', () => {
    requireAuth();
    loadNotifications();
    updateUnreadBadge();
});

function showAlert(message, type = 'success') {
    const container = document.getElementById('alertContainer');
    if (!container) return;
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    container.appendChild(alert);
    setTimeout(() => alert.remove(), 3000);
}

async function loadNotifications() {
    try {
        const user = getCurrentUser();
        if (!user) return;
        const notifications = await apiRequest(`/notifications/user/${user.userId}`);
        const tbody = document.getElementById('notificationsTableBody');
        if (!notifications || !notifications.length) {
            tbody.innerHTML = '<tr><td colspan="5">No notifications found</td></tr>';
            return;
        }
        tbody.innerHTML = notifications.map(n => `
            <tr style="${n.isRead ? '' : 'background-color: #eff6ff;'}">
                <td>${escapeHtml(n.message)}</td>
                <td>${escapeHtml(n.type || 'INFO')}</td>
                <td>${n.isRead ? '<span class="status-badge status-applied">Read</span>' : '<span class="status-badge status-interview">Unread</span>'}</td>
                <td>${n.createdAt ? n.createdAt.substring(0, 19).replace('T', ' ') : ''}</td>
                <td class="actions">
                    ${!n.isRead ? `<button class="btn btn-sm btn-primary" onclick="markAsRead('${n.notificationId}')">Mark Read</button>` : ''}
                    <button class="btn btn-sm btn-danger" onclick="deleteNotification('${n.notificationId}')">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Failed to load notifications: ' + error.message, 'error');
    }
}

async function markAsRead(notificationId) {
    try {
        await apiRequest(`/notifications/${notificationId}/read`, { method: 'PUT' });
        showAlert('Notification marked as read');
        loadNotifications();
        updateUnreadBadge();
    } catch (error) {
        showAlert(error.message, 'error');
    }
}

async function deleteNotification(notificationId) {
    if (!confirm('Delete this notification?')) return;
    try {
        await apiRequest(`/notifications/${notificationId}`, { method: 'DELETE' });
        showAlert('Notification deleted');
        loadNotifications();
        updateUnreadBadge();
    } catch (error) {
        showAlert(error.message, 'error');
    }
}

async function markAllAsRead() {
    try {
        const user = getCurrentUser();
        if (!user) return;
        const notifications = await apiRequest(`/notifications/user/${user.userId}`);
        for (const n of notifications) {
            if (!n.isRead) {
                await apiRequest(`/notifications/${n.notificationId}/read`, { method: 'PUT' });
            }
        }
        showAlert('All notifications marked as read');
        loadNotifications();
        updateUnreadBadge();
    } catch (error) {
        showAlert(error.message, 'error');
    }
}

async function updateUnreadBadge() {
    try {
        const user = getCurrentUser();
        if (!user) return;
        const notifications = await apiRequest(`/notifications/user/${user.userId}/unread`);
        const badge = document.getElementById('unreadBadge');
        if (badge) {
            if (notifications && notifications.length > 0) {
                badge.textContent = notifications.length;
                badge.classList.remove('hidden');
            } else {
                badge.classList.add('hidden');
            }
        }
    } catch (error) {
        console.error('Failed to update unread badge:', error);
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
