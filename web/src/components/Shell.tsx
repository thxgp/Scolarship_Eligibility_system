import { useEffect, useState, type ReactNode } from 'react';
import { Link, NavLink, useLocation } from 'react-router';

const NAV_ITEMS = [
  { to: '/', label: 'Home' },
  { to: '/schemes', label: 'Schemes' },
  { to: '/apply', label: 'Apply' },
  { to: '/track', label: 'Track status' },
  { to: '/admin', label: 'Officer portal' },
];

export function Shell({ children }: { children: ReactNode }) {
  const { pathname } = useLocation();
  const [open, setOpen] = useState(false);

  // Close the mobile menu and reset scroll on navigation.
  useEffect(() => {
    setOpen(false);
    window.scrollTo(0, 0);
  }, [pathname]);

  return (
    <>
      <a href="#main" className="skip">
        Skip to content
      </a>
      <header className="bar">
        <div className="wrap bar__in">
          <Link to="/" className="brand" aria-label="EduScholar home">
            <span className="brand__mark" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M12 2Q12 12 22 12Q12 12 12 22Q12 12 2 12Q12 12 12 2Z" /></svg></span>
            EduScholar
          </Link>
          <nav className="nav" id="site-nav" aria-label="Main" data-open={open}>
            {NAV_ITEMS.map((item) => (
              <NavLink key={item.to} to={item.to} end={item.to === '/'}>
                {item.label}
              </NavLink>
            ))}
          </nav>
          <div className="bar__cta">
            <Link to="/apply" className="btn btn--solid">
              Apply now
            </Link>
            <button
              type="button"
              className="menu-btn"
              aria-expanded={open}
              aria-controls="site-nav"
              onClick={() => setOpen((o) => !o)}
            >
              {open ? 'Close' : 'Menu'}
            </button>
          </div>
        </div>
      </header>
      <main id="main">{children}</main>
      <footer className="foot">
        <div className="wrap foot__in">
          <div>
            <p className="foot__brand">EduScholar</p>
            <p className="foot__tag">Applications, verification and tracking for six national scholarship schemes.</p>
          </div>
          <nav aria-label="Footer">
            {NAV_ITEMS.map((item) => (
              <Link key={item.to} to={item.to}>
                {item.label}
              </Link>
            ))}
          </nav>
        </div>
      </footer>
    </>
  );
}
