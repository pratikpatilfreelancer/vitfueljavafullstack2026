import { useEffect, useState } from "react";
import residentService from "../api/residentService";

function ResidentList() {
  const [residents, setResidents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchResidents();
  }, []);

  const fetchResidents = async () => {
    const res = await residentService.getAll();
    setResidents(res.data);
    setLoading(false);
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this resident?")) return;
    await residentService.delete(id);
    fetchResidents();
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div>
      <h2>Residents (Admin)</h2>
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Flat No</th><th>Building</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {residents.map((r) => (
            <tr key={r.residentId}>
              <td>{r.residentId}</td>
              <td>{r.name}</td>
              <td>{r.email}</td>
              <td>{r.phone}</td>
              <td>{r.flatNo}</td>
              <td>{r.buildingName}</td>
              <td><button onClick={() => handleDelete(r.residentId)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ResidentList;