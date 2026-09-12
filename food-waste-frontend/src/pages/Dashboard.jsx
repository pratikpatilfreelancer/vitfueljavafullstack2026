import { useNavigate } from "react-router-dom";

function Dashboard() {

    const navigate = useNavigate();

    const user = JSON.parse(localStorage.getItem("user"));

    const handleLogout = () => {
        localStorage.removeItem("user");
        navigate("/login");
    };

    if (!user) {
        return (
            <div className="page-container">
                <h2>Please login first.</h2>

                <button
                    className="primary-button"
                    onClick={() => navigate("/login")}
                >
                    Go to Login
                </button>
            </div>
        );
    }

    return (
        <div>

            <div className="navbar">
                <h2>Food Waste Management</h2>

                <button
                    className="secondary-button"
                    onClick={handleLogout}
                >
                    Logout
                </button>
            </div>

            <div className="page-container">

                <div className="card">

                    <h1>Dashboard</h1>

                    <h3>Welcome, {user.name}!</h3>

                    <p>
                        <strong>Email:</strong> {user.email}
                    </p>

                    <p>
                        <strong>Role:</strong> {user.role}
                    </p>

                </div>

                <div className="card">

                    <h2>Available Actions</h2>

                    {/* DONOR */}
                    {user.role === "DONOR" && (
                        <div className="dashboard-buttons">

                            <button
                                className="primary-button"
                                onClick={() =>
                                    navigate("/manage-donations")
                                }
                            >
                                Manage My Donations
                            </button>

                        </div>
                    )}

                    {/* NGO */}
                    {user.role === "NGO" && (
                        <div className="dashboard-buttons">

                            <button
                                className="primary-button"
                                onClick={() =>
                                    navigate("/donations")
                                }
                            >
                                View Available Food
                            </button>

                            <button
                                className="primary-button"
                                onClick={() =>
                                    navigate("/requests")
                                }
                            >
                                My Food Requests
                            </button>

                        </div>
                    )}

                    {/* ADMIN */}
                    {user.role === "ADMIN" && (
                        <div className="dashboard-buttons">

                            <button
                                className="primary-button"
                                onClick={() =>
                                    navigate("/users")
                                }
                            >
                                Manage Users
                            </button>

                            <button
                                className="primary-button"
                                onClick={() =>
                                    navigate("/manage-all-donations")
                                }
                            >
                                Manage Donations
                            </button>

                            <button
                                className="primary-button"
                                onClick={() =>
                                    navigate("/manage-requests")
                                }
                            >
                                Manage Requests
                            </button>

                        </div>
                    )}

                </div>

            </div>

        </div>
    );
}

export default Dashboard;