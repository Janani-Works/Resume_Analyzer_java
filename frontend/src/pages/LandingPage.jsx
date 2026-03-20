import { motion } from 'framer-motion'
import { ArrowRight, Sparkles, Zap, Brain, Target, Download, Clock } from 'lucide-react'

const TICKER_ITEMS = [
  'ATS Optimization', 'Skill Gap Analysis', 'Domain Detection',
  'Fuzzy Matching', 'PDF Parsing', 'JD URL Scraping',
  'Match Scoring', '420+ Skills', '210+ Job Roles',
  'Keyword Density', 'Action Verb Check', 'Smart Suggestions',
]

const features = [
  { icon: Brain,    color: 'var(--ink)',   title: 'NLP Skill Extraction',  desc: 'Exact, synonym, and fuzzy matching across 420+ skills using Apache Commons Text.' },
  { icon: Target,   color: 'var(--green)', title: 'ATS Score (0–100)',      desc: 'Multi-signal scoring: skills, keywords, structure, quantifiers, action verbs.' },
  { icon: Zap,      color: 'var(--amber)', title: 'JD URL Scraper',         desc: 'Paste any job posting link — Jsoup extracts the full description automatically.' },
  { icon: Sparkles, color: 'var(--blue)',  title: '210+ Job Domains',       desc: 'Classifies your role from Junior Engineer to CTO across 50+ industries.' },
  { icon: Download, color: 'var(--red)',   title: 'Download Report',        desc: 'Export a full analysis report as a formatted text file for offline review.' },
  { icon: Clock,    color: 'var(--ink-3)', title: 'Analysis History',       desc: 'Every analysis saved locally. Track score improvements over time.' },
]

