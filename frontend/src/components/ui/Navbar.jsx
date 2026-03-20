import { FileSearch } from 'lucide-react'

export function Navbar({ page, onNav }) {
  return (
    <nav style={{ background: 'var(--cream)', borderBottom: '1px solid var(--border)' }}
      className="fixed top-0 left-0 right-0 z-50">
      <div className="max-w-6xl mx-auto px-6 h-14 flex items-center justify-between">
        <button onClick={() => onNav('landing')} className="flex items-center gap-2.5">
          <div className="w-7 h-7 rounded-lg flex items-center justify-center"
            style={{ background: 'var(--ink)' }}>
            <FileSearch className="w-3.5 h-3.5 text-white" />
          </div>
          <span className="font-semibold text-sm" style={{ color: 'var(--ink)', fontFamily: 'Geist, sans-serif' }}>
            Resume Analyzer
          </span>
        </button>
        <div className="flex items-center gap-1">
          <NavLink active={page === 'analyzer'} onClick={() => onNav('analyzer')}>Analyze</NavLink>
          <NavLink active={page === 'history'}  onClick={() => onNav('history')}>History</NavLink>
        </div>
      </div>
    </nav>
  )
}

function NavLink({ children, active, onClick }) {
  return (
    <button onClick={onClick}
      className="px-3 py-1.5 rounded-lg text-sm font-medium transition-all"
      style={{
        color: active ? 'var(--ink)' : 'var(--ink-3)',
        background: active ? 'var(--cream-2)' : 'transparent',
        fontFamily: 'Geist, sans-serif',
      }}>
      {children}
    </button>
  )
}
