import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts'

const COLORS = ['#1A1814','#1A6B4A','#C0392B','#1E3A8A','#92400E','#6B6760','#374151','#065F46']

const Tip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null
  return (
    <div className="card px-3 py-2 text-xs">
      <p style={{ color: 'var(--ink-3)' }}>{label}</p>
      <p className="font-semibold" style={{ color: 'var(--ink)', fontFamily: 'Geist Mono, monospace' }}>{payload[0].value} skills</p>
    </div>
  )
}

export function SkillsChart({ matched, missing }) {
  const map = {}
  ;[...matched, ...missing].forEach(s => {
    const c = (s.category || 'Other').split('/')[0].trim()
    map[c] = (map[c] || 0) + 1
  })
  const data = Object.entries(map).sort((a,b) => b[1]-a[1]).slice(0,7)
    .map(([name, count]) => ({ name: name.length > 10 ? name.slice(0,10)+'…' : name, count }))

  if (!data.length) return <div className="flex items-center justify-center h-40 text-sm" style={{ color: 'var(--ink-4)' }}>No data</div>

  return (
    <ResponsiveContainer width="100%" height={200}>
      <BarChart data={data} margin={{ top: 4, right: 4, bottom: 0, left: -24 }}>
        <XAxis dataKey="name" tick={{ fill: 'var(--ink-4)', fontSize: 10, fontFamily: 'Geist Mono, monospace' }} axisLine={false} tickLine={false} />
        <YAxis tick={{ fill: 'var(--ink-4)', fontSize: 10, fontFamily: 'Geist Mono, monospace' }} axisLine={false} tickLine={false} allowDecimals={false} />
        <Tooltip content={<Tip />} cursor={{ fill: 'var(--cream-2)' }} />
        <Bar dataKey="count" radius={[3,3,0,0]}>
          {data.map((_,i) => <Cell key={i} fill={COLORS[i % COLORS.length]} fillOpacity={0.9} />)}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  )
}
