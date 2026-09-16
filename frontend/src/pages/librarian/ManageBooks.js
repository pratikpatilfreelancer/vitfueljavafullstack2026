import React, { useEffect, useState } from "react";
import { libraryApi, extractErrorMessage } from "../../api/client";

const emptyForm = { title: "", author: "", isbn: "", category: "", totalCopies: 1 };

export default function ManageBooks() {
  const [books, setBooks] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

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

  function handleChange(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function startEdit(book) {
    setEditingId(book.bookId);
    setForm({
      title: book.title,
      author: book.author,
      isbn: book.isbn,
      category: book.category || "",
      totalCopies: book.totalCopies,
    });
  }

  function resetForm() {
    setEditingId(null);
    setForm(emptyForm);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setMessage("");
    const payload = { ...form, totalCopies: Number(form.totalCopies) };
    try {
      if (editingId) {
        await libraryApi.put(`/books/${editingId}`, payload);
        setMessage("Book updated.");
      } else {
        await libraryApi.post("/books", payload);
        setMessage("Book added.");
      }
      resetForm();
      loadBooks();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  async function handleDelete(bookId) {
    setError("");
    setMessage("");
    try {
      await libraryApi.delete(`/books/${bookId}`);
      setMessage("Book deleted.");
      loadBooks();
    } catch (err) {
      setError(extractErrorMessage(err));
    }
  }

  return (
    <div>
      <div className="card">
        <h2>{editingId ? "Edit Book" : "Add New Book"}</h2>
        {error && <div className="error-box">{error}</div>}
        {message && <div className="success-box">{message}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Title</label>
            <input value={form.title} onChange={(e) => handleChange("title", e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Author</label>
            <input value={form.author} onChange={(e) => handleChange("author", e.target.value)} required />
          </div>
          <div className="form-group">
            <label>ISBN</label>
            <input value={form.isbn} onChange={(e) => handleChange("isbn", e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Category</label>
            <input value={form.category} onChange={(e) => handleChange("category", e.target.value)} />
          </div>
          <div className="form-group">
            <label>Total Copies</label>
            <input
              type="number"
              min={1}
              value={form.totalCopies}
              onChange={(e) => handleChange("totalCopies", e.target.value)}
              required
            />
          </div>
          <button className="btn" type="submit">{editingId ? "Save Changes" : "Add Book"}</button>
          {editingId && (
            <button className="btn secondary" type="button" style={{ marginLeft: 8 }} onClick={resetForm}>
              Cancel
            </button>
          )}
        </form>
      </div>

      <div className="card">
        <h2>All Books</h2>
        <table>
          <thead>
            <tr>
              <th>Title</th>
              <th>Author</th>
              <th>ISBN</th>
              <th>Category</th>
              <th>Copies (avail/total)</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.bookId}>
                <td>{book.title}</td>
                <td>{book.author}</td>
                <td>{book.isbn}</td>
                <td>{book.category}</td>
                <td>{book.availableCopies} / {book.totalCopies}</td>
                <td>
                  <button className="btn small" onClick={() => startEdit(book)}>Edit</button>{" "}
                  <button className="btn small danger" onClick={() => handleDelete(book.bookId)}>Delete</button>
                </td>
              </tr>
            ))}
            {books.length === 0 && (
              <tr>
                <td colSpan={6} className="muted">No books in the catalog yet.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
