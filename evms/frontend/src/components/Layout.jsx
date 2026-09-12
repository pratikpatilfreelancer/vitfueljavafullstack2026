import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_BY_ROLE = {
  EMPLOYEE: [
    { to: '/employee/dashboard', label: 'Dashboard' },
    { to: '/employee/create', label: 'Create Voucher' },
    { to: '/employee/vouchers', label: 'My Vouchers' },
  ],
  DIRECTOR: [
    { to: '/director/dashboard', label: 'Dashboard' },
    { to: '/director/pending', label: 'Pending Approvals' },
    { to: '/director/vouchers', label: 'All Vouchers' },
  ],
  ACCOUNTS: [
    { to: '/accounts/dashboard', label: 'Dashboard' },
    { to: '/accounts/vouchers', label: 'All Vouchers' },
  ],
};

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const links = NAV_BY_ROLE[user.role] || [];

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">Ledger&nbsp;&amp;&nbsp;Co.</div>
          <div className="brand-sub">Expense Voucher System</div>
        </div>

        <div className="nav-section-label">Menu</div>
        {links.map((l) => (
          <NavLink
            key={l.to}
            to={l.to}
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
          >
            {l.label}
          </NavLink>
        ))}

        <div className="sidebar-footer">
          <div className="user-chip">{user.name}</div>
          <div className="user-role">{user.role}</div>
          <button className="logout-btn" onClick={handleLogout}>Log out</button>
        </div>
      </aside>

      <main className="main-area">
        <Outlet />
      </main>
    </div>
  );
}
