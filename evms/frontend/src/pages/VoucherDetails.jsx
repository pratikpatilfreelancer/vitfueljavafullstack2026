import { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import client, { FILE_ORIGIN } from '../api/client';
import { useAuth } from '../context/AuthContext';
import StatusStamp from '../components/StatusStamp';
import SignatureCanvas from 'react-signature-canvas';

// We will use native fetch(dataUrl).blob() instead of manual base64 decoding

export default function VoucherDetails({ backPath }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [voucher, setVoucher] = useState(null);
  const [error, setError] = useState('');
  const [fileError, setFileError] = useState('');
  const [busy, setBusy] = useState(false);
  const [rejectOpen, setRejectOpen] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const sigCanvas = useRef(null);
  const [signatureModalOpen, setSignatureModalOpen] = useState(false);
  const [savedSignature, setSavedSignature] = useState(null);
  const [savedSignaturePreview, setSavedSignaturePreview] = useState(null);

  function load() {
    client.get(`/vouchers/${id}`)
      .then((res) => setVoucher(res.data))
      .catch((err) => setError(err.response?.data?.error || 'Could not load voucher.'));
  }

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [id]);

  if (error) return <div className="error-banner">{error}</div>;
  if (!voucher) return <div className="loading-state">Loading voucher\u2026</div>;

  const isOwner = user.role === 'EMPLOYEE' && voucher.employee_id === user.id;

  async function handleSubmit() {
    if (!savedSignature) {
      setError('Please provide a signature before submitting.');
      return;
    }
    setBusy(true);
    setError('');
    const fd = new FormData();
    fd.append('signature', savedSignature, 'signature.png');
    try {
      const { data } = await client.post(`/vouchers/${id}/submit`, fd, { headers: { 'Content-Type': 'multipart/form-data' } });
      setVoucher(data);
    } catch (err) {
      setError(err.response?.data?.error || 'Could not submit voucher.');
    } finally {
      setBusy(false);
    }
  }

  async function handleApprove() {
    if (!savedSignature) {
      setError('Please provide a signature before approving.');
      return;
    }
    setBusy(true);
    setError('');
    const fd = new FormData();
    fd.append('signature', savedSignature, 'signature.png');
    try {
      const { data } = await client.post(`/vouchers/${id}/approve`, fd, { headers: { 'Content-Type': 'multipart/form-data' } });
      setVoucher(data);
    } catch (err) {
      setError(err.response?.data?.error || 'Could not approve voucher.');
    } finally {
      setBusy(false);
    }
  }

  async function handleProceedDrawn() {
    if (!sigCanvas.current || sigCanvas.current.isEmpty()) {
      alert('Please draw your signature before proceeding.');
      return;
    }
    try {
      const dataUrl = sigCanvas.current.getCanvas().toDataURL('image/png');
      const res = await fetch(dataUrl);
      const blob = await res.blob();
      setSavedSignature(blob);
      setSavedSignaturePreview(dataUrl);
      setSignatureModalOpen(false);
    } catch (err) {
      alert('Error saving signature: ' + err.message);
    }
  }

  function handleUploadFile(e) {
    const file = e.target.files[0];
    if (!file) return;
    if (file.size > 10 * 1024 * 1024) {
      setFileError(`File size (${(file.size / 1024 / 1024).toFixed(1)} MB) exceeds the maximum allowed size of 10 MB.`);
      e.target.value = '';
      return;
    }
    setFileError('');
    setError('');
    setSavedSignature(file);
    setSavedSignaturePreview(URL.createObjectURL(file));
    setSignatureModalOpen(false);
  }

  const renderSignatureSection = () => (
    <div className="field-group" style={{ maxWidth: 400 }}>
      {savedSignaturePreview ? (
        <div style={{ marginBottom: '12px' }}>
          <label className="field-label">Your Signature *</label>
          <img src={savedSignaturePreview} alt="Signature preview" style={{ height: '60px', borderBottom: '1px solid var(--ink)', padding: '4px 20px', background: '#fffdf9', objectFit: 'contain' }} />
          <div style={{ marginTop: '10px' }}>
            <button type="button" onClick={() => setSignatureModalOpen(true)} className="btn btn-sm btn-outline">Change Signature</button>
          </div>
        </div>
      ) : (
        <button type="button" onClick={() => setSignatureModalOpen(true)} className="btn btn-outline" style={{ marginBottom: '16px' }}>
          Add Signature
        </button>
      )}
    </div>
  );

  async function handleDelete() {
    if (!window.confirm('Delete this draft voucher? This cannot be undone.')) return;
    setBusy(true);
    try {
      await client.delete(`/vouchers/${id}`);
      navigate(backPath);
    } catch (err) {
      setError(err.response?.data?.error || 'Could not delete voucher.');
      setBusy(false);
    }
  }

  async function handleReject() {
    if (!rejectReason.trim()) return;
    setBusy(true);
    setError('');
    try {
      const { data } = await client.post(`/vouchers/${id}/reject`, { rejectionReason: rejectReason });
      setVoucher(data);
      setRejectOpen(false);
    } catch (err) {
      setError(err.response?.data?.error || 'Could not reject voucher.');
    } finally {
      setBusy(false);
    }
  }

  return (
    <div>
      <Link to={backPath} className="link-back">&larr; Back to list</Link>

      {error && <div className="error-banner">{error}</div>}

      <div className="voucher-paper">
        <div className="voucher-top-row">
          <div>
            <div className="voucher-label">Voucher Number</div>
            <div className="voucher-number-big">{voucher.voucher_number}</div>
          </div>
          <StatusStamp status={voucher.status} />
        </div>

        <div className="field-grid">
          <Field label="Employee" value={voucher.employee_name} />
          <Field label="Employee ID" value={voucher.employee_code || '\u2014'} />
          <Field label="Department" value={voucher.department} />
          <Field label="Voucher Date" value={voucher.voucher_date} />
          <Field label="Expense Date" value={voucher.expense_date} />
          <Field label="Expense Category" value={voucher.expense_category} />
          <Field label="Expense Title" value={voucher.expense_title} />
          <Field label="Amount" value={`\u20B9${voucher.amount.toLocaleString('en-IN')}`} big />
        </div>

        {voucher.expense_description && (
          <>
            <div className="voucher-label">Description</div>
            <p style={{ marginTop: 6 }}>{voucher.expense_description}</p>
          </>
        )}

        {/* Proof Document Section */}
        {voucher.proofDocumentUrl && (
          <>
            <hr className="section-divider" />
            <div className="voucher-label" style={{ marginBottom: 8 }}>Proof Document (Bill / Voucher)</div>
            <div className="proof-display">
              {voucher.proof_document && voucher.proof_document.toLowerCase().endsWith('.pdf') ? (
                <a
                  href={`${FILE_ORIGIN}${voucher.proofDocumentUrl}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="proof-display-link"
                >
                  <span className="proof-pdf-badge">📄 PDF</span>
                  <span>{voucher.proof_document}</span>
                  <span className="proof-view-text">Click to view / download</span>
                </a>
              ) : (
                <a
                  href={`${FILE_ORIGIN}${voucher.proofDocumentUrl}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="proof-display-link"
                >
                  <img
                    src={`${FILE_ORIGIN}${voucher.proofDocumentUrl}`}
                    alt="Proof document"
                    className="proof-display-img"
                  />
                  <span className="proof-view-text">Click to view full size</span>
                </a>
              )}
            </div>
          </>
        )}

        <hr className="section-divider" />

        <div className="signature-row">
          <div className="signature-block">
            <div className="voucher-label" style={{ marginBottom: 6 }}>Employee Signature</div>
            {voucher.employeeSignatureUrl ? (
              <img className="signature-img" src={`${FILE_ORIGIN}${voucher.employeeSignatureUrl}`} alt="Employee signature" />
            ) : (
              <div className="signature-placeholder">Not yet signed</div>
            )}
          </div>
          <div className="signature-block">
            <div className="voucher-label" style={{ marginBottom: 6 }}>Director Signature</div>
            {voucher.directorSignatureUrl ? (
              <img className="signature-img" src={`${FILE_ORIGIN}${voucher.directorSignatureUrl}`} alt="Director signature" />
            ) : (
              <div className="signature-placeholder">Not yet signed</div>
            )}
          </div>
          {voucher.approval_date && (
            <div className="signature-block">
              <div className="voucher-label" style={{ marginBottom: 6 }}>Approval Date</div>
              <div style={{ paddingTop: 18 }}>{new Date(voucher.approval_date).toLocaleDateString()}</div>
            </div>
          )}
        </div>

        {voucher.status === 'REJECTED' && voucher.rejection_reason && (
          <div className="rejection-note"><strong>Rejection reason:</strong> {voucher.rejection_reason}</div>
        )}

        {isOwner && voucher.status === 'DRAFT' && (
          <>
            <hr className="section-divider" />
            {renderSignatureSection()}
            <div className="action-row">
              <button className="btn btn-brass" disabled={busy || !savedSignature} onClick={handleSubmit}>Submit for Approval</button>
              <Link to={`${backPath}/${voucher.id}/edit`} className="btn btn-outline">Edit Draft</Link>
              <button className="btn btn-danger" disabled={busy} onClick={handleDelete}>Delete Draft</button>
            </div>
          </>
        )}

        {user.role === 'DIRECTOR' && voucher.status === 'SUBMITTED' && (
          <>
            <hr className="section-divider" />
            {renderSignatureSection()}
            <div className="action-row">
              <button className="btn btn-brass" disabled={busy || !savedSignature} onClick={handleApprove}>Approve</button>
              <button className="btn btn-danger" disabled={busy} onClick={() => setRejectOpen(true)}>Reject</button>
            </div>
          </>
        )}

        {/* Accounts print action ------------------------------------------*/}
        {user.role === 'ACCOUNTS' && (
          <>
            <hr className="section-divider" />
            <div className="action-row">
              <button
                className="btn btn-brass"
                onClick={() => window.print()}
              >
                🖨️ Print Voucher
              </button>
            </div>
          </>
        )}
      </div>

      {signatureModalOpen && (
        <div className="modal-backdrop" onClick={() => setSignatureModalOpen(false)}>
          <div className="modal-card" style={{ width: 'auto', minWidth: '500px' }} onClick={(e) => e.stopPropagation()}>
            <h3 style={{ marginTop: 0 }}>Provide Signature</h3>
            
            <div style={{ display: 'flex', gap: '24px', alignItems: 'center', marginTop: '20px' }}>
              <div>
                <label className="field-label">Draw Signature</label>
                <div style={{ border: '1px solid #c9bfa4', borderRadius: '4px', background: '#fffdf9', overflow: 'hidden' }}>
                  <SignatureCanvas 
                    ref={sigCanvas} 
                    canvasProps={{ width: 350, height: 150, className: 'sigCanvas' }} 
                    backgroundColor="transparent"
                  />
                  <div style={{ textAlign: 'right', padding: '6px', borderTop: '1px dashed #e3d9c3' }}>
                    <button type="button" onClick={() => sigCanvas.current.clear()} className="btn btn-sm btn-outline">Clear</button>
                    <button type="button" onClick={handleProceedDrawn} className="btn btn-sm btn-brass" style={{ marginLeft: '8px' }}>Save Signature</button>
                  </div>
                </div>
              </div>
              
              <div style={{ color: 'var(--slate)', fontWeight: 'bold', fontSize: '14px' }}>OR</div>
              
              <div>
                <label className="field-label">Upload Image File</label>
                <input type="file" accept="image/*" className="field-input" onChange={handleUploadFile} />
                <div style={{ fontSize: '11px', color: 'var(--slate-light)', marginTop: '4px' }}>Max size: 10 MB</div>
                {fileError && <div style={{ color: 'red', fontSize: '13px', marginTop: '6px' }}>{fileError}</div>}
              </div>
            </div>
            
            <div className="action-row" style={{ marginTop: '24px' }}>
              <button className="btn btn-outline" onClick={() => setSignatureModalOpen(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      {rejectOpen && (
        <div className="modal-backdrop" onClick={() => setRejectOpen(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <h3 style={{ marginTop: 0 }}>Reject Voucher</h3>
            <div className="field-group">
              <label className="field-label">Rejection reason *</label>
              <textarea
                className="field-textarea"
                rows={3}
                value={rejectReason}
                onChange={(e) => setRejectReason(e.target.value)}
                autoFocus
              />
            </div>
            <div className="action-row">
              <button className="btn btn-danger" disabled={busy || !rejectReason.trim()} onClick={handleReject}>Confirm Reject</button>
              <button className="btn btn-outline" onClick={() => setRejectOpen(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function Field({ label, value, big }) {
  return (
    <div className="field-block">
      <div className="voucher-label">{label}</div>
      <div className={`voucher-value${big ? ' amount' : ''}`}>{value}</div>
    </div>
  );
}
