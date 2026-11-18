import { Route, Routes } from 'react-router';
import { Shell } from './components/Shell';
import { HomePage } from './pages/HomePage';
import { SchemesPage } from './pages/SchemesPage';
import { ApplyPage } from './pages/ApplyPage';
import { TrackPage } from './pages/TrackPage';
import { AdminPage } from './pages/AdminPage';
import { NotFoundPage } from './pages/NotFoundPage';

// Inner pages share one content width and vertical rhythm; the home page lays itself out.
const inner = (page: React.ReactNode) => <div className="wrap page">{page}</div>;

export function App() {
  return (
    <Shell>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/schemes" element={inner(<SchemesPage />)} />
        <Route path="/apply" element={inner(<ApplyPage />)} />
        <Route path="/track" element={inner(<TrackPage />)} />
        <Route path="/admin" element={inner(<AdminPage />)} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Shell>
  );
}
