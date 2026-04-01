import React, { useEffect, useState } from 'react';
import { Bar, Pie } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend } from 'chart.js';
import api from '../services/api';
import { useCurrency } from '../context/CurrencyContext';
import './Dashboard.css';

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend);

function StatCard({ label, value, icon, color, sub }) {
  return (
    <div className="stat-card" style={{ '--accent': color }}>
      <div className="stat-icon">{icon}</div>
      <div className="stat-body">
        <div className="stat-label">{label}</div>
        <div className="stat-value">{value}</div>
        {sub && <div className="stat-sub">{sub}</div>}
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const { fmt } = useCurrency();

  useEffect(() => {
    api.get('/dashboard').then(r => { setData(r.data); setLoading(false); }).catch(() => setLoading(false));
  }, []);

  if (loading) return <div className="page-loader"><div className="spinner" /></div>;
  if (!data) return <div className="empty-state">Failed to load dashboard.</div>;

  // Bar chart
  const barData = {
    labels: data.monthlyTrends?.map(t => t.month) || [],
    datasets: [
      { label: 'Income',   data: data.monthlyTrends?.map(t => t.income)  || [], backgroundColor: '#10b981', borderRadius: 6 },
      { label: 'Expenses', data: data.monthlyTrends?.map(t => t.expense) || [], backgroundColor: '#3b82f6', borderRadius: 6 },
    ],
  };

  // Pie chart
  const pieLabels = Object.keys(data.categoryBreakdown || {});
  const pieValues = Object.values(data.categoryBreakdown || {});
  const pieColors = ['#3b82f6','#10b981','#f59e0b','#ef4444','#8b5cf6','#ec4899','#14b8a6','#f97316','#6366f1','#84cc16'];
  const pieData = {
    labels: pieLabels,
    datasets: [{ data: pieValues, backgroundColor: pieColors.slice(0, pieLabels.length), borderWidth: 2, borderColor: '#fff' }],
  };

  const chartOpts = { responsive: true, plugins: { legend: { position: 'bottom', labels: { font: { family: 'Inter' }, padding: 16 } } } };

  return (
    <div className="dashboard fade-in">
      {/* Budget alert */}
      {data.budgetExceeded && (
        <div className="budget-alert">
          ⚠️ You've exceeded your monthly budget of {fmt(data.monthlyBudget)}!
        </div>
      )}
      {data.monthlyBudget > 0 && !data.budgetExceeded && (
        <div className="budget-bar-wrap">
          <div className="budget-bar-label">
            <span>Monthly Budget</span>
            <span>{fmt(data.monthlyExpenses)} / {fmt(data.monthlyBudget)}</span>
          </div>
          <div className="budget-bar">
            <div className="budget-bar-fill" style={{ width: `${data.budgetUsagePercent}%`,
              background: data.budgetUsagePercent > 80 ? '#ef4444' : '#10b981' }} />
          </div>
        </div>
      )}

      {/* Stat cards */}
      <div className="stats-grid">
        <StatCard label="Total Balance"    value={fmt(data.balance)}         icon="💳" color="#1e40af" />
        <StatCard label="Total Income"     value={fmt(data.totalIncome)}     icon="📈" color="#10b981" />
        <StatCard label="Total Expenses"   value={fmt(data.totalExpenses)}   icon="📉" color="#ef4444" />
        <StatCard label="This Month"       value={fmt(data.monthlyBalance)}  icon="📅" color="#f59e0b"
          sub={`Income ${fmt(data.monthlyIncome)} · Spent ${fmt(data.monthlyExpenses)}`} />
      </div>

      {/* Charts */}
      <div className="charts-grid">
        <div className="chart-card">
          <h3 className="chart-title">Monthly Overview</h3>
          <Bar data={barData} options={{ ...chartOpts, scales: { x: { grid: { display: false } }, y: { grid: { color: '#f1f5f9' } } } }} />
        </div>
        <div className="chart-card">
          <h3 className="chart-title">Spending by Category</h3>
          {pieLabels.length > 0
            ? <Pie data={pieData} options={chartOpts} />
            : <div className="empty-chart">No expenses this month</div>}
        </div>
      </div>

      {/* Recent transactions */}
      <div className="recent-card">
        <h3 className="chart-title">Recent Transactions</h3>
        {data.recentTransactions?.length === 0
          ? <div className="empty-chart">No transactions yet</div>
          : <div className="tx-list">
              {data.recentTransactions?.map((tx, i) => (
                <div key={i} className="tx-item">
                  <div className="tx-icon" style={{ background: tx.categoryColor + '22' }}>
                    {tx.categoryIcon}
                  </div>
                  <div className="tx-info">
                    <div className="tx-title">{tx.title}</div>
                    <div className="tx-meta">{tx.category} · {tx.date}</div>
                  </div>
                  <div className={`tx-amount ${tx.type === 'INCOME' ? 'income' : 'expense'}`}>
                    {tx.type === 'INCOME' ? '+' : '-'}{fmt(tx.amount)}
                  </div>
                </div>
              ))}
            </div>}
      </div>
    </div>
  );
}
