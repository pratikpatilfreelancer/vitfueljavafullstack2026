import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import client from '../../api/client';
import StatusStamp from '../../components/StatusStamp';

export default function AccountsDashboard() {
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
        <h1 className="page-title">Accounts Dashboard</h1>
        <p className="page-subtitle">Approved vouchers ready for reimbursement processing.</p>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {summary && (
        <>
          <div className="kpi-grid">
            <div className="kpi-card"><div className="kpi-label">Total Vouchers</div><div className="kpi-value">{summary.totalVouchers}</div></div>
            <div className="kpi-card"><div className="kpi-label">Pending Approval</div><div className="kpi-value">{summary.pendingApproval}</div></div>
            <div className="kpi-card"><div className="kpi-label">Approved</div><div className="kpi-value">{summary.approvedVouchers}</div></div>
            <div className="kpi-card"><div className="kpi-label">Rejected</div><div className="kpi-value">{summary.rejectedVouchers}</div></div>
            <div className="kpi-card"><div className="kpi-label">Total Approved Amount</div><div className="kpi-value money">{summary.totalApprovedExpenseAmount.toLocaleString('en-IN')}</div></div>
          </div>

          <div className="panel">
            <div className="panel-header">
              <span className="panel-title">Recent Approved Vouchers</span>
              <Link to="/accounts/vouchers" className="btn btn-brass btn-sm">View All</Link>
            </div>
            {summary.recentApprovedVouchers.length === 0 ? (
              <div className="empty-state">No approved vouchers yet.</div>
            ) : (
              <table className="ledger">
                <thead><tr><th>Voucher #</th><th>Employee</th><th>Title</th><th>Amount</th><th>Status</th></tr></thead>
                <tbody>
                  {summary.recentApprovedVouchers.map((v) => (
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
