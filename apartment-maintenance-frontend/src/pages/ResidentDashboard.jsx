import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useRole } from "../context/RoleContext";
import complaintService from "../api/complaintService";

function ResidentDashboard() {
  const { userId, userName, role } = useRole();
  const navigate = useNavigate();
  const [complaints, setComplaints] = useState([]);

  useEffect(() => {
    if (!role) { navigate("/"); return; }
    fetchMyComplaints();
  }, [role]);

  const fetchMyComplaints = async () => {
    const res = await complaintService.getByResident(userId);
    setComplaints(res.data);
  };

  return (
    <div>
      <h2>Welcome, {userName}</h2>
      <button onClick={() => navigate("/complaints/new")}>+ Raise New Complaint</button>

      <h3>My Complaint History</h3>
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>ID</th><th>Description</th><th>Category</th><th>Status</th><th>Raised On</th></tr>
        </thead>
        <tbody>
          {complaints.map((c) => (
            <tr key={c.complaintId}>
              <td>{c.complaintId}</td>
              <td>{c.description}</td>
              <td>{c.categoryName}</td>
              <td>{c.status}</td>
              <td>{c.createdDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ResidentDashboard;