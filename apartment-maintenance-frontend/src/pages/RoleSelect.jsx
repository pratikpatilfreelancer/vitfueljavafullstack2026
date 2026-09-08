import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useRole } from "../context/RoleContext";
import residentService from "../api/residentService";
import staffService from "../api/staffService";

function RoleSelect() {
  const [selectedRole, setSelectedRole] = useState("");
  const [residents, setResidents] = useState([]);
  const [staff, setStaff] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState("");
  const { login } = useRole();
  const navigate = useNavigate();

  useEffect(() => {
    residentService.getAll().then((res) => setResidents(res.data));
    staffService.getAll().then((res) => setStaff(res.data));
  }, []);

  const handleContinue = () => {
    if (selectedRole === "ADMIN") {
      login("ADMIN", null, "Admin");
      navigate("/admin");
      return;
    }
    if (selectedRole === "RESIDENT") {
      const resident = residents.find((r) => r.residentId === Number(selectedUserId));
      login("RESIDENT", resident.residentId, resident.name);
      navigate("/resident");
      return;
    }
    if (selectedRole === "STAFF") {
      const staffMember = staff.find((s) => s.staffId === Number(selectedUserId));
      login("STAFF", staffMember.staffId, staffMember.name);
      navigate("/staff");
      return;
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: "60px auto", textAlign: "center" }}>
      <h1>Apartment Maintenance System</h1>
      <p>Select your role to continue</p>

      <select value={selectedRole} onChange={(e) => { setSelectedRole(e.target.value); setSelectedUserId(""); }}>
        <option value="">-- Select Role --</option>
        <option value="RESIDENT">Resident</option>
        <option value="STAFF">Staff</option>
        <option value="ADMIN">Admin</option>
      </select>

      {selectedRole === "RESIDENT" && (
        <div style={{ marginTop: 15 }}>
          <select value={selectedUserId} onChange={(e) => setSelectedUserId(e.target.value)}>
            <option value="">-- Select Resident --</option>
            {residents.map((r) => (
              <option key={r.residentId} value={r.residentId}>{r.name} (Flat {r.flatNo})</option>
            ))}
          </select>
        </div>
      )}

      {selectedRole === "STAFF" && (
        <div style={{ marginTop: 15 }}>
          <select value={selectedUserId} onChange={(e) => setSelectedUserId(e.target.value)}>
            <option value="">-- Select Staff Member --</option>
            {staff.map((s) => (
              <option key={s.staffId} value={s.staffId}>{s.name} ({s.specialization})</option>
            ))}
          </select>
        </div>
      )}

      <div style={{ marginTop: 20 }}>
        <button
          onClick={handleContinue}
          disabled={!selectedRole || (selectedRole !== "ADMIN" && !selectedUserId)}
        >
          Continue
        </button>
      </div>
    </div>
  );
}

export default RoleSelect;