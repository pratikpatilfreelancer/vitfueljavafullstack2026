import React from "react";
import { NavLink, Outlet } from "react-router-dom";
import Navbar from "../../components/Navbar";

export default function StudentDashboard() {
  return (
    <div>
      <Navbar />
      <div className="container">
        <h1>Student Dashboard</h1>
        <div className="tabs">
          <NavLink to="/student/books" className={({ isActive }) => (isActive ? "active" : "")}>
            Search Books
          </NavLink>
          <NavLink to="/student/reservations" className={({ isActive }) => (isActive ? "active" : "")}>
            My Reservations
          </NavLink>
        </div>
        <Outlet />
      </div>
    </div>
  );
}
