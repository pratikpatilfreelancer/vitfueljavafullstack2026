document.addEventListener('DOMContentLoaded', () => {
    loadInternships();
    loadCompanyOptions();
});

function showAlert(message, type = 'success') {
    const container = document.getElementById('alertContainer');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    container.appendChild(alert);
    setTimeout(() => alert.remove(), 3000);
}

function openInternshipModal(internship = null) {
    document.getElementById('internshipForm').reset();
    document.getElementById('internshipId').value = '';
    document.getElementById('internshipModalTitle').textContent = internship ? 'Edit Internship' : 'Add Internship';
    if (internship) {
        document.getElementById('internshipId').value = internship.internshipId || '';
        document.getElementById('internshipTitle').value = internship.title || '';
        document.getElementById('internshipCompanyId').value = internship.companyId || '';
        document.getElementById('internshipLocation').value = internship.location || '';
        document.getElementById('internshipDescription').value = internship.description || '';
        document.getElementById('internshipDuration').value = internship.duration || '';
        document.getElementById('internshipStipend').value = internship.stipend || '';
        document.getElementById('internshipRequiredSkills').value = internship.requiredSkills || '';
        document.getElementById('internshipDeadline').value = internship.applicationDeadline || '';
    }
    document.getElementById('internshipModal').classList.add('active');
}

function closeInternshipModal() {
    document.getElementById('internshipModal').classList.remove('active');
}

async function loadCompanyOptions() {
    try {
        const companies = await api.companies.getAll();
        const select = document.getElementById('internshipCompanyId');
        select.innerHTML = companies.map(c => `<option value="${c.companyId}">${escapeHtml(c.companyName)}</option>`).join('');
    } catch (error) {
        console.error('Failed to load companies:', error);
    }
}

async function loadInternships() {
    try {
        const internships = await api.internships.getAll();
        const companies = await api.companies.getAll();
        const companyMap = {};
        companies.forEach(c => { companyMap[c.companyId] = c.companyName; });

        const tbody = document.getElementById('internshipsTableBody');
        if (!internships.length) {
            tbody.innerHTML = '<tr><td colspan="7">No internships found</td></tr>';
            return;
        }
        tbody.innerHTML = internships.map(i => `
            <tr>
                <td>${escapeHtml(i.title)}</td>
                <td>${escapeHtml(companyMap[i.companyId] || i.companyId)}</td>
                <td>${escapeHtml(i.location || '')}</td>
                <td>${escapeHtml(i.duration || '')}</td>
                <td>${i.stipend ? 'Rs. ' + i.stipend : ''}</td>
                <td>${i.applicationDeadline || ''}</td>
                <td class="actions">
                    <button class="btn btn-sm btn-primary" onclick='editInternship(${JSON.stringify(i)})'>Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteInternship('${i.internshipId}')">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Failed to load internships: ' + error.message, 'error');
    }
}

function editInternship(internship) {
    openInternshipModal(internship);
}

async function deleteInternship(id) {
    if (!confirm('Are you sure you want to delete this internship?')) return;
    try {
        await api.internships.delete(id);
        showAlert('Internship deleted successfully');
        loadInternships();
    } catch (error) {
        showAlert('Failed to delete internship: ' + error.message, 'error');
    }
}

document.getElementById('internshipForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const internship = {
        internshipId: document.getElementById('internshipId').value || 'intern-' + Date.now(),
        companyId: document.getElementById('internshipCompanyId').value,
        title: document.getElementById('internshipTitle').value,
        description: document.getElementById('internshipDescription').value,
        location: document.getElementById('internshipLocation').value,
        duration: document.getElementById('internshipDuration').value,
        stipend: document.getElementById('internshipStipend').value ? parseFloat(document.getElementById('internshipStipend').value) : null,
        requiredSkills: document.getElementById('internshipRequiredSkills').value,
        applicationDeadline: document.getElementById('internshipDeadline').value,
    };

    try {
        const id = document.getElementById('internshipId').value;
        if (id) {
            await api.internships.update(id, internship);
            showAlert('Internship updated successfully');
        } else {
            await api.internships.create(internship);
            showAlert('Internship created successfully');
        }
        closeInternshipModal();
        loadInternships();
    } catch (error) {
        showAlert(error.message, 'error');
    }
});

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
