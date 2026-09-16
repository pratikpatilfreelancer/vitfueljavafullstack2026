import React, { useEffect, useState } from "react";
import { libraryApi, extractErrorMessage } from "../../api/client";

export default function BookSearch() {
  const [books, setBooks] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [reservingId, setReservingId] = useState(null);

  useEffect(() => {
    loadBooks();
  }, []);

  async function loadBooks() {
    try {
      const response = await libraryApi.get("/books");
      setBooks(response.data);
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleSearch(e) {
    e.preventDefault();
    setError("");
    if (!keyword.trim()) {
      loadBooks();
      return;
    }
    try {
      const response = await libraryApi.get("/books/search", { params: { keyword } });
      setBooks(response.data);
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleReserve(bookId) {
    setError("");
    setMessage("");
    setReservingId(bookId);
    try {
      await libraryApi.post("/reservations", { bookId });
      setMessage("Reservation created! Check My Reservations for its status.");
      loadBooks();
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setReservingId(null);
    }
  }

  return (
    <div className="card">
      <h2>Book Catalog</h2>
      {error && <div className="error-box">{error}</div>}
      {message && <div className="success-box">{message}</div>}

      <form className="search-row" onSubmit={handleSearch}>
        <input
          placeholder="Search by title, author or category..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <button className="btn" type="submit">Search</button>
        <button
          className="btn secondary"
          type="button"
          onClick={() => {
            setKeyword("");
            loadBooks();
          }}
        >
          Reset
        </button>
      </form>

      <table>
        <thead>
          <tr>
            <th>Title</th>
            <th>Author</th>
            <th>Category</th>
            <th>Available</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {books.map((book) => (
            <tr key={book.bookId}>
              <td>{book.title}</td>
              <td>{book.author}</td>
              <td>{book.category}</td>
              <td>{book.availableCopies} / {book.totalCopies}</td>
              <td>
                <button
                  className="btn small"
                  disabled={book.availableCopies === 0 || reservingId === book.bookId}
                  onClick={() => handleReserve(book.bookId)}
                >
                  {book.availableCopies === 0 ? "Unavailable" : "Reserve"}
                </button>
              </td>
            </tr>
          ))}
          {books.length === 0 && (
            <tr>
              <td colSpan={5} className="muted">No books found.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
