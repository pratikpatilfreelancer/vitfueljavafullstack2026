import { useEffect, useState, useCallback } from 'react';
import client from '../api/client';
import FiltersBar from '../components/FiltersBar';
import VoucherTable from '../components/VoucherTable';

export default function VoucherListPage({ title, subtitle, fixedStatus, basePath }) {
  const [vouchers, setVouchers] = useState([]);
  const [filters, setFilters] = useState({});
  const [sort, setSort] = useState({ sortBy: 'created_at', sortDir: 'desc' });
  const [page, setPage] = useState(1);
  const [meta, setMeta] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(() => {
    setLoading(true);
    const params = { ...filters, ...sort, page, limit: 10 };
    if (fixedStatus) params.status = fixedStatus;
    Object.keys(params).forEach((k) => (params[k] === '' || params[k] == null) && delete params[k]);
    client.get('/vouchers', { params })
      .then((res) => {
        setVouchers(res.data.data);
        setMeta(res.data.meta);
      })
      .finally(() => setLoading(false));
  }, [filters, sort, fixedStatus, page]);

  useEffect(() => { load(); }, [load]);

  function handleSort(key) {
    setSort((s) => ({ sortBy: key, sortDir: s.sortBy === key && s.sortDir === 'desc' ? 'asc' : 'desc' }));
  }

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">{title}</h1>
        <p className="page-subtitle">{subtitle}</p>
      </div>

      <div className="panel">
        <div className="panel-header">
          <span className="panel-title">Ledger</span>
        </div>
        <FiltersBar filters={filters} setFilters={setFilters} showEmployeeFilter={!fixedStatus || true} />
        {loading ? (
          <div className="loading-state">Loading vouchers\u2026</div>
        ) : (
          <VoucherTable 
            vouchers={vouchers} 
            sort={sort} 
            onSort={handleSort} 
            basePath={basePath} 
            meta={meta}
            onPageChange={setPage}
          />
        )}
      </div>
    </div>
  );
}
