import { createContext, useContext, useState } from "react";

const RoleContext = createContext(null);

export function RoleProvider({ children }) {
  const [role, setRole] = useState(null);
  const [userId, setUserId] = useState(null);
  const [userName, setUserName] = useState("");

  const login = (selectedRole, selectedUserId, selectedUserName) => {
    setRole(selectedRole);
    setUserId(selectedUserId);
    setUserName(selectedUserName);
  };

  const logout = () => {
    setRole(null);
    setUserId(null);
    setUserName("");
  };

  return (
    <RoleContext.Provider value={{ role, userId, userName, login, logout }}>
      {children}
    </RoleContext.Provider>
  );
}

export function useRole() {
  return useContext(RoleContext);
}