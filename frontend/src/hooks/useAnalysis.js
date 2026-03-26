import { useState, useCallback } from 'react'
import axios from 'axios'

// ✅ Your Railway backend URL
const API = "https://resume-analyzer-production.up.railway.app/api"

export function useAnalysis() {
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const analyze = useCallback(async ({ resumeFile, resumeText, jdText, jdUrl }) => {
    setLoading(true)
    setError(null)

    try {
      // ✅ Validation (frontend safety)
      if (!resumeFile && !resumeText) {
        throw new Error("Please upload a resume or paste resume text")
      }

      if (!jdText && !jdUrl) {
        throw new Error("Please provide job description or URL")
      }

      // ✅ Create FormData
      const form = new FormData()

      if (resumeFile) form.append('resumeFile', resumeFile)
      if (resumeText) form.append('resumeText', resumeText)
      if (jdText) form.append('jdText', jdText)
      if (jdUrl) form.append('jdUrl', jdUrl)

      // ✅ Send request (DO NOT manually set Content-Type)
      const response = await axios.post(`${API}/analyze`, form, {
        timeout: 30000
      })

      // ✅ Store result
      setResult(response.data)
      return response.data

    } catch (err) {
      const msg =
        err.response?.data?.error ||
        err.message ||
        'Analysis failed'

      setError(msg)
      throw new Error(msg)

    } finally {
      setLoading(false)
    }
  }, [])

  const reset = useCallback(() => {
    setResult(null)
    setError(null)
  }, [])

  return { result, loading, error, analyze, reset }
}