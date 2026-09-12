import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

function Users() {
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            const response = await api.get("/users");
            setUsers(response.data);
        } catch (error) {
            console.error(error);
            alert("Failed to load users");
        }
    };

    const deleteUser = async (id) => {
        if (!window.confirm("Are you sure you want to delete this user?")) {
            return;
        }

        try {
            await api.delete(`/users/${id}`);
            alert("User deleted successfully");
            loadUsers();
        } catch (error) {
            alert(error.response?.data?.error || "Failed to delete user");
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

            <h1>Manage Users</h1>
            <p>View and manage registered users.</p>

            {users.length === 0 ? (
                <div className="card">
                    <h3>No users found</h3>
                </div>
            ) : (
                <div className="admin-grid">
                    {users.map((user) => (
                        <div className="card admin-card" key={user.id}>

                            <div className="user-header">
                                <h2>{user.name}</h2>
                                <span className={`role-badge ${user.role?.toLowerCase()}`}>
                                    {user.role}
                                </span>
                            </div>

                            <p>
                                <strong>ID:</strong> {user.id}
                            </p>

                            <p>
                                <strong>Email:</strong> {user.email}
                            </p>

                            <button
                                className="danger-button"
                                onClick={() => deleteUser(user.id)}
                            >
                                Delete User
                            </button>

                        </div>
                    ))}
                </div>
            )}

        </div>
    );
}

export default Users;