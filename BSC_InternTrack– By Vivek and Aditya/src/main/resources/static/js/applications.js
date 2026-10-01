document.addEventListener('DOMContentLoaded', () => {
    loadApplications();
    loadStudentOptions();
    loadInternshipOptions();
});

function showAlert(message, type = 'success') {
    const container = document.getElementById('alertContainer');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    container.appendChild(alert);
    setTimeout(() => alert.remove(), 3000);
}

function getStatusClass(status) {
    const map = {
        'Applied': 'status-applied',
        'Shortlisted': 'status-shortlisted',
        'Interview': 'status-interview',
        'Selected': 'status-selected',
        'Rejected': 'status-rejected',
    };
    return map[status] || 'status-applied';
}

function openApplicationModal(application = null) {
    document.getElementById('applicationForm').reset();
    document.getElementById('applicationId').value = '';
    document.getElementById('applicationModalTitle').textContent = application ? 'Edit Application' : 'Add Application';
    if (application) {
        document.getElementById('applicationId').value = application.applicationId || '';
        document.getElementById('applicationStudentId').value = application.studentId || '';
        document.getElementById('applicationInternshipId').value = application.internshipId || '';
        document.getElementById('applicationDate').value = application.applicationDate || '';
        document.getElementById('applicationStatus').value = application.status || 'Applied';
        document.getElementById('applicationNotes').value = application.notes || '';
    }
    document.getElementById('applicationModal').classList.add('active');
}

function closeApplicationModal() {
    document.getElementById('applicationModal').classList.remove('active');
}

function openStatusModal(applicationId, currentStatus) {
    document.getElementById('statusApplicationId').value = applicationId;
    document.getElementById('statusValue').value = currentStatus || 'Applied';
    document.getElementById('statusModal').classList.add('active');
}

function closeStatusModal() {
    document.getElementById('statusModal').classList.remove('active');
}

async function loadStudentOptions() {
    try {
        const students = await api.students.getAll();
        const select = document.getElementById('applicationStudentId');
        select.innerHTML = students.map(s => `<option value="${s.studentId}">${escapeHtml(s.name)}</option>`).join('');
    } catch (error) {
        console.error('Failed to load students:', error);
    }
}

async function loadInternshipOptions() {
    try {
        const internships = await api.internships.getAll();
        const select = document.getElementById('applicationInternshipId');
        select.innerHTML = internships.map(i => `<option value="${i.internshipId}">${escapeHtml(i.title)}</option>`).join('');
    } catch (error) {
        console.error('Failed to load internships:', error);
    }
}

async function loadApplications() {
    try {
        const applications = await api.applications.getAll();
        const students = await api.students.getAll();
        const internships = await api.internships.getAll();
        const studentMap = {};
        students.forEach(s => { studentMap[s.studentId] = s.name; });
        const internshipMap = {};
        internships.forEach(i => { internshipMap[i.internshipId] = i.title; });

        const tbody = document.getElementById('applicationsTableBody');
        if (!applications.length) {
            tbody.innerHTML = '<tr><td colspan="6">No applications found</td></tr>';
            return;
        }
        tbody.innerHTML = applications.map(a => `
            <tr>
                <td>${escapeHtml(studentMap[a.studentId] || a.studentId)}</td>
                <td>${escapeHtml(internshipMap[a.internshipId] || a.internshipId)}</td>
                <td>${a.applicationDate || ''}</td>
                <td><span class="status-badge ${getStatusClass(a.status)}">${escapeHtml(a.status || '')}</span></td>
                <td>${escapeHtml((a.notes || '').substring(0, 50))}</td>
                <td class="actions">
                    <button class="btn btn-sm btn-primary" onclick='editApplication(${JSON.stringify(a)})'>Edit</button>
                    <button class="btn btn-sm btn-warning" onclick='openStatusModal("${a.applicationId}", "${a.status}")'>Update Status</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteApplication('${a.applicationId}')">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Failed to load applications: ' + error.message, 'error');
    }
}

function editApplication(application) {
    openApplicationModal(application);
}

async function deleteApplication(id) {
    if (!confirm('Are you sure you want to delete this application?')) return;
    try {
        await api.applications.delete(id);
        showAlert('Application deleted successfully');
        loadApplications();
    } catch (error) {
        showAlert('Failed to delete application: ' + error.message, 'error');
    }
}

document.getElementById('applicationForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const application = {
        applicationId: document.getElementById('applicationId').value || 'app-' + Date.now(),
        studentId: document.getElementById('applicationStudentId').value,
        internshipId: document.getElementById('applicationInternshipId').value,
        applicationDate: document.getElementById('applicationDate').value,
        status: document.getElementById('applicationStatus').value,
        notes: document.getElementById('applicationNotes').value,
    };

    try {
        const id = document.getElementById('applicationId').value;
        if (id) {
            await api.applications.update(id, application);
            showAlert('Application updated successfully');
        } else {
            await api.applications.create(application);
            showAlert('Application created successfully');
        }
        closeApplicationModal();
        loadApplications();
    } catch (error) {
        showAlert(error.message, 'error');
    }
});

document.getElementById('statusForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const applicationId = document.getElementById('statusApplicationId').value;
    const status = document.getElementById('statusValue').value;
    try {
        await api.applications.updateStatus(applicationId, status);
        showAlert('Status updated successfully');
        closeStatusModal();
        loadApplications();
    } catch (error) {
        showAlert(error.message, 'error');
    }
});

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
