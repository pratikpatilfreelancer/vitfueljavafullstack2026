import { useEffect, useState, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import client, { FILE_ORIGIN } from '../../api/client';

const CATEGORIES = ['Travel', 'Food & Entertainment', 'Office Supplies', 'Accommodation', 'Client Meeting', 'Other'];
const ACCEPTED_PROOF_TYPES = 'image/png,image/jpeg,image/jpg,image/gif,image/webp,application/pdf';
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

export default function EditVoucher() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(null);
  const [existingProofUrl, setExistingProofUrl] = useState(null);
  const [existingProofName, setExistingProofName] = useState(null);
  const [proofFile, setProofFile] = useState(null);
  const [proofPreview, setProofPreview] = useState(null);
  const fileInputRef = useRef(null);
  const [error, setError] = useState('');
  const [fileError, setFileError] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    client.get(`/vouchers/${id}`).then((res) => {
      const v = res.data;
      setForm({
        department: v.department,
        employeeCode: v.employee_code || '',
        expenseDate: v.expense_date,
        expenseTitle: v.expense_title,
        expenseCategory: v.expense_category,
        expenseDescription: v.expense_description || '',
        amount: v.amount,
      });
      if (v.proofDocumentUrl) {
        setExistingProofUrl(v.proofDocumentUrl);
        setExistingProofName(v.proof_document);
      }
    }).catch((err) => setError(err.response?.data?.error || 'Could not load voucher.'));
  }, [id]);

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
      setProofPreview(null);
    } else {
      setProofPreview(URL.createObjectURL(file));
    }
  }

  function removeNewProof() {
    setProofFile(null);
    setProofPreview(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
  }

  function isPdf(filename) {
    return filename && filename.toLowerCase().endsWith('.pdf');
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      const fd = new FormData();
      fd.append('data', new Blob([JSON.stringify(form)], { type: 'application/json' }));
      if (proofFile) {
        fd.append('proofDocument', proofFile);
      }
      await client.put(`/vouchers/${id}`, fd, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      navigate(`/employee/vouchers/${id}`);
    } catch (err) {
      setError(err.response?.data?.error || 'Could not update voucher.');
    } finally {
      setSaving(false);
    }
  }

  // Calculate the minimum allowed expense date (7 days ago)
  const minDateObj = new Date();
  minDateObj.setDate(minDateObj.getDate() - 7);
  const minDateStr = minDateObj.toISOString().split('T')[0];

  if (error && !form) return <div className="error-banner">{error}</div>;
  if (!form) return <div className="loading-state">Loading&hellip;</div>;

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Edit Draft Voucher</h1>
        <p className="page-subtitle">Only Draft vouchers can be edited.</p>
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

          {/* Proof Document Upload / Replace */}
          <div className="field-group span-2">
            <label className="field-label">Proof Document (Bill / Voucher) *</label>

            {/* Show new file if selected */}
            {proofFile ? (
              <div className="proof-upload-area has-file">
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
                  <button type="button" className="btn btn-sm btn-danger" onClick={removeNewProof}>Remove</button>
                </div>
              </div>
            ) : existingProofUrl ? (
              /* Show existing proof with replace option */
              <div className="proof-upload-area has-file">
                <div className="proof-preview">
                  {isPdf(existingProofName) ? (
                    <div className="proof-pdf-badge">📄 PDF</div>
                  ) : (
                    <img src={`${FILE_ORIGIN}${existingProofUrl}`} alt="Existing proof" className="proof-thumb" />
                  )}
                  <div className="proof-file-info">
                    <span className="proof-file-name">{existingProofName}</span>
                    <span className="proof-file-size" style={{ color: 'var(--stamp-green)' }}>Currently attached</span>
                  </div>
                  <button type="button" className="btn btn-sm btn-outline" onClick={() => fileInputRef.current?.click()}>Replace</button>
                </div>
              </div>
            ) : (
              /* No proof — shouldn't normally happen since it's mandatory on create */
              <div className="proof-upload-area" onClick={() => fileInputRef.current?.click()}>
                <div className="proof-placeholder">
                  <span className="proof-icon">📎</span>
                  <span>Click to upload bill / voucher proof</span>
                  <span className="proof-hint">Accepts images and PDF (Max size: 10 MB)</span>
                </div>
              </div>
            )}
            <input
              ref={fileInputRef}
              type="file"
              accept={ACCEPTED_PROOF_TYPES}
              onChange={handleProofSelect}
              style={{ display: 'none' }}
            />
            {fileError && <div style={{ color: 'red', fontSize: '13px', marginTop: '6px' }}>{fileError}</div>}
          </div>
        </div>

        <div className="action-row">
          <button className="btn btn-brass" type="submit" disabled={saving}>{saving ? 'Saving\u2026' : 'Save Changes'}</button>
          <button className="btn btn-outline" type="button" onClick={() => navigate(-1)}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
