import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * Guards a page behind login and, optionally, a specific role.
 * Note: this is a frontend convenience only - every backend endpoint also
 * enforces its own role checks, since the requirements call for real
 * backend authorization rather than relying on the UI to hide buttons.
 */
export default function ProtectedRoute({ role, children }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (role && user.role !== role) {
    return <Navigate to="/" replace />;
  }
  return children;
}
