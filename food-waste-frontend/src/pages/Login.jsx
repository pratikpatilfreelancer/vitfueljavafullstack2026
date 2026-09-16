import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

function Login() {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const navigate = useNavigate();

    const handleLogin = async (e) => {

        e.preventDefault();
        setError("");

        try {

            const response = await api.post("/users/login", {
                email: email,
                password: password
            });

            localStorage.setItem(
                "user",
                JSON.stringify(response.data)
            );

            navigate("/dashboard");

        } catch (error) {

            setError("Invalid email or password");

        }
    };

    return (
        <div className="login-container">

            <h1>Food Waste Management</h1>

            <p style={{ textAlign: "center" }}>
                Reduce food waste. Help those in need.
            </p>

            <h2>Login</h2>

            <form onSubmit={handleLogin}>

                <div className="form-group">
                    <label>Email</label>

                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Enter your email"
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Password</label>

                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter your password"
                        required
                    />
                </div>

                {error && (
                    <p>
                        {error}
                    </p>
                )}

                <button
                    type="submit"
                    className="primary-button"
                >
                    Login
                </button>

            </form>

        </div>
    );
}

export default Login;