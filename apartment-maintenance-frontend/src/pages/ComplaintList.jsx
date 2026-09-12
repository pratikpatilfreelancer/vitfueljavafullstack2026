import { useEffect, useState } from "react";
import complaintService from "../api/complaintService";
import staffService from "../api/staffService";

function ComplaintList() {
  const [complaints, setComplaints] = useState([]);
  const [staffOptions, setStaffOptions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchComplaints();
    staffService.getAll().then((res) => setStaffOptions(res.data));
  }, []);

  const fetchComplaints = async () => {
    const res = await complaintService.getAll();
    setComplaints(res.data);
    setLoading(false);
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this complaint?")) return;
    await complaintService.delete(id);
    fetchComplaints();
  };

  const handleAssign = async (complaintId, staffId) => {
    if (!staffId) return;
    await complaintService.assignStaff(complaintId, staffId);
    fetchComplaints();
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div>
      <h2>All Complaints (Admin)</h2>
      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>ID</th><th>Description</th><th>Resident</th><th>Category</th>
            <th>Status</th><th>Assigned Staff</th><th>Assign</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {complaints.map((c) => (
            <tr key={c.complaintId}>
              <td>{c.complaintId}</td>
              <td>{c.description}</td>
              <td>{c.residentName}</td>
              <td>{c.categoryName}</td>
              <td>{c.status}</td>
              <td>{c.staffName || "Unassigned"}</td>
              <td>
                <select defaultValue="" onChange={(e) => handleAssign(c.complaintId, e.target.value)}>
                  <option value="">-- Assign --</option>
                  {staffOptions.map((s) => (
                    <option key={s.staffId} value={s.staffId}>{s.name}</option>
                  ))}
                </select>
              </td>
              <td>
                <button onClick={() => handleDelete(c.complaintId)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ComplaintList;