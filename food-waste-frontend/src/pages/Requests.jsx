import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

function Requests() {

    const [requests, setRequests] = useState([]);
    const [error, setError] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        loadRequests();
    }, []);

    const loadRequests = async () => {

        const user = JSON.parse(localStorage.getItem("user"));

        if (!user) {
            navigate("/login");
            return;
        }

        try {

            const response = await api.get(
                `/requests/user/${user.id}`
            );

            setRequests(response.data);

        } catch (error) {

            setError("Unable to load your requests");

        }
    };

    return (
        <div>

            <div className="navbar">

                <h2>Food Waste Management</h2>

                <button
                    className="secondary-button"
                    onClick={() => navigate("/dashboard")}
                >
                    Dashboard
                </button>

            </div>

            <div className="page-container">

                <h1>My Food Requests</h1>

                <p>
                    Track the food requests you have submitted.
                </p>

                {error && (
                    <div className="card">
                        <p>{error}</p>
                    </div>
                )}

                {requests.length === 0 ? (

                    <div className="card">
                        <p>
                            You have not made any food requests.
                        </p>
                    </div>

                ) : (

                    <div>

                        {requests.map((request) => (

                            <div
                                className="card"
                                key={request.id}
                            >

                                <h2>
                                    {request.foodDonation?.foodName}
                                </h2>

                                <p>
                                    <strong>Quantity:</strong>{" "}
                                    {request.foodDonation?.quantity}
                                </p>

                                <p>
                                    <strong>Location:</strong>{" "}
                                    {request.foodDonation?.location}
                                </p>

                                <p>
                                    <strong>Donor:</strong>{" "}
                                    {request.foodDonation?.donor?.name}
                                </p>

                                <p>
                                    <strong>Request Date:</strong>{" "}
                                    {request.requestDate}
                                </p>

                                <p className="status">
                                    <strong>Status:</strong>{" "}
                                    {request.status}
                                </p>

                            </div>

                        ))}

                    </div>

                )}

            </div>

        </div>
    );
}

export default Requests;