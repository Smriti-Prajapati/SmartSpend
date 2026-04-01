import React, { useEffect, useState } from 'react';
import { Doughnut, Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend } from 'chart.js';
import api from '../services/api';
import { useCurrency } from '../context/CurrencyContext';
import './Insights.css';

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend);

export default function Insights() {
  const [data, setData]     = useState(null);
  const [loading, setLoading] = useState(true);
  const { fmt } = useCurrency();

  useEffect(() => {
    api.get('/insights').then(r => { setData(r.data); setLoading(false); }).catch(() => setLoading(false));
  }, []);

  if (loading) return <div className="page-loader"><div className="spinner" /></div>;
  if (!data)   return <div className="empty-state">Failed to load insights.</div>;

  const doughnutData = {
    labels: data.topCategories?.map(c => c.category) || [],
    datasets: [{
      data: data.topCategories?.map(c => c.amount) || [],
      backgroundColor: data.topCategories?.map(c => c.color) || [],
      borderWidth: 2, borderColor: '#fff',
    }],
  };

  const trendLabels = Object.keys(data.categoryTrends || {}).slice(0, 5);
  const months = ['5mo ago','4mo ago','3mo ago','2mo ago','Last mo','This mo'];
  const colors  = ['#3b82f6','#10b981','#f59e0b','#ef4444','#8b5cf6'];
  const trendData = {
    labels: months,
    datasets: trendLabels.map((cat, i) => ({
      label: cat,
      data: data.categoryTrends[cat],
      backgroundColor: colors[i % colors.length],
      borderRadius: 4,
    })),
  };

  const chartOpts = { responsive: true, plugins: { legend: { position: 'bottom', labels: { font: { family: 'Inter' }, padding: 14 } } } };

  return (
    <div className="insights-page fade-in">
      {/* Month comparison */}
      <div className="insight-cards">
        <div className="insight-card">
          <div className="ic-label">This Month</div>
          <div className="ic-value">{fmt(data.currentMonthExpenses)}</div>
          <div className={`ic-change ${data.changeDirection === 'UP' ? 'up' : 'down'}`}>
            {data.changeDirection === 'UP' ? '↑' : '↓'} {data.changePercent?.toFixed(1)}% vs last month
          </div>
        </div>
        <div className="insight-card">
          <div className="ic-label">Last Month</div>
          <div className="ic-value">{fmt(data.previousMonthExpenses)}</div>
        </div>
        <div className="insight-card">
          <div className="ic-label">Daily Average</div>
          <div className="ic-value">{fmt(data.dailyAverage)}</div>
        </div>
        {data.highestExpense && (
          <div className="insight-card">
            <div className="ic-label">Biggest Expense</div>
            <div className="ic-value">{fmt(data.highestExpense.amount)}</div>
            <div className="ic-sub">{data.highestExpense.title}</div>
          </div>
        )}
      </div>

      {/* Alerts */}
      {data.overspendingAlerts?.length > 0 && (
        <div className="alerts-box">
          <h3 className="section-title">⚠️ Spending Alerts</h3>
          {data.overspendingAlerts.map((a, i) => (
            <div key={i} className="alert-item">{a}</div>
          ))}
        </div>
      )}

      {/* Suggestions */}
      <div className="suggestions-box">
        <h3 className="section-title">💡 Smart Suggestions</h3>
        {data.suggestions?.map((s, i) => (
          <div key={i} className="suggestion-item">
            <span className="suggestion-dot" />
            {s}
          </div>
        ))}
      </div>

      {/* Charts */}
      <div className="insight-charts">
        <div className="chart-card">
          <h3 className="chart-title">Category Breakdown</h3>
          {doughnutData.labels.length > 0
            ? <Doughnut data={doughnutData} options={chartOpts} />
            : <div className="empty-chart">No data this month</div>}
        </div>
        <div className="chart-card">
          <h3 className="chart-title">6-Month Category Trends</h3>
          {trendLabels.length > 0
            ? <Bar data={trendData} options={{ ...chartOpts, scales: { x: { stacked: true, grid: { display: false } }, y: { stacked: true } } }} />
            : <div className="empty-chart">Not enough data yet</div>}
        </div>
      </div>

      {/* Top categories */}
      <div className="top-cats-card">
        <h3 className="chart-title">Top Spending Categories</h3>
        <div className="cat-list">
          {data.topCategories?.map((c, i) => (
            <div key={i} className="cat-row">
              <div className="cat-row-left">
                <span className="cat-emoji">{c.icon}</span>
                <span className="cat-name">{c.category}</span>
              </div>
              <div className="cat-row-right">
                <div className="cat-bar-wrap">
                  <div className="cat-bar-fill" style={{ width: `${c.percentage}%`, background: c.color }} />
                </div>
                <span className="cat-pct">{c.percentage?.toFixed(1)}%</span>
                <span className="cat-amt">{fmt(c.amount)}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