export function LandingPage({ onStart }) {
  return (
    <div style={{ background: 'var(--cream)', minHeight: '100vh' }}>
      {/* ── HERO ── */}
      <section className="max-w-6xl mx-auto px-6 pt-32 pb-20">
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
          className="max-w-2xl">
          {/* Eyebrow */}
          <div className="inline-flex items-center gap-2 mb-6">
            <span className="w-1.5 h-1.5 rounded-full" style={{ background: 'var(--green)' }} />
            <span className="text-xs font-medium tracking-wide"
              style={{ color: 'var(--ink-3)', fontFamily: 'Geist Mono, monospace' }}>
              JAVA SPRING BOOT · PDFBOX · JSOUP
            </span>
          </div>

          {/* Headline */}
          <h1 className="font-display leading-[1.08] mb-6"
            style={{ fontSize: 'clamp(42px, 6vw, 72px)', color: 'var(--ink)', fontFamily: 'Instrument Serif, serif' }}>
            Optimize your resume for{' '}
            <span style={{ fontStyle: 'italic', color: 'var(--ink-3)' }}>ATS and impact.</span>
          </h1>

          <p className="text-base mb-10 leading-relaxed" style={{ color: 'var(--ink-3)', maxWidth: '480px' }}>
            Upload your resume, paste any job description or URL — get an instant skill match score,
            gap analysis, and targeted suggestions in seconds.
          </p>

          <div className="flex items-center gap-3 flex-wrap">
            <button className="btn-primary" onClick={onStart}>
              Analyze Resume <ArrowRight className="w-4 h-4" />
            </button>
            <a href="#features"
              onClick={e => { e.preventDefault(); document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' }) }}
              className="text-sm font-medium transition-colors"
              style={{ color: 'var(--ink-3)', textDecoration: 'none' }}>
              How it works →
            </a>
          </div>
        </motion.div>

        {/* Stats */}
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.3 }}
          className="flex gap-12 mt-16 flex-wrap">
          {[['420+', 'Skills tracked'], ['210+', 'Job role profiles'], ['3×', 'Match strategies'], ['< 2s', 'Analysis time']].map(([n, l]) => (
            <div key={l}>
              <div className="text-2xl font-semibold counter" style={{ color: 'var(--ink)', fontFamily: 'Geist, sans-serif' }}>{n}</div>
              <div className="text-xs mt-0.5" style={{ color: 'var(--ink-4)' }}>{l}</div>
            </div>
          ))}
        </motion.div>
      </section>

      {/* ── TICKER ── */}
      <div className="ticker-wrap py-3 my-2">
        <div className="ticker-inner">
          {[...TICKER_ITEMS, ...TICKER_ITEMS].map((item, i) => (
            <span key={i} className="text-xs font-medium flex items-center gap-3"
              style={{ color: 'var(--ink-4)', fontFamily: 'Geist Mono, monospace' }}>
              {item}
              <span style={{ color: 'var(--border)' }}>·</span>
            </span>
          ))}
        </div>
      </div>

      {/* ── FEATURES ── */}
      <section id="features" className="max-w-6xl mx-auto px-6 py-20">
        <motion.div initial={{ opacity: 0, y: 12 }} whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }} className="mb-12">
          <span className="text-xs font-medium tracking-widest uppercase"
            style={{ color: 'var(--ink-4)', fontFamily: 'Geist Mono, monospace' }}>Features</span>
          <h2 className="font-display text-3xl mt-2"
            style={{ color: 'var(--ink)', fontFamily: 'Instrument Serif, serif' }}>
            Built for serious job seekers
          </h2>
        </motion.div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-px"
          style={{ background: 'var(--border)', border: '1px solid var(--border)', borderRadius: '16px', overflow: 'hidden' }}>
          {features.map((f, i) => {
            const Icon = f.icon
            return (
              <motion.div key={f.title}
                initial={{ opacity: 0 }} whileInView={{ opacity: 1 }}
                viewport={{ once: true }} transition={{ delay: i * 0.06 }}
                className="p-6 transition-colors"
                style={{ background: 'var(--cream)' }}
                onMouseEnter={e => e.currentTarget.style.background = '#fff'}
                onMouseLeave={e => e.currentTarget.style.background = 'var(--cream)'}
              >
                <div className="w-8 h-8 rounded-lg flex items-center justify-center mb-4"
                  style={{ background: 'var(--cream-2)', border: '1px solid var(--border)' }}>
                  <Icon className="w-4 h-4" style={{ color: f.color }} />
                </div>
                <h3 className="font-semibold text-sm mb-1.5" style={{ color: 'var(--ink)' }}>{f.title}</h3>
                <p className="text-sm leading-relaxed" style={{ color: 'var(--ink-4)' }}>{f.desc}</p>
              </motion.div>
            )
          })}
        </div>
      </section>

      {/* ── CTA STRIP ── */}
      <section className="max-w-6xl mx-auto px-6 pb-24">
        <motion.div initial={{ opacity: 0, y: 12 }} whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="rounded-2xl p-10 flex items-center justify-between flex-wrap gap-6"
          style={{ background: 'var(--ink)', color: '#fff' }}>
          <div>
            <h3 className="font-display text-2xl mb-1"
              style={{ fontFamily: 'Instrument Serif, serif', color: '#fff' }}>
              Ready to get matched?
            </h3>
            <p className="text-sm" style={{ color: 'rgba(255,255,255,0.5)' }}>
              Upload your resume and paste a job description to get started.
            </p>
          </div>
          <button onClick={onStart}
            className="flex items-center gap-2 px-6 py-3 rounded-xl font-semibold text-sm transition-all"
            style={{ background: '#fff', color: 'var(--ink)', fontFamily: 'Geist, sans-serif' }}
            onMouseEnter={e => e.currentTarget.style.background = 'var(--cream-2)'}
            onMouseLeave={e => e.currentTarget.style.background = '#fff'}>
            Start Analyzing <ArrowRight className="w-4 h-4" />
          </button>
        </motion.div>
      </section>
    </div>
  )
}
