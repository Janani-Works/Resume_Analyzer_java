# Prism — AI Resume Analyzer
### Java Spring Boot Backend · React + Vite + Framer Motion Frontend

---

## 📁 Project Structure

```
resume-analyzer/
├── backend/                          # Spring Boot (Java 17)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/resumeai/
│       │   ├── ResumeAnalyzerApplication.java
│       │   ├── config/CorsConfig.java
│       │   ├── controller/AnalysisController.java
│       │   ├── dto/AnalysisResponse.java
│       │   ├── model/{Skill,JobField,SkillsDatabase,JobFieldsDatabase}.java
│       │   └── service/
│       │       ├── AnalysisService.java          ← orchestrates everything
│       │       ├── DataLoaderService.java         ← loads JSON at startup
│       │       ├── ResumeParserService.java       ← PDFBox + Apache POI
│       │       ├── JdScraperService.java          ← Jsoup URL scraper
│       │       ├── SkillMatcherService.java       ← exact+synonym+fuzzy
│       │       ├── DomainClassifierService.java   ← 210 role classifier
│       │       └── ScoringService.java            ← ATS scoring
│       └── resources/
│           ├── application.properties
│           ├── skills.json           ← 420 skills with synonyms
│           └── job_fields.json       ← 210 job roles
├── frontend/                         # React + Vite + Tailwind
│   ├── package.json
│   ├── vite.config.js
│   ├── tailwind.config.js
│   ├── index.html
│   └── src/
│       ├── App.jsx
│       ├── main.jsx
│       ├── index.css
│       ├── hooks/
│       │   ├── useAnalysis.js        ← API calls
│       │   ├── useToast.js
│       │   └── useHistory.js         ← localStorage persistence
│       ├── components/
│       │   ├── ui/{Navbar,Toast}.jsx
│       │   └── results/{CircularScore,ScoreBar,SkillsChart}.jsx
│       └── pages/
│           ├── LandingPage.jsx
│           ├── AnalyzerPage.jsx
│           ├── ResultsPage.jsx
│           └── HistoryPage.jsx
└── data/
    ├── skills.json                   ← source of truth (420 skills)
    └── job_fields.json               ← source of truth (210 roles)
```

---

## ⚙️ Backend Setup (Spring Boot)

### Prerequisites
- Java 17+
- Maven 3.6+

### Run
```bash
cd backend
mvn spring-boot:run
```
API runs at: **http://localhost:8080**

### Verify
```bash
curl http://localhost:8080/api/health
# {"status":"UP","skillsLoaded":420,"jobFieldsLoaded":210}
```

---

## 🎨 Frontend Setup (React + Vite)

### Prerequisites
- Node.js 18+

### Install & Run
```bash
cd frontend
npm install
npm run dev
```
App runs at: **http://localhost:5173**

The Vite proxy forwards `/api/*` → `http://localhost:8080`.

### Build for production
```bash
npm run build
# Output in frontend/dist/
```

---

## 📡 API Reference

### POST /api/analyze
**Content-Type:** `multipart/form-data`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| resumeFile | File | Either/Or | PDF, DOCX, DOC, or TXT |
| resumeText | String | Either/Or | Pasted resume text |
| jdText | String | Either/Or | Job description text |
| jdUrl | String | Either/Or | Job posting URL (Jsoup scrapes it) |

**Response:**
```json
{
  "matchScore": 85,
  "atsScore": 72,
  "domainMatch": "High",
  "jobField": "Senior ML Engineer",
  "jobDomain": "AI/ML",
  "matchedSkills": [{ "name": "Python", "category": "Programming Language", "weight": 9, "matchType": "EXACT" }],
  "missingSkills": [{ "name": "Kubernetes", "category": "DevOps", "weight": 9, "matchType": "EXACT" }],
  "resumeSkills": [...],
  "jdSkills": [...],
  "suggestions": ["🎯 Add these high-priority missing skills: Kubernetes, Terraform"],
  "keywordDensity": 43.2,
  "analysisId": "a1b2c3d4e5f6",
  "processingTimeMs": 234
}
```

### GET /api/health
Returns: `{ "status": "UP", "skillsLoaded": 420, "jobFieldsLoaded": 210 }`

### GET /api/skills?category=DevOps&limit=20
Returns filtered skill list.

### GET /api/job-fields?domain=AI/ML
Returns filtered job fields.

---

## 🧠 How Matching Works

### Skill Extraction (3 strategies)
1. **Exact Match** — Direct regex word-boundary match on skill names
2. **Synonym Match** — Regex match on all synonyms from skills.json → maps to canonical name
3. **Fuzzy Match** — Jaro-Winkler similarity (≥0.92) + Levenshtein distance (≤2) on tokens & bigrams

### ATS Scoring (0–100)
| Component | Max Points |
|-----------|-----------|
| Skill match ratio | 40 |
| Keyword density overlap | 20 |
| Resume section structure | 20 |
| Quantified achievements | 10 |
| Action verbs | 10 |

### Domain Classification
Scores all 210 job roles by:
- Title mention in JD text (+30)
- Domain mention (+10)
- Key skill overlap (+5 each)

---

## 🎨 Frontend Features
- **Dark glassmorphism UI** with noise texture + radial gradient mesh
- **Framer Motion** page transitions, staggered reveals, hover effects
- **Animated circular ATS ring** (SVG stroke-dashoffset animation)
- **Recharts** skills-by-category bar chart
- **Drag & drop** resume upload
- **Skeleton loader** during analysis
- **Toast notifications** with auto-dismiss
- **Analysis history** persisted in localStorage
- **Download report** as formatted .txt file
- Fully responsive (mobile + desktop)
