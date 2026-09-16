import React, { useEffect, useState } from "react";
import { libraryApi, extractErrorMessage } from "../../api/client";
import StatusBadge from "../../components/StatusBadge";

export default function IssueReturn() {
  const [reservationId, setReservationId] = useState("");
  const [borrowings, setBorrowings] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    loadBorrowings();
  }, []);

  async function loadBorrowings() {
    try {
      const response = await libraryApi.get("/borrowings");
      setBorrowings(response.data);
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleIssue(e) {
    e.preventDefault();
    setError("");
    setMessage("");
    try {
      await libraryApi.post("/borrowings/issue", { reservationId: Number(reservationId) });
      setMessage(`Book issued for reservation #${reservationId}.`);
      setReservationId("");
      loadBorrowings();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleReturn(borrowingId) {
    setError("");
    setMessage("");
    try {
      await libraryApi.put(`/borrowings/${borrowingId}/return`);
      setMessage(`Book returned for borrowing #${borrowingId}.`);
      loadBorrowings();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  return (
    <div>
      <div className="card">
        <h2>Issue a Book</h2>
        <p className="muted">
          Enter the ID of an APPROVED reservation (see Manage Reservations) to hand the book over to the student.
        </p>
        {error && <div className="error-box">{error}</div>}
        {message && <div className="success-box">{message}</div>}
        <form onSubmit={handleIssue} className="search-row">
          <input
            type="number"
            placeholder="Reservation ID"
            value={reservationId}
            onChange={(e) => setReservationId(e.target.value)}
            required
          />
          <button className="btn" type="submit">Issue Book</button>
        </form>
      </div>

      <div className="card">
        <h2>Borrowing Records</h2>
        <table>
          <thead>
            <tr>
              <th>Borrowing ID</th>
              <th>Book</th>
              <th>Student User ID</th>
              <th>Issue Date</th>
              <th>Due Date</th>
              <th>Return Date</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {borrowings.map((b) => (
              <tr key={b.borrowingId}>
                <td>{b.borrowingId}</td>
                <td>{b.bookTitle}</td>
                <td>{b.userId}</td>
                <td>{b.issueDate}</td>
                <td>{b.dueDate}</td>
                <td>{b.returnDate || "-"}</td>
                <td><StatusBadge status={b.status} /></td>
                <td>
                  {b.status === "BORROWED" && (
                    <button className="btn small" onClick={() => handleReturn(b.borrowingId)}>Mark Returned</button>
                  )}
                </td>
              </tr>
            ))}
            {borrowings.length === 0 && (
              <tr>
                <td colSpan={8} className="muted">No borrowing records yet.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
