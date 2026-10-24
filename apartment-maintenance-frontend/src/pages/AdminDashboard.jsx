import { useNavigate } from "react-router-dom";
import { useRole } from "../context/RoleContext";
import { useEffect } from "react";

function AdminDashboard() {
  const { role } = useRole();
  const navigate = useNavigate();

  useEffect(() => {
    if (!role) navigate("/");
  }, [role]);

  return (
    <div>
      <h2>Admin Dashboard</h2>
      <div style={{ display: "flex", gap: 20 }}>
        <button onClick={() => navigate("/complaints")}>Manage All Complaints</button>
        <button onClick={() => navigate("/residents")}>Manage Residents</button>
        <button onClick={() => navigate("/staff-management")}>Manage Staff</button>
      </div>
    </div>
  );
}

export default AdminDashboard;