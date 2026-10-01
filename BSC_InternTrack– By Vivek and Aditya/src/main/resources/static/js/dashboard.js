document.addEventListener('DOMContentLoaded', loadDashboardStats);

async function loadDashboardStats() {
    try {
        const totalRes = await api.stats.countAll();
        const appliedRes = await api.stats.countByStatus('Applied');
        const shortlistedRes = await api.stats.countByStatus('Shortlisted');
        const interviewRes = await api.stats.countByStatus('Interview');
        const selectedRes = await api.stats.countByStatus('Selected');
        const rejectedRes = await api.stats.countByStatus('Rejected');

        document.getElementById('statTotal').textContent = totalRes.total || 0;
        document.getElementById('statApplied').textContent = appliedRes.count || 0;
        document.getElementById('statShortlisted').textContent = shortlistedRes.count || 0;
        document.getElementById('statInterview').textContent = interviewRes.count || 0;
        document.getElementById('statSelected').textContent = selectedRes.count || 0;
        document.getElementById('statRejected').textContent = rejectedRes.count || 0;
    } catch (error) {
        console.error('Failed to load dashboard stats:', error);
    }
}
