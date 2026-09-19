import React from "react";
import { NavLink, Outlet } from "react-router-dom";
import Navbar from "../../components/Navbar";

export default function LibrarianDashboard() {
  return (
    <div>
      <Navbar />
      <div className="container">
        <h1>Librarian Dashboard</h1>
        <div className="tabs">
          <NavLink to="/librarian/books" className={({ isActive }) => (isActive ? "active" : "")}>
            Manage Books
          </NavLink>
          <NavLink to="/librarian/reservations" className={({ isActive }) => (isActive ? "active" : "")}>
            Manage Reservations
          </NavLink>
          <NavLink to="/librarian/issue-return" className={({ isActive }) => (isActive ? "active" : "")}>
            Issue / Return
          </NavLink>
        </div>
        <Outlet />
      </div>
    </div>
  );
}
