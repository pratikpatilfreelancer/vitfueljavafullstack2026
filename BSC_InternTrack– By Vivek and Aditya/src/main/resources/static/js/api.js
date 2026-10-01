const API_BASE = '/api';

async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const config = {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
        ...options,
    };

    if (config.body && typeof config.body === 'object') {
        config.body = JSON.stringify(config.body);
    }

    try {
        const response = await fetch(url, config);
        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: response.statusText }));
            throw new Error(error.message || `HTTP error! status: ${response.status}`);
        }
        if (response.status === 204) {
            return null;
        }
        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

const api = {
    students: {
        getAll: () => apiRequest('/students'),
        getById: (id) => apiRequest(`/students/${id}`),
        getByEmail: (email) => apiRequest(`/students/email/${encodeURIComponent(email)}`),
        create: (student) => apiRequest('/students', { method: 'POST', body: student }),
        update: (id, student) => apiRequest(`/students/${id}`, { method: 'PUT', body: student }),
        delete: (id) => apiRequest(`/students/${id}`, { method: 'DELETE' }),
    },
    companies: {
        getAll: () => apiRequest('/companies'),
        getById: (id) => apiRequest(`/companies/${id}`),
        create: (company) => apiRequest('/companies', { method: 'POST', body: company }),
        update: (id, company) => apiRequest(`/companies/${id}`, { method: 'PUT', body: company }),
        delete: (id) => apiRequest(`/companies/${id}`, { method: 'DELETE' }),
    },
    internships: {
        getAll: () => apiRequest('/internships'),
        getById: (id) => apiRequest(`/internships/${id}`),
        getByCompanyId: (companyId) => apiRequest(`/internships/company/${companyId}`),
        create: (internship) => apiRequest('/internships', { method: 'POST', body: internship }),
        update: (id, internship) => apiRequest(`/internships/${id}`, { method: 'PUT', body: internship }),
        delete: (id) => apiRequest(`/internships/${id}`, { method: 'DELETE' }),
    },
    applications: {
        getAll: () => apiRequest('/applications'),
        getById: (id) => apiRequest(`/applications/${id}`),
        getByStudentId: (studentId) => apiRequest(`/applications/student/${studentId}`),
        getByInternshipId: (internshipId) => apiRequest(`/applications/internship/${internshipId}`),
        getByStatus: (status) => apiRequest(`/applications/status/${encodeURIComponent(status)}`),
        create: (application) => apiRequest('/applications', { method: 'POST', body: application }),
        update: (id, application) => apiRequest(`/applications/${id}`, { method: 'PUT', body: application }),
        updateStatus: (id, status) => apiRequest(`/applications/${id}/status?status=${encodeURIComponent(status)}`, { method: 'PUT' }),
        delete: (id) => apiRequest(`/applications/${id}`, { method: 'DELETE' }),
    },
    stats: {
        countAll: () => apiRequest('/applications/stats/all'),
        countByStatus: (status) => apiRequest(`/applications/stats/status/${encodeURIComponent(status)}`),
    },
    auth: {
        login: (email, password) => apiRequest('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
        register: (user) => apiRequest('/auth/register', { method: 'POST', body: JSON.stringify(user) }),
    },
    notifications: {
        getByUserId: (userId) => apiRequest(`/notifications/user/${encodeURIComponent(userId)}`),
        getUnread: (userId) => apiRequest(`/notifications/user/${encodeURIComponent(userId)}/unread`),
        getById: (id) => apiRequest(`/notifications/${encodeURIComponent(id)}`),
        create: (notification) => apiRequest('/notifications', { method: 'POST', body: JSON.stringify(notification) }),
        markAsRead: (id) => apiRequest(`/notifications/${encodeURIComponent(id)}/read`, { method: 'PUT' }),
        delete: (id) => apiRequest(`/notifications/${encodeURIComponent(id)}`, { method: 'DELETE' }),
        deleteByUserId: (userId) => apiRequest(`/notifications/user/${encodeURIComponent(userId)}`, { method: 'DELETE' }),
    },
};
