import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useRole } from "../context/RoleContext";
import complaintService from "../api/complaintService";

function StaffDashboard() {
  const { userId, userName, role } = useRole();
  const navigate = useNavigate();
  const [complaints, setComplaints] = useState([]);

  useEffect(() => {
    if (!role) { navigate("/"); return; }
    fetchAssigned();
  }, [role]);

  const fetchAssigned = async () => {
    const res = await complaintService.getByStaff(userId);
    setComplaints(res.data);
  };

  const handleStatusChange = async (complaintId, newStatus) => {
    await complaintService.updateStatus(complaintId, newStatus);
    fetchAssigned();
  };

  return (
    <div>
      <h2>Welcome, {userName}</h2>
      <h3>Complaints Assigned to Me</h3>
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>ID</th><th>Description</th><th>Resident</th><th>Status</th><th>Update</th></tr>
        </thead>
        <tbody>
          {complaints.map((c) => (
            <tr key={c.complaintId}>
              <td>{c.complaintId}</td>
              <td>{c.description}</td>
              <td>{c.residentName}</td>
              <td>{c.status}</td>
              <td>
                <select value={c.status} onChange={(e) => handleStatusChange(c.complaintId, e.target.value)}>
                  <option value="OPEN">OPEN</option>
                  <option value="IN_PROGRESS">IN_PROGRESS</option>
                  <option value="RESOLVED">RESOLVED</option>
                  <option value="CLOSED">CLOSED</option>
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default StaffDashboard;