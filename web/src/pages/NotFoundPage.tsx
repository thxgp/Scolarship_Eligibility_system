import { Link } from 'react-router';

export function NotFoundPage() {
  return (
    <section className="hero">
      <div className="wrap">
        <h1>
          Score <em>not found.</em>
        </h1>
        <p className="lede">Nothing lives at this address. Go back home or track an application.</p>
        <div className="cta-row">
          <Link to="/" className="btn btn--solid">
            Back to home
          </Link>
          <Link to="/track" className="btn btn--line">
            Track an application
          </Link>
        </div>
      </div>
    </section>
  );
}
