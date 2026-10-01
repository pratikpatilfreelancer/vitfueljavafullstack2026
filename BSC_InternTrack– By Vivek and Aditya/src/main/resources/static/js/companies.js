document.addEventListener('DOMContentLoaded', loadCompanies);

function showAlert(message, type = 'success') {
    const container = document.getElementById('alertContainer');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    container.appendChild(alert);
    setTimeout(() => alert.remove(), 3000);
}

function openCompanyModal(company = null) {
    document.getElementById('companyForm').reset();
    document.getElementById('companyId').value = '';
    document.getElementById('companyModalTitle').textContent = company ? 'Edit Company' : 'Add Company';
    if (company) {
        document.getElementById('companyId').value = company.companyId || '';
        document.getElementById('companyName').value = company.companyName || '';
        document.getElementById('companyLocation').value = company.location || '';
        document.getElementById('companyIndustry').value = company.industry || '';
        document.getElementById('companyWebsite').value = company.website || '';
        document.getElementById('companyContactEmail').value = company.contactEmail || '';
    }
    document.getElementById('companyModal').classList.add('active');
}

function closeCompanyModal() {
    document.getElementById('companyModal').classList.remove('active');
}

async function loadCompanies() {
    try {
        const companies = await api.companies.getAll();
        const tbody = document.getElementById('companiesTableBody');
        if (!companies.length) {
            tbody.innerHTML = '<tr><td colspan="6">No companies found</td></tr>';
            return;
        }
        tbody.innerHTML = companies.map(c => `
            <tr>
                <td>${escapeHtml(c.companyName)}</td>
                <td>${escapeHtml(c.location || '')}</td>
                <td>${escapeHtml(c.industry || '')}</td>
                <td>${escapeHtml(c.website || '')}</td>
                <td>${escapeHtml(c.contactEmail || '')}</td>
                <td class="actions">
                    <button class="btn btn-sm btn-primary" onclick='editCompany(${JSON.stringify(c)})'>Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteCompany('${c.companyId}')">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Failed to load companies: ' + error.message, 'error');
    }
}

function editCompany(company) {
    openCompanyModal(company);
}

async function deleteCompany(id) {
    if (!confirm('Are you sure you want to delete this company?')) return;
    try {
        await api.companies.delete(id);
        showAlert('Company deleted successfully');
        loadCompanies();
    } catch (error) {
        showAlert('Failed to delete company: ' + error.message, 'error');
    }
}

document.getElementById('companyForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const company = {
        companyId: document.getElementById('companyId').value || 'company-' + Date.now(),
        companyName: document.getElementById('companyName').value,
        location: document.getElementById('companyLocation').value,
        industry: document.getElementById('companyIndustry').value,
        website: document.getElementById('companyWebsite').value,
        contactEmail: document.getElementById('companyContactEmail').value,
    };

    try {
        const id = document.getElementById('companyId').value;
        if (id) {
            await api.companies.update(id, company);
            showAlert('Company updated successfully');
        } else {
            await api.companies.create(company);
            showAlert('Company created successfully');
        }
        closeCompanyModal();
        loadCompanies();
    } catch (error) {
        showAlert(error.message, 'error');
    }
});

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
