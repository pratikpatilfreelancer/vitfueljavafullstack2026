import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

function ManageRequests() {
    const navigate = useNavigate();
    const [requests, setRequests] = useState([]);

    useEffect(() => {
        loadRequests();
    }, []);

    const loadRequests = async () => {
        try {
            const response = await api.get("/requests");
            setRequests(response.data);
        } catch (error) {
            console.error(error);
            alert("Failed to load requests");
        }
    };

    const updateStatus = async (id, status) => {
        try {
            await api.put(`/requests/${id}/status?status=${status}`);

            alert(`Request ${status.toLowerCase()} successfully`);

            loadRequests();
        } catch (error) {
            alert(error.response?.data?.error || "Failed to update request");
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

            <h1>Manage Food Requests</h1>
            <p>Approve or reject food requests from NGOs.</p>

            {requests.length === 0 ? (
                <div className="card">
                    <h3>No requests found</h3>
                </div>
            ) : (
                requests.map((request) => (
                    <div className="card request-card" key={request.id}>

                        <div className="request-header">
                            <h2>Request #{request.id}</h2>

                            <span className={`status-badge ${request.status?.toLowerCase()}`}>
                                {request.status}
                            </span>
                        </div>

                        <p>
                            <strong>Requester:</strong>{" "}
                            {request.requester?.name || "N/A"}
                        </p>

                        <p>
                            <strong>Email:</strong>{" "}
                            {request.requester?.email || "N/A"}
                        </p>

                        <p>
                            <strong>Food:</strong>{" "}
                            {request.foodDonation?.foodName || "N/A"}
                        </p>

                        <p>
                            <strong>Quantity:</strong>{" "}
                            {request.foodDonation?.quantity || "N/A"}
                        </p>

                        <p>
                            <strong>Location:</strong>{" "}
                            {request.foodDonation?.location || "N/A"}
                        </p>

                        <p>
                            <strong>Request Date:</strong>{" "}
                            {request.requestDate
                                ? new Date(request.requestDate).toLocaleString()
                                : "N/A"}
                        </p>

                        {request.status === "PENDING" && (
                            <div className="dashboard-buttons">

                                <button
                                    className="success-button"
                                    onClick={() =>
                                        updateStatus(request.id, "APPROVED")
                                    }
                                >
                                    Approve
                                </button>

                                <button
                                    className="danger-button"
                                    onClick={() =>
                                        updateStatus(request.id, "REJECTED")
                                    }
                                >
                                    Reject
                                </button>

                            </div>
                        )}

                    </div>
                ))
            )}

        </div>
    );
}

export default ManageRequests;