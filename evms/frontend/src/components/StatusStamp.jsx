const LABELS = {
  DRAFT: 'Draft',
  SUBMITTED: 'Pending Approval',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
};

const CLASS = {
  DRAFT: 'stamp-draft',
  SUBMITTED: 'stamp-submitted',
  APPROVED: 'stamp-approved',
  REJECTED: 'stamp-rejected',
};

export default function StatusStamp({ status }) {
  return <span className={`stamp ${CLASS[status] || 'stamp-draft'}`}>{LABELS[status] || status}</span>;
}
