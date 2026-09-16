export default function FiltersBar({ filters, setFilters, showEmployeeFilter = true }) {
  function update(key, value) {
    setFilters((f) => ({ ...f, [key]: value }));
  }

  return (
    <div className="filters-bar">
      <input
        className="field-input"
        placeholder="Voucher #"
        value={filters.voucherNumber || ''}
        onChange={(e) => update('voucherNumber', e.target.value)}
      />
      {showEmployeeFilter && (
        <input
          className="field-input"
          placeholder="Employee name"
          value={filters.employeeName || ''}
          onChange={(e) => update('employeeName', e.target.value)}
        />
      )}
      <input
        className="field-input"
        placeholder="Department"
        value={filters.department || ''}
        onChange={(e) => update('department', e.target.value)}
      />
      <input
        className="field-input"
        placeholder="Category"
        value={filters.category || ''}
        onChange={(e) => update('category', e.target.value)}
      />
      <select
        className="field-select"
        value={filters.status || ''}
        onChange={(e) => update('status', e.target.value)}
      >
        <option value="">All statuses</option>
        <option value="DRAFT">Draft</option>
        <option value="SUBMITTED">Pending Approval</option>
        <option value="APPROVED">Approved</option>
        <option value="REJECTED">Rejected</option>
      </select>
      <input
        type="date"
        className="field-input"
        value={filters.dateFrom || ''}
        onChange={(e) => update('dateFrom', e.target.value)}
        title="From date"
      />
      <input
        type="date"
        className="field-input"
        value={filters.dateTo || ''}
        onChange={(e) => update('dateTo', e.target.value)}
        title="To date"
      />
      <input
        type="number"
        className="field-input"
        placeholder="Min amount"
        style={{ width: 100 }}
        value={filters.amountMin || ''}
        onChange={(e) => update('amountMin', e.target.value)}
      />
      <input
        type="number"
        className="field-input"
        placeholder="Max amount"
        style={{ width: 100 }}
        value={filters.amountMax || ''}
        onChange={(e) => update('amountMax', e.target.value)}
      />
    </div>
  );
}
