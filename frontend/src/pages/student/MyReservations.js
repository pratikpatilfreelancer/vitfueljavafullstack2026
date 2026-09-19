import React, { useEffect, useState } from "react";
import { libraryApi, extractErrorMessage } from "../../api/client";
import { useAuth } from "../../context/AuthContext";
import StatusBadge from "../../components/StatusBadge";

export default function MyReservations() {
  const { user } = useAuth();
  const [reservations, setReservations] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    loadReservations();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function loadReservations() {
    try {
      const response = await libraryApi.get(`/reservations/user/${user.userId}`);
      setReservations(response.data);
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

  const canCancel = (status) => status === "PENDING" || status === "APPROVED";

  return (
    <div className="card">
      <h2>My Reservations</h2>
      {error && <div className="error-box">{error}</div>}
      {message && <div className="success-box">{message}</div>}

      <table>
        <thead>
          <tr>
            <th>Book</th>
            <th>Reserved On</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {reservations.map((r) => (
            <tr key={r.reservationId}>
              <td>{r.bookTitle}</td>
              <td>{new Date(r.reservationDate).toLocaleString()}</td>
              <td><StatusBadge status={r.status} /></td>
              <td>
                {canCancel(r.status) && (
                  <button className="btn small danger" onClick={() => handleCancel(r.reservationId)}>
                    Cancel
                  </button>
                )}
              </td>
            </tr>
          ))}
          {reservations.length === 0 && (
            <tr>
              <td colSpan={4} className="muted">You have no reservations yet.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
