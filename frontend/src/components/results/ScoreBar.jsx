import { motion } from 'framer-motion'
import { useEffect, useState } from 'react'

export function ScoreBar({ value, label, sublabel, suffix = '%' }) {
  const [displayed, setDisplayed] = useState(0)
  const color = value >= 75 ? 'var(--green)' : value >= 50 ? '#D97706' : 'var(--red)'

  useEffect(() => {
    let start = null
    const run = ts => {
      if (!start) start = ts
      const p = Math.min((ts - start) / 1200, 1)
      const e = 1 - Math.pow(1 - p, 3)
      setDisplayed(Math.round(value * e * 10) / 10)
      if (p < 1) requestAnimationFrame(run)
    }
    requestAnimationFrame(run)
  }, [value])

  return (
    <div>
      <div className="flex items-end justify-between mb-1.5">
        <div>
          <p className="text-xs font-medium" style={{ color: 'var(--ink-3)' }}>{label}</p>
          {sublabel && <p className="text-xs mt-0.5" style={{ color: 'var(--ink-4)' }}>{sublabel}</p>}
        </div>
        <span className="font-semibold counter text-xl" style={{ color, fontFamily: 'Geist, sans-serif' }}>
          {displayed}{suffix}
        </span>
      </div>
      <div className="progress-track">
        <motion.div className="progress-fill" style={{ background: color }}
          initial={{ width: 0 }}
          animate={{ width: `${Math.min((value / 100) * 100, 100)}%` }}
          transition={{ duration: 1.2, ease: [0.4, 0, 0.2, 1] }} />
      </div>
    </div>
  )
}
