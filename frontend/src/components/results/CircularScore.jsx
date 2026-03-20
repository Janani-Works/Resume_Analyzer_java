import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'

export function CircularScore({ value, label, size = 140 }) {
  const [displayed, setDisplayed] = useState(0)
  const r = (size - 18) / 2
  const circ = 2 * Math.PI * r
  const offset = circ - (value / 100) * circ
  const color = value >= 75 ? 'var(--green)' : value >= 50 ? '#D97706' : 'var(--red)'

  useEffect(() => {
    let start = null
    const run = ts => {
      if (!start) start = ts
      const p = Math.min((ts - start) / 1400, 1)
      const e = 1 - Math.pow(1 - p, 3)
      setDisplayed(Math.round(value * e))
      if (p < 1) requestAnimationFrame(run)
    }
    requestAnimationFrame(run)
  }, [value])

  return (
    <div className="flex flex-col items-center gap-2">
      <div className="relative" style={{ width: size, height: size }}>
        <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
          <circle cx={size/2} cy={size/2} r={r} className="ring-track" />
          <motion.circle cx={size/2} cy={size/2} r={r} className="ring-fill"
            stroke={color} strokeDasharray={circ}
            initial={{ strokeDashoffset: circ }}
            animate={{ strokeDashoffset: offset }}
            transition={{ duration: 1.5, ease: [0.4, 0, 0.2, 1] }} />
        </svg>
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className="font-semibold counter" style={{ fontSize: '2rem', color, fontFamily: 'Geist, sans-serif', lineHeight: 1 }}>{displayed}</span>
          <span className="text-xs" style={{ color: 'var(--ink-4)', fontFamily: 'Geist Mono, monospace' }}>/ 100</span>
        </div>
      </div>
      <span className="text-xs font-medium" style={{ color: 'var(--ink-3)', fontFamily: 'Geist Mono, monospace', letterSpacing: '0.08em', textTransform: 'uppercase' }}>{label}</span>
    </div>
  )
}
