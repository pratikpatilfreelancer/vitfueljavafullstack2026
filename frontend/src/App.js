import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";

import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";

import StudentDashboard from "./pages/student/StudentDashboard";
import BookSearch from "./pages/student/BookSearch";
import MyReservations from "./pages/student/MyReservations";

import LibrarianDashboard from "./pages/librarian/LibrarianDashboard";
import ManageBooks from "./pages/librarian/ManageBooks";
import ManageReservations from "./pages/librarian/ManageReservations";
import IssueReturn from "./pages/librarian/IssueReturn";

function HomeRedirect() {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return <Navigate to={user.role === "LIBRARIAN" ? "/librarian" : "/student"} replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomeRedirect />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route
            path="/student"
            element={
              <ProtectedRoute role="STUDENT">
                <StudentDashboard />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="books" replace />} />
            <Route path="books" element={<BookSearch />} />
            <Route path="reservations" element={<MyReservations />} />
          </Route>

          <Route
            path="/librarian"
            element={
              <ProtectedRoute role="LIBRARIAN">
                <LibrarianDashboard />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="books" replace />} />
            <Route path="books" element={<ManageBooks />} />
            <Route path="reservations" element={<ManageReservations />} />
            <Route path="issue-return" element={<IssueReturn />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
