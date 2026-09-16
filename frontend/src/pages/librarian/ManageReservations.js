import React, { useEffect, useState } from "react";
import { libraryApi, extractErrorMessage } from "../../api/client";
import StatusBadge from "../../components/StatusBadge";

export default function ManageReservations() {
  const [reservations, setReservations] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    loadReservations();
  }, []);

  async function loadReservations() {
    try {
      const response = await libraryApi.get("/reservations");
      setReservations(response.data);
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleApprove(reservationId) {
    setError("");
    setMessage("");
    try {
      await libraryApi.put(`/reservations/${reservationId}/approve`);
      setMessage("Reservation approved.");
      loadReservations();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleCancel(reservationId) {
    setError("");
    setMessage("");
    try {
      await libraryApi.put(`/reservations/${reservationId}/cancel`);
      setMessage("Reservation cancelled.");
      loadReservations();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  return (
    <div className="card">
      <h2>All Reservations</h2>
      {error && <div className="error-box">{error}</div>}
      {message && <div className="success-box">{message}</div>}

      <table>
        <thead>
          <tr>
            <th>Reservation ID</th>
            <th>Student User ID</th>
            <th>Book</th>
            <th>Reserved On</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {reservations.map((r) => (
            <tr key={r.reservationId}>
              <td>{r.reservationId}</td>
              <td>{r.userId}</td>
              <td>{r.bookTitle}</td>
              <td>{new Date(r.reservationDate).toLocaleString()}</td>
              <td><StatusBadge status={r.status} /></td>
              <td>
                {r.status === "PENDING" && (
                  <button className="btn small" onClick={() => handleApprove(r.reservationId)}>Approve</button>
                )}{" "}
                {(r.status === "PENDING" || r.status === "APPROVED") && (
                  <button className="btn small danger" onClick={() => handleCancel(r.reservationId)}>Cancel</button>
                )}
              </td>
            </tr>
          ))}
          {reservations.length === 0 && (
            <tr>
              <td colSpan={6} className="muted">No reservations yet.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
