import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useRole } from "../context/RoleContext";
import complaintService from "../api/complaintService";
import categoryService from "../api/categoryService";

function ComplaintForm() {
  const { userId, role } = useRole();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    description: "",
    residentId: role === "RESIDENT" ? userId : "",
    categoryId: "",
  });
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    categoryService.getAll().then((res) => setCategories(res.data));
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await complaintService.create({
        description: formData.description,
        residentId: Number(formData.residentId),
        categoryId: Number(formData.categoryId),
      });
      navigate(role === "RESIDENT" ? "/resident" : "/complaints");
    } catch (err) {
      alert("Save failed: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ maxWidth: 400, margin: "30px auto" }}>
      <h2>Raise Complaint</h2>

      <label>Description:</label>
      <textarea name="description" value={formData.description} onChange={handleChange} required />

      <label>Category:</label>
      <select name="categoryId" value={formData.categoryId} onChange={handleChange} required>
        <option value="">-- Select --</option>
        {categories.map((c) => (
          <option key={c.categoryId} value={c.categoryId}>{c.categoryName}</option>
        ))}
      </select>

      <button type="submit">Submit</button>
    </form>
  );
}

export default ComplaintForm;