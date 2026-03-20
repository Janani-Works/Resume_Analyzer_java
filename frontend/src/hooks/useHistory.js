import { useState, useCallback } from 'react'

const STORAGE_KEY = 'prism_history'
const MAX_ITEMS = 20

export function useHistory() {
  const [history, setHistory] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
    } catch {
      return []
    }
  })

  const addToHistory = useCallback((result) => {
    const record = {
      id: result.analysisId,
      timestamp: Date.now(),
      jobField: result.jobField,
      matchScore: result.matchScore,
      atsScore: result.atsScore,
      matchedCount: result.matchedSkills?.length ?? 0,
      missingCount: result.missingSkills?.length ?? 0,
    }
    setHistory(prev => {
      const next = [record, ...prev].slice(0, MAX_ITEMS)
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
      return next
    })
  }, [])

  const clearHistory = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY)
    setHistory([])
  }, [])

  return { history, addToHistory, clearHistory }
}
