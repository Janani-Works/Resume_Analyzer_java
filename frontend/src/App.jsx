import { useState, useCallback } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import { Navbar }      from './components/ui/Navbar'
import { ToastStack }  from './components/ui/Toast'
import { LandingPage } from './pages/LandingPage'
import { AnalyzerPage} from './pages/AnalyzerPage'
import { ResultsPage } from './pages/ResultsPage'
import { HistoryPage } from './pages/HistoryPage'
import { useAnalysis } from './hooks/useAnalysis'
import { useToast }    from './hooks/useToast'
import { useHistory }  from './hooks/useHistory'

const fade = {
  initial: { opacity: 0, y: 10 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.3 } },
  exit:    { opacity: 0, y: -6, transition: { duration: 0.2 } },
}

export default function App() {
  const [page, setPage] = useState('landing')

  const { result, loading, analyze, reset } = useAnalysis()
  const { toasts, addToast, removeToast }   = useToast()
  const { history, addToHistory, clearHistory } = useHistory()

  const handleAnalyze = useCallback(async (inputs) => {
    try {
      const data = await analyze(inputs)

      addToHistory(data)
      addToast(`Analysis complete — ${data.matchScore}% match`, 'success')

      setPage('results')

    } catch (err) {
      addToast(
        err.message || 'Analysis failed. Please check backend connection.',
        'error',
        6000
      )
    }
  }, [analyze, addToHistory, addToast])

  return (
    <div style={{ background: 'var(--cream)', minHeight: '100vh' }}>
      <Navbar
        page={page}
        onNav={(p) => {
          if (p !== 'results') reset()
          setPage(p)
        }}
      />

      <AnimatePresence mode="wait">
        {page === 'landing' && (
          <motion.div key="landing" {...fade}>
            <LandingPage onStart={() => setPage('analyzer')} />
          </motion.div>
        )}

        {page === 'analyzer' && (
          <motion.div key="analyzer" {...fade}>
            <AnalyzerPage
              onAnalyze={handleAnalyze}
              loading={loading}
            />
          </motion.div>
        )}

        {page === 'results' && result && (
          <motion.div key="results" {...fade}>
            <ResultsPage
              result={result}
              onBack={() => setPage('analyzer')}
            />
          </motion.div>
        )}

        {page === 'history' && (
          <motion.div key="history" {...fade}>
            <HistoryPage
              history={history}
              onClear={clearHistory}
            />
          </motion.div>
        )}
      </AnimatePresence>

      <ToastStack toasts={toasts} onRemove={removeToast} />
    </div>
  )
}