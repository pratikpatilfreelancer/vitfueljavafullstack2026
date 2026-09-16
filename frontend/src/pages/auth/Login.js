import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { userApi, extractErrorMessage } from "../../api/client";
import { useAuth } from "../../context/AuthContext";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const response = await userApi.post("/users/login", { email, password });
      login(response.data, email, password);
      // The backend tells us the role - frontend just routes based on it.
      navigate(response.data.role === "LIBRARIAN" ? "/librarian" : "/student");
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="card">
        <h2>Login</h2>
        {error && <div className="error-box">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
          <button className="btn" type="submit" disabled={loading} style={{ width: "100%" }}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>
        <p className="muted" style={{ marginTop: 14 }}>
          Don't have an account? <Link to="/register">Register as a student</Link>
        </p>
        <p className="muted">
          Librarian demo login: librarian@library.com / librarian123
        </p>
      </div>
    </div>
  );
}
