import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import client from '../../api/client';

const CATEGORIES = ['Travel', 'Food & Entertainment', 'Office Supplies', 'Accommodation', 'Client Meeting', 'Other'];
const ACCEPTED_PROOF_TYPES = 'image/png,image/jpeg,image/jpg,image/gif,image/webp,application/pdf';
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

export default function CreateVoucher() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    department: '',
    employeeCode: '',
    expenseDate: '',
    expenseTitle: '',
    expenseCategory: CATEGORIES[0],
    expenseDescription: '',
    amount: '',
  });
  const [proofFile, setProofFile] = useState(null);
  const [proofPreview, setProofPreview] = useState(null);
  const fileInputRef = useRef(null);
  const [error, setError] = useState('');
  const [fileError, setFileError] = useState('');
  const [saving, setSaving] = useState(false);

  function update(key, value) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  function handleProofSelect(e) {
    const file = e.target.files[0];
    if (!file) return;
    if (file.size > MAX_FILE_SIZE) {
      setFileError(`File size (${(file.size / 1024 / 1024).toFixed(1)} MB) exceeds the maximum allowed size of 10 MB.`);
      if (fileInputRef.current) fileInputRef.current.value = '';
      return;
    }
    setFileError('');
    setError('');
    setProofFile(file);
    if (file.type === 'application/pdf') {
      setProofPreview(null); // No image preview for PDF
    } else {
      setProofPreview(URL.createObjectURL(file));
    }
  }

  function removeProof() {
    setProofFile(null);
    setProofPreview(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    if (!proofFile) {
      setError('Proof document (bill/voucher image or PDF) is mandatory.');
      return;
    }

    setSaving(true);
    try {
      const fd = new FormData();
      fd.append('data', new Blob([JSON.stringify(form)], { type: 'application/json' }));
      fd.append('proofDocument', proofFile);
      const { data } = await client.post('/vouchers', fd, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      navigate(`/employee/vouchers/${data.id}`);
    } catch (err) {
      setError(err.response?.data?.error || 'Could not create voucher.');
    } finally {
      setSaving(false);
    }
  }

  // Calculate the minimum allowed expense date (7 days ago)
  const minDateObj = new Date();
  minDateObj.setDate(minDateObj.getDate() - 7);
  const minDateStr = minDateObj.toISOString().split('T')[0];

  return (
    <div className="page-container fade-in">
      <div className="page-header">
        <h1 className="page-title">Create Voucher</h1>
        <p className="page-subtitle">Saved as Draft first &mdash; you can edit it before submitting for approval.</p>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <form className="form-panel" onSubmit={handleSubmit}>
        <div className="form-grid">
          <div className="field-group">
            <label className="field-label">Department *</label>
            <input className="field-input" required value={form.department} onChange={(e) => update('department', e.target.value)} />
          </div>
          <div className="field-group">
            <label className="field-label">Employee ID (optional)</label>
            <input className="field-input" value={form.employeeCode} onChange={(e) => update('employeeCode', e.target.value)} />
          </div>
          <div className="field-group">
            <label className="field-label">Date of Expense *</label>
            <input className="field-input" type="date" required min={minDateStr} max={new Date().toISOString().split('T')[0]} value={form.expenseDate} onChange={(e) => update('expenseDate', e.target.value)} />
          </div>
          <div className="field-group">
            <label className="field-label">Expense Category</label>
            <select className="field-select" value={form.expenseCategory} onChange={(e) => update('expenseCategory', e.target.value)}>
              {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
          <div className="field-group span-2">
            <label className="field-label">Expense Title *</label>
            <input className="field-input" required value={form.expenseTitle} onChange={(e) => update('expenseTitle', e.target.value)} />
          </div>
          <div className="field-group span-2">
            <label className="field-label">Expense Description</label>
            <textarea className="field-textarea" rows={3} value={form.expenseDescription} onChange={(e) => update('expenseDescription', e.target.value)} />
          </div>
          <div className="field-group">
            <label className="field-label">Amount ({'\u20B9'}) *</label>
            <input className="field-input" type="number" min="0.01" step="0.01" required value={form.amount} onChange={(e) => update('amount', e.target.value)} />
          </div>

          {/* Proof Document Upload */}
          <div className="field-group span-2">
            <label className="field-label">Proof Document (Bill / Voucher) *</label>
            <div
              className={`proof-upload-area${proofFile ? ' has-file' : ''}`}
              onClick={() => !proofFile && fileInputRef.current?.click()}
            >
              <input
                ref={fileInputRef}
                type="file"
                accept={ACCEPTED_PROOF_TYPES}
                onChange={handleProofSelect}
                style={{ display: 'none' }}
              />
              {proofFile ? (
                <div className="proof-preview">
                  {proofPreview ? (
                    <img src={proofPreview} alt="Proof preview" className="proof-thumb" />
                  ) : (
                    <div className="proof-pdf-badge">📄 PDF</div>
                  )}
                  <div className="proof-file-info">
                    <span className="proof-file-name">{proofFile.name}</span>
                    <span className="proof-file-size">{(proofFile.size / 1024).toFixed(1)} KB</span>
                  </div>
                  <button type="button" className="btn btn-sm btn-danger" onClick={(e) => { e.stopPropagation(); removeProof(); }}>Remove</button>
                </div>
              ) : (
                <div className="proof-placeholder">
                  <span className="proof-icon">📎</span>
                  <span>Click to upload bill / voucher proof</span>
                  <span className="proof-hint">Accepts images and PDF (Max size: 10 MB)</span>
                </div>
              )}
            </div>
            {fileError && <div style={{ color: 'red', fontSize: '13px', marginTop: '6px' }}>{fileError}</div>}
          </div>
        </div>

        <div className="action-row">
          <button className="btn btn-brass" type="submit" disabled={saving}>{saving ? 'Saving\u2026' : 'Save as Draft'}</button>
          <button className="btn btn-outline" type="button" onClick={() => navigate(-1)}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
