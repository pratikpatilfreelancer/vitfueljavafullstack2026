import { Link, useNavigate } from "react-router-dom";
import { useRole } from "../context/RoleContext";

function Navbar() {
  const { role, userName, logout } = useRole();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  if (!role) return null;

  return (
    <nav style={{ padding: "10px", background: "#333", color: "white" }}>
      <span style={{ marginRight: 20 }}>Logged in as: {userName} ({role})</span>

      {role === "RESIDENT" && <Link to="/resident" style={{ color: "white", marginRight: 15 }}>My Dashboard</Link>}
      {role === "STAFF" && <Link to="/staff" style={{ color: "white", marginRight: 15 }}>My Dashboard</Link>}
      {role === "ADMIN" && (
        <>
          <Link to="/admin" style={{ color: "white", marginRight: 15 }}>Dashboard</Link>
          <Link to="/complaints" style={{ color: "white", marginRight: 15 }}>Complaints</Link>
          <Link to="/residents" style={{ color: "white", marginRight: 15 }}>Residents</Link>
          <Link to="/staff-management" style={{ color: "white", marginRight: 15 }}>Staff</Link>
        </>
      )}

      <button onClick={handleLogout} style={{ marginLeft: 20 }}>Switch Role / Logout</button>
    </nav>
  );
}

export default Navbar;