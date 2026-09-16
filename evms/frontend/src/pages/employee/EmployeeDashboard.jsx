import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import client from '../../api/client';

export default function EmployeeDashboard() {
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    client.get('/vouchers/dashboard/summary')
      .then((res) => setSummary(res.data))
      .catch(() => setError('Could not load dashboard summary.'));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">My Dashboard</h1>
        <p className="page-subtitle">A snapshot of your expense vouchers.</p>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {summary && (
        <div className="kpi-grid">
          <div className="kpi-card"><div className="kpi-label">Total Vouchers</div><div className="kpi-value">{summary.totalVouchers}</div></div>
          <div className="kpi-card"><div className="kpi-label">Draft</div><div className="kpi-value">{summary.draftVouchers}</div></div>
          <div className="kpi-card"><div className="kpi-label">Pending Approval</div><div className="kpi-value">{summary.pendingApproval}</div></div>
          <div className="kpi-card"><div className="kpi-label">Approved</div><div className="kpi-value">{summary.approvedVouchers}</div></div>
          <div className="kpi-card"><div className="kpi-label">Rejected</div><div className="kpi-value">{summary.rejectedVouchers}</div></div>
          <div className="kpi-card"><div className="kpi-label">Total Claimed</div><div className="kpi-value money">{summary.totalAmountClaimed.toLocaleString('en-IN')}</div></div>
        </div>
      )}

      <div className="panel" style={{ padding: 24 }}>
        <p style={{ marginTop: 0 }}>Ready to file a new expense?</p>
        <Link to="/employee/create" className="btn btn-brass">Create Voucher</Link>
      </div>
    </div>
  );
}
