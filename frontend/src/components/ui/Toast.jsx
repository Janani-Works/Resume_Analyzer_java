import { AnimatePresence, motion } from 'framer-motion'
import { CheckCircle, XCircle, Info, X } from 'lucide-react'

const cfg = {
  success: { icon: <CheckCircle className="w-4 h-4" style={{color:'var(--green)'}}/>, bar: 'var(--green)' },
  error:   { icon: <XCircle    className="w-4 h-4" style={{color:'var(--red)'}}  />, bar: 'var(--red)'   },
  info:    { icon: <Info       className="w-4 h-4" style={{color:'var(--ink-2)'}}/>, bar: 'var(--ink-2)' },
}

export function ToastStack({ toasts, onRemove }) {
  return (
    <div className="fixed bottom-6 right-6 z-50 flex flex-col gap-2">
      <AnimatePresence>
        {toasts.map(t => (
          <motion.div key={t.id}
            initial={{ opacity: 0, y: 16, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 8, scale: 0.97 }}
            transition={{ duration: 0.22 }}
            className="card flex items-center gap-3 px-4 py-3 min-w-[280px] max-w-sm"
            style={{ borderLeft: `3px solid ${cfg[t.type]?.bar}` }}
          >
            {cfg[t.type]?.icon}
            <span className="text-sm flex-1" style={{ color: 'var(--ink-2)', fontFamily: 'Geist, sans-serif' }}>
              {t.message}
            </span>
            <button onClick={() => onRemove(t.id)} style={{ color: 'var(--ink-4)' }}
              className="hover:opacity-70 transition-opacity">
              <X className="w-3.5 h-3.5" />
            </button>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  )
}
