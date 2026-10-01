let companiesCache = [];

document.addEventListener('DOMContentLoaded', () => {
    loadStudents();
    loadCompanies();
});

function showAlert(message, type = 'success') {
    const container = document.getElementById('alertContainer');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    container.appendChild(alert);
    setTimeout(() => alert.remove(), 3000);
}

function openStudentModal(student = null) {
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = '';
    document.getElementById('studentModalTitle').textContent = student ? 'Edit Student' : 'Add Student';
    if (student) {
        document.getElementById('studentId').value = student.studentId || '';
        document.getElementById('studentName').value = student.name || '';
        document.getElementById('studentEmail').value = student.email || '';
        document.getElementById('studentPhone').value = student.phone || '';
        document.getElementById('studentCollege').value = student.college || '';
        document.getElementById('studentCourse').value = student.course || '';
        document.getElementById('studentYear').value = student.year || '';
        document.getElementById('studentSkills').value = student.skills || '';
        document.getElementById('studentResumeUrl').value = student.resumeUrl || '';
    }
    document.getElementById('studentModal').classList.add('active');
}

function closeStudentModal() {
    document.getElementById('studentModal').classList.remove('active');
}

async function loadStudents() {
    try {
        const students = await api.students.getAll();
        const tbody = document.getElementById('studentsTableBody');
        if (!students.length) {
            tbody.innerHTML = '<tr><td colspan="8">No students found</td></tr>';
            return;
        }
        tbody.innerHTML = students.map(s => `
            <tr>
                <td>${escapeHtml(s.name)}</td>
                <td>${escapeHtml(s.email)}</td>
                <td>${escapeHtml(s.phone || '')}</td>
                <td>${escapeHtml(s.college || '')}</td>
                <td>${escapeHtml(s.course || '')}</td>
                <td>${s.year || ''}</td>
                <td>${escapeHtml((s.skills || '').substring(0, 50))}</td>
                <td class="actions">
                    <button class="btn btn-sm btn-primary" onclick='editStudent(${JSON.stringify(s)})'>Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteStudent('${s.studentId}')">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        showAlert('Failed to load students: ' + error.message, 'error');
    }
}

async function loadCompanies() {
    try {
        companiesCache = await api.companies.getAll();
    } catch (error) {
        console.error('Failed to load companies:', error);
    }
}

function editStudent(student) {
    openStudentModal(student);
}

async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) return;
    try {
        await api.students.delete(id);
        showAlert('Student deleted successfully');
        loadStudents();
    } catch (error) {
        showAlert('Failed to delete student: ' + error.message, 'error');
    }
}

document.getElementById('studentForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const student = {
        studentId: document.getElementById('studentId').value || 'student-' + Date.now(),
        name: document.getElementById('studentName').value,
        email: document.getElementById('studentEmail').value,
        phone: document.getElementById('studentPhone').value,
        college: document.getElementById('studentCollege').value,
        course: document.getElementById('studentCourse').value,
        year: document.getElementById('studentYear').value ? parseInt(document.getElementById('studentYear').value) : null,
        skills: document.getElementById('studentSkills').value,
        resumeUrl: document.getElementById('studentResumeUrl').value,
    };

    try {
        const id = document.getElementById('studentId').value;
        if (id) {
            await api.students.update(id, student);
            showAlert('Student updated successfully');
        } else {
            await api.students.create(student);
            showAlert('Student created successfully');
        }
        closeStudentModal();
        loadStudents();
    } catch (error) {
        showAlert(error.message, 'error');
    }
});

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
