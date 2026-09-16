import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

function ManageAllDonations() {
    const navigate = useNavigate();
    const [donations, setDonations] = useState([]);

    useEffect(() => {
        loadDonations();
    }, []);

    const loadDonations = async () => {
        try {
            const response = await api.get("/donations");
            setDonations(response.data);
        } catch (error) {
            console.error(error);
            alert("Failed to load donations");
        }
    };

    const deleteDonation = async (id) => {
        if (!window.confirm("Are you sure you want to delete this donation?")) {
            return;
        }

        try {
            await api.delete(`/donations/${id}`);

            alert("Donation deleted successfully");

            loadDonations();
        } catch (error) {
            alert(
                error.response?.data?.error ||
                "Failed to delete donation"
            );
        }
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

            <h1>Manage All Donations</h1>
            <p>View and manage all food donations in the system.</p>

            {donations.length === 0 ? (
                <div className="card">
                    <h3>No donations found</h3>
                </div>
            ) : (
                donations.map((donation) => (
                    <div className="card donation-card" key={donation.id}>

                        <div className="request-header">
                            <h2>{donation.foodName}</h2>

                            <span className={`status-badge ${donation.status?.toLowerCase()}`}>
                                {donation.status}
                            </span>
                        </div>

                        <p>
                            <strong>Description:</strong>{" "}
                            {donation.description || "N/A"}
                        </p>

                        <p>
                            <strong>Quantity:</strong>{" "}
                            {donation.quantity}
                        </p>

                        <p>
                            <strong>Category:</strong>{" "}
                            {donation.category?.name || "N/A"}
                        </p>

                        <p>
                            <strong>Donor:</strong>{" "}
                            {donation.donor?.name || "N/A"}
                        </p>

                        <p>
                            <strong>Location:</strong>{" "}
                            {donation.location || "N/A"}
                        </p>

                        <p>
                            <strong>Expiry Date:</strong>{" "}
                            {donation.expiryDate || "N/A"}
                        </p>

                        <button
                            className="danger-button"
                            onClick={() => deleteDonation(donation.id)}
                        >
                            Delete Donation
                        </button>

                    </div>
                ))
            )}

        </div>
    );
}

export default ManageAllDonations;