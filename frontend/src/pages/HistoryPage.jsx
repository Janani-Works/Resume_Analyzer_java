import { motion } from 'framer-motion'
import { Trash2, BarChart3, Clock, TrendingUp } from 'lucide-react'

export function HistoryPage({ history, onClear }) {
  const color = s => s >= 75 ? 'var(--green)' : s >= 50 ? '#D97706' : 'var(--red)'
  const bg    = s => s >= 75 ? 'var(--green-light)' : s >= 50 ? 'var(--amber-light)' : 'var(--red-light)'
  const bdr   = s => s >= 75 ? '#B7DFC8' : s >= 50 ? '#FDE68A' : '#F5C6C2'

  return (
    <div style={{ background: 'var(--cream)', minHeight: '100vh' }} className="pt-20 pb-16 px-6">
      <div className="max-w-3xl mx-auto">

        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}
          className="flex items-start justify-between pt-8 pb-8 flex-wrap gap-4">
          <div>
            <span className="text-xs font-mono uppercase tracking-widest block mb-2"
              style={{ color: 'var(--ink-4)' }}>Analysis History</span>
            <h1 className="font-display text-3xl" style={{ color: 'var(--ink)', fontFamily: 'Instrument Serif, serif' }}>
              Your past analyses
            </h1>
            <p className="text-sm mt-1" style={{ color: 'var(--ink-3)' }}>
              {history.length} {history.length === 1 ? 'analysis' : 'analyses'} saved locally
            </p>
          </div>
          {history.length > 0 && (
            <button onClick={onClear}
              className="flex items-center gap-1.5 text-sm font-medium px-3 py-2 rounded-lg transition-all"
              style={{ color: 'var(--red)', background: 'var(--red-light)', border: '1px solid #F5C6C2', cursor: 'pointer', fontFamily: 'Geist, sans-serif' }}>
              <Trash2 className="w-3.5 h-3.5" /> Clear All
            </button>
          )}
        </motion.div>

        {history.length === 0 ? (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}
            className="card p-16 text-center">
            <BarChart3 className="w-10 h-10 mx-auto mb-4" style={{ color: 'var(--ink-4)', opacity: 0.4 }} />
            <h3 className="font-semibold text-base mb-1" style={{ color: 'var(--ink-2)' }}>No analyses yet</h3>
            <p className="text-sm" style={{ color: 'var(--ink-4)' }}>Your analysis history will appear here once you run your first analysis.</p>
          </motion.div>
        ) : (
          <div className="flex flex-col gap-3">
            {history.map((h, i) => (
              <motion.div key={h.id}
                initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.04 }}
                className="card p-4 flex items-center gap-4 transition-all"
                onMouseEnter={e => e.currentTarget.style.boxShadow = 'var(--shadow-md)'}
                onMouseLeave={e => e.currentTarget.style.boxShadow = 'var(--shadow)'}>

                {/* Score badge */}
                <div className="w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0 font-semibold text-sm counter"
                  style={{ background: bg(h.matchScore), color: color(h.matchScore), border: `1px solid ${bdr(h.matchScore)}`, fontFamily: 'Geist, sans-serif' }}>
                  {h.matchScore}%
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-sm truncate" style={{ color: 'var(--ink)' }}>
                    {h.jobField || 'Unknown Role'}
                  </p>
                  <div className="flex items-center gap-2 mt-0.5 text-xs flex-wrap"
                    style={{ color: 'var(--ink-4)', fontFamily: 'Geist Mono, monospace' }}>
                    <Clock className="w-3 h-3" />
                    {new Date(h.timestamp).toLocaleDateString('en', { month: 'short', day: 'numeric', year: 'numeric' })}
                    <span>·</span>
                    <TrendingUp className="w-3 h-3" />
                    ATS: {h.atsScore}
                    <span>·</span>
                    ID: {h.id?.slice(0, 8)}
                  </div>
                </div>

                {/* Counts */}
                <div className="flex gap-2 flex-shrink-0">
                  <span className="tag-matched text-xs font-mono px-2 py-0.5 rounded-lg">
                    ✓ {h.matchedCount}
                  </span>
                  <span className="tag-missing text-xs font-mono px-2 py-0.5 rounded-lg">
                    ✗ {h.missingCount}
                  </span>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
