import React, { createContext, useContext, useState } from "react";
import { clearStoredCredentials, setStoredCredentials } from "../api/client";

const AuthContext = createContext(null);

const STORAGE_KEY = "library_user";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored ? JSON.parse(stored) : null;
  });

  function login(userResponse, email, password) {
    // userResponse comes from POST /api/users/login: { userId, name, email, role }
    setStoredCredentials(email, password);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(userResponse));
    setUser(userResponse);
  }

  function logout() {
    clearStoredCredentials();
    localStorage.removeItem(STORAGE_KEY);
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
