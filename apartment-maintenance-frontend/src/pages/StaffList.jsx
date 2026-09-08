import { useEffect, useState } from "react";
import staffService from "../api/staffService";

function StaffList() {
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStaff();
  }, []);

  const fetchStaff = async () => {
    const res = await staffService.getAll();
    setStaff(res.data);
    setLoading(false);
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this staff member?")) return;
    await staffService.delete(id);
    fetchStaff();
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div>
      <h2>Staff (Admin)</h2>
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>ID</th><th>Name</th><th>Designation</th><th>Specialization</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {staff.map((s) => (
            <tr key={s.staffId}>
              <td>{s.staffId}</td>
              <td>{s.name}</td>
              <td>{s.designation}</td>
              <td>{s.specialization}</td>
              <td><button onClick={() => handleDelete(s.staffId)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default StaffList;