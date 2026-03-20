import { useState, useRef, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Upload, FileText, Link, X, ArrowRight, Loader2 } from 'lucide-react'

export function AnalyzerPage({ onAnalyze, loading }) {
  const [resumeFile, setResumeFile] = useState(null)
  const [resumeText, setResumeText] = useState('')
  const [jdText, setJdText]         = useState('')
  const [jdUrl, setJdUrl]           = useState('')
  const [jdTab, setJdTab]           = useState('text')
  const [dragging, setDragging]     = useState(false)
  const fileRef = useRef()

  const handleFile = useCallback(file => {
    if (!file) return
    if (!file.name.match(/\.(pdf|docx|doc|txt)$/i)) return
    setResumeFile(file)
  }, [])

  const handleDrop = useCallback(e => {
    e.preventDefault(); setDragging(false)
    handleFile(e.dataTransfer.files[0])
  }, [handleFile])

  const canSubmit = (resumeFile || resumeText.trim()) &&
    ((jdTab === 'text' && jdText.trim()) || (jdTab === 'url' && jdUrl.trim())) && !loading

  const submit = () => {
    if (!canSubmit) return
    onAnalyze({
      resumeFile: resumeFile || null,
      resumeText: resumeFile ? '' : resumeText,
      jdText: jdTab === 'text' ? jdText : '',
      jdUrl:  jdTab === 'url'  ? jdUrl  : '',
    })
  }

  return (
    <div style={{ background: 'var(--cream)', minHeight: '100vh' }} className="pt-20 pb-16 px-6">
      <div className="max-w-6xl mx-auto">

        {/* Header */}
        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}
          className="pt-10 pb-8">
          <span className="text-xs font-medium tracking-widest uppercase block mb-3"
            style={{ color: 'var(--ink-4)', fontFamily: 'Geist Mono, monospace' }}>
            AI Resume Analyzer
          </span>
          <h1 className="font-display leading-tight mb-2"
            style={{ fontSize: '2.25rem', color: 'var(--ink)', fontFamily: 'Instrument Serif, serif' }}>
            Optimize your resume for ATS and impact.
          </h1>
          <p className="text-sm" style={{ color: 'var(--ink-3)' }}>
            Upload your resume and paste a job description to get an instant match report.
          </p>
        </motion.div>

        {/* Two-column layout */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">

          {/* ── LEFT: Resume ── */}
          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.05 }} className="card overflow-hidden">

            <div className="px-5 py-4 flex items-center justify-between"
              style={{ borderBottom: '1px solid var(--border)' }}>
              <div className="flex items-center gap-2.5">
                <div className="w-6 h-6 rounded-md flex items-center justify-center"
                  style={{ background: 'var(--cream-2)', border: '1px solid var(--border)' }}>
                  <FileText className="w-3.5 h-3.5" style={{ color: 'var(--ink-2)' }} />
                </div>
                <span className="font-semibold text-sm" style={{ color: 'var(--ink)' }}>Upload Resume</span>
              </div>
              <span className="text-xs px-2 py-0.5 rounded-full font-medium"
                style={{ background: 'var(--red-light)', color: 'var(--red)', fontFamily: 'Geist Mono, monospace' }}>
                PDF Only*
              </span>
            </div>

            {/* Drop zone */}
            <div className={`upload-zone m-4 rounded-xl p-8 text-center ${dragging ? 'drag-active' : ''} ${resumeFile ? 'has-file' : ''}`}
              onDragOver={e => { e.preventDefault(); setDragging(true) }}
              onDragLeave={() => setDragging(false)}
              onDrop={handleDrop}
              onClick={() => !resumeFile && fileRef.current?.click()}>
              <input ref={fileRef} type="file" accept=".pdf,.docx,.doc,.txt" className="hidden"
                onChange={e => handleFile(e.target.files[0])} />

              <AnimatePresence mode="wait">
                {resumeFile ? (
                  <motion.div key="file" initial={{ opacity: 0, scale: 0.96 }} animate={{ opacity: 1, scale: 1 }}
                    className="flex flex-col items-center gap-2">
                    <div className="w-10 h-10 rounded-xl flex items-center justify-center"
                      style={{ background: 'var(--green-light)', border: '1px solid #B7DFC8' }}>
                      <FileText className="w-5 h-5" style={{ color: 'var(--green)' }} />
                    </div>
                    <p className="font-medium text-sm" style={{ color: 'var(--green)' }}>{resumeFile.name}</p>
                    <p className="text-xs" style={{ color: 'var(--ink-4)' }}>{(resumeFile.size / 1024).toFixed(1)} KB</p>
                    <button onClick={e => { e.stopPropagation(); setResumeFile(null) }}
                      className="flex items-center gap-1 text-xs mt-1 transition-colors"
                      style={{ color: 'var(--ink-4)' }}
                      onMouseEnter={e => e.currentTarget.style.color = 'var(--red)'}
                      onMouseLeave={e => e.currentTarget.style.color = 'var(--ink-4)'}>
                      <X className="w-3 h-3" /> Remove
                    </button>
                  </motion.div>
                ) : (
                  <motion.div key="empty" initial={{ opacity: 0 }} animate={{ opacity: 1 }}
                    className="flex flex-col items-center gap-2">
                    <div className="w-10 h-10 rounded-xl flex items-center justify-center mb-1"
                      style={{ background: 'var(--cream-2)', border: '1px solid var(--border)' }}>
                      <Upload className="w-5 h-5" style={{ color: 'var(--ink-3)' }} />
                    </div>
                    <p className="font-medium text-sm" style={{ color: 'var(--ink-2)' }}>Upload Resume</p>
                    <p className="text-xs" style={{ color: 'var(--ink-4)' }}>PDF Only · Max 5MB</p>
                    <div className="flex gap-1.5 mt-2">
                      {['PDF', 'DOCX', 'TXT'].map(f => (
                        <span key={f} className="text-xs px-2 py-0.5 rounded"
                          style={{ background: 'var(--cream-3)', color: 'var(--ink-3)', fontFamily: 'Geist Mono, monospace' }}>{f}</span>
                      ))}
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>

            {/* Or paste */}
            <div className="px-4 pb-5">
              <div className="flex items-center gap-3 mb-3">
                <div className="flex-1 h-px" style={{ background: 'var(--border)' }} />
                <span className="text-xs" style={{ color: 'var(--ink-4)', fontFamily: 'Geist Mono, monospace' }}>or paste text</span>
                <div className="flex-1 h-px" style={{ background: 'var(--border)' }} />
              </div>
              <textarea value={resumeText} onChange={e => setResumeText(e.target.value)}
                placeholder="Paste your resume content here..."
                rows={5} className="input-base" />
            </div>
          </motion.div>

          {/* ── RIGHT: JD ── */}
          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }} className="card overflow-hidden">

            <div className="px-5 py-4 flex items-center justify-between"
              style={{ borderBottom: '1px solid var(--border)' }}>
              <div className="flex items-center gap-2.5">
                <div className="w-6 h-6 rounded-md flex items-center justify-center"
                  style={{ background: 'var(--cream-2)', border: '1px solid var(--border)' }}>
                  <FileText className="w-3.5 h-3.5" style={{ color: 'var(--ink-2)' }} />
                </div>
                <span className="font-semibold text-sm" style={{ color: 'var(--ink)' }}>Paste the Job Description</span>
              </div>
              <span className="text-xs px-2 py-0.5 rounded-full font-medium"
                style={{ background: 'var(--red-light)', color: 'var(--red)', fontFamily: 'Geist Mono, monospace' }}>
                Required*
              </span>
            </div>

            {/* Tabs */}
            <div className="flex gap-1.5 px-4 pt-4">
              <button className={`tab-btn ${jdTab === 'text' ? 'active' : ''}`} onClick={() => setJdTab('text')}>
                Paste Text
              </button>
              <button className={`tab-btn ${jdTab === 'url' ? 'active' : ''}`} onClick={() => setJdTab('url')}>
                <Link className="w-3 h-3 inline mr-1" />
                URL Scrape
              </button>
            </div>

            <div className="px-4 pt-3 pb-5">
              <AnimatePresence mode="wait">
                {jdTab === 'text' ? (
                  <motion.div key="text" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
                    <textarea value={jdText} onChange={e => setJdText(e.target.value)}
                      placeholder="Paste the job description here..."
                      className="input-base" style={{ minHeight: '280px' }} />
                    <div className="text-right text-xs mt-1"
                      style={{ color: 'var(--ink-4)', fontFamily: 'Geist Mono, monospace' }}>
                      {jdText.length.toLocaleString()} chars
                    </div>
                  </motion.div>
                ) : (
                  <motion.div key="url" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                    className="flex flex-col gap-3">
                    <div className="flex items-center gap-2 input-base" style={{ padding: '10px 14px' }}>
                      <Link className="w-4 h-4 flex-shrink-0" style={{ color: 'var(--ink-4)' }} />
                      <input type="url" value={jdUrl} onChange={e => setJdUrl(e.target.value)}
                        placeholder="https://linkedin.com/jobs/view/..."
                        className="flex-1 bg-transparent outline-none text-sm"
                        style={{ color: 'var(--ink)', fontFamily: 'Geist, sans-serif' }} />
                      {jdUrl && <button onClick={() => setJdUrl('')}><X className="w-3.5 h-3.5" style={{ color: 'var(--ink-4)' }} /></button>}
                    </div>
                    <div className="rounded-xl p-4 text-sm leading-relaxed"
                      style={{ background: 'var(--cream-2)', border: '1px solid var(--border)', color: 'var(--ink-3)' }}>
                      Paste any job posting URL — LinkedIn, Indeed, Glassdoor, Greenhouse, or Lever.
                      Jsoup will automatically extract the job description.
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </motion.div>
        </div>

        {/* ── Analyze Button ── */}
        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }} className="flex flex-col items-center mt-8 gap-3">
          <button className="btn-primary" style={{ fontSize: '15px', padding: '14px 40px', borderRadius: '12px' }}
            disabled={!canSubmit} onClick={submit}>
            {loading
              ? <><Loader2 className="w-4 h-4 spinner" /> Analyzing...</>
              : <>Analyze Resume <ArrowRight className="w-4 h-4" /></>
            }
          </button>
          {loading && (
            <p className="text-xs" style={{ color: 'var(--ink-4)', fontFamily: 'Geist Mono, monospace' }}>
              Extracting skills · Matching domains · Computing ATS score...
            </p>
          )}
        </motion.div>
      </div>
    </div>
  )
}
