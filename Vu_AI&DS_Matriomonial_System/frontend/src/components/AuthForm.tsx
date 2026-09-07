"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Eye, EyeOff, Lock, Mail, UserRound } from "lucide-react";
import { authApi } from "@/lib/api";
import { useAuth } from "@/contexts/AuthContext";
import { PixelButton } from "@/components/PixelButton";
import { PhysicsCupid } from "@/components/PhysicsCupid";

interface AuthFormProps {
  initialMode: "login" | "signup";
}

export function AuthForm({ initialMode }: AuthFormProps) {
  const router = useRouter();
  const { login, register } = useAuth();
  const [mode, setMode] = useState<"login" | "signup">(initialMode);

  // Login form states
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [showLoginPassword, setShowLoginPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);

  // Sign Up form states
  const [name, setName] = useState("");
  const [signupEmail, setSignupEmail] = useState("");
  const [signupPassword, setSignupPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showSignupPassword, setShowSignupPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [termsAgreed, setTermsAgreed] = useState(true);

  // Feedback states
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const switchMode = (newMode: "login" | "signup") => {
    setError("");
    setMode(newMode);
    if (newMode === "login") {
      window.history.replaceState(null, "", "/login");
    } else {
      window.history.replaceState(null, "", "/register");
    }
  };

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await authApi.login({ email: loginEmail, password: loginPassword });
      await login(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || "Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  const handleSignupSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!termsAgreed) {
      setError("Please agree to the Terms & Conditions.");
      return;
    }

    if (signupPassword !== confirmPassword) {
      setError("Passwords do not match!");
      return;
    }

    if (signupPassword.length < 6) {
      setError("Password must be at least 6 characters long.");
      return;
    }

    setLoading(true);

    try {
      const res = await authApi.register({
        name,
        email: signupEmail,
        password: signupPassword,
        confirmPassword,
      });
      await register(res.data);
    } catch (err: any) {
      const msg = err.response?.data?.message || "Registration failed. Email may already be registered.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="match-page auth-page">
      <div className="match-stage auth-stage">
        {/* Animated Sky Elements */}
        <div className="pixel-sky-object pixel-cloud cloud-one" aria-hidden="true" />
        <div className="pixel-sky-object pixel-cloud cloud-two" aria-hidden="true" />
        <div className="pixel-sky-object pixel-cloud cloud-three" aria-hidden="true" />
        <div className="pixel-sky-object pixel-cloud cloud-four" aria-hidden="true" />

        <div className="pixel-sky-object pixel-spark spark-one" aria-hidden="true" />
        <div className="pixel-sky-object pixel-spark spark-two" aria-hidden="true" />
        <div className="pixel-sky-object pixel-spark spark-three" aria-hidden="true" />
        <div className="pixel-sky-object pixel-spark spark-four" aria-hidden="true" />
        <div className="pixel-sky-object pixel-spark spark-five" aria-hidden="true" />

        <div className="pixel-sky-object pixel-heart heart-one" aria-hidden="true" />
        <div className="pixel-sky-object pixel-heart heart-two" aria-hidden="true" />
        <div className="pixel-sky-object pixel-heart heart-three" aria-hidden="true" />
        <div className="pixel-sky-object pixel-heart heart-four" aria-hidden="true" />

        {/* Cupids with real physics */}
        <PhysicsCupid side="left" />
        <PhysicsCupid side="right" />

        <section className="auth-content">
          <h1 className="auth-brand">
            <span className="brand-heart" aria-hidden="true">♥</span>
            HeartMate
            <span className="brand-heart" aria-hidden="true">♥</span>
          </h1>
          <p className="auth-tagline">Find your perfect match</p>

          {error && (
            <div className="pixel-alert-error" style={{ marginBottom: "1rem" }}>
              {error}
            </div>
          )}

          <div className={`auth-card pixel-border mode-${mode}`}>
            <div className="auth-slider">
              
              {/* ===== LOGIN PANEL ===== */}
              <form
                className="auth-panel"
                aria-hidden={mode !== "login"}
                onSubmit={handleLoginSubmit}
              >
                <h2 className="auth-panel-title">
                  Welcome Back! <span aria-hidden="true">♥</span>
                </h2>
                <p className="auth-panel-sub">Login to continue your journey</p>

                <div className="pixel-field-icon">
                  <UserRound aria-hidden="true" />
                  <input
                    type="email"
                    placeholder="Email Address"
                    value={loginEmail}
                    onChange={(e) => setLoginEmail(e.target.value)}
                    required
                  />
                </div>

                <div className="pixel-field-icon">
                  <Lock aria-hidden="true" />
                  <input
                    type={showLoginPassword ? "text" : "password"}
                    placeholder="Password"
                    value={loginPassword}
                    onChange={(e) => setLoginPassword(e.target.value)}
                    required
                  />
                  <button
                    type="button"
                    className="field-toggle"
                    onClick={() => setShowLoginPassword((v) => !v)}
                  >
                    {showLoginPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>

                <div className="auth-row">
                  <label className="pixel-check">
                    <input
                      type="checkbox"
                      checked={rememberMe}
                      onChange={(e) => setRememberMe(e.target.checked)}
                    />
                    <span>Remember me</span>
                  </label>
                  <button
                    type="button"
                    className="pixel-link"
                    onClick={() => alert("Password reset is not available in this demo.")}
                  >
                    Forgot Password?
                  </button>
                </div>

                <PixelButton type="submit" disabled={loading} className="auth-submit">
                  {loading ? "LOGGING IN..." : "Log In ♥"}
                </PixelButton>

                <p className="auth-switch">
                  New here?{" "}
                  <button type="button" className="pixel-link" onClick={() => switchMode("signup")}>
                    Sign Up
                  </button>
                </p>
              </form>

              {/* ===== SIGN UP PANEL ===== */}
              <form
                className="auth-panel"
                aria-hidden={mode !== "signup"}
                onSubmit={handleSignupSubmit}
              >
                <h2 className="auth-panel-title">
                  Create Account <span aria-hidden="true">♥</span>
                </h2>
                <p className="auth-panel-sub">Join HeartMate today</p>

                <div className="pixel-field-icon">
                  <UserRound aria-hidden="true" />
                  <input
                    type="text"
                    placeholder="Full Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                  />
                </div>

                <div className="pixel-field-icon">
                  <Mail aria-hidden="true" />
                  <input
                    type="email"
                    placeholder="Email Address"
                    value={signupEmail}
                    onChange={(e) => setSignupEmail(e.target.value)}
                    required
                  />
                </div>

                <div className="pixel-field-icon">
                  <Lock aria-hidden="true" />
                  <input
                    type={showSignupPassword ? "text" : "password"}
                    placeholder="Password (Min 6 chars)"
                    value={signupPassword}
                    onChange={(e) => setSignupPassword(e.target.value)}
                    required
                  />
                  <button
                    type="button"
                    className="field-toggle"
                    onClick={() => setShowSignupPassword((v) => !v)}
                  >
                    {showSignupPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>

                <div className="pixel-field-icon">
                  <Lock aria-hidden="true" />
                  <input
                    type={showConfirmPassword ? "text" : "password"}
                    placeholder="Confirm Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                  />
                  <button
                    type="button"
                    className="field-toggle"
                    onClick={() => setShowConfirmPassword((v) => !v)}
                  >
                    {showConfirmPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>

                <label className="pixel-check">
                  <input
                    type="checkbox"
                    checked={termsAgreed}
                    onChange={(e) => setTermsAgreed(e.target.checked)}
                  />
                  <span>I agree to Terms &amp; Conditions</span>
                </label>

                <PixelButton type="submit" disabled={loading} className="auth-submit">
                  {loading ? "CREATING ACCOUNT..." : "Sign Up ♥"}
                </PixelButton>

                <p className="auth-switch">
                  Already have an account?{" "}
                  <button type="button" className="pixel-link" onClick={() => switchMode("login")}>
                    Log In
                  </button>
                </p>
              </form>

            </div>
          </div>

          <div className="auth-footer-hearts" aria-hidden="true">
            ♥ ♥ ♥
          </div>
        </section>
      </div>
    </main>
  );
}
