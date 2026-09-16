import "./App.css";

import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Donations from "./pages/Donations";
import Requests from "./pages/Requests";
import ManageDonations from "./pages/ManageDonations";
import Users from "./pages/Users";
import ManageRequests from "./pages/ManageRequests";
import ManageAllDonations from "./pages/ManageAllDonations";


function ProtectedRoute({ children, allowedRoles }) {

    const user = JSON.parse(localStorage.getItem("user"));

    // User is not logged in
    if (!user) {
        return <Navigate to="/login" replace />;
    }

    // User does not have permission
    if (
        allowedRoles &&
        !allowedRoles.includes(user.role)
    ) {
        return <Navigate to="/dashboard" replace />;
    }

    return children;
}


function App() {

    return (
        <BrowserRouter>

            <Routes>

                {/* Public Route */}
                <Route
                    path="/login"
                    element={<Login />}
                />


                {/* Dashboard */}
                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute
                            allowedRoles={["DONOR", "NGO", "ADMIN"]}
                        >
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />


                {/* NGO - Available Donations */}
                <Route
                    path="/donations"
                    element={
                        <ProtectedRoute allowedRoles={["NGO"]}>
                            <Donations />
                        </ProtectedRoute>
                    }
                />


                {/* NGO - My Requests */}
                <Route
                    path="/requests"
                    element={
                        <ProtectedRoute allowedRoles={["NGO"]}>
                            <Requests />
                        </ProtectedRoute>
                    }
                />


                {/* Donor - Manage Donations */}
                <Route
                    path="/manage-donations"
                    element={
                        <ProtectedRoute allowedRoles={["DONOR"]}>
                            <ManageDonations />
                        </ProtectedRoute>
                    }
                />


                {/* Admin - Users */}
                <Route
                    path="/users"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <Users />
                        </ProtectedRoute>
                    }
                />


                {/* Admin - Requests */}
                <Route
                    path="/manage-requests"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <ManageRequests />
                        </ProtectedRoute>
                    }
                />


                {/* Admin - All Donations */}
                <Route
                    path="/manage-all-donations"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <ManageAllDonations />
                        </ProtectedRoute>
                    }
                />


                {/* Default */}
                <Route
                    path="/"
                    element={<Navigate to="/login" replace />}
                />

            </Routes>

        </BrowserRouter>
    );
}

export default App;