import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

function ManageDonations() {
    const navigate = useNavigate();

    const user = JSON.parse(localStorage.getItem("user"));

    const [donations, setDonations] = useState([]);
    const [categories, setCategories] = useState([]);

    const [form, setForm] = useState({
        foodName: "",
        description: "",
        quantity: "",
        expiryDate: "",
        location: "",
        categoryId: ""
    });

    const [editingId, setEditingId] = useState(null);

    useEffect(() => {
        if (!user) {
            navigate("/login");
            return;
        }

        loadDonations();
        loadCategories();
    }, []);

    const loadDonations = async () => {
        try {
            const response = await api.get(`/donations/donor/${user.id}`);
            setDonations(response.data);
        } catch (error) {
            console.error(error);
            alert("Failed to load donations");
        }
    };

    const loadCategories = async () => {
        try {
            const response = await api.get("/categories");
            setCategories(response.data);
        } catch (error) {
            console.error(error);
            alert("Failed to load categories");
        }
    };

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const donationData = {
                foodName: form.foodName,
                description: form.description,
                quantity: Number(form.quantity),
                expiryDate: form.expiryDate,
                location: form.location,
                status: editingId ? form.status : "AVAILABLE",
                donor: {
                    id: user.id
                },
                category: {
                    id: Number(form.categoryId)
                }
            };

            if (editingId) {
                await api.put(`/donations/${editingId}`, donationData);
                alert("Donation updated successfully");
            } else {
                await api.post("/donations", donationData);
                alert("Donation added successfully");
            }

            resetForm();
            loadDonations();

        } catch (error) {
            console.error(error);

            if (error.response?.data) {
                alert(JSON.stringify(error.response.data));
            } else {
                alert("Something went wrong");
            }
        }
    };

    const handleEdit = (donation) => {
        setEditingId(donation.id);

        setForm({
            foodName: donation.foodName,
            description: donation.description || "",
            quantity: donation.quantity,
            expiryDate: donation.expiryDate || "",
            location: donation.location || "",
            categoryId: donation.category?.id || "",
            status: donation.status || "AVAILABLE"
        });

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    };

    const handleDelete = async (id) => {
        const confirmDelete = window.confirm(
            "Are you sure you want to delete this donation?"
        );

        if (!confirmDelete) {
            return;
        }

        try {
            await api.delete(`/donations/${id}`);

            alert("Donation deleted successfully");

            loadDonations();
        } catch (error) {
            console.error(error);
            alert("Failed to delete donation");
        }
    };

    const resetForm = () => {
        setEditingId(null);

        setForm({
            foodName: "",
            description: "",
            quantity: "",
            expiryDate: "",
            location: "",
            categoryId: "",
            status: "AVAILABLE"
        });
    };

    return (
        <div className="page-container">

            <div className="navbar">
                <h2>Food Waste Management</h2>

                <button
                    className="secondary-button"
                    onClick={() => navigate("/dashboard")}
                >
                    Dashboard
                </button>
            </div>

            <div className="card">

                <h1>
                    {editingId ? "Edit Food Donation" : "Add Food Donation"}
                </h1>

                <form onSubmit={handleSubmit}>

                    <div className="form-group">
                        <label>Food Name</label>
                        <input
                            type="text"
                            name="foodName"
                            value={form.foodName}
                            onChange={handleChange}
                            placeholder="Enter food name"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Description</label>
                        <textarea
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            placeholder="Enter description"
                        />
                    </div>

                    <div className="form-group">
                        <label>Quantity</label>
                        <input
                            type="number"
                            name="quantity"
                            value={form.quantity}
                            onChange={handleChange}
                            min="1"
                            placeholder="Enter quantity"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Expiry Date</label>
                        <input
                            type="date"
                            name="expiryDate"
                            value={form.expiryDate}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label>Location</label>
                        <input
                            type="text"
                            name="location"
                            value={form.location}
                            onChange={handleChange}
                            placeholder="Enter location"
                        />
                    </div>

                    <div className="form-group">
                        <label>Category</label>

                        <select
                            name="categoryId"
                            value={form.categoryId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Select Category</option>

                            {categories.map((category) => (
                                <option
                                    key={category.id}
                                    value={category.id}
                                >
                                    {category.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    {editingId && (
                        <div className="form-group">
                            <label>Status</label>

                            <select
                                name="status"
                                value={form.status}
                                onChange={handleChange}
                            >
                                <option value="AVAILABLE">AVAILABLE</option>
                                <option value="CLAIMED">CLAIMED</option>
                                <option value="COMPLETED">COMPLETED</option>
                            </select>
                        </div>
                    )}

                    <button
                        type="submit"
                        className="primary-button"
                    >
                        {editingId ? "Update Donation" : "Add Donation"}
                    </button>

                    {editingId && (
                        <button
                            type="button"
                            className="secondary-button"
                            onClick={resetForm}
                            style={{ marginLeft: "10px" }}
                        >
                            Cancel Edit
                        </button>
                    )}

                </form>
            </div>

            <hr />

            <h2>My Donations</h2>

            {donations.length === 0 ? (
                <p>No donations found.</p>
            ) : (
                donations.map((donation) => (
                    <div className="card" key={donation.id}>

                        <h3>{donation.foodName}</h3>

                        <p>
                            <strong>Description:</strong>{" "}
                            {donation.description || "N/A"}
                        </p>

                        <p>
                            <strong>Quantity:</strong>{" "}
                            {donation.quantity}
                        </p>

                        <p>
                            <strong>Expiry Date:</strong>{" "}
                            {donation.expiryDate || "N/A"}
                        </p>

                        <p>
                            <strong>Location:</strong>{" "}
                            {donation.location || "N/A"}
                        </p>

                        <p>
                            <strong>Category:</strong>{" "}
                            {donation.category?.name || "N/A"}
                        </p>

                        <p>
                            <strong>Status:</strong>{" "}
                            {donation.status}
                        </p>

                        <button
                            className="warning-button"
                            onClick={() => handleEdit(donation)}
                        >
                            Edit
                        </button>

                        <button
                            className="danger-button"
                            onClick={() => handleDelete(donation.id)}
                            style={{ marginLeft: "10px" }}
                        >
                            Delete
                        </button>

                    </div>
                ))
            )}

        </div>
    );
}

export default ManageDonations;