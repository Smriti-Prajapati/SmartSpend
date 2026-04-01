import React, { useEffect, useState, useCallback } from 'react';
import toast from 'react-hot-toast';
import api from '../services/api';
import TransactionModal from '../components/TransactionModal';
import { useCurrency } from '../context/CurrencyContext';
import './Transactions.css';

const SOURCES = ['Salary', 'Freelance', 'Investment', 'Business', 'Gift', 'Other'];

export default function Income() {
  const [incomes, setIncomes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal]     = useState(null);
  const [dateRange, setDateRange] = useState({ start: '', end: '' });
  const { fmt } = useCurrency();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params = {};
      if (dateRange.start && dateRange.end) { params.startDate = dateRange.start; params.endDate = dateRange.end; }
      const res = await api.get('/income', { params });
      setIncomes(res.data);
    } finally { setLoading(false); }
  }, [dateRange]);

  useEffect(() => { load(); }, [load]);

  const handleSave = async (form) => {
    try {
      if (modal?.id) { await api.put(`/income/${modal.id}`, form); toast.success('Income updated'); }
      else           { await api.post('/income', form);             toast.success('Income added'); }
      setModal(null); load();
    } catch (e) { toast.error(e.response?.data?.error || 'Error saving income'); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this income?')) return;
    try { await api.delete(`/income/${id}`); toast.success('Deleted'); load(); }
    catch { toast.error('Failed to delete'); }
  };

  const total = incomes.reduce((s, i) => s + i.amount, 0);

  return (
    <div className="tx-page fade-in">
      <div className="tx-header">
        <div>
          <h2 className="tx-heading">Income</h2>
          <p className="tx-sub">{incomes.length} records · Total: <strong style={{color:'#10b981'}}>{fmt(total)}</strong></p>
        </div>
        <button className="btn-primary" onClick={() => setModal({})}>+ Add Income</button>
      </div>

      <div className="filters-bar">
        <input type="date" className="filter-input" value={dateRange.start}
          onChange={e => setDateRange(d => ({ ...d, start: e.target.value }))} />
        <input type="date" className="filter-input" value={dateRange.end}
          onChange={e => setDateRange(d => ({ ...d, end: e.target.value }))} />
        <button className="btn-ghost" onClick={() => setDateRange({ start: '', end: '' })}>Clear</button>
      </div>

      {loading ? <div className="page-loader"><div className="spinner" /></div> : (
        <div className="tx-table-wrap">
          {incomes.length === 0
            ? <div className="empty-state">No income records yet. Add your first one!</div>
            : <table className="tx-table">
                <thead>
                  <tr><th>Title</th><th>Source</th><th>Amount</th><th>Date</th><th>Actions</th></tr>
                </thead>
                <tbody>
                  {incomes.map(i => (
                    <tr key={i.id}>
                      <td>
                        <div className="tx-title-cell">
                          <span className="cat-dot" style={{ background: '#10b98122' }}>💰</span>
                          <div>
                            <div className="cell-title">{i.title}</div>
                            {i.description && <div className="cell-sub">{i.description}</div>}
                          </div>
                        </div>
                      </td>
                      <td><span className="cat-badge" style={{ background: '#10b98122', color: '#10b981' }}>{i.source || 'Income'}</span></td>
                      <td><span className="amount-green">{fmt(i.amount)}</span></td>
                      <td className="date-cell">{i.date}</td>
                      <td>
                        <div className="action-btns">
                          <button className="btn-edit" onClick={() => setModal(i)}>✏️</button>
                          <button className="btn-del"  onClick={() => handleDelete(i.id)}>🗑️</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>}
        </div>
      )}

      {modal !== null && (
        <TransactionModal type="income" data={modal} sources={SOURCES} onSave={handleSave} onClose={() => setModal(null)} />
      )}
    </div>
  );
}
