import { useNavigate } from 'react-router-dom';
import StatusStamp from './StatusStamp';

const COLUMNS = [
  { key: 'voucher_number', label: 'Voucher #' },
  { key: 'employee_name', label: 'Employee' },
  { key: 'department', label: 'Department' },
  { key: 'expense_title', label: 'Title' },
  { key: 'expense_date', label: 'Expense Date' },
  { key: 'amount', label: 'Amount' },
  { key: 'status', label: 'Status' },
];

export default function VoucherTable({ vouchers, sort, onSort, basePath, meta, onPageChange }) {
  const navigate = useNavigate();

  if (!vouchers.length) {
    return <div className="empty-state">No vouchers match your search yet.</div>;
  }

  return (
    <>
    <table className="ledger">
      <thead>
        <tr>
          {COLUMNS.map((c) => (
            <th key={c.key} onClick={() => onSort && onSort(c.key)}>
              {c.label}
              {sort?.sortBy === c.key ? (sort.sortDir === 'asc' ? ' \u2191' : ' \u2193') : ''}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {vouchers.map((v) => (
          <tr key={v.id} onClick={() => navigate(`${basePath}/${v.id}`)} style={{ cursor: 'pointer' }}>
            <td className="voucher-number">{v.voucher_number}</td>
            <td>{v.employee_name}</td>
            <td>{v.department}</td>
            <td>{v.expense_title}</td>
            <td>{v.expense_date}</td>
            <td className="amount-cell">{v.amount.toLocaleString('en-IN')}</td>
            <td><StatusStamp status={v.status} /></td>
          </tr>
        ))}
      </tbody>
    </table>
    {meta && meta.totalPages > 1 && (
      <div className="pagination-bar" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px', borderTop: '1px solid #e3d9c3', background: '#fffdf9' }}>
        <div style={{ fontSize: '13px', color: 'var(--slate)' }}>
          Showing {(meta.page - 1) * meta.limit + 1} to {Math.min(meta.page * meta.limit, meta.total)} of {meta.total} results
        </div>
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <button 
            className="btn btn-outline btn-sm" 
            disabled={meta.page <= 1} 
            onClick={() => onPageChange(meta.page - 1)}
          >
            &larr; Prev
          </button>
          <span style={{ padding: '0 8px', fontSize: '13px', fontWeight: 'bold' }}>
            Page {meta.page} of {meta.totalPages}
          </span>
          <button 
            className="btn btn-outline btn-sm" 
            disabled={meta.page >= meta.totalPages} 
            onClick={() => onPageChange(meta.page + 1)}
          >
            Next &rarr;
          </button>
        </div>
      </div>
    )}
    </>
  );
}
