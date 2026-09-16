import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import client from '../../api/client';
import StatusStamp from '../../components/StatusStamp';

export default function DirectorDashboard() {
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
        <h1 className="page-title">Director Dashboard</h1>
        <p className="page-subtitle">Organization-wide voucher activity awaiting your review.</p>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {summary && (
        <>
          <div className="kpi-grid">
            <div className="kpi-card"><div className="kpi-label">Pending Approval</div><div className="kpi-value">{summary.pendingApprovalCount}</div></div>
            <div className="kpi-card"><div className="kpi-label">Approved Today</div><div className="kpi-value">{summary.approvedToday}</div></div>
            <div className="kpi-card"><div className="kpi-label">Rejected Today</div><div className="kpi-value">{summary.rejectedToday}</div></div>
            <div className="kpi-card"><div className="kpi-label">Total Pending Amount</div><div className="kpi-value money">{summary.totalPendingAmount.toLocaleString('en-IN')}</div></div>
          </div>

          <div className="panel">
            <div className="panel-header">
              <span className="panel-title">Recent Voucher Activity</span>
              <Link to="/director/pending" className="btn btn-brass btn-sm">Review Pending</Link>
            </div>
            {summary.recentActivity.length === 0 ? (
              <div className="empty-state">No recent activity.</div>
            ) : (
              <table className="ledger">
                <thead><tr><th>Voucher #</th><th>Employee</th><th>Title</th><th>Amount</th><th>Status</th></tr></thead>
                <tbody>
                  {summary.recentActivity.map((v) => (
                    <tr key={v.id}>
                      <td className="voucher-number">{v.voucher_number}</td>
                      <td>{v.employee_name}</td>
                      <td>{v.expense_title}</td>
                      <td className="amount-cell">{v.amount.toLocaleString('en-IN')}</td>
                      <td><StatusStamp status={v.status} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}
    </div>
  );
}
