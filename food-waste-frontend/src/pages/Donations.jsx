import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

function Donations() {
    const navigate = useNavigate();

    const user = JSON.parse(localStorage.getItem("user"));

    const [donations, setDonations] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) {
            navigate("/login");
            return;
        }

        loadDonations();
    }, []);

    const loadDonations = async () => {
        try {
            const response = await api.get("/donations/available");
            setDonations(response.data);
        } catch (error) {
            console.error(error);
            alert("Failed to load available food.");
        } finally {
            setLoading(false);
        }
    };

    const handleRequest = async (donationId) => {
        try {
            await api.post(
                `/requests?donationId=${donationId}&userId=${user.id}`
            );

            alert("Food requested successfully!");

            navigate("/requests");

        } catch (error) {
            console.error(error);

            const message =
                error.response?.data?.error ||
                "Unable to request this food.";

            alert(message);
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

            <h1>Available Food</h1>

            <p>
                Browse food donations available for collection.
            </p>

            {loading ? (
                <p>Loading available food...</p>
            ) : donations.length === 0 ? (
                <div className="card">
                    <h3>No food available</h3>
                    <p>
                        There are currently no available food donations.
                    </p>
                </div>
            ) : (
                donations.map((donation) => (
                    <div className="card" key={donation.id}>

                        <h2>{donation.foodName}</h2>

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

                        <p>
                            <strong>Status:</strong>{" "}
                            {donation.status}
                        </p>

                        <button
                            className="primary-button"
                            onClick={() => handleRequest(donation.id)}
                        >
                            Request Food
                        </button>

                    </div>
                ))
            )}

        </div>
    );
}

export default Donations;